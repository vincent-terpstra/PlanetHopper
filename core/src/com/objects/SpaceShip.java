package com.objects;

import com.library.PointXY;
import com.planethopper.SpaceBatch;

public class SpaceShip extends PointXY {
    public SpaceShip(float x, float y){
        set(x, y);
    }
    public final PointXY angle = new PointXY().set(1,0);
    public void draw(SpaceBatch batch){
        batch.draw(batch.spaceShip, x, y, .8f, 1f, angle.x(), angle.y());
    }

}
