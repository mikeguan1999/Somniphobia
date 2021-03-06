package edu.cornell.gdiac.audio;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.*;
import edu.cornell.gdiac.assets.*;
import edu.cornell.gdiac.audio.*;
import edu.cornell.gdiac.util.*;
import org.lwjgl.Sys;

public class MusicController {

    /**
     * Inner class to track and active sound instance
     *
     * A sound instance is a Music object and a number.  That is because
     * a single Music object may have multiple instances.  We do not
     * know when a sound ends.  Therefore, we simply let the sound go
     * and we garbage collect when the lifespace is greater than the
     * sound limit.
     */
    private class ActiveMusic {
        /** Reference to the sound resource */
        public MusicBuffer music;
        /** The id number representing the sound instance */
        public long  id;
        /** Is the sound looping (so no garbage collection) */
        public boolean loop;
        /** How long this sound has been running */
        public long lifespan;

        /**
         * Creates a new active sound with the given values
         *
         * @param m	Reference to the sound resource
         * @param b Is the sound looping (so no garbage collection)
         */
        public ActiveMusic(MusicBuffer m, boolean b) {
            music = m;
            loop = b;
            lifespan = 0;
        }
    }


    /** The singleton Music controller instance */
    private static MusicController controller;

    /** Keeps track of all of the allocated sound resources */
    private ObjectMap<String, MusicBuffer> musicbank;
    /** Reverse look up of source files */
    private IdentityMap<MusicBuffer,String> musicsrc;
    /** Keeps track of all of the "active" sounds */
    private ObjectMap<String, MusicController.ActiveMusic> actives;
    /** Support class for garbage collection */
    private Array<String> collection;

    private boolean menu;

    private boolean shifted;

    private float volume;

    /** The reader to process JSON files */
    protected JsonReader jsonReader;
    /** The JSON asset directory */
    protected JsonValue assetDirectory;


    /**
     * Creates a new MusicController with the default settings.
     */
    private MusicController() {
        musicbank = new ObjectMap<String, MusicBuffer>();
        musicsrc = new IdentityMap<MusicBuffer,String>();
        actives = new ObjectMap<String, ActiveMusic>();
        collection = new Array<String>();
        shifted = false;
        volume = 1;
    }

    /**
     * Returns the single instance for the MusicController
     *
     * The first time this is called, it will construct the MusicController.
     *
     * @return the single instance for the MusicController
     */
    public static MusicController getInstance() {
        if (controller == null) {
            controller = new MusicController();
        }
        return controller;
    }

    /**
     * returns the volume modifier
     * @return the volume modifier
     */
    public float getVolume(){
        return volume;
    }

    /**
     * sets the new volume modifier
     * @param v new volume modifier
     */
    public void setVolume(float v){
        volume = v;
    }

    /// Music Management
    /**
     * Uses the asset manager to allocate a sound
     *
     * All sound assets are managed internally by the controller.  Do not try
     * to access the sound directly.  Use the play and stop methods instead.
     *
     * @param manager  A reference to the asset manager loading the sound
     * @param filename The filename for the sound asset
     */
    public void allocate(AssetManager manager, String filename) {
        MusicBuffer music = (MusicBuffer) manager.get(filename, Music.class);
        musicbank.put(filename, music);
        musicsrc.put(music,filename);
    }

//
//    /**
//     * Shifts from one background music to another
//     * @param currentMusicTag the current playing music
//     * @param newMusicTag the new music to play
//     */
//    public void shiftMusic(String currentMusicTag, String newMusicTag) {
//        if (!actives.isEmpty()) {
//            MusicBuffer currentMusic = actives.get(currentMusicTag).music;
//            MusicBuffer newMusic = actives.get(newMusicTag).music;
//
//            float crossFade = .05f;
//
////            currentMusic.sound.setVolume(currentMusic.id, 1);
//            //TODO: CrossFade
//            currentMusic.setVolume(Math.max(0,
//                    currentMusic.getVolume() - crossFade) );
//
////            currentMusic.sound.setVolume(currentMusic.id, 0f * volume);
//
//            newMusic.setVolume(Math.min(volume,
//                    newMusic.getVolume() + crossFade) );
//
////            newMusic.sound.setVolume(newMusic.id, 1f * volume);
//        }
//    }


    /**Deallocate all sounds*/
    public void deallocate(AssetManager manager, String filename) {
        MusicBuffer music = (MusicBuffer) manager.get(filename,Music.class);
        musicbank.remove(filename);
        musicsrc.remove(music);
    }

