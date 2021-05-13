package edu.cornell.gdiac.somniphobia.game.controllers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.ObjectSet;
import edu.cornell.gdiac.audio.SoundController;
import edu.cornell.gdiac.somniphobia.InputController;
import edu.cornell.gdiac.somniphobia.WorldController;
import edu.cornell.gdiac.somniphobia.game.models.CharacterModel;
import edu.cornell.gdiac.somniphobia.game.models.PlatformModel;
import edu.cornell.gdiac.somniphobia.obstacle.BoxObstacle;
import edu.cornell.gdiac.somniphobia.obstacle.Obstacle;
import edu.cornell.gdiac.util.PooledList;

/**
 * Movement Controller to control the primary game mechanics involving the two playable characters, Somni and Phobia
 */
public class MovementController implements ContactListener {

    /** Reference to the active character avatar */
    private CharacterModel avatar;
    /** Reference to Somni DudeModel*/
    private CharacterModel somni;
    /** Reference to Phobia DudeModel*/
    private CharacterModel phobia;
    /** Reference to leading DudeModel*/
    private CharacterModel lead;
    /** Reference to combined DudeModel*/
    private CharacterModel combined;

    private Vector2 vectorCache;
    private Vector2 prevPositionVector;
    /** Reference to the goalDoor (for collision detection) */
    private BoxObstacle goalDoor;

    /** Timeout for attempting hand holding */
    private final float HAND_HOLD_TIMEOUT = 40;

    private float handHoldTimer = HAND_HOLD_TIMEOUT;

    /** shared objects */
    protected PooledList<Obstacle> sharedObjects  = new PooledList<Obstacle>();
    /** All the objects in the world. */
    protected PooledList<Obstacle> objects  = new PooledList<>();

    /** All the objects in the dark world */
    protected PooledList<Obstacle> lightObjects  = new PooledList<Obstacle>();
    /** All the objects in the light world. */
    protected PooledList<Obstacle> darkObjects  = new PooledList<Obstacle>();

    /** Currently raining platforms */
    protected PooledList<Obstacle> currRainingPlatforms = new PooledList<>();

    /** Mark set to handle more sophisticated collision callbacks */
//	protected ObjectSet<Fixture> sensorFixtures;
    protected ObjectSet<Fixture> lightSensorFixtures;
    protected ObjectSet<Fixture> darkSensorFixtures;

    protected ObjectSet<Fixture> combinedSensorFixtures;

    WorldController worldController;

    /** Whether or not characters are currently holding hands */
    private boolean holdingHands;

    /** Whether or not characters are transitioning to holding hands */
    private boolean transitioningHoldingHands;

    /** Whether or not characters were switched */
    private boolean switchedCharacters;

    private float HAND_HOLDING_DISTANCE = 2f;

    private boolean canHoldHands;
    private boolean justSeparated;
    private boolean justPropelled;
    /** Determines how long justSeparated remain true */
    private int separationCoolDown;
    /** Determines how long justSeparated remain true */
    private static final int SEPARATION_COOL_DOWN = 24;


    /**
     * Creates a new MovementController
     * @param somni The Somni CharacterModel
     * @param phobia The Phobia CharacterModel
     * @param combined The Combined CharacterModel
     * @param goalDoor The goal door
     * @param objects The objects in the world
     * @param sharedObjects The sharedObjects
     */
    public MovementController(CharacterModel somni, CharacterModel phobia, CharacterModel combined,
                              BoxObstacle goalDoor, PooledList<Obstacle> objects, PooledList<Obstacle> sharedObjects,
                              PooledList<Obstacle> lightObjects, PooledList<Obstacle> darkObjects,
                              WorldController worldController) {
        this.somni = somni;
        this.phobia = phobia;
        this.combined = combined;
        this.goalDoor = goalDoor;
        this.avatar = somni;
        this.objects = objects;
        this.sharedObjects = sharedObjects;
        this.worldController = worldController;
        this.lightObjects = lightObjects;
        this.darkObjects = darkObjects;

        lightSensorFixtures = new ObjectSet<Fixture>();
        darkSensorFixtures = new ObjectSet<Fixture>();
        combinedSensorFixtures = new ObjectSet<Fixture>();

        this.holdingHands = false;
        this.transitioningHoldingHands = false;
        vectorCache = new Vector2();
        prevPositionVector = new Vector2();
    }

