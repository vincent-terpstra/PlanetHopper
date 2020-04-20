package com.planethopper;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.library.AtlasReader;
import com.library.NumberDraw;
import com.library.PointXY;
import com.objects.Planet;
import com.objects.SpaceShip;

public class PlanetHopperGame extends ApplicationAdapter {

	NumberDraw numbers;
	SpaceBatch batch;

	Planet planet;
	SpaceShip ship;

	@Override
	public void create () {
		AtlasReader reader = new AtlasReader("images");
		numbers = new NumberDraw(reader);

		batch = new SpaceBatch(reader);

		planet = new Planet(0,0, 3);
		ship	= new SpaceShip(0, 2f);

		Gdx.gl.glClearColor(0,0.1f,0.1f,1);
	}

	@Override
	public void pause(){ }

	@Override
	public void resume(){ }


	@Override
	public void resize(int width, int height){
		final float mapHeight = 10f;

		batch.setSize(mapHeight * width / height, mapHeight);
	}


	@Override
	public void render () {
		float delta = Gdx.graphics.getDeltaTime();
		PointXY angle = planet.angle;
		angle.rotate(new PointXY().radians(delta));
		ship.angle.set(angle);
		ship.set(angle.y(), angle.x()).scale(2f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();

		//TODO
		planet.draw(batch);
		ship.draw(batch);

		batch.end();
	}


	@Override
	public void dispose () {
		batch.dispose();
	}
}
