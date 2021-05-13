package edu.cornell.gdiac.somniphobia.game.models;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import edu.cornell.gdiac.somniphobia.obstacle.BoxObstacle;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.physics.box2d.*;

import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.somniphobia.*;
import edu.cornell.gdiac.somniphobia.obstacle.*;
import edu.cornell.gdiac.util.FilmStrip;
import edu.cornell.gdiac.util.PooledList;

public class PlatformModel extends BoxObstacle {

    /** Behavior tag constants */
    public final static int normal = 1;
    public final static int harming = 2;
    public final static int crumbling = 3;

    /** Width of the platform*/
    private float width;
    /** Height of the platform*/
    private float height;

    /** X position based on lower left corner*/
    private float leftX;
    /** Y position based on lower left corner*/
    private float bottomY;

    /** Filter*/
    private Filter filter;

    private int type;


    /** Whether the platform is currently raining **/
    private boolean isCurrentlyRaining;

    /** Density position*/
    private float density;
    /** Friction position*/
    private float friction;
    /** restitution position*/
    private float restitution;

    /** behavior of this platform */
    private int property;

    /** velocity for moving platform **/
    private float velocity;

    private float rainingCooldown;

    /** Path for a moving obstacle **/
    private PooledList<Vector2> paths;

    /** scale*/
    public float scale;

    /// VARIABLES FOR DRAWING AND ANIMATION
    /** CURRENT image for this object. May change over time. */
    private FilmStrip animator;
    /** Reference to texture origin */
    private Vector2 origin;
    /** The texture for the shape. */
    private TextureRegion texture;
    /** Radius of the object (used for collisions) */
    private float radius;
    /** How fast we change frames (one frame per 10 calls to update) */
    private float animationSpeed = 0.1f;
    /** The number of animation frames in our filmstrip */
    private int numAnimFrames = 2;
    /** Texture for animated objects */
    private Texture actualTexture;
    /** Current animation frame for this shell */
    private float animeframe = 0.0f;
    /** Pixel width of the current texture */
    private double entirePixelWidth;
    /** Pixel width of the current frame in the texture */
    private double framePixelWidth = 32;

    Obstacle touching = null;

    public PlatformModel(float [] bounds, int t, TextureRegion tr, Vector2 s, float d, float f , float r){
        super(bounds[0]+bounds[2]/2, bounds[1] + bounds[3]/2,
                bounds[2], bounds[3]);
        this.setBodyType(BodyDef.BodyType.StaticBody);
        this.setDensity(d);
        this.setFriction(f);
        this.setRestitution(r);
        this.setDrawScale(s);

        this.setTexture(tr);

        this.setTag(t);
        this.property = 0;
        this.isCurrentlyRaining = false;

    }

    public float getLeftX() {
        return getX() - getWidth() / 2;
    }
    public float getBottomY() {
        return getY() - getHeight() / 2;
    }


    /**
     * Begins the rain animation for a raining platform
     */
    public void setCurrentlyRaining(boolean currentlyRaining) {
        isCurrentlyRaining = currentlyRaining;
    }


    /**
     * Is the platform currently raining
     * @return whether platform is currently raining
     */
    public boolean isCurrentlyRaining() {
        return isCurrentlyRaining;
    }

    /**
     * Sets the raining cooldown of this platform
     * @param rainingCooldown the raining cooldown
     */
    public void setRainingCooldown(float rainingCooldown) {
        this.rainingCooldown = rainingCooldown;
    }

    /**
     * Gets the raining cooldown of this platform
     * @return the raining cooldown
     */
    public float getRainingCooldown() {
        return this.rainingCooldown;
    }

    /**
     * Sets what this platform is currently touching
     * @param o the obstacle the platform is touching
     */
    public void setTouching(Obstacle o) {
        this.touching = o;
    }

    /**
     * Returns an obstacle that is it is in contact with, otherwise returning null
     * @return the obstacle or null of no obstacle in contact
     */
    public Obstacle getTouching() {
        return touching;
    }


    /**
     * Sets the paths of this obstacle
     * @param paths the paths
     */
    public void setPaths(PooledList<Vector2> paths) {
        this.paths = paths;
    }

    /**
     * Sets the velocity of this platform
     * @param velocity the new velocity
     */
    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    /**
     * Returns the velocity for this moving platform. If not a moving platform, velocity is 0
     * @return the velocity
     */
    public float getVelocity() {
        return velocity;
    }


    /**
     * Get the behavior of this platform
     * @return the behavior of this platform
     */
    public int getProperty() {
        return property;
    }

    /**
     * Set the behavior of this platform
     * @param property the behavior
     */
    public void setProperty(int property) {
        this.property = property;
    }

    /**
     * Returns the paths
     * @return the paths
     */
    public PooledList<Vector2> getPaths() {
        return this.paths;
    }

    /**
     * Allows for animated character motions. It sets the texture to prepare to draw.
     * This method overrides the setTexture method in SimpleObstacle
     */
    public void setTexture(TextureRegion textureRegion) {
        texture = textureRegion;
        actualTexture = textureRegion.getTexture();
        entirePixelWidth = actualTexture.getWidth();
        if (entirePixelWidth < framePixelWidth) {
            entirePixelWidth = framePixelWidth;
        }
        // For something that is not a platform, make it only 1 animation frame
        if (actualTexture.getHeight() > framePixelWidth*2) {
            framePixelWidth = entirePixelWidth;
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
     * Draws the physics object.
     * This method overrides the draw method in SimpleObstacle
     *
     * @param canvas Drawing context
     */
    public void draw(GameCanvas canvas) {
        if (texture != null) {
            animator.setFrame((int)animeframe);
            canvas.draw(animator, Color.WHITE, origin.x, origin.y,getX()*drawScale.x,getY()*drawScale.y,getAngle(),
                    1.0f, 1.0f);
        }
    }

    /**
     * Draws the physics object with tint.
     * This method overrides the drawWithTint method in SimpleObstacle
     *
     * @param canvas Drawing context
     * @param tint Tint to apply
     */
    public void drawWithTint(GameCanvas canvas, Color tint) {
        if (texture != null) {
            animator.setFrame((int)animeframe);
            canvas.draw(animator, tint, origin.x, origin.y,getX()*drawScale.x,getY()*drawScale.y,getAngle(),
                    1.0f, 1.0f);
        }
    }

    public void update(float dt) {
        // Increase animation frame
        animeframe += animationSpeed;
        if (animeframe >= numAnimFrames) {
            animeframe = 0;
        }
        super.update(dt);
    }

}

