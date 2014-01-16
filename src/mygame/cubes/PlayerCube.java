/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.cubes;

import com.jme3.scene.Geometry;
import mygame.Cube;
import mygame.Selectible;

/**
 *
 * @author Campbell Suter
 */
@Selectible
public class PlayerCube extends Cube {

    public PlayerCube(Geometry geom) {
        super(geom);
    }
}
