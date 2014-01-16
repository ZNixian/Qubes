package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioNode;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import de.lessvoid.nifty.Nifty;
import java.util.ArrayList;
import java.util.Map;

public class Main extends SimpleApplication {

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }
    private Node shootables;
    private Node animationNode;
    private Node dirMarks;
    private ArrayList<Cube> cubes = new ArrayList<Cube>();
    private Input in;
    private Cube selected;
    private Geometry mark;
    private Vector3f mark_pos;
    private boolean isInMenuState = true;
    private Nifty nifty;
    private Animator animator;
    private AudioNode audio_background;
    private String currentLevel;
    private float levelTimer;

    @Override
    public void simpleInitApp() {
        loadInitApp();

        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(
                assetManager, inputManager, audioRenderer, guiViewPort);
        nifty = niftyDisplay.getNifty();
        guiViewPort.addProcessor(niftyDisplay);
        reloadLevels();
    }

    public void loadInitApp() {
        initCrossHairs(); // a "+" in the middle of the screen to help aiming
        initKeys();       // load custom key mappings
        initMark();       // a red sphere to mark the hit
        initAudio();

        /**
         * create four colored boxes and a floor to shoot at:
         */
//        cubes.add(Cube.makeCube("a Dragon", -2f, 0f, 1f, assetManager, shootables, PlayerCube.class));
//        cubes.add(Cube.makeCube("a Dragon", -2f, 3f, 1f, assetManager, shootables, PlayerCube.class));
        flyCam.setEnabled(false);
        in = new Input(cam, inputManager, new GUI(guiNode, assetManager, settings, this));

        assetManager.registerLoader(LevelLoader.class, "level");

        animator = new Animator();


    }

    public void loadLevel(String name) {
        clearSence();
//        rootNode.attachChild(makeFloor());
        Map<Vector3f, Class<? extends Cube>> list =
                (Map<Vector3f, Class<? extends Cube>>) assetManager.loadAsset("Levels/" + name + ".level");
        for (Vector3f tobecube : list.keySet()) {
            cubes.add(Cube.makeCube(tobecube, assetManager, shootables, rootNode, list.get(tobecube)));
        }
        isInMenuState = false;
        nifty.gotoScreen("InGame"); // start the screen
        currentLevel = name;
        levelTimer = 0;
    }

    public void clearSence() {
        rootNode.detachAllChildren();
        shootables = new Node("Shootables");
        animationNode = new Node("animationNode");
        rootNode.attachChild(shootables);
        rootNode.attachChild(animationNode);
        cubes.clear();
        removeMark();
    }

    /**
     * Declaring the "Shoot" action and mapping to its triggers.
     */
    private void initKeys() {
        inputManager.addMapping("Shoot", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT)); // trigger 2: left-button click
        inputManager.addListener(actionListener, "Shoot");
    }
    /**
     * Defining the "Shoot" action: Determine what was hit and how to respond.
     */
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (isInMenuState) {
                return;
            }
            if (name.equals("Shoot") && !keyPressed) {
                // 1. Reset results list.
                CollisionResults results = new CollisionResults();
                Vector2f click2d = inputManager.getCursorPosition();
                Vector3f click3d = cam.getWorldCoordinates(
                        new Vector2f(click2d.x, click2d.y), 0f).clone();
                Vector3f dir = cam.getWorldCoordinates(
                        new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();
                Ray ray = new Ray(click3d, dir);
                shootables.collideWith(ray, results);
                // 4. Print the results
//        System.out.println("----- Collisions? " + results.size() + "-----");
//        for (int i = 0; i < results.size(); i++) {
//          // For each hit, we know distance, impact point, name of geometry.
//          float dist = results.getCollision(i).getDistance();
//          Vector3f pt = results.getCollision(i).getContactPoint();
//          String hit = results.getCollision(i).getGeometry().getName();
//          System.out.println("* Collision #" + i);
//          System.out.println("  You shot " + hit + " at " + pt + ", " + dist + " wu away.");
//        }
                // 5. Use the results (we mark the hit object)
                if (getSelected() != null) {
                    getSelected().getGeom().getMaterial().setColor("Color", ColorRGBA.Blue);//.setLocalScale(1);
                    deselect();
                }
                if (results.size() > 0) {
                    // The closest collision point is what was truly hit:
                    CollisionResult closest = results.getClosestCollision();
                    Geometry geom1 = closest.getGeometry();
                    Vector3f vec = geom1.getLocalTranslation();
                    Cube cube = Cube.cubeAt(vec, cubes);
                    if (cube != null) {
                        Geometry geom = cube.getGeom();
//                        geom.setLocalTranslation(vec.add(0, 1, 0));
                        setSelected(cube);
                        geom.getMaterial().setColor("Color", ColorRGBA.Orange);//.setLocalScale(2);
                    }
                    // Let's interact - we mark the hit with a red dot.
                } else {
                    // No hits? Then remove the red mark.
                }

            }
        }
    };

    /**
     * A floor to show that the "shot" can go through several objects.
     */
