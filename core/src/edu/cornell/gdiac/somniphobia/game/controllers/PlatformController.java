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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.graphics.OrthographicCamera;

import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.audio.SoundBuffer;
import edu.cornell.gdiac.somniphobia.game.models.CharacterModel;
import edu.cornell.gdiac.somniphobia.game.models.PlatformModel;
import edu.cornell.gdiac.util.*;
import edu.cornell.gdiac.somniphobia.*;
import edu.cornell.gdiac.somniphobia.obstacle.*;

import java.awt.*;

/**
 * Gameplay specific controller for the platformer game.
 *
 * You will notice that asset loading is not done with static methods this time.
 * Instance asset loading makes it easier to process our game modes in a loop, which
 * is much more scalable. However, we still want the assets themselves to be static.
 * This is the purpose of our AssetState variable; it ensures that multiple instances
 * place nicely with the static assets.
 */
public class PlatformController extends WorldController {
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
	/** Texture asset for dark background*/
	private TextureRegion backgroundDarkTexture;
	/** Texture asset for light background*/
	private TextureRegion backgroundLightTexture;
	/** Texture asset for background*/
	private TextureRegion backgroundTexture;
	/** Texture asset list for somni*/
	private TextureRegion [] somnisTexture;
	/** Texture asset list for phobia*/
	private TextureRegion [] phobiasTexture;
	/** Texture asset list for somniphobia*/
	private TextureRegion [] somniphobiasTexture;
	/** Texture asset list for phobiasomni*/
	private TextureRegion [] phobiasomnisTexture;
	/** Texture asset list for phobiasomni*/
	private float[] animationSpeed;
	private double[] framePixelWidth;
	/** Texture for slider bars*/
	private Texture sliderBarTexture;
	private Texture sliderKnobTexture;
	/** Texture for masking */
	private TextureRegion circle_mask;
	private Texture alpha_background;


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
	/** Dimensions for the mask when at its smallest */
	Vector2 MIN_MASK_DIMENSIONS;
	/** Max size for the mask to reach to extend off the screen TODO: replace with bounds checking*/
	float MAX_MASK_SIZE;
	/** Amount to increase and decrease rift mask size with */
	float INCREMENT_AMOUNT;
	/** Current width and height of the mask */
	float maskWidth, maskHeight;
	/** Whether or not the mask is in the process of switching*/
	boolean switching;
	/** Whether or not the mask is shrinking (switch occurred early on) */
	boolean shrinking;
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

	Label.LabelStyle labelStyle;
	private Slider [] sliders;
	private Label [] labels;

	public Widget sliderMenu;

	public int tes = 0; // <-- enoch please don't do this

	// WASD Camera Variables

	private Vector2 cameraCenter;
	private int cameraDelay = 0;



	/**
	 * Creates and initialize a new instance of the platformer game
	 *
	 * The game has default gravity and other settings
	 */
	public PlatformController() {

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
	}

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


		Stage stage = new Stage(new ScreenViewport(camera));
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

		s.draw(b, 1.0f);
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
		lightTexture = new TextureRegion(directory.getEntry( "shared:light", Texture.class ));
		darkTexture = new TextureRegion(directory.getEntry( "shared:dark", Texture.class ));
		allTexture = new TextureRegion(directory.getEntry( "shared:all", Texture.class ));

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
		backgroundDarkTexture = new TextureRegion(directory.getEntry("platform:background_dark",Texture.class));
		backgroundLightTexture = new TextureRegion(directory.getEntry("platform:background_light",Texture.class));
		backgroundTexture = backgroundLightTexture;

		TextureRegion [] somnis = {somniIdleTexture,somniWalkTexture,somniDashSideTexture,somniDashUpTexture, somniFallTexture};
		somnisTexture = somnis;
		TextureRegion [] phobias = {phobiaIdleTexture,phobiaWalkTexture,phobiaDashSideTexture,phobiaDashUpTexture, phobiaFallTexture};
		phobiasTexture = phobias;
		TextureRegion [] somniphobias = {somniPhobiaTexture,somniPhobiaWalkTexture,somniPhobiaDashSideTexture,somniPhobiaDashUpTexture, somniPhobiaDashUpTexture};
		somniphobiasTexture = somniphobias;
		TextureRegion [] phobiasomnis = {phobiaSomniTexture,phobiaSomniWalkTexture,phobiaSomniDashSideTexture,phobiaSomniDashUpTexture, phobiaSomniDashUpTexture};
		phobiasomnisTexture = phobiasomnis;

