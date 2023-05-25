package io.github.jarlish.voxeldemo.world;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import com.badlogic.gdx.math.MathUtils;
import io.github.jarlish.voxeldemo.world.chunk.Chunk;
import io.github.jarlish.voxeldemo.world.chunk.ChunkCoordinate;
import io.github.jarlish.voxeldemo.world.chunk.ChunkPool;
import make.some.noise.Noise;

public class World {

	public static final int WORLD_SIZE = 40;
	public static final int WORLD_DEPTH = 5;
	private static final int MAX_CHUNK_GENERATIONS = 100;

	private Noise heightMap;
	private ChunkPool chunkPool;
	private ConcurrentHashMap<ChunkCoordinate, Chunk> chunks;
	private ConcurrentLinkedQueue<Chunk> chunkGenerationQueue;
	private ConcurrentLinkedQueue<Chunk> chunkMeshCreationQueue;

	public World() {
		heightMap = new Noise(MathUtils.random(Integer.MAX_VALUE - 1), 0.0025f, Noise.SIMPLEX_FRACTAL, 8, 2.0f, 0.5f);
		heightMap.setFractalType(Noise.RIDGED_MULTI);
		heightMap.setFractalGain(2f);
		chunkPool = new ChunkPool();
		chunks = new ConcurrentHashMap<ChunkCoordinate, Chunk>();
		chunkGenerationQueue = new ConcurrentLinkedQueue<Chunk>();
		chunkMeshCreationQueue = new ConcurrentLinkedQueue<Chunk>();
	}

	public void init() {
		addChunks();
		chunkGenerationQueue.addAll(chunks.values());
	}

	public void generateChunks() {
		for(int i = 0; i < Math.min(chunkGenerationQueue.size(), MAX_CHUNK_GENERATIONS); i++) {
			Chunk chunk = chunkGenerationQueue.poll();
			generateChunk(chunk);
			chunkMeshCreationQueue.add(chunk);
		}
	}

	private void addChunks() {
		for(int x = 0; x < WORLD_SIZE; x++) {
			for(int y = 0; y < WORLD_DEPTH; y++) {
				for(int z = 0; z < WORLD_SIZE; z++) {
					Chunk chunk = chunkPool.obtain(x, y, z);
					chunks.put(new ChunkCoordinate(x, y, z), chunk);
				}
			}
		}
	}

	private void generateChunk(Chunk chunk) {
		for(int x = 0; x < Chunk.CHUNK_SIZE; x++) {
			for(int z = 0; z < Chunk.CHUNK_SIZE; z++) {
				int worldX = (chunk.getLocation().x * Chunk.CHUNK_SIZE) + x;
				int worldZ = (chunk.getLocation().z * Chunk.CHUNK_SIZE) + z;
				float height = (heightMap.getConfiguredNoise(worldX, worldZ) + 1f) / 2f;
				int h = height > 0.5f ? (int) (height * 128) : 64;
				for(int y = 0; y < Chunk.CHUNK_SIZE; y++) {
					int worldY = (chunk.getLocation().y * Chunk.CHUNK_SIZE) + y;
					if(worldY < h) {
						byte voxel = 0;
						if(worldY < 64) {
							voxel = 1;
						}
						if(worldY >= 64) {
							voxel = 2;
						}
						if(worldY >= 70) {
							voxel = 3;
						}
						if(worldY > 96 - 3 + MathUtils.random(6)) {
							voxel = 4;
						}
						chunk.setVoxel(x, y, z, voxel);
					}
				}
			}
		}
		chunk.setGenerated(true);
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

	public ConcurrentLinkedQueue<Chunk> getChunkMeshCreationQueue() {
		return chunkMeshCreationQueue;
	}
}
