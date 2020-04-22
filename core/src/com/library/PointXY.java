package com.library;

public class PointXY {
    protected float x, y;

    public float x(){
        return x;
    }

    public float y(){
        return y;
    }
    public PointXY set(PointXY point){
        return set(point.x(), point.y());
    }
    public PointXY set(float x, float y){
        this.x = x;
        this.y = y;

        return this;
    }

    public PointXY rotate(float cos, float sin){
        return set(x * cos - y * sin, y * cos + x * sin);
    }

    public PointXY rotate(PointXY angle){
        return rotate(angle.x, angle.y);
    }

    public PointXY radians(float rads){
        return set((float)Math.cos(rads), (float)Math.sin(rads));
    }

    public PointXY degrees(float degs){
        return radians(degs * (float)Math.PI / 180f);
    }

    public PointXY move(PointXY velocity, float delta){
        x += velocity.x * delta;
        y += velocity.y * delta;

        return this;
    }

    public PointXY scale(float delta){
        x *= delta;
        y *= delta;

        return this;
    }

    public float dist(PointXY target){
        float x = this.x - target.x;
        float y = this.y - target.y;

        return x * x + y * y;
    }

    public float unit(){
        float dist = x * x + y * y;
        if(dist > 0){
            dist = (float)Math.sqrt(dist);
            scale( 1f/ dist);
            return dist;
        }
        return 1;
    }

}
