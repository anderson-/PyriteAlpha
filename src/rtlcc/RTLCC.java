/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rtlcc;

/**
 *
 * @author antunes
 */
public class RTLCC {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Circuit and = CircuitBuilder.AND4();
        and.show2D();

        and.get("a").pos = new int[]{0, 0, 0};
        and.get("b").pos = new int[]{4, 0, 0};
        and.get("c").pos = new int[]{0, 4, 0};
        and.get("d").pos = new int[]{4, 4, 0};
        and.get("s").pos = new int[]{0, 0, 4};
        and.get("vcc").pos = new int[]{4, 0, 4};
        and.get("gnd").pos = new int[]{0, 4, 0};
//        and.get("b").pos = new int[]{3, 0, 0};
//        and.get("s").pos = new int[]{0, 3, 0};
//        and.get("vcc").pos = new int[]{3, 3, 0};
//        and.get("gnd").pos = new int[]{0, 0, 3};
        and.cubeficate(Topology.SIMPLE2);
        and.show3D();
//        {//teste
//            and.get("j").pos = new int[]{0, 0, 0};
//            and.get("a").pos = new int[]{0, 4, 4};
//            and.get("b").pos = new int[]{1, 3, 4};
//            and.get("vcc").pos = new int[]{4, 4, 4};
//            and.makePathTo(and.get("j"), and.get("vcc"), Topology.SIMPLE);
//        }


        System.out.println("done");

        and.show2D();
    }

}
