/*
 * DudeModel.java
 *
 * You SHOULD NOT need to modify this file.  However, you may learn valuable lessons
 * for the rest of the lab by looking at it.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * Updated asset version, 2/6/2021
 */
package edu.cornell.gdiac.somniphobia.game.models;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.physics.box2d.*;

import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.somniphobia.*;
import edu.cornell.gdiac.somniphobia.obstacle.*;
import edu.cornell.gdiac.util.FilmStrip;

/**
 * Player avatar for the plaform game.
 *
 * Note that this class returns to static loading.  That is because there are
 * no other subclasses that we might loop through.
 */
public class CharacterModel extends CapsuleObstacle {
	/** The initializing data (to avoid magic numbers) */
	private final JsonValue data;

	/** The factor to multiply by the input */
	private float force;
	/** The amount to slow the character down */
	private float damping;
	/** The amount to slow the character down during dashing*/
	private float dashDamping;
	/** The maximum character speed */
	private float maxspeed;
	/** Identifier to allow us to track the sensor in ContactListener */
	private final String sensorName;
	/** The impulse for the character jump */
	private float jumpForce;

	/** The velocity for the character dash */
	private float dashVelocity;
	/** The velocity to stop dashing */
	private float dashEndVelocity;
	/** Cooldown (in animation frames) for jumping */
	private int jumpLimit;

	/** The current horizontal movement of the character */
	private float   movement;
	/** Which direction is the character facing */
	private boolean faceRight;
	/** How long until we can jump again */
	private int jumpCooldown;
	/** Whether we are actively jumping */
	private boolean isJumping;
//	/** How long until we can dash again */
//	private int dashCooldown;
	/** Whether we are actively dashing */
	private boolean isDashing;

	private boolean justPropelled;

//	/** Distance to dash */
//	private float dashDistance;
	/** Whether we have applied initial dash velocity */
	private boolean dashed;
	/** Whether we can dash */
	private boolean canDash;
	/** The current dash direction of the character */
	private Vector2 dashDirection;
	/** The start position of the current dash */
	private Vector2 dashStartPos;
	/** Whether our feet are on the ground */
	private boolean isGrounded;
	/** The physics shape of this object */
	private PolygonShape sensorShape;
	/** Filter of the model*/
	private Filter filter;

	/** The platform that the model is touching; Is null if not in contact*/
	private Obstacle ground;

	public static final boolean LIGHT = true;
	public static final boolean DARK = false;

	/** Cache for internal force calculations */
	private final Vector2 forceCache;

	/// VARIABLES FOR DRAWING AND ANIMATION
	/** CURRENT image for this object. May change over time. */
	private FilmStrip animator;
	/** Reference to texture origin */
	private Vector2 origin;
	/** Radius of the object (used for collisions) */
	private float radius;
	/** How fast we change frames (one frame per 10 calls to update) */
	private float animationSpeed = 0.1f;
	/** The number of animation frames in our filmstrip */
	private int numAnimFrames = 2;
	/** Texture for animated objects */
	private Texture texture;
	/** Current animation frame for this shell */
	private float animeframe = 0.0f;
	/** Pixel width of the current texture */
	private double entirePixelWidth;
	/** Pixel width of the current frame in the texture */
	private double framePixelWidth = 32;
	/** Offset in x direction */
	private float xOffset;
	/** Offset in y direction */
	private float yOffset;

	/// VARIABLES FOR SECOND DRAWING AND ANIMATION
	/** CURRENT image for this object. May change over time. */
	private FilmStrip animatorTwo;
	/** Reference to texture origin */
	private Vector2 origin2;
	/** How fast we change frames (one frame per 10 calls to update) */
	private float animationSpeedTwo = 0.1f;
	/** The number of animation frames in our filmstrip */
	private int numAnimeframesTwo = 2;
	/** Texture for animated objects */
	private Texture textureTwo;
	/** Current animation frame for this shell */
	private float animeFrameTwo = 0.0f;
	/** Pixel width of the current texture */
	private double entirePixelWidthTwo;
	/** Pixel width of the current frame in the texture */
	private double framePixelWidthTwo = 32;
	/** Offset in x direction */
	private float xOffset2;
	/** Offset in y direction */
	private float yOffset2;
	/** rotation of the animation */
	private float angle;


