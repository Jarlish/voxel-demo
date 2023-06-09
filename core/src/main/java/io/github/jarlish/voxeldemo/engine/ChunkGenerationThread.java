package io.github.jarlish.voxeldemo.engine;

public class ChunkGenerationThread implements Runnable {

	private GameEngine gameEngine;
	
	public ChunkGenerationThread(GameEngine gameEngine) {
		this.gameEngine = gameEngine;
	}
	
	@Override
	public void run() {
		while(gameEngine.isRunning()) {
			gameEngine.getWorld().init();
			while(gameEngine.isRunning()) {
				gameEngine.getWorld().generateChunks();
			}
		}
	}
}
