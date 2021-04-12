package edu.cornell.gdiac.somniphobia.game.controllers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.ObjectSet;
import edu.cornell.gdiac.somniphobia.InputController;
import edu.cornell.gdiac.somniphobia.WorldController;
import edu.cornell.gdiac.somniphobia.game.models.CharacterModel;
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

    /** Reference to the goalDoor (for collision detection) */
    private BoxObstacle goalDoor;

    /** shared objects */
    protected PooledList<Obstacle> sharedObjects  = new PooledList<Obstacle>();
    /** All the objects in the world. */
    protected PooledList<Obstacle> objects  = new PooledList<>();

    /** Mark set to handle more sophisticated collision callbacks */
//	protected ObjectSet<Fixture> sensorFixtures;
    protected ObjectSet<Fixture> lightSensorFixtures;
    protected ObjectSet<Fixture> darkSensorFixtures;

    protected ObjectSet<Fixture> combinedSensorFixtures;

    WorldController worldController;

    /** Whether or not characters are currently holding hands */
    private boolean holdingHands;

    /** Whether or not characters were switched */
    private boolean switchedCharacters;

    private float HAND_HOLDING_DISTANCE = 2f;


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
                              WorldController worldController) {
        this.somni = somni;
        this.phobia = phobia;
        this.combined = combined;
        this.goalDoor = goalDoor;
        this.avatar = somni;
        this.objects = objects;
        this.sharedObjects = sharedObjects;
        this.worldController = worldController;

        lightSensorFixtures = new ObjectSet<Fixture>();
        darkSensorFixtures = new ObjectSet<Fixture>();
        combinedSensorFixtures = new ObjectSet<Fixture>();

        this.holdingHands = false;
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
        InputController inputController = InputController.getInstance();
        avatar.setMovement(inputController.getHorizontal() * avatar.getForce());
        avatar.setJumping(inputController.didJump());

        if(inputController.didDash()) {
            handleDash(inputController.getHorizontal(), inputController.getVertical());
        }

        somni.applyForce();
        phobia.applyForce();
        combined.applyForce();
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

        // Check if switched
        if(inputController.didSwitch()) {
            //Switch active character
            if (!holdingHands) {
                avatar.setMovement(0f);
                avatar = avatar == somni ? phobia : somni;
            }else{
                lead = lead == somni ? phobia :somni;
            }
            setSwitchedCharacters(true);
        }
        else {
            setSwitchedCharacters(false);
        }
        if(avatar !=combined) {
            lead = avatar;
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
        if (avatar.isDashing() && !avatar.isDashingUp()) {
            action = 2; // Side dash
        }
        if (avatar.isFalling() && !holdingHands) { //! CHANGE CODE HERE WHEN ADD ASSET 4 TO HANDHOLDING!
            action = 4; // Falling
        }

        //Check if hand holding
        if(inputController.didHoldHands()) {
            handleHoldingHands();
        }

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
        if (holdingHands) {
            // Check for propel
            endHoldHands();
            avatar.dashOrPropel(true, x, y);

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
            beginHoldHands();
        }
    }

    /**
     * Stops holding hands
     */
    private void endHoldHands() {
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

        avatar = lead;
        avatar.setPosition(avatarX, avatarY);
        avatar.setVX(avatarVX);
        avatar.setVY(avatarVY);
        float dampeningFactor = -0.25f;
        if(lead == phobia){
            phobia.setCanDash(true);
            somni.setPosition(avatarX, avatarY);
            somni.setVX(avatarVX * dampeningFactor);
            somni.setVY(0);
        }else {
            somni.setCanDash(true);
            phobia.setPosition(avatarX, avatarY);
            phobia.setVX(avatarVX * dampeningFactor);
            phobia.setVY(0);
        }
        somni.setFacingRight(combined.isFacingRight());
        phobia.setFacingRight(combined.isFacingRight());
        holdingHands = false;
    }


    /**
     * Somni and Phobia hold hands
     */
    private void beginHoldHands() {

        somni.setMovement(0f);
        phobia.setMovement(0f);

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


        CharacterModel follower = somni == avatar ? phobia : somni;
        float avatarX = follower.getX();
        float avatarY = follower.getY();

        avatar = combined;
        avatar.setPosition(avatarX, avatarY);

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

            // See if we have collided with a wall
            if (avatar.getCore().equals(fix1) || avatar.getCore().equals(fix2) ||
                    avatar.getCap1().equals(fix1) || avatar.getCap1().equals(fix2) ||
                    avatar.getCap2().equals(fix1) || avatar.getCap2().equals(fix2)) {
                avatar.endDashing();
                avatar.setGravityScale(1);

            }

            // See if we have landed on the ground.
            if ((somni.getSensorName().equals(fd2) && somni != bd1 && goalDoor != bd1) ||
                    (somni.getSensorName().equals(fd1) && somni != bd2 && goalDoor != bd2)) {
                somni.setGrounded(true);
                lightSensorFixtures.add(somni == bd1 ? fix1 : fix2); // Could have more than one ground
//				somni.canJump = true;

            }
            if ((phobia.getSensorName().equals(fd2) && phobia != bd1 && goalDoor != bd1) ||
                    (phobia.getSensorName().equals(fd1) && phobia != bd2 && goalDoor != bd2)) {
                phobia.setGrounded(true);
                darkSensorFixtures.add(phobia == bd1 ? fix1 : fix2); // Could have more than one ground
//				phobia.canJump = true;
            }
            if (avatar == combined && (avatar.getSensorName().equals(fd2) && avatar != bd1 && goalDoor != bd1) ||
                    (avatar.getSensorName().equals(fd1) && avatar != bd2 && goalDoor != bd2)) {
                avatar.setGrounded(true);
                combinedSensorFixtures.add(avatar == bd1 ? fix1 : fix2); // Could have more than one ground
//				combined.canJump = true;
            }


            // Check for win condition
            if ((bd1 == combined   && bd2 == goalDoor) ||
                    (bd1 == goalDoor && bd2 == combined)) {
                worldController.setComplete(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

            lightSensorFixtures.remove(somni == bd1 ? fix1 : fix2);

            if (lightSensorFixtures.size == 0) {
                somni.setGrounded(false);
            }
        }
        if ((phobia.getSensorName().equals(fd2) && phobia != bd1 && goalDoor != bd1) ||
                (phobia.getSensorName().equals(fd1) && phobia != bd2 && goalDoor != bd2)) {
            darkSensorFixtures.remove(phobia == bd1 ? fix1 : fix2);

            if (darkSensorFixtures.size == 0) {
                phobia.setGrounded(false);
            }
        }
        if ((avatar.getSensorName().equals(fd2) && avatar != bd1 && goalDoor != bd1) ||
                (avatar.getSensorName().equals(fd1) && avatar != bd2 && goalDoor != bd2)) {
            combinedSensorFixtures.remove(avatar == bd1 ? fix1 : fix2);

            if (combinedSensorFixtures.size == 0) {
                avatar.setGrounded(false);
            }
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
