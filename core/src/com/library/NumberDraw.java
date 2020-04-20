package com.library;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class NumberDraw {
    TextureRegion[] numbers;

    public NumberDraw(AtlasReader reader){
        numbers = new TextureRegion[10];

        for(int i = 0; i < 10; i++){
            numbers[i] = reader.getRegion("" + i);
        }
    }

    public void drawNumber(MyBatch batch, int val, float x, float y){
        batch.draw(numbers[val], x, y, .3f, .5f);
    }

}
