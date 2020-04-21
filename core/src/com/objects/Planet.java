package com.objects;

import com.library.PointXY;
import com.planethopper.SpaceBatch;

public class Planet extends Entity {
    public PointXY angle;

    final float radius;

    public Planet(float x, float y, float radius){
        super(x, y);

        this.radius = radius;
        this.angle = new PointXY().degrees(45f);
    }

    public void draw(SpaceBatch batch){
        batch.draw(batch.planet, this, angle, radius);
    }

    public boolean update(float delta){
        angle.rotate(new PointXY().radians(delta));

        return false;
    }

}
