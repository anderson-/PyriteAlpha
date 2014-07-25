/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rtlcc;

import com.falstad.circuit.CirSim;
import com.falstad.circuit.CircuitElm;
import com.falstad.circuit.CircuitNode;
import com.falstad.circuit.CircuitNodeLink;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import static javax.swing.JSplitPane.VERTICAL_SPLIT;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import net.infonode.docking.RootWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.View;
import net.infonode.docking.mouse.DockingWindowActionMouseButtonListener;
import net.infonode.docking.properties.RootWindowProperties;
import net.infonode.docking.theme.DockingWindowsTheme;
import net.infonode.docking.theme.ShapedGradientDockingTheme;
import net.infonode.docking.util.DockingUtil;
import net.infonode.gui.laf.InfoNodeLookAndFeel;
import processing.core.PApplet;
import rtlcc.circuitsim.MyLogicInputElm;
import rtlcc.circuitsim.MyLogicOutputElm;

/**
 *
 * @author antunes
 */
public class RTLCC {

//    public static void main2(String[] args) {
//        //Circuit and = CircuitBuilder.AND();
//        Circuit.sleep = 10;
//        Circuit and = CircuitBuilder.FULL_ADDER();
//        and = CircuitBuilder.CLOSE(and);
//        and.reset();
//        and.show2D();
////        boolean b = true;
////        while (b) {
////            teste();
////        }
//
//        and.get("vcc").pos = new int[]{3, 4, 4};
//        and.get("gnd").pos = new int[]{6, 4, 4};
////        and.get("b").pos = new int[]{4, 0, 0};
////        and.get("s").pos = new int[]{0, 0, 4};
//
////        and.get("vcc").pos = new int[]{2, 2, 2};
////        and.get("gnd").pos = new int[]{0, 4, 0};
////        and.show2D();
////        and.geneticPlaceComponents(10, 10, Topology.SIMPLE, new CircuitBuilder() {
////            @Override
////            public Circuit build() {
////                return CLOSE(AND());
////            }
////        });
//        Circuit3DEditPanel cep = new Circuit3DEditPanel(and);
//        cep.createFrame("oieeee!");
//
////        and.cubeficate(Topology.SIMPLE2);
////        and.show3D();
////        {//teste
////            and.get("j").pos = new int[]{0, 0, 0};
////            and.get("a").pos = new int[]{0, 4, 4};
////            and.get("b").pos = new int[]{1, 3, 4};
////            and.get("vcc").pos = new int[]{4, 4, 4};
////            and.makePathTo(and.get("j"), and.get("vcc"), Topology.SIMPLE);
////        }
//        System.out.println("done");
//
////        
//    }
    private JFrame window;
    private RootWindow rootWindow;
    private DockingWindowsTheme currentTheme = new net.infonode.docking.theme.ShapedGradientDockingTheme();
    //private DockingWindowsTheme currentTheme = new net.infonode.docking.theme.DefaultDockingTheme();
    private RootWindowProperties properties = new RootWindowProperties();

    public RTLCC() {
        createAndShowUI();
    }

    private void createAndShowUI() {
        window = new JFrame("RTLCC");
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.getContentPane().setPreferredSize(new Dimension(1000, 600));

        rootWindow = new RootWindow(null);
        rootWindow.setBorder(null);
        properties.addSuperObject(currentTheme.getRootWindowProperties());
        rootWindow.getRootWindowProperties().addSuperObject(properties);
        // Add a mouse button listener that closes a window when it's clicked with the middle mouse button.
        rootWindow.addTabMouseButtonListener(DockingWindowActionMouseButtonListener.MIDDLE_BUTTON_CLOSE_LISTENER);
        window.getContentPane().add(rootWindow);

        try {
            InfoNodeLookAndFeel infoNodeLookAndFeel = new InfoNodeLookAndFeel();
            UIManager.setLookAndFeel(infoNodeLookAndFeel);
        } catch (UnsupportedLookAndFeelException ex) {
        }

        //fill
        Circuit3DEditPanel cep = new Circuit3DEditPanel();
        View applet = new View("3D view", null, cep.createPanel());
        ConfigurationPanel configurationPanel = new ConfigurationPanel(cep);
        View parameters = new View("Parameters", null, configurationPanel.getContentPane());
        DockingUtil.addWindow(applet, rootWindow);
        DockingUtil.addWindow(parameters, rootWindow);
        SplitWindow splitWindow = new SplitWindow(true, 0.8f, applet, parameters);
        configurationPanel.setWindow(splitWindow.getRightWindow());
        rootWindow.setWindow(splitWindow);
        window.pack();
    }

