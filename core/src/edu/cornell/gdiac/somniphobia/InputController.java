/*
 * InputController.java
 *
 * This class buffers in input from the devices and converts it into its
 * semantic meaning. If your game had an option that allows the player to
 * remap the control keys, you would store this information in this class.
 * That way, the main GameEngine does not have to keep track of the current
 * key mapping.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.somniphobia;

import com.badlogic.gdx.*;
import com.badlogic.gdx.math.*;

import com.badlogic.gdx.utils.Array;
import edu.cornell.gdiac.util.*;

/**
 * Class for reading player input. 
 *
 * This supports only a keyboard.
 */
public class InputController {
	/** The singleton instance of the input controller */
	private static InputController theController = null;
	
	/** 
	 * Return the singleton instance of the input controller
	 *
	 * @return the singleton instance of the input controller
	 */
	public static InputController getInstance() {
		if (theController == null) {
			theController = new InputController();
		}
		return theController;
	}
	
	// Fields to manage buttons
	/** Whether the button to advanced worlds was pressed. */
	private boolean nextPressed;
	private boolean nextPrevious;
	/** Whether the reset button was pressed. */
	private boolean resetPressed;
	private boolean resetPrevious;
	/** Whether the jump button was pressed. */
	private boolean jumpPressed;
	private boolean jumpPrevious;
	/** Whether the dash button was pressed. */
	private boolean dashPressed;
	private boolean dashPrevious;
	/** Whether the hand holding button was pressed. */
	private boolean handHoldingPressed;
	private boolean handHoldingPrevious;
	/** Whether the switch button was pressed. */
	private boolean switchPressed;
	private boolean switchPrevious;
	/** Whether the debug toggle was pressed. */
	private boolean debugPressed;
	private boolean debugPrevious;

	/** Whether the camera WASD keys were pressed. */
	private boolean wPressed;
	private boolean aPressed;
	private boolean sPressed;
	private boolean dPressed;

	/** Whether the slider toggle was pressed. */
	private boolean sliderToggled;
	private boolean sliderToggledPrevious;

	/** Whether the exit button was pressed. */
	private boolean exitPressed;
	private boolean exitPrevious;

	private boolean prevPressed;
	private boolean prevPrevious;
	
	/** How much did we move horizontally? */
	private float horizontal;
	/** How much did we move vertically? */
	private float vertical;
	/** How much did we move the camera horizontally? */
	private float cameraHorizontal;
	/** How much did we move the camera vertically? */
	private float cameraVertical;
	
	/**
	 * Returns the amount of sideways movement. 
	 *
	 * -1 = left, 1 = right, 0 = still
	 *
	 * @return the amount of sideways movement. 
	 */
	public float getHorizontal() {
		return horizontal;
	}
	
	/**
	 * Returns the amount of vertical movement. 
	 *
	 * -1 = down, 1 = up, 0 = still
	 *
	 * @return the amount of vertical movement. 
	 */
	public float getVertical() { return vertical; }

	/**
	 * Returns the amount of sideways camera movement.
	 *
	 * -1 = left, 1 = right, 0 = still
	 *
	 * @return the amount of sideways camera movement.
	 */
	public float getCameraHorizontal() {
		return cameraHorizontal;
	}

	/**
	 * Returns the amount of vertical camera movement.
	 *
	 * -1 = down, 1 = up, 0 = still
	 *
	 * @return the amount of vertical camera movement.
	 */
	public float getCameraVertical() { return cameraVertical; }

	/**
	 * Returns true if the player wants to go to the next level.
	 *
	 * @return true if the player wants to go to the next level.
	 */
	public boolean didAdvance() {
		return nextPressed && !nextPrevious;
	}

	/**
	 * Returns true if the player wants to go to the previous level.
	 *
	 * @return true if the player wants to go to the previous level.
	 */
	public boolean didRetreat() {
		return prevPressed && !prevPrevious;
	}


	/**
	 * Returns true if the jump button was pressed.
	 *
	 * This is a one-press button. It only returns true at the moment it was
	 * pressed, and returns false at any frame afterwards.
	 *
	 * @return true if the jump button was pressed.
	 */
	public boolean didJump() {
		return jumpPressed && !jumpPrevious;
	}

	/**
	 * Returns true if the dash button was pressed.
	 *
	 * This is a one-press button. It only returns true at the moment it was
	 * pressed, and returns false at any frame afterwards.
	 *
	 * @return true if the dash button was pressed.
	 */
	public boolean didDash() {
		return dashPressed && !dashPrevious;
	}

	/**
	 * Returns true if the hand holding button was pressed.
	 *
	 * This is a one-press button. It only returns true at the moment it was
	 * pressed, and returns false at any frame afterwards.
	 *
	 * @return true if the dash button was pressed.
	 */
	public boolean didHoldHands() {
		return handHoldingPressed && !handHoldingPrevious;
	}