    public String getSource(MusicBuffer music) {
        return musicsrc.get(music);
    }

    /**
     * Plays the an instance of the given sound
     *
     * A sound is identified by its filename.  You can have multiple instances of the
     * same sound playing.  You use the key to identify a sound instance.  You can only
     * have one key playing at a time.  If a key is in use, the existing sound may
     * be garbage collected to allow you to reuse it, depending on the settings.
     *
     * However, it is also possible that the key use may fail.  In the latter case,
     * this method returns false.  In addition, if the sound is currently looping,
     * then this method will return true but will not stop and restart the sound.
     *
     *
     * @param key		The identifier for this sound instance
     * @param filename	The filename of the sound asset
     * @param loop		Whether to loop the sound
     *
     * @return True if the sound was successfully played
     */
    public boolean play(String key, String filename, boolean loop) {
        return play(key,filename,1.0f, loop);
    }


    /**
     * Plays the an instance of the given sound
     *
     * A sound is identified by its filename.  You can have multiple instances of the
     * same sound playing.  You use the key to identify a sound instance.  You can only
     * have one key playing at a time.  If a key is in use, the existing sound may
     * be garbage collected to allow you to reuse it, depending on the settings.
     *
     * However, it is also possible that the key use may fail.  In the latter case,
     * this method returns false.  In addition, if the sound is currently looping,
     * then this method will return true but will not stop and restart the sound.
     *
     *
     * @param key		The identifier for this sound instance
     * @param filename	The filename of the sound asset
     * @param loop		Whether to loop the sound
     * @param volume	The sound volume in the range [0,1]
     *
     * @return True if the sound was successfully played
     */
    public boolean play(String key, String filename, float volume, boolean loop) {
        // Get the sound for the file
//		System.out.println(musicbank.containsKey(filename));
        if (!musicbank.containsKey(filename)) {
            return false;
        }

        // If there is a sound for this key, stop it
        MusicBuffer music = musicbank.get(filename);
        if (actives.containsKey(key)) {
            MusicController.ActiveMusic snd = actives.get(key);
            if (!snd.loop) {
                // This is a workaround for the OS X sound bug
                //snd.sound.stop(snd.id);
                snd.music.setVolume(0);
            } else {
                return true;
            }
        }

        // Play the new sound and add it
        music.setVolume(volume * this.volume);
        music.play();
        if (loop) {
            music.setLooping(true);
        }

        actives.put(key,new MusicController.ActiveMusic(music, loop));
        return true;
    }

    /**
     * Stops the sound, allowing its key to be reused.
     *
     * This is the only way to stop a sound on a loop.  Otherwise it will
     * play forever.
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

        MusicController.ActiveMusic snd = actives.get(key);

        // This is a workaround for the OS X sound bug
        //snd.sound.stop(snd.id);
        snd.music.setLooping(false); // Will eventually garbage collect
        snd.music.setVolume(0.0f);
        actives.remove(key);
    }

    public void stopAll() {
        for (String m : actives.keys()){
            MusicBuffer music = actives.get(m).music;
            menu = false;
            music.stop();
            music.setLooping(false);
            music.setVolume(0.0f);
            actives.remove(m);
        }
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

    public void setVolume(float value, String key){
        volume = value;
        MusicBuffer snd = actives.get(key).music;
        snd.setVolume(volume);
    }


    /**
     * Shifts from one background music to another
     * @param currentMusicTag the current playing music
     * @param newMusicTag the new music to play
     */
    public void shiftMusic(String currentMusicTag, String newMusicTag) {
        if (!actives.isEmpty()) {
            MusicBuffer currentMusic = actives.get(currentMusicTag).music;
            MusicBuffer newMusic = actives.get(newMusicTag).music;

//            System.out.println(currentMusic.sound.getVolume(currentMusic.id, 0));
            float crossFade = .05f;

            //TODO: CrossFade
            currentMusic.setVolume(Math.max(0,
                    currentMusic.getVolume() - crossFade));

            newMusic.setVolume(Math.min(volume,
                    newMusic.getVolume() + crossFade));

        }

        for(String key : actives.keys()) {
            MusicController.ActiveMusic snd = actives.get(key);
            snd.lifespan++;
        }
        for(String key : collection) {
            actives.remove(key);
        }
        collection.clear();

    }
}
