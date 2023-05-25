package io.github.jarlish.voxeldemo.world.chunk;

import java.util.Objects;

public class ChunkCoordinate {

	public int x;
	public int y;
	public int z;
	private int hashCode;

	public ChunkCoordinate(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.hashCode = Objects.hash(x, y, z);
	}

	public ChunkCoordinate set(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.hashCode = Objects.hash(x, y, z);
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ChunkCoordinate) {
			ChunkCoordinate coordinate = (ChunkCoordinate) obj;
			return (x == coordinate.x) && (y == coordinate.y) && (z == coordinate.z);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return this.hashCode;
	}
}
