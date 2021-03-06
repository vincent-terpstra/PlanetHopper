package com.planethopper;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.library.AtlasReader;
import com.library.NumberDraw;
import com.library.ScreenCaptureUtils;
import com.objects.Entity;
import com.objects.World;

import java.util.Date;
import java.util.Vector;

public class PlanetHopperGame extends ApplicationAdapter {

	NumberDraw numbers;
	SpaceBatch batch;

	World world;
	Texture texture;

	@Override
	public void create () {
		world = new World();

		AtlasReader reader = new AtlasReader("images");
		texture = AtlasReader.getTexture("images");
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
	}


	@Override
	public void render () {
		Vector<Entity> objects = World.objects;

		float delta = Gdx.graphics.getDeltaTime();
		for(int i = 0; i < objects.size();){
			if(objects.get(i).update(delta))
				objects.remove(i);
			else
				i++;
		}
		for(Entity e : World.objects){
			e.update(delta);
		}

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin(texture);

		for(Entity e: World.objects){
			e.draw(batch);
		}

		batch.end();

		ScreenCaptureUtils.capture(Input.Keys.A);
	}


	@Override
	public void dispose () {
		batch.dispose();
	}
}
