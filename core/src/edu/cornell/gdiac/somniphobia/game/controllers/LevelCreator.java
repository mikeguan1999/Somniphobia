package edu.cornell.gdiac.somniphobia.game.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
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
import edu.cornell.gdiac.somniphobia.obstacle.SimpleObstacle;
import edu.cornell.gdiac.util.PooledList;

import java.lang.Math;
import java.util.ArrayList;


public class LevelCreator extends WorldController {
    /** Width of the game world in Box2d units */
    protected static final float DEFAULT_WIDTH  = 32.0f;
    /** Height of the game world in Box2d units */
    protected static final float DEFAULT_HEIGHT = 18.0f;
    /** The default value of gravity (going down) */
    protected static final float DEFAULT_GRAVITY = 0f;

    protected static final int DEFAULT_WORLD_WIDTH = 1400;
    protected static final int DEFAULT_WORLD_HEIGHT = 800;

    protected static final float[] SOMNI_DEFAULT_POS = new float[]{5.0f, 5.0f};
    protected static final float[] PHOBIA_DEFAULT_POS = new float[]{7.0f, 5.0f};
    protected static final float[] GOAL_DEFAULT_POS = new float[]{9.0f, 5.0f};
    protected static final float[] CHARACTER_DIMENSIONS = new float[]{1.0f, 2.0f};
    protected static final float[] GOAL_DIMENSIONS = new float[]{2.0f, 4.0f};

    private int worldWidth;
    private int worldHeight;

    /** Mouse selector to move the platforms */
    private ObstacleSelector selector;
    /** List to hold all platforms */
    private PooledList<Platform> platformList = new PooledList<Platform>();

    private Batch batch;

    /** TextureRegion variables */
    TextureRegion[] backgrounds;
    private TextureRegion backgroundTexture;
    private TextureRegion lightTexture;
    private TextureRegion darkTexture;
    private TextureRegion allTexture;
    private TextureRegion somniTexture;
    private TextureRegion phobiaTexture;
    private TextureRegion goalTexture;
    private TextureRegion [] platTexture;
    private TextureRegion crosshairTexture;
    private Texture buttonUpTexture;
    private Texture buttonDownTexture;
    private Texture textBackground;
    private Texture selectBackground;
    private Texture sliderBarTexture;
    private Texture sliderKnobTexture;
    private Texture dropdownTexture;
    private Texture dropdownDownTexture;
    private Texture cursorTexture;

    private boolean platformSelected;
    private boolean editSelected;
//    private boolean characterSelected;
//    private boolean doorSelected;


    private Table menuTable;

    private ImageTextButton lightPlatformSelect;
    private ImageTextButton darkPlatformSelect;
    private ImageTextButton allPlatformSelect;

    private ImageTextButton widthInc;
    private ImageTextButton widthDec;
    private ImageTextButton heightInc;
    private ImageTextButton heightDec;

    /** Tag constants */
    protected final static int lightTag = 0;
    protected final static int darkTag = 1;
    protected final static int allTag = 2;
    protected final static int somniTag = 3;
    protected final static int phobiaTag = 4;
    protected final static int goalTag = 5;

    private int currBackground;
    private int currPlatformSelection;


    private boolean moving = false;

    /** Cache for selected obstacle */
    private Obstacle selectedObstacle;
    /** Cache for obstacle x position */
    private int obstacleX;
    /** Cache for obstacle y position */
    private int obstacleY;

    private TextField platformWidth;
    private TextField platformHeight;
    private TextField worldWidthText;
    private TextField worldHeightText;



    private TextField loadPath;



    class Platform extends BoxObstacle {
        int tag;
        float[] position;
        ArrayList<String> properties = new ArrayList<String>();
        ArrayList<String> behaviors = new ArrayList<String>();
        public Platform(int tag, float posX, float posY, float width, float height, ArrayList<String> properties,
                        ArrayList<String> behaviors) {
            super(posX + width / 2, posY + height / 2, width, height);
            this.position = new float[]{posX, posY, width, height};
            this.tag = tag;
            this.properties = properties;
            this.behaviors = behaviors;
        }
    }
    public void addPlatform(int tag, float posX, float posY, float width, float height,
                            ArrayList<String> properties, ArrayList<String> behaviors) {
        Platform obj = new Platform(tag, posX, posY, width, height, properties, behaviors);
        platformList.add(obj);
        obj.deactivatePhysics(world);
        obj.setDrawScale(scale);
        TextureRegion newXTexture;
        if(tag < somniTag) {
            newXTexture = new TextureRegion(platTexture[tag]);
            newXTexture.setRegion(posX, posY, posX + width, posY + height);
        } else {
            newXTexture = platTexture[tag];
        }
        obj.setTexture(newXTexture);
        addObject(obj);
        selectedObstacle = obj;
    }

