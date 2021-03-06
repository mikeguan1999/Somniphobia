/*
 * SoundController.java
 *
 * Sound management in LibGDX is horrible.  It is the absolute worse thing
 * about the engine.  Furthermore, because of OpenAL bugs in OS X, it is
 * even worse than that.  There is a lot of magic vodoo that you have to
 * do to get everything working properly.  This class hides all of that
 * for you and makes it easy to play sound effects.
 *
 * Note that this class is an instance of a Singleton.  There is only one
 * SoundController at a time.  The constructor is hidden and you cannot
 * make your own sound controller.  Instead, you use the method getInstance()
 * to get the current sound controller.
 *
 * Author:  Walker M. White
 * Version: 4/15/2021
 */
package edu.cornell.gdiac.audio;

import com.badlogic.gdx.utils.*;
/**
 * A singleton class for controlling sound effects in LibGDX
 *
 * Sound sucks in LibGDX for three reasons.  (1) You have to keep track of
 * a mysterious number every time you play a sound.  (2) You have no idea
 * when a sound has finished playing.  (3) OpenAL bugs on OS X cause popping
 * and distortions if you have no idea what you are doing.  If you are using
 * the GDIAC audio classes, then those solve (2) and (3).  This class is 
 * a pontential solution for (1)
 *
 * To get around (1), this sound controller uses a key management system.  
 * Instead of waiting for a number after playing the sound, you give it a
 * key ahead of time.  The key allows you to identify different instances
 * of the same sound.  
 *
 * Note that this class is only designed for processing sound effects only
 * (e.g. SoundBuffer objects).  It is not designed for music as music does not
 * have simultaneous instances.
 */
public class SoundController implements SoundBuffer.OnCompletionListener {
    /**
     * Inner class to track and active sound instance
     *
     * A sound instance is a Sound object and a number. That is because
     * a single Sound object may have multiple instances.
     */
    private class ActiveSound {
        /** Reference to the sound resource */
        public SoundBuffer sound;
        /** The id number representing the sound instance */
        public long  id;
        /** The reverse key for the callback */
        public String revkey;
        /**
         * Creates a new active sound with the given values
         *
         * @param s	Reference to the sound resource
         * @param n The id number representing the sound instance
         */
        public ActiveSound(SoundBuffer s, long n) {
            sound = s;
            id = n;
            revkey = s.getFile().path()+n;
        }
    }

    /** The singleton Sound controller instance */
    private static SoundController controller;

    /** Keeps track of all of the allocated sound resources */
    private IdentityMap<String,SoundBuffer> soundbank;
    /** Keeps track of all of the "active" sounds */
    private IdentityMap<String,ActiveSound> actives;
    /** Reverse map of instance ids to keys */
    private ObjectMap<String,String> keyMap;

    private SoundBuffer winTrack;
    private SoundBuffer failTrack;



    /** Volume values **/
    private float volume;
    private float somniVolume;
    private float phobiaVolume;
    private float combinedVolume;

    /**
     * Creates a new SoundController with the default settings.
     */
    private SoundController() {
        soundbank = new IdentityMap<String,SoundBuffer>();
        actives = new IdentityMap<String,ActiveSound>();
        keyMap  = new ObjectMap<String,String>();
        volume = 0.5f;
        somniVolume = 0;
        phobiaVolume = 0;
        combinedVolume = 0;
    }

    /**
     * Resets the level music
     */
    public void resetLevelMusic() {
        somniVolume = 1;
        phobiaVolume = 0;
        combinedVolume = 0;
    }

    /**
     * Sets the wintrack
     * @param winTrack
     */
    public void setWinTrack(SoundBuffer winTrack) {
        this.winTrack = winTrack;
    }

    /**
     * Returns the win track
     * @return the win track
     */
    public SoundBuffer getWinTrack() {
        return winTrack;
    }


    /**
     * Sets the fail track
     * @param failTrack
     */
    public void setFailTrack(SoundBuffer failTrack) {
        this.failTrack = failTrack;
    }

    /**
     * Returns the win track
     * @return the win track
     */
    public SoundBuffer getFailTrack() {
        return failTrack;
    }

    /**
     * Returns the single instance for the SoundController
     *
     * The first time this is called, it will construct the SoundController.
     *
     * @return the single instance for the SoundController
     */
    public static SoundController getInstance() {
        if (controller == null) {
            controller = new SoundController();
        }
        return controller;
    }

