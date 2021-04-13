package edu.cornell.gdiac.somniphobia.game.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.somniphobia.GameCanvas;
import edu.cornell.gdiac.somniphobia.InputController;
import edu.cornell.gdiac.somniphobia.WorldController;
import edu.cornell.gdiac.somniphobia.game.models.CharacterModel;
import edu.cornell.gdiac.somniphobia.obstacle.BoxObstacle;
import edu.cornell.gdiac.somniphobia.obstacle.Obstacle;
import edu.cornell.gdiac.somniphobia.obstacle.ObstacleSelector;
import edu.cornell.gdiac.util.PooledList;
import java.lang.Math;




public class LevelCreator extends WorldController {
    /** Width of the game world in Box2d units */
    protected static final float DEFAULT_WIDTH  = 32.0f;
    /** Height of the game world in Box2d units */
    protected static final float DEFAULT_HEIGHT = 18.0f;
    /** The default value of gravity (going down) */
    protected static final float DEFAULT_GRAVITY = 0f;

    protected static final int DEFAULT_WORLD_WIDTH = 100;
    protected static final int DEFAULT_WORLD_HEIGHT = 100;


    /** Mouse selector to move the platforms */
    private ObstacleSelector selector;

    private Batch batch;

    /** TextureRegion variables */
    private TextureRegion backgroundTexture;
    private TextureRegion platTexture;
    private TextureRegion crosshairTexture;
    private Texture buttonUpTexture;
    private Texture buttonDownTexture;
    private Texture textBackground;
    private Texture selectBackground;
    private Texture sliderBarTexture;
    private Texture sliderKnobTexture;
    private Table menuTable;


    private boolean moving = false;

    /** Cache for selected obstacle */
    private Obstacle selectedObstacle;
    /** Cache for obstacle x position */
    private int obstacleX;
    /** Cache for obstacle y position */
    private int obstacleY;

    private TextField platformWidth;
    private TextField platformHeight;


    /** Current level we are modifying*/
    private Level level;


    class Platform extends BoxObstacle {
        float posX;
        float posY;
        float width;
        float height;
        public Platform(float posX, float posY, float width, float height) {
            super(posX,posY,width,height);
            this.posX = posX;
            this.posY = posY;
            this.width = width;
            this.height = height;
        }
    }

    class Level {
        int width;
        int height;
        LevelCreator levelCreator;
        PooledList<Obstacle> platformList;
        public Level(int width, int height, PooledList<Obstacle> platformList, LevelCreator levelCreator) {

            this.platformList = platformList;
            this.levelCreator = levelCreator;
        }
        // TODO: Add platform
        public void addPlatform(float posX, float posY, float width, float height) {


            platformList.add(new Platform(posX, posY, width, height));
            float[] bounds = {7.0f, 3.0f, 13.0f, 3.0f, 13.0f, 2.0f, 7.0f, 2.0f };
            Platform obj = new Platform(posX + width / 2, posY + height / 2, width,height);
            obj.deactivatePhysics(this.levelCreator.world);
            obj.setDrawScale(scale);
            TextureRegion newXTexture = new TextureRegion(platTexture);
            newXTexture.setRegion(posX, posY, posX + width, posY + height);
            obj.setTexture(newXTexture);
            addObject(obj);

        }
        // TODO: Delete platform
        public void deletePlatform(Obstacle o) {
            platformList.remove(o);

            objects.remove(o);
        }

        public PooledList<Obstacle> getPlatformList() {
            return platformList;
        }
    }

    public LevelCreator() {
        super(DEFAULT_WIDTH,DEFAULT_HEIGHT,DEFAULT_GRAVITY);
        setDebug(false);
        setComplete(false);
        setFailure(false);


        level = new Level(DEFAULT_WORLD_WIDTH, DEFAULT_WORLD_HEIGHT, new PooledList<Obstacle>(), this);



//        world.setContactListener();

    }

    public void initialize() {
//        System.out.println("initialized\n\n");

        createSidebar();
        selector= new ObstacleSelector(world,1 ,1);
        this.setDebug(true);
        selector.setTexture(crosshairTexture);
        selector.setDrawScale(scale);


        float[] bounds = {7.0f, 3.0f, 13.0f, 3.0f, 13.0f, 2.0f, 7.0f, 2.0f };
        float width = bounds[2]-bounds[0];
        float height = bounds[5]-bounds[1];
        Platform obj = new Platform(bounds[0] + width / 2, bounds[1] + height / 2,width,height);
        //obj.setBodyType(BodyDef.BodyType.DynamicBody);

        obj.setDrawScale(scale);
        TextureRegion newXTexture = new TextureRegion(platTexture);
        newXTexture.setRegion(bounds[0], bounds[1], bounds[4], bounds[5]);
        obj.setTexture(newXTexture);
        //addObject(obj);


//        float[] bounds = {7.0f, 3.0f, 13.0f, 3.0f, 13.0f, 2.0f, 7.0f, 2.0f };
//        float width = bounds[2]-bounds[0];
//        float height = bounds[5]-bounds[1];
//
//        Platform obj = new Platform(bounds[0] + width / 2, bounds[1] + height / 2,width,height);
//        //obj.setBodyType(BodyDef.BodyType.DynamicBody);
//
//        obj.setDrawScale(scale);
//        TextureRegion newXTexture = new TextureRegion(platTexture);
//        newXTexture.setRegion(bounds[0], bounds[1], bounds[4], bounds[5]);
//        obj.setTexture(newXTexture);
//        addObject(obj);
    }

