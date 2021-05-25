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
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
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
public class MenuScrollable implements Screen {
	/** Reference to GameCanvas created by the root */
	private GameCanvas canvas;
	/** Listener that will update the player mode when we are done */
	private ScreenListener listener;
	/** Internal assets for this menu screen */
	private AssetDirectory internal;

	private Stage stage;
	private Table table;
	/** All buttons*/
	private Button[] buttons;
	private int totalNumLevels;
	private int numLevels = 4;
	private final int FONT_SIZE = 60;
	/** Setting the font color to the rgb values of black & visible, ie a=1*/
	private final Color FONT_COLOR = Color.WHITE;
//	rgba(255,176,111,151)
	private final Color BORDER_COLOR = new Color(255f/255f, 176f/255f, 111f/255f, 151f/255f);
	/** Setting the font color to the rgb values of black & invisible, ie a=0*/
	private final Color FONT_COLOR_TRANSPARENT = new Color(0,0,0,0);
	private BitmapFont font;
	/** The global variable for access inside an inner class during an iteration */
	public int currentLevel;
	/** Whether each individual button is clicked */
	private boolean[] buttonsClicked;
	private float[] positionsX;
	private Button leftButton;
	private Button rightButton;
	private Button arrow;
	/** Reference to the actor of cloudline */
	private Actor cloudlineActor;
	/** Height and width of the left and right arrows*/
	private final float ARROW_SIZE = 100;
	private final float LEFT_BUTTON_POSITION = 50;
	private final float RIGHT_BUTTON_POSITION = 100;
	private boolean toRight = false;
	private boolean toLeft = false;
	/** Setting the Y position of the cloudline to overlap with the doors*/
	private final float CLOUDLINE_YPOSITION = 20;
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
	private final float CLOUD_HEIGHT = 250;
	private final float CLOUDLINE_HEIGHT = 250;
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
	private TextureRegion [] overImages;
	private TextureRegionDrawable doorLockedImage;
	private TextureRegionDrawable upImage;
	private TextureRegion background;
	private TextureRegionDrawable titleDrawable;
	private Texture titleTexture;
	private TextureRegionDrawable leftButtonDrawable;
	private TextureRegionDrawable rightButtonDrawable;
	private TextureRegionDrawable cloudLineDrawable;
	private TextureRegionDrawable cloudDrawable;
	private TextureRegionDrawable arrowDrawable;
	private Texture arrowTexture;
	private int[] zIndices;
	private int startIndex;
	private boolean prevClicked;
	private boolean [] levelsCompleted;
	private Button cloudLine;
	private int totalLevels = 9;
	private int menuIndex;

	public Stage getStage(){
		return stage;
	}

