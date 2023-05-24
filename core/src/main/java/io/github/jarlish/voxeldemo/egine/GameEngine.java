package io.github.jarlish.voxeldemo.egine;

import io.github.jarlish.voxeldemo.world.World;

public class GameEngine {

	private World world;
	private Thread engineThread;
	private boolean running = true;

	public GameEngine() {
		world = new World();

		engineThread = new Thread(new Runnable() {
			@Override
			public void run() {
				//Initialization
				world.init();

				//Loop
				int tps = 20;
				double last = System.nanoTime();
				double delta = 0;
				double ns = 1000000000 / tps;
				while(running) {
					double now = System.nanoTime();
					delta += (now - last) / ns;
					last = now;
					if(delta >= 1) {
						world.tick();
						delta--;
					}
				}
			}
		});
	}

	public void start() {
		engineThread.start();
	}

	public void end() {
		running = false;
	}
	
	public World getWorld() {
		return world;
	}
}