//    protected Geometry makeFloor() {
//        Box box = new Box(15, .2f, 15);
//        Geometry floor = new Geometry("the Floor", box);
//        floor.setLocalTranslation(0, -4, -5);
//        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//        mat1.setColor("Color", ColorRGBA.Gray);
//        floor.setMaterial(mat1);
//        return floor;
//    }
    /**
     * A red ball that marks the last spot that was "hit" by the "shot".
     */
    protected void initMark() {
        Sphere sphere = new Sphere(30, 30, 0.2f);
        mark = new Geometry("BOOM!", sphere);
        Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat.setColor("Color", ColorRGBA.Red);
        mark.setMaterial(mark_mat);
    }

    protected Geometry createBall(ColorRGBA colour, Vector3f pos) {
        Sphere sphere = new Sphere(30, 30, 0.15f);
        Geometry ball = new Geometry("", sphere);
        Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat.setColor("Color", colour);
        ball.setMaterial(mark_mat);
        ball.setLocalTranslation(pos);
        return ball;
    }

    /**
     * A centred plus sign to help the player aim.
     */
    protected void initCrossHairs() {
        setDisplayStatView(false);
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
//        BitmapText ch = new BitmapText(guiFont, false);
//        ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
//        ch.setText("+"); // crosshairs
//        ch.setLocalTranslation( // center
//                settings.getWidth() / 2 - ch.getLineWidth() / 2, settings.getHeight() / 2 + ch.getLineHeight() / 2, 0);
//        guiNode.attachChild(ch);
    }

    public Cube getSelected() {
        return selected;
    }

    public void setSelected(Cube selected) {
        if (selected == null || selected.getPos() == null) {
            deselect();
        }
        this.selected = selected;

        if (dirMarks != null) {
            dirMarks.removeFromParent();
        }
        dirMarks = new Node("dirMarks");
        rootNode.attachChild(dirMarks);

        dirMarks.attachChild(createBall(ColorRGBA.Green, Vector3f.UNIT_X));
        dirMarks.attachChild(createBall(ColorRGBA.Blue, Vector3f.UNIT_Y));
        dirMarks.attachChild(createBall(ColorRGBA.Orange, Vector3f.UNIT_Z));

        dirMarks.attachChild(createBall(ColorRGBA.Magenta, Vector3f.UNIT_X.negate()));
        dirMarks.attachChild(createBall(ColorRGBA.Yellow, Vector3f.UNIT_Y.negate()));
        dirMarks.attachChild(createBall(ColorRGBA.Red, Vector3f.UNIT_Z.negate()));

        recalcSelDirMarks();
    }

    public void recalcSelDirMarks() {
        if (dirMarks != null && selected != null && selected.getPos() != null) {
            dirMarks.setLocalTranslation(selected.getPos());
        } else if (dirMarks != null) {
            dirMarks.removeFromParent();
            dirMarks = null;
        }
    }

    public Geometry getMark() {
        return mark;
    }

    public void addMark(Vector3f pos) {
        if (pos.equals(mark_pos)) {
            return;
        }
        mark.setLocalTranslation(pos);
        rootNode.attachChild(mark);
    }

    public void removeMark() {
        rootNode.detachChild(mark);
        mark_pos = null;
    }

    public void deselect() {
        selected = null;
        if (dirMarks != null) {
            dirMarks.removeFromParent();
        }
        dirMarks = null;
    }

    public ArrayList<Cube> getCubes() {
        return cubes;
    }

    public void goToLevelMenu() {
        nifty.gotoScreen("Menu"); // start the screen
        clearSence();
    }

    public Animator getAnimator() {
        return animator;
    }

    public Node getAnimationNode() {
        return animationNode;
    }

    @Override
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf); //To change body of generated methods, choose Tools | Templates.
        animator.onTick(tpf);
        levelTimer += tpf;
    }

    private void initAudio() {
        audio_background = new AudioNode(assetManager, "Sounds/tinydroplets.ogg", false);
        audio_background.setLooping(true);  // activate continuous playing
        audio_background.setPositional(false);
        audio_background.setVolume(5);
        rootNode.attachChild(audio_background);
        audio_background.play(); // play continuously!
    }

    public String getCurrentLevel() {
        return currentLevel;
    }

    public float getLevelTimer() {
        return levelTimer;
    }

    public void reloadLevels() {
        LevelScreen.initScreen(nifty, this);
    }
}
