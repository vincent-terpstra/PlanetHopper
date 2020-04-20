package com.objects;

import com.library.PointXY;
import com.planethopper.SpaceBatch;

/**
 *  A parent class for all objects in the World
 */

public abstract class Entity extends PointXY {
    public Entity(float x, float y){
        set(x, y);

        World.objects.add(this);
    }


    /**
     * updates the object
     * @param delta timestep
     */
    public abstract void update(float delta);

    /**
     * draws the object using the batch
     * @param batch
     */
    public abstract void draw(SpaceBatch batch);


}
