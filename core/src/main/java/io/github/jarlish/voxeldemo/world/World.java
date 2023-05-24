package io.github.jarlish.voxeldemo.world;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import com.badlogic.gdx.math.MathUtils;
import io.github.jarlish.voxeldemo.world.chunk.Chunk;
import io.github.jarlish.voxeldemo.world.chunk.ChunkCoordinate;
import io.github.jarlish.voxeldemo.world.chunk.ChunkPool;
import make.some.noise.Noise;

public class World {

	private Noise heightMap;
	private ChunkPool chunkPool;
	private ConcurrentHashMap<ChunkCoordinate, Chunk> chunks;
	private ConcurrentLinkedQueue<Chunk> chunkGenerationQueue;
	private ConcurrentLinkedQueue<Chunk> chunkMeshBuildingQueue;

	public World() {
		heightMap = new Noise(MathUtils.random(Integer.MAX_VALUE - 1), 0.01f, Noise.SIMPLEX, 1, 2.0f, 0.5f);
		chunkPool = new ChunkPool();
		chunks = new ConcurrentHashMap<ChunkCoordinate, Chunk>();
		chunkGenerationQueue = new ConcurrentLinkedQueue<Chunk>();
		chunkMeshBuildingQueue = new ConcurrentLinkedQueue<Chunk>();
	}

	public void init() {
		addChunks();
		chunkGenerationQueue.addAll(chunks.values());
	}

	public void tick() {
		for(int i = 0; i < Math.min(50, chunkGenerationQueue.size()); i++) {
			Chunk chunk = chunkGenerationQueue.poll();
			generateChunk(chunk);
			chunkMeshBuildingQueue.add(chunk);
		}
	}

	private void addChunks() {
		for(int x = 0; x < 10; x++) {
			for(int y = 0; y < 10; y++) {
				for(int z = 0; z < 10; z++) {
					Chunk chunk = chunkPool.obtain(x, y, z);
					chunks.put(new ChunkCoordinate(x, y, z), chunk);
				}
			}
		}
	}

	private void generateChunk(Chunk chunk) {
		for(int x = 0; x < Chunk.CHUNK_SIZE; x++) {
			for(int y = 0; y < Chunk.CHUNK_SIZE; y++) {
				for(int z = 0; z < Chunk.CHUNK_SIZE; z++) {
					int worldX = (chunk.getLocation().x * Chunk.CHUNK_SIZE) + x;
					int worldY = (chunk.getLocation().y * Chunk.CHUNK_SIZE) + y;
					int worldZ = (chunk.getLocation().z * Chunk.CHUNK_SIZE) + z;
					float height = heightMap.getConfiguredNoise(worldX, worldY, worldZ);
					if(height > 0.25f) {
						chunk.setVoxel(x, y, z, (byte) (2 + MathUtils.random(2)));
					}else {
						chunk.setVoxel(x, y, z, (byte) 0);
					}
				}
			}
		}
	}

	public Chunk getChunk(int chunkX, int chunkY, int chunkZ) {
		ChunkCoordinate chunkLocation = new ChunkCoordinate(chunkX, chunkY, chunkZ);
		Chunk chunk = chunks.get(chunkLocation);
		return chunk;
	}

	public byte getVoxel(int x, int y, int z) {
		int chunkX = MathUtils.floor((float) x / Chunk.CHUNK_SIZE);
		int chunkY = MathUtils.floor((float) y / Chunk.CHUNK_SIZE);
		int chunkZ = MathUtils.floor((float) z / Chunk.CHUNK_SIZE);

		Chunk chunk = getChunk(chunkX, chunkY, chunkZ);
		if(chunk == null) {
			return 0;
		}
		int chunkWorldX = (chunk.getLocation().x * Chunk.CHUNK_SIZE);
		int chunkWorldY = (chunk.getLocation().y * Chunk.CHUNK_SIZE);
		int chunkWorldZ = (chunk.getLocation().z * Chunk.CHUNK_SIZE);
		return chunk.getVoxel(x - chunkWorldX, y - chunkWorldY, z - chunkWorldZ);
	}

	public ConcurrentHashMap<ChunkCoordinate, Chunk> getChunks() {
		return chunks;
	}

	public ConcurrentLinkedQueue<Chunk> getChunkMeshBuildingQueue() {
		return chunkMeshBuildingQueue;
	}
}
