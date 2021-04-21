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

    /** Density position*/
    private float density;
    /** Friction position*/
    private float friction;
    /** restitution position*/
    private float restitution;

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
     * Returns the paths
     * @return the paths
     */
    public PooledList<Vector2> getPaths() {
        return this.paths;
    }

    public boolean hurts(){
        if(type == 0){
            return false;
        }
        else{
            return true;
        }
    }

}

