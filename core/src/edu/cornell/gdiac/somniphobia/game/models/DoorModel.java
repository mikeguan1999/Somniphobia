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

import java.util.ArrayList;
import java.util.List;

public class DoorModel extends BoxObstacle {

    private ParticleModel flame;

    public DoorModel(float gX, float gY, float gWidth, float gHeight){
        super(gX, gY, gWidth, gHeight);

        this.flame = new ParticleModel();
        flame.create("platform/doorParticleEmitter", "platform/");
        flame.scaleParticles(2);
    }

    public void draw(GameCanvas canvas) {
        flame.startParticles();
        flame.render(getX()*drawScale.x, getY()*drawScale.y, canvas.getBatch(), 0.016f);
        super.draw(canvas);
    }

}

