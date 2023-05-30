package io.github.jarlish.voxeldemo.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.IndexBufferObject;
import com.badlogic.gdx.graphics.glutils.IndexData;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import io.github.jarlish.voxeldemo.VoxelDemo;
import io.github.jarlish.voxeldemo.render.chunk.ChunkMesh;
import io.github.jarlish.voxeldemo.render.chunk.MeshBuildingThread;
import io.github.jarlish.voxeldemo.world.World;
import io.github.jarlish.voxeldemo.world.chunk.Chunk;
import io.github.jarlish.voxeldemo.world.chunk.ChunkCoordinate;
import io.github.jarlish.voxeldemo.world.chunk.ChunkMap;

public class WorldRenderer {

	private static final int MAX_VERTEX_BUFFER_CREATIONS = 100;
	private static final int MAX_MESH_BUILDS = 100;

	private Camera camera;
	private World world;
	private float[] vertices;
	private VertexAttributes vertexAttributes;

	private Thread meshBuildingThread;
	private boolean running;

	private IndexData indexBuffer;
	private ShaderProgram shader;
	private int chunksRendered;

	public WorldRenderer(Camera camera, World world) {
		this.camera = camera;
		this.world = world;

		//Vertices
		this.vertices = new float[Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE * ChunkMesh.VERTEX_SIZE];

		//Indices
		int length = Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE * 6 * 6 / 3;
		short[] indices = new short[length];
		for(int i = 0, j = 0; i < indices.length; i += 6, j += 4) {
			indices[i] = (short) j;
			indices[i + 1] = (short) (j + 1);
			indices[i + 2] = (short) (j + 2);
			indices[i + 3] = (short) (j + 2);
			indices[i + 4] = (short) (j + 3);
			indices[i + 5] = (short) j;
		}
		indexBuffer = new IndexBufferObject(true, length);
		indexBuffer.setIndices(indices, 0, length);

		//Vertex attributes
		vertexAttributes = new VertexAttributes(VertexAttribute.Position(), VertexAttribute.TexCoords(0));

		//Mesh building
		meshBuildingThread = new Thread(new MeshBuildingThread(this));
		running = false;

		//Shader
		shader = new ShaderProgram(Gdx.files.internal("shader.vert"), Gdx.files.internal("shader.frag"));
	}

	public void render(float delta, Camera camera) {
		createChunkMeshes();
		drawWorld(camera);
	}

	private void createChunkMeshes() {
		ChunkMap chunkMap = world.getChunkMap();
		for(int i = 0; i < Math.min(chunkMap.vertexBufferCreationQueueCount(), MAX_VERTEX_BUFFER_CREATIONS); i++) {
			Chunk chunk = chunkMap.pollVertexBufferCreationQueue();
			if(chunkNeedsMesh(chunk)) {
				chunk.getChunkMesh().createMesh(world, vertexAttributes);
				chunkMap.buildMesh(chunk);
			}else {
				chunkMap.createVertexBuffer(chunk);
			}
		}
	}

	public void buildChunkMeshes() {
		ChunkMap chunkMap = world.getChunkMap();
		for(int i = 0; i < Math.min(chunkMap.meshBuildingQueueCount(), MAX_MESH_BUILDS); i++) {
			Chunk chunk = chunkMap.pollMeshBuildingQueue();
			if(chunkNeedsMesh(chunk)) {
				chunk.getChunkMesh().buildMesh(world, vertices);
			}else {
				chunkMap.buildMesh(chunk);
			}
		}
	}

	private boolean chunkNeedsMesh(Chunk chunk) {
		ChunkMap chunkMap = world.getChunkMap();
		ChunkCoordinate location = chunk.getLocation();
		for(int x = -1; x <= 1; x++) {
			for(int y = -1; y <= 1; y++) {
				for(int z = -1; z <= 1; z++) {
					Chunk neighbor = chunkMap.getChunk(location.x + x, location.y + y, location.z + z);
					if(neighbor == null || !neighbor.isGenerated()) {
						return false;
					}
				}
			}
		}
		return true;
	}

	private void drawWorld(Camera camera) {
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glEnable(GL20.GL_CULL_FACE);
		Gdx.gl.glCullFace(GL20.GL_BACK);

		VoxelDemo.terrainTexture.bind();
		indexBuffer.bind();
		shader.bind();
		shader.setUniformMatrix("u_projTrans", camera.combined);
		shader.setUniformf(shader.getUniformLocation("u_cameraPosition"), camera.position);
		shader.setUniformf(shader.getUniformLocation("u_fogColor"), GameRenderer.SKY_COLOR);
		shader.setUniformf(shader.getUniformLocation("u_fogStart"), 200f);
		shader.setUniformf(shader.getUniformLocation("u_fogEnd"), 500f);

		chunksRendered = 0;
		for(Chunk chunk : world.getChunkMap().getChunks()) {
			ChunkMesh chunkMesh = chunk.getChunkMesh();
			if(!isChunkVisible(chunk) || chunkMesh.isDirty() || chunkMesh.getSize() == 0) {
				continue;
			}
			chunkMesh.render(shader, indexBuffer);
			chunksRendered++;
		}

		indexBuffer.unbind();
		Gdx.gl.glDisable(GL20.GL_CULL_FACE);
	}

	public boolean isChunkVisible(Chunk chunk) {
		if(!chunk.getChunkMesh().didFirstRender()) {
			return true;
		}
		int chunkWorldX = (chunk.getLocation().x * Chunk.CHUNK_SIZE);
		int chunkWorldY = (chunk.getLocation().y * Chunk.CHUNK_SIZE);
		int chunkWorldZ = (chunk.getLocation().z * Chunk.CHUNK_SIZE);
		return camera.frustum.boundsInFrustum(chunkWorldX + (Chunk.CHUNK_SIZE / 2), chunkWorldY + (Chunk.CHUNK_SIZE / 2), chunkWorldZ + (Chunk.CHUNK_SIZE / 2), (Chunk.CHUNK_SIZE / 2), (Chunk.CHUNK_SIZE / 2), (Chunk.CHUNK_SIZE / 2));
	}

	public void start() {
		running = true;
		meshBuildingThread.start();
	}

	public void end() {
		running = false;
	}

	public boolean isRunning() {
		return running;
	}

	public int getChunksLoaded() {
		return world.getChunkMap().chunkCount();
	}

	public int getChunksRendered() {
		return chunksRendered;
	}

	public World getWorld() {
		return world;
	}
}