    private void show() {
        //centraliza a janela
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle frame = window.getBounds();
        window.setLocation((screen.width - frame.width) / 2, (screen.height - frame.height) / 2);
        //torna a janela visivel
        window.setVisible(true);
//        freezeLayout(true);
    }

    private void freezeLayout(boolean freeze) {
        // Freeze window operations
        properties.getDockingWindowProperties().setDragEnabled(!freeze);
        properties.getDockingWindowProperties().setCloseEnabled(!freeze);
        properties.getDockingWindowProperties().setMinimizeEnabled(!freeze);
        properties.getDockingWindowProperties().setRestoreEnabled(!freeze);
        properties.getDockingWindowProperties().setMaximizeEnabled(!freeze);
        properties.getDockingWindowProperties().setUndockEnabled(!freeze);
        properties.getDockingWindowProperties().setDockEnabled(!freeze);

        // Freeze tab reordering inside tabbed panel
        properties.getTabWindowProperties().getTabbedPanelProperties().setTabReorderEnabled(!freeze);

        properties.getSplitWindowProperties().setDividerLocationDragEnabled(!freeze);
    }

    public static void main(String[] args) throws InterruptedException {
//        SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                RTLCC ui = new RTLCC();
//                ui.show();
//            }
//        });
        String d = "$ 1 5.0E-6 10.20027730826997 54 5.0 50\n"
                + "w 432 384 272 400 0\n"
                + "162 432 384 496 224 1 2.1024259 1.0 0.0 0.0\n"
                + "r 112 224 288 32 0 1000.0\n"
                + "w 112 224 272 400 0\n"
                + "t 176 192 208 192 0 1 -2.487582614283124E-4 9.900989699053085E-12 100.0\n"
                + "w 112 224 208 176 0\n"
                + "t 368 288 400 288 0 1 8.99975124172867 9.0 100.0\n"
                + "w 400 272 272 400 0\n"
                + "w 208 208 176 336 0\n"
                + "w 176 336 400 304 0\n"
                + "w 320 144 176 336 0\n"
                + "s 176 64 288 32 0 1 false\n"
                + "s 448 96 288 32 0 0 false\n"
                + "v 320 144 288 32 0 0 40.0 9.0 0.0 0.0 0.5\n"
                + "r 176 64 320 144 0 10000.0\n"
                + "r 448 96 320 144 0 10000.0\n"
                + "r 496 224 320 144 0 1000.0\n"
                + "r 176 192 176 64 0 10000.0\n"
                + "w 368 288 448 96 0\n"
                + "- 464 112 544 128 0 2.5 s\n"
                + "w 464 112 448 96 0\n"
                + "+ 448 96 496 64 0 1 false 5.0 0.0 a\n";

        String d2 = "$ 1 5.0E-6 10.20027730826997 54 5.0 50\n"
                + "w 464 384 304 400 0\n"
                + "162 464 384 528 224 1 2.1024259 1.0 0.0 0.0\n"
                + "r 144 224 320 32 0 1000.0\n"
                + "w 144 224 304 400 0\n"
                + "t 208 192 240 192 0 1 -2.487582614283124E-4 9.900989699053085E-12 100.0\n"
                + "w 144 224 240 176 0\n"
                + "t 400 288 432 288 0 1 8.99975124172867 9.0 100.0\n"
                + "w 432 272 304 400 0\n"
                + "w 240 208 208 336 0\n"
                + "w 208 336 432 304 0\n"
                + "w 352 144 208 336 0\n"
                + "s 208 64 320 32 0 1 false\n"
                + "s 480 96 320 32 0 0 false\n"
                + "v 352 144 320 32 0 0 40.0 9.0 0.0 0.0 0.5\n"
                + "r 208 64 352 144 0 10000.0\n"
                + "r 480 96 352 144 0 10000.0\n"
                + "r 528 224 352 144 0 1000.0\n"
                + "r 208 192 208 64 0 10000.0\n"
                + "w 400 288 480 96 0\n"
                + "- 496 112 576 128 0 2.5 s\n"
                + "w 496 112 480 96 0\n"
                + "+ 496 112 480 96 0 0 false 5.0 0.0 a\n";

        String inv = "$ 1 5.0E-6 10.20027730826997 54 5.0 50\n"
                + "t 320 192 320 272 0 1 0.5875584150944089 -3.7844516501481884 100.0\n"
                + "t 336 272 416 272 0 1 0.6035944264912844 0.6279899347574025 100.0\n"
                + "g 416 288 416 336 0\n"
                + "r 320 192 320 112 0 4700.0\n"
                + "r 416 112 416 192 0 1000.0\n"
                + "w 416 192 416 256 0\n"
                + "R 320 112 240 112 0 0 40.0 5.0 0.0 0.0 0.5\n"
                + "w 320 112 416 112 0\n"
                + "+ 304 272 240 272 0 0 false 5.0 0.0 a\n"
                + "- 416 192 496 192 0 2.5 s\n";

        String inv2 = "$ 1 5.0E-6 10.20027730826997 54 5.0 50\n"
                + "t 288 240 288 272 0 1 0.5908812846401497 0.5911300429121449 100.0\n"
                + "t 352 272 384 272 0 1 -4.999751241627014 2.4875827199525436E-4 100.0\n"
                + "g 384 320 384 336 0\n"
                + "r 288 192 288 112 0 4700.0\n"
                + "r 384 112 384 192 0 1000.0\n"
                + "w 384 192 384 256 0\n"
                + "R 240 112 208 112 0 0 40.0 5.0 0.0 0.0 0.5\n"
                + "w 288 112 384 112 0\n"
                + "+ 240 272 208 272 0 0 false 5.0 0.0 a\n"
                + "- 432 192 464 192 0 2.5 s\n"
                + "w 384 288 384 320 0\n"
                + "w 352 272 304 272 0\n"
                + "w 272 272 240 272 0\n"
                + "w 288 240 288 192 0\n"
                + "w 240 112 288 112 0\n"
                + "w 432 192 384 192 0\n";

        String inv3 = "$ 1 5.0E-6 10.20027730826997 54 5.0 50\n"
                + "t 288 240 288 272 0 1 0.5908812846401497 0.5911300429121449 100.0\n"
                + "t 352 272 384 272 0 1 -4.999751241627014 2.487582719952544E-4 100.0\n"
                + "g 384 320 384 336 0\n"
                + "r 288 192 288 112 0 4700.0\n"
                + "r 384 112 384 192 0 1000.0\n"
                + "w 384 192 384 256 0\n"
                + "R 240 112 208 112 0 0 40.0 5.0 0.0 0.0 0.5\n"
                + "w 288 112 384 112 0\n"
                + "+ 240 272 208 272 0 0 false 5.0 0.0 a\n"
                + "- 432 192 464 192 0 2.5 s\n"
                + "w 384 288 384 320 0\n"
                + "w 352 272 320 288 0\n"
                + "w 272 272 240 272 0\n"
                + "w 288 240 288 192 0\n"
                + "w 240 112 288 112 0\n"
                + "w 432 192 384 192 0\n"
                + "w 304 272 320 288 0\n";

        String v = "$ 1 5.0E-6 10.20027730826997 52 5.0 50\n"
                + "t 192 208 240 208 0 1 0.6381941100809846 0.6478969866591135 100.0\n"
                + "t 288 208 336 208 0 1 -0.00970287657056978 7.559144238542608E-12 100.0\n"
                + "t 384 208 432 208 0 1 -0.00970287657056978 7.559144238542608E-12 100.0\n"
                + "w 240 160 240 192 0\n"
                + "w 336 160 336 192 0\n"
                + "w 240 160 336 160 0\n"
                + "w 336 160 432 160 0\n"
                + "w 432 160 432 192 0\n"
                + "r 192 208 192 272 0 470.0\n"
                + "r 288 208 288 272 0 470.0\n"
                + "r 384 208 384 272 0 470.0\n"
                + "+ 192 272 192 304 0 1 false 3.6 0.0 a\n"
                + "+ 288 272 288 304 0 0 false 3.6 0.0 b\n"
                + "+ 384 272 384 304 0 0 false 3.6 0.0 c\n"
                + "w 240 224 240 336 0\n"
                + "w 336 224 336 336 0\n"
                + "w 432 224 432 336 0\n"
                + "w 432 336 336 336 0\n"
                + "w 336 336 240 336 0\n"
                + "r 432 160 432 64 0 640.0\n"
                + "g 432 336 432 368 0\n"
                + "R 432 64 368 64 0 0 40.0 3.6 0.0 0.0 0.5\n"
                + "- 432 160 480 160 0 2.5 s\n";

//        String s = CircuitBuilder.OR.build().dump(true);
//        System.out.println(s);
        CirSim ogf = new CirSim(null);
        ogf.startCircuitText = v;
        ogf.init();
        ogf.register(MyLogicInputElm.class, ogf.constructElement(MyLogicInputElm.class, 0, 0));
        ogf.register(MyLogicOutputElm.class, ogf.constructElement(MyLogicOutputElm.class, 0, 0));
        ogf.posInit();

        Thread.sleep(1500);
//
        CircuitBuilder.parse(ogf).show2D();
//
//        System.out.println(s2);
//        CirSim ogf2 = new CirSim(null);
//        ogf2.init();
//        ogf2.register(MyLogicInputElm.class, ogf2.constructElement(MyLogicInputElm.class, 0, 0));
//        ogf2.register(MyLogicOutputElm.class, ogf2.constructElement(MyLogicOutputElm.class, 0, 0));
//        ogf2.readSetup(s2);
    }