	public MenuScrollable(GameCanvas canvas, int totalNumLevels, boolean[] levelsCompleted, int menuIndex) {
		internal = new AssetDirectory( "level_select.json" );
		internal.loadAssets();
		internal.finishLoading();

		titleTexture = internal.getEntry("title", Texture.class);
		titleDrawable = new TextureRegionDrawable(titleTexture);
		leftButtonDrawable = new TextureRegionDrawable(internal.getEntry("left_button", Texture.class));
		rightButtonDrawable = new TextureRegionDrawable(internal.getEntry("right_button", Texture.class));
		cloudLineDrawable = new TextureRegionDrawable(internal.getEntry("cloudline", Texture.class));
		cloudDrawable = new TextureRegionDrawable(internal.getEntry("cloud", Texture.class));
		arrowTexture = internal.getEntry("back_arrow", Texture.class);
		arrowDrawable = new TextureRegionDrawable(arrowTexture);
//		upImage = new TextureRegionDrawable(internal.getEntry("door", Texture.class));

		/*
		if (index==0){
			background = new TextureRegion(internal.getEntry("background_forest", Texture.class));
		}
		else if (index==1){
			background = new TextureRegion(internal.getEntry("background_statues", Texture.class));
		}
		else if (index==2){
			background = new TextureRegion(internal.getEntry("background_houses", Texture.class));
		}
		else if (index==3){
			background = new TextureRegion(internal.getEntry( "background_eyes", Texture.class));
		}
		else {
			background = new TextureRegion(internal.getEntry( "background_gears", Texture.class));
		}*/

		arrow = new Button(arrowDrawable);

		for (int i=0; i<numLevels; i++){
			upImages[i] = new TextureRegionDrawable(internal.getEntry("door"+(i%numLevels+1), Texture.class));
		}

		this.canvas = canvas;
		this.levelsCompleted = levelsCompleted;
		this.menuIndex = menuIndex;

		camera = new OrthographicCamera(canvas.getWidth(), canvas.getHeight());
//		camera.translate(0, camera.viewportHeight / 2, 0);
		stage = new Stage(new ScreenViewport(camera));
		//startIndex = index;
		//levelsCompleted = levels;
		this.totalNumLevels = totalNumLevels;
		buttonsClicked = new boolean[totalLevels];
		positionsX = new float[totalNumLevels];
		zIndices = new int[totalNumLevels];
		overImages = new TextureRegion[totalLevels];

//		Creating bmp font from ttf
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("menu\\Comfortaa.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = FONT_SIZE;
		parameter.color = Color.WHITE;
		parameter.borderWidth = 5;
		parameter.borderStraight = false;
		parameter.borderColor = BORDER_COLOR;
		font = generator.generateFont(parameter);
		generator.dispose();
//		font.setColor(BORDER_COLOR);
//		System.out.println(BORDER_COLOR.toString());
//		System.out.println(font.getColor());
		upImage = new TextureRegionDrawable(internal.getEntry("door", Texture.class));

		buttons = new Button[totalLevels];
		for (currentLevel =0; currentLevel <buttons.length; currentLevel++) {
			buttons[currentLevel] = new Button(upImage);
			buttons[currentLevel].addListener(new ClickListener() {
				int saved_i = currentLevel;
				public void clicked(InputEvent event, float x, float y) {
					buttonsClicked[saved_i] = true;
				}
			});
			overImages[currentLevel] = new TextureRegion(internal.getEntry("number_"+(currentLevel+1), Texture.class));;
		}

		for (currentLevel =0; currentLevel <buttons.length; currentLevel++) {
			if (menuIndex!=0 || (currentLevel!=0 && levelsCompleted[currentLevel-1]==false)){
				buttons[currentLevel].getStyle().up = doorLockedImage;
				buttons[currentLevel].setTouchable(Touchable.disabled);
			}
		}


		arrow.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				prevClicked = true;
			}
		});

		placeButtons();

		this.canvas  = canvas;
		// Compute the dimensions from the canvas
		resize(canvas.getWidth(),canvas.getHeight());
	}

	public void setBackground(TextureRegion tr) {
		background = tr;
	}

	public void setDoorImages(TextureRegionDrawable door) {
//		for (int i=0; i<numLevels; i++){
//			upImages[i] = door;
//		}
		upImage = door;
	}

	public void setDoorLockedImage(TextureRegionDrawable door){
		doorLockedImage = door;
	}

	public void setLevels(String[] levels) {

	}

	private void placeButtons(){
		table = new Table();
		table.setFillParent(true);

		table.add(arrow).size(arrow.getWidth()/2, arrow.getHeight()/2);;
		table.row();
		Image titleImage = new Image(titleDrawable);
		table.add(titleImage).colspan(6).expandX().height(TITLE_HEIGHT).width(TITLE_WIDTH).padTop(TOP_PADDING);
		titleImage.setVisible(false);
		table.row();

		for (int i=0; i<buttons.length;i++){
			if (i==0){
				table.add(buttons[i]).padTop(TOP_PADDING).size(buttons[i].getWidth() * 2/3, buttons[i].getHeight() * 2/3);
			}
			else{
				table.add(buttons[i]).padTop(TOP_PADDING).size(buttons[i].getWidth() * 2/3, buttons[i].getHeight() * 2/3);
			}
		}

		leftButton = new ImageButton(leftButtonDrawable);
		rightButton = new ImageButton(rightButtonDrawable);

//		Button[] cloudLineImages = new Button[totalNumLevels/numLevels+1];
//		for (int i=0; i<cloudLineImages.length; i++){
//			cloudLineImages[i] = new Button(cloudLineDrawable);
//			cloudLineImages[i].setDisabled(true);
//		}

		table.row();
		cloudLine = new Button(cloudLineDrawable);
		table.add(cloudLine).size(canvas.getWidth()+100, cloudLine.getHeight()/2).colspan(4);

//		for (int i=0; i<cloudLineImages.length; i++){
//			if (i==0){
//				table.add(cloudLineImages[i]).size(CLOUDLINE_WIDTH+100, CLOUDLINE_HEIGHT).padLeft(SIDE_PADDING+200).colspan(6);
//			}
//			else {
//				table.add(cloudLineImages[i]).height(CLOUDLINE_HEIGHT).width(CLOUDLINE_WIDTH+100).colspan(6);
//			}
//		}


		table.add(rightButton).size(ARROW_SIZE+30, ARROW_SIZE);
		table.add(leftButton).size(ARROW_SIZE+30, ARROW_SIZE);
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

		for (int i=0; i<totalLevels; i++){
			buttons[i].getStyle().up = upImage;
			if (i>=totalNumLevels){
				buttons[i].setVisible(false);
			}
		}


//		for (int i=0; i<cloudLineImages.length; i++){
//			cloudLineImages[i].setY(CLOUDLINE_YPOSITION);
//			if (i!=0) {
//				cloudLineImages[i].setX(cloudLineImages[i].getX() - 400*i);
//			}
//			else{
//				cloudLineImages[i].setX(cloudLineImages[i].getX() - 200);
//			}
//			cloudLineImages[i].setDisabled(true);
//			cloudLineImages[i].setTouchable(Touchable.disabled);
//		}

		cloudLine.setY(CLOUDLINE_YPOSITION);
		cloudLine.setX(-362-canvas.getWidth()/2-80);
		cloudLine.setDisabled(true);
		cloudLine.setTouchable(Touchable.disabled);

		arrow.setPosition(-362-canvas.getWidth()/2+10, canvas.getHeight()-arrow.getHeight()-10);
		rightButton.setPosition(-362+canvas.getWidth()/2-200,120);
		leftButton.setPosition(-362-canvas.getWidth()/2+90, 120);
		leftButton.setVisible(false);

		initialCameraX = -362;
		camera.position.x = initialCameraX;
		camera.position.y = 288;
		camera.update();

	}

	/**
	 * Creating an image button that appears as an image with upFilepath.
	 */
	private Button createImageButton(String upFilepath){
		TextureRegionDrawable buttonDrawable = new TextureRegionDrawable(new Texture(Gdx.files.internal(upFilepath)));
		Button imgButton= new Button(buttonDrawable);
		return imgButton;
	}

	private ImageTextButton createImageTextButton(TextureRegionDrawable drawable, BitmapFont font, int number){ ;
		ImageTextButton.ImageTextButtonStyle btnStyle1 = new ImageTextButton.ImageTextButtonStyle();
		btnStyle1.up = drawable;
		btnStyle1.font = font;
		ImageTextButton btn = new ImageTextButton(""+number, btnStyle1);
		return btn;
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
		stage.getBatch().begin();
		stage.getBatch().draw(background, camera.position.x-canvas.getWidth()/2, camera.position.y-canvas.getHeight()/2, canvas.getWidth(), canvas.getHeight());
		stage.getBatch().draw(titleTexture, camera.position.x-canvas.getWidth()/4+40, camera.position.y+canvas.getHeight()/4, TITLE_WIDTH, TITLE_HEIGHT);
		stage.getBatch().end();

		if (first) {
			for (int i=0; i<totalNumLevels;i++){
				zIndices[i] = buttons[i].getZIndex();
			}
		}
		else{
			if (rightButton.isOver()){
				if (camera.position.x<(buttons[totalNumLevels-1].getX())) {
					leftButton.setVisible(true);
					camera.translate(7, 0);
					rightButton.setPosition(rightButton.getX() + 7, rightButton.getY());
					leftButton.setPosition(leftButton.getX() + 7, leftButton.getY());
					arrow.setPosition(arrow.getX()+7, arrow.getY());
					cloudLine.setPosition(cloudLine.getX()+7, cloudLine.getY());
				}
			}
			if (leftButton.isOver()){
				if (camera.position.x>=initialCameraX) {
					camera.translate(-7, 0);
					leftButton.setPosition(leftButton.getX() - 7, leftButton.getY());
					rightButton.setPosition(rightButton.getX() - 7, rightButton.getY());
					arrow.setPosition(arrow.getX()-7, arrow.getY());
					cloudLine.setPosition(cloudLine.getX()-7, cloudLine.getY());
				}
				else{
					leftButton.setVisible(false);
				}
			}
//
		}
		camera.update();

		for (currentLevel =0; currentLevel <buttons.length; currentLevel++) {
			if (buttons[currentLevel].isOver()){
				buttons[currentLevel].getStyle().up = upImage;
				stage.getBatch().begin();
				Actor btnActor = (Actor) buttons[currentLevel];
				stage.getBatch().draw(overImages[currentLevel], btnActor.getX(), btnActor.getY(), buttons[currentLevel].getWidth(),  buttons[currentLevel].getHeight());
				stage.getBatch().end();

//				buttons[currentLevel].getStyle().up = upImage;
//				buttons[currentLevel].setSize(CLOUD_WIDTH,CLOUD_HEIGHT);
//				buttons[currentLevel].setZIndex(buttons[buttons.length-1].getZIndex());
//				Actor actor = (Actor) buttons[currentLevel];
//				actor.setX(positionsX[currentLevel]-CLOUD_OFFSETX);
//				actor.setY(initialButtonY-CLOUD_OFFSETY);
			}
			else{
				buttons[currentLevel].getStyle().up = upImage;
//				buttons[currentLevel].getStyle().up = upImage;
//				buttons[currentLevel].setZIndex(zIndices[currentLevel]);
//				buttons[currentLevel].setSize(buttons[currentLevel].getWidth(), buttons[currentLevel].getHeight());
//				Actor actor = (Actor) buttons[currentLevel];
//				actor.setX(positionsX[currentLevel]);
//				actor.setY(initialButtonY);
			}

			if (menuIndex!=0 || (currentLevel!=0 && levelsCompleted[currentLevel-1]==false)){
				buttons[currentLevel].getStyle().up = doorLockedImage;
				buttons[currentLevel].setTouchable(Touchable.disabled);
			}
			else {
				buttons[currentLevel].setTouchable(Touchable.enabled);
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

			for (currentLevel =0; currentLevel <buttons.length; currentLevel++) {
				if (buttons[currentLevel].isOver()) {
					buttons[currentLevel].getStyle().up = upImage;
					stage.getBatch().begin();
					Actor btnActor = (Actor) buttons[currentLevel];
					stage.getBatch().draw(overImages[currentLevel], btnActor.getX(), btnActor.getY(), buttons[currentLevel].getWidth(), buttons[currentLevel].getHeight());
					stage.getBatch().end();
				}
			}

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
				currentLevel = i+1;
				listener.exitScreen(this, WorldController.EXIT_NEW_LEVEL);
			}
		}
		if (prevClicked){
			listener.exitScreen(this, WorldController.EXIT_WORLD_SELECT_ENTER);
			prevClicked = false;
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

