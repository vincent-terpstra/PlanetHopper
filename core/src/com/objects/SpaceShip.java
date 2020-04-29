package com.objects;

import com.library.PointXY;
import com.planethopper.SpaceBatch;

public class SpaceShip extends Entity {
    public SpaceShip(float x, float y, float vx, float vy){
        super(x, y);
        velocity = new PointXY().set(vx / 4, vy / 4);
    }
    public PointXY angle = new PointXY();
    public final PointXY velocity;

    public void draw(SpaceBatch batch){
        batch.draw(batch.spaceShip, x, y, .8f, 1f, angle.y(), angle.x());
    }

    public boolean update(float delta){
        World.updatePilot(this, velocity, angle, delta);

        return false;

    }

}
