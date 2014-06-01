/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rtlcc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 *
 * @author antunes
 */
public class RTLCC {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Circuit and = CircuitBuilder.FULL_ADDER();
        and = CircuitBuilder.CLOSE(and);
//        and.show2D();
//        boolean b = true;
//        while (b) {
//            teste();
//        }

//        and.get("s").pos = new int[]{3, 3, 3};
//        and.get("b").pos = new int[]{4, 0, 0};
//        and.get("s").pos = new int[]{0, 0, 4};
        
        and.get("vcc").pos = new int[]{4, 4, 4};
//        and.get("gnd").pos = new int[]{0, 4, 0};
//        and.geneticPlaceComponents(100, 100, Topology.SIMPLE2, new CircuitBuilder() {
//            @Override
//            public Circuit build() {
//                return CLOSE(FULL_ADDER());
//            }
//        });

        Circuit3DEditPanel cep = new Circuit3DEditPanel(and);
        cep.createFrame("oieeee!");

//        and.cubeficate(Topology.SIMPLE2);
//        and.show3D();

//        {//teste
//            and.get("j").pos = new int[]{0, 0, 0};
//            and.get("a").pos = new int[]{0, 4, 4};
//            and.get("b").pos = new int[]{1, 3, 4};
//            and.get("vcc").pos = new int[]{4, 4, 4};
//            and.makePathTo(and.get("j"), and.get("vcc"), Topology.SIMPLE);
//        }
        System.out.println("done");

//        and.show2D();
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
        for (int i = 0; i < c; i++) {
            if (toInt(l3.get(i)) == val3) {
                count++;
            }
        }
        System.out.println(count);
        System.out.println("t3: " + (System.currentTimeMillis() - t));

        System.gc();
    }

}
