package io.github.jarlish.voxeldemo.world.chunk;

import com.badlogic.gdx.utils.Pool;

public class ChunkPool extends Pool<Chunk> {

	@Override
	protected Chunk newObject() {
		return new Chunk();
	}
}