	/** Current animation frame for the ring */
	private float animeframeRing = 0.0f;
	/** Whether a ring animation cycle is complete */
	private boolean ringCycleComplete;

	/// VARIABLES FOR THIRD DRAWING AND ANIMATION
	/** Texture for animated objects */
	private Texture textureThree;
	/** Offset in x direction */
	private float xOffset3;
	/** Offset in x direction */
	private float yOffset3;

	/** Getters and setters*/
	public float getDashEndVelocity() { return dashEndVelocity; }
	public void setDashEndVelocity(float f) { dashEndVelocity = f; }
	public float getDashDamping(){
		return dashDamping;
	}
	public void setDashDamping(float f){
		dashDamping = f;
	}
	public float getJumpForce(){
		return jumpForce;
	}
	public void setJumpForce(float f){
		jumpForce = f;
	}
	public float getDashVelocity(){
		return dashVelocity;
	}
	public void setDashVelocity(float f){
		dashVelocity = f;
	}
	public void setCharacterForce(float f){
		force = f;
	}
	/**
	 * Creates a new dude avatar with the given physics data
	 *
	 * The size is expressed in physics units NOT pixels.  In order for
	 * drawing to work properly, you MUST set the drawScale. The drawScale
	 * converts the physics units to pixels.
	 *
	 * @param data  	The physics constants for this dude
	 * @param width		The object width in physics units
	 * @param height	The object width in physics units
	 */
	public CharacterModel(JsonValue data, float x, float y, float width, float height, Filter f, boolean type) {
		// The shrink factors fit the image to a tighter hitbox
		super(	x, y, width*data.get("shrink").getFloat( 0 ),
				height*data.get("shrink").getFloat( 1 ));
		setDensity(data.getFloat("density", 0));
		setFriction(data.getFloat("friction", 0));  /// HE WILL STICK TO WALLS IF YOU FORGET
		setFixedRotation(true);


		dashStartPos = new Vector2(0, 0);
		dashDirection = new Vector2(0,0);
		forceCache = new Vector2();

		maxspeed = data.getFloat("max_speed", 0);
		damping = data.getFloat("damping", 0);
		force = data.getFloat("force", 0);
		dashDamping = data.getFloat("dash_damping", 0);
		jumpForce = data.getFloat( "jump_force", 0 );
		jumpLimit = data.getInt( "jump_cool", 0 );
		sensorName = type == LIGHT ? "SomniSensor" : "PhobiaSensor";
		this.data = data;
		filter = f;

		dashVelocity = 35f;
		dashEndVelocity = 4f;

		// Gameplay attributes
		isGrounded = false;
		isJumping = false;
		isDashing = false;
		justPropelled = false;
		faceRight = true;
		canDash = true;
		dashed = false;

//		dashDistance = 3.5f;

		origin = new Vector2();
		origin2 = new Vector2();

		jumpCooldown = 0;
//		dashCooldown = 0;
		setName("dude");
	}

	/**
	 * Returns left/right movement of this character.
	 *
	 * This is the result of input times dude force.
	 *
	 * @return left/right movement of this character.
	 */
	public float getMovement() {
		return movement;
	}

	/**
	 * Sets left/right movement of this character.
	 *
	 * This is the result of input times dude force.
	 *
	 * @param value left/right movement of this character.
	 */
	public void setMovement(float value) {
		movement = value;
		// Change facing if appropriate
		if (movement < 0) {
			faceRight = false;
		} else if (movement > 0) {
			faceRight = true;
		}
	}

	/**
	 * Returns true if the dude is actively jumping.
	 *
	 * @return true if the dude is actively jumping.
	 */
	public boolean isJumping() {
		return isJumping && isGrounded && jumpCooldown <= 0;
	}

