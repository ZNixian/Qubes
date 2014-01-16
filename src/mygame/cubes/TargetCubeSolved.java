/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.cubes;

import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import mygame.Cube;

/**
 *
 * @author Campbell Suter
 */
public class TargetCubeSolved extends Cube {

    public TargetCubeSolved(Geometry geom) {
        super(geom);
        geom.getMaterial().setColor("Color", ColorRGBA.Magenta);
    }
}
