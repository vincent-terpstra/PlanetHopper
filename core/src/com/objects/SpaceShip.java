package com.objects;

import com.library.PointXY;
import com.planethopper.SpaceBatch;

public class SpaceShip extends Entity {
    public SpaceShip(float x, float y){
        super(x, y);
    }
    public PointXY angle = new PointXY();
    public final PointXY velocity = new PointXY().set(0, -4.5f);

    public void draw(SpaceBatch batch){
        angle.set(velocity).unit();
        batch.draw(batch.spaceShip, x, y, .8f, 1f, angle.y(), angle.x());
    }

    public boolean update(float delta){
        velocity.move(World.getGravity(), delta);
        move(velocity, delta);

        return false;

    }

}