    public CharacterModel getLead() {
        return lead;
    }

    public void setLead(CharacterModel lead) {
        this.lead = lead;
    }

    public CharacterModel getSomni() {
        return somni;
    }

    public void setSomni(CharacterModel somni) {
        this.somni = somni;
    }

    public CharacterModel getPhobia() {
        return phobia;
    }

    public void setPhobia(CharacterModel phobia) {
        this.phobia = phobia;
    }

    /**
     * Returns the avatar CharacterModel
     * @return the avatar
     */
    public CharacterModel getAvatar() {
        return avatar;
    }

    /**
     * Sets the avatar to a CharacterModel
     * @param newAvatar the CharacterModel
     */
    public void setAvatar(CharacterModel newAvatar) {
        avatar = newAvatar;
    }

    /**
     * Returns whether characters are holding hands
     * @return whether characters are holding hands
     */
    public boolean isHoldingHands() {
        return holdingHands;
    }

    /**
     * Sets whether characters are holding hands
     * @param b whether characters are holding hands
     */
    public void setHoldingHands(boolean b) {
        holdingHands = b;
    }


    /**
     * Returns whether characters were switched
     * @return whether characters were switched
     */
    public boolean getSwitchedCharacters() {
        return switchedCharacters;
    }

    /**
     * Sets whether characters were switched
     * @param b whether characters were switched
     */
    public void setSwitchedCharacters(boolean b) {
        switchedCharacters = b;
    }




    /**
     * Gets the hand holding Distance
     * @return the hand holding Distance
     */
    public float getHAND_HOLDING_DISTANCE() {
        return HAND_HOLDING_DISTANCE;
    }

    /**
     * Sets the Hand holding distance
     * @param f the new hand holding distance
     */
    public void setHAND_HOLDING_DISTANCE(float f) {
        HAND_HOLDING_DISTANCE = f;
    }
    /**
     * Main update loop for character movement
     */
    public int update() {

        CharacterModel follower = somni == avatar ? phobia : somni;

        if (transitioningHoldingHands) {

            if (avatar.getPosition().dst2(follower.getPosition()) < .05) {
                beginHoldHands();
                transitioningHoldingHands = false;
            }
            else if (avatar.getPosition().dst2(prevPositionVector) < .0001 || handHoldTimer < 0) {
                transitioningHoldingHands = false;
                handHoldTimer = HAND_HOLD_TIMEOUT;
            }
            else {
                Vector2 shiftDirection = vectorCache.set(follower.getPosition()).sub(avatar.getPosition());
                avatar.getBody().setLinearVelocity(shiftDirection.nor().scl(20));
                prevPositionVector.set(avatar.getPosition());

            }
            handHoldTimer--;

        } else {

            InputController inputController = InputController.getInstance();
            avatar.setMovement(inputController.getHorizontal() * avatar.getForce());
            avatar.setJumping(inputController.didJump());
            if(inputController.didDash()) {
                handleDash(inputController.getHorizontal(), inputController.getVertical());
            }
            // Check if switched
            if(inputController.didSwitch()) {
                //Switch active character
                if (!holdingHands) {
                    avatar.setMovement(0f);
                    //TODO: Add combined track

                    avatar = avatar == somni ? phobia : somni;
                }else{
//                if (lead == somni) {
//                    SoundController.getInstance().shiftMusic("phobiaTrack", "somniTrack");
//                } else {
//                    SoundController.getInstance().shiftMusic("somniTrack", "phobiaTrack");
//                }
                    lead = lead == somni ? phobia :somni;
                }
                setSwitchedCharacters(true);
            }
            else {
                setSwitchedCharacters(false);
            }
            if(avatar != combined) {
                lead = avatar;
            }
            somni.applyForce();
            phobia.applyForce();
            combined.applyForce();
            //Check if hand holding
            if(inputController.didHoldHands()) {
                handleHoldingHands();
            }

        }





        //handleworldview();

        //TODO: Play movement sounds
//        if (avatar.isJumping()) {
//            //jumpId = playSound( jumpSound, jumpId, volume );
//        } else if (avatar.isDashing()) {
//            // some dash sound
//        }


        if (somni.isDashing()) {
            somni.setGravityScale(0f);
        } else {
            somni.setGravityScale(1);
        }

        if (phobia.isDashing()) {
            phobia.setGravityScale(0f);
        } else {
            phobia.setGravityScale(1);
        }

        if (holdingHands) {
            SoundController.getInstance().shiftMusic("phobiaTrack", "combinedTrack");
            SoundController.getInstance().shiftMusic("somniTrack", "combinedTrack");
        } else {
            if (avatar == somni) {
                SoundController.getInstance().shiftMusic("phobiaTrack", "somniTrack");
                SoundController.getInstance().shiftMusic("combinedTrack", "somniTrack");
            } else {
                SoundController.getInstance().shiftMusic("somniTrack", "phobiaTrack");
                SoundController.getInstance().shiftMusic("combinedTrack", "phobiaTrack");
            }
        }



        int action = 0;
//        if(avatar.isGrounded() && !avatar.isJumping()){
//            if (avatar.getMovement() == 0f){
//                action = 0;
//            }else{
//                action = 1;
//            }
//        }else{
//            action = 2;
//        }
        if(avatar.isGrounded() && !avatar.isJumping()){
            if (avatar.getMovement() == 0f){
                action = 0; // Idle
            }else{
                action = 1; // Walk
            }
        }else{
            action = 4; // Jump
        }
        if (avatar.isDashing() && !avatar.isDashingUp() && !avatar.isDashingDown()) {
            action = 2; // Side dash
        }
        if (avatar.isDashingUp()){
            action = 3;
        }
        if (avatar.isFalling() && !holdingHands) { //! CHANGE CODE HERE WHEN ADD ASSET 4 TO HANDHOLDING!
            action = 4; // Falling
        }
        if (avatar.isDashingDown()){
            action = 5;
        }



        separationCoolDown = Math.max(0, separationCoolDown-1);

        return action;
//        if(holdingHands){
//            if(lead == somni){
//                combined.setTexture(somniphobiasTexture[action]);
//            }else{
//                combined.setTexture(phobiasomnisTexture[action]);
//            }
//        }
//        else{
//            if(lead == somni){
//                avatar.setTexture(somnisTexture[action]);
//            }else{
//                avatar.setTexture(phobiasTexture[action]);
//            }
//        }
    }


