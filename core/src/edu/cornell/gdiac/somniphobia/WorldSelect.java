/*
 * LoadingMode.java
 *
 * Asset loading is a really tricky problem.  If you have a lot of sound or images,
 * it can take a long time to decompress them and load them into memory.  If you just
 * have code at the start to load all your assets, your game will look like it is hung
 * at the start.
 *
 * The alternative is asynchronous asset loading.  In asynchronous loading, you load a
 * little bit of the assets at a time, but still animate the game while you are loading.
 * This way the player knows the game is not hung, even though he or she cannot do
 * anything until loading is complete. You know those loading screens with the inane tips
 * that want to be helpful?  That is asynchronous loading.
 *
 * This player mode provides a basic loading screen.  While you could adapt it for
 * between level loading, it is currently designed for loading all assets at the
 * start of the game.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * Updated asset version, 2/6/2021
 */
package edu.cornell.gdiac.somniphobia;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.util.ScreenListener;

/**
 * Class that provides a loading screen for the state of the game.
 *
 * You still DO NOT need to understand this class for this lab.  We will talk about this
 * class much later in the course.  This class provides a basic template for a loading
 * screen to be used at the start of the game or between levels.  Feel free to adopt
 * this to your needs.
 *
 * You will note that this mode has some textures that are not loaded by the AssetManager.
 * You are never required to load through the AssetManager.  But doing this will block
 * the application.  That is why we try to have as few resources as possible for this
 * loading screen.
 */
public class WorldSelect implements Screen {
	/** Reference to GameCanvas created by the root */
	private GameCanvas canvas;
	/** Listener that will update the player mode when we are done */
	private ScreenListener listener;
	/** Internal assets for this menu screen */
	private AssetDirectory internal;
	/** Whether or not this player mode is still active */
	private boolean active;
	private Stage stage;
	/** Reference of the table of this screen */
	private Table table;
	private Image titleImage;
	private Button world1;
	private Button world2;
	private Button world3;
	private Button world4;
	private Button world5;
	private Button arrow;
	private Button [] worlds;
	private final int numWorlds = 5;
	public int currentWorld;
	private boolean[] worldsClicked;
	private TextureRegionDrawable upImage;
	private TextureRegionDrawable [] overImages = new TextureRegionDrawable[numWorlds];


	private TextureRegionDrawable titleDrawable;
	private TextureRegionDrawable backgroundDrawable;
	private TextureRegionDrawable world1Drawable;
	private TextureRegionDrawable world2Drawable;
	private TextureRegionDrawable world3Drawable;
	private TextureRegionDrawable world4Drawable;
	private TextureRegionDrawable blueArrowDrawable;

	private boolean prevClicked;

	public Stage getStage(){
		return stage;
	}

