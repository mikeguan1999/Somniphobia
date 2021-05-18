package edu.cornell.gdiac.somniphobia.game.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.physics.box2d.*;

import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.somniphobia.*;
import edu.cornell.gdiac.somniphobia.obstacle.*;
import edu.cornell.gdiac.util.FilmStrip;

/**
 * Player running dust particles for the plaform game.
 *
 */

public class ParticleModel {

    /** Batch for drawing */
    private SpriteBatch batch;
    /** Effect object used for creating the particles */
    private ParticleEffect effect;

    /**
     * Constructor for the particle model
     *
     */
    public ParticleModel() {
    }

    /**
     * Draws the particles onto the screen
     *
     * @param x the x coordinate for the particle origin
     * @param y the y coordinate for the particle origin
     *
     */
    public void render(float x, float y) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        effect.setPosition(x, y);
        effect.start();
        effect.draw(batch);
        batch.end();
    }

    /**
     * Initializes the effect for particles
     */
    public void create() {
        batch = new SpriteBatch();

        effect = new ParticleEffect();
        effect.load(Gdx.files.internal("platform/particleEmitter"),
                Gdx.files.internal("platform/"));
    }

    /**
     * Removes the particles from the screen
     */
    public void hide() {
    }


}
