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
package edu.cornell.gdiac.physics.platform;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;

import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.audio.SoundBuffer;
import edu.cornell.gdiac.util.*;
import edu.cornell.gdiac.physics.*;
import edu.cornell.gdiac.physics.obstacle.*;

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
	/** Texture asset for character avatar */
	private TextureRegion avatarTexture;
	/** Texture asset for combined character avatar */
	private TextureRegion combinedTexture;
	/** Texture asset for the spinning barrier */
	private TextureRegion barrierTexture;
	/** Texture asset for the bullet */
	private TextureRegion bulletTexture;
	/** Texture asset for the bridge plank */
	private TextureRegion bridgeTexture;
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
	private DudeModel avatar;

	/** Reference to Somni DudeModel*/
	private DudeModel somni;
	/** Reference to Phobia DudeModel*/
	private DudeModel phobia;
	/** Reference to leading DudeModel*/
	private DudeModel lead;
	/** Reference to combined DudeModel*/
	private DudeModel combined;
	/** Reference to the goalDoor (for collision detection) */
	private BoxObstacle goalDoor;

	/** Are characters currently holding hands */
	private boolean holdingHands;

	/** Level */
	private int level;


	private final float HAND_HOLDING_DISTANCE = 2f;

	/** Mark set to handle more sophisticated collision callbacks */
//	protected ObjectSet<Fixture> sensorFixtures;
	protected ObjectSet<Fixture> lightSensorFixtures;
	protected ObjectSet<Fixture> darkSensorFixtures;

	protected ObjectSet<Fixture> combinedSensorFixtures;
	//Platform logic
	/** This values so light only interacts with light and dark only interacts with dark*/
	private final short CATEGORY_LPLAT = 0x0001;  //0000000000000001
	private final short CATEGORY_DPLAT = 0x0002;  //0000000000000010
	private final short CATEGORY_SOMNI = 0x0004;  //0000000000000100
	private final short CATEGORY_PHOBIA = 0x0008;	   	  //0000000000001000
	private final short CATEGORY_COMBINED = 0x0010; 	  //0000000000010000
	private final short CATEGORY_ALLPLAT = 0x0020;
//	private short all = 11111;

	private final short MASK_LPLAT = CATEGORY_SOMNI | CATEGORY_COMBINED; //Collides with all

	private final short MASK_DPLAT = CATEGORY_PHOBIA | CATEGORY_COMBINED;
