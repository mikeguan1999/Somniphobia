package edu.cornell.gdiac.somniphobia.game.controllers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import edu.cornell.gdiac.somniphobia.WorldController;
import edu.cornell.gdiac.somniphobia.game.models.PlatformModel;
import edu.cornell.gdiac.somniphobia.obstacle.*;
import edu.cornell.gdiac.util.*;

import java.util.Iterator;

public class PlatformController {

    /** This values so light only interacts with light and dark only interacts with dark*/
    private final short CATEGORY_LPLAT = 0x0001;  //0000000000000001
    private final short CATEGORY_DPLAT = 0x0002;  //0000000000000010
    private final short CATEGORY_SOMNI = 0x0004;  //0000000000000100
    private final short CATEGORY_PHOBIA = 0x0008;	   	  //0000000000001000
    private final short CATEGORY_COMBINED = 0x0010; 	  //0000000000010000
    private final short CATEGORY_ALLPLAT = 0x0020;

    /** short values for masking bits*/
    private final short MASK_LPLAT = CATEGORY_SOMNI | CATEGORY_COMBINED;
    private final short MASK_DPLAT = CATEGORY_PHOBIA | CATEGORY_COMBINED;
    private final short MASK_SOMNI = CATEGORY_LPLAT | CATEGORY_ALLPLAT;
    private final short MASK_PHOBIA = CATEGORY_DPLAT | CATEGORY_ALLPLAT;
    private final short MASK_COMBINED = CATEGORY_DPLAT | CATEGORY_LPLAT | CATEGORY_ALLPLAT;
    private final short MASK_ALLPLAT = CATEGORY_SOMNI | CATEGORY_PHOBIA | CATEGORY_COMBINED;

    public static final float rainingCooldown = 100;
    public static final float respawnCooldown = 300;

    /** Filters for objects*/
    public Filter lightplatf;
    public Filter darkplatf;
    public Filter somnif;
    public Filter phobiaf;
    public Filter combinedf;
    public Filter allf;
    public Filter [] filters;



    WorldController worldController;

    /** shared objects */
    protected PooledList<Obstacle> sharedObjects  = new PooledList<Obstacle>();
    /** shared objects */
    protected PooledList<Obstacle> lightObjects  = new PooledList<Obstacle>();
    /** shared objects */
    protected PooledList<Obstacle> darkObjects  = new PooledList<Obstacle>();
    /** moving objects */
    protected PooledList<Obstacle> movingObjects = new PooledList<Obstacle>();
    /** platforms that are raining out of the world **/
    protected PooledList<Obstacle> currRainingPlatforms = new PooledList<>();
    /** platforms that are respawning **/
    protected PooledList<Obstacle> respawningPlatforms = new PooledList<>();

    /** Vector2 cache */
    private Vector2 vector;
    private Vector2 vector2;


    /**
     * Constructor for platform controller. Creates all the necessary filters.
     */
    public PlatformController() {
        this.worldController = worldController;
        lightplatf = new Filter();
        lightplatf.categoryBits = CATEGORY_LPLAT;
        lightplatf.maskBits = MASK_LPLAT;
        darkplatf = new Filter();
        darkplatf.categoryBits = CATEGORY_DPLAT;
        darkplatf.maskBits = MASK_DPLAT;
        somnif = new Filter();
        somnif.categoryBits = CATEGORY_SOMNI;
        somnif.maskBits = MASK_SOMNI;
        phobiaf = new Filter();
        phobiaf.categoryBits = CATEGORY_PHOBIA;
        phobiaf.maskBits = MASK_PHOBIA;
        combinedf = new Filter();
        combinedf.categoryBits = CATEGORY_COMBINED;
        combinedf.maskBits = MASK_COMBINED;
        allf = new Filter();
        allf.categoryBits = CATEGORY_ALLPLAT;
        allf.maskBits = MASK_ALLPLAT;
        Filter [] fs = {lightplatf, darkplatf, allf, somnif, phobiaf, combinedf};
        filters = fs;
        vector = new Vector2();
        vector2 = new Vector2();
    }


    /**
     * Sets the worldController
     * @param worldController the worldController
     */
    public void setWorldController(WorldController worldController) {
        this.worldController = worldController;
    }

