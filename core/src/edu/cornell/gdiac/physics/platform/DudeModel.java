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
package edu.cornell.gdiac.physics.platform;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.physics.box2d.*;

import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.physics.*;
import edu.cornell.gdiac.physics.obstacle.*;

/**
 * Player avatar for the plaform game.
 *
 * Note that this class returns to static loading.  That is because there are
 * no other subclasses that we might loop through.
 */
public class DudeModel extends CapsuleObstacle {
	/** The initializing data (to avoid magic numbers) */
	private final JsonValue data;

	/** The factor to multiply by the input */
	private final float force;
	/** The amount to slow the character down */
	private final float damping;
	/** The maximum character speed */
	private final float maxspeed;
	/** Identifier to allow us to track the sensor in ContactListener */
	private final String sensorName;
	/** The impulse for the character jump */
	private final float jump_force;
	/** Cooldown (in animation frames) for jumping */
	private final int jumpLimit;

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
	/** Whether we can dash */
	private boolean canDash;
	/** The current dash direction of the character */
	private Vector2 dashDirection = new Vector2(0,0);
	/** Whether our feet are on the ground */
	private boolean isGrounded;
	/** The physics shape of this object */
	private PolygonShape sensorShape;
	/** Filter of the model*/
	private Filter filter;

	public static final boolean LIGHT = true;
	public static final boolean DARK = false;

	/** Cache for internal force calculations */
	private final Vector2 forceCache = new Vector2();


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
	 * Returns true if the dude is actively dashing.
	 *
	 * @return true if the dude is actively dashing.
	 */
	public boolean isDashing() {
		return isDashing && !canDash && dashCooldown <= 0;
	}

	/**
	 * Sets whether the dude is actively dashing. If so, set the dashing direction of this dude.
	 *
	 * @param value true if the dude is actively dashing
	 * @param dir_X horizontal component of the dash
	 * @param dir_Y vertical component of the dash
	 */
	public void setDashing(boolean value, float dir_X, float dir_Y) {
		if(dir_X == 0 && dir_Y == 0) {
			// Default dash in direction player faces
			dashDirection.set(isFacingRight() ? 1 : -1, 0);
		} else {
			dashDirection.set(dir_X, dir_Y).nor();
		}
		isDashing = value && canDash;
		if(isDashing) { canDash = false; }
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
	public DudeModel(JsonValue data, float width, float height, Filter f, boolean type) {
		// The shrink factors fit the image to a tighter hitbox
		super(	data.get("pos").getFloat(0),
				data.get("pos").getFloat(1),
				width*data.get("shrink").getFloat( 0 ),
				height*data.get("shrink").getFloat( 1 ));
        setDensity(data.getFloat("density", 0));
		setFriction(data.getFloat("friction", 0));  /// HE WILL STICK TO WALLS IF YOU FORGET
		setFixedRotation(true);

		maxspeed = data.getFloat("maxspeed", 0);
		damping = data.getFloat("damping", 0);
		force = data.getFloat("force", 0);
		jump_force = data.getFloat( "jump_force", 0 );
		jumpLimit = data.getInt( "jump_cool", 0 );
		sensorName = type == LIGHT ? "SomniSensor" : "PhobiaSensor";
		this.data = data;
		filter = f;

		// Gameplay attributes
		isGrounded = false;
		isJumping = false;
		isDashing = false;
		faceRight = true;
		canDash = true;

		jumpCooldown = 0;
		dashCooldown = 0;
		setName("dude");
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
	 * Applies the force to the body of this dude
	 *
	 * This method should be called after the force attribute is set.
	 */
	public void applyForce() {
		if (!isActive()) {
			return;
		}
		
		// Don't want to be moving. Damp out player motion
		if (getMovement() == 0f && isGrounded) {
			forceCache.set(-getDamping()*getVX(),0);
			body.applyForce(forceCache,getPosition(),true);
		}
		
		// Velocity too high on ground, clamp it
		if (Math.abs(getVX()) >= getMaxSpeed() && canDash && isGrounded) {
			setVX(Math.signum(getVX()) * getMaxSpeed());
		} else if (Math.abs(getVX()) >= getMaxSpeed() * 1.5f) {
			setVX(Math.signum(getVX()) * getMaxSpeed() * 1.4f);
		} else {
			forceCache.set(getMovement(),0);
			body.applyForce(forceCache,getPosition(),true);
		}

		// Jump!
		if (isJumping()) {
			forceCache.set(0, jump_force);
			body.applyLinearImpulse(forceCache,getPosition(),true);
		}

		// Dash!
		if (isDashing()) {
			body.setLinearVelocity(0,0);
			forceCache.set(dashDirection.scl(1.25f * jump_force));
			body.applyLinearImpulse(forceCache, getPosition(), true);
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
		canvas.draw(texture,Color.WHITE,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y,getAngle(),effect,1.0f);
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