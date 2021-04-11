package edu.cornell.gdiac.somniphobia.game.controllers;

import edu.cornell.gdiac.util.PooledList;

public class LevelCreator {
    class Platform {
        int posX;
        int posY;
        int width;
        int height;
        public Platform(int posX, int posY, int width, int height) {
            this.posX = posX;
            this.posY = posY;
            this.width = width;
            this.height = height;
        }
    }

    class Level {
        int width;
        int height;
        PooledList<Platform> platformList;
        public Level(PooledList<Platform> platformList) {
            this.platformList = platformList;
        }
        // TODO: Add platform
        public void addPlatform() {

        }
        // TODO: Delete platform
        public void deletePlatform() {

        }
    }

    public LevelCreator() {
        //TODO
    }

    // TODO: Implement

}
