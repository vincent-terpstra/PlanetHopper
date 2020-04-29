package com.objects;


import com.badlogic.gdx.Gdx;
import com.library.PointXY;

import java.util.Vector;

/**
 * A container class for all objects in the world
 */
public class World {
    public static Vector<Entity> objects;
    public static SpaceShip pilot;

    public World(){
        this.objects = new Vector<>();
        pilot = new SpaceShip(-5f, 0f, 0, -3.5f);
        new SpaceShip(5f, 0f, 0, -3.5f);
        new SpaceShip(0f, -5f, -3.5f, 0);
        new SpaceShip(0f, 5f, -3.5f, 0);

        new Planet(0,0, 1.5f, .5f);
        new Planet(4,4, .5f, 1);
        new Planet(-5,-5, 1f, -.75f);
        new Star(0, -2f);
    }

    static void updatePilot(SpaceShip ship, PointXY velocity, PointXY angle, float delta){
        PointXY sum = new PointXY().set(0,0);
        for(Entity e : objects){
            if(e instanceof Planet){
                float distSqrd = e.dist(ship);
                if(distSqrd > 0){
                    PointXY direction = new PointXY().set(e).move(ship, -1);
                    float radius = direction.unit();

                    direction.scale(((Planet)e).mass / radius / radius );

                    sum.move(direction, 1);
                }

            }
        }
        velocity.move(sum, delta);
        angle.set(velocity).unit();

        float inf = 0;
        float iRad = 0;

        float radians = angle.angle();

        for(Entity e: objects) {
            if (e instanceof Planet) {
                Planet planet = (Planet) e;
                PointXY direction = new PointXY().set(ship).move(planet, -1);
                float distance = direction.unit() - planet.radius - .5f;
                //ship is in range of plant (rotate towards landing angle)
                float influence = Math.min(1, 1 - distance / RANGE);
                float r = 0;

                if(influence == 1){
                    angle.set(direction).rotate(delta * planet.rotation);
                    ship.set(angle).scale(planet.radius + .4998f).move(planet, 1);
                    velocity.set(100,100);
                    if(Gdx.input.isTouched()){
                        float multi = planet.rotation / (planet.radius + .5f);
                        velocity.set(angle.y() * multi, - angle.x() * multi);
                        velocity.move(angle,  1.4f * delta);
                        ship.move(velocity, delta);
                    }

                    return;
                } else if (influence > inf) {
                    float tmp = inf;
                    inf = influence;
                    influence = tmp;

                    r = iRad;
                    iRad = direction.angle();
                } else if(influence > 0){
                    r = direction.angle();
                }

                if(influence > 0){
                    if(Math.abs(r - radians) > Math.PI)
                        r += (radians < 0 ? -2 : 2) * Math.PI;

                    radians = radians * (1 -  influence) + r * influence;
                }
            }
        }
        radians = radians * (1 - inf) + iRad * inf;
        angle.radians(radians);

        if(Gdx.input.isTouched()){
            velocity.move(angle,  1.4f * delta);
        }
        ship.move(velocity, delta);
    }

    static final float RANGE = 1.5f;
}
