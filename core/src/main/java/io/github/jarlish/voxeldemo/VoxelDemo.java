package io.github.jarlish.voxeldemo;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import io.github.jarlish.voxeldemo.screen.PlayScreen;

public class VoxelDemo extends Game {

	public static BitmapFont font;
	public static Texture terrainTexture;
	public static Texture crosshairTexture;

	@Override
	public void create() {
		font = new BitmapFont(Gdx.files.internal("font/font.fnt"));
		font.getData().setScale(2);
		terrainTexture = new Texture(Gdx.files.internal("terrain.png"));
		crosshairTexture = new Texture(Gdx.files.internal("crosshair.png"));
		setScreen(new PlayScreen());
	}
}
