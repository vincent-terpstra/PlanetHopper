package com.texturepacker;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.ScreenUtils;

public class IconProcessor {
	public static void processIcons(){
		Texture icon = new Texture("icon.png");
		Texture mask = new Texture("icon.png");
		SpriteBatch batch = new SpriteBatch();
		ShaderProgram.pedantic = false;
		ShaderProgram corners = new ShaderProgram(
				"attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
						+ "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
						+ "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
						+ "uniform mat4 u_projTrans;\n" //
						+ "varying vec4 v_color;\n" //
						+ "varying vec2 v_texCoords;\n" //
						+ "\n" //
						+ "void main()\n" //
						+ "{\n" //
						+ "   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
						+ "   v_color.a = v_color.a * (255.0/254.0);\n" //
						+ "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
						+ "   gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
						+ "}\n",

				"#ifdef GL_ES\n" //
						+ "#define LOWP lowp\n" //
						+ "precision mediump float;\n" //
						+ "#else\n" //
						+ "#define LOWP \n" //
						+ "#endif\n" //
						+ "varying LOWP vec4 v_color;\n" //
						+ "varying vec2 v_texCoords;\n" //

						+ "uniform sampler2D u_texture;\n" //
						+ "uniform sampler2D u_mask;\n" //

						+ "void main()\n"//
						+ "{\n" //
						+ "if(texture2D(u_mask, v_texCoords).a == 0.0) discard;"
						+ "  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n" //
						+ "}");
		if(corners.getLog().length() != 0){
			System.out.print(corners.getLog());
			return;
		}
		batch.setShader(corners);

		corners.setUniformi("u_mask", 1);

		Gdx.gl.glClearColor(0,0,0,0);

		saveTexture(corners, mask, icon, batch, 48, "mdpi");
		saveTexture(corners, mask, icon,  batch, 72, "hdpi");
		saveTexture(corners, mask, icon,  batch, 96, "xhdpi");
		saveTexture(corners, mask, icon, batch, 144, "xxhdpi");
		saveTexture(corners, mask, icon, batch, 192, "xxxhdpi");

	}

	private static void saveTexture(ShaderProgram corners, Texture mask, Texture icon, SpriteBatch batch, int width, String file){
		batch.getProjectionMatrix().setToOrtho2D(0,0, icon.getWidth(), icon.getWidth());
		FrameBuffer frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, width, width, false);
		frameBuffer.begin();
		batch.begin();

		mask.bind(1);
		corners.setUniformi("u_mask", 1);
		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.draw(icon, 0, icon.getHeight(), icon.getWidth(), -icon.getHeight());
		batch.end();
		Pixmap pixmap = ScreenUtils.getFrameBufferPixmap(0,0, width, width);
		frameBuffer.end();
		FileHandle fileHandle = new FileHandle("./android/res/drawable-" + file + "/ic_launcher.png");
		PixmapIO.writePNG(fileHandle, pixmap);
	}
}
