package io.github.jarlish.voxeldemo.render.chunk;

import io.github.jarlish.voxeldemo.render.WorldRenderer;

public class MeshBuildingThread implements Runnable {

	private WorldRenderer worldRenderer;
	
	public MeshBuildingThread(WorldRenderer worldRenderer) {
		this.worldRenderer = worldRenderer;
	}
	
	@Override
	public void run() {
		while(worldRenderer.isRunning()) {
			worldRenderer.buildChunkMeshes();
		}
	}
}