		animationSpeed = new float[]{0.1f, 0.5f, 0.1f, 0.1f, 0.1f};
		framePixelWidth = new double[]{32, 64, 32, 32, 32};

		// Setup masking
		circle_mask = new TextureRegion(directory.getEntry("circle_mask",Texture.class));
		Vector2 mask_size = new Vector2(circle_mask.getRegionWidth(), circle_mask.getRegionHeight());
		MIN_MASK_DIMENSIONS = new Vector2(mask_size).scl(0.125f);
		maskWidth = MIN_MASK_DIMENSIONS.x;
		maskHeight = MIN_MASK_DIMENSIONS.y;
		MAX_MASK_SIZE = MIN_MASK_DIMENSIONS.x * 22.5f;
		INCREMENT_AMOUNT = 50;

		sliderBarTexture = directory.getEntry( "platform:sliderbar", Texture.class);
		sliderKnobTexture = directory.getEntry( "platform:sliderknob", Texture.class);

		jumpSound = directory.getEntry( "platform:jump", SoundBuffer.class );
		fireSound = directory.getEntry( "platform:pew", SoundBuffer.class );
		plopSound = directory.getEntry( "platform:plop", SoundBuffer.class );

		constants = directory.getEntry( "constants", JsonValue.class );
		super.gatherAssets(directory);
	}

	/**
	 * Gather the level JSON for this controller.
	 *
	 * This method extracts the asset variables from the given asset directory. It
	 * should only be called after the asset directory is completed.
	 *
	 * @param directory	Reference to global asset manager.
	 */
	public void gatherLevelJson(AssetDirectory directory) {
		levelAssets = directory.getEntry( String.format("level%d", level), JsonValue.class);
	}

	/** Returns the current level */
	public int getLevel() {
		return level;
	}

	/** Sets the current level */
	public void setLevel(int level) {
		int newLevel = Math.min(level, 19); // TODO: Figure out how to retrieve MAX_LEVEL from `jsons` size in assets
		newLevel = Math.max(0, newLevel);
		this.level = newLevel;
	}
	/**
	 * Resets the status of the game so that we can play again.
	 *
	 * This method disposes of the world and creates a new one.
	 */
	public void reset() {
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
		objects.clear();
		sharedObjects.clear();
		lightObjects.clear();
		darkObjects.clear();
		addQueue.clear();
		world.dispose();

		world = new World(gravity,false);
		setComplete(false);
		setFailure(false);
		populateLevel();

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
		backgroundTexture = backgroundLightTexture;

		movementController = new MovementController(somni, phobia, combined, goalDoor, objects, sharedObjects, this);
		world.setContactListener(movementController);

		movementController.setAvatar(somni);
		movementController.setLead(somni);

		maskLeader = phobia;
		switching = false;
		maskWidth = MIN_MASK_DIMENSIONS.x;
		maskHeight = MIN_MASK_DIMENSIONS.y;
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
		TextureRegion[] xTexture = {lightTexture, darkTexture, allTexture};


		// Setup platforms
		for(int i=0; i < objs.size; i++)
		{
			JsonValue obj = objs.get(i);

			// Determine platform type
			String platformType = obj.get("type").asString();
			int selector = -1;
			switch (platformType) {
				case "light": selector = LevelCreator.lightTag; break;
				case "dark": selector = LevelCreator.darkTag; break;
				case "all": selector = LevelCreator.allTag; break;
				default: selector = -1; break;
			}

			// Apply platform properties
			String[] properties = obj.get("properties").asStringArray();
			for(String property: properties) {
				// TODO: Harming & crumbling platforms
			}

			// Apply platform behaviors
			String[] behaviors = obj.get("behaviors").asStringArray();
			for(String behavior: behaviors) {
				// TODO: Wandering & chasing platforms
			}

			// Setup platforms
			JsonValue platformArgs = obj.get("positions");
			for (int j = 0; j < platformArgs.size; j++) {
				float[] bounds = platformArgs.get(j).asFloatArray();
				float x = bounds[0], y = bounds[1], width = bounds[2], height = bounds[3];
				TextureRegion newXTexture = new TextureRegion(xTexture[selector]);
				newXTexture.setRegion(x, y, x + width, y + height);
				PlatformModel platformModel  = new PlatformModel(bounds, selector, newXTexture, scale,
						defaults.getFloat( "density", 0.0f ), defaults.getFloat( "friction", 0.0f ) ,
						defaults.getFloat( "restitution", 0.0f ));
				platformModel.setTag(selector);
				addObject(platformModel);
				addObjectTo(platformModel, selector);
			}
		}

		// This world is heavier
		world.setGravity( new Vector2(0,defaults.getFloat("gravity",0)) );

		// Set level bounds
		widthUpperBound = levelAssets.get("dimensions").getInt(0);
		heightUpperBound = levelAssets.get("dimensions").getInt(1);

		// Setup Somni

		JsonValue somniVal = levelAssets.get("somni");
		float sWidth  = somniTexture.getRegionWidth()/scale.x;
		float sHeight = somniTexture.getRegionHeight()/scale.y;
		float sX = somniVal.get("pos").getFloat(0) + sWidth / 2;
		float sY = somniVal.get("pos").getFloat(1) + sHeight / 2;
		somni = new CharacterModel(constants.get("somni"), sX, sY, sWidth, sHeight, platController.somnif, CharacterModel.LIGHT);
		somni.setDrawScale(scale);
		somni.setTexture(somniIdleTexture);
		somni.setFilterData(platController.somnif);
		somni.setActive(true);
		addObject(somni);
		addObjectTo(somni, LevelCreator.allTag);


		// Setup Phobia

		JsonValue phobiaVal = levelAssets.get("phobia");
		float pWidth  = phobiaTexture.getRegionWidth()/scale.x;
		float pHeight = phobiaTexture.getRegionHeight()/scale.y;
		float pX = phobiaVal.get("pos").getFloat(0) + pWidth / 2;
		float pY = phobiaVal.get("pos").getFloat(1) + pHeight / 2;
		phobia = new CharacterModel(constants.get("phobia"), pX, pY, pWidth, pHeight, platController.phobiaf, CharacterModel.DARK);
		phobia.setDrawScale(scale);
		phobia.setTexture(phobiaIdleTexture);
		phobia.setFilterData(platController.phobiaf);
		addObject(phobia);
		addObjectTo(phobia, LevelCreator.allTag);
		phobia.setActive(true);

		// Setup Combined

		float cWidth  = combinedTexture.getRegionWidth()/scale.x;
		float cHeight = combinedTexture.getRegionHeight()/scale.y;

		combined = new CharacterModel(constants.get("combined"), 0, 0, cWidth, cHeight, platController.combinedf, CharacterModel.DARK);
		combined.setDrawScale(scale);
		combined.setTexture(somniPhobiaTexture);
		//combined.setTag();
		combined.setFilterData(platController.combinedf);
		addObject(combined);
		addObjectTo(combined, LevelCreator.allTag);
		combined.setActive(true);

		//Remove combined
		objects.remove(combined);
		sharedObjects.remove(combined);
		combined.setActive(false);

		action = 0;

		volume = constants.getFloat("volume", 1.0f);
		platController.applyFilters(objects);
	}

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

		return true;
	}

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

		action = movementController.update();

		CharacterModel lead = movementController.getLead();
