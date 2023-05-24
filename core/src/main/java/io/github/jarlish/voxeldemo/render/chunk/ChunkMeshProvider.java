package io.github.jarlish.voxeldemo.render.chunk;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import io.github.jarlish.voxeldemo.VoxelDemo;
import io.github.jarlish.voxeldemo.render.WorldRenderer;
import io.github.jarlish.voxeldemo.world.chunk.Chunk;

public class ChunkMeshProvider implements RenderableProvider {

	private WorldRenderer worldRenderer;
	private Material material;
	private int chunksRendered;

	public ChunkMeshProvider(WorldRenderer worldRenderer) {
		this.worldRenderer = worldRenderer;
		material = new Material(TextureAttribute.createDiffuse(VoxelDemo.terrainTexture));
	}

	@Override
	public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
		chunksRendered = 0;
		for(Chunk chunk : worldRenderer.getWorld().getChunks().values()) {
			ChunkMesh chunkMesh = chunk.getChunkMesh();
			if(!worldRenderer.isChunkVisible(chunk) || chunkMesh.isDirty() || chunkMesh.getSize() == 0) {
				continue;
			}
			Mesh mesh = chunkMesh.getMesh();
			Renderable renderable = pool.obtain();
			renderable.material = material;
			renderable.meshPart.mesh = mesh;
			renderable.meshPart.offset = 0;
			renderable.meshPart.size = chunkMesh.getSize();
			renderable.meshPart.primitiveType = GL20.GL_TRIANGLES;
			renderables.add(renderable);
			chunksRendered++;
		}
	}

	public int getChunksRendered() {
		return chunksRendered;
	}
}