	public WorldSelect(GameCanvas canvas) {
		internal = new AssetDirectory( "world_select.json" );
		internal.loadAssets();
		internal.finishLoading();

		stage = new Stage();
		table = new Table();
		backgroundDrawable = new TextureRegionDrawable(internal.getEntry("background", Texture.class));
		titleDrawable = new TextureRegionDrawable(internal.getEntry("title", Texture.class));
//		world1Drawable = new TextureRegionDrawable(internal.getEntry("world1", Texture.class));
//		world2Drawable = new TextureRegionDrawable(internal.getEntry("world2", Texture.class));
//		world3Drawable = new TextureRegionDrawable(internal.getEntry("world3", Texture.class));
//		world4Drawable = new TextureRegionDrawable(internal.getEntry("world4", Texture.class));
		blueArrowDrawable = new TextureRegionDrawable(internal.getEntry("blue_arrow", Texture.class));

		table.setBackground(backgroundDrawable);
		table.setFillParent(true);

		worldsClicked = new boolean[numWorlds];
		worlds = new Button[numWorlds];
		for(currentWorld =0; currentWorld <worlds.length; currentWorld++){
//			worlds[i] = new Button(new TextureRegionDrawable(internal.getEntry("world"+(i+1), Texture.class)));
			upImage = new TextureRegionDrawable(internal.getEntry("button", Texture.class));
			overImages[currentWorld] = new TextureRegionDrawable(internal.getEntry("phobia"+(currentWorld +1), Texture.class));
			worlds[currentWorld] = new Button(upImage);
			worlds[currentWorld].getStyle().over = overImages[currentWorld];
			worlds[currentWorld].addListener(new ClickListener() {
				int saved_i = currentWorld;
				public void clicked(InputEvent event, float x, float y) {
					worldsClicked[saved_i] = true;
				}
			});
		}

		titleImage = new Image(titleDrawable);
//		world1 = new Button(world1Drawable);
//		world2 = new Button(world2Drawable);
//		world3 = new Button(world3Drawable);
//		world4 = new Button(world4Drawable);
//		world5 = new Button(world4Drawable);
		arrow = new Button(blueArrowDrawable);

		table.add(arrow).size(arrow.getWidth()/2, arrow.getHeight()/2).left().top();
		table.row();
		table.add(titleImage).size(titleImage.getWidth()/2, titleImage.getHeight()/2).colspan(4);
		table.row();
		for(currentWorld =0; currentWorld <worlds.length; currentWorld++){
			if (currentWorld ==0){
				table.add(worlds[currentWorld]).size(worlds[currentWorld].getWidth()/2, worlds[currentWorld].getHeight()/2).space(50).padLeft(50);
			}
			else if (currentWorld ==4){
				table.add(worlds[currentWorld]).size(worlds[currentWorld].getWidth()/2, worlds[currentWorld].getHeight()/2).space(50).padBottom(100);
			}
			else if (currentWorld ==3){
				table.add(worlds[currentWorld]).size(worlds[currentWorld].getWidth()/2, worlds[currentWorld].getHeight()/2).space(50).padLeft(50).padBottom(100);
			}
			else {
				table.add(worlds[currentWorld]).size(worlds[currentWorld].getWidth() / 2, worlds[currentWorld].getHeight() / 2).space(50);
			}
			if (currentWorld ==2){
				table.row();
			}
		}

//		second row bottom offset
		arrow.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				prevClicked = true;
			}
		});

		stage.addActor(table);
		table.validate();
		arrow.setPosition(arrow.getX()-30, arrow.getY()-30);
		worlds[3].setPosition(worlds[3].getX()+150, worlds[3].getY());
		worlds[4].setPosition(worlds[4].getX()+150, worlds[4].getY());


		this.canvas = canvas;
		// Compute the dimensions from the canvas
		resize(canvas.getWidth(),canvas.getHeight());
	}


	/**
	 * Called when this screen should release all resources.
	 */
	public void dispose() {
		internal.unloadAssets();
		internal.dispose();
	}

	/**
	 * Update the status of this player mode.
	 *
	 * We prefer to separate update and draw from one another as separate methods, instead
	 * of using the single render() method that LibGDX does.  We will talk about why we
	 * prefer this in lecture.
	 *
	 * @param delta Number of seconds since last animation frame
	 */
	private void update(float delta) {
	}

	/**
	 * Draw the status of this player mode.
	 *
	 * We prefer to separate update and draw from one another as separate methods, instead
	 * of using the single render() method that LibGDX does.  We will talk about why we
	 * prefer this in lecture.
	 */
	private void draw() {
		canvas.begin();
		canvas.end();
	}
	// ADDITIONAL SCREEN METHODS
	/**
	 * Called when the Screen should render itself.
	 *
	 * We defer to the other methods update() and draw().  However, it is VERY important
	 * that we only quit AFTER a draw.
	 *
	 * @param delta Number of seconds since last animation frame
	 */
	public void render(float delta) {
		if (active) {
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			update(delta);
			stage.act(delta);
			stage.draw();

			if (prevClicked){
				listener.exitScreen(this, WorldController.EXIT_MAIN_MENU_ENTER);
				prevClicked = false;
			}

			for (int i=0; i<worlds.length; i++){
//				if (worlds[i].isOver()){
//					worlds[i].getStyle().up =
//				}
				if (worldsClicked[i]==true){
					currentWorld = i;
					listener.exitScreen(this, WorldController.EXIT_LEVEL_SELECT_ENTER);
				}
			}
		}
	}

	/**
	 * Called when the Screen is resized.
	 *
	 * This can happen at any point during a non-paused state but will never happen
	 * before a call to show().
	 *
	 * @param width  The new width in pixels
	 * @param height The new height in pixels
	 */
	public void resize(int width, int height) {
		// TODO
	}

	/**
	 * Called when the Screen is paused.
	 *
	 * This is usually when it's not active or visible on screen. An Application is
	 * also paused before it is destroyed.
	 */
	public void pause() {
		// TODO Auto-generated method stub

	}

	/**
	 * Called when the Screen is resumed from a paused state.
	 *
	 * This is usually when it regains focus.
	 */
	public void resume() {
		// TODO Auto-generated method stub

	}

	/**
	 * Called when this screen becomes the current screen for a Game.
	 */
	public void show() {
		// Useless if called in outside animation loop
		Gdx.input.setInputProcessor(stage);

		table = new Table();
		table.setFillParent(true);
		stage.addActor(table);

//		table.setDebug(true);
		active = true;
	}

	/**
	 * Called when this screen is no longer the current screen for a Game.
	 */
	public void hide() {
		// Useless if called in outside animation loop
		active = false;
	}

	/**
	 * Sets the ScreenListener for this mode
	 *
	 * The ScreenListener will respond to requests to quit.
	 */
	public void setScreenListener(ScreenListener listener) {
		this.listener = listener;
	}

	/**
	 * Resets the status of the game so that we can play again.
	 *
	 * This method disposes of the world and creates a new one.
	 */
//	public void reset();


}