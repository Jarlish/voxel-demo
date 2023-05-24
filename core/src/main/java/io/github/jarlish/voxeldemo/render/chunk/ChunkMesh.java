package io.github.jarlish.voxeldemo.render.chunk;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes;
import io.github.jarlish.voxeldemo.world.World;
import io.github.jarlish.voxeldemo.world.chunk.Chunk;

public class ChunkMesh {

	public static final int VERTEX_SIZE = 5;

	private Chunk chunk;
	private Mesh mesh;
	private int size;
	private boolean dirty;
	private int currentTexture;

	public ChunkMesh(Chunk chunk) {
		this.chunk = chunk;
		dirty = true;
	}

	public void buildMesh(World world, float[] vertices, short[] indices, VertexAttributes vertexAttributes) {
		if(mesh == null) {
			mesh = new Mesh(true, Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE * VERTEX_SIZE * 5 * 4, Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE * 36 / 3, vertexAttributes);
			mesh.setIndices(indices);
		}
		int vertexCount = calculateVertices(world, vertices);
		size = vertexCount / 4 * 6;
		mesh.setVertices(vertices, 0, vertexCount * ChunkMesh.VERTEX_SIZE);
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
					currentTexture = voxel;
					if(world.getVoxel(worldX, worldY + 1, worldZ) == 0) {
						vertexOffset = createTop(worldX, worldY, worldZ, vertices, vertexOffset);
					}
					if(world.getVoxel(worldX, worldY - 1, worldZ) == 0) {
						vertexOffset = createBottom(worldX, worldY, worldZ, vertices, vertexOffset);
					}
					if(world.getVoxel(worldX - 1, worldY, worldZ) == 0) {
						vertexOffset = createLeft(worldX, worldY, worldZ, vertices, vertexOffset);
					}
					if(world.getVoxel(worldX + 1, worldY, worldZ) == 0) {
						vertexOffset = createRight(worldX, worldY, worldZ, vertices, vertexOffset);
					}
					if(world.getVoxel(worldX, worldY, worldZ - 1) == 0) {
						vertexOffset = createFront(worldX, worldY, worldZ, vertices, vertexOffset);
					}
					if(world.getVoxel(worldX, worldY, worldZ + 1) == 0) {
						vertexOffset = createBack(worldX, worldY, worldZ, vertices, vertexOffset);
					}
				}
			}
		}
		return vertexOffset / VERTEX_SIZE;
	}

	private int createTop(int x, int y, int z, float[] vertices, int vertexOffset) {
		//Top right
		vertexOffset += addPositionToVertices(x + 1, y + 1, z, vertices, vertexOffset);
		vertexOffset += addTextureCoordinatesToVertices(currentTexture, 0, 0, 0, vertices, vertexOffset);

		//Top left
		vertexOffset += addPositionToVertices(x, y + 1, z, vertices, vertexOffset);
		vertexOffset += addTextureCoordinatesToVertices(currentTexture, 0, 1, 0, vertices, vertexOffset);

		//Bottom left
		vertexOffset += addPositionToVertices(x, y + 1, z + 1, vertices, vertexOffset);
		vertexOffset += addTextureCoordinatesToVertices(currentTexture, 0, 1, 1, vertices, vertexOffset);

		//Bottom right
		vertexOffset += addPositionToVertices(x + 1, y + 1, z + 1, vertices, vertexOffset);
		vertexOffset += addTextureCoordinatesToVertices(currentTexture, 0, 0, 1, vertices, vertexOffset);

		return vertexOffset;
	}

	private int createBottom(int x, int y, int z, float[] vertices, int vertexOffset) {
		//Top right
		vertexOffset += addPositionToVertices(x, y, z, vertices, vertexOffset);
		vertexOffset += addTextureCoordinatesToVertices(currentTexture, 5, 0, 0, vertices, vertexOffset);

		//Top left
		vertexOffset += addPositionToVertices(x + 1, y, z, vertices, vertexOffset);
		vertexOffset += addTextureCoordinatesToVertices(currentTexture, 5, 1, 0, vertices, vertexOffset);

		//Bottom left
		vertexOffset += addPositionToVertices(x + 1, y, z + 1, vertices, vertexOffset);
		vertexOffset += addTextureCoordinatesToVertices(currentTexture, 5, 1, 1, vertices, vertexOffset);

		//Bottom right
		vertexOffset += addPositionToVertices(x, y, z + 1, vertices, vertexOffset);
		vertexOffset += addTextureCoordinatesToVertices(currentTexture, 5, 0, 1, vertices, vertexOffset);

		return vertexOffset;
	}

	private int createLeft(int x, int y, int z, float[] vertices, int vertexOffset) {
		//Top right
		vertexOffset += addPositionToVertices(x, y + 1, z + 1, vertices, vertexOffset);
		vertexOffset += addTextureCoordinatesToVertices(currentTexture, 1, 0, 0, vertices, vertexOffset);

		//Top left
		vertexOffset += addPositionToVertices(x, y + 1, z, vertices, vertexOffset);
		vertexOffset += addTextureCoordinatesToVertices(currentTexture, 1, 1, 0, vertices, vertexOffset);

		//Bottom left
		vertexOffset += addPositionToVertices(x, y, z, vertices, vertexOffset);
		vertexOffset += addTextureCoordinatesToVertices(currentTexture, 1, 1, 1, vertices, vertexOffset);

		//Bottom right
		vertexOffset += addPositionToVertices(x, y, z + 1, vertices, vertexOffset);
		vertexOffset += addTextureCoordinatesToVertices(currentTexture, 1, 0, 1, vertices, vertexOffset);

		return vertexOffset;
	}

	private int createRight(int x, int y, int z, float[] vertices, int vertexOffset) {
		//Top right
		vertexOffset += addPositionToVertices(x + 1, y + 1, z, vertices, vertexOffset);
		vertexOffset += addTextureCoordinatesToVertices(currentTexture, 2, 0, 0, vertices, vertexOffset);

		//Top left
		vertexOffset += addPositionToVertices(x + 1, y + 1, z + 1, vertices, vertexOffset);
		vertexOffset += addTextureCoordinatesToVertices(currentTexture, 2, 1, 0, vertices, vertexOffset);

		//Bottom left
		vertexOffset += addPositionToVertices(x + 1, y, z + 1, vertices, vertexOffset);
		vertexOffset += addTextureCoordinatesToVertices(currentTexture, 2, 1, 1, vertices, vertexOffset);

		//Bottom right
		vertexOffset += addPositionToVertices(x + 1, y, z, vertices, vertexOffset);
		vertexOffset += addTextureCoordinatesToVertices(currentTexture, 2, 0, 1, vertices, vertexOffset);

		return vertexOffset;
	}

	private int createFront(int x, int y, int z, float[] vertices, int vertexOffset) {
		//Bottom right
		vertexOffset += addPositionToVertices(x + 1, y, z, vertices, vertexOffset);
		vertexOffset += addTextureCoordinatesToVertices(currentTexture, 3, 0, 1, vertices, vertexOffset);

		//Bottom left
		vertexOffset += addPositionToVertices(x, y, z, vertices, vertexOffset);
		vertexOffset += addTextureCoordinatesToVertices(currentTexture, 3, 1, 1, vertices, vertexOffset);

		//Top left
		vertexOffset += addPositionToVertices(x, y + 1, z, vertices, vertexOffset);
		vertexOffset += addTextureCoordinatesToVertices(currentTexture, 3, 1, 0, vertices, vertexOffset);

		//Top right
		vertexOffset += addPositionToVertices(x + 1, y + 1, z, vertices, vertexOffset);
		vertexOffset += addTextureCoordinatesToVertices(currentTexture, 3, 0, 0, vertices, vertexOffset);

		return vertexOffset;
	}

	private int createBack(int x, int y, int z, float[] vertices, int vertexOffset) {
		//Top right
		vertexOffset += addPositionToVertices(x + 1, y + 1, z + 1, vertices, vertexOffset);
		vertexOffset += addTextureCoordinatesToVertices(currentTexture, 4, 0, 0, vertices, vertexOffset);

		//Top left
		vertexOffset += addPositionToVertices(x, y + 1, z + 1, vertices, vertexOffset);
		vertexOffset += addTextureCoordinatesToVertices(currentTexture, 4, 1, 0, vertices, vertexOffset);

		//Bottom left
		vertexOffset += addPositionToVertices(x, y, z + 1, vertices, vertexOffset);
		vertexOffset += addTextureCoordinatesToVertices(currentTexture, 4, 1, 1, vertices, vertexOffset);

		//Bottom right
		vertexOffset += addPositionToVertices(x + 1, y, z + 1, vertices, vertexOffset);
		vertexOffset += addTextureCoordinatesToVertices(currentTexture, 4, 0, 1, vertices, vertexOffset);

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

	public Mesh getMesh() {
		return mesh;
	}

	public int getSize() {
		return size;
	}

	public boolean isDirty() {
		return dirty;
	}
}
