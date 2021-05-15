/*
 * PlatformController.java
 *
 * You SHOULD NOT need to modify this file.  However, you may learn valuable lessons
 * for the rest of the lab by looking at it.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * Updated asset version, 2/6/2021
 */
package edu.cornell.gdiac.somniphobia.game.controllers;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import edu.cornell.gdiac.audio.SoundController;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.graphics.OrthographicCamera;

import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.audio.SoundBuffer;
import edu.cornell.gdiac.somniphobia.game.models.CharacterModel;
import edu.cornell.gdiac.somniphobia.game.models.PlatformModel;
import edu.cornell.gdiac.util.*;
import edu.cornell.gdiac.somniphobia.*;
import edu.cornell.gdiac.somniphobia.obstacle.*;

/**
 * Gameplay specific controller for the platformer game.
 *
 * You will notice that asset loading is not done with static methods this time.
 * Instance asset loading makes it easier to process our game modes in a loop, which
 * is much more scalable. However, we still want the assets themselves to be static.
 * This is the purpose of our AssetState variable; it ensures that multiple instances
 * place nicely with the static assets.
 */
public class LevelController extends WorldController {
	private OrthographicCamera camera;

	/** Texture asset for character avatar */
	private TextureRegion avatarTexture;
	/** Texture asset for combined character avatar */
	private TextureRegion combinedTexture;
	/** Texture asset for light tiles*/
	private TextureRegion lightTexture;
	/** Texture asset for dark tiles*/
	private TextureRegion darkTexture;
	/** Texture asset for "all" tiles*/
	private TextureRegion allTexture;
	/** Texture asset for raining light tiles*/
	private TextureRegion rainLightTexture;
	/** Texture asset for raining dark tiles*/
	private TextureRegion rainDarkTexture;
	/** Texture asset for raining "all" tiles*/
	private TextureRegion rainAllTexture;
	/** Texture asset for lightning light tiles*/
	private TextureRegion lightningLightTexture;
	/** Texture asset for lightning dark tiles*/
	private TextureRegion lightningDarkTexture;
	/** Texture asset for lightning "all" tiles*/
	private TextureRegion lightningAllTexture;
	/** Texture asset for crumbling light tiles*/
	private TextureRegion crumbleLightTexture;
	/** Texture asset for crumbliing dark tiles*/
	private TextureRegion crumbleDarkTexture;
	/** Texture asset for crumbling "all" tiles*/
	private TextureRegion crumbleAllTexture;
	/** Texture asset for reduced size raining light tiles*/
	private TextureRegion rainLightTextureReduced;
	/** Texture asset for reduced size raining dark tiles*/
	private TextureRegion rainDarkTextureReduced;
	/** Texture asset for reduced size raining "all" tiles*/
	private TextureRegion rainAllTextureReduced;
	/** Texture asset for reduced size lightning light tiles*/
	private TextureRegion lightningLightTextureReduced;
	/** Texture asset for reduced size lightning dark tiles*/
	private TextureRegion lightningDarkTextureReduced;
	/** Texture asset for reduced size lightning "all" tiles*/
	private TextureRegion lightningAllTextureReduced;
	/** Texture asset for reduced crumbling light tiles*/
	private TextureRegion crumbleLightTextureReduced;
	/** Texture asset for reduced crumbliing dark tiles*/
	private TextureRegion crumbleDarkTextureReduced;
	/** Texture asset for reduced crumbling "all" tiles*/
	private TextureRegion crumbleAllTextureReduced;

	/** Texture asset for Somni*/
	private TextureRegion somniTexture;
	/** Texture asset for Somni's Idle animation*/
	private TextureRegion somniIdleTexture;
	/** Texture asset for Somni's Walk*/
	private TextureRegion somniWalkTexture;
	/** Texture asset for Somni's Dash side*/
	private TextureRegion somniDashSideTexture;
	/** Texture asset for Somni's Dash up*/
	private TextureRegion somniDashUpTexture;
	/** Texture asset for Somni's Falling*/
	private TextureRegion somniFallTexture;
	/** Texture asset for Phobia's Falling*/
	private TextureRegion phobiaFallTexture;
	/** Texture asset for phobia*/
	private TextureRegion phobiaTexture;
	/** Texture asset for Phobia's Idle animation*/
	private TextureRegion phobiaIdleTexture;
	/** Texture asset for Phobia's Walk*/
	private TextureRegion phobiaWalkTexture;
	/** Texture asset for Phobia's Dash side*/
	private TextureRegion phobiaDashSideTexture;
	/** Texture asset for Phobia's Dash up*/
	private TextureRegion phobiaDashUpTexture;
	/** Texture asset for Somni*/
	private TextureRegion somniPhobiaTexture;
	/** Texture asset for Somni's Walk*/
	private TextureRegion somniPhobiaWalkTexture;
	/** Texture asset for Somni's Dash side*/
	private TextureRegion somniPhobiaDashSideTexture;
	/** Texture asset for Somni's Dash up*/
	private TextureRegion somniPhobiaDashUpTexture;
	/** Texture asset for phobia*/
	private TextureRegion phobiaSomniTexture;
	/** Texture asset for Somni's Walk*/
	private TextureRegion phobiaSomniWalkTexture;
	/** Texture asset for Somni's Dash side*/
	private TextureRegion phobiaSomniDashSideTexture;
	/** Texture asset for Somni's Dash up*/
	private TextureRegion phobiaSomniDashUpTexture;

	/** Texture asset for the hands of somni and phobia */
	private TextureRegion somniPhobiaHandsTexture;
	/** Texture asset for the hands of phobia and somni */
	private TextureRegion phobiaSomniHandsTexture;
	/** Texture asset for phobia's hand and the blue ring in propelling */
	private TextureRegion blueRingBigTexture;
	/** Texture asset for somni's hand and the yellow ring in propelling */
	private TextureRegion yellowRingBigTexture;
	/** Texture asset for the dashing blue ring */
	private TextureRegion blueRingSmallTexture;
	/** Texture asset for the dashing yellow ring */
	private TextureRegion yellowRingSmallTexture;
	/** Testure asset for somni's hand reaching out forwards */
	private TextureRegion somniHandFrontTexture;
	/** Testure asset for somni's hand reaching out backwards */
	private TextureRegion somniHandBackTexture;
	/** Testure asset for phobia's hand reaching out forwards */
	private TextureRegion phobiaHandFrontTexture;
	/** Testure asset for phobia's hand reaching out backwards */
	private TextureRegion phobiaHandBackTexture;

	/** Texture asset for current background*/
	private TextureRegion backgroundTexture;
	/** Texture asset for level's light background*/
	private TextureRegion backgroundLightTexture;
	/** Texture asset for level's dark background*/
	private TextureRegion backgroundDarkTexture;
	/** Texture assets for backgrounds */
	private TextureRegion[] backgrounds;

	/** Texture asset for tutorial signs */
	private TextureRegion[] tutorial_signs;

	/** Texture asset list for somni*/
	private TextureRegion [] somnisTexture;
	/** Texture asset list for phobia*/
	private TextureRegion [] phobiasTexture;
	/** Texture asset list for somniphobia*/
	private TextureRegion [] somniphobiasTexture;
	/** Texture asset list for phobiasomni*/
	private TextureRegion [] phobiasomnisTexture;

	/** Texture asset list for somnie's hands */
	private TextureRegion [] somniHandsTextures;
	/** Texture asset list for phobia's hands */
	private TextureRegion [] phobiaHandsTextures;

	/** Texture asset list for phobiasomni*/
	private float[] animationSpeed;
	private double[] framePixelWidth;
	private float[] offsetsX;
	private float[] offsetsY;
	private float[] secOffsetsX;
	private float[] secOffsetsY;
	private float[] thirdOffsetsX;
	private float[] thirdOffsetsY;
	private float[] dashAngles;
	private float[] propelAngles;
	/** Texture for slider bars*/
	private Texture sliderBarTexture;
	private Texture sliderKnobTexture;

	/** Origin for the expanding/shrinking mask */
	private Vector2 maskOrigin = new Vector2();
	/** Texture for masking */
	private TextureRegion circle_mask;
	/** Texture to cover screen to produce mask effect*/
	private Texture alpha_background;
	/** Buffer used to apply 2 blends to one texture*/
	private FrameBuffer fbo;
	/** Color used for holding hand fade in effect */
	private Color alphaWhite = new Color(Color.WHITE);
	/** Alpha value used for `alphaWhite` */
	private float alphaAmount = 0.0f;
	/** Amount to change `alphaAmount` by when holding hands */
	private float alphaIncrement = 0.05f;


	/** Texture asset int for action*/
	private int action;

	/** The jump sound.  We only want to play once. */
	private SoundBuffer jumpSound;
	private long jumpId = -1;
	/** The weapon fire sound.  We only want to play once. */
	private SoundBuffer fireSound;
	private long fireId = -1;
	/** The weapon pop sound.  We only want to play once. */
	private SoundBuffer plopSound;

	private SoundBuffer somniTrackPath;
	private SoundBuffer phobiaTrackPath;
	private SoundBuffer combinedTrackPath;


	private long plopId = -1;
	/** The default sound volume */
	private float volume;

	private MovementController movementController;

	/** The current level being played */
	private int level;

	// Physics objects for the game
	/** Physics constants for initialization */
	private JsonValue constants;
	/** Level constants for initialization */
	private JsonValue levelAssets;
	/** Reference to Somni DudeModel*/
	private CharacterModel somni;
	/** Reference to Phobia DudeModel*/
	private CharacterModel phobia;
	/** Reference to combined DudeModel*/
	private CharacterModel combined;
	/** Reference to the goalDoor (for collision detection) */
	private BoxObstacle goalDoor;

	/** shared objects */
	protected PooledList<Obstacle> sharedObjects  = new PooledList<Obstacle>();
	/** shared objects */
	protected PooledList<Obstacle> lightObjects  = new PooledList<Obstacle>();
	/** shared objects */
	protected PooledList<Obstacle> darkObjects  = new PooledList<Obstacle>();
	/** moving objects */
	protected PooledList<Obstacle> movingObjects = new PooledList<Obstacle>();
	/** Currently raining platforms */
	protected PooledList<Obstacle> currRainingPlatforms = new PooledList<>();


	private boolean lightclear = false;
	private boolean darkclear = false;
	private boolean sharedclear = false;
	private boolean allclear = false;

	/** Are characters currently holding hands */
	private boolean holdingHands;

	/** Camera stuff */
	private float widthUpperBound, heightUpperBound;
	private float LERP = 2f;
	private Vector2 panMovement = new Vector2(0,0);


	/** Masking stuff */
	/** Amount to scale down circle mask image by */
	float mask_shrink_factor = 0.125f;
	/** Dimensions for the mask when at its smallest */
	Vector2 MIN_MASK_DIMENSIONS;
	/** Amount to increase and decrease rift mask size with */
	float INCREMENT_AMOUNT = 50;
	/** Current width and height of the mask */
	float maskWidth, maskHeight;
	/** Offset to apply to mask when checking if in camera bounds*/
	float maskOffset = 1000;
	/** Whether or not the mask is in the process of switching*/
	boolean switching;
	/** The character to perform the mask effect from */
	CharacterModel maskLeader;

	/** Mark set to handle more sophisticated collision callbacks */
	protected ObjectSet<Fixture> lightSensorFixtures;
	protected ObjectSet<Fixture> darkSensorFixtures;

	protected ObjectSet<Fixture> combinedSensorFixtures;
	// Platform logic

	private int LIGHT_TAG = 0;
	private int DARK_TAG = 0;
	private int ALL_TAG = 0;
	private int SOMNI_TAG = 0;
	private int PHOBIA_TAG = 0;
	private int COMBINED_TAG = 0;

