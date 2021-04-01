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
public class PlatformController extends WorldController implements ContactListener {
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
	/** Texture asset for Somni's Walk*/
	private TextureRegion somniWalkTexture;
	/** Texture asset for Somni's Dash side*/
	private TextureRegion somniDashSideTexture;
	/** Texture asset for Somni's Dash up*/
	private TextureRegion somniDashUpTexture;
	/** Texture asset for phobia*/
	private TextureRegion phobiaTexture;
	/** Texture asset for Somni's Walk*/
	private TextureRegion phobiaWalkTexture;
	/** Texture asset for Somni's Dash side*/
	private TextureRegion phobiaDashSideTexture;
	/** Texture asset for Somni's Dash up*/
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

	// Physics objects for the game
	/** Physics constants for initialization */
	private JsonValue constants;
	/** Reference to the active character avatar */
	private CharacterModel avatar;

	/** Reference to Somni DudeModel*/
	private CharacterModel somni;
	/** Reference to Phobia DudeModel*/
	private CharacterModel phobia;
	/** Reference to leading DudeModel*/
	private CharacterModel lead;
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

	private int sharedtag = 0;
	private int lighttag = 1;
	private int darktag = 2;

	private boolean lightclear = false;
	private boolean darkclear = false;
	private boolean sharedclear = false;
	private boolean allclear = false;

	/** Are characters currently holding hands */
	private boolean holdingHands;

	/** Level */
	private int level;

	/** Camera stuff */
	private float widthUpperBound, heightUpperBound;
	private float LERP = 2f;

	private float HAND_HOLDING_DISTANCE = 2f;

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
	/** The character to perform the mask effect from */
	CharacterModel maskLeader;

	/** Mark set to handle more sophisticated collision callbacks */
	protected ObjectSet<Fixture> lightSensorFixtures;
	protected ObjectSet<Fixture> darkSensorFixtures;

	protected ObjectSet<Fixture> combinedSensorFixtures;
	// Platform logic
	/** This values so light only interacts with light and dark only interacts with dark*/
	private final short CATEGORY_LPLAT = 0x0001;  //0000000000000001
	private final short CATEGORY_DPLAT = 0x0002;  //0000000000000010
	private final short CATEGORY_SOMNI = 0x0004;  //0000000000000100
	private final short CATEGORY_PHOBIA = 0x0008;	   	  //0000000000001000
	private final short CATEGORY_COMBINED = 0x0010; 	  //0000000000010000
	private final short CATEGORY_ALLPLAT = 0x0020;

	private final short MASK_LPLAT = CATEGORY_SOMNI | CATEGORY_COMBINED; //Collides with all

	private final short MASK_DPLAT = CATEGORY_PHOBIA | CATEGORY_COMBINED;

	private final short MASK_SOMNI = CATEGORY_LPLAT | CATEGORY_ALLPLAT;
	private final short MASK_PHOBIA = CATEGORY_DPLAT | CATEGORY_ALLPLAT;
	private final short MASK_COMBINED = CATEGORY_DPLAT | CATEGORY_LPLAT | CATEGORY_ALLPLAT;
	private final short MASK_ALLPLAT = CATEGORY_SOMNI | CATEGORY_PHOBIA | CATEGORY_COMBINED;


	Label.LabelStyle labelStyle;
	private Slider [] sliders;
	private Label [] labels;

	public Widget sliderMenu;

	public int tes = 0;



	/**
	 * Creates and initialize a new instance of the platformer game
	 *
	 * The game has default gravity and other settings
	 */
	public PlatformController(int level) {

		super(DEFAULT_WIDTH,DEFAULT_HEIGHT,DEFAULT_GRAVITY);
		setDebug(false);
		setComplete(false);
		setFailure(false);
		world.setContactListener(this);
		lightSensorFixtures = new ObjectSet<Fixture>();
		darkSensorFixtures = new ObjectSet<Fixture>();
		combinedSensorFixtures = new ObjectSet<Fixture>();
		holdingHands = false;
		this.level = level;
		widthUpperBound = 0;
		heightUpperBound = 0;
	}


