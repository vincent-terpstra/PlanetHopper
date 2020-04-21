package com.library;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;

public final class AtlasReader {

    private final HashMap<String, float[]> regions;

    public AtlasReader(String atlas){
        regions = new HashMap<>();
        FileHandle file = Gdx.files.internal("images/" + atlas + ".atlas");
        Texture texture = getTexture(atlas);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.read()))){
            String name;
            while((name = reader.readLine()) != null) {
                String tuple = reader.readLine();
                float[] a = {0,0,0,0};
                int colon = tuple.indexOf(":") + 2;

                for(int i = 0; i < 4; i++){
                    int comma = tuple.indexOf(",", colon);
                    a[i] = Integer.parseInt(tuple.substring(colon,  comma)) / 1024f; //(resize to texture size)
                    colon = comma + 2;
                }
                a[2] += a[0];
                a[3] += a[1];

                regions.put(name, a);
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Texture getTexture(String atlas){
        return new Texture(Gdx.files.internal("images/" + atlas + ".png"));
    }

    public float[] getRegion(String region){
        return regions.get(region);
    }
}
