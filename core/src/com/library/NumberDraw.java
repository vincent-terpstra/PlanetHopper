package com.library;

public class NumberDraw {
    float[][] numbers;

    public NumberDraw(AtlasReader reader){
        numbers = new float[10][];

        for(int i = 0; i < 10; i++){
            numbers[i] = reader.getRegion("" + i);
        }
    }

    public void drawNumber(MyBatch batch, int val, float x, float y){
        batch.draw(numbers[val], x, y, .3f, .5f);
    }

}