    /**
     * Performs a dash or propel
     * @param x the horizontal movement
     * @param y the vertical movement
     */
    private void handleDash(float x, float y) {
        CharacterModel oppositeCharacter = avatar == somni? phobia: somni;

        if (holdingHands) {
            // Check for propel
            endHoldHands();
            CharacterModel oppositeChar = avatar == somni? phobia: somni;

            avatar.dashOrPropel(true, x, y);
            if (!oppositeChar.isGrounded()) {
                oppositeChar.setCanDash(false);
            }

        } else if (Math.abs(somni.getPosition().dst2(phobia.getPosition())) < HAND_HOLDING_DISTANCE * HAND_HOLDING_DISTANCE
        && oppositeCharacter.getCanDash()) {
            avatar.dashOrPropel(true, x, y);
            oppositeCharacter.setFacingRight(avatar.isFacingRight());
            if (oppositeCharacter.isGrounded()) {
                avatar.setCanDash(true);
            }
        } else {
            avatar.dashOrPropel(false, x, y);
        }
    }

    /**
     * Allow Somni and Phobia to hold hands if within range
     */
    private void handleHoldingHands() {
        if (holdingHands) {
            endHoldHands();
        } else if (Math.abs(somni.getPosition().dst2(phobia.getPosition())) < HAND_HOLDING_DISTANCE * HAND_HOLDING_DISTANCE) {
            transitionHoldHands(avatar, avatar == somni? phobia: somni);
//            avatar.setLinearVelocity(new Vector2(0,10));
//            beginHoldHands();
        }
    }

    /**
     * return whether somni and phobia are within range to hold hands
     */
    protected boolean canHoldHands(){
        if (Math.abs(somni.getPosition().dst2(phobia.getPosition())) < HAND_HOLDING_DISTANCE * HAND_HOLDING_DISTANCE) {
            canHoldHands = true;
        } else {
            canHoldHands = false;
        }

        return canHoldHands;
    }

    /**
     * set whether the characters have just separated for animation purposes
     * @param value
     */
    protected void setJustSeparated(boolean value){
        if (separationCoolDown<=0 && !value)
            justSeparated = value;
    }

