package io.github.jarlish.voxeldemo.engine;

import io.github.jarlish.voxeldemo.world.World;

public class GameEngine {

	private World world;
	private Thread engineThread;
	private boolean running;

	public GameEngine() {
		world = new World();

		engineThread = new Thread(new ChunkGenerationThread(this));
		running = false;
	}

	public boolean isRunning() {
		return running;
	}

	public void start() {
		running = true;
		engineThread.start();
	}

	public void end() {
		running = false;
	}

	public World getWorld() {
		return world;
	}
}
