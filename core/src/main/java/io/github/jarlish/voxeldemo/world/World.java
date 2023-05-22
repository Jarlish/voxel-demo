package io.github.jarlish.voxeldemo.world;

import java.util.HashMap;
import java.util.LinkedList;
import com.badlogic.gdx.math.MathUtils;
import io.github.jarlish.voxeldemo.world.chunk.Chunk;
import io.github.jarlish.voxeldemo.world.chunk.ChunkCoordinate;
import io.github.jarlish.voxeldemo.world.chunk.ChunkPool;
import make.some.noise.Noise;

public class World {

	private Noise heightMap;
	private ChunkPool chunkPool;
	private HashMap<ChunkCoordinate, Chunk> chunks;
	private LinkedList<Chunk> chunkGenerationQueue;

	public World() {
		heightMap = new Noise(MathUtils.random(Integer.MAX_VALUE - 1), 0.5f, Noise.SIMPLEX, 1, 2.0f, 0.5f);
		chunkPool = new ChunkPool();
		chunks = new HashMap<ChunkCoordinate, Chunk>();
		chunkGenerationQueue = new LinkedList<Chunk>();
	}

	public void init() {
		addChunks();
		chunkGenerationQueue.addAll(chunks.values());
	}

	public void tick() {
		for(int i = 0; i < Math.min(50, chunkGenerationQueue.size()); i++) {
			Chunk chunk = chunkGenerationQueue.pop();
			generateChunk(chunk);
		}
	}

	private void addChunks() {
		for(int x = 0; x < 20; x++) {
			for(int y = 0; y < 5; y++) {
				for(int z = 0; z < 20; z++) {
					Chunk chunk = chunkPool.obtain();
					chunks.put(new ChunkCoordinate(x, y, z), chunk);
				}
			}
		}
	}

	private void generateChunk(Chunk chunk) {
		for(int x = 0; x < Chunk.CHUNK_SIZE; x++) {
			for(int y = 0; y < Chunk.CHUNK_SIZE; y++) {
				for(int z = 0; z < Chunk.CHUNK_SIZE; z++) {
					float height = heightMap.getConfiguredNoise(x, y, z);
					if(height > 0) {
						chunk.setVoxel(x, y, z, (byte) 1);
					}else {
						chunk.setVoxel(x, y, z, (byte) 0);
					}
				}
			}
		}
	}
}
