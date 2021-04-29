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
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import edu.cornell.gdiac.util.ScreenListener;
import org.w3c.dom.Text;

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
public class MenuScrollable implements Screen {
	/** Reference to GameCanvas created by the root */
	private GameCanvas canvas;
	/** Listener that will update the player mode when we are done */
	private ScreenListener listener;

	private Stage stage;
	private Table table;
	/** All buttons*/
	private Button[] buttons;
	private int totalActualLevels;
	private int totalNumLevels;
	private int numLevels = 4;
	private final int FONT_SIZE = 50;
	/** Setting the font color to the rgb values of black & visible, ie a=1*/
	private final Color FONT_COLOR = new Color(0,0,0,1);
	/** Setting the font color to the rgb values of black & invisible, ie a=0*/
	private final Color FONT_COLOR_TRANSPARENT = new Color(0,0,0,0);
	private BitmapFont font;
	/** The global variable for access inside an inner class during an iteration */
	private int i;
	/** Whether each individual button is clicked */
	private boolean[] buttonsClicked;
	private float[] positionsX;
	private Button leftButton;
	private Button rightButton;
	/** Reference to the actor of cloudline */
	private Actor cloudlineActor;
	/** Height and width of the left and right arrows*/
	private final float ARROW_SIZE = 50;
	private final float LEFT_BUTTON_POSITION = 50;
	private final float RIGHT_BUTTON_POSITION = 100;
	private boolean toRight = false;
	private boolean toLeft = false;
	/** Setting the Y position of the cloudline to overlap with the doors*/
	private final float CLOUDLINE_YPOSITION = 80;
	private final float CLOUD_OFFSETX = 80;
	private final float CLOUD_OFFSETY = 30;

	/** Constants for loading, positioning, and resizing images*/
	private final float TITLE_HEIGHT = 70;
	private final float TITLE_WIDTH = 450;
	private final float DOOR_WIDTH = 100;
	private final float DOOR_HEIGHT = 200;
	private final float TOP_PADDING = 50;
	private final float SIDE_PADDING = 150;
	private final float CLOUD_WIDTH = 250;
	private final float CLOUD_HEIGHT = 280;
	private final float CLOUDLINE_HEIGHT = 200;
	private final float CLOUDLINE_WIDTH = 800;
	/** The value of initial Y position of the button group */
	private float initialButtonY=203;
	private float initialCameraX;

	private OrthographicCamera camera;

	/** Whether or not this player mode is still active */
	private boolean active;
	/** Whether this is the initial(first) iteration */
	private boolean first=true;

	private TextureRegionDrawable[] upImages = new TextureRegionDrawable[numLevels];
	private TextureRegionDrawable[] overImages = new TextureRegionDrawable[numLevels];
	private TextureRegion background = new TextureRegion(new Texture("menu\\selection_background1.png"));
	private TextureRegionDrawable titleDrawable;
	private Texture titleTexture;
	private int[] zIndices;

	public Stage getStage(){
		return stage;
	}

