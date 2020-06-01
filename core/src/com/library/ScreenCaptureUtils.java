package com.library;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Date;

public class ScreenCaptureUtils {
    public static void capture(int key){
        capture(key, "screenshot");
    }

    public static void capture(int key, String title){
        if(Gdx.input.isKeyJustPressed(key)){

            byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), true);

            // This loop makes sure the whole screenshot is opaque and looks exactly like what the user is seeing
            for(int i = 4; i < pixels.length; i+=4){
                pixels[i-1] = (byte)-1;
            }

            Pixmap pixmap = new Pixmap(Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), Pixmap.Format.RGBA8888);
            BufferUtils.copy(pixels, 0, pixmap.getPixels(), pixels.length);

            PixmapIO.writePNG(new FileHandle("./core/screenshots/"+ title + new Date().getTime() +".png"), pixmap);
            pixmap.dispose();
        }
    }
}