	/**
	 * Sets whether the dude is actively jumping.
	 *
	 * @param value whether the dude is actively jumping.
	 */
	public void setJumping(boolean value) {
		isJumping = value;
	}

	/**
	 * Returns true if the dude is actively falling.
	 *
	 * @return true if the dude is actively falling.
	 */
	public boolean isFalling() {
		return isJumping && this.getVY() < 0;
	}

	/**
	 * Returns true if the dude is actively dashing.
	 *
	 * @return true if the dude is actively dashing.
	 */
	public boolean isDashing() {
		return isDashing;
	}

	/**
	 * Returns true if the dude is actively dashing straight up.
	 *
	 * @return true if the dude is actively dashing straight up.
	 */
	public boolean isDashingUp() {
		return (isDashing && dashDirection.y >= 0 && dashDirection.x==0);
	}

	/**
	 * Returns true if the dude is actively dashing straight down.
	 *
	 * @return true if the dude is actively dashing straight down.
	 */
	public boolean isDashingDown() {
		return (isDashing && dashDirection.y <= 0 && dashDirection.x==0);
	}

	/**
	 * Sets whether character can dash
	 * @param value whether character can dash
	 */
	public void setCanDash(boolean value) {
		canDash = value;
	}

	/**
	 * Sets whether character is actively dashing
	 * @param b true if character is actively dashing
	 */
	public void setDashing(boolean b) {
		isDashing = b;
	}

	/**
	 * gets whether character just propelled
	 * @return whether character just propelled
	 */
	public boolean justPropelled() {
		return justPropelled;
	}
	/**
	 * Sets whether character just propelled
	 * @param b whether character just propelled
	 */
	public void setJustPropelled(boolean b) {
		justPropelled = b;
	}

	/**
	 * Performs a dash or propel
	 *
	 * @param isPropel whether character propelled
	 * @param dir_X horizontal component of the dash
	 * @param dir_Y vertical component of the dash
	 */
	public void dashOrPropel(boolean isPropel, float dir_X, float dir_Y) {
		if (isPropel) {
			justPropelled = true;
		} else {
			justPropelled = false;
		}

		if(isGrounded && dir_Y < 0) {
			return;
		}

		if (dir_X == 0 && dir_Y == 0) {
			// Default dash in direction player faces
			dashDirection.set(isFacingRight() ? 1 : -1, 0);

		} else {
			dashDirection.set(dir_X, dir_Y).nor();
		}
		dashStartPos.set(getPosition());
		isDashing = canDash;
		if (isDashing || isPropel) {
			if (!isPropel) {
				canDash = false;
			}
			dashed = false;
		}
	}

	/**
	 * Ends the dashing and sets velocity to 0 if we haven't done so
	 */
	public void endDashing() {
//		this.setGravityScale(1);
		if (isDashing) {
			isDashing = false;
			setVY(0f);
			setVX(0f);
		}
	}

	/**
	 * Returns true if the dude is on the ground.
	 *
	 * @return true if the dude is on the ground.
	 */
	public boolean isGrounded() {
		return isGrounded;
	}

	/**
	 * Sets whether the dude is on the ground.
	 *
	 * @param value whether the dude is on the ground.
	 */
	public void setGrounded(boolean value) {
		isGrounded = value;
	}

	/**
	 * Returns how much force to apply to get the dude moving
	 *
	 * Multiply this by the input to get the movement value.
	 *
	 * @return how much force to apply to get the dude moving
	 */
	public float getForce() {
		return force;
	}

	/**
	 * Returns how hard the brakes are applied to get a dude to stop moving
	 *
	 * @return how hard the brakes are applied to get a dude to stop moving
	 */
	public float getDamping() {
		return damping;
	}

	/**
	 * Returns the upper limit on dude left-right movement.  
	 *
	 * This does NOT apply to vertical movement.
	 *
	 * @return the upper limit on dude left-right movement.  
	 */
	public float getMaxSpeed() {
		return maxspeed;
	}

