package edu.cornell.gdiac.somniphobia.game.models;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import edu.cornell.gdiac.somniphobia.obstacle.BoxObstacle;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.physics.box2d.*;

import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.somniphobia.*;
import edu.cornell.gdiac.somniphobia.obstacle.*;

public class PlatformModel extends BoxObstacle {

    /** Width of the platform*/
    public float width;
    /** Height of the platform*/
    public float height;

    /** X position*/
    public float xpos;
    /** Y position*/
    public float ypos;

    /** Filter*/
    public Filter filter;
    /** Texture of the platform*/
    public TextureRegion texture;

    public int type;

    /** Density position*/
    public float density;
    /** Friction position*/
    public float friction;
    /** restitution position*/
    public float restitution;

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

    public boolean hurts(){
        if(type == 0){
            return false;
        }
        else{
            return true;
        }
    }

}

