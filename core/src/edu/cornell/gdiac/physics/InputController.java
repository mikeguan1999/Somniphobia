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
package edu.cornell.gdiac.physics;

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
	/** Whether the exit button was pressed. */
	private boolean exitPressed;
	private boolean exitPrevious;
	
	/** How much did we move horizontally? */
	private float horizontal;
	/** How much did we move vertically? */
	private float vertical;
	
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
	 * Returns true if the exit button was pressed.
	 *
	 * @return true if the exit button was pressed.
	 */
	public boolean didExit() {
		return exitPressed && !exitPrevious;
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
		exitPrevious = exitPressed;

		readKeyboard();
	}

	/**
	 * Reads input from the keyboard.
	 */
	private void readKeyboard() {
		// Give priority to gamepad results
		resetPressed  = Gdx.input.isKeyPressed(Input.Keys.R);
		debugPressed  = Gdx.input.isKeyPressed(Input.Keys.G);
		jumpPressed  = Gdx.input.isKeyPressed(Input.Keys.E) || Gdx.input.isKeyPressed(Input.Keys.O);
		dashPressed = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
		handHoldingPressed = Gdx.input.isKeyPressed(Input.Keys.SPACE);
		switchPressed = Gdx.input.isKeyPressed(Input.Keys.Q) || Gdx.input.isKeyPressed(Input.Keys.LEFT_BRACKET);
				exitPressed   = Gdx.input.isKeyPressed(Input.Keys.ESCAPE);

		// Directional controls
		horizontal = 0.0f;
		if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.APOSTROPHE)) {
			horizontal += 1.0f;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.L)) {
			horizontal -= 1.0f;
		}
		
		vertical = 0.0f;
		if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.P)) {
			vertical += 1.0f;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.SEMICOLON)) {
			vertical -= 1.0f;
		}

		// Print testing for unimplemented features
		boolean DEBUG = true;
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