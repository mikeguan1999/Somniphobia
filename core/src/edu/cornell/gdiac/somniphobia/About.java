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
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
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
public class About implements Screen {
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
	private TextureRegion backgroundDrawable;
	private TextureRegionDrawable arrowDrawable;
	private Button arrow;
	private boolean prevClicked;
	private OrthographicCamera camera;
	private float initialCameraY;

	private Button downArrow;
	private Button upArrow;
	private TextureRegionDrawable downArrowDrawable;
	private TextureRegionDrawable upArrowDrawable;


	public Stage getStage(){
		return stage;
	}

	public About(GameCanvas canvas) {
		internal = new AssetDirectory( "controls.json" );
		internal.loadAssets();
		internal.finishLoading();

		table = new Table();
		camera = new OrthographicCamera(canvas.getWidth(), canvas.getHeight());
		stage = new Stage(new ScreenViewport(camera));
		backgroundDrawable = new TextureRegion(internal.getEntry("about_background", Texture.class));
		arrowDrawable = new TextureRegionDrawable(internal.getEntry("blue_arrow", Texture.class));
		downArrowDrawable = new TextureRegionDrawable(internal.getEntry("down_arrow", Texture.class));
		upArrowDrawable = new TextureRegionDrawable(internal.getEntry("up_arrow", Texture.class));

		table.setFillParent(true);
		arrow = new Button(arrowDrawable);
		upArrow = new Button(upArrowDrawable);
		downArrow = new Button(downArrowDrawable);

		table.add(arrow).size(arrow.getWidth()/2, arrow.getHeight()/2);
//		underline.setVisible(false);
		arrow.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				prevClicked = true;
			}
		});
		table.row();
		table.add(upArrow).size(upArrow.getWidth()/3, upArrow.getHeight()/3);
		table.row();
		table.add(downArrow).size(downArrow.getWidth()/3, downArrow.getHeight()/3);;
		stage.addActor(table);
		table.validate();
		int ARROW_OFFSET = 10;
//		arrow.setPosition(arrow.getX()-canvas.getWidth()/2+arrow.getWidth()/2+ARROW_OFFSET, arrow.getY()+ canvas.getHeight()/2- arrow.getHeight()/2-ARROW_OFFSET);

		this.canvas  = canvas;
		// Compute the dimensions from the canvas
		resize(canvas.getWidth(),canvas.getHeight());

		initialCameraY = backgroundDrawable.getRegionHeight()/4- canvas.getHeight()/2;
		camera.position.y = initialCameraY;
		int CAMERA_OFFSET = 15;
		camera.position.x = camera.position.x + CAMERA_OFFSET;
		camera.update();
		int UPARROW_OFFSET = 30;
		arrow.setPosition(camera.position.x-canvas.getWidth()/2+ARROW_OFFSET, camera.position.y+canvas.getHeight()/2- arrow.getHeight()-ARROW_OFFSET);
		upArrow.setPosition(camera.position.x-upArrow.getWidth()/2, camera.position.y+ canvas.getHeight()/4);
		downArrow.setPosition(camera.position.x-downArrow.getWidth()/2, camera.position.y- canvas.getHeight()/2);
		upArrow.setVisible(false);
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
		stage.getBatch().begin();
		stage.getBatch().draw(backgroundDrawable, (canvas.getWidth()- backgroundDrawable.getRegionWidth()/4)/2, 0, backgroundDrawable.getRegionWidth()/4, backgroundDrawable.getRegionHeight()/4);
		stage.getBatch().end();
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
			draw();
			stage.draw();

			if (upArrow.isOver()&&camera.position.y<=initialCameraY){
				camera.position.y += 7;
				downArrow.setVisible(true);
				arrow.setPosition(arrow.getX(), arrow.getY()+7);
				downArrow.setPosition(downArrow.getX(), downArrow.getY()+7);
				upArrow.setPosition(upArrow.getX(), upArrow.getY()+7);
				if (camera.position.y==initialCameraY){
					upArrow.setVisible(false);
				}
			}

			else if (downArrow.isOver() && camera.position.y>=canvas.getHeight()/2){
				camera.position.y -= 7;
				arrow.setPosition(arrow.getX(), arrow.getY()-7);
				downArrow.setPosition(downArrow.getX(), downArrow.getY()-7);
				upArrow.setVisible(true);
				upArrow.setPosition(upArrow.getX(), upArrow.getY()-7);
				if (camera.position.y==canvas.getHeight()/2){
					downArrow.setVisible(false);
				}
			}

			if (prevClicked){
				prevClicked = false;
				listener.exitScreen(this, WorldController.EXIT_MAIN_MENU_ENTER);
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