    int toInt(int x, int y, int z) {
        int rgb = x;
        rgb = (rgb << 8) + y;
        rgb = (rgb << 8) + z;
        return rgb;
    }

    static int toInt(int[] i) {
        int rgb = i[0];
        rgb = (rgb << 8) + i[1];
        rgb = (rgb << 8) + i[2];
        return rgb;
    }

    int[] toVet(int i) {
        int x = (i >> 16) & 0xFF;
        int y = (i >> 8) & 0xFF;
        int z = i & 0xFF;
        return new int[]{x, y, z};
    }

    private static void teste() {
        int c = 20000000;
        long t;

        ArrayList<Integer> l = new ArrayList<>();
        Integer val = 13123172;
        int count = 0;

        for (int i = 0; i < c; i++) {
            l.add((int) (Math.random() * 100));
        }

        t = System.currentTimeMillis();
        for (int i = 0; i < c; i++) {
            if (val == l.get(i)) {
                count++;
            }
        }
        System.out.println(count);
        System.out.println("t1: " + (System.currentTimeMillis() - t));

        ArrayList<int[]> l2 = new ArrayList<>();
        int[] val2 = new int[]{2, 3, 4};
        count = 0;

        for (int i = 0; i < c; i++) {
            l2.add(new int[]{(int) (Math.random() * 100), (int) (Math.random() * 100), (int) (Math.random() * 100)});

        }
        t = System.currentTimeMillis();
        for (int i = 0; i < c; i++) {
            if (Arrays.equals(l2.get(i), val2)) {
                count++;
            }
        }
        System.out.println(count);
        System.out.println("t2: " + (System.currentTimeMillis() - t));

        ArrayList<int[]> l3 = new ArrayList<>();
        int val3 = 126313281;
        count = 0;

        for (int i = 0; i < c; i++) {
            l3.add(new int[]{(int) (Math.random() * 100), (int) (Math.random() * 100), (int) (Math.random() * 100)});

        }
        t = System.currentTimeMillis();
        for (int[] i : l3) {
            if ((((i[0] << 8 | i[1]) << 8) | i[2]) == val3) {
                count++;
            }
        }
        System.out.println(count);
        System.out.println("t3: " + (System.currentTimeMillis() - t));

        System.gc();
    }

}