	/**
	 * Creates sliders to adjust game constants.
	 */
	public void createSliders() {
		sliders = new Slider[7];
		labels = new Label[7];


		Stage stage = new Stage(new ScreenViewport());
//		Table table= new Table();
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

		//Dash Velocity
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

		//Dash Dampening
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

		//Dash Dampening
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

		//Jump Force
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

		//Hand Holding Distance
		current = HAND_HOLDING_DISTANCE;
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
				HAND_HOLDING_DISTANCE = f;
				test5.setText("Hand Holding Distance : " + f);
			}
		});
		stage.addActor(s5);
		sliders[4] = s5;
		labels[4] = test5;

		//Dash End Velocity
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

		//Character Force
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
			s.setPosition(camera.position.x - canvas.getWidth()/2.5f, s.getY());
			Label l= labels[i];
			l.setPosition(camera.position.x - canvas.getWidth()/2.5f, l.getY());

			l.draw(b, 1.0f);
			s.draw(b, 1.0f);
		}
	}

	public void applySliders(){
		// 0 Dash Velocity, 1 Dash Distance, 2 Dash Dampening, 3 Jump Force,
		// 4 Hand Holding Distance, 5 Character Friction, 6 Character force
		Slider s = sliders[0];
		float f = s.getValue();
		System.out.println("Dash Velocity : " + f);
		somni.setDashVelocity(f);
		phobia.setDashVelocity(f);
		combined.setDashVelocity(f);

		s = sliders[1];
		f = s.getValue();
		System.out.println("Dash Dampening : " + f);
		somni.setDashDamping(f);
		phobia.setDashDamping(f);
		combined.setDashDamping(f);

//		s = sliders[2];
//		f = s.getValue();
//		System.out.println("Dash Dampening : " + f);
//		somni.setDashDistance(f);
//		phobia.setDashDistance(f);
//		combined.setDashDistance(f);
//
//		s = sliders[3];
//		f = s.getValue();
//		System.out.println("Jump Force : " + f);
//		somni.setDashDistance(f);
//		phobia.setDashDistance(f);
//		combined.setDashDistance(f);
//
//		s = sliders[4];
//		f = s.getValue();
//		System.out.println("Hand Holding Distance : " + f);
//		somni.setDashDistance(f);
//		phobia.setDashDistance(f);
//		combined.setDashDistance(f);
//
//		s = sliders[5];
//		f = s.getValue();
//		System.out.println("Character Friction : " + f);
//		somni.setDashDistance(f);
//		phobia.setDashDistance(f);
//		combined.setDashDistance(f);
//
//		s = sliders[6];
//		f = s.getValue();
//		System.out.println("Character Force : " + f);
//		somni.setDashDistance(f);
//		phobia.setDashDistance(f);
//		combined.setDashDistance(f);

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
		avatarTexture  = new TextureRegion(directory.getEntry("platform:somni_stand",Texture.class));
		combinedTexture = new TextureRegion(directory.getEntry("platform:somni_stand",Texture.class));

		// Tiles
		lightTexture = new TextureRegion(directory.getEntry( "shared:placeholder_light", Texture.class ));
		darkTexture = new TextureRegion(directory.getEntry( "shared:placeholder_dark", Texture.class ));
		allTexture = new TextureRegion(directory.getEntry( "shared:placeholder_all", Texture.class ));

		// Base models
		somniTexture  = new TextureRegion(directory.getEntry("platform:somni_stand",Texture.class));
		somniWalkTexture = new TextureRegion(directory.getEntry("platform:somni_walk",Texture.class));
		somniDashSideTexture = new TextureRegion(directory.getEntry("platform:somni_dash_side",Texture.class));
		somniDashUpTexture = new TextureRegion(directory.getEntry("platform:somni_dash_up",Texture.class));
		phobiaTexture = new TextureRegion(directory.getEntry("platform:phobia_stand",Texture.class));
		phobiaWalkTexture = new TextureRegion(directory.getEntry("platform:phobia_walk",Texture.class));
		phobiaDashSideTexture = new TextureRegion(directory.getEntry("platform:phobia_dash_side",Texture.class));
		phobiaDashUpTexture = new TextureRegion(directory.getEntry("platform:phobia_dash_up",Texture.class));

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

		TextureRegion [] somnis = {somniTexture,somniWalkTexture,somniDashSideTexture,somniDashUpTexture};
		somnisTexture = somnis;
		TextureRegion [] phobias = {phobiaTexture,phobiaWalkTexture,phobiaDashSideTexture,phobiaDashUpTexture};
		phobiasTexture = phobias;
		TextureRegion [] somniphobias = {somniPhobiaTexture,somniPhobiaWalkTexture,somniPhobiaDashSideTexture,somniPhobiaDashUpTexture};
		somniphobiasTexture = somniphobias;
		TextureRegion [] phobiasomnis = {phobiaSomniTexture,phobiaSomniWalkTexture,phobiaSomniDashSideTexture,phobiaSomniDashUpTexture};
		phobiasomnisTexture = phobiasomnis;

		somnisTexture = new TextureRegion[]{somniTexture,somniWalkTexture,somniDashSideTexture,somniDashUpTexture};
		phobiasTexture = new TextureRegion[]{phobiaTexture,phobiaWalkTexture,phobiaDashSideTexture,phobiaDashUpTexture};
		somniphobiasTexture = new TextureRegion[]{somniPhobiaTexture,somniPhobiaWalkTexture,somniPhobiaDashSideTexture,somniPhobiaDashUpTexture};
		phobiasomnisTexture = new TextureRegion[]{phobiaSomniTexture,phobiaSomniWalkTexture,phobiaSomniDashSideTexture,phobiaSomniDashUpTexture};

		// Setup masking
		circle_mask = new TextureRegion(directory.getEntry("circle_mask",Texture.class));
		Vector2 mask_size = new Vector2(circle_mask.getRegionWidth(), circle_mask.getRegionHeight());
		MIN_MASK_DIMENSIONS = new Vector2(mask_size).scl(0.125f);
		maskWidth = MIN_MASK_DIMENSIONS.x;
		maskHeight = MIN_MASK_DIMENSIONS.y;
		MAX_MASK_SIZE = MIN_MASK_DIMENSIONS.x * 22.5f;
		INCREMENT_AMOUNT = 50;

		AssetDirectory internal = new AssetDirectory( "loading.json" );
		internal.loadAssets();
		internal.finishLoading();

		sliderBarTexture = directory.getEntry( "platform:sliderbar", Texture.class);
		sliderKnobTexture = directory.getEntry( "platform:sliderknob", Texture.class);

		jumpSound = directory.getEntry( "platform:jump", SoundBuffer.class );
		fireSound = directory.getEntry( "platform:pew", SoundBuffer.class );
		plopSound = directory.getEntry( "platform:plop", SoundBuffer.class );

		constants = directory.getEntry( "platform:constants", JsonValue.class );
		super.gatherAssets(directory);
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

		holdingHands = false;
		backgroundTexture = backgroundLightTexture;
		avatar = phobia;
		lead = phobia;
		maskLeader = somni;

		world = new World(gravity,false);
		world.setContactListener(this);
		setComplete(false);
		setFailure(false);
		populateLevel(level);
	}

	/**
	 * Lays out the game geography.
	 */
	private void populateLevel(int level) {

		// Add level goal
		float dwidth  = goalTile.getRegionWidth()/scale.x;
		float dheight = goalTile.getRegionHeight()/scale.y;

		//create filters
		Filter lightplatf = new Filter();
		lightplatf.categoryBits = CATEGORY_LPLAT;
		lightplatf.maskBits = MASK_LPLAT;
		Filter darkplatf = new Filter();
		darkplatf.categoryBits = CATEGORY_DPLAT;
		darkplatf.maskBits = MASK_DPLAT;
		Filter somnif = new Filter();
		somnif.categoryBits = CATEGORY_SOMNI;
		somnif.maskBits = MASK_SOMNI;
//		somniplatf.groupIndex = 011;
		Filter phobiaf = new Filter();
		phobiaf.categoryBits = CATEGORY_PHOBIA;
		phobiaf.maskBits = MASK_PHOBIA;
//		phobiaplatf.groupIndex = 011;
		Filter combinedf = new Filter();
		combinedf.categoryBits = CATEGORY_COMBINED;
		combinedf.maskBits = MASK_COMBINED;
		Filter allf = new Filter();
		allf.categoryBits = CATEGORY_ALLPLAT;
		allf.maskBits = MASK_ALLPLAT;
//		allf.groupIndex = 011;

//		allf.categoryBits = CATEGORY_COMBINED;
//		allf.maskBits = MASK_COMBINED;
		JsonValue goal = constants.get("goalL" + level);
		JsonValue goalpos = goal.get("pos");
		goalDoor = new BoxObstacle(goalpos.getFloat(0),goalpos.getFloat(1),dwidth,dheight);
		goalDoor.setBodyType(BodyDef.BodyType.StaticBody);
		goalDoor.setDensity(goal.getFloat("density", 0));
		goalDoor.setFriction(goal.getFloat("friction", 0));
		goalDoor.setRestitution(goal.getFloat("restitution", 0));
		goalDoor.setSensor(true);
		goalDoor.setDrawScale(scale);
		goalDoor.setTexture(goalTile);
		goalDoor.setName("goal");
		addObject(goalDoor);
		addObjectTo(goalDoor, sharedtag);

	    JsonValue defaults = constants.get("defaults");

		String lightPlat = "lightL" + level;
		JsonValue lightPlatJson = constants.get("lightL" + level);
		String darkPlat = "darkL" + level;
		JsonValue darkPlatJson = constants.get("darkL" + level);
		String grayPlat = "grayL" + level;
		JsonValue grayPlatJson = constants.get("grayL" + level);

		// Light platform
		if (lightPlatJson != null) {
			for (int jj = 0; jj < lightPlatJson.size; jj++) {
				BoxObstacle obj;
				float[] bounds = lightPlatJson.get(jj).asFloatArray();
				float width = bounds[2]-bounds[0];
				float height = bounds[5]-bounds[1];
				obj = new BoxObstacle(bounds[0] + width / 2, bounds[1] + height / 2, width, height);
				obj.setBodyType(BodyDef.BodyType.StaticBody);
				obj.setDensity(defaults.getFloat( "density", 0.0f ));
				obj.setFriction(defaults.getFloat( "friction", 0.0f ));
				obj.setRestitution(defaults.getFloat( "restitution", 0.0f ));
				obj.setDrawScale(scale);
				TextureRegion newLightTexture = new TextureRegion(lightTexture);
				newLightTexture.setRegion(bounds[0], bounds[1], bounds[4], bounds[5]);
				obj.setTexture(newLightTexture);
				obj.setName(lightPlat+jj);
				obj.setFilterData(lightplatf);
				addObject(obj);
				addObjectTo(obj, lighttag);
			}
		}

		// Dark platform
		if (darkPlatJson != null) {
			for (int jj = 0; jj < darkPlatJson.size; jj++) {
				BoxObstacle obj;
				float[] bounds = darkPlatJson.get(jj).asFloatArray();
				float width = bounds[2]-bounds[0];
				float height = bounds[5]-bounds[1];
				obj = new BoxObstacle(bounds[0] + width / 2, bounds[1] + height / 2, width, height);
				obj.setBodyType(BodyDef.BodyType.StaticBody);
				obj.setDensity(defaults.getFloat( "density", 0.0f ));
				obj.setFriction(defaults.getFloat( "friction", 0.0f ));
				obj.setRestitution(defaults.getFloat( "restitution", 0.0f ));
				obj.setDrawScale(scale);
				TextureRegion newDarkTexture = new TextureRegion(darkTexture);
				newDarkTexture.setRegion(bounds[0], bounds[1], bounds[4], bounds[5]);
				obj.setTexture(newDarkTexture);
				obj.setName(darkPlat+jj);
				obj.setFilterData(darkplatf);
				addObject(obj);
				addObjectTo(obj, darktag);
			}
		}

		if (grayPlatJson != null) {
			// Gray platform
			for (int jj = 0; jj < grayPlatJson.size; jj++) {
				BoxObstacle obj;
				float[] bounds = grayPlatJson.get(jj).asFloatArray();
				float width = bounds[2]-bounds[0];
				float height = bounds[5]-bounds[1];
				obj = new BoxObstacle(bounds[0] + width / 2, bounds[1] + height / 2, width, height);
				obj.setBodyType(BodyDef.BodyType.StaticBody);
				obj.setDensity(defaults.getFloat( "density", 0.0f ));
				obj.setFriction(defaults.getFloat( "friction", 0.0f ));
				obj.setRestitution(defaults.getFloat( "restitution", 0.0f ));
				obj.setDrawScale(scale);
				TextureRegion newAllTexture = new TextureRegion(allTexture);
				newAllTexture.setRegion(bounds[0], bounds[1], bounds[4], bounds[5]);
				obj.setTexture(newAllTexture);
				obj.setName(grayPlat+jj);
				obj.setFilterData(allf);
				addObject(obj);
				addObjectTo(obj, sharedtag);
			}
		}


	    // This world is heavier
		world.setGravity( new Vector2(0,defaults.getFloat("gravity",0)) );

		// Set level bounds
		widthUpperBound = canvas.getWidth();
		heightUpperBound = canvas.getHeight();

		// Create Somni
		dwidth  = somniTexture.getRegionWidth()/scale.x;
		dheight = somniTexture.getRegionHeight()/scale.y;
		somni = new CharacterModel(constants.get("somniL" + level), dwidth, dheight, somnif, CharacterModel.LIGHT);
		somni.setDrawScale(scale);
		somni.setTexture(somniTexture);
		somni.setFilterData(somnif);
		somni.setBullet(true);
		addObject(somni);
		addObjectTo(somni, sharedtag);
		somni.setActive(true);

		// Create Phobia
		dwidth  = phobiaTexture.getRegionWidth()/scale.x;
		dheight = phobiaTexture.getRegionHeight()/scale.y;
		phobia = new CharacterModel(constants.get("phobiaL" + level), dwidth, dheight, phobiaf, CharacterModel.DARK);
		phobia.setDrawScale(scale);
		phobia.setTexture(phobiaTexture);
		phobia.setFilterData(phobiaf);
		phobia.setBullet(true);
		addObject(phobia);
		addObjectTo(phobia, sharedtag);
		phobia.setActive(true);

		dwidth  = somniPhobiaTexture.getRegionWidth()/scale.x;
		dheight = somniPhobiaTexture.getRegionHeight()/scale.y;
		combined = new CharacterModel(constants.get("combined"), dwidth, dheight, combinedf, CharacterModel.DARK);
		combined.setDrawScale(scale);
		combined.setTexture(somniPhobiaTexture);
		combined.setFilterData(combinedf);
		combined.setBullet(true);
		addObject(combined);
		addObjectTo(combined, sharedtag);
		combined.setActive(true);

		objects.remove(combined);
		sharedObjects.remove(combined);

		combined.setActive(false);
		action = 0;
		//Set current avatar to Phobia
		avatar = phobia;
		maskLeader = somni;
		volume = constants.getFloat("volume", 1.0f);
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


		InputController inputController = InputController.getInstance();
		avatar.setMovement(inputController.getHorizontal() * avatar.getForce());
		avatar.setJumping(inputController.didJump());

		if(inputController.didDash()) {
			if (holdingHands) {
				// Check for propel
				endHoldHands();
				avatar.dashOrPropel(true, inputController.getHorizontal(), inputController.getVertical());

			} else {
				avatar.dashOrPropel(false, inputController.getHorizontal(), inputController.getVertical());
			}
		}

		somni.applyForce();
		phobia.applyForce();
		combined.applyForce();
		//handleworldview();
	    if (avatar.isJumping()) {
	    	//jumpId = playSound( jumpSound, jumpId, volume );
	    } else if (avatar.isDashing()) {
	    	// some dash sound
		}


		if (somni.isDashing()) {
			somni.setGravityScale(0f);
		} else {
			somni.setGravityScale(1);
		}

		if (phobia.isDashing()) {
			phobia.setGravityScale(0f);
		} else {
			phobia.setGravityScale(1);
		}

	    // Check if switched
		if(inputController.didSwitch()) {
			//Switch active character
			if (!holdingHands) {
				avatar.setMovement(0f);
				avatar = avatar == somni ? phobia : somni;
			}else{
				lead = lead == somni ? phobia :somni;
			}
			switching = !switching;
			/*backgroundTexture = backgroundTexture.equals(backgroundLightTexture) ? backgroundDarkTexture :
					backgroundLightTexture;*/
		}
		if(avatar !=combined) {
			lead = avatar;
		}
		if(avatar.isGrounded() && !avatar.isJumping()){
			if (avatar.getMovement() == 0f){
				action = 0;
			}else{
				action = 1;
			}
		}else{
			action = 2;
		}
		//Check if hand holding
		if(inputController.didHoldHands()) {
			handleHoldingHands();
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
				avatar.setTexture(somnisTexture[action]);
			}else{
				avatar.setTexture(phobiasTexture[action]);
			}
		}

		// Set camera position bounded by the canvas size
		camera = canvas.getCamera();

	    float newX = avatar.getX() * canvas.PPM;
		newX = Math.min(newX, widthUpperBound);
		newX = Math.max(canvas.getWidth() / 2, newX );
		camera.position.x += (newX - camera.position.x) * LERP * dt;

		float newY = avatar.getY() * canvas.PPM;
		newY = Math.min(newY, heightUpperBound);
		newY = Math.max(canvas.getHeight() / 2, newY );
		camera.position.y += (newY - camera.position.y) * LERP * dt;

		camera.update();
	}

	/**
	 * Allow Somni and Phobia to hold hands if within range
	 */
	private void handleHoldingHands() {
		if (holdingHands) {
			endHoldHands();
		}
		else if (distance(somni.getX(), somni.getY(), phobia.getX(), phobia.getY()) < HAND_HOLDING_DISTANCE) {
			holdHands();
		}
	}

	/**
	 * Stops holding hands
	 */
	private void endHoldHands() {
		somni.setActive(true);
		phobia.setActive(true);
		combined.setActive(false);

		objects.add(somni);
		objects.add(phobia);
		sharedObjects.add(somni);
		sharedObjects.add(phobia);
		sharedObjects.remove(combined);
		objects.remove(combined);

		float avatarX = avatar.getX();
		float avatarY = avatar.getY();
		float avatarVX = avatar.getVX();
		float avatarVY = avatar.getVY();

		avatar = lead;
		avatar.setPosition(avatarX, avatarY);
		avatar.setVX(avatarVX);
		avatar.setVY(avatarVY);
		float dampeningFactor = -0.25f;
		if(lead == phobia){
			phobia.setCanDash(true);
			somni.setPosition(avatarX, avatarY);
			somni.setVX(avatarVX * dampeningFactor);
			somni.setVY(0);
		}else {
			somni.setCanDash(true);
			phobia.setPosition(avatarX, avatarY);
			phobia.setVX(avatarVX * dampeningFactor);
			phobia.setVY(0);
		}
		somni.setFacingRight(combined.isFacingRight());
		phobia.setFacingRight(combined.isFacingRight());
		holdingHands = false;
	}

	/**
	 * Somni and Phobia hold hands
	 */
	private void holdHands() {
//		Vector2 anchor1 = new Vector2();
//		Vector2 anchor2 = new Vector2(.1f,0);
//
//		RevoluteJointDef jointDef = new RevoluteJointDef();

		somni.setActive(false);
		phobia.setActive(false);
		combined.setActive(true);

		lead = avatar;
		sharedObjects.remove(somni);
		sharedObjects.remove(phobia);
		objects.remove(somni);
		objects.remove(phobia);
		objects.add(combined);
		sharedObjects.add(combined);
		combined.setLinearVelocity(somni.getLinearVelocity().add(phobia.getLinearVelocity()));


		CharacterModel follower = somni == avatar ? phobia : somni;
		float avatarX = follower.getX();
		float avatarY = follower.getY();

		avatar = combined;
		avatar.setPosition(avatarX, avatarY);

		holdingHands = true;
	}

	/**
	 * Finds the Euclidean distance between two coordinates
	 * @param x1 x value of first coord
	 * @param y1 y value of first coord
	 * @param x2 x value of second coord
	 * @param y2 y value of second coord
	 * @return The distance between two coordinates
	 */
	private float distance(float x1, float y1, float x2, float y2) {
		return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}

	/**
	 * Callback method for the start of a collision
	 *
	 * This method is called when we first get a collision between two objects.  We use
	 * this method to test if it is the "right" kind of collision.  In particular, we
	 * use it to test if we made it to the win door.
	 *
	 * @param contact The two bodies that collided
	 */
	public void beginContact(Contact contact) {
		Fixture fix1 = contact.getFixtureA();
		Fixture fix2 = contact.getFixtureB();

		Body body1 = fix1.getBody();
		Body body2 = fix2.getBody();

		Object fd1 = fix1.getUserData();
		Object fd2 = fix2.getUserData();

		try {
			Obstacle bd1 = (Obstacle)body1.getUserData();
			Obstacle bd2 = (Obstacle)body2.getUserData();
			int tile1 = -1;
			int tile2 = -1;

			// See if we have collided with a wall
			if (avatar.getCore().equals(fix1) || avatar.getCore().equals(fix2) ||
					avatar.getCap1().equals(fix1) || avatar.getCap1().equals(fix2) ||
					avatar.getCap2().equals(fix1) || avatar.getCap2().equals(fix2)) {
				avatar.endDashing();
				avatar.setGravityScale(1);

			}

			// See if we have landed on the ground.
			if ((somni.getSensorName().equals(fd2) && somni != bd1 && goalDoor != bd1) ||
				(somni.getSensorName().equals(fd1) && somni != bd2 && goalDoor != bd2)) {
				somni.setGrounded(true);
				lightSensorFixtures.add(somni == bd1 ? fix1 : fix2); // Could have more than one ground
//				somni.canJump = true;

			}
			if ((phobia.getSensorName().equals(fd2) && phobia != bd1 && goalDoor != bd1) ||
					(phobia.getSensorName().equals(fd1) && phobia != bd2 && goalDoor != bd2)) {
				phobia.setGrounded(true);
				darkSensorFixtures.add(phobia == bd1 ? fix1 : fix2); // Could have more than one ground
//				phobia.canJump = true;
			}
			if (avatar == combined && (avatar.getSensorName().equals(fd2) && avatar != bd1 && goalDoor != bd1) ||
					(avatar.getSensorName().equals(fd1) && avatar != bd2 && goalDoor != bd2)) {
				avatar.setGrounded(true);
				combinedSensorFixtures.add(avatar == bd1 ? fix1 : fix2); // Could have more than one ground
//				combined.canJump = true;
			}


			// Check for win condition
			if ((bd1 == combined   && bd2 == goalDoor) ||
					(bd1 == goalDoor && bd2 == combined)) {
				setComplete(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Callback method for the start of a collision
	 *
	 * This method is called when two objects cease to touch.  The main use of this method
	 * is to determine when the characer is NOT on the ground.  This is how we prevent
	 * double jumping.
	 */
	public void endContact(Contact contact) {

		Fixture fix1 = contact.getFixtureA();
		Fixture fix2 = contact.getFixtureB();

		Body body1 = fix1.getBody();
		Body body2 = fix2.getBody();

		Object fd1 = fix1.getUserData();
		Object fd2 = fix2.getUserData();

		Object bd1 = body1.getUserData();
		Object bd2 = body2.getUserData();

		if ((somni.getSensorName().equals(fd2) && somni != bd1 && goalDoor != bd1) ||
			(somni.getSensorName().equals(fd1) && somni != bd2 && goalDoor != bd2)) {

			lightSensorFixtures.remove(somni == bd1 ? fix1 : fix2);

			if (lightSensorFixtures.size == 0) {
				somni.setGrounded(false);
			}
		}
		if ((phobia.getSensorName().equals(fd2) && phobia != bd1 && goalDoor != bd1) ||
				(phobia.getSensorName().equals(fd1) && phobia != bd2 && goalDoor != bd2)) {
			darkSensorFixtures.remove(phobia == bd1 ? fix1 : fix2);

			if (darkSensorFixtures.size == 0) {
				phobia.setGrounded(false);
			}
		}
		if ((avatar.getSensorName().equals(fd2) && avatar != bd1 && goalDoor != bd1) ||
				(avatar.getSensorName().equals(fd1) && avatar != bd2 && goalDoor != bd2)) {
			combinedSensorFixtures.remove(avatar == bd1 ? fix1 : fix2);

			if (combinedSensorFixtures.size == 0) {
				avatar.setGrounded(false);
			}
		}
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
		float leadCenterX = character.getX() * canvas.PPM + character.getWidth() / 2 - maskWidth / 2;
		float leadCenterY = character.getY() * canvas.PPM + character.getHeight() / 2 - maskHeight / 2;
		canvas.beginCustom(GameCanvas.BlendState.OPAQUE, GameCanvas.ChannelState.ALPHA);
		if(alpha_background == null) {
			Pixmap pixmap=new Pixmap((int) canvas.getWidth(), (int) canvas.getHeight(), Pixmap.Format.RGBA8888);
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
	public void drawCharacterRift(float cameraX, float cameraY, float maskWidth, float maskHeight,
										 CharacterModel character) {
		// Mask the environment around character

		// Mask background
		drawMask(cameraX, cameraY, maskWidth, maskHeight, character);
		canvas.beginCustom(GameCanvas.BlendState.NO_PREMULT_DST, GameCanvas.ChannelState.ALL);
		TextureRegion background = character.equals(somni) ? backgroundDarkTexture : backgroundLightTexture;
		canvas.draw(background, Color.WHITE, cameraX, cameraY, canvas.getWidth(), canvas.getHeight());
		canvas.endCustom();

		//Mask obstacles
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

		canvas.setCamera(camera);
		canvas.clear();

		float cameraX = camera.position.x - canvas.getWidth() / 2;
		float cameraY = camera.position.y - canvas.getHeight() / 2;

		// Draw background
		canvas.beginCustom(GameCanvas.BlendState.NO_PREMULT_DST, GameCanvas.ChannelState.ALL);
		canvas.draw(backgroundTexture, Color.WHITE, cameraX, cameraY, canvas.getWidth(), canvas.getHeight());
		canvas.endCustom();

		// Draw lead and follower's rift
		CharacterModel follower = lead.equals(phobia) ? somni : phobia;
		drawCharacterRift(cameraX, cameraY, maskWidth, maskHeight, holdingHands ? combined : maskLeader);
		// Check if switching and update mask drawing
		if(switching) {
			maskWidth += maskWidth > MAX_MASK_SIZE ? 0 : INCREMENT_AMOUNT;
			maskHeight += maskHeight > MAX_MASK_SIZE ? 0 : INCREMENT_AMOUNT;
			if(maskWidth > MAX_MASK_SIZE) {
				maskWidth = MIN_MASK_DIMENSIONS.x;
				maskHeight = MIN_MASK_DIMENSIONS.y;
				switching = false;
				maskLeader = follower;
				backgroundTexture = backgroundTexture.equals(backgroundLightTexture) ? backgroundDarkTexture :
						backgroundLightTexture;
			}
			drawCharacterRift(cameraX, cameraY, MIN_MASK_DIMENSIONS.x, MIN_MASK_DIMENSIONS.y, follower);
		} else {
			maskWidth -= maskWidth <= MIN_MASK_DIMENSIONS.x ? 0 : INCREMENT_AMOUNT;
			maskHeight -= maskHeight <= MIN_MASK_DIMENSIONS.y ? 0 : INCREMENT_AMOUNT;

			// Draw lead platform
			canvas.begin();
			PooledList<Obstacle> objects = lead.equals(somni) ? lightObjects : darkObjects;
			for(Obstacle obj : objects) {
				obj.draw(canvas);
			}
			canvas.end();
		}

		// Draw current model
		canvas.begin();
		if(holdingHands) {
			combined.draw(canvas);
		} else {
			follower.draw(canvas);
			lead.draw(canvas);
		}
		canvas.end();

		// Draw shared platforms
		canvas.begin();
		for(Obstacle obj : sharedObjects) {
			// Ignore characters which we draw separately
			if (!(obj instanceof CharacterModel)) {
				obj.draw(canvas);
			}
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
	private void addObjectTo(Obstacle obj, int l){
		assert inBounds(obj) : "Object is not in bounds";
		if( l == sharedtag){
			sharedObjects.add(obj);
			//obj.activatePhysics(world);
		}
		else if( l == lighttag){
			lightObjects.add(obj);
			//obj.activatePhysics(world);
		}else{
			darkObjects.add(obj);
			//obj.activatePhysics(world);
		}
	}


}