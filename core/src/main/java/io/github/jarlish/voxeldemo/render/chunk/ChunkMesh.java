package io.github.jarlish.voxeldemo.render.chunk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.IndexData;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.VertexBufferObjectWithVAO;
import com.badlogic.gdx.graphics.glutils.VertexData;
import io.github.jarlish.voxeldemo.world.World;
import io.github.jarlish.voxeldemo.world.chunk.Chunk;

public class ChunkMesh {

	public static final int VERTEX_SIZE = 5;

	private Chunk chunk;
	private VertexData vertexData;
	private int size;
	private boolean dirty;
	private boolean didFirstRender;

	public ChunkMesh(Chunk chunk) {
		this.chunk = chunk;
		dirty = true;
		didFirstRender = false;
	}

	public void render(ShaderProgram shader, IndexData indexBuffer) {
		vertexData.bind(shader);
		indexBuffer.bind();
		Gdx.gl.glDrawElements(GL20.GL_TRIANGLES, size / 4 * 6, GL20.GL_UNSIGNED_SHORT, 0);
		vertexData.unbind(shader);
		didFirstRender = true;
	}

	public void createMesh(World world, VertexAttributes vertexAttributes) {
		if(vertexData == null) {
			vertexData = new VertexBufferObjectWithVAO(false, Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE, vertexAttributes);
		}
	}

	public void buildMesh(World world, float[] vertices) {
		if(vertexData == null) {
			return;
		}
		int vertexCount = calculateVertices(world, vertices);
		size = vertexCount;
		vertexData.setVertices(vertices, 0, vertexCount * VERTEX_SIZE);
		dirty = false;
	}

	public int calculateVertices(World world, float[] vertices) {
		int vertexOffset = 0;
		for(int y = 0; y < Chunk.CHUNK_SIZE; y++) {
			for(int z = 0; z < Chunk.CHUNK_SIZE; z++) {
				for(int x = 0; x < Chunk.CHUNK_SIZE; x++) {
					int worldX = (chunk.getLocation().x * Chunk.CHUNK_SIZE) + x;
					int worldY = (chunk.getLocation().y * Chunk.CHUNK_SIZE) + y;
					int worldZ = (chunk.getLocation().z * Chunk.CHUNK_SIZE) + z;
					byte voxel = world.getVoxel(worldX, worldY, worldZ);

					if(voxel == 0) {
						continue;
					}
					if(world.getVoxel(worldX, worldY + 1, worldZ) == 0) {
						vertexOffset = createTop(voxel, worldX, worldY, worldZ, vertices, vertexOffset);
					}
					if(world.getVoxel(worldX, worldY - 1, worldZ) == 0) {
						vertexOffset = createBottom(voxel, worldX, worldY, worldZ, vertices, vertexOffset);
					}
					if(world.getVoxel(worldX - 1, worldY, worldZ) == 0) {
						vertexOffset = createLeft(voxel, worldX, worldY, worldZ, vertices, vertexOffset);
					}
					if(world.getVoxel(worldX + 1, worldY, worldZ) == 0) {
						vertexOffset = createRight(voxel, worldX, worldY, worldZ, vertices, vertexOffset);
					}
					if(world.getVoxel(worldX, worldY, worldZ - 1) == 0) {
						vertexOffset = createFront(voxel, worldX, worldY, worldZ, vertices, vertexOffset);
					}
					if(world.getVoxel(worldX, worldY, worldZ + 1) == 0) {
						vertexOffset = createBack(voxel, worldX, worldY, worldZ, vertices, vertexOffset);
					}
				}
			}
		}
		return vertexOffset / VERTEX_SIZE;
	}

	private int createTop(int texture, int x, int y, int z, float[] vertices, int vertexOffset) {
		//Top right
		vertexOffset += addPositionToVertices(x + 1, y + 1, z, vertices, vertexOffset);
		vertexOffset += addTextureCoordinatesToVertices(texture, 0, 0, 0, vertices, vertexOffset);

		//Top left
		vertexOffset += addPositionToVertices(x, y + 1, z, vertices, vertexOffset);
		vertexOffset += addTextureCoordinatesToVertices(texture, 0, 1, 0, vertices, vertexOffset);

		//Bottom left
		vertexOffset += addPositionToVertices(x, y + 1, z + 1, vertices, vertexOffset);
		vertexOffset += addTextureCoordinatesToVertices(texture, 0, 1, 1, vertices, vertexOffset);

		//Bottom right
		vertexOffset += addPositionToVertices(x + 1, y + 1, z + 1, vertices, vertexOffset);
		vertexOffset += addTextureCoordinatesToVertices(texture, 0, 0, 1, vertices, vertexOffset);

		return vertexOffset;
	}

	private int createBottom(int texture, int x, int y, int z, float[] vertices, int vertexOffset) {
		//Top right
		vertexOffset += addPositionToVertices(x, y, z, vertices, vertexOffset);
		vertexOffset += addTextureCoordinatesToVertices(texture, 5, 0, 0, vertices, vertexOffset);

		//Top left
		vertexOffset += addPositionToVertices(x + 1, y, z, vertices, vertexOffset);
		vertexOffset += addTextureCoordinatesToVertices(texture, 5, 1, 0, vertices, vertexOffset);

		//Bottom left
		vertexOffset += addPositionToVertices(x + 1, y, z + 1, vertices, vertexOffset);
		vertexOffset += addTextureCoordinatesToVertices(texture, 5, 1, 1, vertices, vertexOffset);

		//Bottom right
		vertexOffset += addPositionToVertices(x, y, z + 1, vertices, vertexOffset);
		vertexOffset += addTextureCoordinatesToVertices(texture, 5, 0, 1, vertices, vertexOffset);

		return vertexOffset;
	}

