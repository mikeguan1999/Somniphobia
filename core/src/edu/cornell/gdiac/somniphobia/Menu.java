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

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import edu.cornell.gdiac.util.*;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import org.lwjgl.Sys;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

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
public class Menu implements Screen {
	/** Reference to GameCanvas created by the root */
	private GameCanvas canvas;
	/** Listener that will update the player mode when we are done */
	private ScreenListener listener;

	/** Whether or not this player mode is still active */
	private boolean active;
	/** Whether this is the initial(first) iteration */
	private boolean first=true;
	/** The value of initial Y position of the button group */
	private float initialButtonY;
	/** Reference of the stage of this screen */
	private Stage stage;
	/** Reference of the table of this screen */
	private Table table;
//	private Skin skin;
	/** The buttons in one row */
	private Button[] buttons;
	/** The global variable for access inside an inner class during an iteration */
	private int i;
	/** Number of levels we have in one row */
	private int numLevels=4;
	/** Whether each individual button is clicked */
	private boolean[] buttonsClicked = new boolean[numLevels];
	/** The initial X positions of the buttons */
	private Float[] positionsX;
	/** Reference to the actor of cloudline */
	private Actor cloudlineActor;
	/** Constants for loading, positioning, and resizing images*/
	private final float SIDE_PADDING = 150;
	private final float TOP_PADDING = 50;
	private final float DOOR_WIDTH = 120;
	private final float DOOR_HEIGHT = 200;
	private final float CLOUD_WIDTH = 250;
	private final float CLOUD_HEIGHT = 250;
	private final float CLOUDLINE_HEIGHT = 200;
	private final float CLOUDLINE_WIDTH = 800;
	private final float TITLE_HEIGHT = 70;
	private final float TITLE_WIDTH = 450;
	/** Setting the Y position of the cloudline to overlap with the doors*/
	private final float CLOUDLINE_YPOSITION = 100;
	private final float CLOUD_OFFSETX = 50;
	private final float CLOUD_OFFSETY = 10;
	private boolean toRight = false;
	private boolean toLeft = false;
	private Button leftButton;
	private Button rightButton;
	private int LEFT_EXIT_CODE = -1;
	private int RIGHT_EXIT_CODE = -2;
	private boolean leftExist;
	private boolean rightExist;

	private TextureRegionDrawable[] upImages = new TextureRegionDrawable[numLevels];
	private TextureRegionDrawable[] overImages = new TextureRegionDrawable[numLevels];

	public Menu(GameCanvas canvas, boolean left, boolean right) {
		stage = new Stage();
		table = new Table();
		table.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("menu\\background_blue.png"))));
		table.setFillParent(true);
		leftExist = left;
		rightExist = right;

//		Testing with default style buttons
//		skin = new Skin(Gdx.files.internal("uiskin.json"));
//		TextButton.TextButtonStyle buttonStyle = skin.get("bigButton", TextButton.TextButtonStyle.class);

//		TextButton button1 = new TextButton("Level 1", skin);
//		TextButton button2 = new TextButton("Level 2", skin);
//		TextButton button3 = new TextButton("Level 3", skin);
//		TextButton button4 = new TextButton("Level 4", skin);

		TextureRegionDrawable titleDrawable = new TextureRegionDrawable(new Texture(Gdx.files.internal("menu\\Dream Selection.png")));
		Image titleImage = new Image(titleDrawable);
		table.add(titleImage).colspan(numLevels+2).expandX().height(TITLE_HEIGHT).width(TITLE_WIDTH).padTop(TOP_PADDING);
		table.row();

//		Adding images with stack to put them on top of each other
//		TextureRegionDrawable forestDoorDrawable = new TextureRegionDrawable(new Texture(Gdx.files.internal("menu\\forest door.png")));
//		forestImage = new Button(forestDoorDrawable);
//		TextureRegionDrawable numberDrawable1 = new TextureRegionDrawable(new Texture(Gdx.files.internal("menu\\level1cloud.png")));
//		forestImageButton= new Button(numberDrawable1);
//		color  = forestImageButton.getColor();
//		imgButton1 = new Button(forestDoorDrawable);
//
//		Stack stack = new Stack();
//		stack.add(forestImage);
//		stack.add(forestImageButton);
//
//		table.add(stack).width(DOOR_WIDTH).height(DOOR_HEIGHT);


		buttons = new Button[numLevels];
		for (i=0; i<numLevels; i++) {
			buttons[i] = createImageButton("menu\\door"+(i+1)+".png");
			buttons[i].addListener(new ClickListener() {
				int saved_i = i;
				public void clicked(InputEvent event, float x, float y) {
					buttonsClicked[saved_i] = true;
				}
			});
			upImages[i] = new TextureRegionDrawable(new Texture(Gdx.files.internal("menu\\door"+(i+1)+".png")));
			overImages[i] = new TextureRegionDrawable(new Texture(Gdx.files.internal("menu\\level"+(i+1)+"cloud.png")));
		}

		leftButton = createImageButton("menu\\left_arrow.png");
		rightButton = createImageButton("menu\\right_arrow.png");
		table.add(leftButton).size(50,50);

		leftButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				toLeft = true;
			}
		});

