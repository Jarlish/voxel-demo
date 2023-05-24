package io.github.jarlish.voxeldemo.world.chunk;

import com.badlogic.gdx.utils.Pool;

public class ChunkPool extends Pool<Chunk> {

	public Chunk obtain(int x, int y, int z) {
		Chunk chunk = super.obtain();
		chunk.setLocation(x, y, z);
		return chunk;
	}
	
	@Override
	protected Chunk newObject() {
		return new Chunk();
	}
}
