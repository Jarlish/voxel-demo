package io.github.jarlish.voxeldemo.world.chunk;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ChunkMap {

	private ChunkPool chunkPool;
	private ConcurrentHashMap<ChunkCoordinate, Chunk> chunks;
	private ConcurrentLinkedQueue<Chunk> generationQueue;
	private ConcurrentLinkedQueue<Chunk> vertexBufferCreationQueue;
	private ConcurrentLinkedQueue<Chunk> meshBuildingQueue;
	private ChunkCoordinate locator;

	public ChunkMap() {
		chunkPool = new ChunkPool();
		chunks = new ConcurrentHashMap<ChunkCoordinate, Chunk>();
		generationQueue = new ConcurrentLinkedQueue<Chunk>();
		vertexBufferCreationQueue = new ConcurrentLinkedQueue<Chunk>();
		meshBuildingQueue = new ConcurrentLinkedQueue<Chunk>();
		locator = new ChunkCoordinate(0, 0, 0);
	}

	public Collection<Chunk> getChunks() {
		return chunks.values();
	}

	public Chunk getChunk(int x, int y, int z) {
		locator.set(x, y, z);
		return chunks.get(locator);
	}

	public Chunk getChunkThreadSafe(ChunkCoordinate location) {
		return chunks.get(location);
	}

	public void addChunk(int x, int y, int z) {
		chunks.put(new ChunkCoordinate(x, y, z), chunkPool.obtain(x, y, z));
	}

	public int chunkCount() {
		return chunks.size();
	}

	public void generate() {
		generationQueue.addAll(chunks.values());
	}

	public Chunk pollGenertionQueue() {
		return generationQueue.poll();
	}

	public int generationQueueCount() {
		return generationQueue.size();
	}

	public void createVertexBuffer(Chunk chunk) {
		vertexBufferCreationQueue.add(chunk);
	}

	public Chunk pollVertexBufferCreationQueue() {
		return vertexBufferCreationQueue.poll();
	}

	public int vertexBufferCreationQueueCount() {
		return vertexBufferCreationQueue.size();
	}

	public void buildMesh(Chunk chunk) {
		meshBuildingQueue.add(chunk);
	}

	public Chunk pollMeshBuildingQueue() {
		return meshBuildingQueue.poll();
	}

	public int meshBuildingQueueCount() {
		return meshBuildingQueue.size();
	}
}
