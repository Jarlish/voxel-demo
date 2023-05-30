package io.github.jarlish.voxeldemo.world;

import com.badlogic.gdx.math.MathUtils;
import io.github.jarlish.voxeldemo.world.chunk.Chunk;
import io.github.jarlish.voxeldemo.world.chunk.ChunkCoordinate;
import io.github.jarlish.voxeldemo.world.chunk.ChunkMap;
import make.some.noise.Noise;

public class World {

	public static final int WORLD_SIZE = 40;
	public static final int WORLD_DEPTH = 5;
	private static final int MAX_CHUNK_GENERATIONS = 100;

	private Noise heightMap;
	private ChunkMap chunkMap;

	public World() {
		heightMap = new Noise(MathUtils.random(Integer.MAX_VALUE - 1), 0.0025f, Noise.SIMPLEX_FRACTAL, 8, 2.0f, 0.5f);
		heightMap.setFractalType(Noise.RIDGED_MULTI);
		heightMap.setFractalGain(2f);
		chunkMap = new ChunkMap();
	}

	public void init() {
		addChunks();
		chunkMap.generate();
	}

	private void addChunks() {
		for(int x = 0; x < WORLD_SIZE; x++) {
			for(int y = 0; y < WORLD_DEPTH; y++) {
				for(int z = 0; z < WORLD_SIZE; z++) {
					chunkMap.addChunk(x, y, z);
				}
			}
		}
	}

	public void generateChunks() {
		for(int i = 0; i < Math.min(chunkMap.generationQueueCount(), MAX_CHUNK_GENERATIONS); i++) {
			Chunk chunk = chunkMap.pollGenertionQueue();
			generateChunk(chunk);
			chunkMap.createVertexBuffer(chunk);
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

	public byte getVoxel(int x, int y, int z) {
		int chunkX = MathUtils.floor((float) x / Chunk.CHUNK_SIZE);
		int chunkY = MathUtils.floor((float) y / Chunk.CHUNK_SIZE);
		int chunkZ = MathUtils.floor((float) z / Chunk.CHUNK_SIZE);

		ChunkCoordinate location = new ChunkCoordinate(chunkX, chunkY, chunkZ);
		Chunk chunk = chunkMap.getChunkThreadSafe(location);
		if(chunk == null) {
			return 0;
		}
		int chunkWorldX = (chunk.getLocation().x * Chunk.CHUNK_SIZE);
		int chunkWorldY = (chunk.getLocation().y * Chunk.CHUNK_SIZE);
		int chunkWorldZ = (chunk.getLocation().z * Chunk.CHUNK_SIZE);
		return chunk.getVoxel(x - chunkWorldX, y - chunkWorldY, z - chunkWorldZ);
	}

	public ChunkMap getChunkMap() {
		return chunkMap;
	}
}
