package edu.cornell.gdiac.somniphobia.game.controllers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import edu.cornell.gdiac.assets.AssetDirectory;
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

    private TextureRegion backgroundTexture;
    private boolean initialized;

    Label[] labels;



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

//        world.setContactListener();

    }

    public void initialize() {
//        System.out.println("initialized\n\n");
        createSidebar();
    }

    public void createSidebar() {
        labels = new Label[1];
//        Stage stage = new Stage(new ScreenViewport(camera));
//		Table table= new Table();
//        Batch b = canvas.getBatch();

        Label.LabelStyle labelStyle = new Label.LabelStyle(displayFont, Color.BLACK);
//        Stage stage = new Stage(canvas.getViewPort());


        float current = 0;
        float max = 0;
        float min = 0;

//        Slider.SliderStyle style =
//                new Slider.SliderStyle(new TextureRegionDrawable(sliderBarTexture), new TextureRegionDrawable(sliderKnobTexture));
        BitmapFont font = displayFont;
        System.out.println(font);
        font.getData().setScale(.3f, .3f);
        labelStyle = new Label.LabelStyle(font, Color.BLACK);


        final Label test1 = new Label("Menu Interface ", labelStyle);
        test1.setPosition(10, 532);
        labels[0] = test1;
//        l.draw(b, 1.0f);
    }

    public void draw(float dt) {
        canvas.begin();
//        canvas.draw(backgroundTexture, Color.WHITE, cameraX, cameraY, canvas.getWidth(), canvas.getHeight());
        canvas.draw(backgroundTexture, 0, 0);

        canvas.end();

        canvas.begin();
        labels[0].draw(canvas.getBatch(), 1.0f);
        canvas.end();

    }

    @Override
    public void reset() {
        Vector2 gravity = new Vector2(world.getGravity() );
        objects.clear();
        addQueue.clear();
        world.dispose();


        world = new World(gravity,false);
        setComplete(false);
        setFailure(false);
        initialize();
    }

    @Override
    public void update(float dt) {

    }

    public void setCanvas(GameCanvas canvas) {
        this.canvas = canvas;
        this.scale.x = canvas.getWidth()/bounds.getWidth();
        this.scale.y = canvas.getHeight()/bounds.getHeight();
    }

    public void gatherAssets(AssetDirectory directory) {
        backgroundTexture = new TextureRegion(directory.getEntry("platform:background_light", Texture.class));
        super.gatherAssets(directory);
    }



    // TODO: Implement

}
