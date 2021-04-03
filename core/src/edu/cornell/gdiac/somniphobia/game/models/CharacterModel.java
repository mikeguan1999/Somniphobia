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
	/** How long until we can dash again */
	private int dashCooldown;
	/** Whether we are actively dashing */
	private boolean isDashing;

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
	private static final float ANIMATION_SPEED = 0.1f;
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
//	public float getDashDistance(){
//		return dashDistance;
//	}
//	public void setDashDistance(float f){
//		dashDistance = f;
//	}
	public float getCharacterFriction(){
		return getFriction();
	}
	public void setCharacterFriction(float f){
		setFriction(f);
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
	public CharacterModel(JsonValue data, float width, float height, Filter f, boolean type, String level) {
		// The shrink factors fit the image to a tighter hitbox
		super(	data.get("pos"+level).getFloat(0),
				data.get("pos"+level).getFloat(1),
				width*data.get("shrink").getFloat( 0 ),
				height*data.get("shrink").getFloat( 1 ));
		setDensity(data.getFloat("density", 0));
		setFriction(data.getFloat("friction", 0));  /// HE WILL STICK TO WALLS IF YOU FORGET
		setFixedRotation(true);


		dashStartPos = new Vector2(0, 0);
		dashDirection = new Vector2(0,0);
		forceCache = new Vector2();

		maxspeed = data.getFloat("maxspeed", 0);
		damping = data.getFloat("damping", 0);
		force = data.getFloat("force", 0);
		dashDamping = 5f;
//		jumpForce = data.getFloat( "jump_force", 0 );
		jumpForce = 8f;
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
		faceRight = true;
		canDash = true;
		dashed = false;

//		dashDistance = 3.5f;

		jumpCooldown = 0;
		dashCooldown = 0;
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
		return (isDashing && dashDirection.x == 0);
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
	 * Performs a dash or propel
	 *
	 * @param isPropel whether character propelled
	 * @param dir_X horizontal component of the dash
	 * @param dir_Y vertical component of the dash
	 */
	public void dashOrPropel(boolean isPropel, float dir_X, float dir_Y) {
		if(isGrounded && dir_Y < 0) {
			return;
		}

		if (dir_X == 0 && dir_Y == 0) {
			// Default dash in direction player faces
			dashDirection.set(isFacingRight() ? 1 : -1, 0);
//			System.out.println(dashDirection);

		} else {
			dashDirection.set(dir_X, dir_Y).nor();
//			System.out.println(dashDirection);
		}
		dashStartPos.set(getPosition());
		isDashing = canDash;
		if (isDashing) {
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

	/**
	 * Allows for animated character motions. It sets the texture to prepare to draw.
	 *
	 * This method overrides the setTexture method in SimpleObstacle
	 */
	public void setTexture(TextureRegion textureRegion) {
		texture = new Texture(String.valueOf(textureRegion.getTexture()));
		entirePixelWidth = texture.getWidth();
		if (entirePixelWidth < framePixelWidth) {
			entirePixelWidth = framePixelWidth;
		}

		numAnimFrames = (int)(entirePixelWidth/framePixelWidth);
		animator = new FilmStrip(texture,1, numAnimFrames, numAnimFrames);
		if(animeframe > numAnimFrames) {
			animeframe -= numAnimFrames;
		}

		origin = new Vector2(animator.getRegionWidth()/2.0f, animator.getRegionHeight()/2.0f);
		radius = animator.getRegionHeight() / 2.0f;
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
		if (getMovement() == 0f && isGrounded && !isDashing) {
			forceCache.set(-getDamping()*getVX(),0);
			body.applyForce(forceCache,getPosition(),true);
		}

		// Velocity too high on ground, clamp it
		if (Math.abs(getVX()) >= getMaxSpeed() && !isDashing() && isGrounded) {
			setVX(Math.signum(getVX()) * getMaxSpeed());
		}
//		else if (Math.abs(getVX()) >= getMaxSpeed() * 1.5f && !isDashing()) {
//			setVX(Math.signum(getVX()) * getMaxSpeed() * 1.4f);
//		}
		else if (!isDashing()){
			forceCache.set(getMovement() * .3f,getVY());

//			body.applyForce(forceCache,getPosition(),true);
			body.setLinearVelocity(forceCache);
		}

		// Jump!
		if (isJumping()) {
			forceCache.set(0, jumpForce * 1.5f);
//			body.applyLinearImpulse(forceCache,getPosition(),true);
			body.setLinearVelocity(forceCache);
		}

		// Dash!
		if (isDashing() && !dashed) {
//			System.out.println("Dash in direction: (" + dashDirection.x + "," + dashDirection.y);
			forceCache.set(dashDirection.scl(dashVelocity));
			body.setLinearVelocity(forceCache);

//			body.applyLinearImpulse(forceCache, getPosition(), true);
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
		animeframe += ANIMATION_SPEED;
		if (animeframe >= numAnimFrames) {


			animeframe -= numAnimFrames;

		}

		// Apply cooldowns
		if (isJumping()) {
			jumpCooldown = jumpLimit;
		} else {
			jumpCooldown = Math.max(0, jumpCooldown - 1);
		}

		if (isDashing()) {
			dashCooldown = jumpLimit;
		} else {
			dashCooldown = Math.max(0, dashCooldown - 1);
		}

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

		if(isGrounded && dashCooldown <= 0) {
			canDash = true;
		}

		//System.out.println(canDash);
		super.update(dt);
	}

	/**
	 * Draws the physics object.
	 *
	 * @param canvas Drawing context
	 */
	public void draw(GameCanvas canvas) {
		float effect = faceRight ? -1.0f : 1.0f;
//		canvas.draw(texture,Color.WHITE,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y,getAngle(),effect,1.0f);
		animator.setFrame((int)animeframe);
		canvas.draw(animator, Color.WHITE, origin.x, origin.y,getX()*drawScale.x,getY()*drawScale.y,getAngle(),
				effect, 1.0f);
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