	public MenuScrollable(GameCanvas canvas, int totalLevels) {
		this.canvas = canvas;
		int numPages = totalNumLevels/numLevels;
		if (totalNumLevels%numLevels != 0){
			numPages += 1;
		}

		camera = new OrthographicCamera(canvas.getWidth(), canvas.getHeight());
//		camera.translate(0, camera.viewportHeight / 2, 0);
		stage = new Stage(new ScreenViewport(camera));
		totalActualLevels = totalLevels;
		totalNumLevels = (int) Math.ceil((double)totalActualLevels/(double)6) * 6;
		buttonsClicked = new boolean[totalNumLevels];
		positionsX = new float[totalNumLevels];
		zIndices = new int[totalNumLevels];

//		Creating bmp font from ttf
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("menu\\Comfortaa.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = FONT_SIZE;
		parameter.color = FONT_COLOR;
		parameter.borderWidth = 2;
		font = generator.generateFont(parameter);
		generator.dispose();

		buttons = new ImageTextButton[totalNumLevels];
		for (i=0; i<totalNumLevels; i++) {
			buttons[i] = createImageTextButton("menu\\door"+(i%numLevels+1)+"light.png", font, i+1);
			buttons[i].addListener(new ClickListener() {
				int saved_i = i;
				public void clicked(InputEvent event, float x, float y) {
					buttonsClicked[saved_i] = true;
				}
			});
			upImages[i%numLevels] = new TextureRegionDrawable(new Texture(Gdx.files.internal("menu\\door"+(i%numLevels+1)+"light.png")));
			overImages[i%numLevels] = new TextureRegionDrawable(new Texture(Gdx.files.internal("menu\\cloud_withpink.png")));
		}

		placeButtons();

		this.canvas  = canvas;
		// Compute the dimensions from the canvas
		resize(canvas.getWidth(),canvas.getHeight());
	}


	private void placeButtons(){
		table = new Table();
		table.setFillParent(true);

		titleTexture = new Texture(Gdx.files.internal("menu\\dream_selection.png"));
		titleDrawable = new TextureRegionDrawable(titleTexture);
		Image titleImage = new Image(titleDrawable);
		table.add(titleImage).colspan(numLevels+4).expandX().height(TITLE_HEIGHT).width(TITLE_WIDTH).padTop(TOP_PADDING);
		titleImage.setVisible(false);
		table.row();
//
//		for (int i=startIndex; i<startIndex+numLevels; i++) {
//			if (i%numLevels==0){
//				table.add(buttons[i]).padLeft(SIDE_PADDING).padTop(TOP_PADDING).size(DOOR_WIDTH, DOOR_HEIGHT).expandX();
//			}
//			else if (i%numLevels==3){
//				table.add(buttons[i]).padRight(SIDE_PADDING).padTop(TOP_PADDING).size(DOOR_WIDTH, DOOR_HEIGHT).expandX();
//			}
//			else {
//				table.add(buttons[i]).padTop(TOP_PADDING).size(DOOR_WIDTH, DOOR_HEIGHT).expandX();
//			}
//		}

		for (int i=0; i<totalNumLevels;i++){
			if (i==0){
				table.add(buttons[i]).padTop(TOP_PADDING).size(DOOR_WIDTH, DOOR_HEIGHT).expandX().padLeft(SIDE_PADDING+200);
			}
			else{
				table.add(buttons[i]).padTop(TOP_PADDING).size(DOOR_WIDTH, DOOR_HEIGHT).expandX();
			}
		}

		leftButton = createImageButton("menu\\left_arrow.png");
		rightButton = createImageButton("menu\\right_arrow.png");
//
//		table.add(leftButton).size(ARROW_SIZE, ARROW_SIZE);
//		leftButton.addListener(new ClickListener() {
//			public void clicked(InputEvent event, float x, float y) {
//				toLeft = true;
//			}
//		});
//


//		table.add(rightButton).size(ARROW_SIZE, ARROW_SIZE);
		Image[] cloudLineImages = new Image[totalNumLevels/numLevels];
		TextureRegionDrawable drawable = new TextureRegionDrawable(new Texture(Gdx.files.internal("menu\\cloudline_smaller.png")));
		for (int i=0; i<cloudLineImages.length; i++){
			cloudLineImages[i] = new Image(drawable);
		}

		table.row();

		for (int i=0; i<cloudLineImages.length; i++){
			if (i==0){
				table.add(cloudLineImages[i]).size(CLOUDLINE_WIDTH+100, CLOUDLINE_HEIGHT).padLeft(SIDE_PADDING+200).colspan(6);
			}
			else {
				table.add(cloudLineImages[i]).height(CLOUDLINE_HEIGHT).width(CLOUDLINE_WIDTH+100).colspan(6);
			}
		}


		table.add(rightButton);
		table.add(leftButton);
		rightButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				toRight = true;
			}
		});


		stage.addActor(table);
		table.validate();
		for (int j=0;j<totalNumLevels; j++){
			Actor buttonActor = (Actor) buttons[j];
			positionsX[j] = buttonActor.getX();
		}


		for (int i=0; i<cloudLineImages.length; i++){
			cloudLineImages[i].setY(CLOUDLINE_YPOSITION);
			if (i!=0) {
				cloudLineImages[i].setX(cloudLineImages[i].getX() - 400*i);
			}
			else{
				cloudLineImages[i].setX(cloudLineImages[i].getX() - 200);
			}
		}


		rightButton.setPosition(-1686+canvas.getWidth()/3,100);
		leftButton.setPosition(-1686-canvas.getWidth()/2+90, 100);
		leftButton.setVisible(false);
