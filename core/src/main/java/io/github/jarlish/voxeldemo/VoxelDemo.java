package io.github.jarlish.voxeldemo;

import com.badlogic.gdx.Game;
import io.github.jarlish.voxeldemo.screen.PlayScreen;

public class VoxelDemo extends Game {
	
	@Override
	public void create() {
		setScreen(new PlayScreen());
	}
}