	//JENNA SETUP
	private Table pauseMenu;
	private Table failMenu;
	private Table winMenu;
	private Boolean firstTimeRenderedPauseMenu=true;
	private Boolean firstTimeRenderedFailMenu=true;
	private Boolean firstTimeRenderedWinMenu=true;
	private Boolean firstTimeRenderedPauseButton = true;
	private Button exitButton;
	private Button exitButtonFail;
	private Button exitButtonWin;
	private Button resumeButton;
	private Button restartButton;
	private Button restartButtonFail;
	private Button advanceButton;
	private Button pauseButton;
	private boolean exitClicked;
	private boolean resumeClicked;
	private boolean restartClicked;
	private boolean advanceClicked;
	private Stage pauseMenuStage = new Stage();
	private Stage failMenuStage = new Stage();
	private Stage winMenuStage = new Stage();
	private Stage pauseButtonStage = new Stage();
	private boolean gameScreenActive = true;

	//END JENNA

	/** whether pauseMenu is rendered for the first time*/
	private Boolean firstTimeRendered=true;
	private Boolean firstTimePause=true;
	/** the underline on pauseMenu*/
	private Image underline;
	private Image underlineWinMenu;
	private Image underlineFailMenu;
	/** pauseMenu sliders and icons*/
	private Slider sliderMusic;
	private Slider sliderSound;
	private Image musicIcon;
	private Image soundIcon;
	/** pause menu drawables*/
	private TextureRegionDrawable blueUnderline;
	private TextureRegionDrawable orangeUnderline;
	private TextureRegionDrawable blueRectangle;
	private TextureRegionDrawable blueExit;
	private TextureRegionDrawable blueResume;
	private TextureRegionDrawable blueRestart;
	private TextureRegionDrawable orangeRectangle;
	private TextureRegionDrawable orangeExit;
	private TextureRegionDrawable orangeResume;
	private TextureRegionDrawable orangeRestart;
	private TextureRegionDrawable blueNext;
	private TextureRegionDrawable orangeNext;
	private TextureRegionDrawable blueKnob;
	private TextureRegionDrawable orangeKnob;
	private TextureRegionDrawable blueMusicNote;
	private TextureRegionDrawable orangeMusicNote;
	private TextureRegionDrawable blueSlider;
	private TextureRegionDrawable orangeSlider;
	private TextureRegionDrawable blueSound;
	private TextureRegionDrawable orangeSound;
	private TextureRegionDrawable bluePauseButton;
	private TextureRegionDrawable orangePauseButton;

	/** constants for positioning pause menu and pause button */
	private final int PAUSE_BUTTON_OFFSETX = 400;
	private final int PAUSE_BUTTON_OFFSETY = 200;
	private final int PAUSE_BUTTON_WIDTH = 100;
	private final int PAUSE_BUTTON_HEIGHT = 80;
	private final float PAUSE_MENU_SCALE = 0.5f;
	private final int PAUSE_MENU_BUTTON_SPACE = 0;
	private final int UNDERLINE_WIDTH_OFFSET = 10;
	private final int UNDERLINE_HEIGHT_OFFSET = 0;
	private final int UNDERLINE_OFFSETX = -5;
	private final int UNDERLINE_OFFSETY = -40;
	private final int PAUSE_MENU_POSITION_SCALE = 4;
	private boolean firstPosition=false;

	Label.LabelStyle labelStyle;
	private Slider [] sliders;
	private Label [] labels;

	public Widget sliderMenu;

	public int tes = 0; // <-- enoch please don't do this

	// WASD Camera Variables

	private Vector2 cameraCenter;
	private int cameraDelay = 0;
	private Stage stage=new Stage();

	/// VARIABLES FOR DRAWING AND ANIMATION OF BACKGROUNDS
	/** CURRENT image for this background. May change over time. */
	private FilmStrip backgroundAnimator;
	/** Reference to texture origin */
	private Vector2 backgroundOrigin;
	/** How fast we change frames (one frame per 10 calls to update) */
	private float backgroundAnimationSpeed = 0.05f;
	/** The number of animation frames in our filmstrip */
	private int backgroundNumAnimFrames = 15;
	/** Current animation frame for this shell */
	private float backgroundAnimeframe = 0.0f;
	/** Pixel width of the current texture */
	private double backgroundEntirePixelWidth;
	/** Pixel width of the current frame in the texture */
	private double backgroundFramePixelWidth = 1920;
	/** Texture for animated objects */
	private Texture actualBackgroundTexture;

	/**
	 * Creates and initialize a new instance of the platformer game
	 *
	 * The game has default gravity and other settings
	 */
	public LevelController(GameCanvas gameCanvas) {
		super(DEFAULT_WIDTH,DEFAULT_HEIGHT,DEFAULT_GRAVITY);
		setDebug(false);
		setComplete(false);
		setFailure(false);
		world.setContactListener(movementController);
		lightSensorFixtures = new ObjectSet<Fixture>();
		darkSensorFixtures = new ObjectSet<Fixture>();
		combinedSensorFixtures = new ObjectSet<Fixture>();
		holdingHands = false;
		widthUpperBound = 0;
		heightUpperBound = 0;
		canvas = gameCanvas;

	}

	public Stage getPauseButtonStage(){
		return pauseButtonStage;
	}

	public Stage getPauseMenuStage(){
		return pauseMenuStage;
	}

//	/**
//	 * Helper function for creating a drawable from an image with filepath
//	 */
//	private TextureRegionDrawable createDrawable(String filePath){
//		TextureRegionDrawable drawable = new TextureRegionDrawable(new Texture(Gdx.files.internal(filePath)));
//		return drawable;
//	}

//	/**
//	 * Helper function for creating buttons on pause menu:
//	 * Creating an image button that appears as an image with upFilepath.
//	 */
//	private Button createImageButton(String upFilepath){
//		TextureRegionDrawable upButtonDrawable = createDrawable(upFilepath);
//		Button imgButton= new Button(upButtonDrawable);
//		return imgButton;
//		return imgButton;
//	}

	/**
	 * Creates the pauseMenu with the buttons
	 */
	public void createModalWindow(float cameraX, float cameraY) {
		camera.position.x = cameraX;
		camera.position.y = cameraY;
		camera.update();

//		pauseMenuStage = new Stage(new ScreenViewport(camera));
		pauseMenu = new Table();
		pauseMenu.setBackground(blueRectangle);
		pauseMenu.setFillParent(true);


//		create sliders
		Slider.SliderStyle sliderStyle = new Slider.SliderStyle(blueSlider, blueKnob);
		sliderMusic = new Slider(0,1,0.01f,false,sliderStyle);
		sliderSound = new Slider(0,1,0.01f,false,sliderStyle);
		musicIcon = new Image(blueMusicNote);
		soundIcon = new Image(blueSound);
		sliderMusic.setValue(volume);


		exitButton = new Button(blueExit);
		resumeButton = new Button(blueResume);
		restartButton = new Button(blueRestart);
		underline = new Image(blueUnderline);

		pauseMenu.add(musicIcon).height(100).width(100);
		pauseMenu.add(sliderMusic).colspan(3).height(150).width(500).padRight(150);
		pauseMenu.row();
		pauseMenu.add(soundIcon).height(100).width(100);
		pauseMenu.add(sliderSound).colspan(3).height(100).width(500).padRight(150);
		sliderSound.setDisabled(true);
		pauseMenu.row();
		pauseMenu.add(exitButton).space(PAUSE_MENU_BUTTON_SPACE).size(150,70).padLeft(110).padTop(50);
		pauseMenu.add(restartButton).space(PAUSE_MENU_BUTTON_SPACE).size(200,70).padTop(50).padLeft(20);
		pauseMenu.add(resumeButton).space(PAUSE_MENU_BUTTON_SPACE).size(200,80).padRight(110).padTop(50);
		pauseMenu.row();
		pauseMenu.add(underline);
		underline.setVisible(false);

//		adding listeners
		exitButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				exitClicked = true;
			}
		});

		resumeButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				resumeClicked = true;
			}
		});

		restartButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				restartClicked = true;
			}
		});

		sliderMusic.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (movementController.isHoldingHands()){
					SoundController.getInstance().setVolume(volume, "combinedTrack");
				}
				else if (movementController.getAvatar()==somni){
					SoundController.getInstance().setVolume(volume, "somniTrack");
				}
				else if (movementController.getAvatar()==phobia){
					SoundController.getInstance().setVolume(volume, "phobiaTrack");
				}
				volume = sliderMusic.getValue();
				GDXRoot.setPreferences(GDXRoot.getPreferences().putFloat("volume", volume));
			}
		});

//		pauseMenu.setPosition(camera.position.x- canvas.getWidth()/PAUSE_MENU_POSITION_SCALE , camera.position.y-canvas.getHeight()/PAUSE_MENU_POSITION_SCALE );
		pauseMenuStage.addActor(pauseMenu);
		pauseMenu.validate();
		pauseMenu.setTransform(true);
		pauseMenu.setScale(PAUSE_MENU_SCALE);
		underline.setZIndex(0);
		underline.setVisible(false);

	}

	/**
	 * Resets the position of the pauseMenu relative to the camera's position
	 */
	public void setPositionPauseMenu(){
		pauseMenu.setPosition(camera.position.x- canvas.getWidth()/PAUSE_MENU_POSITION_SCALE , camera.position.y-canvas.getHeight()/PAUSE_MENU_POSITION_SCALE );
	}

	public void createFailWindow(float cameraX, float cameraY) {
//		failMenuStage = new Stage();
		camera.position.x = cameraX;
		camera.position.y = cameraY;
		failMenu = new Table();
		failMenu.setBackground(blueRectangle);
		failMenu.setFillParent(true);

		TextureRegionDrawable drawable1 = new TextureRegionDrawable(new Texture(Gdx.files.internal("pause_menu\\dreamfailed.png")));
		Image img = new Image (drawable1);

		exitButtonFail = new Button(blueExit);
		restartButtonFail = new Button(blueRestart);
		underlineFailMenu = new Image(blueUnderline);

		//Buttons needed
		failMenu.add(img).colspan(2);
		failMenu.row().padTop(50);
		failMenu.add(exitButtonFail).size(150,70).padRight(70);
		failMenu.add(restartButtonFail).size(200,70).padRight(10);
		failMenu.row();
		failMenu.add(underlineFailMenu);
		underlineFailMenu.setVisible(false);

		exitButtonFail.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				exitClicked = true;
			}
		});

		restartButtonFail.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				restartClicked = true;
			}
		});

		failMenu.setPosition(camera.position.x, camera.position.y);
		failMenuStage.addActor(failMenu);
		failMenu.validate();
		failMenu.setTransform(true);
		failMenu.setScale(0.5f);
		underlineFailMenu.setZIndex(0);

	}

	public void createWinWindow(float cameraX, float cameraY) {
//		winMenuStage = new Stage();
		camera.position.x = cameraX;
		camera.position.y = cameraY;
		camera.update();
		winMenu = new Table();
		winMenu.setBackground(blueRectangle);
		winMenu.setFillParent(true);

		TextureRegionDrawable drawable1 = new TextureRegionDrawable(new Texture(Gdx.files.internal("pause_menu\\dreamcomplete.png")));
		Image img = new Image (drawable1);

		exitButtonWin = new Button(blueExit);
		advanceButton = new Button(blueNext);
		underlineWinMenu = new Image(blueUnderline);


		//Buttons needed
		winMenu.add(img).colspan(2);
		winMenu.row().padTop(50);
		winMenu.add(exitButtonWin).size(150,70);
		winMenu.add(advanceButton).size(150,70).padLeft(90).padRight(40);
		winMenu.row();
		winMenu.add(underlineWinMenu);
		underlineWinMenu.setVisible(false);
		winMenu.row();
		winMenu.add(underlineWinMenu);
		underlineWinMenu.setVisible(false);

		exitButtonWin.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				exitClicked = true;
			}
		});

		advanceButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				advanceClicked = true;
			}
		});

		winMenuStage.addActor(winMenu);
		winMenu.validate();
		winMenu.setTransform(true);
		winMenu.setScale(0.5f);
		underlineWinMenu.setZIndex(0);
		underlineWinMenu.setVisible(false);

	}

	public void setPositionMenu(Table menu){
		menu.setPosition(camera.position.x- canvas.getWidth()/4, camera.position.y-canvas.getHeight()/4);
	}

	//END JENNA


