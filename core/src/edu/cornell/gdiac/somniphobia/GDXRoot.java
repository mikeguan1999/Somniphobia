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
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import edu.cornell.gdiac.somniphobia.game.controllers.LevelController;
import edu.cornell.gdiac.somniphobia.game.controllers.LevelCreator;
import edu.cornell.gdiac.somniphobia.game.controllers.PlatformController;
import edu.cornell.gdiac.util.*;
import edu.cornell.gdiac.assets.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import org.lwjgl.Sys;

import java.util.HashMap;
import com.badlogic.gdx.scenes.scene2d.Stage;
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
	static AssetDirectory directory;
	/** Drawing context to display graphics (VIEW CLASS) */
	private GameCanvas canvas;

	/** Platform controller which controlls the filters*/
	private PlatformController platformController;

	/** Player mode for the asset loading screen (CONTROLLER CLASS) */
	private LoadingMode loading;

	static private Preferences preferences;

	/** The World Controller */
	static private WorldController[] controllers;

	/** Player mode for the the game proper (CONTROLLER CLASS) */
	private int current;

	/** Level selection screen variables */
	static public int totalNumLevels = 36;
	private MenuScrollable menu;

	private OrthographicCamera cam;
	private MainMenu mainMenu;

	static private final int LEVEL_CONTROLLER_INDEX = 0;
	static private final int LEVEL_CREATOR_INDEX = 1;

	private WorldSelect worldSelectMenu;
	private MenuScrollable [] menus;
	private final int numWorlds=5;
//	in the sequence of first row then second row of buttons in the world selector
	private int [] worldToNumLevels = {7, 7, 8, 2, 4};
	private boolean [] levelsCompleted;
	private int currentIndexController;
	private Stage pauseMenuStage;
	private Stage pauseButtonStage;


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
		canvas  = new GameCanvas();
		platformController = new PlatformController();
		loading = new LoadingMode("assets.json",canvas,1);
		worldSelectMenu = new WorldSelect(canvas);
		levelsCompleted = new boolean[totalNumLevels];

//		for (int k=0; k< totalNumLevels; k++){
//			levelsCompleted[k] = true;
//		}


