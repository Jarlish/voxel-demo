package io.github.jarlish.voxeldemo.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.jarlish.voxeldemo.VoxelDemo;
import io.github.jarlish.voxeldemo.camera.VoxelCamera;
import io.github.jarlish.voxeldemo.world.World;
import io.github.jarlish.voxeldemo.world.chunk.Chunk;

public class GameRenderer {

	private VoxelCamera camera;
	private WorldRenderer worldRenderer;
	private SpriteBatch spriteBatch;

	public GameRenderer(World world) {
		camera = new VoxelCamera();
		camera.position.set(5 * Chunk.CHUNK_SIZE, 11 * Chunk.CHUNK_SIZE, 5 * Chunk.CHUNK_SIZE);

		worldRenderer = new WorldRenderer(camera, world);
		spriteBatch = new SpriteBatch();
	}

	public void render(float delta) {
		camera.doInput(delta);
		ScreenUtils.clear(0.6f, 0.6f, 1.0f, 1f, true);
		worldRenderer.render(delta, camera);
		drawHUD();
	}

	private void drawHUD() {
		spriteBatch.begin();
		VoxelDemo.font.draw(spriteBatch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 5, Gdx.graphics.getHeight() - 5);
		VoxelDemo.font.draw(spriteBatch, "Chunks Loaded: " + worldRenderer.getChunksLoaded(), 5, Gdx.graphics.getHeight() - 25);
		VoxelDemo.font.draw(spriteBatch, "Chunks Rendered: " + worldRenderer.getChunksRendered(), 5, Gdx.graphics.getHeight() - 45);
		spriteBatch.draw(VoxelDemo.crosshairTexture, (Gdx.graphics.getWidth() / 2) - 7, (Gdx.graphics.getHeight() / 2) - 7, 14, 14);
		spriteBatch.end();
	}
}
