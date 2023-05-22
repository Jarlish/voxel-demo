package io.github.jarlish.voxeldemo.world.chunk;

import com.badlogic.gdx.utils.Pool.Poolable;

public class Chunk implements Poolable {

	public static final int CHUNK_SIZE = 32;
	
	private byte[][][] voxels;
	
	public Chunk() {
		voxels = new byte[Chunk.CHUNK_SIZE][Chunk.CHUNK_SIZE][Chunk.CHUNK_SIZE];
	}
	
	@Override
	public void reset() {
		
	}
	
	public byte getVoxel(int x, int y, int z) {
		return voxels[x][y][z];
	}
	
	public void setVoxel(int x, int y, int z, byte voxel) {
		voxels[x][y][z] = voxel;
	}
}
