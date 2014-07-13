/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rtlcc;

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                RTLCC ui = new RTLCC();
                ui.show();
            }
        });
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
