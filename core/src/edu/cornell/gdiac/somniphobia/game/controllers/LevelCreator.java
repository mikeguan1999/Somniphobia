package edu.cornell.gdiac.somniphobia.game.controllers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import edu.cornell.gdiac.somniphobia.GameCanvas;
import edu.cornell.gdiac.somniphobia.WorldController;
import edu.cornell.gdiac.util.PooledList;


public class LevelCreator extends WorldController {
    /** Width of the game world in Box2d units */
    protected static final float DEFAULT_WIDTH  = 32.0f;
    /** Height of the game world in Box2d units */
    protected static final float DEFAULT_HEIGHT = 18.0f;
    /** The default value of gravity (going down) */
    protected static final float DEFAULT_GRAVITY = 0f;


    class Platform {
        int posX;
        int posY;
        int width;
        int height;
        public Platform(int posX, int posY, int width, int height) {
            this.posX = posX;
            this.posY = posY;
            this.width = width;
            this.height = height;
        }
    }

    class Level {
        int width;
        int height;
        PooledList<Platform> platformList;
        public Level(PooledList<Platform> platformList) {
            this.platformList = platformList;
        }
        // TODO: Add platform
        public void addPlatform() {

        }
        // TODO: Delete platform
        public void deletePlatform() {

        }
    }

    public LevelCreator() {
        super(DEFAULT_WIDTH,DEFAULT_HEIGHT,DEFAULT_GRAVITY);
        setDebug(false);
        setComplete(false);
        setFailure(false);
    }

    public void createSidebar() {
//        Stage stage = new Stage(new ScreenViewport(camera));
//		Table table= new Table();
        Batch b = canvas.getBatch();

        Label.LabelStyle labelStyle = new Label.LabelStyle(displayFont, Color.BLACK);

        float current = 0;
        float max = 0;
        float min = 0;

        Slider.SliderStyle style =
                new Slider.SliderStyle(new TextureRegionDrawable(sliderBarTexture), new TextureRegionDrawable(sliderKnobTexture));
        BitmapFont font = displayFont;
        font.getData().setScale(.3f, .3f);
        labelStyle = new Label.LabelStyle(font, Color.BLACK);

        //Dash Velocity

        Slider s = new Slider(min, max, 0.1f, false, style);
        s.setValue(current);
        s.setPosition(10, 500);
        stage.addActor(s);

        final Label test1 = new Label("Dash Velocity: " + avatar.getDashVelocity(), labelStyle);
        test1.setPosition(10, 532);
        s.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Slider s = (Slider) actor;
                float f = s.getValue();
                System.out.println("Dash Velocity : " + f);
                somni.setDashVelocity(f);
                phobia.setDashVelocity(f);
                combined.setDashVelocity(f);
                test1.setText("Dash Velocity: " + f);
            }
        });
//        sliders[0] = s;
//        labels[0] = test1;
    }

    @Override
    public void reset() {

    }

    @Override
    public void update(float dt) {

    }

    public void setCanvas(GameCanvas canvas) {
        this.canvas = canvas;
        this.scale.x = canvas.getWidth()/bounds.getWidth();
        this.scale.y = canvas.getHeight()/bounds.getHeight();
    }


    // TODO: Implement

}
