package edu.cornell.gdiac.somniphobia.game.controllers;

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

    /**
     * Updates platform states
     */
    public void update(PooledList<Obstacle> objects){

    }


}