	/**
	 * Returns true if the switch button was pressed.
	 *
	 * This is a one-press button. It only returns true at the moment it was
	 * pressed, and returns false at any frame afterwards.
	 *
	 * @return true if the dash button was pressed.
	 */
	public boolean didSwitch() {
		return switchPressed && !switchPrevious;
	}

	/**
	 * Returns true if the reset button was pressed.
	 *
	 * @return true if the reset button was pressed.
	 */
	public boolean didReset() {
		return resetPressed && !resetPrevious;
	}

	/**
	 * Returns true if the player wants to go toggle the debug mode.
	 *
	 * @return true if the player wants to go toggle the debug mode.
	 */
	public boolean didDebug() {
		return debugPressed && !debugPrevious;
	}


	/**
	 * Returns true if the player wants to go toggle the debug mode.
	 *
	 * @return true if the player wants to go toggle the debug mode.
	 */
	public boolean didToggleSliders() {
		return sliderToggled && !sliderToggledPrevious;
	}
	
	/**
	 * Returns true if the exit button was pressed.
	 *
	 * @return true if the exit button was pressed.
	 */
	public boolean didExit() {
		return exitPressed && !exitPrevious;
	}

	/**
	 * Returns true if the W button was pressed.
	 *
	 * @return true if the W button was pressed.
	 */
	public boolean didCameraUp() {
		return wPressed;
	}

	/**
	 * Returns true if the A button was pressed.
	 *
	 * @return true if the A button was pressed.
	 */
	public boolean didCameraLeft() {
		return aPressed;
	}

	/**
	 * Returns true if the S button was pressed.
	 *
	 * @return true if the S button was pressed.
	 */
	public boolean didCameraDown() {
		return sPressed;
	}

	/**
	 * Returns true if the D button was pressed.
	 *
	 * @return true if the D button was pressed.
	 */
	public boolean didCameraRight() { return dPressed; }

	/**
	 * Returns true if any of WASD are pressed
	 *
	 * @return true if any of WASD are pressed
	 */
	public boolean didWASDPressed() {
		return wPressed || aPressed || sPressed || dPressed;
	}


	/**
	 * Reads the input for the player and converts the result into game logic.
	 */
	public void readInput(Rectangle bounds, Vector2 scale) {
		// Copy state from last animation frame
		// Helps us ignore buttons that are held down
		jumpPrevious  = jumpPressed;
		dashPrevious = dashPressed;
		handHoldingPrevious = handHoldingPressed;
		switchPrevious = switchPressed;
		resetPrevious  = resetPressed;
		debugPrevious  = debugPressed;
		sliderToggledPrevious = sliderToggled;
		exitPrevious = exitPressed;
		nextPrevious = nextPressed;
		prevPrevious = prevPressed;

		readKeyboard();
	}

	/**
	 * Reads input from the keyboard.
	 */
	private void readKeyboard() {
		// Give priority to gamepad results
		resetPressed  = Gdx.input.isKeyPressed(Input.Keys.R);
		debugPressed  = Gdx.input.isKeyPressed(Input.Keys.G);
		sliderToggled  = Gdx.input.isKeyPressed(Input.Keys.Q);
		jumpPressed  = Gdx.input.isKeyPressed(Input.Keys.Z);
		dashPressed = Gdx.input.isKeyPressed(Input.Keys.X);
		handHoldingPressed = Gdx.input.isKeyPressed(Input.Keys.C);
		switchPressed = Gdx.input.isKeyPressed(Input.Keys.SPACE);
		exitPressed   = Gdx.input.isKeyPressed(Input.Keys.ESCAPE);
		prevPressed = (Gdx.input.isKeyPressed(Input.Keys.P));
		nextPressed = (Gdx.input.isKeyPressed(Input.Keys.N));

		wPressed = (Gdx.input.isKeyPressed(Input.Keys.W));
		aPressed = (Gdx.input.isKeyPressed(Input.Keys.A));
		sPressed = (Gdx.input.isKeyPressed(Input.Keys.S));
		dPressed = (Gdx.input.isKeyPressed(Input.Keys.D));

		// Directional controls



		horizontal = 0.0f;
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			horizontal += 1.0f;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			horizontal -= 1.0f;
		}

		vertical = 0.0f;
		if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
			vertical += 1.0f;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
			vertical -= 1.0f;
		}

		cameraHorizontal = 0.0f;
		if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			cameraHorizontal += 1.0f;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			cameraHorizontal -= 1.0f;
		}

		cameraVertical = 0.0f;
		if (Gdx.input.isKeyPressed(Input.Keys.W)) {
			cameraVertical += 1.0f;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.S)) {
			cameraVertical -= 1.0f;
		}



		// Print testing for unimplemented features
		boolean DEBUG = false;
		if (DEBUG) {
			if(dashPressed) {
				System.out.println("Somni/Phobia dashes");
			}
			if(handHoldingPressed) {
				System.out.println("Somni & Phobia hold hands");
			}
			if(switchPressed) {
				System.out.println("Somni/Phobia switched");
			}
		}
	}

}