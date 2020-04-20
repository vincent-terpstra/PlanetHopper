package com.planethopper;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.library.AtlasReader;
import com.library.NumberDraw;
import com.objects.Entity;
import com.objects.World;

public class PlanetHopperGame extends ApplicationAdapter {

	NumberDraw numbers;
	SpaceBatch batch;

	World world;

	@Override
	public void create () {
		world = new World();

		AtlasReader reader = new AtlasReader("images");
		numbers = new NumberDraw(reader);

		batch = new SpaceBatch(reader);

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
		for(Entity e : World.objects){
			e.update(delta);
		}

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();

		for(Entity e: World.objects){
			e.draw(batch);
		}

		batch.end();
	}


	@Override
	public void dispose () {
		batch.dispose();
	}
}