//		leftButton.setX(LEFT_BUTTON_POSITION);
//		rightButton.setX(canvas.getWidth()-RIGHT_BUTTON_POSITION);

	}

	/**
	 * Creating an image button that appears as an image with upFilepath.
	 */
	private Button createImageButton(String upFilepath){
		TextureRegionDrawable buttonDrawable = new TextureRegionDrawable(new Texture(Gdx.files.internal(upFilepath)));
		Button imgButton= new Button(buttonDrawable);
		return imgButton;
	}

	private ImageTextButton createImageTextButton(String upFilepath, BitmapFont font, int number){
		TextureRegionDrawable drawable1 = new TextureRegionDrawable(new Texture(Gdx.files.internal(upFilepath)));
		ImageTextButton.ImageTextButtonStyle btnStyle1 = new ImageTextButton.ImageTextButtonStyle();
		btnStyle1.up = drawable1;
		btnStyle1.font = font;
		ImageTextButton btn = new ImageTextButton(""+number, btnStyle1);
		return btn;
	}

	/**
	 * Called when this screen should release all resources.
	 */
	public void dispose() {}

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
		stage.getBatch().begin();
		stage.getBatch().draw(background, camera.position.x-canvas.getWidth()/2, camera.position.y-canvas.getHeight()/2, canvas.getWidth(), canvas.getHeight());
		stage.getBatch().draw(titleTexture, camera.position.x-canvas.getWidth()/4+40, camera.position.y+canvas.getHeight()/4, TITLE_WIDTH, TITLE_HEIGHT);
		stage.getBatch().end();

		if (first) {
			for (int i=0; i<totalNumLevels;i++){
				zIndices[i] = buttons[i].getZIndex();
			}
			initialCameraX =  camera.position.x - canvas.getWidth()*((totalNumLevels/6)-2)-150;
			System.out.println(initialCameraX);
			camera.position.x = initialCameraX;
			camera.position.y = 288;
		}
		else{
			if (rightButton.isOver()){
				leftButton.setVisible(true);
				camera.translate(7,0);
				rightButton.setPosition(rightButton.getX()+7, rightButton.getY());
				leftButton.setPosition(leftButton.getX()+7, leftButton.getY());
			}
			if (leftButton.isOver()){
				if (camera.position.x>=initialCameraX) {
					camera.translate(-7, 0);
					leftButton.setPosition(leftButton.getX() - 7, leftButton.getY());
					rightButton.setPosition(rightButton.getX() - 7, rightButton.getY());
				}
			}
//
		}
		camera.update();


		for (int i=0; i<totalNumLevels; i++){
			if (i>=totalActualLevels){
				buttons[i].setVisible(false);
			}
		}

		for (i=0; i<totalNumLevels; i++) {
			if (buttons[i].isOver()){
				ImageTextButton btn = (ImageTextButton) buttons[i];
				btn.getStyle().fontColor = FONT_COLOR;
				buttons[i].getStyle().up = overImages[i%numLevels];
				buttons[i].setSize(CLOUD_WIDTH,CLOUD_HEIGHT);
				buttons[i].setZIndex(100);

				Actor actor = (Actor) buttons[i];
				actor.setX(positionsX[i]-CLOUD_OFFSETX);
				actor.setY(initialButtonY-CLOUD_OFFSETY);
			}
			else{
				ImageTextButton btn = (ImageTextButton) buttons[i];
				btn.getStyle().fontColor = FONT_COLOR_TRANSPARENT;
				buttons[i].getStyle().up = upImages[i%numLevels];
				buttons[i].setZIndex(zIndices[i]);
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

			if (first) {
				for (int j = 0; j < totalNumLevels; j++) {
					Actor actor = (Actor) buttons[j];
					positionsX[j] = actor.getX();
					initialButtonY = actor.getY();
				}
				first = false;
			}

		}
		for (int i=0; i<totalNumLevels; i++){
			if (buttonsClicked[i]==true){
				buttonsClicked = new boolean[totalNumLevels];
				listener.exitScreen(this, i);
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
		stage.act();

//		table.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("menu\\selection_background1.png"))));
		stage.draw();
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