    /**
     * set whether the characters have just propelled for animation purposes
     * @param value
     */
    protected void setJustPropelled(boolean value){
        justPropelled = value;
    }

    /**
     * returns whether the characters have just separated
     */
    protected boolean justSeparated(){
        return justSeparated;
    }

    /**
     * returns whether the characters have just propelled
     */
    protected boolean justPropelled(){
        return justPropelled;
    }

    /**
     * Returns whether the moving characters is facing the idle character
     * for animation purposes
     * 0 = true, 1 = false
     */
    protected int faceTowards(){
        if (lead == somni){
            if ((somni.isFacingRight() && somni.getX() <= phobia.getX()) ||
                    (!somni.isFacingRight() && somni.getX() >= phobia.getX())) {
                return 0;
            }else {
                return 1;
            }
        } else {
            if ((phobia.isFacingRight() && phobia.getX() <= somni.getX()) ||
                    (!phobia.isFacingRight() && phobia.getX() >= somni.getX())) {
                return 0;
            }else {
                return 1;
            }
        }
    }

    /**
     * Stops holding hands
     */
    private void endHoldHands() {
        if (separationCoolDown<=0){
            separationCoolDown = SEPARATION_COOL_DOWN;
//            justSeparated = true;
        }
        justPropelled = true;

        somni.setActive(true);
        phobia.setActive(true);
        combined.setActive(false);

        objects.add(somni);
        objects.add(phobia);
        objects.remove(combined);
        sharedObjects.add(somni);
        sharedObjects.add(phobia);
        sharedObjects.remove(combined);


        float avatarX = avatar.getX();
        float avatarY = avatar.getY();
        float avatarVX = avatar.getVX();
        float avatarVY = avatar.getVY();

        int directionMultiplier = combined.isFacingRight()? 1: -1;

        avatar = lead;
        avatar.setPosition(avatarX + avatar.getWidth()*0.4f * directionMultiplier, avatarY);
        avatar.setVX(avatarVX);
        avatar.setVY(avatarVY);
        float dampeningFactor = -0.25f;
        if(lead == phobia){
//            phobia.setCanDash(true);
            somni.setPosition(avatarX + somni.getWidth()*0.65f* -directionMultiplier, avatarY);
            somni.setVX(avatarVX * dampeningFactor);
            somni.setVY(0);
        }else {
//            somni.setCanDash(true);
            phobia.setPosition(avatarX + phobia.getWidth()*0.65f * -directionMultiplier, avatarY);
            phobia.setVX(avatarVX * dampeningFactor);
            phobia.setVY(0);
        }
        somni.setFacingRight(combined.isFacingRight());
        phobia.setFacingRight(combined.isFacingRight());
        holdingHands = false;
    }

    private void transitionHoldHands(CharacterModel leadCharacter, CharacterModel follower) {
        //Direction to move leadCharacter towards
        Vector2 shiftDirection = vectorCache.set(follower.getPosition()).sub(leadCharacter.getPosition());
        leadCharacter.getBody().setLinearVelocity(shiftDirection.nor().scl(20));
        transitioningHoldingHands = true;
    }

    /**
     * Somni and Phobia hold hands
     */
    private void beginHoldHands() {
        CharacterModel follower = somni == avatar ? phobia : somni;
        int directionMultiplier = avatar.isFacingRight()? 1: -1;

        if (follower.isGrounded()) {
            avatar.setCanDash(true);
        }

        somni.setMovement(0f);
        phobia.setMovement(0f);
        combined.setMovement(0f);
        combined.setVX(0f);
        combined.setVY(0f);

        somni.setActive(false);
        phobia.setActive(false);
        combined.setActive(true);

        lead = avatar;
        sharedObjects.remove(somni);
        sharedObjects.remove(phobia);
        objects.remove(somni);
        objects.remove(phobia);
        objects.add(combined);
        sharedObjects.add(combined);


        float avatarX = follower.getX();
        float avatarY = follower.getY();

        combined.setFacingRight(avatar.isFacingRight());
        avatar = combined;
        avatar.setPosition(avatarX + combined.getWidth() * .55f * directionMultiplier, avatarY);


        holdingHands = true;
    }