//		somni = movementController.getSomni();
//		phobia = movementController.getPhobia();
		CharacterModel avatar = movementController.getAvatar();
		holdingHands = movementController.isHoldingHands();

		if (movementController.getSwitchedCharacters()) {
			switching = !switching;
		}

		if(holdingHands){
            if(lead == somni){
                combined.setTexture(somniphobiasTexture[action]);
            }else{
                combined.setTexture(phobiasomnisTexture[action]);
            }
        }
        else{
            if(lead == somni){
                somni.setTexture(somnisTexture[action], animationSpeed[action], framePixelWidth[action]);
                phobia.setTexture(phobiaIdleTexture, animationSpeed[0], framePixelWidth[0]);
            }else{
                phobia.setTexture(phobiasTexture[action], animationSpeed[action], framePixelWidth[action]);
				somni.setTexture(somniIdleTexture, animationSpeed[0], framePixelWidth[0]);
            }
        }

		// Set camera position bounded by the canvas size
		camera = canvas.getCamera();

        if(cameraCenter == null) {
        	cameraCenter = new Vector2(avatar.getX(), avatar.getY());
			cameraCenter.x = avatar.getX();
			cameraCenter.y = avatar.getY();
		}

        float PAN_DISTANCE = 100f;
        float CAMERA_SPEED = 10f;

		float newX = avatar.getX() * canvas.PPM;
		float camX = InputController.getInstance().getCameraHorizontal();
		if(camX != 0) {
			panMovement.x = camX * CAMERA_SPEED * canvas.PPM;
		} else {
			panMovement.x = 0;
		}

		float camY = InputController.getInstance().getCameraVertical();
		if(camY != 0) {
			panMovement.y = camY * CAMERA_SPEED * canvas.PPM;
		} else {
			panMovement.y = 0;
		}

		float newY = avatar.getY() * canvas.PPM;
		//float displacementFactor = camera.frustum.sphereInFrustumWithoutNearFar(newX, newY, 0, PAN_RADIUS) ?
	//			1 : 0;

		newX = Math.min(newX, widthUpperBound);
		newX = Math.max(canvas.getWidth() / 2, newX );
		float displacementX = newX - camera.position.x;
		//panMovement.x +=  camX * CAMERA_SPEED * canvas.PPM;
		//float lerpDisplacementX = (displacementX + panMovement.x) * displacementFactor;
		float lerpDisplacementX = Math.abs(displacementX + panMovement.x) < PAN_DISTANCE * canvas.PPM ? displacementX + panMovement.x : displacementX;
		camera.position.x += lerpDisplacementX * LERP * dt;

		newY = Math.min(newY, heightUpperBound);
		newY = Math.max(canvas.getHeight() / 2, newY );
		float displacementY = newY - camera.position.y;
		//panMovement.y += camY * CAMERA_SPEED * canvas.PPM;
		//float lerpDisplacementY = (displacementY + panMovement.y) * displacementFactor;
		float lerpDisplacementY = Math.abs(displacementY + panMovement.y) < PAN_DISTANCE * canvas.PPM ? displacementY + panMovement.y : displacementY;
		camera.position.y += lerpDisplacementY * LERP * dt;

		camera.update();


	}


	/**
	 * Draws the necessary textures to mask properly.
	 * @param cameraX The x-coord for the camera origin
	 * @param cameraY The y-coord for the camera origin
	 * @param maskWidth The width of the mask
	 * @param maskHeight The height of the mask
	 * @param character The character to center the mask on
	 */
	public void drawMask(float cameraX, float cameraY, float maskWidth, float maskHeight, CharacterModel character) {
		character = holdingHands ? combined : character;
		float leadCenterX = character.getX() * canvas.PPM + character.getWidth() / 2 - maskWidth / 2;
		float leadCenterY = character.getY() * canvas.PPM + character.getHeight() / 2 - maskHeight / 2;
		canvas.beginCustom(GameCanvas.BlendState.OPAQUE, GameCanvas.ChannelState.ALPHA);
		if(alpha_background == null) {
			Pixmap pixmap=new Pixmap(canvas.getWidth(), canvas.getHeight(), Pixmap.Format.RGBA8888);
			pixmap.setColor(Color.CLEAR);
			pixmap.fillRectangle(0,0, pixmap.getWidth(), pixmap.getHeight());
			alpha_background = new Texture(pixmap);
		}
		canvas.draw(alpha_background, Color.WHITE, cameraX, cameraY, canvas.getWidth(), canvas.getHeight());
		canvas.draw(circle_mask, Color.WHITE, leadCenterX, leadCenterY, maskWidth, maskHeight);
		canvas.endCustom();
	}

	/**
	 * Draws the necessary textures for the character's realm rift.
	 * @param cameraX The x-coord for the camera origin
	 * @param cameraY The y-coord for the camera origin
	 * @param character The character whose environment is being drawn
	 */
	public void drawCharacterRift(float cameraX, float cameraY, CharacterModel character) {
		canvas.beginCustom(GameCanvas.BlendState.NO_PREMULT_DST, GameCanvas.ChannelState.ALL);
		TextureRegion background = character.equals(somni) ? backgroundLightTexture : backgroundDarkTexture;
		canvas.draw(background, Color.WHITE, cameraX, cameraY, canvas.getWidth(), canvas.getHeight());
		canvas.endCustom();
	}

	/**
	 * Draws the necessary textures for the character's platforms.
	 * @param character The character whose environment is being drawn
	 */
	public void drawCharacterPlatform(CharacterModel character) {
		canvas.beginCustom(GameCanvas.BlendState.NO_PREMULT_DST, GameCanvas.ChannelState.ALL);
		PooledList<Obstacle> objects = character.equals(somni) ? lightObjects : darkObjects;
		for(Obstacle obj : objects) {
			obj.draw(canvas);
		}
		canvas.endCustom();
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
//		CharacterModel maskLeader = movementController.getMaskLeader();
//		CharacterModel maskLeader = movementController.getMaskLeader();
		canvas.clear();


		float cameraX = camera.position.x - canvas.getWidth() / 2;
		float cameraY = camera.position.y - canvas.getHeight() / 2;

		// Draw background
		canvas.beginCustom(GameCanvas.BlendState.NO_PREMULT_DST, GameCanvas.ChannelState.ALL);
		canvas.draw(backgroundTexture, Color.WHITE, cameraX, cameraY, canvas.getWidth(), canvas.getHeight());
		canvas.endCustom();

		drawMask(cameraX, cameraY, maskWidth, maskHeight, maskLeader);
		drawCharacterRift(cameraX, cameraY, maskLeader);
		drawCharacterPlatform(maskLeader);

		CharacterModel follower = lead.equals(phobia) ? somni : phobia;
		// Check if switching and update mask drawing
		if(switching) {
			maskWidth += maskWidth >= MAX_MASK_SIZE ? 0 : INCREMENT_AMOUNT;
			maskHeight += maskHeight >= MAX_MASK_SIZE ? 0 : INCREMENT_AMOUNT;
			if(maskWidth >= MAX_MASK_SIZE) {
				maskWidth = MIN_MASK_DIMENSIONS.x;
				maskHeight = MIN_MASK_DIMENSIONS.y;
				switching = false;

				maskLeader = follower;
//				movementController.setMaskLeader(follower);
				//System.out.println(follower.equals(somni) ? "Somni" : "Phobia");
				backgroundTexture = backgroundTexture.equals(backgroundLightTexture) ? backgroundDarkTexture :
						backgroundLightTexture;
			}
			drawMask(cameraX, cameraY, MIN_MASK_DIMENSIONS.x, MIN_MASK_DIMENSIONS.y, follower);
			drawCharacterRift(cameraX, cameraY, follower);
			drawCharacterPlatform(follower);
		} else {
			/*if(maskWidth == MIN)
			if(shrinking) {
				drawMask(cameraX, cameraY, maskWidth, maskHeight, maskLeader);
				drawCharacterRift(cameraX, cameraY, maskLeader);
				drawCharacterPlatform(maskLeader);
			}*/

			// Draw lead platform
			canvas.begin();
			for(Obstacle obj : lead.equals(somni) ? lightObjects : darkObjects) {
				obj.draw(canvas);
			}
			canvas.end();

			// Draw follower platforms if holding hands
			canvas.begin();
			if(holdingHands) {
				for(Obstacle obj : lead.equals(somni) ? darkObjects : lightObjects) {
					obj.draw(canvas);
				}
			}
			canvas.end();
			maskWidth -= maskWidth <= MIN_MASK_DIMENSIONS.x ? 0 : INCREMENT_AMOUNT;
			maskHeight -= maskHeight <= MIN_MASK_DIMENSIONS.y ? 0 : INCREMENT_AMOUNT;
		}
		// Draw shared platforms
		canvas.begin();
		for(Obstacle obj : sharedObjects) {
			// Ignore characters which we draw separately
			if (!(obj instanceof CharacterModel)) {
				obj.draw(canvas);
			}
		}
		canvas.end();

		// Draw current model
		canvas.begin();
		if(holdingHands) {
			combined.draw(canvas);
		} else {
			follower.draw(canvas);
			lead.draw(canvas);
		}
		canvas.end();

		// Draw sliders if active
		canvas.begin();
		if (slidersActive()) {
			if (tes == 0) {
				createSliders();
				tes = 1;
			} else {
				displayFont.getData().setScale(.3f, .3f);
				labelStyle.fontColor = lead == phobia? Color.BLACK: Color.WHITE;


				drawSliders();
			}
		}
		canvas.end();

		// Draw debug if active
		if (isDebug()) {
			canvas.beginDebug();
			for(Obstacle obj : sharedObjects) {
				obj.drawDebug(canvas);
			}
			canvas.endDebug();
			canvas.beginDebug();
			for(Obstacle obj : lightObjects) {
				obj.drawDebug(canvas);
			}
			canvas.endDebug();
			canvas.beginDebug();
			for(Obstacle obj : darkObjects) {
				obj.drawDebug(canvas);
			}
			canvas.endDebug();

		}

		// Draw final message when level ends
		if (isComplete() && !isFailure()) {
			displayFont.setColor(Color.YELLOW);
			canvas.begin(); // DO NOT SCALE
			displayFont.getData().setScale(1f, 1f);

			canvas.drawTextCameraCentered("VICTORY!", displayFont, camera.position.x, camera.position.y);
			canvas.end();
		} else if (isFailure()) {
			displayFont.setColor(Color.RED);
			canvas.begin(); // DO NOT SCALE
			displayFont.getData().setScale(1f, 1f);

			canvas.drawTextCameraCentered("FAILURE!", displayFont, camera.position.x, camera.position.y);
			canvas.end();
		}
	}

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
	 * adds objects to correct list
	 * 0 for shared
	 * 1 for light
	 * else for dark
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


}