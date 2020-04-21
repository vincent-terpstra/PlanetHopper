package com.planethopper;

import com.library.AtlasReader;
import com.library.MyBatch;
import com.library.PointXY;

public class SpaceBatch extends MyBatch {
    public float[] planet, spaceShip, star;

    public SpaceBatch(AtlasReader reader){
        super(100, null);

        planet = reader.getRegion("planet");
        spaceShip = reader.getRegion("spaceship");
        star = reader.getRegion("star");

    }

    public void draw(float[] region, PointXY loc, PointXY angle, float radius){
        draw(region, loc.x(), loc.y(), radius, radius, angle.x(), angle.y());
    }
}