//	public void createPauseButton(){
//		TextureRegionDrawable buttonDrawable = new TextureRegionDrawable(new Texture(Gdx.files.internal()));
//		Button imgButton= new Button(buttonDrawable);
//	}

	/**
	 * Creates sliders to adjust game constants.
	 */
	public void createSliders() {
		sliders = new Slider[7];
		labels = new Label[7];
		CharacterModel avatar = movementController.getAvatar();

		Batch b = canvas.getBatch();
		ChangeListener slide = new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Slider slider = (Slider) actor;
				float value = slider.getValue();
				System.out.println(value);
			}
		};

		float current = 0;
		float max = 0;
		float min = 0;
//		stage = new Stage();

		Slider.SliderStyle style =
				new Slider.SliderStyle(new TextureRegionDrawable(sliderBarTexture), new TextureRegionDrawable(sliderKnobTexture));
		BitmapFont font = displayFont;
		font.getData().setScale(.3f, .3f);
		labelStyle = new Label.LabelStyle(font, Color.BLACK);

		// Dash Velocity

		current = avatar.getDashVelocity();
		max = current * 1.5f;
		min = current * 0.5f;

		Slider s = new Slider(min, max, 0.1f, false, style);
		s.setValue(current);
		s.setPosition(10, 500);
		stage.addActor(s);

		final Label test1 = new Label("Dash Velocity: " + avatar.getDashVelocity(), labelStyle);
		test1.setPosition(10, 532);
		s.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Slider s = (Slider) actor;
				float f = s.getValue();
				System.out.println("Dash Velocity : " + f);
				somni.setDashVelocity(f);
				phobia.setDashVelocity(f);
				combined.setDashVelocity(f);
				test1.setText("Dash Velocity: " + f);
			}
		});
		sliders[0] = s;
		labels[0] = test1;

		// Dash Dampening
		current = avatar.getDashDamping();
		max = current * 2.5f;
		min = current * 0.5f;

		Slider s2 = new Slider(min, max, 0.1f, false, style);
		s2.setValue(current);
		s2.setPosition(10, 443);
		final Label test2 = new Label("Dash Dampening: " + current, labelStyle);
		test2.setPosition(10, 475);


		s2.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Slider s = (Slider) actor;
				float f = s.getValue();
				System.out.println("Dash Dampening : " + f);
				somni.setDashDamping(f);
				phobia.setDashDamping(f);
				combined.setDashDamping(f);
				test2.setText("Dash Dampening : " + f);
			}
		});
		stage.addActor(s2);
		sliders[1] = s2;
		labels[1] = test2;

		// Gravity
		current = world.getGravity().y;


		min = current * 2.5f;
		max = current * 0.5f;

		Slider s3 = new Slider(min, max, 0.1f, false, style);
		s3.setValue(current);
		s3.setPosition(10, 386);
		final Label test3 = new Label("Gravity: " + current, labelStyle);
		test3.setPosition(10, 418);

		s3.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Slider s = (Slider) actor;
				float f = s.getValue();
				System.out.println("Gravity: " + f);
				world.setGravity( new Vector2(0,f) );

				test3.setText("Gravity : " + f);
			}
		});
		stage.addActor(s3);
		sliders[2] = s3;
		labels[2] = test3;

		// Jump Force
		current = avatar.getJumpForce();
		max = current * 1.5f;
		min = current * 0.5f;

		Slider s4 = new Slider(min, max, 0.1f, false, style);
		s4.setValue(current);
		s4.setPosition(10, 329);
		final Label test4 = new Label("Jump Force: " + current, labelStyle);
		test4.setPosition(10, 361);

		s4.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Slider s = (Slider) actor;
				float f = s.getValue();
				System.out.println("Jump Force : " + f);
				somni.setJumpForce(f);
				phobia.setJumpForce(f);
				combined.setJumpForce(f);
				test4.setText("Jump Force : " + f);
			}
		});
		stage.addActor(s4);
		sliders[3] = s4;
		labels[3] = test4;

		// Hand Holding Distance
		current = movementController.getHAND_HOLDING_DISTANCE();
		max = current * 1.5f;
		min = current * 0.5f;

		Slider s5 = new Slider(min, max, 0.1f, false, style);
		s5.setValue(current);
		s5.setPosition(10, 272);
		final Label test5 = new Label("Hand Holding Distance: " + current, labelStyle);
		test5.setPosition(10, 304);

		s5.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Slider s = (Slider) actor;
				float f = s.getValue();
				System.out.println("Hand Holding Distance : " + f);
				movementController.setHAND_HOLDING_DISTANCE(f);
				test5.setText("Hand Holding Distance : " + f);
			}
		});
		stage.addActor(s5);
		sliders[4] = s5;
		labels[4] = test5;

		// Dash End Velocity
		current = avatar.getDashEndVelocity();
		max = current * 4f;
		min = current * 0.5f;

		Slider s6 = new Slider(min, max, 0.1f, false, style);
		s6.setValue(current);
		s6.setPosition(10, 215);
		final Label test6 = new Label("Dash End Velocity: " + current, labelStyle);
		test6.setPosition(10, 247);

		s6.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Slider s = (Slider) actor;
				float f = s.getValue();
				System.out.println("Dash End Velocity : " + f);
				somni.setDashEndVelocity(f);
				phobia.setDashEndVelocity(f);
				combined.setDashEndVelocity(f);
				test6.setText("Dash End Velocity : " + f);
			}
		});
		stage.addActor(s6);
		sliders[5] = s6;
		labels[5] = test6;

		// Character Force
		current = avatar.getForce();
		max = current * 1.5f;
		min = current * 0.5f;

		Slider s7 = new Slider(min, max, 0.1f, false, style);
		s7.setValue(current);
		s7.setPosition(10, 158);
		final Label test7 = new Label("Movement Speed : " + current, labelStyle);
		test7.setPosition(10, 190);

		s7.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Slider s = (Slider) actor;
				float f = s.getValue();
				System.out.println("Movement Speed : " + f);
				somni.setCharacterForce(f);
				phobia.setCharacterForce(f);
				combined.setCharacterForce(f);
				test7.setText("Movement Speed : " + f);
			}
		});
		stage.addActor(s7);
		sliders[6] = s7;
		labels[6] = test7;

		Gdx.input.setInputProcessor(stage);

