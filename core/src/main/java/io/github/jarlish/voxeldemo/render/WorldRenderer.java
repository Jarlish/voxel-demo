package io.github.jarlish.voxeldemo.render;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.utils.Array;
import io.github.jarlish.voxeldemo.render.chunk.ChunkMesh;
import io.github.jarlish.voxeldemo.render.chunk.ChunkMeshProvider;
import io.github.jarlish.voxeldemo.render.chunk.MeshBuildingThread;
import io.github.jarlish.voxeldemo.world.World;
import io.github.jarlish.voxeldemo.world.chunk.Chunk;
import io.github.jarlish.voxeldemo.world.chunk.ChunkCoordinate;

public class WorldRenderer {

	private static final int MAX_MESH_CREATIONS = 100;
	private static final int MAX_MESH_BUILDS = 100;

	private Camera camera;
	private World world;
	private ModelBatch modelBatch;
	private ChunkMeshProvider chunkMeshProvider;
	private float[] vertices;
	private short[] indices;
	private VertexAttributes vertexAttributes;
	private ChunkCoordinate tmp;

	private ConcurrentLinkedQueue<Chunk> chunkMeshBuildingQueue;
	private Thread meshBuildingThread;
	private boolean running;

	public WorldRenderer(Camera camera, World world) {
		this.camera = camera;
		this.world = world;
		modelBatch = new ModelBatch();
		chunkMeshProvider = new ChunkMeshProvider(this);
		tmp = new ChunkCoordinate(0, 0, 0);

		//Vertices
		this.vertices = new float[ChunkMesh.VERTEX_SIZE * 6 * Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE];

		//Indices
		indices = new short[Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE * 6 * 6 / 3];
		short j = 0;
		for(int i = 0; i < indices.length; i += 6, j += 4) {
			indices[i] = (short) j;
			indices[i + 1] = (short) (j + 1);
			indices[i + 2] = (short) (j + 2);
			indices[i + 3] = (short) (j + 2);
			indices[i + 4] = (short) (j + 3);
			indices[i + 5] = (short) j;
		}

		//Vertex attributes
		Array<VertexAttribute> attributeArray = new Array<>();
		attributeArray.add(VertexAttribute.Position());
		attributeArray.add(VertexAttribute.TexCoords(0));
		vertexAttributes = new VertexAttributes(attributeArray.toArray(VertexAttribute.class));

		//Mesh building
		chunkMeshBuildingQueue = new ConcurrentLinkedQueue<Chunk>();
		meshBuildingThread = new Thread(new MeshBuildingThread(this));
		running = false;
	}

	public void render(float delta, Camera camera) {
		createChunkMeshes();
		drawWorld(camera);
	}

	private void createChunkMeshes() {
		ConcurrentLinkedQueue<Chunk> chunkMeshCreationQueue = world.getChunkMeshCreationQueue();
		for(int i = 0; i < Math.min(chunkMeshCreationQueue.size(), MAX_MESH_CREATIONS); i++) {
			Chunk chunk = chunkMeshCreationQueue.poll();
			if(chunkNeedsMesh(chunk)) {
				chunk.getChunkMesh().createMesh(world, vertices, indices, vertexAttributes);
				chunkMeshBuildingQueue.add(chunk);
			}else {
				chunkMeshCreationQueue.add(chunk);
			}
		}
	}

	public void buildChunkMeshes() {
		for(int i = 0; i < Math.min(chunkMeshBuildingQueue.size(), MAX_MESH_BUILDS); i++) {
			Chunk chunk = chunkMeshBuildingQueue.poll();
			if(chunkNeedsMesh(chunk)) {
				chunk.getChunkMesh().buildMesh(world, vertices, indices, vertexAttributes);
			}else {
				chunkMeshBuildingQueue.add(chunk);
			}
		}
	}

	private boolean chunkNeedsMesh(Chunk chunk) {
		ConcurrentHashMap<ChunkCoordinate, Chunk> chunks = world.getChunks();
		ChunkCoordinate location = chunk.getLocation();
		for(int x = -1; x <= 1; x++) {
			for(int y = -1; y <= 1; y++) {
				for(int z = -1; z <= 1; z++) {
					Chunk neighbor = chunks.get(tmp.set(location.x + x, location.y + y, location.z + z));
					if(neighbor == null || !neighbor.isGenerated()) {
						return false;
					}
				}
			}
		}
		return true;
	}

	private void drawWorld(Camera camera) {
		modelBatch.begin(camera);
		modelBatch.render(chunkMeshProvider);
		modelBatch.end();
	}

	public boolean isChunkVisible(Chunk chunk) {
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
		return world.getChunks().size();
	}

	public int getChunksRendered() {
		return chunkMeshProvider.getChunksRendered();
	}

	public World getWorld() {
		return world;
	}
}
