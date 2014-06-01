/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rtlcc;

import com.jogamp.newt.event.KeyEvent;
import java.awt.Color;
import java.util.Arrays;
import processing.core.PApplet;
import static processing.core.PConstants.DOWN;
import static processing.core.PConstants.LEFT;
import static processing.core.PConstants.RIGHT;
import static processing.core.PConstants.UP;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PGraphics3D;
import quickp3d.DrawingPanel3D;
import quickp3d.simplegraphics.Axis;
import quickp3d.tools.ObjectPicker;
import static rtlcc.CircuitBuilder.CLOSE;
import static rtlcc.CircuitBuilder.FULL_ADDER;

/**
 *
 * @author antunes
 */
public class Circuit3DEditPanel extends DrawingPanel3D {

    private CircuitDrawingTool defaultDrawer;
    private ObjectPicker<Component> picker;
    private CircuitDrawingTool pickerBufferDrawer;
    private PGraphics3D pickerBuffer;
    private ObjectPicker.Selectable<Component> pickerSource;
    private Circuit circuit;

    public Circuit3DEditPanel(Circuit circuit) {
        super(800, 600);
        this.circuit = circuit;
        defaultDrawer = new CircuitDrawingTool(this.circuit) {
            @Override
            public void drawNode(Component c, PGraphics g3d) {

                g3d.stroke(0);

                if (c.type != null) {
                    g3d.fill(c.type.hashCode() * 2);
                } else {
                    g3d.fill(1f, 1, (c.uid * 2 / 360f));
                }

                for (Component v : c.connections) {
                    if (v.pos == null) {
                        g3d.stroke(0, 255, 0);
                        break;
                    }
                }

                for (boolean b : c.doneConnections) {
                    if (!b) {
                        g3d.fill(255, 0, 0);
                        break;
                    }
                }

                boolean printNodeInfo = false;
                for (Component bn : picker) {
                    if (bn.equals(c)) {
                        printNodeInfo = true;
                        break;
                    }
                }

                if (printNodeInfo) {
                    g3d.pushMatrix();
                    g3d.translate(c.pos[0], c.pos[1], c.pos[2]);
                    g3d.fill(0);
                    g3d.scale(DrawingPanel3D.RESET_SCALE / 10);
                    g3d.textSize(100);
                    g3d.text(c.getUID() + Arrays.toString(c.pos), 120, 0, 0);
                    g3d.popMatrix();
                }
//
//                //g3d.fill(Color.cyan.getRGB());
//                g3d.fill(Color.HSBtoRGB(1f / 300 * n.getAddress(), 1, 1));
//
                for (Component bn : picker) {
                    if (bn.equals(c)) {
                        g3d.fill(Color.blue.getRGB());
                    }
                }

                g3d.stroke(0);

                int i = 0;
                for (Component v : c.connections) {
                    if (!c.doneConnections.get(i)) {
                        g3d.stroke(255, 0, 0);
                    }
                    if (v.pos != null) {
                        g3d.line(c.pos[0], c.pos[1], c.pos[2], v.pos[0], v.pos[1], v.pos[2]);
                    }
                    i++;
                }

                super.drawNode(c, g3d);
            }
        };

        pickerBufferDrawer = new CircuitDrawingTool(circuit) {
            @Override
            public void drawNode(Component c, PGraphics g3d) {
                g3d.fill(picker.getColor(Circuit3DEditPanel.this.circuit.vertices.indexOf(c)));
                g3d.noStroke();
                super.drawNode(c, g3d);
            }
        };

        pickerSource = new ObjectPicker.Selectable<Component>() {
            @Override
            public Component select(int index) {
                if (index < Circuit3DEditPanel.this.circuit.vertices.size()) {
                    return Circuit3DEditPanel.this.circuit.vertices.get(index);
                } else {
                    return null;
                }
            }
        };
    }

    @Override
    public void setup(Scene3D scene3D) {
        super.setup(scene3D);
        scene3D.textFont(scene3D.createFont("Ubuntu Light", 50, true));
        pickerBuffer = (PGraphics3D) scene3D.createGraphics(scene3D.width, scene3D.height, PApplet.P3D);
        picker = new ObjectPicker<>(pickerBuffer, pickerSource);
        append(new Axis());
    }

    @Override
    public void draw(PGraphics3D g3d) {
        g3d.background(140, 170, 255);
        defaultDrawer.drawAll(g3d);
    }

    @Override
    public void draw(PGraphics g2d) {
        int unc = 0;
        int con = 0;
        for (Component c : circuit.vertices) {
            unc += (c.pos == null) ? 1 : 0;
            unc += (c.consumed) ? 1 : 0;
        }
        g2d.clear();
        g2d.fill(0);
        g2d.textSize(15);
        g2d.textAlign = PApplet.RIGHT;
        g2d.text("nodes: " + circuit.vertices.size() + " unc: " + circuit.getDisconectedConnections() + " vol: " + circuit.getVolume() + " con:" + con, g2d.width - 30, g2d.height - 40);
    }

    @Override
    public void mouseClicked(PApplet applet) {
        pickerBuffer.beginDraw();
        pickerBuffer.background(Color.black.getRGB());
        scene3D.applyCameraTransform(pickerBuffer);
        pickerBuffer.scale(scale);
        pickerBufferDrawer.drawAll(pickerBuffer);
        pickerBuffer.endDraw();
        picker.select(applet.mouseX, applet.mouseY);
    }

    @Override
    public void keyTyped(PApplet applet) {
        for (final Component c : picker) {
            if (c.pos == null) {
                continue;
            }
            switch (applet.keyCode) {
                case KeyEvent.VK_L:
                    c.pos[0]++;
                    break;
                case KeyEvent.VK_J:
                    c.pos[0]--;
                    break;
                case KeyEvent.VK_I:
                    c.pos[1]++;
                    break;
                case KeyEvent.VK_K:
                    c.pos[1]--;
                    break;
                case KeyEvent.VK_U:
                    c.pos[2]++;
                    break;
                case KeyEvent.VK_O:
                    c.pos[2]--;
                    break;
            }
        }

        if (applet.keyCode == KeyEvent.VK_BACK_SPACE) {
            circuit.reset();
        } else if (applet.keyCode == KeyEvent.VK_ENTER) {
            System.out.println("cub");
            new Thread() {
                @Override
                public void run() {
                    circuit.cubeficate(Topology.SIMPLE2);
                    System.out.println("end");
                }
            }.start();
        } else if (applet.keyCode == KeyEvent.VK_M) {
            new Thread() {
                @Override
                public void run() {
                    for (Component c : circuit.vertices) {
                        boolean remove = true;
                        for (Component j : picker) {
                            if (c == j) {
                                remove = false;
                            }
                        }
                        if (remove) {
                            c.pos = null;
                        }
                    }
                }
            }.start();
        } else if (applet.keyCode == KeyEvent.VK_9) {
            System.out.println("GEN");
            new Thread() {
                @Override
                public void run() {
                    System.out.println("GEN");
                    circuit.geneticPlaceComponents(10, 2, Topology.SIMPLE2, new CircuitBuilder() {
                        @Override
                        public Circuit build() {
                            return CLOSE(FULL_ADDER());
                        }
                    });
                }
            }.start();
        }

    }
}
