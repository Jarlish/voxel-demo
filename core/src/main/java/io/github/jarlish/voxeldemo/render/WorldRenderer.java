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
import io.github.jarlish.voxeldemo.world.World;
import io.github.jarlish.voxeldemo.world.chunk.Chunk;
import io.github.jarlish.voxeldemo.world.chunk.ChunkCoordinate;

public class WorldRenderer {

	private Camera camera;
	private World world;
	private ModelBatch modelBatch;
	private ChunkMeshProvider chunkMeshProvider;
	private float[] vertices;
	private short[] indices;
	private VertexAttributes vertexAttributes;
	private ChunkCoordinate tmp = new ChunkCoordinate(0, 0, 0);

	public WorldRenderer(Camera camera, World world) {
		this.camera = camera;
		this.world = world;
		modelBatch = new ModelBatch();
		chunkMeshProvider = new ChunkMeshProvider(this);

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
	}

	public void render(float delta, Camera camera) {
		buildChunkMeshes();
		drawWorld(camera);
	}

	private void buildChunkMeshes() {
		ConcurrentHashMap<ChunkCoordinate, Chunk> chunks = world.getChunks();
		ConcurrentLinkedQueue<Chunk> chunkMeshBuildingQueue = world.getChunkMeshBuildingQueue();
		for(int i = 0; i < Math.min(25, chunkMeshBuildingQueue.size()); i++) {
			Chunk chunk = chunkMeshBuildingQueue.poll();
			ChunkCoordinate location = chunk.getLocation();

			Chunk neighbor1 = chunks.get(tmp.set(location.x - 1, location.y, location.z));
			Chunk neighbor2 = chunks.get(tmp.set(location.x + 1, location.y, location.z));
			Chunk neighbor3 = chunks.get(tmp.set(location.x, location.y - 1, location.z));
			Chunk neighbor4 = chunks.get(tmp.set(location.x, location.y + 1, location.z));
			Chunk neighbor5 = chunks.get(tmp.set(location.x, location.y, location.z - 1));
			Chunk neighbor6 = chunks.get(tmp.set(location.x, location.y, location.z + 2));

			if(neighbor1 == null || neighbor2 == null || neighbor3 == null || neighbor4 == null || neighbor5 == null || neighbor6 == null) {
				chunkMeshBuildingQueue.add(chunk);
				continue;
			}else if(!neighbor1.isGenerated() || !neighbor2.isGenerated() || !neighbor3.isGenerated() || !neighbor4.isGenerated() || !neighbor5.isGenerated() || !neighbor6.isGenerated()) {
				chunkMeshBuildingQueue.add(chunk);
				continue;
			}

			chunk.getChunkMesh().buildMesh(world, vertices, indices, vertexAttributes);
		}
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
