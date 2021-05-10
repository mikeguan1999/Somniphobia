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
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.somniphobia.GDXRoot;
import edu.cornell.gdiac.somniphobia.GameCanvas;
import edu.cornell.gdiac.somniphobia.InputController;
import edu.cornell.gdiac.somniphobia.WorldController;
import edu.cornell.gdiac.somniphobia.game.models.CharacterModel;
import edu.cornell.gdiac.somniphobia.game.models.PlatformModel;
import edu.cornell.gdiac.somniphobia.obstacle.BoxObstacle;
import edu.cornell.gdiac.somniphobia.obstacle.Obstacle;
import edu.cornell.gdiac.somniphobia.obstacle.ObstacleSelector;
import edu.cornell.gdiac.somniphobia.obstacle.SimpleObstacle;
import edu.cornell.gdiac.util.PooledList;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Iterator;


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

    static int worldWidth;
    static int worldHeight;

    private final float CAMERA_SPEED = 6.0f;

    private boolean isPlatformSelected;
    /** Mouse selector to move the platforms */
    private ObstacleSelector selector;
    /** List to hold all platforms */
    private PooledList<Platform> platformList = new PooledList<Platform>();

    private Batch batch;
    private TextField movingVelocity;

    /** TextureRegion variables */
    TextureRegion[] backgrounds;
    TextureRegion[] vertices;
    TextureRegion[] special;
    private TextureRegion backgroundTexture;
    private TextureRegion lightTexture;
    private TextureRegion darkTexture;
    private TextureRegion allTexture;
    private TextureRegion harmTexture;
    private TextureRegion crumbleTexture;
    private TextureRegion vertexTexture;
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
    private ImageTextButton normalPlatformSelect;
    private ImageTextButton crumblePlatformSelect;
    private ImageTextButton harmingPlatformSelect;

    private ImageTextButton widthInc;
    private ImageTextButton widthDec;
    private ImageTextButton heightInc;
    private ImageTextButton heightDec;

    /** Tag constants */
    protected final static int lightTag = 1;
    protected final static int darkTag = 2;
    protected final static int allTag = 3;
    protected final static int somniTag = 4;
    protected final static int phobiaTag = 5;
    protected final static int goalTag = 6;
    protected final static int vertexPlatformTag = 7;

    static int currBackground;
    private int selectedType; // needs to be removed since we have reference to selectedObstacle
    private int selectedProperty;
    private boolean addingMovement;
    private boolean movingPlatform;


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
    private TextField assetText;

    Stage stage;

    private TextField loadPath;
    /** Whether or not currently loading a level */
    private boolean loading;
    /** Whether or not the play button was pressed */
    private boolean playtesting;


    static class Platform extends BoxObstacle {
        int type;
        int property;
        float[] pos;
        ArrayList<Platform> path;
        Platform reference;
        float velocity;
        public Platform(int type, float posX, float posY, float width, float height,
                        int property, ArrayList<Platform> path, float velocity) {
            super(posX + width / 2, posY + height / 2, width, height);
            this.pos = new float[]{posX, posY, width, height};
            this.type = type;
            this.property = property;
            this.path = path;
            this.velocity = velocity;
        }


        public void addMovement(Platform v){
            this.path.add(v);
        }
    }

    public void setupPlatform(Platform platform) {
        Platform obj = platform;
        platformList.add(obj);
        obj.deactivatePhysics(world);
        obj.setDrawScale(scale);
        TextureRegion newXTexture;
        if(platform.type < somniTag) {
            if(platform.property == 1) {
                newXTexture = new TextureRegion(platTexture[platform.type - 1]);
                float posX = platform.pos[0], posY = platform.pos[1], width = platform.pos[2], height = platform.pos[3];
                newXTexture.setRegion(platform.pos[0], posY, posX + width, posY + height);
            }else{
                newXTexture = new TextureRegion(special[2*(platform.type - 1)+(platform.property-PlatformModel.harming)]);
                float posX = platform.pos[0], posY = platform.pos[1], width = platform.pos[2], height = platform.pos[3];
                newXTexture.setRegion(platform.pos[0], posY, posX + width, posY + height);
            }
        } else {
            newXTexture = platTexture[platform.type-1];
        }
        if(platform.type == vertexPlatformTag){
            newXTexture = vertices[(platform.reference.path.size()-1)%6];
        }
        obj.setTexture(newXTexture);
        addObject(obj);
        //selectedObstacle = obj;
    }

    public void createPlatform(int type, float posX, float posY, float width, float height, int property,
                               ArrayList<Platform> path, float velocity) {
        Platform platform = new Platform(type, posX, posY, width, height, property, path, velocity);
        setupPlatform(platform);
    }

    public void editPlatform(Platform p ){
        Platform currPlatform = p;


        float posX = currPlatform.getX() - currPlatform.getWidth() / 2;
        float posY = currPlatform.getY() - currPlatform.getHeight() / 2;
        float width = Float.parseFloat(platformWidth.getText());
        float height = Float.parseFloat(platformHeight.getText());
        int type = selectedType;
        int property = selectedProperty;
        currPlatform.property = property;
        ArrayList<Platform> temp = new ArrayList<>();
        ArrayList<Platform> path = currPlatform.path;
        ArrayList<Platform> updatedPath = new ArrayList<>();
        float velocity = Float.parseFloat(movingVelocity.getText());

        Platform platform = new Platform(type, posX, posY, width, height, property, temp, velocity);

        for (int i = 0; i < currPlatform.path.size(); i++) {
            updatedPath.add(currPlatform.path.get(i));
        }

        deletePlatform(currPlatform);

        if(path.size()>0){
            Iterator<Platform> iter = updatedPath.iterator();
            while(iter.hasNext()){
                Platform v = iter.next();
                System.out.println(v.getX());
                System.out.println(v.getY());
                float x = v.getX() - width/2;
                float y = v.getY() - height/2;
                Platform vertex = new Platform(vertexPlatformTag, x, y, width, height,0,
                        null, 0);
                vertex.reference = platform;
                platform.addMovement(vertex);
                setupPlatform(vertex);
            }
        }
        setupPlatform(platform);

    }

    public void deletePlatform(Obstacle o) {
        if(o instanceof Platform && ((Platform) o).path !=null) {
            for (Platform v :((Platform) o).path) {
                platformList.remove(v);
                v.deactivatePhysics(world);
                objects.remove(v);
            }
        }
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
        stage = new Stage(new ScreenViewport(canvas.getCamera()));
        hideDropdowns();
        createSidebar();
        selector= new ObstacleSelector(world,1 ,1);
        this.setDebug(true);
        selector.setTexture(crosshairTexture);
        selector.setDrawScale(scale);
        currBackground = 0;
        backgroundTexture = backgrounds[currBackground];
        if(!loading) {
            // Add Somni
            createPlatform(somniTag, SOMNI_DEFAULT_POS[0], SOMNI_DEFAULT_POS[1], CHARACTER_DIMENSIONS[0],
                    CHARACTER_DIMENSIONS[1], 0, null, 0);
            // Add Phobia
            createPlatform(phobiaTag, PHOBIA_DEFAULT_POS[0], PHOBIA_DEFAULT_POS[1], CHARACTER_DIMENSIONS[0],
                    CHARACTER_DIMENSIONS[1], 0, null, 0);
            // Add Goal
            createPlatform(goalTag, GOAL_DEFAULT_POS[0], GOAL_DEFAULT_POS[1], GOAL_DIMENSIONS[0],
                    GOAL_DIMENSIONS[1], 0, null, 0);
        } else {
            loading = false;
        }
        playtesting = false;
        selectedType = lightTag;
        selectedProperty = PlatformModel.normal;
        addingMovement = false;
        movingPlatform = false;
        isPlatformSelected = false;
    }

    public void createSidebar() {
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

        TextField.TextFieldFilter numberFilter = new TextField.TextFieldFilter.DigitsOnlyFilter();

        worldWidthText = new TextField(null, textFieldStyle);
        worldWidthText.setText(String.valueOf(DEFAULT_WORLD_WIDTH));
        worldWidthText.setMaxLength(4);
        worldWidthText.setTextFieldFilter(numberFilter);

        worldHeightText = new TextField(null, textFieldStyle);
        worldHeightText.setText(String.valueOf(DEFAULT_WORLD_HEIGHT));
        worldHeightText.setMaxLength(4);
        worldHeightText.setTextFieldFilter(numberFilter);

        assetText = new TextField(null, textFieldStyle);
        assetText.setText("");


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

                if (selectedObstacle != null && ((Platform) selectedObstacle).type < somniTag) {
                    deletePlatform(selectedObstacle);
                }
            }
        });

        ImageTextButton setAsset = new ImageTextButton("Set Asset", buttonStyle);

        setAsset.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(selectedObstacle != null && selectedObstacle instanceof Platform &&
                        ((Platform) selectedObstacle).type <somniTag) {
                    //((Platform) selectedObstacle).setTexture(assetText.getText());
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

        normalPlatformSelect = new ImageTextButton("Normal", selectButtonStyle);
        normalPlatformSelect.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                setProperty(PlatformModel.normal);
            }
        });
        normalPlatformSelect.setChecked(true);

        harmingPlatformSelect = new ImageTextButton("Harm", selectButtonStyle);
        harmingPlatformSelect.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                setProperty(PlatformModel.harming);
            }
        });

        crumblePlatformSelect = new ImageTextButton("Crumble", selectButtonStyle);
        crumblePlatformSelect.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                setProperty(PlatformModel.crumbling);
            }
        });
        

        Label labelDimension = new Label("Dimensions: ", labelStyle);
        Label labelVelocity = new Label("Speed: ", labelStyle);


        platformWidth = new TextField(null, textFieldStyle);
        platformWidth.setText("2");
        platformWidth.setMaxLength(4);
        platformWidth.setTextFieldFilter(numberFilter);


        platformHeight = new TextField(null, textFieldStyle);
        platformHeight.setText("2");
        platformHeight.setMaxLength(4);
        platformHeight.setTextFieldFilter(numberFilter);

        movingVelocity = new TextField(null, textFieldStyle);
        movingVelocity.setText("2");
        movingVelocity.setMaxLength(4);
        movingVelocity.setTextFieldFilter(numberFilter);

        ImageTextButton addPlatform = new ImageTextButton("Add", buttonStyle);
        addPlatform.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Camera camera = canvas.getCamera();
                float posX = (int) (camera.position.x/canvas.PPM);
                float posY = (int) (camera.position.y/canvas.PPM);
                float width = Float.parseFloat(platformWidth.getText());
                float height = Float.parseFloat(platformHeight.getText());
                ArrayList<Platform> path = new ArrayList<>();
                float velocity = Float.parseFloat(movingVelocity.getText());
                if(selectedType < somniTag) {
                    createPlatform(selectedType, posX, posY, width, height, selectedProperty, path, velocity);
                }
            }
        });

        ImageTextButton addMovementPlatform = new ImageTextButton("addMove", buttonStyle);
        addMovementPlatform.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Camera camera = canvas.getCamera();
                if(selectedObstacle != null && selectedObstacle instanceof Platform &&
                        ((Platform) selectedObstacle).type <somniTag){
                    float posX = (int) (camera.position.x/canvas.PPM);
                    float posY = (int) (camera.position.y/canvas.PPM);
                    float width = ((Platform) selectedObstacle).getWidth();
                    float height = ((Platform) selectedObstacle).getHeight();
                    float velocity = Float.parseFloat(movingVelocity.getText());
                    Platform vertex = new Platform(vertexPlatformTag, posX, posY, width, height,0,
                            null, 0);
                    vertex.reference = (Platform) selectedObstacle;
                    ((Platform)selectedObstacle).addMovement(vertex);
                    setupPlatform(vertex);

                }
            }
        });

        ImageTextButton editButton = new ImageTextButton("Edit", buttonStyle);
        editButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (selectedObstacle instanceof Platform) {
                    //Platform currPlatform = (Platform) selectedObstacle;

                    /*
                    float posX = currPlatform.getX() - currPlatform.getWidth() / 2;
                    float posY = currPlatform.getY() - currPlatform.getHeight() / 2;
                    float width = Float.parseFloat(platformWidth.getText());
                    float height = Float.parseFloat(platformHeight.getText());
                    int type = selectedType;
                    int property = selectedProperty;
                    currPlatform.property = property;
                    ArrayList<Platform> path = currPlatform.path;
                    float velocity = Float.parseFloat(movingVelocity.getText());

                    platformList.remove(currPlatform);
                    currPlatform.deactivatePhysics(world);
                    objects.remove(currPlatform);

                    createPlatform(type, posX, posY, width, height, property, path, velocity);*/
                    if(((Platform) selectedObstacle).type < somniTag) {
                        editPlatform((Platform) selectedObstacle);
                    }
                }
            }
        });
        
        ImageTextButton saveButton = new ImageTextButton("Save", buttonStyle);
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String fileName = String.format("drafts/%s.json", loadPath.getText());
                LevelSerializer.serialize(fileName, currBackground + 1, worldWidth, worldHeight, platformList, false);
            }
        });

        ImageTextButton playButton = new ImageTextButton("Play", buttonStyle);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                LevelSerializer.serialize(null, currBackground + 1, worldWidth, worldHeight, platformList, true);
                playtesting = true;
            }
        });


        loadPath = new TextField("path", textFieldStyle);

        ImageTextButton button4 = new ImageTextButton("Load Dream", buttonStyle);
        button4.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String fileName = String.format("levels/%s.json", loadPath.getText());
                LevelSerializer.Level level = LevelSerializer.deserialize(fileName, loadPath.getText().equals("prefs"));
                if(level != null) {
                    loading = true;
                    reset();

                    currBackground = level.background - 1;
                    backgroundTexture = backgrounds[currBackground];

                    worldWidth = level.dimensions[0];
                    worldWidthText.setText(String.valueOf(worldWidth));

                    worldHeight = level.dimensions[1];
                    worldHeightText.setText(String.valueOf(worldHeight));

                    PooledList<Platform> platforms = level.levelToPlatforms();
                    for(Platform platform: platforms) {
                        setupPlatform(platform);
                        if(platform.path != null) {
                            for(Platform pathPlatform: platform.path) {
                                setupPlatform(pathPlatform);
                            }
                        }
                    }
                }
            }
        });

        ImageTextButton button5 = new ImageTextButton("Reset Dream", buttonStyle);
        button5.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                reset();
            }
        });


        ImageTextButton switchBackgroundButton = new ImageTextButton("Switch Background", buttonStyle);
        switchBackgroundButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currBackground += 2;
                currBackground %= backgrounds.length;
                backgroundTexture = backgrounds[currBackground];
            }
        });


        Table platformParamTable = new Table();

        platformParamTable.add(lightPlatformSelect).pad(0,5,5,5);
        platformParamTable.add(darkPlatformSelect).pad(0,5,5,5);
        platformParamTable.add(allPlatformSelect).pad(0,5,5,5);
        platformParamTable.row();
        platformParamTable.add(normalPlatformSelect).pad(0,5,5,5);
        platformParamTable.add(crumblePlatformSelect).pad(0,5,5,5);
        platformParamTable.add(harmingPlatformSelect).pad(0,5,5,5);
        platformParamTable.row();
        platformParamTable.add(addMovementPlatform).pad(0,5,5,5);
        platformParamTable.add(labelVelocity).pad(0,5,5,0);
        platformParamTable.add(movingVelocity).width(60).pad(0,0,5,5);
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
        if (selectedObstacle==null || selectedObstacle instanceof Platform) {
            lightPlatformSelect.setChecked(tag == lightTag);
            darkPlatformSelect.setChecked(tag == darkTag);
            allPlatformSelect.setChecked(tag == allTag);
            selectedType = tag;
        }

    }

    /**
     * Sets the selectedObstacle
     */
    public void setProperty(int tag) {
        if (selectedObstacle==null || selectedObstacle instanceof Platform) {
            normalPlatformSelect.setChecked(tag == PlatformModel.normal);
            crumblePlatformSelect.setChecked(tag == PlatformModel.crumbling);
            harmingPlatformSelect.setChecked(tag == PlatformModel.harming);
            selectedProperty = tag;
        }

    }

    /**
     * Sets the selectedObstacle
     */
    public void setSelectedObstacle(Obstacle obstacle) {
        if(obstacle instanceof Platform && ((Platform)obstacle).type == vertexPlatformTag){
            selectedObstacle = ((Platform) obstacle).reference;
        }else {
            selectedObstacle = obstacle;
        }
        if (selectedObstacle instanceof Platform) {
            Platform currPlatform = (Platform) selectedObstacle;
            platformWidth.setText(String.valueOf((int)currPlatform.getWidth()));
            platformHeight.setText(String.valueOf((int)currPlatform.getHeight()));
            setSelectedColor(currPlatform.type);
            setProperty(currPlatform.property);
        }

    }

    public void draw(float dt) {

        // Draw background at camera position
        Camera camera = canvas.getCamera();
        float cameraX = camera.position.x - canvas.getWidth() / 2;
        float cameraY = camera.position.y - canvas.getHeight() / 2;
        canvas.begin();
        canvas.draw(backgroundTexture, Color.WHITE, cameraX, cameraY, canvas.getWidth(), canvas.getHeight());
        canvas.end();

        // Draw the selector
        canvas.begin();
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
        if(playtesting) {
            playtesting = false;
            return;
        }
        Vector2 gravity = new Vector2(world.getGravity() );
        objects.clear();
        addQueue.clear();
        platformList.clear();
        world.dispose();

        world = new World(gravity,false);
        setComplete(false);
        setFailure(false);
        initialize();
    }

    @Override
    public void update(float dt) {
        // Move an object if touched
        Gdx.input.setInputProcessor(stage);
        Camera camera = canvas.getCamera();
        displayFont.getData().setScale(.3f);
        InputController input = InputController.getInstance();
        if (input.getCrossHair().x < 20 && input.didTertiary() && !selector.isSelected()) {
            if(selector.select((camera.position.x- canvas.getWidth()/2) / canvas.PPM + input.getCrossHair().x ,
            (camera.position.y - canvas.getHeight()/2) / canvas.PPM + input.getCrossHair().y  )){
                moving = true;
                if (input.getCrossHair().x < 20) {
                    setSelectedObstacle(selector.getObstacle());
                }
            }
        } else if (!input.didTertiary() && selector.isSelected()) {
            moving = false;
            selector.deselect();
        }

        if(input.didSwitch()) {
            int backgroundIndex = backgroundTexture.equals(backgrounds[currBackground]) ? currBackground + 1 :
                    currBackground;
            backgroundTexture = backgrounds[backgroundIndex];
        }

        if(playtesting) {
            GDXRoot.prepareLevelJson(0, false);
            getListener().exitScreen(this, EXIT_SWITCH);
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
                            platform.pos[0] = x - platform.pos[2] / 2;
                            platform.pos[1] = y - platform.pos[3] / 2;
                            platform.pos[0] = Math.max(0 ,platform.pos[0]);
                            platform.pos[1] = Math.max(0, platform.pos[1]);
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
                if(!selector.isSelected()){
                    obj.getBody().getFixtureList().get(0).setSensor(false);
                }else{
                    if(obj != selector.getObstacle() || obj != selectedObstacle){
                        obj.getBody().getFixtureList().get(0).setSensor(true);
                    }
                }
            }
        }


        float newX = Math.max(canvas.getWidth() / 2, camera.position.x +
                InputController.getInstance().getCameraHorizontal() * CAMERA_SPEED);
        camera.position.x = Math.min(newX, worldWidth);
        float newY = Math.max(canvas.getHeight() / 2, camera.position.y +
                InputController.getInstance().getCameraVertical() * CAMERA_SPEED);
        camera.position.y = Math.min(newY, worldHeight);
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
        lightTexture = new TextureRegion(directory.getEntry("shared:solidCloud_light", Texture.class));
        darkTexture = new TextureRegion(directory.getEntry("shared:solidCloud_dark", Texture.class));
        allTexture = new TextureRegion(directory.getEntry("shared:solidCloud_all", Texture.class));
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
        vertexTexture =  new TextureRegion(directory.getEntry("platform:vertex", Texture.class));
        vertices = new TextureRegion[] {
                new TextureRegion(directory.getEntry("platform:vertex1", Texture.class)),
                new TextureRegion(directory.getEntry("platform:vertex2", Texture.class)),
                new TextureRegion(directory.getEntry("platform:vertex3", Texture.class)),
                new TextureRegion(directory.getEntry("platform:vertex4", Texture.class)),
                new TextureRegion(directory.getEntry("platform:vertex5", Texture.class)),
                new TextureRegion(directory.getEntry("platform:vertex6", Texture.class))
        };


        TextureRegion[] temp = {lightTexture,darkTexture,allTexture,somniTexture, phobiaTexture, goalTexture, vertexTexture};
        platTexture = temp;

        sliderBarTexture = directory.getEntry( "platform:sliderbar", Texture.class);
        sliderKnobTexture = directory.getEntry( "platform:sliderknob", Texture.class);

        backgrounds = new TextureRegion[] {
                new TextureRegion(directory.getEntry("platform:background_light_forest", Texture.class)),
                new TextureRegion(directory.getEntry("platform:background_dark_forest", Texture.class)),
                new TextureRegion(directory.getEntry("platform:background_light_gear", Texture.class)),
                new TextureRegion(directory.getEntry("platform:background_dark_gear", Texture.class)),
                new TextureRegion(directory.getEntry("platform:background_light_dreams", Texture.class)),
                new TextureRegion(directory.getEntry("platform:background_dark_dreams", Texture.class)),
                new TextureRegion(directory.getEntry("platform:background_light_house", Texture.class)),
                new TextureRegion(directory.getEntry("platform:background_dark_house", Texture.class)),
                new TextureRegion(directory.getEntry("platform:background_light_statues", Texture.class)),
                new TextureRegion(directory.getEntry("platform:background_dark_statues", Texture.class)),
        };
        special = new TextureRegion[]{
                new TextureRegion(directory.getEntry("shared:lightning_cloud_light_single", Texture.class)),
                new TextureRegion(directory.getEntry("shared:rain_cloud_light_single", Texture.class)),
                new TextureRegion(directory.getEntry("shared:lightning_cloud_dark_single", Texture.class)),
                new TextureRegion(directory.getEntry("shared:rain_cloud_dark_single", Texture.class)),
                new TextureRegion(directory.getEntry("shared:lightning_cloud_all_single", Texture.class)),
                new TextureRegion(directory.getEntry("shared:rain_cloud_all_single", Texture.class)),
        };



        super.gatherAssets(directory);
    }

    public static class LevelSerializer {

        private static String getAssetName(int tag) {
            String assetName = "";
            switch(tag) {
                case lightTag:
                    assetName = "shared:light";
                    break;
                case darkTag:
                    assetName = "shared:dark";
                    break;
                case allTag:
                    assetName = "shared:all";
                    break;
            }
            return assetName;
        }

        public static class Level {
            int background;
            int[] dimensions;
            Somni somni;
            Phobia phobia;
            Goal goal;
            ArrayList<LevelObject> objects = new ArrayList<LevelObject>();

            public PooledList<Platform> levelToPlatforms() {
                PooledList<Platform> platforms = new PooledList<>();
                // Add Somni
                platforms.add(new Platform(somniTag, somni.pos[0], somni.pos[1], CHARACTER_DIMENSIONS[0],
                        CHARACTER_DIMENSIONS[1], 0, null, 0));
                // Add Phobia
                platforms.add(new Platform(phobiaTag, phobia.pos[0], phobia.pos[1], CHARACTER_DIMENSIONS[0],
                        CHARACTER_DIMENSIONS[1], 0, null, 0));
                // Add Goal
                platforms.add(new Platform(goalTag, goal.pos[0], goal.pos[1], GOAL_DIMENSIONS[0],
                        GOAL_DIMENSIONS[1], 0, null, 0));
                for(LevelObject object: objects) {
                    for(int i = 0; i < object.positions.size(); i++) {
                        float[] pos = object.positions.get(i);
                        float x = pos[0], y = pos[1], width = pos[2], height = pos[3];
                        Platform newPlatform = new Platform(object.type, x, y, width, height, object.property,
                                null, 0);

                        ArrayList<Platform> pathPlatforms = new ArrayList<>();
                        if(object.paths != null) {
                            float[] path = object.paths.get(i);
                            if(LevelController.hasValidPath(x, y, path)) {
                                for (int j = 0; j < path.length / 2; j++) {
                                    float pathX = path[j * 2], pathY = path[j * 2 + 1];
                                    Platform pathPlatform = new Platform(vertexPlatformTag, pathX, pathY, width, height, 0,
                                            null, 0);
                                    pathPlatform.reference = newPlatform;
                                    pathPlatforms.add(pathPlatform);
                                }
                            }
                        }

                        newPlatform.path = pathPlatforms;
                        platforms.add(newPlatform);
                    }
                }
                return platforms;
            }

            private float[] extractPath(ArrayList<Platform> platformPath){
                float[] path = new float[platformPath.size() * 2];
                for(int i = 0; i < platformPath.size(); i++) {
                    Platform platform = platformPath.get(i);
                    path[i * 2]     = platform.pos[0];
                    path[i * 2 + 1] = platform.pos[1];
                }
                return path;
            }

            private void createLevel(int background, int width, int height, PooledList<Platform> platforms) {
                this.background = background;
                this.dimensions = new int[]{width, height};
                for (Platform platform : platforms) {
                    if (platform.type < somniTag) {
                        // Check to see if the platform belongs to a LevelObject group
                        String assetName = getAssetName(platform.type);
                        boolean unique = true;
                        for (LevelObject object : objects) {
                            if (object.hasInCommon(platform.type, assetName, platform.property)) {
                                // If so, add it to that group
                                object.positions.add(platform.pos);
                                if(platform.path.size() < 2) {
                                    float[] pos = new float[2];
                                    pos[0] = platform.pos[0];
                                    pos[1] = platform.pos[1];
                                    object.paths.add(pos);
                                } else {
                                    object.paths.add(extractPath(platform.path));
                                }
                                unique = false;
                            }
                        }

                        if (unique) {
                            // If not, create a new LevelObject group for it
                            ArrayList<float[]> positions = new ArrayList<>();
                            positions.add(platform.pos);
                            ArrayList<float[]> path = new ArrayList<>();
                            if(platform.path.size() < 2) {
                                float[] pos = new float[2];
                                pos[0] = platform.pos[0];
                                pos[1] = platform.pos[1];
                                path.add(pos);
                            } else {
                                path.add(extractPath(platform.path));
                            }
                            LevelObject levelObject = new LevelObject(platform.type, assetName, positions, platform.property,
                                    path);
                            objects.add(levelObject);
                        }
                    } else {
                        // Set our special platforms
                        switch (platform.type) {
                            case somniTag:
                                somni = new Somni(platform.pos[0], platform.pos[1]);
                                break;
                            case phobiaTag:
                                phobia = new Phobia(platform.pos[0], platform.pos[1]);
                                break;
                            case goalTag:
                                goal = new Goal(platform.pos[0], platform.pos[1]);
                                break;
                        }
                    }
                }
            }

            private Level() { }

            public Level(int background, int width, int height, PooledList<Platform> platforms) {
                createLevel(background, width, height, platforms);
            }
        }

        private static class Somni {
            float[] pos = new float[2];

            private Somni() { }

            private Somni(float x, float y) {
                this.pos[0] = x;
                this.pos[1] = y;
            }
        }

        private static class Phobia {
            float[] pos = new float[2];

            private Phobia() { }

            private Phobia(float x, float y) {
                this.pos[0] = x;
                this.pos[1] = y;
            }
        }

        private static class Goal {
            float[] pos = new float[2];

            private Goal() { }

            private Goal(float x, float y) {
                this.pos[0] = x;
                this.pos[1] = y;
            }
        }

        private static class LevelObject {
            int type;
            String assetName;
            ArrayList<float[]> positions;
            int property;
            ArrayList<float[]> paths;

            private LevelObject() { }

            private LevelObject(int type, String assetName, ArrayList<float[]> positions, int property,
                                ArrayList<float[]> paths) {
                this.type = type;
                this.assetName = assetName;
                this.positions = positions;
                this.property = property;
                this.paths = paths;
            }

            private Boolean hasInCommon(int type, String assetName, int property) {
                return this.type == type &&
                        this.assetName.equals(assetName) &&
                        this.property == property;
            }
        }

        public static void serialize(String fileName, int levelBackground, int levelWidth, int levelHeight,
                                     PooledList<Platform> platforms, boolean isEditor) {
            Level level = new Level(levelBackground, levelWidth, levelHeight, platforms);
            Json json = new Json();
            json.setOutputType(JsonWriter.OutputType.json);
            String prettyJson = json.prettyPrint(level);
            System.out.println(prettyJson);
            if(isEditor) {
                GDXRoot.setPreferences(GDXRoot.getPreferences().putString("playLevel", prettyJson));
            } else {
                FileHandle file = Gdx.files.local(fileName);
                file.writeString(prettyJson, false);
            }
        }

        public static Level deserialize(String fileName, boolean isEditor) {
            String text;
            try {
                if(isEditor) {
                    text = GDXRoot.getPreferences().getString("playLevel");
                } else {
                    FileHandle file = Gdx.files.internal(fileName);
                    text = file.readString();
                }
            } catch (Exception e) {
                System.out.println(e);
                return null;
            }
            Json json = new Json();
            Level level;
            try {
                level = json.fromJson(Level.class, text);
            } catch (Exception e) {
                System.out.println(e);
                return null;
            }
            return level;
        }
    }
}