//		System.out.println(canvas.getWidth());

		positionsX = new Float[numLevels];
		for (int i=0; i<numLevels; i++) {
			if (i==0){
				table.add(buttons[i]).padLeft(SIDE_PADDING).padTop(TOP_PADDING).size(DOOR_WIDTH, DOOR_HEIGHT).expandX();
			}
			else if (i==3){
				table.add(buttons[i]).padRight(SIDE_PADDING).padTop(TOP_PADDING).size(DOOR_WIDTH, DOOR_HEIGHT).expandX();
			}
			else {
				table.add(buttons[i]).padTop(TOP_PADDING).size(DOOR_WIDTH, DOOR_HEIGHT).expandX();
			}
		}

		rightButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				toRight = true;
			}
		});

		table.add(rightButton).size(50,50);

		TextureRegionDrawable drawable = new TextureRegionDrawable(new Texture(Gdx.files.internal("menu\\cloudline_smaller.png")));
		Image cloudLineImage = new Image(drawable);
		table.row();
		table.add(cloudLineImage).colspan(numLevels+2).height(CLOUDLINE_HEIGHT).width(CLOUDLINE_WIDTH);
		cloudlineActor = (Actor) cloudLineImage;

		for (int j=0; j<numLevels; j++){
			Actor buttonActor = (Actor) buttons[j];
			positionsX[j] = buttonActor.getX();
		}

		stage.addActor(table);
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

	public int getLEFT_EXIT_CODE(){
		return LEFT_EXIT_CODE;
	}

	public int getRIGHT_EXIT_CODE(){
		return RIGHT_EXIT_CODE;
	}
	/**
	 * Called when this screen should release all resources.
	 */
	public void dispose() {
	}
//	public Boolean Over(){
//		return forestImageButton.isOver() && !prevHovered;
//	}

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
//		isHovered = Over();
//		prevHovered = isHovered;
//		if (Over()){
//			forestImage.setColor(0,0,0,0);
//			forestImageButton.setColor(color);
//			forestImageButton.setVisible(true);
//		}
//		else{
//			forestImage.setVisible(true);
//			forestImageButton.setVisible(false);
//		}
		for (i=0; i<numLevels; i++) {
			boolean checked = false;
			if (buttons[i].isOver()){
				buttons[i].getStyle().up = overImages[i];
				buttons[i].setSize(CLOUD_WIDTH,CLOUD_HEIGHT);
				Actor actor = (Actor) buttons[i];
				actor.setX(positionsX[i]-CLOUD_OFFSETX);
				actor.setY(initialButtonY-CLOUD_OFFSETY);
			}
			else{
				buttons[i].getStyle().up = upImages[i];
				buttons[i].setSize(DOOR_WIDTH, DOOR_HEIGHT);
				Actor actor = (Actor) buttons[i];
				actor.setX(positionsX[i]);
				actor.setY(initialButtonY);
			}
		}
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
			if (!leftExist){
				leftButton.setVisible(false);
			}
			if (!rightExist){
				rightButton.setVisible(false);
			}
			cloudlineActor.setY(CLOUDLINE_YPOSITION);
			leftButton.setX(50);
			rightButton.setX(canvas.getWidth()-100);

			if (first) {
				for (int j = 0; j < numLevels; j++) {
					Actor actor = (Actor) buttons[j];
					positionsX[j] = actor.getX();
					initialButtonY = actor.getY();
				}
				first = false;
			}

		}
		for (int i=0; i<4; i++){
			if (buttonsClicked[i]==true){
				buttonsClicked = new boolean[numLevels];
				listener.exitScreen(this, i);
			}
		}
		if (toRight){
			toRight = false;
			listener.exitScreen(this, RIGHT_EXIT_CODE);
		}
		if (toLeft){
			toLeft = false;
			listener.exitScreen(this, LEFT_EXIT_CODE);
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

		table.setDebug(true);
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

