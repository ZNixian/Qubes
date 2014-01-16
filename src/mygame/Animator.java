/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Campbell Suter
 */
public class Animator {

    private Map<Geometry, AnimatedItem> items = new HashMap<Geometry, AnimatedItem>();
    private ArrayList<RunItem> toRun = new ArrayList<RunItem>();

    private static class AnimatedItem {

        public Vector3f movement = new Vector3f(0, 0, 0);
        public Vector3f scale = new Vector3f(0, 0, 0);
        public Geometry geom;

        public void addMovement(Vector3f movement) {
            this.movement.addLocal(movement);
        }
        
        public void addScale(Vector3f scale) {
            this.scale.addLocal(scale);
        }

        public void onTick(float dtime) {
            geom.setLocalTranslation(geom.getLocalTranslation().add(movement.mult(dtime)));
            geom.setLocalScale(geom.getLocalScale().add(scale.mult(dtime)));
        }
    }

    private static class RunItem {

        public Runnable run;
        public float time;

        public RunItem(Runnable run, float time) {
            this.run = run;
            this.time = time;
        }
    }

    public void moveItem(Geometry geom, Vector3f movement, Vector3f scale) {
        AnimatedItem item = items.get(geom);
        if (item == null) {
            item = new AnimatedItem();
            item.geom = geom;
            items.put(geom, item);
        }
        item.addMovement(movement);
        item.addScale(scale);
    }

    public void onTick(float dtime) {
        for (Geometry geometry : items.keySet()) {
            items.get(geometry).onTick(dtime);
        }

        int indexToRemove = -1;

        for (int i = 0; i < toRun.size(); i++) {
            RunItem runItem = toRun.get(i);
            runItem.time -= dtime;
            if (runItem.time < 0 && runItem.run != null) {
                runItem.run.run();
                runItem.run = null;
                indexToRemove = i;
            }
        }

        if (indexToRemove != -1) {
            toRun.remove(indexToRemove);
        }
    }

    public void haltMovement(Geometry geom) {
        items.remove(geom);
    }

    public void runAfter(float time, Runnable run) {
        toRun.add(new RunItem(run, time));
    }
}