    public void deletePlatform(Obstacle o) {
        platformList.remove(o);
        o.deactivatePhysics(world);
        objects.remove(o);
    }

    public LevelCreator() {
        super(DEFAULT_WIDTH,DEFAULT_HEIGHT,DEFAULT_GRAVITY);
        setDebug(false);
        setComplete(false);
        setFailure(false);

        this.worldWidth = DEFAULT_WORLD_WIDTH;
        this.worldHeight = DEFAULT_WORLD_HEIGHT;

    }

    public void hideDropdowns() {
        platformSelected = false;
        editSelected = false;
//        darkPlatformSelected = false;
//        allPlatformSelected = false;
//        characterSelected = false;
//        doorSelected = false;
    }


    public void initialize() {
        hideDropdowns();
        createSidebar();
        selector= new ObstacleSelector(world,1 ,1);
        this.setDebug(true);
        selector.setTexture(crosshairTexture);
        selector.setDrawScale(scale);
        currBackground = 0;
        // Add Somni
        addPlatform(somniTag, SOMNI_DEFAULT_POS[0], SOMNI_DEFAULT_POS[1], CHARACTER_DIMENSIONS[0],
                CHARACTER_DIMENSIONS[1], null, null);
        // Add Phobia
        addPlatform(phobiaTag, PHOBIA_DEFAULT_POS[0], PHOBIA_DEFAULT_POS[1], CHARACTER_DIMENSIONS[0],
                CHARACTER_DIMENSIONS[1], null, null);
        // Add Goal
        addPlatform(goalTag, GOAL_DEFAULT_POS[0], GOAL_DEFAULT_POS[1], GOAL_DIMENSIONS[0],
                GOAL_DIMENSIONS[1], null, null);
        currPlatformSelection = 0;

    }

