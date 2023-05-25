package io.github.jarlish.voxeldemo.screen;

import io.github.jarlish.voxeldemo.egine.GameEngine;
import io.github.jarlish.voxeldemo.render.GameRenderer;

public class PlayScreen extends BaseScreen {

	private GameEngine engine;
	private GameRenderer renderer;

	public PlayScreen() {
		engine = new GameEngine();
		renderer = new GameRenderer(engine.getWorld());
		engine.start();
		renderer.start();
	}

	@Override
	public void render(float delta) {
		renderer.render(delta);
	}

	@Override
	public void hide() {
		engine.end();
		renderer.end();
	}
}
