package io.github.jarlish.voxeldemo.world.chunk;

import com.badlogic.gdx.utils.Pool.Poolable;
import io.github.jarlish.voxeldemo.render.chunk.ChunkMesh;

public class Chunk implements Poolable {

	public static final int CHUNK_SIZE = 32;

	private ChunkCoordinate location;
	private byte[][][] voxels;
	private ChunkMesh chunkMesh;
	private boolean generated;

	public Chunk() {
		location = new ChunkCoordinate(0, 0, 0);
		voxels = new byte[Chunk.CHUNK_SIZE][Chunk.CHUNK_SIZE][Chunk.CHUNK_SIZE];
		chunkMesh = new ChunkMesh(this);
		generated = false;
	}

	public ChunkCoordinate getLocation() {
		return location;
	}

	public void setLocation(int x, int y, int z) {
		location.set(x, y, z);
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

	public ChunkMesh getChunkMesh() {
		return chunkMesh;
	}

	public boolean isGenerated() {
		return generated;
	}

	public void setGenerated(boolean generated) {
		this.generated = generated;
	}
}
