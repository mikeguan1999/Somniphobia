package edu.cornell.gdiac.somniphobia.game.models;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import edu.cornell.gdiac.somniphobia.obstacle.BoxObstacle;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.physics.box2d.*;

import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.somniphobia.*;
import edu.cornell.gdiac.somniphobia.obstacle.*;
import edu.cornell.gdiac.util.PooledList;

public class PlatformModel extends BoxObstacle {

    /** Behavior tag constants */
    public final static int normal = 1;
    public final static int harming = 2;
    public final static int crumbling = 3;
    public final static int holdOnly = 4;

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
    /** Texture of the platform*/
    private TextureRegion texture;

    private int type;

    /** Whether the platform is spiked **/
    private boolean spiked;

    /** Whether the platform is raining **/
    private boolean raining;

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

    /** Path for a moving obstacle **/
    private PooledList<Vector2> paths;

    /** scale*/
    public float scale;





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
        this.spiked = false;
        this.property = 0;
    }

    public float getLeftX() {
        return getX() - getWidth() / 2;
    }
    public float getBottomY() {
        return getY() - getHeight() / 2;
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

}

