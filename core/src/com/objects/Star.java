package com.objects;

import com.library.PointXY;
import com.planethopper.SpaceBatch;

public class Star extends Entity {
    Star(float x, float y){
        super(x, y);

        angle = new PointXY().set(1,0);
    }

    final PointXY angle;

    @Override
    public boolean update(float delta) {
        angle.rotate(new PointXY().radians(delta / 2));
        return dist(World.pilot) < .6f;
    }

    @Override
    public void draw(SpaceBatch batch) {
        batch.draw(batch.star, this, angle, .5f);
    }
}
