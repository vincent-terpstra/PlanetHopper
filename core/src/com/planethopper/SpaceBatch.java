package com.planethopper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.library.AtlasReader;
import com.library.MyBatch;
import com.library.PointXY;
import com.objects.World;

public class SpaceBatch extends MyBatch {
    public float[] planet, spaceShip, star;

    public SpaceBatch(AtlasReader reader){
        super(100, null);

        planet = reader.getRegion("planet");
        spaceShip = reader.getRegion("spaceship");
        star = reader.getRegion("star");

    }

    @Override
    public void begin(Texture texture){

        float height = 10f;
        setSize(height * Gdx.graphics.getWidth() / Gdx.graphics.getHeight(), height, World.pilot.x(), World.pilot.y());
        super.begin(texture);
    }

    public void draw(float[] region, PointXY loc, PointXY angle, float radius){
        draw(region, loc.x(), loc.y(), radius, radius, angle.x(), angle.y());
    }
}