	/**
	 * Returns the name of the ground sensor
	 *
	 * This is used by ContactListener
	 *
	 * @return the name of the ground sensor
	 */
	public String getSensorName() {
		return sensorName;
	}

	/**
	 * Returns true if this character is facing right
	 *
	 * @return true if this character is facing right
	 */
	public boolean isFacingRight() {
		return faceRight;
	}

	/**
	 * Sets whether the dude is facing right.
	 *
	 * @param value whether the dude is facing right.
	 */
	public void setFacingRight(boolean value) {
		faceRight = value;
	}


	public void setGround(Obstacle ground) {
		this.ground = ground;
	}


	/**
	 * Creates the physics Body(s) for this object, adding them to the world.
	 *
	 * This method overrides the base method to keep your ship from spinning.
	 *
	 * @param world Box2D world to store body
	 *
	 * @return true if object allocation succeeded
	 */
	public boolean activatePhysics(World world) {
		// create the box from our superclass
		if (!super.activatePhysics(world)) {
			return false;
		}

		// Ground Sensor
		// -------------
		// We only allow the dude to jump when he's on the ground. 
		// Double jumping is not allowed.
		//
		// To determine whether or not the dude is on the ground, 
		// we create a thin sensor under his feet, which reports 
		// collisions with the world but has no collision response.
		Vector2 sensorCenter = new Vector2(0, -getHeight() / 2);
		FixtureDef sensorDef = new FixtureDef();
		sensorDef.density = data.getFloat("density",0);
		sensorDef.isSensor = true;
		sensorShape = new PolygonShape();
		JsonValue sensorjv = data.get("sensor");
		sensorShape.setAsBox(sensorjv.getFloat("shrink",0)*getWidth()/2.0f,
								 sensorjv.getFloat("height",0), sensorCenter, 0.0f);
		sensorDef.shape = sensorShape;
		sensorDef.filter.categoryBits = filter.categoryBits;
		sensorDef.filter.maskBits = filter.maskBits;

		// Ground sensor to represent our feet
		Fixture sensorFixture = body.createFixture( sensorDef );
		sensorFixture.setUserData(getSensorName());

		return true;
	}

	public boolean isRingCycleComplete(){return ringCycleComplete;}
	public void setRingCycleComplete(boolean value){ringCycleComplete = value;}
	/**
	 * Allows for animated character motions. It sets the texture to prepare to draw.
	 *
	 * This method overrides the setTexture method in SimpleObstacle
	 */
	public void setTexture(TextureRegion textureRegion) {
		texture = textureRegion.getTexture();
		entirePixelWidth = texture.getWidth();
		if (entirePixelWidth < framePixelWidth) {
			entirePixelWidth = framePixelWidth;
		}

		numAnimFrames = (int)(entirePixelWidth/framePixelWidth);

		animator = new FilmStrip(texture,1, numAnimFrames, numAnimFrames);
		if(animeframe > numAnimFrames) {
			animeframe -= numAnimFrames;
		}

		origin.set(animator.getRegionWidth()/2.0f, animator.getRegionHeight()/2.0f);
		radius = animator.getRegionHeight() / 2.0f;
	}

	/**
	 * Allows for animated character motions. It sets the texture to prepare to draw.
	 *
	 * This method overrides the setTexture method above to set animation speed and pixel width
	 */
	public void setTexture(TextureRegion textureRegion, float animationSpeed, double framePixelWidth) {
		this.animationSpeed = animationSpeed;
		this.framePixelWidth = framePixelWidth;
		texture = textureRegion.getTexture();
		entirePixelWidth = texture.getWidth();
		if (entirePixelWidth < framePixelWidth) {
			entirePixelWidth = framePixelWidth;
		}

		numAnimFrames = (int)(entirePixelWidth/framePixelWidth);
		animator = new FilmStrip(texture,1, numAnimFrames, numAnimFrames);
		if(animeframe > numAnimFrames) {
			animeframe -= numAnimFrames;
		}

		origin.set(animator.getRegionWidth()/2.0f, animator.getRegionHeight()/2.0f);
		radius = animator.getRegionHeight() / 2.0f;

		if (ringCycleComplete){
			textureTwo = null;
		}
		textureThree = null;
	}


