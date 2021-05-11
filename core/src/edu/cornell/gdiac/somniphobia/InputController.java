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

/**
 * Class for reading player input. 
 *
 * This supports only a keyboard.
 */
public class InputController {
	/** The singleton instance of the input controller */
	private static InputController theController = null;


	/** Key mappings */
	private int jumpKey	= Input.Keys.UP;
	private int dashKey = Input.Keys.SHIFT_LEFT;
	private int dashKey2 = Input.Keys.SPACE;
	private int handHoldingKey = Input.Keys.Z;
	private int fullscreenKey = Input.Keys.O;
	private int handHoldingKey2 = Input.Keys.E;
	private int switchKey = Input.Keys.X;
	private int switchKey2 = Input.Keys.Q;
	private int prevKey = Input.Keys.P;
	private int nextKey = Input.Keys.N;
	private int leftKey = Input.Keys.LEFT;
	private int rightKey = Input.Keys.RIGHT;
	
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
	/** Whether the button to go to the next level was pressed. */
	private boolean nextPressed;
	private boolean nextPrevious;
	/** Whether the button to go to the previous level was pressed. */
	private boolean prevPressed;
	private boolean prevPrevious;
	/** Whether the button to switch to the creator was pressed. */
	private boolean switchToCreatorPressed;
	private boolean switchToCreatorPrevious;
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
	/** Whether the hand holding button was pressed. */
	private boolean fullscreenPressed;
	private boolean fullscreenPrevious;
	/** Whether the switch button was pressed. */
	private boolean switchPressed;
	private boolean switchPrevious;
	/** Whether the debug toggle was pressed. */
	private boolean debugPressed;
	private boolean debugPrevious;
	/** Whether the left or right keys were pressed. */
	private boolean walkPressed;

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

	/** Whether the teritiary action button was pressed. */
	private boolean tertiaryPressed;
	/** The crosshair position (for raddoll) */
	private Vector2 crosshair = new Vector2();
	/** The crosshair cache (for using as a return value) */
	private Vector2 crosscache = new Vector2();


	/** How much did we move horizontally? */
	private float horizontal;
	/** How much did we move vertically? */
	private float vertical;


	/** How much did we move the camera horizontally? */
	private float cameraHorizontal;
	/** How much did we move the camera vertically? */
	private float cameraVertical;

	private boolean pauseClicked;
	private boolean pauseClickedPrevious;
	
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
	 * Returns true if the hand holding button was pressed.
	 *
	 * This is a one-press button. It only returns true at the moment it was
	 * pressed, and returns false at any frame afterwards.
	 *
	 * @return true if the dash button was pressed.
	 */
	public boolean didFullscreen() {
		return fullscreenPressed && !fullscreenPrevious;
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
	 * Returns true if the button to enter the creator was pressed.
	 *
	 * @return true if the creator mode button was pressed.
	 */
	public boolean didSwitchToCreatorMode() {
		return switchToCreatorPressed && !switchToCreatorPrevious;
	}

	public boolean didClickPause() { return pauseClicked && !pauseClickedPrevious; }


	/**
	 * Returns the current position of the crosshairs on the screen.
	 *
	 * This value does not return the actual reference to the crosshairs position.
	 * That way this method can be called multiple times without any fair that
	 * the position has been corrupted.  However, it does return the same object
	 * each time.  So if you modify the object, the object will be reset in a
	 * subsequent call to this getter.
	 *
	 * @return the current position of the crosshairs on the screen.
	 */
	public Vector2 getCrossHair() {
		return crosscache.set(crosshair);
	}

	/**
	 * Reads the input for the player and converts the result into game logic.
	 */
	public void readInput(Rectangle bounds, Vector2 scale) {
		// Copy state from last animation frame
		// Helps us ignore buttons that are held down

		jumpPrevious  			= jumpPressed;
		dashPrevious 			= dashPressed;
		handHoldingPrevious 	= handHoldingPressed;
		fullscreenPrevious   	= fullscreenPressed;
		switchPrevious 			= switchPressed;
		resetPrevious  			= resetPressed;
		debugPrevious  			= debugPressed;
		sliderToggledPrevious 	= sliderToggled;
		exitPrevious 			= exitPressed;
		switchToCreatorPrevious = switchToCreatorPressed;
		nextPrevious 			= nextPressed;
		prevPrevious 			= prevPressed;
		pauseClickedPrevious	= pauseClicked;

		readKeyboard(bounds,scale);
	}

	/**
	 * Reads input from the keyboard.
	 */
	private void readKeyboard(Rectangle bounds, Vector2 scale) {
		// Give priority to gamepad results
		resetPressed			= Gdx.input.isKeyPressed(Input.Keys.R);
		debugPressed  			= Gdx.input.isKeyPressed(Input.Keys.G);
		sliderToggled  			= Gdx.input.isKeyPressed(Input.Keys.RIGHT_BRACKET);
		jumpPressed  			= Gdx.input.isKeyPressed(jumpKey);
		dashPressed 			= Gdx.input.isKeyPressed(dashKey) || Gdx.input.isKeyPressed(dashKey2);
		handHoldingPressed 		= Gdx.input.isKeyPressed(handHoldingKey) || Gdx.input.isKeyPressed(handHoldingKey2);
		fullscreenPressed       = Gdx.input.isButtonPressed(fullscreenKey);
		switchPressed 			= Gdx.input.isKeyPressed(switchKey) || Gdx.input.isKeyPressed(switchKey2);
		exitPressed   			= Gdx.input.isKeyPressed(Input.Keys.ESCAPE);
		switchToCreatorPressed 	= Gdx.input.isKeyPressed(Input.Keys.BACKSLASH);
		prevPressed 			= Gdx.input.isKeyPressed(Input.Keys.P);
		nextPressed 			= Gdx.input.isKeyPressed(Input.Keys.N);
		walkPressed 			= Gdx.input.isKeyPressed(leftKey) || Gdx.input.isKeyPressed(rightKey);
		pauseClicked			= Gdx.input.isKeyPressed(Input.Keys.LEFT_BRACKET);

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
		if (dPressed) {
			cameraHorizontal += 1.0f;
		}
		if (aPressed) {
			cameraHorizontal -= 1.0f;
		}

		cameraVertical = 0.0f;
		if (wPressed) {
			cameraVertical += 1.0f;
		}
		if (sPressed) {
			cameraVertical -= 1.0f;
		}

		tertiaryPressed = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
		crosshair.set(Gdx.input.getX(), Gdx.input.getY());
		crosshair.scl(1/scale.x,-1/scale.y);
		crosshair.y += bounds.height;
	}

	/**
	 * Returns true if the tertiary action button was pressed.
	 *
	 * This is a sustained button. It will returns true as long as the player
	 * holds it down.
	 *
	 * @return true if the secondary action button was pressed.
	 */
	public boolean didTertiary() {
		return tertiaryPressed;
	}


}