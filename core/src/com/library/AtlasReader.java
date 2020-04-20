package com.library;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;

public final class AtlasReader {
    private final HashMap<String, TextureRegion> regions;

    public AtlasReader(String atlas){
        regions = new HashMap<>();
        Texture texture = new Texture(Gdx.files.internal("images/" + atlas + ".png"));
        FileHandle file = Gdx.files.internal("images/" + atlas + ".atlas");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.read()))){
            String name;
            while((name = reader.readLine()) != null) {
                String tuple = reader.readLine();
                int[] a = {0,0,0,0};
                int colon = tuple.indexOf(":") + 2;

                for(int i = 0; i < 4; i++){
                    int comma = tuple.indexOf(",", colon);
                    a[i] = Integer.parseInt(tuple.substring(colon,  comma));
                    colon = comma + 2;
                }
                regions.put(name, new TextureRegion(texture, a[0], a[1], a[2], a[3]));
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public TextureRegion getRegion(String region){
        return regions.get(region);
    }
}