	/**
	 * Allows for animated character motions. It sets the texture to prepare to draw.
	 * This method draws three animations synchronously, although the third is essentially a still image
	 * This method overrides the setTexture method above to set animation speeds and pixel widths
	 */
	public void setTexture(TextureRegion textureRegion, float animationSpeed, double framePixelWidth, float offsetX, float offsetY,
						   TextureRegion secTextureRegion, float secAnimationSpeed, double secFramePixelWidth, float secOffsetX, float secOffsetY,
						   TextureRegion thirdTextureRegion, float thirdOffsetX, float thirdOffsetY) {
		// first animation
		this.animationSpeed = animationSpeed;
		this.framePixelWidth = framePixelWidth;
		texture = textureRegion.getTexture();
		entirePixelWidth = texture.getWidth();
		if (entirePixelWidth < framePixelWidth) {
			entirePixelWidth = framePixelWidth;
		}

		numAnimFrames = (int)(entirePixelWidth/framePixelWidth);
		animator = new FilmStrip(texture,1, numAnimFrames, numAnimFrames);
		if(animeframe > numAnimFrames) {
			animeframe -= numAnimFrames;
		}

		origin.set(animator.getRegionWidth()/2.0f, animator.getRegionHeight()/2.0f);
		radius = animator.getRegionHeight() / 2.0f;
		this.xOffset = offsetX;
		this.yOffset = offsetY;

		//second animation
		this.animationSpeedTwo = secAnimationSpeed;
		this.framePixelWidthTwo = secFramePixelWidth;
		textureTwo = secTextureRegion.getTexture();
		entirePixelWidthTwo = textureTwo.getWidth();
		if (entirePixelWidthTwo < secFramePixelWidth) {
			entirePixelWidthTwo = secFramePixelWidth;
		}

		numAnimeframesTwo = (int)(entirePixelWidthTwo/secFramePixelWidth);
		animatorTwo = new FilmStrip(textureTwo,1, numAnimeframesTwo, numAnimeframesTwo);
		if(animeFrameTwo > numAnimeframesTwo) {
			animeFrameTwo -= numAnimeframesTwo;
		}

		origin2.set(animatorTwo.getRegionWidth()/2.0f, animatorTwo.getRegionHeight()/2.0f);
		this.xOffset2 = secOffsetX;
		this.yOffset2 = secOffsetY;

		//third animation
		if (thirdTextureRegion!=null) {
			textureThree = thirdTextureRegion.getTexture();
			this.xOffset3 = thirdOffsetX;
			this.yOffset3 = thirdOffsetY;
		}
	}

	/**
	 * Allows for animated character motions. It sets the texture to prepare to draw.
	 * This method draws three animations synchronously.
	 * This method overrides the setTexture method above to set animation speeds and pixel widths
	 */
	public void setTexture(TextureRegion textureRegion, float animationSpeed, double framePixelWidth, float offsetX, float offsetY,
						   TextureRegion secTextureRegion, float secAnimationSpeed, double secFramePixelWidth, float secOffsetX, float secOffsetY, float angle) {
		ringCycleComplete = false;

		// first animation
		this.animationSpeed = animationSpeed;
		this.framePixelWidth = framePixelWidth;
		texture = textureRegion.getTexture();
		entirePixelWidth = texture.getWidth();
		if (entirePixelWidth < framePixelWidth) {
			entirePixelWidth = framePixelWidth;
		}

		numAnimFrames = (int)(entirePixelWidth/framePixelWidth);
		animator = new FilmStrip(texture,1, numAnimFrames, numAnimFrames);
		if(animeframe > numAnimFrames) {
			animeframe -= numAnimFrames;
		}

		origin.set(animator.getRegionWidth()/2.0f, animator.getRegionHeight()/2.0f);
		radius = animator.getRegionHeight() / 2.0f;
		this.xOffset = offsetX;
		this.yOffset = offsetY;

		//second animation
		this.animationSpeedTwo = secAnimationSpeed;
		this.framePixelWidthTwo = secFramePixelWidth;
		textureTwo = secTextureRegion.getTexture();
		entirePixelWidthTwo = textureTwo.getWidth();
		if (entirePixelWidthTwo < secFramePixelWidth) {
			entirePixelWidthTwo = secFramePixelWidth;
		}

		numAnimeframesTwo = (int)(entirePixelWidthTwo/secFramePixelWidth);
		animatorTwo = new FilmStrip(textureTwo,1, numAnimeframesTwo, numAnimeframesTwo);
		if(animeFrameTwo > numAnimeframesTwo) {
			animeFrameTwo -= numAnimeframesTwo;
		}

		origin2.set(animatorTwo.getRegionWidth()/2.0f, animatorTwo.getRegionHeight()/2.0f);
		this.xOffset2 = secOffsetX;
		this.yOffset2 = secOffsetY;
		this.angle = angle;

		textureThree = null;
	}

