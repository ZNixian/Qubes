/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.controls.button.builder.ButtonBuilder;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 *
 * @author Campbell Suter
 */
public class LevelScreen implements ScreenController {

    private final Main main;
    private final Nifty nifty;

    public static void initScreen(final Nifty nifty, final Main main) {

        nifty.loadStyleFile("nifty-default-styles.xml");
        nifty.loadControlFile("nifty-default-controls.xml");

        // <screen>
        nifty.removeScreen("Menu");
        nifty.addScreen("Menu", new ScreenBuilder("Menu Screen") {
            {
                controller(new LevelScreen(main, nifty)); // Screen properties       

                // <layer>
                layer(new LayerBuilder("MainLayer") {
                    {
                        childLayoutVertical(); // layer properties, add more...

                        // <panel>
                        for (int i = 0; i < LevelLoader.levels.length; i++) {
                            final String level = LevelLoader.levels[i];
                            panel(new PanelBuilder("Level" + level) {
                                {
                                    childLayoutCenter(); // panel properties, add more...


                                    // GUI elements
                                    String levelScore = IOSys.getInstance().readLineFromFile("qubes-saves/lvl-" + level);

                                    String levelStatus = levelScore == null ? (isLevelUnLocked(level) ? "Unlocked" : "Locked") : levelScore + "sec";

                                    control(new ButtonBuilder("LevelSel" + level, level + " (" + levelStatus + ")") {
                                        {
                                            alignCenter();
                                            valignCenter();
                                            height("50px");
                                            width("15%");
                                            visibleToMouse(true);
                                            interactOnClick("onClick(" + level + ")");
                                        }
                                    });

                                }
                            });
                            // </panel>
                        }
                    }
                });
                // </layer>
            }
        }.build(nifty));
        // </screen>


        nifty.addScreen("InGame", new ScreenBuilder("In-Game Screen") {
            {
                controller(new LevelScreen(main, nifty)); // Screen properties       

                // <layer>
                layer(new LayerBuilder("InGame") {
                    {
                        childLayoutVertical(); // layer properties, add more...

                        // <panel>
                        panel(new PanelBuilder("Close") {
                            {
                                childLayoutCenter();

                                // GUI elements
                                control(new ButtonBuilder("CloseSel", "Close") {
                                    {
                                        alignRight();
                                        valignTop();
                                        height("50px");
                                        width("50px");
                                        visibleToMouse(true);
                                        interactOnClick("close()");
                                    }
                                });

                            }
                        });
                        // </panel>
                    }
                });
                // </layer>
            }
        }.build(nifty));
        // </screen>

        nifty.gotoScreen("Menu"); // start the screen
    }

    public LevelScreen(Main main, Nifty nifty) {
        this.main = main;
        this.nifty = nifty;
    }

    public void bind(Nifty nifty, Screen screen) {
    }

    public void onStartScreen() {
    }

    public void onEndScreen() {
    }

    public void onClick(String level) {
        if (isLevelUnLocked(level)) {
            main.loadLevel(level);
        }
    }

    public void close() {
        main.goToLevelMenu();
    }

    public static boolean isLevelUnLocked(String level) {
        String prevLevel = null;
        for (int i = 1; i < LevelLoader.levels.length; i++) {
            if (LevelLoader.levels[i].equals(level)) {
                prevLevel = LevelLoader.levels[i - 1];
            }
        }
        if (prevLevel != null) {
            String currentScore = IOSys.getInstance().readLineFromFile("qubes-saves/lvl-" + prevLevel);
            if (currentScore == null) {
                return false;
            }
        }
        return true;
    }
}
