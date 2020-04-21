package com.objects;


import java.util.Vector;

/**
 * A container class for all objects in the world
 */
public class World {
    public static Vector<Entity> objects;
    public static SpaceShip pilot;

    public World(){
        this.objects = new Vector<>();
        pilot = new SpaceShip(0, 2f);

        new Planet(0,0, 3);
        new Star(0, -2f);
    }

}
