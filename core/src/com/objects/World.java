package com.objects;


import java.util.Vector;

/**
 * A container class for all objects in the world
 */
public class World {
    public static Vector<Entity> objects;

    public World(){
        this.objects = new Vector<>();
        new Planet(0,0, 3);
        new SpaceShip(0, 2f);
    }

}