    public void createSidebar() {
        Stage stage = new Stage(new ScreenViewport(canvas.getCamera()));
        menuTable = new Table();
        batch = canvas.getBatch();

        Label.LabelStyle labelStyle;


//        Slider.SliderStyle style =
//                new Slider.SliderStyle(new TextureRegionDrawable(sliderBarTexture), new TextureRegionDrawable(sliderKnobTexture));
        BitmapFont font = displayFont;
        font.setColor(Color.BLACK);
        System.out.println(font);
        font.getData().setScale(.3f, .3f);
        labelStyle = new Label.LabelStyle(font, Color.BLACK);


        Label label1 = new Label("Level Editor", labelStyle);

        ImageTextButton.ImageTextButtonStyle buttonStyle = new ImageTextButton.ImageTextButtonStyle(new TextureRegionDrawable(buttonUpTexture), new TextureRegionDrawable(buttonDownTexture), null, font);
        buttonStyle.fontColor = Color.BLACK;

        ImageTextButton button1 = new ImageTextButton("Remove Object", buttonStyle);

        button1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                level.deletePlatform(selectedObstacle);
            }
        });


        Label labelWidth = new Label("Width: ", labelStyle);
        Label labelHeight = new Label("Height: ", labelStyle);


        TextField.TextFieldStyle style = new TextField.TextFieldStyle();
        style.font = font;
        style.fontColor = Color.BLACK;
        style.background = new TextureRegionDrawable(textBackground);
        platformWidth = new TextField(null, style);
        platformWidth.setText("2");
        platformWidth.setMaxLength(4);


        platformHeight = new TextField(null, style);
        platformHeight.setText("2");
        platformHeight.setMaxLength(4);





        ImageTextButton buttonSelect = new ImageTextButton("Add Object", buttonStyle);
        buttonSelect.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                float width = Float.parseFloat(platformWidth.getText());
                float height = Float.parseFloat(platformHeight.getText());
                level.addPlatform(10, 10, width, height);
            }
        });

        ImageTextButton button2 = new ImageTextButton("Save", buttonStyle);
        button2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("button press!");
            }
        });

        ImageTextButton button3 = new ImageTextButton("Play", buttonStyle);
        button3.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("button press!");
            }
        });

        ImageTextButton button4 = new ImageTextButton("Load Dream", buttonStyle);
        button4.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("button press!");
            }
        });

        ImageTextButton button5 = new ImageTextButton("Save Dream", buttonStyle);
        button5.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("button press!");
            }
        });


        ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle(new TextureRegionDrawable(selectBackground),
                new TextureRegionDrawable(sliderBarTexture), new TextureRegionDrawable(sliderKnobTexture),
                new TextureRegionDrawable(sliderBarTexture), new TextureRegionDrawable(sliderKnobTexture));

        List.ListStyle listStyle = new List.ListStyle(font, Color.BLACK, Color.RED, new TextureRegionDrawable(selectBackground));
        SelectBox.SelectBoxStyle selectBoxStyle = new SelectBox.SelectBoxStyle(font, Color.BLACK, null, scrollPaneStyle, listStyle);
        final SelectBox<String> tileSelect = new SelectBox<String>(selectBoxStyle);
        tileSelect.setItems("light", "dark", "all");
        tileSelect.addListener( new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String newValue = tileSelect.getSelected();
//                valueChanged(newValue);
                System.out.println("hi");
//                refresh();
            }
        });



        menuTable.add(label1).colspan(3).center();
        menuTable.row();

        menuTable.add(button1).colspan(3).center();
        menuTable.row();
        menuTable.add(labelWidth);
        menuTable.add(platformWidth).width(60);
        menuTable.add(labelHeight);
        menuTable.add(platformHeight).width(60);
        menuTable.row();
        menuTable.add(buttonSelect).colspan(3).center();
        menuTable.row();
        menuTable.add(button2);
        menuTable.add(button3);
        menuTable.row();
        menuTable.add(button4).colspan(3).center();
        menuTable.row();
        menuTable.add(button5).colspan(3).center();
        menuTable.row();
        menuTable.add(tileSelect);