    /**
     * Callback method for the start of a collision
     *
     * This method is called when we first get a collision between two objects.  We use
     * this method to test if it is the "right" kind of collision.  In particular, we
     * use it to test if we made it to the win door.
     *
     * @param contact The two bodies that collided
     */
    @Override
    public void beginContact(Contact contact) {
        Fixture fix1 = contact.getFixtureA();
        Fixture fix2 = contact.getFixtureB();

        Body body1 = fix1.getBody();
        Body body2 = fix2.getBody();

        Object fd1 = fix1.getUserData();
        Object fd2 = fix2.getUserData();

        try {
            Obstacle bd1 = (Obstacle)body1.getUserData();
            Obstacle bd2 = (Obstacle)body2.getUserData();
            int tile1 = -1;
            int tile2 = -1;

            //Harming platforms

            if (bd1 instanceof PlatformModel && ((PlatformModel) bd1).getProperty() == PlatformModel.harming) {
                if (somni.getCore().equals(fix2) || somni.getCap1().equals(fix2) || somni.getCap2().equals(fix2) ||
                        phobia.getCore().equals(fix2) || phobia.getCap1().equals(fix2) || phobia.getCap2().equals(fix2) ||
                            combined.getCore().equals(fix2) || combined.getCap1().equals(fix2) || combined.getCap2().equals(fix2)) {
                    worldController.setFailure(true);
                }
            } else if (bd2 instanceof PlatformModel && ((PlatformModel) bd2).getProperty() == PlatformModel.harming) {
                if (somni.getCore().equals(fix1) || somni.getCap1().equals(fix1) || somni.getCap2().equals(fix1) ||
                        phobia.getCore().equals(fix1) || phobia.getCap1().equals(fix1) || phobia.getCap2().equals(fix1) ||
                        combined.getCore().equals(fix1) || combined.getCap1().equals(fix1) || combined.getCap2().equals(fix1)) {
                    worldController.setFailure(true);
                }
            }

            // See if we have landed on the ground.
            if ((somni.getSensorName().equals(fd2) && somni != bd1 && goalDoor != bd1) ||
                    (somni.getSensorName().equals(fd1) && somni != bd2 && goalDoor != bd2)) {
                somni.setGrounded(true);
                lightSensorFixtures.add(somni == bd1 ? fix2 : fix1); // Could have more than one ground
//				somni.canJump = true;
                somni.setGround(somni == bd1 ? bd2: bd1);
                if (bd1 instanceof PlatformModel && ((PlatformModel) bd1).getProperty() == PlatformModel.crumbling)  {
                    if (((PlatformModel) bd1).getTouching() == phobia) {
                        beginRainAnimation((PlatformModel) bd1);

                    } else {
                        ((PlatformModel) bd1).setTouching(somni);
                    }
                } else if (bd2 instanceof PlatformModel && ((PlatformModel) bd2).getProperty() == PlatformModel.crumbling) {
                    if (((PlatformModel) bd2).getTouching() == phobia) {

                        beginRainAnimation((PlatformModel) bd2);
                    } else {
                        ((PlatformModel) bd2).setTouching(somni);
                    }
                }

            }
            if ((phobia.getSensorName().equals(fd2) && phobia != bd1 && goalDoor != bd1) ||
                    (phobia.getSensorName().equals(fd1) && phobia != bd2 && goalDoor != bd2)) {
                phobia.setGrounded(true);
                darkSensorFixtures.add(phobia == bd1 ? fix2 : fix1); // Could have more than one ground
//				phobia.canJump = true;
                phobia.setGround(phobia == bd1 ? bd2: bd1);
                if (bd1 instanceof PlatformModel && ((PlatformModel) bd1).getProperty() == PlatformModel.crumbling)  {
                    if (((PlatformModel) bd1).getTouching() == somni) {
                        beginRainAnimation((PlatformModel) bd1);
                    } else {
                        ((PlatformModel) bd1).setTouching(phobia);
                    }
                } else if (bd2 instanceof PlatformModel && ((PlatformModel) bd2).getProperty() == PlatformModel.crumbling) {
                    if (((PlatformModel) bd2).getTouching() == somni) {

                        beginRainAnimation((PlatformModel) bd2);
                    } else {
                        ((PlatformModel) bd2).setTouching(phobia);
                    }
                }

            }
            if (avatar == combined && ((avatar.getSensorName().equals(fd2) && avatar != bd1 && goalDoor != bd1) ||
                    (avatar.getSensorName().equals(fd1) && avatar != bd2 && goalDoor != bd2))) {
                avatar.setGrounded(true);
                somni.setCanDash(true);
                phobia.setCanDash(true);
                combinedSensorFixtures.add(avatar == bd1 ? fix2 : fix1); // Could have more than one ground
//				combined.canJump = true;
                combined.setGround(combined == bd1 ? bd2: bd1);
                if (bd1 instanceof PlatformModel && ((PlatformModel) bd1).getProperty() == PlatformModel.crumbling) {
                    beginRainAnimation((PlatformModel) bd1);
                } else if (bd2 instanceof PlatformModel && ((PlatformModel) bd2).getProperty() == PlatformModel.crumbling) {
                    beginRainAnimation((PlatformModel) bd2);
                }
            }


            // Check for win condition
            if ((bd1 == combined  && bd2 == goalDoor) ||
                    (bd1 == goalDoor && bd2 == combined)) {
                worldController.setComplete(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void handleLanding() {
//
//    }

    /**
     * Callback method for the end of a collision
     *
     * This method is called when two objects cease to touch.  The main use of this method
     * is to determine when the characer is NOT on the ground.  This is how we prevent
     * double jumping.
     */
    @Override
    public void endContact(Contact contact) {
        Fixture fix1 = contact.getFixtureA();
        Fixture fix2 = contact.getFixtureB();

        Body body1 = fix1.getBody();
        Body body2 = fix2.getBody();

        Object fd1 = fix1.getUserData();
        Object fd2 = fix2.getUserData();

        Object bd1 = body1.getUserData();
        Object bd2 = body2.getUserData();

        if ((somni.getSensorName().equals(fd2) && somni != bd1 && goalDoor != bd1) ||
                (somni.getSensorName().equals(fd1) && somni != bd2 && goalDoor != bd2)) {

            lightSensorFixtures.remove(somni == bd1 ? fix2 : fix1);

            if (lightSensorFixtures.size == 0) {
                somni.setGrounded(false);
                somni.setGround(null);
            }

            if (bd1 instanceof PlatformModel && ((PlatformModel) bd1).getProperty() == PlatformModel.crumbling &&
                    ((PlatformModel) bd1).getTouching() == somni)  {
                ((PlatformModel) bd1).setTouching(null);
            } else if (bd2 instanceof PlatformModel && ((PlatformModel) bd2).getProperty() == PlatformModel.crumbling &&
                    ((PlatformModel) bd2).getTouching() == somni) {
                ((PlatformModel) bd2).setTouching(null);

            }
        }
        if ((phobia.getSensorName().equals(fd2) && phobia != bd1 && goalDoor != bd1) ||
                (phobia.getSensorName().equals(fd1) && phobia != bd2 && goalDoor != bd2)) {

            darkSensorFixtures.remove(phobia == bd1 ? fix2 : fix1);

            if (darkSensorFixtures.size == 0) {
                phobia.setGrounded(false);
                phobia.setGround(null);

            }
            if (bd1 instanceof PlatformModel && ((PlatformModel) bd1).getProperty() == PlatformModel.crumbling &&
                    ((PlatformModel) bd1).getTouching() == phobia)  {
                ((PlatformModel) bd1).setTouching(null);
            } else if (bd2 instanceof PlatformModel && ((PlatformModel) bd2).getProperty() == PlatformModel.crumbling &&
                    ((PlatformModel) bd2).getTouching() == phobia) {
                ((PlatformModel) bd2).setTouching(null);

            }
        }
        if ((avatar.getSensorName().equals(fd2) && avatar != bd1 && goalDoor != bd1) ||
                (avatar.getSensorName().equals(fd1) && avatar != bd2 && goalDoor != bd2)) {
            combinedSensorFixtures.remove(avatar == bd1 ? fix2 : fix1);

            if (combinedSensorFixtures.size == 0) {
                combined.setGrounded(false);
                combined.setGround(null);
            }
        }
    }


    /**
     * Sets the currently raining platforms objects
     * @param currRainingPlatforms
     */
    public void setCurrRainingPlatforms(PooledList<Obstacle> currRainingPlatforms) {
        this.currRainingPlatforms = currRainingPlatforms;
    }

    private void beginRainAnimation(PlatformModel platform) {
        currRainingPlatforms.add(platform);
        platform.setRainingCooldown(PlatformController.rainingCooldown);
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
