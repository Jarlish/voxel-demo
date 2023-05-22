package io.github.jarlish.voxeldemo.screen;

import io.github.jarlish.voxeldemo.egine.GameEngine;

public class PlayScreen extends BaseScreen {

	private GameEngine engine;

	public PlayScreen() {
		engine = new GameEngine();
		engine.start();
	}

	@Override
	public void hide() {
		engine.end();
	}
}
