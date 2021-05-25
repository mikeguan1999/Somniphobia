package edu.cornell.gdiac.somniphobia.game.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.*;
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
    public void render(float x, float y, Batch batch) {
        effect.setPosition(x, y);
        effect.update(Gdx.graphics.getDeltaTime());
//        System.out.println(Gdx.graphics.getDeltaTime());

        effect.draw(batch);
    }

    /**
     * Draws the particles onto the screen with tint
     *
     * @param x the x coordinate for the particle origin
     * @param y the y coordinate for the particle origin
     *
     */
    public void renderTint(float x, float y, Batch batch, Color tint) {
        batch.setColor(tint);
        effect.setPosition(x, y);
        effect.update(Gdx.graphics.getDeltaTime());

        effect.draw(batch);
        batch.setColor(Color.WHITE);
    }

    /**
     * Draws the particles onto the screen but you can add your own dt
     *
     * @param x the x coordinate for the particle origin
     * @param y the y coordinate for the particle origin
     * @param dt the time change
     *
     */
    public void render(float x, float y, Batch batch, float dt) {
        effect.setPosition(x, y);
        effect.update(dt);

        effect.draw(batch);
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
     * Initializes the effect for particles by specifying the emitter
     * and particle paths
     */
    public void create(String emitterPath, String particlePath) {
        batch = new SpriteBatch();

        effect = new ParticleEffect();
        effect.load(Gdx.files.internal(emitterPath),
                Gdx.files.internal(particlePath));
    }

    /**
     * Removes the particles from the screen
     */
    public void hide() {
    }

    /**
     * Starts a particle effect
     */
    public void startParticles() {
        effect.start();
    }

    /**
     * Change size of particles
     *
     * @param scaleFactor the factor to scale particles  by
     *
     */
    public void scaleParticles(float scaleFactor) {
        effect.scaleEffect(scaleFactor);
    }

    /**
     * Change size of particles
     *
     * @param duration the duration that the flame exists for
     *
     */
    public void setDuration(int duration) {
        effect.setDuration(duration);
    }


}