	private int createLeft(int texture, int x, int y, int z, float[] vertices, int vertexOffset) {
		//Top right
		vertexOffset += addPositionToVertices(x, y + 1, z + 1, vertices, vertexOffset);
		vertexOffset += addTextureCoordinatesToVertices(texture, 1, 0, 0, vertices, vertexOffset);

		//Top left
		vertexOffset += addPositionToVertices(x, y + 1, z, vertices, vertexOffset);
		vertexOffset += addTextureCoordinatesToVertices(texture, 1, 1, 0, vertices, vertexOffset);

		//Bottom left
		vertexOffset += addPositionToVertices(x, y, z, vertices, vertexOffset);
		vertexOffset += addTextureCoordinatesToVertices(texture, 1, 1, 1, vertices, vertexOffset);

		//Bottom right
		vertexOffset += addPositionToVertices(x, y, z + 1, vertices, vertexOffset);
		vertexOffset += addTextureCoordinatesToVertices(texture, 1, 0, 1, vertices, vertexOffset);

		return vertexOffset;
	}

	private int createRight(int texture, int x, int y, int z, float[] vertices, int vertexOffset) {
		//Top right
		vertexOffset += addPositionToVertices(x + 1, y + 1, z, vertices, vertexOffset);
		vertexOffset += addTextureCoordinatesToVertices(texture, 2, 0, 0, vertices, vertexOffset);

		//Top left
		vertexOffset += addPositionToVertices(x + 1, y + 1, z + 1, vertices, vertexOffset);
		vertexOffset += addTextureCoordinatesToVertices(texture, 2, 1, 0, vertices, vertexOffset);

		//Bottom left
		vertexOffset += addPositionToVertices(x + 1, y, z + 1, vertices, vertexOffset);
		vertexOffset += addTextureCoordinatesToVertices(texture, 2, 1, 1, vertices, vertexOffset);

		//Bottom right
		vertexOffset += addPositionToVertices(x + 1, y, z, vertices, vertexOffset);
		vertexOffset += addTextureCoordinatesToVertices(texture, 2, 0, 1, vertices, vertexOffset);

		return vertexOffset;
	}

	private int createFront(int texture, int x, int y, int z, float[] vertices, int vertexOffset) {
		//Bottom right
		vertexOffset += addPositionToVertices(x + 1, y, z, vertices, vertexOffset);
		vertexOffset += addTextureCoordinatesToVertices(texture, 3, 0, 1, vertices, vertexOffset);

		//Bottom left
		vertexOffset += addPositionToVertices(x, y, z, vertices, vertexOffset);
		vertexOffset += addTextureCoordinatesToVertices(texture, 3, 1, 1, vertices, vertexOffset);

		//Top left
		vertexOffset += addPositionToVertices(x, y + 1, z, vertices, vertexOffset);
		vertexOffset += addTextureCoordinatesToVertices(texture, 3, 1, 0, vertices, vertexOffset);

		//Top right
		vertexOffset += addPositionToVertices(x + 1, y + 1, z, vertices, vertexOffset);
		vertexOffset += addTextureCoordinatesToVertices(texture, 3, 0, 0, vertices, vertexOffset);

		return vertexOffset;
	}

	private int createBack(int texture, int x, int y, int z, float[] vertices, int vertexOffset) {
		//Top right
		vertexOffset += addPositionToVertices(x + 1, y + 1, z + 1, vertices, vertexOffset);
		vertexOffset += addTextureCoordinatesToVertices(texture, 4, 0, 0, vertices, vertexOffset);

		//Top left
		vertexOffset += addPositionToVertices(x, y + 1, z + 1, vertices, vertexOffset);
		vertexOffset += addTextureCoordinatesToVertices(texture, 4, 1, 0, vertices, vertexOffset);

		//Bottom left
		vertexOffset += addPositionToVertices(x, y, z + 1, vertices, vertexOffset);
		vertexOffset += addTextureCoordinatesToVertices(texture, 4, 1, 1, vertices, vertexOffset);

		//Bottom right
		vertexOffset += addPositionToVertices(x + 1, y, z + 1, vertices, vertexOffset);
		vertexOffset += addTextureCoordinatesToVertices(texture, 4, 0, 1, vertices, vertexOffset);

		return vertexOffset;
	}

	private int addPositionToVertices(int x, int y, int z, float[] vertices, int vertexOffset) {
		vertices[vertexOffset++] = x;
		vertices[vertexOffset++] = y;
		vertices[vertexOffset] = z;
		return 3;
	}

	private int addTextureCoordinatesToVertices(int texture, int face, int xOffset, int yOffset, float[] vertices, int vertexOffset) {
		vertices[vertexOffset++] = (face + xOffset) * (1f / 3f);
		vertices[vertexOffset] = (texture + yOffset) * (1f / 5f);
		return 2;
	}

	public int getSize() {
		return size;
	}

	public boolean isDirty() {
		return dirty;
	}

	public boolean didFirstRender() {
		return didFirstRender;
	}
}
