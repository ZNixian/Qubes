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
public class TargetCube extends Cube {

    public TargetCube(Geometry geom) {
        super(geom);
        geom.getMaterial().setColor("Color", ColorRGBA.Pink);
    }
}
