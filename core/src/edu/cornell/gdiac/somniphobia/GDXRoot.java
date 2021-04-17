/*
 * GDXRoot.java
 *
 * This is the primary class file for running the game.  It is the "static main" of
 * LibGDX.  In the first lab, we extended ApplicationAdapter.  In previous lab
 * we extended Game.  This is because of a weird graphical artifact that we do not
 * understand.  Transparencies (in 3D only) is failing when we use ApplicationAdapter. 
 * There must be some undocumented OpenGL code in setScreen.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * Updated asset version, 2/6/2021
 */
package edu.cornell.gdiac.somniphobia;

import com.badlogic.gdx.*;
import edu.cornell.gdiac.somniphobia.game.controllers.PlatformController;
import edu.cornell.gdiac.util.*;
import edu.cornell.gdiac.assets.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import edu.cornell.gdiac.somniphobia.Menu;
import org.lwjgl.Sys;

/**
 * Root class for a LibGDX.  
 * 
 * This class is technically not the ROOT CLASS. Each platform has another class above
 * this (e.g. PC games use DesktopLauncher) which serves as the true root.  However, 
 * those classes are unique to each platform, while this class is the same across all 
 * plaforms. In addition, this functions as the root class all intents and purposes, 
 * and you would draw it as a root class in an architecture specification.  
 */
public class GDXRoot extends Game implements ScreenListener {
	/** AssetManager to load game assets (textures, sounds, etc.) */
	AssetDirectory directory;
	/** Drawing context to display graphics (VIEW CLASS) */
	private GameCanvas canvas; 
	/** Player mode for the asset loading screen (CONTROLLER CLASS) */
	private LoadingMode loading;
	/** The World Controller */
	private WorldController[] controllers;
	/** Player mode for the the game proper (CONTROLLER CLASS) */
	private int current;
	private int numLevelsPerPage = 4;
	private int totalNumLevels = 10;
	private int numPages;
	private Menu[] menuPages;
	private Menu currentMenu;
	private int currentMenuIndex;
	private Boolean level1;

	private OrthographicCamera cam;

	/**
	 * Creates a new game from the configuration settings.
	 *
	 * This method configures the asset manager, but does not load any assets
	 * or assign any screen.
	 */
	public GDXRoot() { }

	/** 
	 * Called when the Application is first created.
	 * 
	 * This is method immediately loads assets for the loading screen, and prepares
	 * the asynchronous loader for all other assets.
	 */
	public void create() {
		numPages = totalNumLevels/numLevelsPerPage;
		if (totalNumLevels%numLevelsPerPage != 0){
			numPages += 1;
		}

		canvas  = new GameCanvas();
		loading = new LoadingMode("assets.json",canvas,1);

		menuPages = new Menu[numPages];
		for (int i=0; i<menuPages.length; i++){
			if (i==0){
				Menu menu = new Menu(canvas, false, true, i*numLevelsPerPage, totalNumLevels);
				menuPages[i] = menu;
			}
			else if (i== menuPages.length-1){
				Menu menu = new Menu(canvas, true, false, i*numLevelsPerPage, totalNumLevels);
				menuPages[i] = menu;
			}
			else {
				Menu menu = new Menu(canvas, true, true, i*numLevelsPerPage, totalNumLevels);
				menuPages[i] = menu;
			}
		}
//		0123
//				4567
//						891011

		currentMenuIndex = 0;
		currentMenu = menuPages[currentMenuIndex];

		// Initialize the Platformer Controller
		// TODO
		controllers = new WorldController[10];
		controllers[0] = new PlatformController(0);
		controllers[1] = new PlatformController(1);
		controllers[2] = new PlatformController(2);
		controllers[3] = new PlatformController(3);
		controllers[4] = new PlatformController(0);
		controllers[5] = new PlatformController(0);
		controllers[6] = new PlatformController(0);
		controllers[7] = new PlatformController(0);
		controllers[8] = new PlatformController(0);
		controllers[9] = new PlatformController(0);


		// Constructs a new OrthographicCamera, using the given viewport width and height
		// Height is multiplied by aspect ratio.

		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		cam = new OrthographicCamera(30, 30 * (h / w));

		cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
		cam.update();

		loading.setScreenListener(this);
		setScreen(loading);
	}

	public Menu getCurrentMenu(){
		return currentMenu;
	}
	/** 
	 * Called when the Application is destroyed. 
	 *
	 * This is preceded by a call to pause().
	 */
	public void dispose() {
		// Call dispose on our children
		setScreen(null);

		for(int ii = 0; ii < controllers.length; ii++) {
			controllers[ii].dispose();
		}

		currentMenu.dispose();
		canvas.dispose();
		canvas = null;

		// Unload all of the resources
		if (directory != null) {
			directory.unloadAssets();
			directory.dispose();
			directory = null;
		}
		super.dispose();
	}
	
	/**
	 * Called when the Application is resized. 
	 *
	 * This can happen at any point during a non-paused state but will never happen 
	 * before a call to create().
	 *
	 * @param width  The new width in pixels
	 * @param height The new height in pixels
	 * @param height The new height in pixels
	 */
	public void resize(int width, int height) {
		canvas.resize();
		super.resize(width,height);
	}
	
	/**
	 * The given screen has made a request to exit its player mode.
	 *
	 * The value exitCode can be used to implement menu options.
	 *
	 * @param screen   The screen requesting to exit
	 * @param exitCode The state of the screen upon exit
	 */
	public void exitScreen(Screen screen, int exitCode) {
		if (screen == loading) {
			for(int ii = 0; ii < controllers.length; ii++) {
				directory = loading.getAssets();
				controllers[ii].gatherAssets(directory);
				controllers[ii].setScreenListener(this);
				controllers [ii].setCanvas(canvas);
			}
			for(int ii = 0; ii < menuPages.length; ii++) {
				menuPages[ii].setScreenListener(this);
				setScreen(menuPages[ii]);
			}


			currentMenu.setScreenListener(this);
//			menu.setCanvas(canvas);
			setScreen(currentMenu);
			loading.dispose();
			loading = null;
		} else if (screen==currentMenu){
			if (exitCode<0){
				if (exitCode==currentMenu.getLEFT_EXIT_CODE()){
					currentMenuIndex -= 1;
				}
				else if (exitCode==currentMenu.getRIGHT_EXIT_CODE()) {
					currentMenuIndex += 1;
				}
				currentMenu = menuPages[currentMenuIndex];
				setScreen(currentMenu);
			}
			else {
				current = exitCode;
				controllers[exitCode].reset();
				setScreen(controllers[exitCode]);
			}
		} else if (exitCode == WorldController.EXIT_MENU) {
//			resetting the menu
			menuPages[currentMenuIndex] = new Menu(canvas, currentMenu.getLeftExist(), currentMenu.getRightExist(),
					currentMenuIndex*numLevelsPerPage, totalNumLevels);
			currentMenu = menuPages[currentMenuIndex];
			currentMenu.setScreenListener(this);
			setScreen(currentMenu);
		} else if (exitCode == WorldController.EXIT_NEXT) {
			current = (current + 1 ) % controllers.length;
			controllers[current].reset();
			setScreen(controllers[current]);
		} else if (exitCode == WorldController.EXIT_PREV) {
			current = (current+controllers.length-1) % controllers.length;
			controllers[current].reset();
			setScreen(controllers[current]);
		} else if (exitCode == WorldController.EXIT_QUIT) {
			// We quit the main application
			Gdx.app.exit();
		}
	}

}
