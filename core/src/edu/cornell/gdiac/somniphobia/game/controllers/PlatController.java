package edu.cornell.gdiac.somniphobia.game.controllers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import edu.cornell.gdiac.somniphobia.game.models.PlatformModel;
import edu.cornell.gdiac.somniphobia.obstacle.*;
import edu.cornell.gdiac.util.*;

public class PlatController {

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

    /** Filters for objects*/
    public Filter lightplatf;
    public Filter darkplatf;
    public Filter somnif;
    public Filter phobiaf;
    public Filter combinedf;
    public Filter allf;
    public Filter [] filters;



    /** moving objects */
    protected PooledList<Obstacle> movingObjects = new PooledList<Obstacle>();

    /** Vector2 cache */
    private Vector2 vector;
    private Vector2 vector2;


    /**
     * Constructor for platform controller. Creates all the necessary filters.
     */
    public PlatController(){
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
     * Takes in a list of obstacles and applys the corresponding filter to the data.
     * @param objects
     */
    public void applyFilters(PooledList<Obstacle> objects){
        for( Obstacle o : objects){
            if(o instanceof PlatformModel){
                o.setFilterData(filters[o.getTag()]);
            }
        }
    }

    public void setMovingObjects(PooledList<Obstacle> movingObjects) {
        this.movingObjects = movingObjects;
    }

    /**
     * Updates platform states
     */
    public void update(float dt){

        for (Obstacle obstacle : movingObjects) {
            PlatformModel platform = (PlatformModel) obstacle;
            Vector2 position = new Vector2();
            position.set(platform.getLeftX(), platform.getBottomY());
            PooledList<Vector2> paths = platform.getPaths();

            Vector2 nextDestination = paths.getHead();

            if (position.dst(nextDestination) < 0.01) {
                position.set(nextDestination);
            }

            if (position.equals(nextDestination)) {
//                System.out.println("next destination!! \n\n\n");
//                System.out.println(paths);
                paths.add(paths.poll());
//                System.out.println(paths);
                nextDestination = paths.getHead();

                //Direction towards next

            }

            Vector2 nextPath = vector.set(nextDestination).sub(position).nor();
            platform.setLinearVelocity(nextPath.scl(2));
//            platform.setActive(true);
//            platform.setAwake(true);
//            platform.setX(platform.getX() + 1);
//            platform.getBody().setLinearVelocity(10,10);
//            platform.update(dt);

        }
    }


}

