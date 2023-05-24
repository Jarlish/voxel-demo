package io.github.jarlish.voxeldemo.render;

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

public class WorldRenderer {

	private World world;
	private ModelBatch modelBatch;
	private ChunkMeshProvider chunkMeshProvider;
	private float[] vertices;
	private short[] indices;
	private VertexAttributes vertexAttributes;

	public WorldRenderer(World world) {
		this.world = world;
		modelBatch = new ModelBatch();
		chunkMeshProvider = new ChunkMeshProvider(world);

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
		ConcurrentLinkedQueue<Chunk> chunkMeshBuildingQueue = world.getChunkMeshBuildingQueue();
		for(int i = 0; i < Math.min(5, chunkMeshBuildingQueue.size()); i++) {
			Chunk chunk = chunkMeshBuildingQueue.poll();
			chunk.getChunkMesh().buildMesh(world, vertices, indices, vertexAttributes);
		}
	}

	private void drawWorld(Camera camera) {
		modelBatch.begin(camera);
		modelBatch.render(chunkMeshProvider);
		modelBatch.end();
	}

	public int getChunksLoaded() {
		return world.getChunks().size();
	}
	
	public int getChunksRendered() {
		return chunkMeshProvider.getChunksRendered();
	}
}