	/**
	 * Allows for animated character motions. It sets the texture to prepare to draw.
	 * This method draws two animations synchronously, although the second is essentially a still image
	 * This method overrides the setTexture method above to set animation speeds and pixel widths
	 */
	public void setTexture(TextureRegion textureRegion, float animationSpeed, double framePixelWidth, float offsetX, float offsetY,
						   TextureRegion thirdTextureRegion, float thirdOffsetX, float thirdOffsetY) {
		// first animation
		this.animationSpeed = animationSpeed;
		this.framePixelWidth = framePixelWidth;
		texture = textureRegion.getTexture();
		entirePixelWidth = texture.getWidth();
		if (entirePixelWidth < framePixelWidth) {
			entirePixelWidth = framePixelWidth;
		}

		numAnimFrames = (int)(entirePixelWidth/framePixelWidth);
		animator = new FilmStrip(texture,1, numAnimFrames, numAnimFrames);
		if(animeframe > numAnimFrames) {
			animeframe -= numAnimFrames;
		}

		origin.set(animator.getRegionWidth()/2.0f, animator.getRegionHeight()/2.0f);
		radius = animator.getRegionHeight() / 2.0f;
		this.xOffset = offsetX;
		this.yOffset = offsetY;

		//third animation
		textureThree = thirdTextureRegion.getTexture();
		this.xOffset3 = thirdOffsetX;
		this.yOffset3 = thirdOffsetY;

		textureTwo = null;
	}

	/**
	 * Applies the force to the body of this dude
	 *
	 * This method should be called after the force attribute is set.
	 */
	public void applyForce() {
		if (!isActive()) {
			return;
		}

		// Don't want to be moving. Damp out player motion
//		if (getMovement() == 0f && isGrounded && !isDashing) {
//			forceCache.set(-getDamping()*getVX(),0);
//			body.applyForce(forceCache,getPosition(),true);
//		}

		// Velocity too high on ground, clamp it
//		if (Math.abs(getVX()) > getMaxSpeed() && !isDashing() && isGrounded && false) {
//			setVX(Math.signum(getVX()) * getMaxSpeed());
//		} else
		if (!isDashing()) {
			forceCache.set(getMovement() * .3f + (ground == null ? 0: ground.getVX()),getVY());
			body.setLinearVelocity(forceCache);
		}

		// Jump!
		if (isJumping()) {
			forceCache.set(0, jumpForce * 1.5f);
			body.setLinearVelocity(forceCache);
		}

		// Dash!
		if (isDashing() && !dashed) {
			forceCache.set(dashDirection.scl(dashVelocity));
			body.setLinearVelocity(forceCache);
			dashed = true;
		}
	}

