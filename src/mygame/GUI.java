/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.jme3.ui.Picture;

/**
 *
 * @author Campbell Suter
 */
public class GUI {

    private AppSettings settings;
    private Main main;

    public GUI(Node guiNode, AssetManager assetManager, AppSettings settings, Main main) {
        this.main = main;
        this.settings = settings;
        Picture pic = new Picture("HUD Picture");
        pic.setImage(assetManager, "Textures/dpad.png", false);
//        pic.setWidth(128);
//        pic.setHeight(128);
        pic.setWidth(400);
        pic.setHeight(150);
        pic.setPosition(32, 32);
        guiNode.attachChild(pic);
    }

//    public boolean handleHover(Vector2f vec) {
//        if (main.getSelected() == null) {
//            return false;
//        }
//        vec.subtractLocal(32, 32);
//        if (vec.x > 128 || vec.y > 128 || vec.x < 0 || vec.y < 0) {
//            main.removeMark();
//            return false;
//        }
//        vec.divideLocal(4);
//        Vector3f pos = getNewPosForBlock(vec, main.getSelected().getPos());
//        if (pos != null) {
//            main.addMark(pos);
//        } else {
//            main.removeMark();
//        }
//        return true;
//    }
    public boolean handleHover(Vector2f vec) {
        if (main.getSelected() == null) {
            return false;
        }
        vec.subtractLocal(32, 32);
        if (vec.x > 400 || vec.y > 150 || vec.x < 0 || vec.y < 0) {
            main.removeMark();
            return false;
        }
        vec.divideLocal(5);
        Vector3f pos = getNewPosForBlock(vec, Vector3f.ZERO);
        return true;
    }

//    public boolean handleClick(Vector2f vec) {
//        if (main.getSelected() == null) {
//            return false;
//        }
//        vec.subtractLocal(32, 32);
//        if (vec.x > 128 || vec.y > 128 || vec.x < 0 || vec.y < 0) {
//            return false;
//        }
//        vec.divideLocal(4);
//        Vector3f pos = getNewPosForBlock(vec, Vector3f.ZERO);
//        if (pos != null) {
//            main.removeMark();
//            main.getSelected().moveInDir(main.getCubes(), pos, main);
//        }
//        return true;
//    }
    public boolean handleClick(Vector2f vec) {
        if (main.getSelected() == null) {
            return false;
        }
        vec.subtractLocal(32, 32);
        if (vec.x > 400 || vec.y > 150 || vec.x < 0 || vec.y < 0) {
            return false;
        }
        vec.divideLocal(5);
        Vector3f pos = getNewPosForBlock(vec, Vector3f.ZERO);
        if (pos != null) {
            main.removeMark();
            main.getSelected().moveInDir(main.getCubes(), pos, main);
        }
        return true;
    }

//    private Vector3f getNewPosForBlock(Vector2f vec, Vector3f orgpos) {
//        Vector3f pos = null;
//        if (vec.x >= 10 && vec.y >= 22 && vec.x <= 22 && vec.y <= 32) {
//            pos = orgpos.add(0, 1, 0);
//        } else if (vec.x >= 10 && vec.y >= 0 && vec.x <= 22 && vec.y <= 10) {
//            pos = orgpos.subtract(0, 1, 0);
//        } else if (vec.x >= 22 && vec.y >= 10 && vec.x <= 32 && vec.y <= 22) {
//            pos = orgpos.add(1, 0, 0);
//        } else if (vec.x >= 0 && vec.y >= 10 && vec.x <= 10 && vec.y <= 22) {
//            pos = orgpos.subtract(1, 0, 0);
//        }
//        return pos;
//    }
    private Vector3f getNewPosForBlock(Vector2f vec, Vector3f orgpos) {
        Vector3f pos = null;
        Vector3f[] poses = {
            Vector3f.UNIT_X,
            Vector3f.UNIT_Y,
            Vector3f.UNIT_Z,
            Vector3f.UNIT_X.negate(),
            Vector3f.UNIT_Y.negate(),
            Vector3f.UNIT_Z.negate(),};

        vec = vec.subtractLocal(10, 10);
        if (vec.x >= 0 && vec.y >= 0 && vec.x < 60 && vec.y < 10) {
            vec = vec.divideLocal(10);
//            System.out.println(vec.x);
            pos = orgpos.add(poses[(int) vec.x]);
        }
        return pos;
    }
}
