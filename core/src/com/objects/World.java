package com.objects;


import com.library.PointXY;

import java.util.Vector;

/**
 * A container class for all objects in the world
 */
public class World {
    public static Vector<Entity> objects;
    public static SpaceShip pilot;

    public World(){
        this.objects = new Vector<>();
        pilot = new SpaceShip(-5f, 0f);

        new Planet(0,0, 3);
        new Planet(4,4, 1);
        new Star(0, -2f);
    }

    static PointXY getGravity(){
        PointXY sum = new PointXY().set(0,0);
        for(Entity e : objects){
            if(e instanceof Planet){
                float distSqrd = e.dist(pilot);
                if(distSqrd > 0){
                    PointXY direction = new PointXY().set(e).move(pilot, -1);
                    float radius = direction.unit();

                    direction.scale(1f / radius / radius * ((Planet)e).mass);

                    sum.move(direction, 1);
                }

            }
        }
        return sum;
    }

}
