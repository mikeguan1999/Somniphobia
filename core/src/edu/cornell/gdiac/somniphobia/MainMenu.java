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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
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
public class MainMenu implements Screen {
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
	private Button startDream;
	private Button about;
	private Button controls;
	private Image underline;
	private Button exit;
	private boolean started;
	private boolean exitClicked;
	private boolean controlsClicked;
	private boolean aboutClicked;

	private TextureRegionDrawable titleDrawable;
	private TextureRegionDrawable aboutDrawable;
	private TextureRegionDrawable controlsDrawable;
	private TextureRegionDrawable startDreamDrawable;
	private TextureRegionDrawable underlineDrawable;
	private TextureRegionDrawable backgroundDrawable;
	private TextureRegionDrawable exitDrawable;
	private TextureRegionDrawable underlineOrangeDrawable;

	public Stage getStage(){
		return stage;
	}

	public MainMenu(GameCanvas canvas) {
		internal = new AssetDirectory( "main_screen.json" );
		internal.loadAssets();
		internal.finishLoading();

		stage = new Stage();
		table = new Table();
		backgroundDrawable = new TextureRegionDrawable(internal.getEntry("background", Texture.class));
		titleDrawable = new TextureRegionDrawable(internal.getEntry("title", Texture.class));
		aboutDrawable = new TextureRegionDrawable(internal.getEntry("about", Texture.class));
		controlsDrawable = new TextureRegionDrawable(internal.getEntry("controls", Texture.class));
		startDreamDrawable = new TextureRegionDrawable(internal.getEntry("start_dream", Texture.class));
		underlineDrawable = new TextureRegionDrawable(internal.getEntry("underline", Texture.class));
		exitDrawable = new TextureRegionDrawable(internal.getEntry("exit", Texture.class));
		underlineOrangeDrawable = new TextureRegionDrawable(internal.getEntry("orange_underline", Texture.class));

		table.setBackground(backgroundDrawable);
		table.setFillParent(true);

		titleImage = new Image(titleDrawable);
		about = new Button(aboutDrawable);
		controls = new Button(controlsDrawable);
		startDream = new Button(startDreamDrawable);
		underline = new Image(underlineDrawable);
		exit = new Button(exitDrawable);
    
		table.add(titleImage).colspan(4).height(100).padTop(50).padLeft(150).padRight(150).width(500);
		table.row().padBottom(300);
		table.add(startDream).padTop(30).size(startDream.getWidth()/2, startDream.getHeight()/2).expand().fillX();
		table.add(controls).padTop(30).size(controls.getWidth()/2, controls.getHeight()/2).expand().fillX();
		table.add(about).padTop(30).size(about.getWidth()/2, about.getHeight()/2).expand().fillX();
		table.add(exit).padTop(30).size(exit.getWidth()/2, exit.getHeight()/2).expand().fillX();
		table.row();
		table.add(underline);
//		underline.setVisible(false);

		startDream.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				started = true;
			}
		});

		exit.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				exitClicked = true;
			}
		});

		controls.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				controlsClicked = true;
			}
		});

		about.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				aboutClicked = true;
			}
		});

		stage.addActor(table);
		table.validate();
		startDream.setX(50);
		controls.setX(startDream.getX()+startDream.getWidth()+30);
		about.setX(canvas.getWidth()/2+50);
		exit.setX(about.getWidth()+about.getX()+100);
		this.canvas  = canvas;
		// Compute the dimensions from the canvas
		resize(canvas.getWidth(),canvas.getHeight());
	}

	/**
	 * Creating an image button that appears as an image with upFilepath.
	 */
	private Button createImageButton(String upFilepath){
		TextureRegionDrawable buttonDrawable = new TextureRegionDrawable(new Texture(Gdx.files.internal(upFilepath)));
		Button imgButton= new Button(buttonDrawable);
		return imgButton;
	}

	private Image createImage(String upFilepath){
		TextureRegionDrawable drawable = new TextureRegionDrawable(new Texture(Gdx.files.internal(upFilepath)));
		Image image = new Image(drawable);
		return image;
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

			if (startDream.isOver()){
				underline.setSize(startDream.getWidth()+10, startDream.getHeight());
				underline.setPosition(startDream.getX()-5, startDream.getY()-20);
				underline.setDrawable(underlineDrawable);
				underline.setVisible(true);
			}

			else if (controls.isOver()){
				underline.setSize(controls.getWidth()+10, controls.getHeight());
				underline.setPosition(controls.getX()-5, controls.getY()-20);
				underline.setDrawable(underlineDrawable);
				underline.setVisible(true);
			}

			else if (about.isOver()){
				underline.setSize(about.getWidth()+10, about.getHeight());
				underline.setPosition(about.getX()-5, about.getY()-20);
				underline.setDrawable(underlineOrangeDrawable);
				underline.setVisible(true);
			}
			else if (exit.isOver()){
				underline.setSize(exit.getWidth()+10, exit.getHeight());
				underline.setPosition(exit.getX()-5, exit.getY()-20);
				underline.setDrawable(underlineOrangeDrawable);
				underline.setVisible(true);
			}

			else{
				underline.setZIndex(0);
				underline.setVisible(false);

			}

			if (started){
				started = false;
				listener.exitScreen(this, WorldController.EXIT_WORLD_SELECT_ENTER);
			}

			if (exitClicked){
				exitClicked = false;
				listener.exitScreen(this, WorldController.EXIT_QUIT);
			}

			if (controlsClicked){
				controlsClicked = false;
				listener.exitScreen(this, WorldController.EXIT_CONTROLS);
			}

			if (aboutClicked){
				aboutClicked = false;
				listener.exitScreen(this, WorldController.EXIT_ABOUT);
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