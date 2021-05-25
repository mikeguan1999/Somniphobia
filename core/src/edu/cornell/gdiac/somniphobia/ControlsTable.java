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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
public class ControlsTable implements Screen {
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
	private TextureRegionDrawable backgroundDrawable;
	private TextureRegionDrawable arrowDrawable;
	private TextureRegionDrawable backwardArrowDrawable;
	private TextureRegionDrawable column1;
	private TextureRegionDrawable column2;
	private TextureRegionDrawable column3;
	private TextureRegionDrawable column2_blue;
	private TextureRegionDrawable column3_blue;
	private TextureRegionDrawable titleDrawable;
	private Image title;
	private Button arrow;
	private Button backwardArrow;
	private boolean prevClicked;
	private boolean backwardClicked;
	private boolean defaultSelected;
	private boolean alternativeSelected;
	private TextureRegion whiteTable;
	private TextureRegion blueTable;
	private Image col1;
	private Button col2;
	private Button col3;

	public Stage getStage(){
		return stage;
	}

	public ControlsTable(GameCanvas canvas) {
		internal = new AssetDirectory( "controls.json" );
		internal.loadAssets();
		internal.finishLoading();

		stage = new Stage();
		table = new Table();
		backgroundDrawable = new TextureRegionDrawable(internal.getEntry("background", Texture.class));
		arrowDrawable = new TextureRegionDrawable(internal.getEntry("blue_arrow", Texture.class));
		backwardArrowDrawable = new TextureRegionDrawable(internal.getEntry("backward_arrow", Texture.class));
		whiteTable = new TextureRegion(internal.getEntry("table_white", Texture.class));
		blueTable = new TextureRegion(internal.getEntry("table_blue", Texture.class));
		titleDrawable = new TextureRegionDrawable(internal.getEntry("title", Texture.class));

		column1 = new TextureRegionDrawable(new TextureRegion(whiteTable, 0,0, 525, 526));
		column2 = new TextureRegionDrawable(new TextureRegion(whiteTable, 526,0, 525, 526));
		column3 = new TextureRegionDrawable(new TextureRegion(whiteTable, 1051,0, 525, 526));

		column2_blue = new TextureRegionDrawable(new TextureRegion(blueTable, 526,0, 526, 527));
		column3_blue = new TextureRegionDrawable(new TextureRegion(blueTable, 1052,0, 526, 527));

		col1 = new Image(column1);
		col2 = new Button(column2);
		col3 = new Button(column3);
		title = new Image(titleDrawable);

		table.setBackground(backgroundDrawable);
		table.setFillParent(true);
//		arrow = new Button(arrowDrawable);
		backwardArrow = new Button(backwardArrowDrawable);

		int TITLE_OFFSET = 40;
		table.add(title).colspan(3).padBottom(TITLE_OFFSET);
		table.row();
		table.add(col1).size(col1.getWidth()/2, col1.getHeight()/2);
		table.add(col2).size(col2.getWidth()/2, col2.getWidth()/2);
		table.add(col3).size(col3.getWidth()/2, col3.getHeight()/2);
		table.row();
		table.add(backwardArrow).size(backwardArrow.getWidth()/2, backwardArrow.getHeight()/2);

//		arrow.addListener(new ClickListener() {
//			public void clicked(InputEvent event, float x, float y) {
//				prevClicked = true;
//			}
//		});

		backwardArrow.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				backwardClicked = true;
			}
		});

		col2.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				defaultSelected = true;
				InputController.getInstance().setControlScheme(0);
			}
		});

		col3.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				alternativeSelected = true;
				InputController.getInstance().setControlScheme(1);
			}
		});

		stage.addActor(table);
		table.validate();
		int ARROW_OFFSET = 10;
//		arrow.setPosition(ARROW_OFFSET, canvas.getHeight()- arrow.getHeight()-ARROW_OFFSET/2);
		backwardArrow.setPosition(ARROW_OFFSET, canvas.getHeight()/2- backwardArrow.getHeight()/2);

		this.canvas  = canvas;
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

			if (backwardClicked){
				backwardClicked = false;
				listener.exitScreen(this, WorldController.EXIT_CONTROLS);
			}

			if (col2.isOver()){
				col2.getStyle().up = column2_blue;
			}
			else {
				col2.getStyle().up = column2;
			}

			if (col3.isOver()){
				col3.getStyle().up = column3_blue;
			}
			else {
				col3.getStyle().up = column3;
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