//		private final short MASK_DPLAT = -1 ;

	private final short MASK_SOMNI = CATEGORY_LPLAT | CATEGORY_ALLPLAT;
	private final short MASK_PHOBIA = CATEGORY_DPLAT | CATEGORY_ALLPLAT;
	private final short MASK_COMBINED = CATEGORY_DPLAT | CATEGORY_LPLAT | CATEGORY_ALLPLAT;
	private final short MASK_ALLPLAT = CATEGORY_SOMNI | CATEGORY_PHOBIA | CATEGORY_COMBINED;


	/**
	 * Creates and initialize a new instance of the platformer game
	 *
	 * The game has default gravity and other settings
	 */
	public PlatformController(int level) {

		super(DEFAULT_WIDTH,DEFAULT_HEIGHT,DEFAULT_GRAVITY);
		System.out.println(MASK_DPLAT & CATEGORY_PHOBIA);
		setDebug(false);
		setComplete(false);
		setFailure(false);
		world.setContactListener(this);
//		sensorFixtures = new ObjectSet<Fixture>();
		lightSensorFixtures = new ObjectSet<Fixture>();
		darkSensorFixtures = new ObjectSet<Fixture>();
		combinedSensorFixtures = new ObjectSet<Fixture>();
		holdingHands = false;
		this.level = level;
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
		avatarTexture  = new TextureRegion(directory.getEntry("platform:dude",Texture.class));
		combinedTexture = new TextureRegion(directory.getEntry("platform:combined",Texture.class));
		barrierTexture = new TextureRegion(directory.getEntry("platform:barrier",Texture.class));
		bulletTexture = new TextureRegion(directory.getEntry("platform:bullet",Texture.class));
		bridgeTexture = new TextureRegion(directory.getEntry("platform:rope",Texture.class));
		//tiles
		lightTexture = new TextureRegion(directory.getEntry( "shared:light", Texture.class ));
		darkTexture = new TextureRegion(directory.getEntry( "shared:dark", Texture.class ));
		allTexture = new TextureRegion(directory.getEntry( "shared:all", Texture.class ));
		//base models
		somniTexture  = new TextureRegion(directory.getEntry("platform:somni_stand",Texture.class));
		somniWalkTexture = new TextureRegion(directory.getEntry("platform:somni_walk",Texture.class));
		somniDashSideTexture = new TextureRegion(directory.getEntry("platform:somni_dash_side",Texture.class));
		somniDashUpTexture = new TextureRegion(directory.getEntry("platform:somni_dash_up",Texture.class));
		phobiaTexture = new TextureRegion(directory.getEntry("platform:phobia_stand",Texture.class));
		phobiaWalkTexture = new TextureRegion(directory.getEntry("platform:phobia_walk",Texture.class));
		phobiaDashSideTexture = new TextureRegion(directory.getEntry("platform:phobia_dash_side",Texture.class));
		phobiaDashUpTexture = new TextureRegion(directory.getEntry("platform:phobia_dash_up",Texture.class));
		//combined models
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
		objects.clear();
		addQueue.clear();
		world.dispose();

		backgroundTexture = backgroundLightTexture;
		lead = somni;

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

	    String wname = "wall";
	    JsonValue walljv = constants.get("walls");
	    JsonValue defaults = constants.get("defaults");

	    for (int ii = 0; ii < walljv.size; ii++) {
	        PolygonObstacle obj;
	    	obj = new PolygonObstacle(walljv.get(ii).asFloatArray(), 0, 0);
			obj.setBodyType(BodyDef.BodyType.StaticBody);
			obj.setDensity(defaults.getFloat( "density", 0.0f ));
			obj.setFriction(defaults.getFloat( "friction", 0.0f ));
			obj.setRestitution(defaults.getFloat( "restitution", 0.0f ));
			obj.setDrawScale(scale);
			obj.setTexture(earthTile);
			obj.setName(wname+ii);
			obj.setFilterData(allf);
			addObject(obj);
	    }
		String lightPlat = "lightL" + level;
		JsonValue lightPlatJson = constants.get("lightL" + level);
		String darkPlat = "darkL" + level;
		JsonValue darkPlatJson = constants.get("darkL" + level);
		String grayPlat = "grayL" + level;
		JsonValue grayPlatJson = constants.get("grayL" + level);

		// Light platform
		if (lightPlatJson != null) {
			for (int jj = 0; jj < lightPlatJson.size; jj++) {
				PolygonObstacle obj;
				obj = new PolygonObstacle(lightPlatJson.get(jj).asFloatArray(), 0, 0);
				obj.setBodyType(BodyDef.BodyType.StaticBody);
				obj.setDensity(defaults.getFloat( "density", 0.0f ));
				obj.setFriction(defaults.getFloat( "friction", 0.0f ));
				obj.setRestitution(defaults.getFloat( "restitution", 0.0f ));
				obj.setDrawScale(scale);
				obj.setTexture(lightTexture);
				obj.setName(lightPlat+jj);
				obj.setFilterData(lightplatf);
				addObject(obj);
			}
		}

		// Dark platform
		if (darkPlatJson != null) {
			for (int jj = 0; jj < darkPlatJson.size; jj++) {
				PolygonObstacle obj;
				obj = new PolygonObstacle(darkPlatJson.get(jj).asFloatArray(), 0, 0);
				obj.setBodyType(BodyDef.BodyType.StaticBody);
				obj.setDensity(defaults.getFloat( "density", 0.0f ));
				obj.setFriction(defaults.getFloat( "friction", 0.0f ));
				obj.setRestitution(defaults.getFloat( "restitution", 0.0f ));
				obj.setDrawScale(scale);
				obj.setTexture(darkTexture);
				obj.setName(darkPlat+jj);
				obj.setFilterData(darkplatf);
				addObject(obj);
			}
		}

		if (grayPlatJson != null) {
			// Gray platform
			for (int ii = 0; ii < grayPlatJson.size; ii++) {
				PolygonObstacle obj;
				obj = new PolygonObstacle(grayPlatJson.get(ii).asFloatArray(), 0, 0);
				obj.setBodyType(BodyDef.BodyType.StaticBody);
				obj.setDensity(defaults.getFloat( "density", 0.0f ));
				obj.setFriction(defaults.getFloat( "friction", 0.0f ));
				obj.setRestitution(defaults.getFloat( "restitution", 0.0f ));
				obj.setDrawScale(scale);
				obj.setTexture(earthTile);
				obj.setName(grayPlat+ii);
				obj.setFilterData(allf);
				addObject(obj);
			}
		}


	    // This world is heavier
		world.setGravity( new Vector2(0,defaults.getFloat("gravity",0)) );

		// Create dude
		dwidth  = somniTexture.getRegionWidth()/scale.x;
		dheight = somniTexture.getRegionHeight()/scale.y;
		somni = new DudeModel(constants.get("somniL" + level), dwidth, dheight, somnif, DudeModel.LIGHT);
		somni.setDrawScale(scale);
		somni.setTexture(somniTexture);
		somni.setFilterData(somnif);
		somni.setBullet(true);
		addObject(somni);

		// Create Phobia
		dwidth  = phobiaTexture.getRegionWidth()/scale.x;
		dheight = phobiaTexture.getRegionHeight()/scale.y;
		phobia = new DudeModel(constants.get("phobiaL" + level), dwidth, dheight, phobiaf, DudeModel.DARK);
		phobia.setDrawScale(scale);
		phobia.setTexture(phobiaTexture);
		phobia.setFilterData(phobiaf);
		phobia.setBullet(true);
		addObject(phobia);

		dwidth  = somniPhobiaTexture.getRegionWidth()/scale.x;
		dheight = somniPhobiaTexture.getRegionHeight()/scale.y;
		combined = new DudeModel(constants.get("combined"), dwidth, dheight, combinedf, DudeModel.DARK);
		combined.setDrawScale(scale);
		combined.setTexture(somniPhobiaTexture);
		combined.setFilterData(combinedf);
		combined.setBullet(true);
		addObject(combined);

		objects.remove(combined);

		combined.setActive(false);
		action = 0;
		//Set current avatar to somni
		avatar = somni;

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
		if (!isFailure() && somni.getY() < -1 || phobia.getY() < -1) {
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
		// Process actions in object model
//		lightSensorFixtures.clear();
//		darkSensorFixtures.clear();
		InputController inputController = InputController.getInstance();
		avatar.setMovement(inputController.getHorizontal() * avatar.getForce());
		avatar.setJumping(inputController.didJump());
		avatar.setDashing(inputController.didDash(), inputController.getHorizontal(), inputController.getVertical());
		avatar.applyForce();
	    if (avatar.isJumping()) {
	    	jumpId = playSound( jumpSound, jumpId, volume );
	    } else if (avatar.isDashing()) {
	    	// some dash sound
		}
	    // Check if switched
		if(inputController.didSwitch()) {
			//Switch active character
			if (!holdingHands) {
				avatar = avatar == somni ? phobia : somni;
			}else{
				lead = lead == somni ? phobia :somni;
			}
			backgroundTexture = backgroundTexture == backgroundLightTexture ? backgroundDarkTexture : backgroundLightTexture;
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
	    // Check if dashed
	    if(inputController.didDash()) {
	    	Vector2 dashDirection = new Vector2(inputController.getHorizontal(), inputController.getVertical()).nor();
			System.out.println("Dash in direction " + dashDirection.toString());
		}
	}

	/**
	 * Allow Somni and Phobia to hold hands if within range
	 */
	private void handleHoldingHands() {
		if (holdingHands) {
			endHoldHands();
			holdingHands = false;
		}
		else if (distance(somni.getX(), somni.getY(), phobia.getX(), phobia.getY()) < HAND_HOLDING_DISTANCE) {
			System.out.println("close enough to hold hands!");
			holdHands();
			holdingHands = true;
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
		objects.remove(combined);

		float avatarX = avatar.getX();
		float avatarY = avatar.getY();
		float avatarVX = avatar.getVX();
		float avatarVY = avatar.getVY();

		avatar = lead;
		avatar.setPosition(avatarX, avatarY);
		avatar.setVX(avatarVX);
		avatar.setVY(avatarVY);
		if(lead == phobia){
			somni.setPosition(avatarX - 1, avatarY);
			somni.setVX(avatarVX);
			somni.setVY(avatarVY);
		}else {
			phobia.setPosition(avatarX - 1, avatarY);
			phobia.setVX(avatarVX);
			phobia.setVY(avatarVY);
		}
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
		objects.remove(somni);
		objects.remove(phobia);
		objects.add(combined);

		float avatarX = avatar.getX();
		float avatarY = avatar.getY();

		avatar = combined;
		avatar.setPosition(avatarX, avatarY);


//		System.out.println(lightSensorFixtures.size);
//		System.out.println(darkSensorFixtures.size);

//		jointDef.bodyA = somni.getBody();
//		jointDef.bodyB = phobia.getBody();
//		jointDef.localAnchorA.set(anchor1);
//		jointDef.localAnchorB.set(anchor2);
//		jointDef.collideConnected = false;
//		Joint joint = world.createJoint(jointDef);
////		joints.add(joint);
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
		System.out.println("Collision begin");
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


			// See if we have landed on the ground.
			if ((somni.getSensorName().equals(fd2) && somni != bd1) ||
				(somni.getSensorName().equals(fd1) && somni != bd2)) {
				somni.setGrounded(true);
//				lightSensorFixtures.add(somni == bd1 ? fix1 : fix2); // Could have more than one ground
				somni.canJump = true;

			}
			if ((phobia.getSensorName().equals(fd2) && phobia != bd1) ||
					(phobia.getSensorName().equals(fd1) && phobia != bd2)) {
				phobia.setGrounded(true);
//				darkSensorFixtures.add(phobia == bd1 ? fix1 : fix2); // Could have more than one ground
				phobia.canJump = true;
			}
			if (avatar == combined && (avatar.getSensorName().equals(fd2) && avatar != bd1) ||
					(avatar.getSensorName().equals(fd1) && avatar != bd2)) {
				avatar.setGrounded(true);
//				combinedSensorFixtures.add(avatar == bd1 ? fix1 : fix2); // Could have more than one ground
				combined.canJump = true;
			}


			// Check for win condition
			if ((bd1 == avatar   && bd2 == goalDoor) ||
					(bd1 == goalDoor && bd2 == avatar)) {
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
		System.out.println("Collision end");

		Fixture fix1 = contact.getFixtureA();
		Fixture fix2 = contact.getFixtureB();

		Body body1 = fix1.getBody();
		Body body2 = fix2.getBody();

		Object fd1 = fix1.getUserData();
		Object fd2 = fix2.getUserData();

		Object bd1 = body1.getUserData();
		Object bd2 = body2.getUserData();

		if ((somni.getSensorName().equals(fd2) && somni != bd1) ||
			(somni.getSensorName().equals(fd1) && somni != bd2)) {

//			lightSensorFixtures.remove(somni == bd1 ? fix1 : fix2);

//			if (lightSensorFixtures.size == 0) {
				somni.setGrounded(false);


//			}
		}
		if ((phobia.getSensorName().equals(fd2) && phobia != bd1) ||
				(phobia.getSensorName().equals(fd1) && phobia != bd2)) {
//			darkSensorFixtures.remove(phobia == bd1 ? fix1 : fix2);

//			if (darkSensorFixtures.size == 0) {
				phobia.setGrounded(false);
//			}
		}
		if ((avatar.getSensorName().equals(fd2) && avatar != bd1) ||
				(avatar.getSensorName().equals(fd1) && avatar != bd2)) {
//			combinedSensorFixtures.remove(avatar == bd1 ? fix1 : fix2);

//			if (combinedSensorFixtures.size == 0) {
				avatar.setGrounded(false);
//			}
		}
	}

	/**
	 * Draw the physics objects together with foreground and background
	 *
	 * This is completely overridden to support custom background and foreground art.
	 *
	 * @param dt Timing values from parent loop
	 */
	public void draw(float dt) {
		canvas.clear();

		// Draw background unscaled.
		canvas.begin();
		canvas.draw(backgroundTexture, Color.WHITE, 0, 0,canvas.getWidth(),canvas.getHeight());
		canvas.end();

		canvas.begin();
		for(Obstacle obj : objects) {
			obj.draw(canvas);
		}
		canvas.end();

		if (isDebug()) {
			canvas.beginDebug();
			for(Obstacle obj : objects) {
				obj.drawDebug(canvas);
			}
			canvas.endDebug();
		}
		// Final message
		if (isComplete() && !isFailure()) {
			displayFont.setColor(Color.YELLOW);
			canvas.begin(); // DO NOT SCALE
			canvas.drawTextCentered("VICTORY!", displayFont, 0.0f);
			canvas.end();
		} else if (isFailure()) {
			displayFont.setColor(Color.RED);
			canvas.begin(); // DO NOT SCALE
			canvas.drawTextCentered("FAILURE!", displayFont, 0.0f);
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
}