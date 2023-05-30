package io.github.jarlish.voxeldemo.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;

public class VoxelCamera extends PerspectiveCamera {

	private float speed = 75.0f;
	private float sensitivity = 250f;
	private Vector3 tmp = new Vector3();
	private Vector3 tmp2 = new Vector3();
	private Vector3 tmp3 = new Vector3();

	public VoxelCamera() {
		super(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		near = 5.0f;
		far = 1000f;
		Gdx.input.setCursorCatched(true);
		Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
	}

	public void doInput(float delta) {
		//Input
		float deltaX = 0;
		float deltaY = 0;
		float deltaZ = 0;

		boolean fast = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT);
		if(Gdx.input.isKeyPressed(Input.Keys.W)) {
			deltaZ += speed * (fast ? 2 : 1);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.S)) {
			deltaZ -= speed * (fast ? 2 : 1);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.A)) {
			deltaX += speed * (fast ? 2 : 1);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.D)) {
			deltaX -= speed * (fast ? 2 : 1);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
			deltaY -= speed * (fast ? 2 : 1);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
			deltaY += speed * (fast ? 2 : 1);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
			Gdx.app.exit();
		}

		//Rotation
		float mouseDeltaX = -Gdx.input.getDeltaX() * sensitivity / viewportWidth;
		float mouseDeltaY = -Gdx.input.getDeltaY() * sensitivity / viewportHeight;
		Vector3 oldPitchAxis = tmp.set(direction).crs(up).nor();
		Vector3 newDirection = tmp2.set(direction).rotate(tmp, mouseDeltaY);
		Vector3 newPitchAxis = tmp3.set(tmp2).crs(up);
		if(!newPitchAxis.hasOppositeDirection(oldPitchAxis)) {
			direction.set(newDirection);
		}
		direction.rotate(up, mouseDeltaX);

		//Movement
		Vector3 cameraHorizontalDirection = tmp.set(this.direction.x, 0, this.direction.z).nor();
		Vector3 cameraRight = tmp2.set(up).crs(cameraHorizontalDirection).nor();
		Vector3 movement = cameraHorizontalDirection.scl(deltaZ * delta).add(cameraRight.scl(deltaX * delta));
		this.translate(movement);
		this.translate(0, deltaY * delta, 0);

		//Update
		this.update();
	}
}
