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
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.somniphobia.game.controllers.LevelController;
import edu.cornell.gdiac.somniphobia.game.controllers.LevelCreator;
import edu.cornell.gdiac.somniphobia.game.controllers.PlatformController;
import edu.cornell.gdiac.util.*;
import edu.cornell.gdiac.assets.*;
import com.badlogic.gdx.graphics.OrthographicCamera;

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

	/** World selection screen variables */
	static public int totalNumWorlds = 5;

	/** Level selection screen variables */
	static public int totalNumLevels = 36;

	private OrthographicCamera cam;
	private MainMenu mainMenu;

	static private final int LEVEL_CONTROLLER_INDEX = 0;
	static private final int LEVEL_CREATOR_INDEX = 1;

	static private WorldSelect worldSelectMenu;
	static private MenuScrollable [] menus;
	static private String[][] levels;
//	in the sequence of first row then second row of buttons in the world selector
	private int [] worldToNumLevels = {5, 7, 8, 2, 4};
	private boolean [] levelsCompleted;
	private int currentIndexController;
	private Stage pauseMenuStage;
	private Stage pauseButtonStage;
	private Controls controlsPage;
	private About aboutPage;


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

		menus = new MenuScrollable[totalNumWorlds];
		levels = new String[totalNumWorlds][];

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
		controlsPage = new Controls(canvas);
		aboutPage = new About(canvas);
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
	static public boolean prepareLevelJson(int num, boolean increment) {
		LevelController lc = (LevelController) controllers[LEVEL_CONTROLLER_INDEX];
		int newLevel = increment ? lc.getLevel() + num : num;
		if(newLevel <= 0 || newLevel > levels[worldSelectMenu.currentWorld].length) {
			return false;
		}
		lc.setLevel(newLevel);
		lc.gatherLevelJson(newLevel == 0 ? "playLevel" : levels[worldSelectMenu.currentWorld][newLevel-1]);
		return true;
	}

	static public Preferences getPreferences() {
		return preferences;
	}

	static public void setPreferences(Preferences prefs) {
		preferences = prefs;
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
			directory = loading.getAssets();
//			System.out.println("hi");
//			System.out.println(directory.getEntry("somniTrack"));
			directory.unload("audio/SomniTrack.mp3");
			directory.load("audio/SomniTrack.mp3", Music.class);
			directory.unload("audio/PhobiaTrack.mp3");
			directory.load("audio/PhobiaTrack.mp3", Music.class);
			directory.unload("audio/CombinedTrack.mp3");
			directory.load("audio/CombinedTrack.mp3", Music.class);
//			directory.load("phobiaTrack", Music.class);
//			directory.load("combinedTrack", Music.class);
			directory.finishLoading();
			for (int ii = 0; ii < controllers.length; ii++) {
				controllers[ii].gatherAssets(directory);
				//if (ii == LEVEL_CONTROLLER_INDEX) {
				//	prepareLevelJson(1, false);
				//}
				controllers[ii].setScreenListener(this);
				controllers[ii].setCanvas(canvas);
				controllers[ii].setPlatController(platformController);
			}

			//load music


			mainMenu.setScreenListener(this);
			setScreen(mainMenu);

			// Set up World Select menu
			JsonValue worlds = directory.getEntry("worlds", JsonValue.class);
			for(int i = 1; i <= menus.length; i++) {
				JsonValue world = worlds.get("world" + i);
				String[] levels = world.get("levels").asStringArray();
				menus[i-1] = new MenuScrollable(canvas, levels.length);
				this.levels[i-1] = levels;
				TextureRegion background = new TextureRegion(directory.getEntry(
						world.get("worldMenuBackground").asString(), Texture.class ));
				menus[i-1].setBackground(background);
				TextureRegionDrawable door = new TextureRegionDrawable(directory.getEntry(
						world.get("worldMenuDoor").asString(), Texture.class ));
				menus[i-1].setDoorImages(door);
			}

			loading.dispose();
			loading = null;

		} else if (exitCode==WorldController.EXIT_MAIN_MENU_ENTER) {
			mainMenu.setScreenListener(this);
			setScreen(mainMenu);
		} else if (exitCode==WorldController.EXIT_WORLD_SELECT_ENTER){
			worldSelectMenu = new WorldSelect(canvas);
			worldSelectMenu.setScreenListener(this);
			setScreen(worldSelectMenu);
		} else if(exitCode==WorldController.EXIT_LEVEL_SELECT_ENTER) {
			menus[worldSelectMenu.currentWorld].setScreenListener(this);
			setScreen(menus[worldSelectMenu.currentWorld]);
		} else if(exitCode==WorldController.EXIT_NEW_LEVEL) {
			if(prepareLevelJson(menus[worldSelectMenu.currentWorld].currentLevel, false)) {
				controllers[current].reset();
				setScreen(controllers[current]);
			}
		} else if (exitCode == WorldController.EXIT_NEXT) {
			if(current == LEVEL_CONTROLLER_INDEX) {
				if(prepareLevelJson(1, true)) {
					controllers[current].reset();
				} else {
					LevelController lc = (LevelController) controllers[current];
					lc.setGameScreenActive(false);
					menus[worldSelectMenu.currentWorld].setScreenListener(this);
					setScreen(menus[worldSelectMenu.currentWorld]);
				}
			}
		} else if (exitCode == WorldController.EXIT_PREV) {
			if(current == LEVEL_CONTROLLER_INDEX) {
				if(prepareLevelJson(-1, true)) {
					controllers[current].reset();
				}
			}
		} else if (exitCode == WorldController.EXIT_SWITCH) {;
			current = (current + 1 ) % controllers.length;
			controllers[current].reset();
			setScreen(controllers[current]);
		} else if (exitCode==WorldController.EXIT_CONTROLS){
			controlsPage.setScreenListener(this);
			setScreen(controlsPage);
		}
		else if (exitCode==WorldController.EXIT_ABOUT){
			aboutPage.setScreenListener(this);
			setScreen(aboutPage);
		}
		else if (exitCode == WorldController.EXIT_QUIT) {
			preferences.flush(); // Persist user save data
			Gdx.app.exit(); // We quit the main application
		}
	}

}