	/**
	 * Updates the object's physics state (NOT GAME LOGIC).
	 *
	 * We use this method to reset cooldowns and states.
	 *
	 * @param dt	Number of seconds since last animation frame
	 */
	public void update(float dt) {

		// Increase animation frame
		animeframe += animationSpeed;
		if (animeframe >= numAnimFrames) {
			animeframe = 0;
		}

		animeFrameTwo += animationSpeedTwo;
		if (animeFrameTwo >= numAnimeframesTwo) {
			animeFrameTwo = 0;
		}

		if (animeframeRing > 6 ){
			ringCycleComplete = true;
			animeframeRing = -0.2f;
		}
		if (!ringCycleComplete){
			animeframeRing += animationSpeedTwo;
		}


		// Apply cooldowns
		if (isJumping()) {
			jumpCooldown = jumpLimit;
		} else {
			jumpCooldown = Math.max(0, jumpCooldown - 1);
		}

//		if (isDashing()) {
//			dashCooldown = jumpLimit;
//		} else {
//			dashCooldown = Math.max(0, dashCooldown - 1);
//		}

		if(isDashing) {
//			System.out.println(getVY());
			forceCache.set(-dashDamping*getVX(),-dashDamping*getVY());
			body.applyForce(forceCache,getPosition(),true);
//			body.setLinearVelocity(.8f * getVX(), .8f * getVY());



			// Dash based on distance
//			if (currDashDist >= dashDistance) {
//				setDashing(false);
//				forceCache.set(0,0);
//				setLinearVelocity(forceCache);
//			}

			//Dash using dampening
			if(getLinearVelocity().len() < dashEndVelocity) {
				setDashing(false);
//				forceCache.set(0,0);
//				setLinearVelocity(forceCache);
			}

		}

//		if (isGrounded && dashCooldown == 0) {
		if(isGrounded && !isDashing) {
			canDash = true;
		}
		super.update(dt);
	}

	/**
	 * Draws the physics object.
	 *
	 * @param canvas Drawing context
	 */
	public void draw(GameCanvas canvas, Color tint) {
		float effect = faceRight ? -1.0f : 1.0f;
		animator.setFrame((int)animeframe);
		canvas.draw(animator, tint, origin.x + xOffset, origin.y + yOffset,getX()*drawScale.x,getY()*drawScale.y,getAngle(),
				effect, 1.0f);

		// for handholding
		if (textureTwo!=null && textureThree !=null) {
			animatorTwo.setFrame((int)animeFrameTwo);
			// draw the second character
			canvas.draw(animatorTwo, Color.WHITE, origin2.x+xOffset2, origin2.y+yOffset2,getX()*drawScale.x,getY()*drawScale.y,getAngle(),
					effect, 1.0f);
			// draw the hands
			canvas.draw(textureThree, Color.WHITE, origin.x+ xOffset3, origin.y+ yOffset3, getX()*drawScale.x,getY()*drawScale.y,getAngle(),
					effect, 1.0f);
		}

		// for propelling / dashing
		if (textureTwo!=null && textureThree ==null && animeframeRing>=0 && animeframeRing <=6) {
			animatorTwo.setFrame((int)animeframeRing);
			// draw the blue ring animation
			canvas.draw(animatorTwo, Color.WHITE, origin2.x+xOffset2, origin2.y+yOffset2+60,getX()*drawScale.x,getY()*drawScale.y,angle,
					effect, 1.0f);
		}
		if (textureTwo==null && textureThree !=null) {
			// draw the reaching out hand (can-hold-hand indicator)
			canvas.draw(textureThree, Color.WHITE, origin.x+ xOffset3, origin.y+ yOffset3, getX()*drawScale.x,getY()*drawScale.y,getAngle(),
					effect, 1.0f);
		}
	}

	/**
	 * Draws the outline of the physics body.
	 *
	 * This method can be helpful for understanding issues with collisions.
	 *
	 * @param canvas Drawing context
	 */
	public void drawDebug(GameCanvas canvas) {
		super.drawDebug(canvas);
		canvas.drawPhysics(sensorShape,Color.RED,getX(),getY(),getAngle(),drawScale.x,drawScale.y);
	}

}