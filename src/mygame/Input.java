/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

/**
 *
 * @author Campbell Suter
 */
public class Input implements AnalogListener, ActionListener {

    private final Camera cam;
    private float vtilt;
    private float htilt;
    private float zoom = 2;
    private boolean isMouseDown;
    private final InputManager inputManager;
    private final GUI gui;
    private final Main main;

    public Input(Camera cam, InputManager inputManager, GUI gui, Main main) {
        this.cam = cam;
        this.gui = gui;
        this.inputManager = inputManager;
        this.main = main;

        String[] mappings = new String[]{
            "FLYCAM_Left",
            "FLYCAM_Right",
            "FLYCAM_Up",
            "FLYCAM_Down",
            "FLYCAM_ZoomIn",
            "FLYCAM_ZoomOut",};

        inputManager.addListener(this, mappings);
        inputManager.addMapping("Mouse", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(this, "Mouse");
    }

    public void onAnalog(String name, float value, float tpf) {
        if (isMouseDown) {
            if (name.equals("FLYCAM_Left")) {
                htilt -= value;
            } else if (name.equals("FLYCAM_Right")) {
                htilt += value;
            } else if (name.equals("FLYCAM_Up")) {
                vtilt -= value;
            } else if (name.equals("FLYCAM_Down")) {
                vtilt += value;
            }
        }
        if (name.equals("FLYCAM_ZoomIn")) {
            zoom -= value / 2;
        } else if (name.equals("FLYCAM_ZoomOut")) {
            zoom += value / 2;
        }
        vtilt = Math.min(vtilt, (float) Math.PI / 2 - 0.05f);
        vtilt = Math.max(vtilt, -((float) Math.PI / 2 - 0.05f));

        zoom = Math.min(zoom, 50);
        zoom = Math.max(zoom, 0.5f);

        float vtilts = (float) Math.cos(vtilt);
        Vector3f pos = new Vector3f((float) Math.cos(htilt) * vtilts * zoom,
                (float) Math.sin(vtilt) * zoom,
                (float) Math.sin(htilt) * vtilts * zoom);
        pos.multLocal(10);
        Vector3f lookPos = Vector3f.ZERO;
        if (main.getSelected() != null) {
            lookPos = main.getSelected().getPos();
        }
        cam.setLocation(pos.add(lookPos));
        cam.lookAt(lookPos, Vector3f.UNIT_Y);

        gui.handleHover(inputManager.getCursorPosition());
    }

    public void onAction(String name, boolean mousePressed, float tpf) {
        if (name.equals("Mouse")) {
            if (!mousePressed || !gui.handleClick(inputManager.getCursorPosition())) {
                isMouseDown = mousePressed;
                main.actionListener.onAction(name, isMouseDown, 0);
            }
        }
    }
}