//		s.draw(b, 1.0f);
	}

	public void drawSliders(){
		Batch b = canvas.getBatch();
		for (int i = 0; i < sliders.length; i++) {
			Slider s = sliders[i];
			s.setPosition(camera.position.x - canvas.getWidth()/2.5f, 57*i + camera.position.y - canvas.getHeight()/3f);
			Label l= labels[i];
			l.setPosition(camera.position.x - canvas.getWidth()/2.5f, 57*i + 32 +  + camera.position.y - canvas.getHeight()/3f);

			l.draw(b, 1.0f);
			s.draw(b, 1.0f);
		}
	}

	/**
	 * Creates the pauseButton
	 */
	public void createPauseButton(){
		Table table = new Table();
		gameScreenActive = true;
//		pauseButtonStage = new Stage(new ScreenViewport(camera));
		pauseButton = new Button(bluePauseButton);
		pauseButton.setPosition(camera.position.x+PAUSE_BUTTON_OFFSETX, camera.position.y+PAUSE_BUTTON_OFFSETY);
		pauseButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				setPause(true);
			}
		});
		pauseButton.setSize(PAUSE_BUTTON_WIDTH,PAUSE_BUTTON_HEIGHT);
		table.add(pauseButton);
		pauseButtonStage.addActor(table);
	}

	/**
	 * Draws the pauseButton and resets the position relative to the camera position
	 */
	public void drawPauseButton(){
		Batch b = canvas.getBatch();
		pauseButton.setPosition(camera.position.x+PAUSE_BUTTON_OFFSETX, camera.position.y+PAUSE_BUTTON_OFFSETY);
		pauseButton.draw(b, 1);
	}

	/**
	 * Gather the assets for this controller.
	 *
	 * This method extracts the asset variables from the given asset directory. It
	 * should only be called after the asset directory is completed.
	 *
	 * @param directory	Reference to global asset manager.
	 */
	public void gatherAssets(AssetDirectory directory) {

		avatarTexture  = new TextureRegion(directory.getEntry("platform:Somni_Idle",Texture.class));
		combinedTexture = new TextureRegion(directory.getEntry("platform:somni_phobia_stand",Texture.class));

		// Tiles
		lightTexture = new TextureRegion(directory.getEntry( "shared:solidCloud_light", Texture.class ));
		darkTexture = new TextureRegion(directory.getEntry( "shared:solidCloud_dark", Texture.class ));
		allTexture = new TextureRegion(directory.getEntry( "shared:solidCloud_all", Texture.class ));
		rainLightTexture = new TextureRegion(directory.getEntry( "shared:rain_cloud_light", Texture.class ));
		rainDarkTexture = new TextureRegion(directory.getEntry( "shared:rain_cloud_dark", Texture.class ));
		rainAllTexture = new TextureRegion(directory.getEntry( "shared:rain_cloud_all", Texture.class ));
		lightningLightTexture = new TextureRegion(directory.getEntry( "shared:lightning_cloud_light", Texture.class ));
		lightningDarkTexture = new TextureRegion(directory.getEntry( "shared:lightning_cloud_dark", Texture.class ));
		lightningAllTexture = new TextureRegion(directory.getEntry( "shared:lightning_cloud_all", Texture.class ));
		crumbleLightTexture = new TextureRegion(directory.getEntry( "shared:rain_crumble_light", Texture.class ));
		crumbleDarkTexture = new TextureRegion(directory.getEntry( "shared:rain_crumble_dark", Texture.class ));
		crumbleAllTexture = new TextureRegion(directory.getEntry( "shared:rain_crumble_all", Texture.class ));

		rainLightTextureReduced = new TextureRegion(directory.getEntry( "shared:rain_cloud_light_reduced", Texture.class ));
		rainDarkTextureReduced = new TextureRegion(directory.getEntry( "shared:rain_cloud_dark_reduced", Texture.class ));
		rainAllTextureReduced = new TextureRegion(directory.getEntry( "shared:rain_cloud_all_reduced", Texture.class ));
		lightningLightTextureReduced = new TextureRegion(directory.getEntry( "shared:lightning_cloud_light_reduced", Texture.class ));
		lightningDarkTextureReduced = new TextureRegion(directory.getEntry( "shared:lightning_cloud_dark_reduced", Texture.class ));
		lightningAllTextureReduced = new TextureRegion(directory.getEntry( "shared:lightning_cloud_all_reduced", Texture.class ));
		crumbleLightTextureReduced = new TextureRegion(directory.getEntry( "shared:rain_crumble_light_reduced", Texture.class ));
		crumbleDarkTextureReduced = new TextureRegion(directory.getEntry( "shared:rain_crumble_dark_reduced", Texture.class ));
		crumbleAllTextureReduced = new TextureRegion(directory.getEntry( "shared:rain_crumble_all_reduced", Texture.class ));

		// Tutorial
		tutorial_signs = new TextureRegion[]{
				new TextureRegion(directory.getEntry("tutorial:camera_pan", Texture.class)),      //0
				new TextureRegion(directory.getEntry("tutorial:phobia_dash", Texture.class)),     //1
				new TextureRegion(directory.getEntry("tutorial:phobia_jump", Texture.class)),     //2
				new TextureRegion(directory.getEntry("tutorial:phobia_propel", Texture.class)),   //3
				new TextureRegion(directory.getEntry("tutorial:phobia_walk", Texture.class)),     //4
				new TextureRegion(directory.getEntry("tutorial:somni_dash", Texture.class)),      //5
				new TextureRegion(directory.getEntry("tutorial:somni_jump", Texture.class)),      //6
				new TextureRegion(directory.getEntry("tutorial:somni_propel", Texture.class)),    //7
				new TextureRegion(directory.getEntry("tutorial:somni_walk", Texture.class)),      //8
				new TextureRegion(directory.getEntry("tutorial:spirit_switch", Texture.class)),   //9
				new TextureRegion(directory.getEntry("tutorial:spirit_separate", Texture.class)), //10
				new TextureRegion(directory.getEntry("tutorial:spirit_unify", Texture.class))     //11
		};

		// Base models
		somniTexture  = new TextureRegion(directory.getEntry("platform:somni_stand",Texture.class));
		somniIdleTexture  = new TextureRegion(directory.getEntry("platform:Somni_Idle",Texture.class));
		somniWalkTexture = new TextureRegion(directory.getEntry("platform:somni_walk_cycle",Texture.class));
		somniDashSideTexture = new TextureRegion(directory.getEntry("platform:Somni_Jump_Dash",Texture.class));
		somniDashUpTexture = new TextureRegion(directory.getEntry("platform:Somni_Falling",Texture.class));
		somniFallTexture = new TextureRegion(directory.getEntry("platform:Somni_Falling", Texture.class));

		phobiaTexture = new TextureRegion(directory.getEntry("platform:phobia_stand",Texture.class));
		phobiaIdleTexture  = new TextureRegion(directory.getEntry("platform:Phobia_Idle",Texture.class));
		phobiaWalkTexture = new TextureRegion(directory.getEntry("platform:phobia_walk_cycle",Texture.class));
		phobiaDashSideTexture = new TextureRegion(directory.getEntry("platform:Phobia_Jump_Dash",Texture.class));
		phobiaDashUpTexture = new TextureRegion(directory.getEntry("platform:Phobia_Falling",Texture.class));
		phobiaFallTexture = new TextureRegion(directory.getEntry("platform:Phobia_Falling", Texture.class));


		// Combined models
		somniPhobiaTexture  = new TextureRegion(directory.getEntry("platform:somni_phobia_stand",Texture.class));
		somniPhobiaWalkTexture = new TextureRegion(directory.getEntry("platform:somni_phobia_walk",Texture.class));
		somniPhobiaDashSideTexture = new TextureRegion(directory.getEntry("platform:somni_phobia_dash_side",Texture.class));
		somniPhobiaDashUpTexture = new TextureRegion(directory.getEntry("platform:somni_phobia_dash_up",Texture.class));
		phobiaSomniTexture = new TextureRegion(directory.getEntry("platform:phobia_somni_stand",Texture.class));
		phobiaSomniWalkTexture = new TextureRegion(directory.getEntry("platform:phobia_somni_walk",Texture.class));
		phobiaSomniDashSideTexture = new TextureRegion(directory.getEntry("platform:phobia_somni_dash_side",Texture.class));
		phobiaSomniDashUpTexture = new TextureRegion(directory.getEntry("platform:phobia_somni_dash_up",Texture.class));

		somniPhobiaHandsTexture = new TextureRegion(directory.getEntry("platform:somni_phobia_hands",Texture.class));
		phobiaSomniHandsTexture = new TextureRegion(directory.getEntry("platform:phobia_somni_hands",Texture.class));
		blueRingBigTexture = new TextureRegion(directory.getEntry("platform:blue_ring_big",Texture.class));
		yellowRingBigTexture = new TextureRegion(directory.getEntry("platform:yellow_ring_big",Texture.class));
		blueRingSmallTexture = new TextureRegion(directory.getEntry("platform:blue_ring_small",Texture.class));
		yellowRingSmallTexture = new TextureRegion(directory.getEntry("platform:yellow_ring_small",Texture.class));
		somniHandFrontTexture = new TextureRegion(directory.getEntry("platform:somni_hand_front",Texture.class));
		somniHandBackTexture = new TextureRegion(directory.getEntry("platform:somni_hand_back",Texture.class));
		phobiaHandFrontTexture = new TextureRegion(directory.getEntry("platform:phobia_hand_front",Texture.class));
		phobiaHandBackTexture = new TextureRegion(directory.getEntry("platform:phobia_hand_back",Texture.class));

		backgrounds = new TextureRegion[] {
				new TextureRegion(directory.getEntry("platform:background_light_forest", Texture.class)),
				new TextureRegion(directory.getEntry("platform:background_dark_forest", Texture.class)),
				new TextureRegion(directory.getEntry("platform:background_light_gear", Texture.class)),
				new TextureRegion(directory.getEntry("platform:background_dark_gear", Texture.class)),
				new TextureRegion(directory.getEntry("platform:background_light_dreams", Texture.class)),
				new TextureRegion(directory.getEntry("platform:background_dark_dreams", Texture.class)),
				new TextureRegion(directory.getEntry("platform:background_light_house", Texture.class)),
				new TextureRegion(directory.getEntry("platform:background_dark_house", Texture.class)),
				new TextureRegion(directory.getEntry("platform:background_light_statues", Texture.class)),
				new TextureRegion(directory.getEntry("platform:background_dark_statues", Texture.class)),

//				new TextureRegion(directory.getEntry("platform:animated_background_light_forest", Texture.class)),
//				new TextureRegion(directory.getEntry("platform:animated_background_dark_forest", Texture.class)),
//				new TextureRegion(directory.getEntry("platform:animated_background_light_gear", Texture.class)),
//				new TextureRegion(directory.getEntry("platform:animated_background_dark_gear", Texture.class)),
//				new TextureRegion(directory.getEntry("platform:animated_background_light_dreams", Texture.class)),
//				new TextureRegion(directory.getEntry("platform:animated_background_dark_dreams", Texture.class)),
//				new TextureRegion(directory.getEntry("platform:animated_background_light_house", Texture.class)),
//				new TextureRegion(directory.getEntry("platform:animated_background_dark_house", Texture.class)),
//				new TextureRegion(directory.getEntry("platform:animated_background_light_statues", Texture.class)),
//				new TextureRegion(directory.getEntry("platform:animated_background_dark_statues", Texture.class)),
		};


		TextureRegion [] somnis = {somniIdleTexture,somniWalkTexture,somniDashSideTexture,somniDashUpTexture, somniFallTexture, somniDashUpTexture};
		somnisTexture = somnis;
		TextureRegion [] phobias = {phobiaIdleTexture,phobiaWalkTexture,phobiaDashSideTexture,phobiaDashUpTexture, phobiaFallTexture, phobiaDashUpTexture};
		phobiasTexture = phobias;
		TextureRegion [] somniphobias = {somniPhobiaTexture,somniPhobiaWalkTexture,somniPhobiaDashSideTexture,somniPhobiaDashUpTexture, somniPhobiaDashUpTexture};
		somniphobiasTexture = somniphobias;
		TextureRegion [] phobiasomnis = {phobiaSomniTexture,phobiaSomniWalkTexture,phobiaSomniDashSideTexture,phobiaSomniDashUpTexture, phobiaSomniDashUpTexture};
		phobiasomnisTexture = phobiasomnis;
		TextureRegion [] somniHands = {somniHandFrontTexture, somniHandBackTexture, somniPhobiaHandsTexture};
		somniHandsTextures = somniHands;
		TextureRegion [] phobiaHands = {phobiaHandFrontTexture, phobiaHandBackTexture, phobiaSomniHandsTexture};
		phobiaHandsTextures = phobiaHands;

		animationSpeed = new float[]{0.1f, 0.5f, 0.1f, 0.1f, 0.1f, 0.1f};
		framePixelWidth = new double[]{32, 64, 32, 32, 32, 32};
		offsetsX = new float[]{12, 19, 0, 0, 15, 0};
		offsetsY = new float[]{0, 0, 0, 0, 0, 0};
		secOffsetsX = new float[]{-20, -16, 52, 60, -18, 35, -18};
		secOffsetsY = new float[]{0, 0, -80, -60, 0, -70, 0};
		thirdOffsetsX = new float[]{0, -18, -22, -22, 0, -22,   10, -15, 0, 0, 5, 0,   0, -20, 0, 0, -2, 0};
		thirdOffsetsY = new float[]{0, 0, 0, 0, 0, 0};
		dashAngles = new float[] {0, 0, -1.55f, 0, 0, 3.14f};
		propelAngles = new float[] {0, 0, 0, 1.55f, 0, -1.55f};


		// Setup masking
		circle_mask = new TextureRegion(directory.getEntry("circle_mask",Texture.class));
		Vector2 mask_size = new Vector2(circle_mask.getRegionWidth(), circle_mask.getRegionHeight());
		MIN_MASK_DIMENSIONS = new Vector2(mask_size).scl(mask_shrink_factor);
		maskWidth = MIN_MASK_DIMENSIONS.x;
		maskHeight = MIN_MASK_DIMENSIONS.y;

		sliderBarTexture = directory.getEntry( "platform:sliderbar", Texture.class);
		sliderKnobTexture = directory.getEntry( "platform:sliderknob", Texture.class);

		jumpSound = directory.getEntry( "platform:jump", SoundBuffer.class );
		fireSound = directory.getEntry( "platform:pew", SoundBuffer.class );
		plopSound = directory.getEntry( "platform:plop", SoundBuffer.class );

		constants = directory.getEntry( "constants", JsonValue.class );


		somniTrackPath = directory.getEntry("somniTrack", SoundBuffer.class);
		phobiaTrackPath = directory.getEntry("phobiaTrack", SoundBuffer.class);
		combinedTrackPath = directory.getEntry("combinedTrack", SoundBuffer.class);


//		menu drawables
		orangeUnderline = new TextureRegionDrawable(directory.getEntry("pause_menu:pausemenu_underline_red", Texture.class));
		blueUnderline = new TextureRegionDrawable(directory.getEntry("pause_menu:pausemenu_underline", Texture.class));
		blueRectangle = new TextureRegionDrawable(directory.getEntry("pause_menu:bluerectangle", Texture.class));
		blueExit = new TextureRegionDrawable(directory.getEntry("pause_menu:exit_blue", Texture.class));
		blueResume = new TextureRegionDrawable(directory.getEntry("pause_menu:resume_blue", Texture.class));
		blueRestart = new TextureRegionDrawable(directory.getEntry("pause_menu:restart_blue", Texture.class));
		orangeRectangle = new TextureRegionDrawable(directory.getEntry("pause_menu:orangerectangle", Texture.class));
		orangeExit = new TextureRegionDrawable(directory.getEntry("pause_menu:exit_orange", Texture.class));
		orangeResume = new TextureRegionDrawable(directory.getEntry("pause_menu:resume_orange", Texture.class));
		orangeRestart = new TextureRegionDrawable(directory.getEntry("pause_menu:restart_orange", Texture.class));
		//		Sliders
		orangeSlider = new TextureRegionDrawable(directory.getEntry("pause_menu:slider_orange", Texture.class));
		blueSlider = new TextureRegionDrawable(directory.getEntry("pause_menu:slider_blue", Texture.class));
		orangeKnob = new TextureRegionDrawable(directory.getEntry("pause_menu:knob_orange", Texture.class));
		blueKnob = new TextureRegionDrawable(directory.getEntry("pause_menu:knob_blue", Texture.class));
		orangeSound = new TextureRegionDrawable(directory.getEntry("pause_menu:sound_orange", Texture.class));
		blueSound = new TextureRegionDrawable(directory.getEntry("pause_menu:sound_blue", Texture.class));
		blueMusicNote = new TextureRegionDrawable(directory.getEntry("pause_menu:musicnote_blue", Texture.class));
		orangeMusicNote = new TextureRegionDrawable(directory.getEntry("pause_menu:musicnote_orange", Texture.class));

		blueNext = new TextureRegionDrawable(directory.getEntry("pause_menu:nextblue", Texture.class));
		orangeNext = new TextureRegionDrawable(directory.getEntry("pause_menu:nextorange", Texture.class));

		bluePauseButton = new TextureRegionDrawable(directory.getEntry("pause_menu:pause_button_blue", Texture.class));
		orangePauseButton = new TextureRegionDrawable(directory.getEntry("pause_menu:pause_button_red", Texture.class));

		super.gatherAssets(directory);


	}

	/**
	 * Gather the level JSON for this controller.
	 *
	 * This method extracts the asset variables from the given JSON.
	 *
	 * @param filename	Reference to the level JSON.
	 */
	public void gatherLevelJson(String filename) {
		if(level == 0) { // Get level editor level
			Preferences prefs = GDXRoot.getPreferences();
			if(prefs.contains(filename)) {
				levelAssets = new JsonReader().parse(prefs.getString(filename));
			}
		} else {
			FileHandle file = Gdx.files.internal(filename);
			String text = file.readString();
			levelAssets = new JsonReader().parse(text);//directory.getEntry( String.format("level%d", level), JsonValue.class);
		}
	}

	/**
	 * Gather the level JSON for this controller.
	 *
	 * This method extracts the asset variables from the given asset directory. It
	 * should only be called after the asset directory is completed.
	 *
	 * @param directory	Reference to global asset manager.
	 */
	public void gatherLevelJson(AssetDirectory directory, int world) {
		levelAssets = directory.getEntry( String.format("level%d", level), JsonValue.class);
	}

	/** Returns the current level */
	public int getLevel() {
		return level;
	}

	/** Sets the current level */
	public void setLevel(int level) {
		this.level = Math.max(0, level);
	}
	/**
	 * Resets the status of the game so that we can play again.
	 *
	 * This method disposes of the world and creates a new one.
	 */
	public void reset() {
		gameScreenActive = true;
		Vector2 gravity = new Vector2(world.getGravity() );
		for(Obstacle obj : objects) {
			obj.deactivatePhysics(world);
		}
		for(Obstacle obj : sharedObjects) {
			obj.deactivatePhysics(world);
		}
		for(Obstacle obj : darkObjects) {
			obj.deactivatePhysics(world);
		}
		for(Obstacle obj : lightObjects) {
			obj.deactivatePhysics(world);
		}
//		for (Obstacle obj: movingObjects) {
//			obj.deactivatePhysics(world);
//		}
		objects.clear();
		sharedObjects.clear();
		lightObjects.clear();
		darkObjects.clear();
		movingObjects.clear();
		addQueue.clear();
		world.dispose();
		disposeStages();

		world = new World(gravity,false);
		setComplete(false);
		setFailure(false);
		firstTimeRendered=true;
		populateLevel();

		camera = canvas.getCamera();
		pauseButtonStage = new Stage(new ScreenViewport(camera));
		pauseMenuStage = new Stage(new ScreenViewport(camera));
		stage = new Stage(new ScreenViewport(camera));
		winMenuStage = new Stage(new ScreenViewport(camera));
		failMenuStage = new Stage(new ScreenViewport(camera));

		Camera camera = canvas.getCamera();
		Vector2 leadPos = somni.getPosition();
		float newX = leadPos.x * canvas.PPM;
		newX = Math.min(newX, widthUpperBound);
		newX = Math.max(canvas.getWidth() / 2, newX );
		camera.position.x = newX;

		float newY = leadPos.y * canvas.PPM;
		newY = Math.min(newY, heightUpperBound);
		newY = Math.max(canvas.getHeight() / 2, newY );
		camera.position.y = newY;

		camera.update();

		holdingHands = false;

		movementController = new MovementController(somni, phobia, combined, goalDoor, objects, sharedObjects,
				lightObjects, darkObjects, this);
		movementController.setCurrRainingPlatforms(currRainingPlatforms);
		world.setContactListener(movementController);

		movementController.setAvatar(somni);
		movementController.setLead(somni);

		createModalWindow(camera.position.x, camera.position.y);
		createPauseButton();
		createSliders();
		createFailWindow(camera.position.x, camera.position.y);
		createWinWindow(camera.position.x, camera.position.y);


		platformController.setMovingObjects(movingObjects);
		platformController.setLightObjects(lightObjects);
		platformController.setDarkObjects(darkObjects);
		platformController.setSharedObjects(sharedObjects);
		platformController.setCurrRainingPlatforms(currRainingPlatforms);

		maskLeader = phobia;
		switching = false;
		maskWidth = MIN_MASK_DIMENSIONS.x;
		maskHeight = MIN_MASK_DIMENSIONS.y;
		alphaAmount = 0;

		SoundController.getInstance().play("somniTrack", somniTrackPath, volume, true);
		SoundController.getInstance().setVolume(volume, "somniTrack");
		SoundController.getInstance().play("phobiaTrack", phobiaTrackPath, 0f, true);
		SoundController.getInstance().play("combinedTrack", combinedTrackPath, 0f, true);
	}

