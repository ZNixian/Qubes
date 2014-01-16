/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import mygame.cubes.PlayerCube;
import mygame.cubes.TargetCube;
import mygame.cubes.TargetCubeSolved;
import mygame.cubes.WoodCube;

/**
 *
 * @author Campbell Suter
 */
public abstract class Cube {

    private final Geometry geom;
    private AssetManager assetManager;
    private Node shootnode;
    private Node rootnode;

    public Cube(Geometry geom) {
        this.geom = geom;
    }

    public void setData(AssetManager assetManager, Node shootnode,
            Node rootnode) {
        this.assetManager = assetManager;
        this.rootnode = rootnode;
        this.shootnode = shootnode;
    }

    public Geometry getGeom() {
        return geom;
    }

    public Vector3f getPos() {
        return geom.getLocalTranslation();
    }

    public static Cube makeCube(Vector3f vec, AssetManager assetManager, Node shootnode,
            Node rootnode, Class<? extends Cube> target) {
        Box box = new Box(0.5f, 0.5f, 0.5f);
        Geometry cube = new Geometry("Cube", box);
        cube.setLocalTranslation(vec);
        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.Blue);
        cube.setMaterial(mat1);
        if (target.getAnnotation(Selectible.class) == null) {
            rootnode.attachChild(cube);
        } else {
            shootnode.attachChild(cube);
        }
        try {
            Cube cu = target.getConstructor(Geometry.class).newInstance(cube);//new Cube(cube);
            cu.setData(assetManager, shootnode, rootnode);
            return cu;
        } catch (InstantiationException ex) {
            Logger.getLogger(Cube.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Cube.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Cube.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(Cube.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(Cube.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(Cube.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static Cube cubeAt(Vector3f vec, List<Cube> cubes) {
        for (Cube cube : cubes) {
            if (cube.getPos().equals(vec)) {
                return cube;
            }
        }
        return null;
    }

    public void moveToNoClip(Vector3f pos, Main main) {
        geom.setLocalTranslation(pos);
        main.recalcSelDirMarks();
    }

    public boolean moveTo(Vector3f pos, List<Cube> cubes, Main main, Vector3f dir) {
        Cube cu = cubeAt(pos, cubes);
        if (cu == null) {
            moveToNoClip(pos, main);
            return true;
        }
        if (cu.getClass().equals(TargetCube.class) && getClass().equals(PlayerCube.class)) {

            cu.getGeom().removeFromParent();
            cubes.remove(cu);
            //////////////////////////
            geom.removeFromParent();
            cubes.remove(this);
            //////////////////////////
            cu = makeCube(pos, assetManager, shootnode, rootnode, TargetCubeSolved.class);
            cubes.add(cu);
            //////////////////////////
            main.setSelected(null);
        }

        if (cu.getClass().equals(WoodCube.class)) {

            cu.moveInDir(cubes, dir, main);
        }
        return false;
    }

    public void moveInDir(List<Cube> cubes, final Vector3f dir, final Main main) {
        Vector3f pos = getPos().add(dir);
        int i = 0;
        while (moveTo(pos, cubes, main, dir)) {

            { //////// Animation code
                Box box = new Box(0.5f, 0.5f, 0.5f);
                final Geometry cube = new Geometry("Cube", box);
                cube.setLocalTranslation(pos);
                Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                mat1.setColor("Color", ColorRGBA.White);
                cube.setMaterial(mat1);
                main.getAnimationNode().attachChild(cube);
                main.getAnimator().runAfter(i / 4f, new Runnable() {
                    public void run() {
                        main.getAnimator().moveItem(cube, Vector3f.ZERO, new Vector3f(-1f, -1f, -1f));
                        main.getAnimator().runAfter(1, new Runnable() {
                            public void run() {
                                main.getAnimator().haltMovement(cube);
                                cube.removeFromParent();
                            }
                        });
                    }
                });
            }

            pos.addLocal(dir);
            if (i++ > 50) {
                geom.removeFromParent();
                cubes.remove(this);
                main.setSelected(null);
                return;
            }
        }
        int cubesLeft = 0;
        for (Cube cube : cubes) {
            if (cube.getClass().equals(TargetCube.class)) {
                cubesLeft++;
            }
        }
        if (cubesLeft == 0) {
            main.getAnimator().runAfter(i / 4f + 0.5f, new Runnable() {
                public void run() {
                    String level = main.getCurrentLevel();
                    String currentScore = IOSys.getInstance().readLineFromFile("qubes-saves/lvl-" + level);
                    int topscore = ((int)main.getLevelTimer());
                    if(currentScore!=null) {
                        topscore = Math.min(topscore, Integer.parseInt(currentScore));
                    }
                    IOSys.getInstance().saveToFile("qubes-saves/lvl-" + level, topscore + "");
                    main.reloadLevels();
                    main.goToLevelMenu();
                }
            });
        }
        main.setSelected(main.getSelected());
    }
}