    /**
     * Takes in a list of obstacles and applys the corresponding filter to the data.
     * @param objects
     */
    public void applyFilters(PooledList<Obstacle> objects){
        for( Obstacle o : objects){
            if(o instanceof PlatformModel){
                o.setFilterData(filters[o.getTag() - 1]);
            }
        }
    }

    /**
     * Sets the light objects
     * @param lightObjects
     */
    public void setLightObjects(PooledList<Obstacle> lightObjects) {
        this.lightObjects = lightObjects;
    }

    /**
     * Sets the dark objects
     * @param darkObjects
     */
    public void setDarkObjects(PooledList<Obstacle> darkObjects) {
        this.lightObjects = darkObjects;
    }

    /**
     * Sets the shared objects
     * @param sharedObjects
     */
    public void setSharedObjects(PooledList<Obstacle> sharedObjects) {
        this.sharedObjects = sharedObjects;
    }


    /**
     * Sets the currently raining platforms objects
     * @param currRainingPlatforms
     */
    public void setCurrRainingPlatforms(PooledList<Obstacle> currRainingPlatforms) {
        this.currRainingPlatforms = currRainingPlatforms;
    }


    /**
     * Sets the moving objects
     * @param movingObjects
     */
    public void setMovingObjects(PooledList<Obstacle> movingObjects) {
        this.movingObjects = movingObjects;
    }

    /**
     * Updates platform states
     */
    public void update(float dt){

        for (Obstacle obstacle : movingObjects) {
            PlatformModel platform = (PlatformModel) obstacle;
            Vector2 position = vector.set(platform.getLeftX(), platform.getBottomY());;
            PooledList<Vector2> paths = platform.getPaths();
            Vector2 nextDestination = paths.getHead();

            //if overshot (destination - position opposite sign as velocity), switch destination
            if (!obstacle.getLinearVelocity().isZero() && (Math.signum(nextDestination.x - position.x)
                    != Math.signum(platform.getLinearVelocity().x) ||
                    Math.signum(nextDestination.y - position.y) != Math.signum(platform.getLinearVelocity().y))) {
                position.set(nextDestination);
                platform.setVY(0);
                platform.setVX(0);
                platform.setX(position.x + platform.getWidth()/2);
                platform.setY(position.y + platform.getHeight()/2);
            }

            // Switch destination if arrived
            if (position.equals(nextDestination)) {
                paths.add(paths.poll());
                nextDestination = paths.getHead();
            }

            Vector2 nextPath = vector2.set(nextDestination).sub(position).nor();
            if (nextPath.isZero()) platform.setVelocity(0);
            platform.setLinearVelocity(nextPath.scl(platform.getVelocity()));

        }

        Iterator<Obstacle> currRainingPlatIt = currRainingPlatforms.iterator();
        while (currRainingPlatIt.hasNext()) {
            PlatformModel platform = (PlatformModel) currRainingPlatIt.next();
            if (platform.getRainingCooldown() <= 0) {

                platform.setActive(false);
//                platform.deactivatePhysics(worldController.getWorld());

                lightObjects.remove(platform);
                darkObjects.remove(platform);
                sharedObjects.remove(platform);
//                platform.markRemoved(true);

                //Remove from curr raining and add to respawn list
                currRainingPlatforms.remove(platform);
                respawningPlatforms.add(platform);
                platform.setRespawnCooldown(respawnCooldown);
            } else {
                platform.setRainingCooldown(platform.getRainingCooldown() - 1);
            }
        }


        Iterator<Obstacle> respawningPlatIt = respawningPlatforms.iterator();
        while (respawningPlatIt.hasNext()) {
            PlatformModel platform = (PlatformModel) respawningPlatIt.next();
            if (platform.getRespawnCooldown() <= 0) {
                respawningPlatforms.remove(platform);
                switch (platform.getTag()) {
                    case PlatformModel.light:
                        lightObjects.add(platform);
                        break;
                    case PlatformModel.dark:
                        darkObjects.add(platform);
                        break;
                    case PlatformModel.shared:
                        sharedObjects.add(platform);
                        break;
                    default:
                        break;
                }
                platform.setActive(true);
                platform.setCurrentlyRaining(false);
            } else {
                platform.setRespawnCooldown(platform.getRespawnCooldown() - 1);
            }
        }

    }


}