    public void createSidebar() {
        final Stage stage = new Stage(new ScreenViewport(canvas.getCamera()));
        menuTable = new Table();
        batch = canvas.getBatch();

        Label.LabelStyle labelStyle;

        BitmapFont font = displayFont;
        font.setColor(Color.BLACK);
        font.getData().setScale(.3f, .3f);
        labelStyle = new Label.LabelStyle(font, Color.BLACK);
        Label label1 = new Label("Level Editor", labelStyle);

        // Widget Styles
        ImageTextButton.ImageTextButtonStyle buttonStyle = new ImageTextButton.ImageTextButtonStyle(new TextureRegionDrawable(buttonUpTexture), new TextureRegionDrawable(buttonDownTexture), null, font);
        buttonStyle.fontColor = Color.BLACK;

        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle(font, Color.BLACK, new TextureRegionDrawable(cursorTexture),
                new TextureRegionDrawable(textBackground), new TextureRegionDrawable(textBackground));

        ImageTextButton.ImageTextButtonStyle dropDownStyle = new ImageTextButton.ImageTextButtonStyle(new TextureRegionDrawable(dropdownTexture),
                new TextureRegionDrawable(dropdownTexture), new TextureRegionDrawable(dropdownDownTexture), font);
        dropDownStyle.fontColor = Color.BLACK;

        final ImageTextButton.ImageTextButtonStyle selectButtonStyle = new ImageTextButton.ImageTextButtonStyle(new TextureRegionDrawable(buttonUpTexture),
                new TextureRegionDrawable(buttonDownTexture), new TextureRegionDrawable(buttonDownTexture), font);


        // World Dimensions
        Label labelWorldDimension = new Label("World Dimensions: ", labelStyle);

        worldWidthText = new TextField(null, textFieldStyle);
        worldWidthText.setText(String.valueOf(DEFAULT_WORLD_WIDTH));
        worldWidthText.setMaxLength(4);

        worldHeightText = new TextField(null, textFieldStyle);
        worldHeightText.setText(String.valueOf(DEFAULT_WORLD_HEIGHT));
        worldHeightText.setMaxLength(4);


        // Remove Object
        ImageTextButton setWorldDimension = new ImageTextButton("Set Dimensions", buttonStyle);

        setWorldDimension.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                worldWidth = Integer.parseInt(worldWidthText.getText());
                worldHeight = Integer.parseInt(worldHeightText.getText());
            }
        });


        // Remove Object
        ImageTextButton removeObjectButton = new ImageTextButton("Remove Object", buttonStyle);

        removeObjectButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                if (selectedObstacle != null) {
                    deletePlatform(selectedObstacle);
                }
            }
        });



        final ImageTextButton platformDropdown = new ImageTextButton("Platform", dropDownStyle);
        platformDropdown.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if(!platformSelected) {
                    hideDropdowns();
                }
                platformSelected = !platformSelected;
                createSidebar();
            }
        });

        platformDropdown.setChecked(platformSelected);


        lightPlatformSelect = new ImageTextButton("Light", selectButtonStyle);
        lightPlatformSelect.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                setSelectedColor(lightTag);
            }
        });
        lightPlatformSelect.setChecked(true);

        darkPlatformSelect = new ImageTextButton("Dark", selectButtonStyle);
        darkPlatformSelect.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                setSelectedColor(darkTag);
            }
        });

        allPlatformSelect = new ImageTextButton("All", selectButtonStyle);
        allPlatformSelect.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                setSelectedColor(allTag);
            }
        });
        

        Label labelDimension = new Label("Dimensions: ", labelStyle);


        platformWidth = new TextField(null, textFieldStyle);
        platformWidth.setText("2");
        platformWidth.setMaxLength(4);


        platformHeight = new TextField(null, textFieldStyle);
        platformHeight.setText("2");
        platformHeight.setMaxLength(4);

        ImageTextButton addPlatform = new ImageTextButton("Add", buttonStyle);
        addPlatform.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                float posX = canvas.getWidth()/canvas.PPM/2;
                float posY = canvas.getHeight()/canvas.PPM/2;
                float width = Float.parseFloat(platformWidth.getText());
                float height = Float.parseFloat(platformHeight.getText());
                float[] platformDimensions = new float[]{width, height};
                ArrayList<String> properties = new ArrayList<>();
                ArrayList<String> behaviors = new ArrayList<>();

                addPlatform(currPlatformSelection, posX, posY, width, height, behaviors, properties);
            }
        });

        ImageTextButton editButton = new ImageTextButton("Edit", buttonStyle);
        editButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (selectedObstacle instanceof Platform) {
                    Platform currPlatform = (Platform) selectedObstacle;


                    float posX = currPlatform.getX() - currPlatform.getWidth() / 2;
                    float posY = currPlatform.getY() - currPlatform.getHeight() / 2;
                    float width = Float.parseFloat(platformWidth.getText());
                    float height = Float.parseFloat(platformHeight.getText());
                    int tag = currPlatformSelection;
                    ArrayList<String> properties = currPlatform.properties;
                    ArrayList<String> behaviors = currPlatform.behaviors;


                    platformList.remove(currPlatform);
                    currPlatform.deactivatePhysics(world);
                    objects.remove(currPlatform);

                    addPlatform(tag, posX, posY, width, height, properties, behaviors);


                }
            }
        });
        
        ImageTextButton saveButton = new ImageTextButton("Save", buttonStyle);
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String fileName = loadPath.getText();
                LevelSerializer.serialize(fileName, worldWidth, worldHeight, platformList);
            }
        });

        ImageTextButton playButton = new ImageTextButton("Play", buttonStyle);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("button press!");
            }
        });




        ImageTextButton button4 = new ImageTextButton("Load Dream", buttonStyle);
        button4.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String path = loadPath.getText();

                JsonReader json = new JsonReader();

                JsonValue value = json.parse("levels/" + path);
                //TODO: load json from path
            }
        });

        loadPath = new TextField("path", textFieldStyle);

        ImageTextButton button5 = new ImageTextButton("Reset Dream", buttonStyle);
        button5.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("button press!");
                for (Obstacle obstacle : objects) {
                    obstacle.deactivatePhysics(world);
                }
                objects.clear();
                initialize();
            }
        });


        ImageTextButton switchBackgroundButton = new ImageTextButton("Switch Background", buttonStyle);
        switchBackgroundButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("switch background!");
                currBackground++;
                currBackground %= backgrounds.length;
            }
        });


        Table platformParamTable = new Table();

        platformParamTable.add(lightPlatformSelect).pad(0,5,0,5);
        platformParamTable.add(darkPlatformSelect).pad(0,5,0,5);
        platformParamTable.add(allPlatformSelect).pad(0,5,0,5);
        platformParamTable.row();
        platformParamTable.add(labelDimension).colspan(3).center();
        platformParamTable.row();
        platformParamTable.add(platformWidth).width(60);
        platformParamTable.add(platformHeight).width(60);
        platformParamTable.row();

        //Add all the widgets to the menu table
        menuTable.add(label1).colspan(3).center();
        menuTable.row();

        menuTable.add(labelWorldDimension).colspan(3).center();
        menuTable.row();
        menuTable.add(worldWidthText).width(60);
        menuTable.add(worldHeightText).width(60);
        menuTable.row();
        menuTable.add(setWorldDimension).colspan(3).center().pad(0,0,10,0);
        menuTable.row();

        menuTable.add(removeObjectButton).colspan(3).center();
        menuTable.row();
        menuTable.add(platformDropdown).width(300).height(50).colspan(3).center().pad(0, 0, 20, 0);
        menuTable.row();

        if (platformSelected) {
            platformParamTable.add(addPlatform).pad(0, 5, 20, 5);
            platformParamTable.add(editButton).pad(0, 5, 20, 5);
            platformParamTable.row();

            menuTable.add(platformParamTable).colspan(3).center();
            menuTable.row();
        }


        menuTable.pad(10);

        menuTable.add(saveButton).pad(0, 0, 20, 0);
        menuTable.add(playButton).pad(0, 0, 20, 0);
        menuTable.row();
        menuTable.add(loadPath).colspan(3).center();
        menuTable.row();
        menuTable.add(button4).colspan(3).center();
        menuTable.row();
        menuTable.add(button5).colspan(3).center();
        menuTable.row();

        menuTable.add(switchBackgroundButton).colspan(3).center();

        stage.addActor(menuTable);
        menuTable.setPosition(canvas.getWidth() - 200, canvas.getHeight()/2);
        Gdx.input.setInputProcessor(stage);

        //Turn off keyboard focus
        stage.getRoot().addCaptureListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (!(event.getTarget() instanceof TextField)) stage.setKeyboardFocus(null);
                return false;
            }
        });
    }

    /**
     * Sets the selectedObstacle
     */
    public void setSelectedColor(int tag) {
        if (selectedObstacle instanceof Platform) {
            lightPlatformSelect.setChecked(tag == lightTag);
            darkPlatformSelect.setChecked(tag == darkTag);
            allPlatformSelect.setChecked(tag == allTag);
            currPlatformSelection = tag;
        }

    }

    /**
     * Sets the selectedObstacle
     */
    public void setSelectedObstacle(Obstacle obstacle) {
        selectedObstacle = obstacle;
        if (selectedObstacle instanceof Platform) {
            Platform currPlatform = (Platform) selectedObstacle;
            platformWidth.setText(String.valueOf((int)currPlatform.getWidth()));
            platformHeight.setText(String.valueOf((int)currPlatform.getHeight()));
            setSelectedColor(currPlatform.tag);
        }

    }

    public void draw(float dt) {
        canvas.begin();
//        canvas.draw(backgroundTexture, Color.WHITE, cameraX, cameraY, canvas.getWidth(), canvas.getHeight());
        canvas.draw(backgrounds[currBackground], 0, 0);
        selector.draw(canvas);
        canvas.end();



        canvas.begin();
        for(Obstacle obj : objects) {
            // Ignore characters which we draw separately
            if (!(obj instanceof CharacterModel)) {
                if (obj == selectedObstacle) {
                    ((SimpleObstacle) obj).draw(canvas, Color.BLACK);
                }
                obj.draw(canvas);
            }
        }
        canvas.end();

        canvas.begin();
        menuTable.draw(batch, 0.75f);
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
        Camera camera = canvas.getCamera();
        displayFont.getData().setScale(.3f);
        InputController input = InputController.getInstance();
        if (input.didTertiary() && !selector.isSelected()) {
            if(selector.select((camera.position.x- canvas.getWidth()/2) / canvas.PPM + input.getCrossHair().x ,
            (camera.position.y- canvas.getHeight()/2) / canvas.PPM + input.getCrossHair().y  )){
                moving = true;
                setSelectedObstacle(selector.getObstacle());
            }
        } else if (!input.didTertiary() && selector.isSelected()) {
            moving = false;
            selector.deselect();
        } else {
//            selector.moveTo(input.getCrossHair().x,input.getCrossHair().y);
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
                    for(Platform platform: platformList) {
                        if(platform.equals(obj)) {
                            // Map center origin to bottom left
                            platform.position[0] = x - platform.position[2] / 2;
                            platform.position[1] = y - platform.position[3] / 2;
                            platform.position[0] = Math.max(0 ,platform.position[0]);
                            platform.position[1] = Math.max(0, platform.position[1]);
                        }
                    }
                    obj.setVX(0);
                    obj.setVY(0);
                    obj.setLinearVelocity(new Vector2(0, 0));
                    obj.setMass(10000000f);
                }
                else {
                    obj.resetMass();
                }
            }
        }


        camera.position.x = Math.min(Math.max(canvas.getWidth() / 2, camera.position.x + InputController.getInstance().getCameraHorizontal() * 6), DEFAULT_WORLD_WIDTH);
        camera.position.y = Math.min(Math.max(canvas.getHeight() / 2, camera.position.y + InputController.getInstance().getCameraVertical() * 6), DEFAULT_WORLD_HEIGHT);
        menuTable.setPosition(camera.position.x + canvas.getWidth() / 3, camera.position.y);
        selector.moveTo((camera.position.x- canvas.getWidth()/2) / canvas.PPM + input.getCrossHair().x ,
                (camera.position.y- canvas.getHeight()/2) / canvas.PPM + input.getCrossHair().y  );

        camera.update();
    }

    public void setCanvas(GameCanvas canvas) {
        this.canvas = canvas;
        this.scale.x = canvas.getWidth()/bounds.getWidth();
        this.scale.y = canvas.getHeight()/bounds.getHeight();
    }

    public void gatherAssets(AssetDirectory directory) {
        backgroundTexture = new TextureRegion(directory.getEntry("platform:background_light", Texture.class));
        lightTexture = new TextureRegion(directory.getEntry("shared:light", Texture.class));
        darkTexture = new TextureRegion(directory.getEntry("shared:dark", Texture.class));
        allTexture = new TextureRegion(directory.getEntry("shared:all", Texture.class));
        crosshairTexture = new TextureRegion(directory.getEntry("platform:bullet", Texture.class));
        buttonUpTexture = directory.getEntry( "level_editor:buttonUp", Texture.class);
        buttonDownTexture = directory.getEntry( "level_editor:buttonDown", Texture.class);
        textBackground = directory.getEntry( "level_editor:text_background", Texture.class);
        selectBackground = directory.getEntry("level_editor:select_background", Texture.class);
        dropdownTexture = directory.getEntry("level_editor:dropdown", Texture.class);
        dropdownDownTexture = directory.getEntry("level_editor:dropdown_down", Texture.class);
        cursorTexture = directory.getEntry("level_editor:cursor", Texture.class);


        somniTexture = new TextureRegion(directory.getEntry("platform:somni_stand", Texture.class));
        phobiaTexture = new TextureRegion(directory.getEntry("platform:phobia_stand", Texture.class));
        goalTexture = new TextureRegion(directory.getEntry("shared:goal", Texture.class));

        TextureRegion[] temp = {lightTexture,darkTexture,allTexture, somniTexture, phobiaTexture, goalTexture};
        platTexture = temp;

        sliderBarTexture = directory.getEntry( "platform:sliderbar", Texture.class);
        sliderKnobTexture = directory.getEntry( "platform:sliderknob", Texture.class);

        backgrounds = new TextureRegion[] {
                new TextureRegion(directory.getEntry("platform:background_light", Texture.class)),
                new TextureRegion(directory.getEntry("platform:background_dark", Texture.class)),
                new TextureRegion(directory.getEntry("platform:background_light_gear", Texture.class)),
                new TextureRegion(directory.getEntry("platform:background_dark_gear", Texture.class)),
                new TextureRegion(directory.getEntry("platform:background_light_dreams", Texture.class)),
                new TextureRegion(directory.getEntry("platform:background_dark_dreams", Texture.class)),
                new TextureRegion(directory.getEntry("platform:background_light_house", Texture.class)),
                new TextureRegion(directory.getEntry("platform:background_dark_house", Texture.class)),
        };



        super.gatherAssets(directory);
    }

    public static class LevelSerializer {

        private static String[] getTypeAndAssetName(int tag) {
            String type = "", assetName = "";
            switch(tag) {
                case lightTag:
                    type = "light";
                    assetName = "shared:light";
                    break;
                case darkTag:
                    type = "dark";
                    assetName = "shared:dark";
                    break;
                case allTag:
                    type = "all";
                    assetName = "shared:all";
                    break;
            }
            return new String[]{type, assetName};
        }

        private static class Level {
            int[] dimensions;
            Somni somni;
            Phobia phobia;
            Goal goal;
            ArrayList<LevelObject> objects = new ArrayList<LevelObject>();

            private Level(int width, int height, PooledList<Platform> platforms) {
                this.dimensions = new int[]{width, height};
                for (Platform platform : platforms) {
                    if (platform.tag < somniTag) {
                        // Check to see if the platform belongs to a LevelObject group
                        String[] typeAndAssetName = getTypeAndAssetName(platform.tag);
                        String type = typeAndAssetName[0], assetName = typeAndAssetName[1];
                        boolean unique = true;
                        for (LevelObject object : objects) {
                            if (object.hasInCommon(type, assetName, platform.properties, platform.behaviors)) {
                                // If so, add it to that group
                                object.positions.add(platform.position);
                                unique = false;
                            }
                        }

                        if (unique) {
                            // If not, create a new LevelObject group for it
                            ArrayList<float[]> positions = new ArrayList<float[]>();
                            positions.add(platform.position);
                            LevelObject levelObject = new LevelObject(type, assetName, positions, platform.properties,
                                    platform.behaviors);
                            objects.add(levelObject);
                        }

                    } else {
                        // Set our special platforms
                        switch (platform.tag) {
                            case somniTag:
                                somni = new Somni(platform.position[0], platform.position[1]);
                                break;
                            case phobiaTag:
                                phobia = new Phobia(platform.position[0], platform.position[1]);
                                break;
                            case goalTag:
                                goal = new Goal(platform.position[0], platform.position[1]);
                                break;
                        }
                    }
                }
            }
        }

        private static class Somni {
            float[] pos = new float[2];

            private Somni(float x, float y) {
                this.pos[0] = x;
                this.pos[1] = y;
            }
        }

        private static class Phobia {
            float[] pos = new float[2];

            private Phobia(float x, float y) {
                this.pos[0] = x;
                this.pos[1] = y;
            }
        }

        private static class Goal {
            float[] pos = new float[2];

            private Goal(float x, float y) {
                this.pos[0] = x;
                this.pos[1] = y;
            }
        }

        private static class LevelObject {
            String type;
            String assetName;
            ArrayList<float[]> positions;
            ArrayList<String> properties;
            ArrayList<String> behaviors;

            private LevelObject(String type, String assetName, ArrayList<float[]> positions,
                                ArrayList<String> properties, ArrayList<String> behaviors) {
                this.type = type;
                this.assetName = assetName;
                this.positions = positions;
                this.properties = properties;
                this.behaviors = behaviors;
            }

            private Boolean hasInCommon(String type, String assetName, ArrayList<String> properties,
                                ArrayList<String> behaviors) {
                return this.type.equals(type) &&
                        this.assetName.equals(assetName) &&
                        this.properties.equals(properties) &&
                        this.behaviors.equals(behaviors);
            }
        }

        public static void serialize(String fileName, int levelWidth, int levelHeight, PooledList<Platform> platforms) {
            Level level = new Level(levelWidth, levelHeight, platforms);
            Json json = new Json();
            json.setOutputType(JsonWriter.OutputType.json);
            FileHandle file = Gdx.files.local(String.format("drafts/%s.json", fileName));
            file.writeString(json.prettyPrint(level), false);
        }

        public void deserialize(int levelNumber) {

        }
    }
}
