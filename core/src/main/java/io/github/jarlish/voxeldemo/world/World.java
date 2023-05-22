package io.github.jarlish.voxeldemo.world;

import java.util.HashMap;

import io.github.jarlish.voxeldemo.world.chunk.Chunk;
import io.github.jarlish.voxeldemo.world.chunk.ChunkCoordinate;
import io.github.jarlish.voxeldemo.world.chunk.ChunkPool;

public class World {

	private ChunkPool chunkPool;
	private HashMap<ChunkCoordinate, Chunk> chunks;
	
	public World() {
		chunkPool = new ChunkPool();
		chunks = new HashMap<ChunkCoordinate, Chunk>();
		generateWorld();
	}
	
	public void generateWorld() {
		for(int x = 0; x < 10; x ++) {
			for(int y = 0; y < 10; y ++) {
				for(int z = 0; z < 10; z ++) {
					Chunk chunk = chunkPool.obtain();
					generateChunk(chunk);
					chunks.put(new ChunkCoordinate(x, y, z), chunk);
				}
			}
		}
	}
	
	private void generateChunk(Chunk chunk) {
		for(int x = 0; x < Chunk.CHUNK_SIZE; x ++) {
			for(int y = 0; y < Chunk.CHUNK_SIZE; y ++) {
				for(int z = 0; z < Chunk.CHUNK_SIZE; z ++) {
					chunk.setVoxel(x, y, z, (byte) 0);
				}
			}
		}
	}
}