//        menuTable.setHeight(200);



        stage.addActor(menuTable);
        menuTable.setPosition(canvas.getWidth() - 200, canvas.getHeight()/2);
        Gdx.input.setInputProcessor(stage);

//        l.draw(b, 1.0f);
    }

    public void draw(float dt) {
        canvas.begin();
//        canvas.draw(backgroundTexture, Color.WHITE, cameraX, cameraY, canvas.getWidth(), canvas.getHeight());
        canvas.draw(backgroundTexture, 0, 0);
        selector.draw(canvas);
        canvas.end();

        canvas.begin();
//        labels[0].draw(canvas.getBatch(), 1.0f);
//        canvas.setBlendState(GameCanvas.BlendState.NO_PREMULT);
        menuTable.draw(batch, 0.8f);
//        canvas.setBlendState(GameCanvas.BlendState.ALPHA_BLEND);
//        s.draw(batch, 1.0f);
        canvas.end();

        canvas.begin();
        for(Obstacle obj : objects) {
            // Ignore characters which we draw separately
            if (!(obj instanceof CharacterModel)) {
                obj.draw(canvas);
            }
        }
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
        // Move an object if touched
        InputController input = InputController.getInstance();
        if (input.didTertiary() && !selector.isSelected()) {
            if(selector.select(input.getCrossHair().x,input.getCrossHair().y)){
                moving = true;
                selectedObstacle = selector.getObstacle();
            }
        } else if (selector.isSelected() && input.didDelete()) {
            Obstacle o = selector.getObstacle();
            selector.deselect();
            moving = false;
            objects.remove(o);
        }
        else if (!input.didTertiary() && selector.isSelected()) {
            moving = false;
            selector.deselect();
        } else {
            selector.moveTo(input.getCrossHair().x,input.getCrossHair().y);
        }
        for(Obstacle obj : objects) {
            // Ignore characters which we draw separately
            if (!(obj instanceof CharacterModel)) {
                if (!(selector.isSelected() && obj == selector.getObstacle())){
                    Vector2 pos = obj.getPosition();
                    float x;
                    float y;
                    if(obj instanceof BoxObstacle &&  ((BoxObstacle) obj).getWidth()%2 == 0 && (pos.x) % 1.0f != 0f){
                        x = (float) (Math.round(pos.x*1d)/1d);
                    }else if(obj instanceof BoxObstacle && ((BoxObstacle) obj).getWidth()%2 != 0 && pos.x % 1.0f != 0.5f){
                        x = (float)(Math.round((pos.x+.5f)*1d)/1d)-.5f;
                    }else{
                        x = pos.x;
                    }
                    if(obj instanceof BoxObstacle &&  ((BoxObstacle) obj).getHeight()%2 != 0 && (pos.y) % 1.0f != 0f){
                        y = (float) (Math.round((pos.y+.5f)*1d)/1d)-.5f;
                    }else if(obj instanceof BoxObstacle && ((BoxObstacle) obj).getHeight()%2 == 0 && pos.y % 1.0f != 0.5f){
                        y = (float)(Math.round(pos.y*1d)/1d) ;
                    }else{
                        y = pos.y;
                    }
                    obj.setPosition(x, y);
                    obj.setVX(0);
                    obj.setVY(0);
                    obj.setLinearVelocity(new Vector2(0, 0));
                    obj.setMass(10000000f);
                }
                else {
                    obj.resetMass();
                }
                //System.out.println(obj.getPosition());
            }
        }

    }

    public void setCanvas(GameCanvas canvas) {
        this.canvas = canvas;
        this.scale.x = canvas.getWidth()/bounds.getWidth();
        this.scale.y = canvas.getHeight()/bounds.getHeight();
    }

    public void gatherAssets(AssetDirectory directory) {
        backgroundTexture = new TextureRegion(directory.getEntry("platform:background_light", Texture.class));
        platTexture = new TextureRegion(directory.getEntry("shared:light", Texture.class));
        crosshairTexture = new TextureRegion(directory.getEntry("platform:bullet", Texture.class));
        buttonUpTexture = directory.getEntry( "level_editor:buttonUp", Texture.class);
        buttonDownTexture = directory.getEntry( "level_editor:buttonDown", Texture.class);
        textBackground = directory.getEntry( "level_editor:text_background", Texture.class);
        selectBackground = directory.getEntry("level_editor:select_background", Texture.class);

        sliderBarTexture = directory.getEntry( "platform:sliderbar", Texture.class);
        sliderKnobTexture = directory.getEntry( "platform:sliderknob", Texture.class);


        super.gatherAssets(directory);
    }



    // TODO: Implement


}
