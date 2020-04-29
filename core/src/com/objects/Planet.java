package com.objects;

import com.library.PointXY;
import com.planethopper.SpaceBatch;

public class Planet extends Entity {
    public PointXY angle;
    final float rotation;
    final float mass;

    final float radius;

    public Planet(float x, float y, float radius, float rotation){
        super(x, y);
        this.mass = radius * radius * radius * 1.4f;
        this.radius = radius;
        this.angle = new PointXY().degrees(45f);
        this.rotation = rotation;
    }

    public void draw(SpaceBatch batch){
        batch.draw(batch.planet, this, angle, radius * 2 + .2f);
    }

    public boolean update(float delta){
        angle.rotate(new PointXY().radians(delta * rotation));

        return false;
    }

}