    public void setVolume(float value, String key){
        volume = value;
        ActiveSound snd = actives.get(key);
        snd.sound.setVolume(snd.id, volume);
    }
    /// Sound Management
    /**
     * Plays the an instance of the given sound
     *
     * A sound is defined by a sound buffer, which is the Linear PCM data loaded entirely
     * into memory. You can have multiple instances of the same sound buffer playing.
     * You use the key to identify a sound instance.  You can only have one key playing
     * at a time.  If a key is in use, the existing sound will be stopped to make way for
     * this one.
     *
     * @param key		The identifier for this sound instance
     * @param sound		The buffer with the sound data
     *
     * @return True if the sound was successfully played
     */
    public boolean play(String key, SoundBuffer sound) {
        return play(key,sound,volume, false);
    }

    /**
     * Plays the an instance of the given sound
     *
     * A sound is defined by a sound buffer, which is the Linear PCM data loaded entirely
     * into memory. You can have multiple instances of the same sound buffer playing.
     * You use the key to identify a sound instance.  You can only have one key playing
     * at a time.  If a key is in use, the existing sound will be stopped to make way for
     * this one.
     *
     * @param key		The identifier for this sound instance
     * @param sound		The buffer with the sound data
     * @param volume	The sound volume in the range [0,1]
     *
     * @return True if the sound was successfully played
     */
    public boolean play(String key, SoundBuffer sound, float volume) {
        return play(key,sound, volume,false);
    }

    /**
     * Shifts from one background music to another
     * @param currentMusicTag the current playing music
     * @param newMusicTag the new music to play
     */
    public void shiftMusic(String currentMusicTag, String newMusicTag) {
        if (!actives.isEmpty()) {
            SoundController.ActiveSound currentMusic = actives.get(currentMusicTag);
            SoundController.ActiveSound newMusic = actives.get(newMusicTag);

            float crossFade = .05f;

//            currentMusic.sound.setVolume(currentMusic.id, 1);
            //TODO: CrossFade
            currentMusic.sound.setVolume(currentMusic.id, Math.max(0,
                    currentMusic.sound.getVolume(currentMusic.id) - crossFade) );

//            currentMusic.sound.setVolume(currentMusic.id, 0f * volume);

            newMusic.sound.setVolume(newMusic.id, Math.min(volume,
                    newMusic.sound.getVolume(newMusic.id) + crossFade) );

//            newMusic.sound.setVolume(newMusic.id, 1f * volume);
        }
    }
    /**
     * Plays the an instance of the given sound
     *
     * A sound is defined by a sound buffer, which is the Linear PCM data loaded entirely
     * into memory. You can have multiple instances of the same sound buffer playing.
     * You use the key to identify a sound instance.  You can only have one key playing
     * at a time.  If a key is in use, the existing sound will be stopped to make way for
     * this one.
     *
     * @param key		The identifier for this sound instance
     * @param sound		The buffer with the sound data
     * @param volume	The sound volume in the range [0,1]
     * @param loop		Whether to loop the sound
     *
     * @return True if the sound was successfully played
     */
    public boolean play(String key, SoundBuffer sound, float volume, boolean loop) {
        // If there is a sound for this key, stop it
        stop(key);

        // Check to see if we are an active listener for this sound buffer
        String path = sound.getFile().path();
        if (!soundbank.containsKey(path)) {
            soundbank.put(path,sound);
            sound.setOnCompletionListener(this);
        }

        // Play the new sound and add it
        long id = sound.play(volume);
        if (id == -1) {
            return false;
        } else if (loop) {
            sound.setLooping(id, true);
        }

        ActiveSound act = new ActiveSound(sound,id);
        actives.put(key,act);
        keyMap.put(act.revkey,key);
        return true;
    }

    /**
     * Stops the sound, allowing its key to be reused.
     *
     * If there is no sound instance for the key, this method does nothing.
     *
     * @param key	The sound instance to stop.
     */
    public void stop(String key) {
        // Get the active sound for the key
        if (!actives.containsKey(key)) {
            return;
        }

        // Stop the sound
        ActiveSound snd = actives.get(key);
        snd.sound.stop(snd.id);
        actives.remove(key);
        keyMap.remove(snd.revkey);
    }

    /**
     * Returns true if the sound instance is currently active
     *
     * @param key	The sound instance identifier
     *
     * @return true if the sound instance is currently active
     */
    public boolean isActive(String key) {
        return actives.containsKey(key);
    }

    /**
     * Returns the number of sounds currently playing
     *
     * @return true number of sounds currently playing
     */
    public int size() {
        return actives.size;
    }

    /**
     * Called when the end of a music stream is reached during playback.
     *
     * @param buffer    The sound buffer that finished playing
     * @param instance     The particular instance that has completed
     */
    public void onCompletion(SoundBuffer buffer, long instance) {
        String revkey = buffer.getFile().path()+instance;
        if (keyMap.containsKey(revkey)) {
            String key = keyMap.get(revkey);
            actives.remove(key);
            keyMap.remove(revkey);
        }
    }
}