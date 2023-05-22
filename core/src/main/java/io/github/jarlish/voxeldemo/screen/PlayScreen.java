package io.github.jarlish.voxeldemo.screen;

import io.github.jarlish.voxeldemo.world.World;

public class PlayScreen extends BaseScreen {

	private World world;
	
	public PlayScreen() {
		world = new World();
		world.generateWorld();
	}
}