//	public void playMusic() {
//
//		if (movementController.getAvatar() == somni) {
//			SoundController.getInstance().play("somni", somniTrack, 1f, true);
//		} else if (movementController.getAvatar() == phobia) {
//			SoundController.getInstance().play("phobia", phobiaTrack, 1f, true);
//		} else {
//			SoundController.getInstance().play("phobia", phobiaTrack, 1f, true);
//		}
//	}

	/**
	 * Checks the path of a platform for validity
	 * @param posX The x position of the platform
	 * @param posY The y position of the platform
	 * @param path The path of the platform
	 * @return Whether or not a platform's path is valid
	 */
	public static boolean hasValidPath(float posX, float posY, float[] path) {
		return path.length > 2 || path[0] != posX || path[1] != posY;
	}

	/**
	 * Lays out the game geography.
	 */
	private void populateLevel() {


		// Setup Goal
		JsonValue goalVal = levelAssets.get("goal");
		float gWidth  = goalTile.getRegionWidth()/scale.x;
		float gHeight = goalTile.getRegionHeight()/scale.y;
		float gX = goalVal.get("pos").getFloat(0) + gWidth / 2;
		float gY = goalVal.get("pos").getFloat(1) + gHeight / 2;
		goalDoor = new BoxObstacle(gX, gY, gWidth, gHeight);
		goalDoor.setBodyType(BodyDef.BodyType.StaticBody);
		goalDoor.setDensity(constants.get("goal").getFloat("density", 0));
		goalDoor.setFriction(constants.get("goal").getFloat("friction", 0));
		goalDoor.setRestitution(constants.get("goal").getFloat("restitution", 0));
		goalDoor.setSensor(true);
		goalDoor.setDrawScale(scale);
		goalDoor.setTexture(goalTile);
		goalDoor.setName("goal");
		addObject(goalDoor);
		addObjectTo(goalDoor, LevelCreator.allTag);

		// Get default values
		JsonValue defaults = constants.get("defaults");
		JsonValue objs = levelAssets.get("objects");

		//group platform constants together for access in following for-loop
		TextureRegion[] xTexture = {lightTexture, darkTexture, allTexture,
			lightningLightTexture, lightningDarkTexture, lightningAllTexture,
			rainLightTexture, rainDarkTexture, rainAllTexture,
				crumbleLightTexture, crumbleDarkTexture, crumbleAllTexture};

		TextureRegion[] reducedXTexture = {lightTexture, darkTexture, allTexture,
				lightningLightTextureReduced, lightningDarkTextureReduced, lightningAllTextureReduced,
				rainLightTextureReduced, rainDarkTextureReduced, rainAllTextureReduced,
				crumbleLightTextureReduced, crumbleDarkTextureReduced, crumbleAllTextureReduced};


		// Setup platforms
		for(int i=0; i < objs.size; i++)
		{
			JsonValue obj = objs.get(i);

			// Get platform attributes
			int platformType = obj.get("type").asInt();
			int property = obj.get("property") == null ?  0: obj.get("property").asInt();
			JsonValue platformArgs = obj.get("positions");
			JsonValue pathsArgs = obj.get("paths");

			for (int j = 0; j < platformArgs.size; j++) {
				float[] bounds = platformArgs.get(j).asFloatArray();
				float x = bounds[0], y = bounds[1], width = bounds[2], height = bounds[3];
				TextureRegion newXTexture;
				TextureRegion crumbleTexture = null;
				Texture originalTexture = null;
				try {
					// temporary - need to refactor asset directory
					JsonValue assetName = obj.get("assetName");
					int assetIndex = assetName.asInt();
					newXTexture = new TextureRegion(tutorial_signs[assetIndex]);
				} catch(Exception e) {
					int platIdx = platformType-1+(property - 1)*3;
					int crumbleIdx = platIdx + 3;
					newXTexture = new TextureRegion(xTexture[platIdx]);
					originalTexture = newXTexture.getTexture();
					// For crumble animation
					if (platIdx > 5) {
						crumbleTexture = new TextureRegion(xTexture[crumbleIdx]);
						crumbleTexture.setRegion(0, 0, width, height);
					}
					// If the platform size is the same as the spritesheet size
					if (originalTexture.getWidth() > 32 && width%(originalTexture.getWidth()/32) == 0) {
						newXTexture = new TextureRegion(reducedXTexture[platIdx]);
						originalTexture = newXTexture.getTexture();
						if (platIdx > 5) {
							crumbleTexture = new TextureRegion(reducedXTexture[crumbleIdx]);
							crumbleTexture.setRegion(0, 0, width, height);
						}
					}
					newXTexture.setRegion(0, 0, width, height);
				}
				PlatformModel platformModel  = new PlatformModel(bounds, platformType, newXTexture, scale,
						defaults.getFloat( "density", 0.0f ), defaults.getFloat( "friction", 0.0f ) ,
						defaults.getFloat( "restitution", 0.0f ), originalTexture, crumbleTexture);
				platformModel.setTag(platformType);
				platformModel.setProperty(property);
				addObject(platformModel);
				addObjectTo(platformModel, platformType);
				//TODO: Moving platforms


				if (pathsArgs != null) {
					float[] paths = pathsArgs.get(j).asFloatArray();

					//** Moving platform if > 1 path or different path from starting position
					if (hasValidPath(x, y, paths)) {
						platformModel.setBodyType(BodyDef.BodyType.KinematicBody);
						movingObjects.add(platformModel);

						PooledList<Vector2> pathList = new PooledList<>();
						for (int k = 0; k < paths.length; k+=2) {
							pathList.add(new Vector2(paths[k], paths[k+1]));
						}
						float velocity = 3;

						platformModel.setGravityScale(0);
						platformModel.setPaths(pathList);
						platformModel.setVelocity(velocity);

						movingObjects.add(platformModel);
					}
				}
			}
		}

		// This world is heavier
		world.setGravity( new Vector2(0,defaults.getFloat("gravity",0)) );

		// Set level background index
		int backgroundTextureIndex = levelAssets.get("background").asInt();
		backgroundLightTexture = backgrounds[backgroundTextureIndex - 1];
		backgroundDarkTexture = backgrounds[backgroundTextureIndex];
		backgroundTexture = backgroundLightTexture;

		// Initialize background animations
		actualBackgroundTexture = backgroundTexture.getTexture();
		backgroundEntirePixelWidth = actualBackgroundTexture.getWidth();
		backgroundNumAnimFrames = (int)(backgroundEntirePixelWidth/backgroundFramePixelWidth);
		backgroundAnimator = new FilmStrip(backgroundTexture,1, backgroundNumAnimFrames, backgroundNumAnimFrames);
		backgroundAnimator.setFrame(0);
		backgroundAnimeframe = 0;
		backgroundOrigin = new Vector2(backgroundAnimator.getRegionWidth()/2.0f, backgroundAnimator.getRegionHeight()/2.0f);

		// Set level bounds
		widthUpperBound = levelAssets.get("dimensions").getInt(0);
		heightUpperBound = levelAssets.get("dimensions").getInt(1);

		// Setup Somni

		JsonValue somniVal = levelAssets.get("somni");
		float sWidth  = somniTexture.getRegionWidth()/scale.x;
		float sHeight = somniTexture.getRegionHeight()/scale.y;
		float sX = somniVal.get("pos").getFloat(0) + sWidth / 2;
		float sY = somniVal.get("pos").getFloat(1) + sHeight / 2;
		somni = new CharacterModel(constants.get("somni"), sX, sY, sWidth, sHeight, platformController.somnif, CharacterModel.LIGHT);
		somni.setDrawScale(scale);
		somni.setTexture(somniIdleTexture);
		somni.setFilterData(platformController.somnif);
		somni.setActive(true);
		addObject(somni);
		addObjectTo(somni, LevelCreator.allTag);


		// Setup Phobia

		JsonValue phobiaVal = levelAssets.get("phobia");
		float pWidth  = phobiaTexture.getRegionWidth()/scale.x;
		float pHeight = phobiaTexture.getRegionHeight()/scale.y;
		float pX = phobiaVal.get("pos").getFloat(0) + pWidth / 2;
		float pY = phobiaVal.get("pos").getFloat(1) + pHeight / 2;
		phobia = new CharacterModel(constants.get("phobia"), pX, pY, pWidth, pHeight, platformController.phobiaf, CharacterModel.DARK);
		phobia.setDrawScale(scale);
		phobia.setTexture(phobiaIdleTexture);
		phobia.setFilterData(platformController.phobiaf);
		addObject(phobia);
		addObjectTo(phobia, LevelCreator.allTag);
		phobia.setActive(true);

		// Setup Combined

		float cWidth  = combinedTexture.getRegionWidth()/scale.x;
		float cHeight = combinedTexture.getRegionHeight()/scale.y;

		combined = new CharacterModel(constants.get("combined"), 0, 0, cWidth, cHeight, platformController.combinedf, CharacterModel.DARK);
		combined.setDrawScale(scale);
		combined.setTexture(somniPhobiaTexture);
		//combined.setTag();
		combined.setFilterData(platformController.combinedf);
		addObject(combined);
		addObjectTo(combined, LevelCreator.allTag);
		combined.setActive(true);

		//Remove combined
		objects.remove(combined);
		sharedObjects.remove(combined);
		combined.setActive(false);

		action = 0;

		Preferences prefs = GDXRoot.getPreferences();
		volume = prefs.contains("volume") ? prefs.getFloat("volume") : defaults.getFloat("volume",
				1.0f);
//		System.out.println(volume);

		platformController.applyFilters(objects);
	}