//		menuPages = new Menu[numPages];
//		for (int i=0; i<menuPages.length; i++){
//			if (i==0){
//				Menu menu = new Menu(canvas, false, true, i*numLevelsPerPage, totalNumLevels);
//				menuPages[i] = menu;
//			}
//			else if (i== menuPages.length-1){
//				Menu menu = new Menu(canvas, true, false, i*numLevelsPerPage, totalNumLevels);
//				menuPages[i] = menu;
//			}
//			else {
//				Menu menu = new Menu(canvas, true, true, i*numLevelsPerPage, totalNumLevels);
//				menuPages[i] = menu;
//			}
//		}
//		0123
//				4567
//						891011
//
//		currentMenuIndex = 0;
//		currentMenu = menuPages[currentMenuIndex];
		menus = new MenuScrollable[numWorlds];
		for (int i=0; i< menus.length; i++){
			menus[i] = new MenuScrollable(canvas, worldToNumLevels[i], i, levelsCompleted);
		}

		menu = new MenuScrollable(canvas, totalNumLevels, 0, levelsCompleted);
		mainMenu = new MainMenu(canvas);

		// Initialize the Platformer Controller
		// TODO
		OrthographicCamera camera = canvas.getCamera();

		controllers = new WorldController[2];
		controllers[LEVEL_CONTROLLER_INDEX] = new LevelController(canvas);
		controllers[LEVEL_CREATOR_INDEX] = new LevelCreator();

		// Constructs a new OrthographicCamera, using the given viewport width and height
		// Height is multiplied by aspect ratio.
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		cam = new OrthographicCamera(30, 30 * (h / w));
		cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
		cam.update();

		Gdx.input.setInputProcessor(loading);
		loading.setScreenListener(this);
		setScreen(loading);

		preferences = Gdx.app.getPreferences("save_data.json");
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

		menu.dispose();
		canvas.dispose();
		mainMenu.dispose();
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
	 */
	public void resize(int width, int height) {
		canvas.resize();
		super.resize(width,height);
	}

	/** Prepares the level JSON in LevelController for the current level plus `num` if `increment`;
	 *  otherwise, prepares for level `num`. */
	static public void prepareLevelJson(int num, boolean increment) {
		LevelController lc = (LevelController) controllers[LEVEL_CONTROLLER_INDEX];
		lc.setLevel(increment ? lc.getLevel() + num : num);
		lc.gatherLevelJson(directory);
	}

	static public Preferences getPreferences() {
		return preferences;
	}

	static public void setPreferences(Preferences prefs) {
		preferences = prefs;
	}

	/** Prepares the level JSON in LevelController for the current level plus `num` if `increment`;
	 *  otherwise, prepares for level `num`. */
	public void prepareLevelJson(WorldController wc, int num, boolean increment, int world) {
		LevelController pc = (LevelController) wc;
		pc.setLevel(increment ? pc.getLevel() + num : num);
		pc.gatherLevelJson(directory);
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
//		LevelController pc = controllers[current];
//		if (pc.isComplete()){
//			levelsCompleted[pc.getLevel()-1] = true;
//		}
//		for (int k=0; k< controllers.length; k++){
//			if (controllers[k].isComplete()){
//				levelsCompleted[k] = true;
//			}
//		}
		if (screen == loading) {
			for(int ii = 0; ii < controllers.length; ii++) {
				directory = loading.getAssets();
				controllers[ii].gatherAssets(directory);
				if (ii == LEVEL_CONTROLLER_INDEX) {
					prepareLevelJson(1, false);
				}
				controllers[ii].setScreenListener(this);
				controllers [ii].setCanvas(canvas);
				controllers[ii].setPlatController(platformController);
			}

			menu.setScreenListener(this);

			mainMenu.setScreenListener(this);
			setScreen(mainMenu);

			loading.dispose();
			loading = null;
		} else if (exitCode==WorldController.EXIT_WORLD_SELECT){
			worldSelectMenu = new WorldSelect(canvas);
			worldSelectMenu.setScreenListener(this);
			setScreen(worldSelectMenu);
		} else if (screen==worldSelectMenu && exitCode!=WorldController.EXIT_MAIN_SCREEN){
//			for (int j=0; j< menus.length; j++){
//				menus[j] = new MenuScrollable(canvas, worldToNumLevels[j], j, levelsCompleted);
//			}
			menus[exitCode].setScreenListener(this);
			setScreen(menus[exitCode]);
		} else if (screen instanceof MenuScrollable){
//			if (exitCode<0){
//				if (exitCode==currentMenu.getLEFT_EXIT_CODE()){
//					currentMenuIndex -= 1;
//				}
//				else if (exitCode==currentMenu.getRIGHT_EXIT_CODE()) {
//					currentMenuIndex += 1;
//				}
//				currentMenu = menuPages[currentMenuIndex];
//				setScreen(currentMenu);
//			}
//			else {
			prepareLevelJson(exitCode+1, false);
			currentIndexController = exitCode+1;
			currentIndexController = exitCode+1;
			controllers[current].reset();
			setScreen(controllers[current]);
		}
		else if (exitCode==WorldController.EXIT_MAIN_SCREEN){
		mainMenu.setScreenListener(this);
		setScreen(mainMenu);
		}
		else if (exitCode == WorldController.EXIT_NEXT) {
			if(current == LEVEL_CONTROLLER_INDEX) {
				prepareLevelJson(1, true);
				controllers[current].reset();
			}
		}
		else if (exitCode == WorldController.EXIT_PREV) {
			if(current == LEVEL_CONTROLLER_INDEX) {
				prepareLevelJson(-1, true);
				controllers[current].reset();
			}
		} else if (exitCode == WorldController.EXIT_SWITCH) {;
			current = (current + 1 ) % controllers.length;
			controllers[current].reset();
			setScreen(controllers[current]);
		} else if (exitCode == WorldController.EXIT_QUIT) {
			preferences.flush(); // Persist user save data
			Gdx.app.exit(); // We quit the main application
		}
	}

}
