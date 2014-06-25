/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rtlcc;

import com.jogamp.newt.event.KeyEvent;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PGraphics3D;
import quickp3d.DrawingPanel3D;
import quickp3d.simplegraphics.Axis;
import quickp3d.tools.ObjectPicker;

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
    private CircuitBuilder circuitBuilder;
    private Topology topology = Topology.SIMPLE;
    private Thread cubThread = null;
    private ArrayList<String> messages = new ArrayList<>();
    private Thread placeThread = null;

    public int pop = 10;
    public int gen = 10;

    public Circuit3DEditPanel(CircuitBuilder cb) {
        super(800, 600);

        eyeZ = 1150;
        atX = 0;
        atY = -200;
        atZ = 648;
        upX = 0;
        upY = 1;
        posX = -400;
        posY = -620;
        theta = 500;

        defaultDrawer = new CircuitDrawingTool() {
            @Override
            public void drawNode(Component c, PGraphics g3d) {

                boolean printNodeInfo = false;
                for (Component bn : picker) {
                    if (bn.equals(c)) {
                        printNodeInfo = true;
                        break;
                    }
                }

                if (printNodeInfo || true) {
                    g3d.pushMatrix();
                    g3d.translate(c.pos[0], c.pos[1], c.pos[2]);
                    g3d.fill(0);
                    g3d.scale(DrawingPanel3D.RESET_SCALE / 10);
                    g3d.textSize(100);
                    g3d.text(c.getUID() + Arrays.toString(c.pos), 120, 0, 0);
                    g3d.popMatrix();
                }

                if (c.type != null) {
                    switch (c.type) {
                        case "transistor":
                            g3d.fill(0);
                            break;
                        case "jj":
                            g3d.fill(Color.magenta.getRGB());
                            break;
                        case "j":
                            g3d.fill(Color.WHITE.getRGB());
                            break;
                        case "&":
                            if (c.name == null) {
                                g3d.fill(Color.orange.getRGB());
                            } else {
                                if (c.name.equals("vcc")) {
                                    g3d.fill(Color.yellow.getRGB());
                                } else if (c.name.equals("gnd")) {
                                    g3d.fill(Color.blue.getRGB());
                                } else {
                                    g3d.fill(Color.orange.getRGB());
                                }
                            }
                            break;
                        case "ex":
                            g3d.fill(Color.LIGHT_GRAY.getRGB());
                            break;
                        case "w":
                            g3d.fill(Color.CYAN.getRGB());
                            break;
                    }
                } else {
                    g3d.fill(1f, 1, (c.uid * 2 / 360f));
                }

                int i = 0;
                for (Component v : c.connections) {
                    if (!c.doneConnections.get(i)) {
                        g3d.stroke(255, 0, 0);
                        if (v.pos != null) {
                            drawDashedLine(g3d, c.pos[0], c.pos[1], c.pos[2], v.pos[0], v.pos[1], v.pos[2]);
                        }
                    } else {
                        g3d.stroke(0, 255, 0);
                        if (v.pos != null) {
                            g3d.line(c.pos[0], c.pos[1], c.pos[2], v.pos[0], v.pos[1], v.pos[2]);
                        }
                    }
                    i++;
                }

                for (Component v : c.connections) {
                    if (v.pos == null) {
                        g3d.stroke(0, 255, 0);
                        break;
                    }
                }

                for (boolean b : c.doneConnections) {
                    if (!b) {
                        g3d.stroke(255, 0, 0);
                        break;
                    }
                }

                //g3d.stroke(0);
                for (Component bn : picker) {
                    if (bn.equals(c)) {
                        g3d.stroke(Color.blue.getRGB());
                    }
                }

//                g3d.fill(Color.HSBtoRGB((Circuit3DEditPanel.this.circuit.vertices.indexOf(c) / (float) Circuit3DEditPanel.this.circuit.vertices.size()), 1, 1));
//                g3d.noStroke();
                super.drawNode(c, g3d);
            }

        };

        pickerBufferDrawer = new CircuitDrawingTool() {
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
        setCircuitBuilder(cb);
    }

    public Circuit3DEditPanel() {
        this(null);
    }

    public final void setCircuitBuilder(CircuitBuilder cb) {
        this.circuitBuilder = cb;
        if (cb != null) {
            circuit = cb.build();
        } else {
            circuit = null;
        }
        defaultDrawer.setCircuit(circuit);
        pickerBufferDrawer.setCircuit(circuit);
    }

    public CircuitBuilder getCircuitBuilder() {
        return circuitBuilder;
    }

    public Circuit getCircuit() {
        return circuit;
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
        //g3d.background(140, 170, 255);
        //g3d.background(18,97,128);
        //g3d.background(150,200,169);
        g3d.background(246, 217, 159);
        //g3d.background(200);
        defaultDrawer.drawAll(g3d);
    }

    @Override
    public void draw(PGraphics g2d) {
        int plc = 0;
        int n = 0;
        for (Component c : circuit.vertices) {
            plc += (c.pos == null) ? 1 : 0;
            n += (c.fixed) ? 1 : 0;
        }
        g2d.clear();
        g2d.fill(0);
        g2d.textSize(15);
        //top info
        g2d.textAlign = PApplet.LEFT;
        int x = 10;
        int y = 15;
        g2d.text("Connections missing: " + circuit.getDisconectedConnections(), x, y);
        y += 15;
        g2d.text("Nodes placed: " + (circuit.vertices.size() - plc), x, y);
        y += 15;
        g2d.text("Nodes unplaced: " + plc, x, y);
        y += 15;
        g2d.text("Nodes total: " + circuit.vertices.size(), x, y);
        y += 15;
        g2d.text("Volume: " + circuit.getVolume(), x, y);
        y += 15;
        int p = Circuit.fitnessFunction(n, circuit.vertices.size(), circuit.getDisconectedConnections(), circuit.getVolume());
        g2d.text("Points: " + p, x, y);
        y += 15;
        g2d.text("P: " + eyeZ + " " + atX + " " + atY + " " + atZ + " " + upX + " " + upY + " " + posX + " " + posY + " " + theta, x, y);
        //bottom info
        g2d.textAlign = PApplet.RIGHT;
        y = 25;
        for (String m : messages) {
            y += 15;
            g2d.text(m, g2d.width - x, g2d.height - y);
        }
    }

    public int print(String m) {
        messages.add(m);
        while (messages.size() > 8) {
            messages.remove(0);
        }
        return messages.size() - 1;
    }

    public int print(int i, String m) {
        messages.set(i, m);
        return i;
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
                case KeyEvent.VK_J:
                    c.pos[0] += left().x;
                    c.pos[1] += left().y;
                    break;
                case KeyEvent.VK_L:
                    c.pos[0] += right().x;
                    c.pos[1] += right().y;
                    break;
                case KeyEvent.VK_I:
                    c.pos[2]++;
                    break;
                case KeyEvent.VK_K:
                    c.pos[2]--;
                    break;
            }
        }

        if (applet.keyCode == KeyEvent.VK_BACK_SPACE) {
            circuit.reset();
        } else if (applet.keyCode == KeyEvent.VK_ENTER) {
            if (cubThread == null || !cubThread.isAlive()) {
                cubThread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            int i = print("cubeficating...");
                            circuit.cubeficate(topology);
                            print(i, "cubeficating... done.");
                        } catch (ThreadDeath e) {
                            print("break");
                        }
                    }
                };
                cubThread.start();
            } else {
                //print("cubefication not done!");
            }
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
        } else if (applet.keyCode == KeyEvent.VK_G) {
            circuit.show2D();
        } else if (applet.keyCode == KeyEvent.VK_X) {
            if (cubThread != null && cubThread.isAlive()) {
                cubThread.stop();
            }
            if (placeThread != null && placeThread.isAlive()) {
                placeThread.stop();
            }
        } else if (applet.keyCode == KeyEvent.VK_R) {
            circuit.decubeficate();
            circuit.get("vcc").pos = new int[]{3, 4, 4};
            circuit.get("gnd").pos = new int[]{6, 4, 4};
        } else if (applet.keyCode == KeyEvent.VK_P) {
            if (placeThread == null || !placeThread.isAlive()) {
                placeThread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            final int i = print("placing components...");
                            final int tpop = pop;
                            final int tgen = gen;
                            circuit.geneticPlaceComponents(tpop, tgen, topology, circuitBuilder);
                            //                                    new CircuitBuilder() {
                            //                                int x = 0;
                            //                                int w = 0;
                            //
                            //                                @Override
                            //                                public Circuit build() {
                            //                                    x++;
                            //                                    if (x > w) {
                            //                                        w = x;
                            //                                        print(i, "placing components... " + (tpop * tgen - x));
                            //                                    }
                            //
                            //                                    return FULL_ADDER();
                            //                                }
                            //                            }
                            print(i, "placing components... done.");
                        } catch (ThreadDeath e) {
                            print("break");
                        }
                    }
                };
                placeThread.start();
            }
        } else if (applet.keyCode == KeyEvent.VK_T) {
            if (Circuit.sleep == 0) {
                Circuit.sleep = 10;
            } else {
                Circuit.sleep = 0;
            }
            print("Set timestep = " + Circuit.sleep + " ms");
        } else if (applet.keyCode == KeyEvent.VK_1) {
            print("SET: Topology.SIMPLE");
            topology = Topology.SIMPLE;
        } else if (applet.keyCode == KeyEvent.VK_2) {
            print("SET: Topology.SIMPLE2");
            topology = Topology.SIMPLE2;
        } else if (applet.keyCode == KeyEvent.VK_3) {
            print("SET: Topology.SIMPLE3");
            topology = Topology.SIMPLE3;
        } else if (applet.keyCode == KeyEvent.VK_4) {
            print("SET: Topology.SIMPLE4");
            topology = Topology.SIMPLE4;
        }

    }

    public static void drawDotedLine(PGraphics g3d, float x0, float y0, float z0, float x1, float y1, float z1) {
        float d = (float) Math.sqrt((x0 - x1) * (x0 - x1) + (y0 - y1) * (y0 - y1) + (z0 - z1) * (z0 - z1));
        int s = 100;
        float x, y, z, t = 0;
        for (; t <= 1;) {
            x = (1 - t) * x0 + t * x1;
            y = (1 - t) * y0 + t * y1;
            z = (1 - t) * z0 + t * z1;
            g3d.point(x, y, z);
            t += 1f / s;
        }
    }

    public static void drawDashedLine(PGraphics g3d, float x0, float y0, float z0, float x1, float y1, float z1) {
        float d = (float) Math.sqrt((x0 - x1) * (x0 - x1) + (y0 - y1) * (y0 - y1) + (z0 - z1) * (z0 - z1));
        float s = d / 0.1f; //[0.01-0.12]
        float x, y, z, t = 0;
        float a, b, c;
        for (; t <= 1;) {
            x = (1 - t) * x0 + t * x1;
            y = (1 - t) * y0 + t * y1;
            z = (1 - t) * z0 + t * z1;
            t += 1f / s;
            a = (1 - t) * x0 + t * x1;
            b = (1 - t) * y0 + t * y1;
            c = (1 - t) * z0 + t * z1;
            t += 2f / s;
            g3d.line(x, y, z, a, b, c);
        }
    }
}