//	/**
//	 * Returns whether to process the update loop
//	 *
//	 * At the start of the update loop, we check if it is time
//	 * to switch to a new game mode.  If not, the update proceeds
//	 * normally.
//	 *
//	 * @param dt	Number of seconds since last animation frame
//	 *
//	 * @return whether to process the update loop
//	 */
//	public boolean preUpdate(float dt) {
//		if (!super.preUpdate(dt)) {
//			return false;
//		}
//		if (!isFailure() && (somni.getY() < -1 || phobia.getY() < -1 || combined.getY() < -1)) {
//			setFailure(true);
//			return false;
//		}
//
//		return true;
//	}

	//JENNA
	/**
	 * Returns whether to process the update loop
	 *
	 * At the start of the update loop, we check if it is time
	 * to switch to a new game mode.  If not, the update proceeds
	 * normally.
	 *
	 * @param dt	Number of seconds since last animation frame
	 *
	 * @return whether to process the update loop
	 */
	public boolean preUpdate(float dt) {
		if (!super.preUpdate(dt)) {
			return false;
		}
		if (!isFailure() && (somni.getY() < -1 || phobia.getY() < -1 || combined.getY() < -1)) {
			setFailure(true);
			return false;
		}

		if (exitClicked){
			pause();
			ScreenListener listener = getListener();
			gameScreenActive = false;
			setPause(false);
			setFailure(false);
			firstTimeRendered = true;
			listener.exitScreen(this, WorldController.EXIT_LEVEL_SELECT_ENTER);
			setComplete(false);
			exitClicked = false;
			return false;
		}

		if (advanceClicked){
			//JENNA ADVANCE
			pause();
			ScreenListener listener = getListener();
			gameScreenActive = false;
			setPause(false);
			setFailure(false);
			listener.exitScreen(this, WorldController.EXIT_NEXT);
			setComplete(false);
			advanceClicked = false;
		}

		if (resumeClicked){
			setPause(false);
			setFailure(false);
			setComplete(false);
			resumeClicked = false;
		}

		if (restartClicked){
			setPause(false);
			reset();
			restartClicked = false;
		}

		return true;
	}

	//END JENNA

	/**
	 * The core gameplay loop of this world.
	 *
	 * This method contains the specific update code for this mini-game. It does
	 * not handle collisions, as those are managed by the parent class WorldController.
	 * This method is called after input is read, but before collisions are resolved.
	 * The very last thing that it should do is apply forces to the appropriate objects.
	 *
	 * @param dt	Number of seconds since last animation frame
	 */
	public void update(float dt) {
		if (pauseMenuActive() || isComplete() || isFailure()) return;
		//if (pauseMenuActive() || isComplete() || isFailure()) return;
		// fix pause ^^^
		action = movementController.update();
		platformController.update(dt);

		if (InputController.getInstance().didPressEscape()) {
			setPause(true);
		}

		CharacterModel lead = movementController.getLead();
//		somni = movementController.getSomni();
//		phobia = movementController.getPhobia();
		CharacterModel avatar = movementController.getAvatar();
		holdingHands = movementController.isHoldingHands();

		if (movementController.getSwitchedCharacters()) {
			switching = !switching;

		}
		// Increase animation frame of background
		backgroundAnimeframe += backgroundAnimationSpeed;
		if (backgroundAnimeframe >= backgroundNumAnimFrames) {
			backgroundAnimeframe = 0;
		}

		if(holdingHands){
			if(lead == somni){
				// draw somni, phobia, and the hands
				combined.setTexture(somnisTexture[action], animationSpeed[action], framePixelWidth[action], offsetsX[action], offsetsY[action],
						phobiasTexture[action], animationSpeed[action], framePixelWidth[action], secOffsetsX[action], secOffsetsY[action],
						somniPhobiaHandsTexture, thirdOffsetsX[action], thirdOffsetsY[action]);
			}else{
				// draw phobia, somni, and the hands
				combined.setTexture(phobiasTexture[action], animationSpeed[action], framePixelWidth[action], offsetsX[action], offsetsY[action],
						somnisTexture[action], animationSpeed[action], framePixelWidth[action], secOffsetsX[action], secOffsetsY[action],
						phobiaSomniHandsTexture, thirdOffsetsX[action], thirdOffsetsY[action]);
			}
		}
		else{
			if(avatar == somni){
				// draw somni
				if ((action == 2 || action ==3 || action ==5)&& !avatar.justPropelled()) {
					int facing = somni.isFacingRight()? 1:-1;
					//draw somni with small dash ring
					somni.setTexture(somnisTexture[action], animationSpeed[action], framePixelWidth[action], 0, 0,
							yellowRingSmallTexture, 0.2f, 128, 0, -5, facing * dashAngles[action]);
				} else {
					if (movementController.canHoldHands()){
						// somni reaches out hand when phobia within distance
						int f = movementController.faceTowards();
						somni.setTexture(somnisTexture[action], animationSpeed[action], framePixelWidth[action], 0, 0,
								somniHandsTextures[f], thirdOffsetsX[action+6*(f+1)], thirdOffsetsY[action]);
					} else {
						// only draw somni
						somni.setTexture(somnisTexture[action], animationSpeed[action], framePixelWidth[action]);
					}
				}

				// draw phobia
				if (action == 2  && somni.justPropelled()){
					// draw phobia and a propelling hand
					phobia.setTexture(phobiaIdleTexture, animationSpeed[0], framePixelWidth[0], 0, 0,
							blueRingBigTexture, 0.2f, 128, secOffsetsX[action], secOffsetsY[action], propelAngles[action]);
				} else if ((action == 3 || action == 5) && somni.justPropelled()) {
					// draw phobia and an upward propelling hand
					int facing = phobia.isFacingRight()? 1:-1;
					phobia.setTexture(phobiaIdleTexture, animationSpeed[0], framePixelWidth[0], 0, 0,
							blueRingBigTexture, 0.2f, 128, secOffsetsX[action], secOffsetsY[action], facing*propelAngles[action]);
				} else {
					// only draw phobia
					phobia.setTexture(phobiaIdleTexture, animationSpeed[0], framePixelWidth[0]);
				}

			}else{
				// draw the leading character phobia
				if ((action == 2 || action == 3 || action == 5) && !avatar.justPropelled()){
					int facing = phobia.isFacingRight()? 1:-1;
					// draw phobia with small dash ring
					phobia.setTexture(phobiasTexture[action], animationSpeed[action], framePixelWidth[action], 0, 0,
							blueRingSmallTexture, 0.2f, 128, 0, -5, facing*dashAngles[action]);
				} else {
					if (movementController.canHoldHands()){
						// phobia reaches out hand when somni within distance
						int f = movementController.faceTowards();
						phobia.setTexture(phobiasTexture[action], animationSpeed[action], framePixelWidth[action], 0, 0,
								phobiaHandsTextures[f], thirdOffsetsX[action+6*(f+1)], thirdOffsetsY[action]);
					} else {
						// only draw phobia
						phobia.setTexture(phobiasTexture[action], animationSpeed[action], framePixelWidth[action]);
					}
				}

				// draw the idle character somni
				if (action == 2  && phobia.justPropelled()){
					// draw somni with a propelling hand
					somni.setTexture(somniIdleTexture, animationSpeed[0], framePixelWidth[0],0, 0,
							yellowRingBigTexture, 0.2f, 128, secOffsetsX[action], secOffsetsY[action], propelAngles[action]);
				} else if ( (action == 3 || action ==5 ) && phobia.justPropelled()) {
					int facing = somni.isFacingRight()? 1:-1;
					somni.setTexture(somniIdleTexture, animationSpeed[0], framePixelWidth[0], 0, 0,
							yellowRingBigTexture, 0.2f, 128, secOffsetsX[action], secOffsetsY[action], facing*propelAngles[action]);
				} else {
					// only draw somni
					somni.setTexture(somniIdleTexture, animationSpeed[0], framePixelWidth[0]);
				}

			}
			movementController.setJustSeparated(false);
			movementController.setJustPropelled(false);
		}

		// Set camera position bounded by the canvas size
		camera = canvas.getCamera();

		if (cameraCenter == null) {
			cameraCenter = new Vector2(avatar.getX(), avatar.getY());
			cameraCenter.x = avatar.getX();
			cameraCenter.y = avatar.getY();
		}

		float PAN_DISTANCE = 100f;
		float CAMERA_SPEED = 10f;

		float newX = avatar.getX() * canvas.PPM;
		float camX = InputController.getInstance().getCameraHorizontal();
		if (camX != 0) {
			panMovement.x = camX * CAMERA_SPEED * canvas.PPM;
		} else {
			panMovement.x = 0;
		}

		float camY = InputController.getInstance().getCameraVertical();
		if (camY != 0) {
			panMovement.y = camY * CAMERA_SPEED * canvas.PPM;
		} else {
			panMovement.y = 0;
		}

		float newY = avatar.getY() * canvas.PPM;

		newX = Math.min(newX, widthUpperBound);
		newX = Math.max(canvas.getWidth() / 2, newX);
		float displacementX = newX - camera.position.x;
		float lerpDisplacementX = Math.abs(displacementX + panMovement.x) < PAN_DISTANCE * canvas.PPM ?
				displacementX + panMovement.x : displacementX;
		camera.position.x += lerpDisplacementX * LERP * dt;

		newY = Math.min(newY, heightUpperBound);
		newY = Math.max(canvas.getHeight() / 2, newY);
		float displacementY = newY - camera.position.y;
		float lerpDisplacementY = Math.abs(displacementY + panMovement.y) < PAN_DISTANCE * canvas.PPM ?
				displacementY + panMovement.y : displacementY;
		camera.position.y += lerpDisplacementY * LERP * dt;

		camera.update();

	}

	private void updateMaskPosition(float maskWidth, float maskHeight, CharacterModel character) {
		character = holdingHands ? combined : character;
		float maskX = character.getX() * canvas.PPM + character.getWidth() / 2 - maskWidth / 2;
		float maskY = character.getY() * canvas.PPM + character.getHeight() / 2 - maskHeight / 2;
		maskOrigin.set(maskX, maskY);
	}

	/**
	 * Draws the necessary textures to mask properly.
	 * @param mask The image to mask with
	 * @param background The optional background to apply along with the mask
	 * @param cameraX The x-coord for the camera origin
	 * @param cameraY The y-coord for the camera origin
	 * @param maskWidth The width of the mask
	 * @param maskHeight The height of the mask
	 * @param character The character to center the mask on
	 */
	private void drawMask(TextureRegion mask, Texture background, float cameraX, float cameraY, float maskWidth,
						  float maskHeight, CharacterModel character) {
		updateMaskPosition(maskWidth, maskHeight, character);
		canvas.beginCustom(GameCanvas.BlendState.OPAQUE, GameCanvas.ChannelState.ALPHA);
		if(background != null) {
			canvas.draw(background, Color.CLEAR, cameraX, cameraY, canvas.getWidth(), canvas.getHeight());
		}
		canvas.draw(mask, Color.WHITE, maskOrigin.x, maskOrigin.y, maskWidth, maskHeight);
		canvas.endCustom();
	}

	/**
	 * Writes the necessary textures for the character's realm rift into the FrameBuffer.
	 * @param cameraX The x-coord for the camera origin
	 * @param cameraY The y-coord for the camera origin
	 * @param character The character whose environment is being written
	 */
	private void writeCharacterRift(float cameraX, float cameraY, CharacterModel character) {
		fbo.begin();
		canvas.beginCustom(GameCanvas.BlendState.NO_PREMULT, GameCanvas.ChannelState.ALL);
		TextureRegion background = character.equals(somni) ? backgroundLightTexture : backgroundDarkTexture;
		backgroundAnimator.setRegion(background);
		backgroundAnimator.setFrame((int)backgroundAnimeframe);
		canvas.draw(backgroundAnimator, Color.WHITE, cameraX, cameraY, canvas.getWidth(), canvas.getHeight());
		canvas.endCustom();
		fbo.end();
	}

	/**
	 * Writes the necessary textures for the character's platforms into the FrameBuffer
	 * @param character The character whose platforms are being written
	 */
	private void writeCharacterPlatform(CharacterModel character, boolean alpha) {
		PooledList<Obstacle> objects = character.equals(somni) ? lightObjects : darkObjects;
		fbo.begin();
		for(Obstacle obj : objects) {
			canvas.beginCustom(GameCanvas.BlendState.NO_PREMULT, GameCanvas.ChannelState.ALL);
			if(alpha) {
				alphaWhite.a = 1 - alphaAmount;
				((SimpleObstacle) obj).drawWithTint(canvas, alphaWhite);
			} else {
				obj.draw(canvas);
			}
			canvas.endCustom();
		}
		fbo.end();
	}

	/**
	 * Draws fading platforms for the given `character`
	 * @param cameraX The x-coord for the camera origin
	 * @param cameraY The y-coord for the camera origin
	 * @param character The character whose fading platforms are being drawn
	 */
	private void drawFadePlatforms(float cameraX, float cameraY, CharacterModel character) {
		fbo.begin();
		canvas.clear();
		canvas.beginCustom(GameCanvas.BlendState.NO_PREMULT, GameCanvas.ChannelState.ALL);
//		canvas.draw(backgroundTexture, Color.WHITE, cameraX, cameraY, canvas.getWidth(), canvas.getHeight());
		backgroundAnimator.setRegion(backgroundTexture);
		backgroundAnimator.setFrame((int)backgroundAnimeframe);
		canvas.draw(backgroundAnimator, Color.WHITE, cameraX, cameraY, canvas.getWidth(), canvas.getHeight());
		canvas.endCustom();
		fbo.end();
		drawMask(circle_mask, alpha_background, cameraX, cameraY, maskWidth, maskHeight, maskLeader);
		writeCharacterPlatform(character,false);
		drawFrameBufferContents(GameCanvas.BlendState.ANTI_MASK);
	}

	/**
	 * Draws the FrameBuffer's contents
	 * @param blend The blend state to use when drawing
	 */
	private void drawFrameBufferContents(GameCanvas.BlendState blend) {
		canvas.beginCustom(blend, GameCanvas.ChannelState.ALL);
		Texture fbo_t = fbo.getColorBufferTexture();
		float fbo_x = camera.position.x - canvas.getWidth() / 2;
		float fbo_y = camera.position.y - canvas.getHeight() / 2 + fbo_t.getHeight();
		canvas.draw(fbo_t, Color.WHITE, fbo_x, fbo_y, fbo_t.getWidth(), -fbo_t.getHeight());
		canvas.endCustom();
	}


	/**
	 * Draws everything necessary for the given `character`
	 * @param cameraX The x-coord for the camera origin
	 * @param cameraY The y-coord for the camera origin
	 * @param maskWidth The width of the mask
	 * @param maskHeight The height of the mask
	 * @param platformKind The kind of platform to draw (1 if regular, 2 if fading, otherwise no platform at all)
	 * @param character The character to center the mask on
	 */
	private void drawSpiritObjects(float cameraX, float cameraY, float maskWidth, float maskHeight,
								   int platformKind, CharacterModel character) {
		// Start with the mask to properly draw things within a spirit's realm
		drawMask(circle_mask, alpha_background, cameraX, cameraY, maskWidth, maskHeight, character);

		// Now write a spirit's rift into the FrameBuffer (FB), i.e. give the mask a background to look like the
		// spirit's realm
		writeCharacterRift(cameraX, cameraY, character);

		// Now write the platforms contained in the spirit's realm into the FB - these will be contained within the
		// realm
		switch(platformKind) {
			case 1:
				// Draw platforms normally
				writeCharacterPlatform(character,  false);
				break;
			case 2:
				// Draw platforms with alpha
				writeCharacterPlatform(character,  true );
				break;
			default:
				break;
		}

		// Finally, draw the contents of the FB - this allows us to apply more than one blend in our masked textures,
		// i.e. using a platform with alpha that must be alpha composited (NO_PREMULT) first and THEN masked (MASK)
		drawFrameBufferContents(GameCanvas.BlendState.MASK);
	}

	/**
	 * Creates a rectangular texture
	 * @param width The width of the rectangle
	 * @param height The height of the rectangle
	 * @return The rectangular texture
	 */
	private Texture createRectangularTexture(int width, int height) {
		Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
		pixmap.setColor(Color.CLEAR);
		pixmap.fillRectangle(0,0, pixmap.getWidth(), pixmap.getHeight());
		return new Texture(pixmap);
	}

	//Vector2 maskInset = new Vector2(1500, 1500);

	/**
	 * Helps with bounds checking for when the rift has covered the entirety of the camera bounds
	 * @param cameraX The x-coord for the camera origin
	 * @param cameraY The y-coord for the camera origin
	 * @param maskWidth The width of the mask
	 * @param maskHeight The height of the mask
	 * @param character The character to center the mask on
	 * @returns Whether or not the rift is covering the camera bounds
	 */
	private boolean riftCoversCameraBounds(float cameraX, float cameraY, float maskWidth, float maskHeight,
										   CharacterModel character) {
		updateMaskPosition(maskWidth, maskHeight, character);
		boolean coversLeft = maskOrigin.x + maskOffset < cameraX;
		boolean coversRight = maskOrigin.x + maskWidth - maskOffset > cameraX + canvas.getWidth();
		boolean coversBottom = maskOrigin.y + maskOffset < cameraY;
		boolean coversTop = maskOrigin.y + maskHeight - maskOffset > cameraY + canvas.getHeight();
		return coversLeft && coversRight && coversBottom && coversTop;
	}

	/**
	 * Draw the physics objects together with foreground and background
	 *
	 * This is completely overridden to support custom background and foreground art.
	 *
	 * @param dt Timing values from parent loop
	 */
	public void draw(float dt) {

		CharacterModel lead = movementController.getLead();
		canvas.clear();

		float cameraX = camera.position.x - canvas.getWidth() / 2;
		float cameraY = camera.position.y - canvas.getHeight() / 2;

		// Create the frame buffer if uninitialized
		if (fbo == null) {
			fbo = new FrameBuffer(Pixmap.Format.RGBA8888, canvas.getWidth(), canvas.getHeight(), false);
		}

		// Draw background
		canvas.beginCustom(GameCanvas.BlendState.NO_PREMULT, GameCanvas.ChannelState.ALL);
//		canvas.draw(backgroundTexture, Color.WHITE, cameraX, cameraY, canvas.getWidth(), canvas.getHeight());
		backgroundAnimator.setRegion(backgroundTexture);
		backgroundAnimator.setFrame((int)backgroundAnimeframe);
		canvas.draw(backgroundAnimator, Color.WHITE, cameraX, cameraY, canvas.getWidth(), canvas.getHeight());
		canvas.endCustom();

		// Create alpha background if uninitialized
		if (alpha_background == null) {
			alpha_background = createRectangularTexture(canvas.getWidth(), canvas.getHeight());
		}

		CharacterModel follower = lead.equals(phobia) ? somni : phobia;

		// Check if switching and update mask drawing
		if (switching) {
			if (!holdingHands) {
				// Apply fade effect for follower (fading away)
				drawFadePlatforms(cameraX, cameraY, follower);
			}

			// Draw mask for the mask leader
			drawSpiritObjects(cameraX, cameraY, maskWidth, maskHeight, !holdingHands ? 1 : 0, maskLeader);

			// Draw mask for the follower while switching
			drawSpiritObjects(cameraX, cameraY, MIN_MASK_DIMENSIONS.x, MIN_MASK_DIMENSIONS.y, 1, follower);

			// Draw mask for the mask leader to cover follower's
			drawSpiritObjects(cameraX, cameraY, MIN_MASK_DIMENSIONS.x, MIN_MASK_DIMENSIONS.y, 1,
					maskLeader);

			// Increase mask size
			maskWidth += INCREMENT_AMOUNT;
			maskHeight += INCREMENT_AMOUNT;
			if (riftCoversCameraBounds(cameraX, cameraY, maskWidth, maskHeight, maskLeader)) {
				maskWidth = MIN_MASK_DIMENSIONS.x;
				maskHeight = MIN_MASK_DIMENSIONS.y;
				switching = false;
				maskLeader = follower;
				backgroundTexture = backgroundTexture.equals(backgroundLightTexture) ? backgroundDarkTexture :
						backgroundLightTexture;
				backgroundAnimator.setRegion(backgroundTexture);
			}
		} else {
			// Check if shrinking
			boolean shrinking = maskWidth > MIN_MASK_DIMENSIONS.x || maskHeight > MIN_MASK_DIMENSIONS.y;
			if (shrinking) {
				// Apply fade away effect for the lead (fading in)
				if (!holdingHands) {
					drawFadePlatforms(cameraX, cameraY, lead);
				}

				// Make sure the rift is still drawn (to carry over the effect)
				drawSpiritObjects(cameraX, cameraY, maskWidth, maskHeight, !holdingHands ? 1 : 0, maskLeader);

				// Draw mask for the lead while shrinking
				drawSpiritObjects(cameraX, cameraY, MIN_MASK_DIMENSIONS.x, MIN_MASK_DIMENSIONS.y,
						!holdingHands ? 1 : 0, lead);

				// Draw mask for the mask leader to cover follower's
				drawSpiritObjects(cameraX, cameraY, MIN_MASK_DIMENSIONS.x, MIN_MASK_DIMENSIONS.y, 1,
						maskLeader);
			} else {
				// Draw lead platform
				if (!holdingHands) {
					canvas.begin();
					for (Obstacle obj : lead.equals(somni) ? lightObjects : darkObjects) {
						obj.draw(canvas);
					}
					canvas.end();
				}

				// Draw mask leader's mask AFTER drawing lead platforms (prevents popping platforms)
				drawSpiritObjects(cameraX, cameraY, maskWidth, maskHeight, 2, maskLeader);

				// Draw mask for the lead to cover maskLeader's
				drawSpiritObjects(cameraX, cameraY, MIN_MASK_DIMENSIONS.x, MIN_MASK_DIMENSIONS.y, 1, lead);

			}

			// Decrease mask size to minimum
			maskWidth -= maskWidth <= MIN_MASK_DIMENSIONS.x ? 0 : INCREMENT_AMOUNT;
			maskHeight -= maskHeight <= MIN_MASK_DIMENSIONS.y ? 0 : INCREMENT_AMOUNT;
		}

		// Draw light and dark platforms if holding hands
		if (holdingHands) {
			canvas.begin();
			for (Obstacle obj : lead.equals(somni) ? lightObjects : darkObjects) {
				obj.draw(canvas);
			}
			canvas.end();
			alphaAmount = alphaAmount + alphaIncrement >= 1 ? 1 : alphaAmount + alphaIncrement;
		} else {
			alphaAmount = alphaAmount - alphaIncrement <= 0 ? 0 : alphaAmount - alphaIncrement;
			;
		}
		alphaWhite.a = alphaAmount;
		canvas.begin();
		for (Obstacle obj : follower.equals(somni) ? lightObjects : darkObjects) {
//			((SimpleObstacle) obj).drawWithTint(canvas, alphaWhite);
			((PlatformModel) obj).drawWithTint(canvas, alphaWhite);
		}
		canvas.end();

		// Draw shared platforms
		canvas.begin();
		for (Obstacle obj : sharedObjects) {

			// Ignore characters which we draw separately
			if (!(obj instanceof CharacterModel)) {
				(obj).draw(canvas);
			}
		}
		canvas.end();

		// Draw current model
		canvas.begin();
		if (holdingHands) {
			combined.draw(canvas, Color.WHITE);
		} else {
			alphaWhite.a = 0.5f;
			follower.draw(canvas, alphaWhite);
			lead.draw(canvas, Color.WHITE);
		}
		canvas.end();

		// Draw sliders if active
		canvas.begin();
		if (tes == 0) {
			createSliders();
			tes = 1;
		}

		if (slidersActive()) {
			stage.draw();
			stage.act();
			displayFont.getData().setScale(.3f, .3f);
			labelStyle.fontColor = lead == phobia ? Color.BLACK : Color.WHITE;
			drawSliders();
			Gdx.input.setInputProcessor(stage);
		}
		canvas.end();

		// Draw pauseMenu
		canvas.begin();

//		if (firstTimeRendered) {
//			createModalWindow(camera.position.x, camera.position.y);
//			firstTimeRendered = false;
//		}
//		if (firstTimeRenderedWinMenu) {
//			createWinWindow(camera.position.x, camera.position.y);
//			firstTimeRenderedWinMenu = false;
//		}
//
//		if (firstTimeRenderedFailMenu) {
//			createFailWindow(camera.position.x, camera.position.y);
//			firstTimeRenderedFailMenu = false;
//		}

		if (pauseMenuActive()) {
			setPositionPauseMenu();
			firstPosition = true;
			pauseMenuStage.draw();
			pauseMenuStage.act(dt);

			if (exitButton.isOver()) {
				underline.setSize(exitButton.getWidth() + UNDERLINE_WIDTH_OFFSET, exitButton.getHeight() + UNDERLINE_HEIGHT_OFFSET);
				underline.setPosition(exitButton.getX() + UNDERLINE_OFFSETX, exitButton.getY() + UNDERLINE_OFFSETY);
				underline.setVisible(true);
			} else if (resumeButton.isOver()) {
				underline.setSize(resumeButton.getWidth() + UNDERLINE_WIDTH_OFFSET, resumeButton.getHeight() + UNDERLINE_HEIGHT_OFFSET);
				underline.setPosition(resumeButton.getX() + UNDERLINE_OFFSETX, resumeButton.getY() + UNDERLINE_OFFSETY);
				underline.setVisible(true);
			} else if (restartButton.isOver()) {
				underline.setSize(restartButton.getWidth() + UNDERLINE_WIDTH_OFFSET, restartButton.getHeight() + UNDERLINE_HEIGHT_OFFSET);
				underline.setPosition(restartButton.getX() + UNDERLINE_OFFSETX, restartButton.getY() + UNDERLINE_OFFSETY);
				underline.setVisible(true);
			} else {
				underline.setVisible(false);
			}
			if (movementController.getAvatar() == somni || movementController.getLead() == somni) {
				pauseMenu.setBackground(blueRectangle);
				exitButton.getStyle().up = blueExit;
				resumeButton.getStyle().up = blueResume;
				restartButton.getStyle().up = blueRestart;
				underline.setDrawable(blueUnderline);
				musicIcon.setDrawable(blueMusicNote);
				soundIcon.setDrawable(blueSound);
				sliderMusic.getStyle().background = blueSlider;
				sliderMusic.getStyle().knob = blueKnob;
				sliderSound.getStyle().background = blueSlider;
				sliderSound.getStyle().knob = blueKnob;
			} else {
				pauseMenu.setBackground(orangeRectangle);
				exitButton.getStyle().up = orangeExit;
				resumeButton.getStyle().up = orangeResume;
				restartButton.getStyle().up = orangeRestart;
				underline.setDrawable(orangeUnderline);
				musicIcon.setDrawable(orangeMusicNote);
				soundIcon.setDrawable(orangeSound);
				sliderMusic.getStyle().background = orangeSlider;
				sliderMusic.getStyle().knob = orangeKnob;
				sliderSound.getStyle().background = orangeSlider;
				sliderSound.getStyle().knob = orangeKnob;
			}

			Gdx.input.setInputProcessor(pauseMenuStage);
		}
		canvas.end();

		canvas.begin();
		if (firstTimeRenderedPauseButton) {
			createPauseButton();
			firstTimeRenderedPauseButton = false;
		} else {
			if (movementController.getAvatar() == somni || movementController.getLead() == somni) {
				pauseButton.getStyle().up = bluePauseButton;
			} else {
				pauseButton.getStyle().up = orangePauseButton;
			}
			drawPauseButton();
		}

		if (!pauseMenuActive() && gameScreenActive && !slidersActive()) {
			Gdx.input.setInputProcessor(pauseButtonStage);
		}
		canvas.end();

		// Draw debug if active
		if (isDebug()) {
			canvas.beginDebug();
			for (Obstacle obj : sharedObjects) {
				obj.drawDebug(canvas);
			}
			canvas.endDebug();
			canvas.beginDebug();
			for (Obstacle obj : lightObjects) {
				obj.drawDebug(canvas);
			}
			canvas.endDebug();
			canvas.beginDebug();
			for (Obstacle obj : darkObjects) {
				obj.drawDebug(canvas);
			}
			canvas.endDebug();

		}

		// Draw final message when level ends
		// Draw final message when level ends
		//JENNA
		if (isComplete() && !isFailure()) {
			canvas.begin();
			if (isComplete()) {
				setPositionMenu(winMenu);
				winMenuStage.draw();
				winMenuStage.act(dt);

				if (exitButtonWin.isOver()) {
					underlineWinMenu.setSize(exitButtonWin.getWidth() + UNDERLINE_WIDTH_OFFSET, exitButtonWin.getHeight() + UNDERLINE_HEIGHT_OFFSET);
					underlineWinMenu.setPosition(exitButtonWin.getX() + UNDERLINE_OFFSETX, exitButtonWin.getY() + UNDERLINE_OFFSETY);
					underlineWinMenu.setVisible(true);
				} else if (advanceButton.isOver()) {
					underlineWinMenu.setSize(advanceButton.getWidth() + UNDERLINE_WIDTH_OFFSET, advanceButton.getHeight() + UNDERLINE_HEIGHT_OFFSET);
					underlineWinMenu.setPosition(advanceButton.getX() + UNDERLINE_OFFSETX, advanceButton.getY() + UNDERLINE_OFFSETY);
					underlineWinMenu.setVisible(true);
				} else {
					underlineWinMenu.setVisible(false);
				}

				if (movementController.getAvatar() == somni || movementController.getLead() == somni) {
					winMenu.setBackground(blueRectangle);
					exitButtonWin.getStyle().up = blueExit;
					advanceButton.getStyle().up = blueNext;
					underlineWinMenu.setDrawable(blueUnderline);
				} else {
					winMenu.setBackground(orangeRectangle);
					exitButtonWin.getStyle().up = orangeExit;
					advanceButton.getStyle().up = orangeNext;
					underlineWinMenu.setDrawable(orangeUnderline);
				}

				Gdx.input.setInputProcessor(winMenuStage);
			}
			canvas.end();


		} else if (isFailure()) {

			canvas.begin();
			if (isFailure()) {
				setPositionMenu(failMenu);
				failMenuStage.draw();
				failMenuStage.act(dt);

				if (exitButtonFail.isOver()) {
					underlineFailMenu.setSize(exitButtonFail.getWidth() + UNDERLINE_WIDTH_OFFSET, exitButtonFail.getHeight() + UNDERLINE_HEIGHT_OFFSET);
					underlineFailMenu.setPosition(exitButtonFail.getX() + UNDERLINE_OFFSETX, exitButtonFail.getY() + UNDERLINE_OFFSETY);
					underlineFailMenu.setVisible(true);
				} else if (restartButtonFail.isOver()) {
					underlineFailMenu.setSize(restartButtonFail.getWidth() + UNDERLINE_WIDTH_OFFSET, restartButtonFail.getHeight() + UNDERLINE_HEIGHT_OFFSET);
					underlineFailMenu.setPosition(restartButtonFail.getX() + UNDERLINE_OFFSETX, restartButtonFail.getY() + UNDERLINE_OFFSETY);
					underlineFailMenu.setVisible(true);
				} else {
					underlineFailMenu.setVisible(false);
				}

				if (movementController.getAvatar() == somni || movementController.getLead() == somni) {
					failMenu.setBackground(blueRectangle);
					exitButtonFail.getStyle().up = blueExit;
					restartButtonFail.getStyle().up = blueRestart;
					underlineFailMenu.setDrawable(blueUnderline);
				} else {
					failMenu.setBackground(orangeRectangle);
					exitButtonFail.getStyle().up = orangeExit;
					restartButtonFail.getStyle().up = orangeRestart;
					underlineFailMenu.setDrawable(orangeUnderline);
				}

				Gdx.input.setInputProcessor(failMenuStage);
			}
			canvas.end();


		}
	}


	//END JENNA


	/** Unused ContactListener method */
	public void postSolve(Contact contact, ContactImpulse impulse) {}
	/** Unused ContactListener method */
	public void preSolve(Contact contact, Manifold oldManifold) {}

	/**
	 * Called when the Screen is paused.
	 *
	 * We need this method to stop all sounds when we pause.
	 * Pausing happens when we switch game modes.
	 */
	public void pause() {
		if (jumpSound.isPlaying( jumpId )) {
			jumpSound.stop(jumpId);
		}
		if (plopSound.isPlaying( plopId )) {
			plopSound.stop(plopId);
		}
		if (fireSound.isPlaying( fireId )) {
			fireSound.stop(fireId);
		}
	}

	/**
	 * Adds objects to their respective lists
	 * @param obj obstacle to add
	 * @param l index
	 */
	private void addObjectTo(Obstacle obj, int l) {
		assert inBounds(obj) : "Object is not in bounds";
		if (l == LevelCreator.allTag) {
			sharedObjects.add(obj);
			//obj.activatePhysics(world);
		}
		else if (l == LevelCreator.lightTag) {
			lightObjects.add(obj);
			//obj.activatePhysics(world);
		}else if (l == LevelCreator.darkTag) {
			darkObjects.add(obj);
			//obj.activatePhysics(world);
		}
	}

	public void disposeStages() {
		pauseMenuStage.dispose();
		pauseButtonStage.dispose();
		winMenuStage.dispose();
		failMenuStage.dispose();
		stage.dispose();
	}

	public void beginRaining(PlatformModel platform) {

	}


}