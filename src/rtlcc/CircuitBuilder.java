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
public abstract class CircuitBuilder {

    public static final int INPUT = 2;
    public static final int OUTPUT = 4;

//    private final Circuit c;
//
//    public CircuitBuilder(Circuit c) {
//        this.c = c;
//    }
//
//    public void goTo() {
//
//    }
    //
    public static void connect(Component c1, Component c2) {
        connect(c1, c2, "", "", "");
    }

    public static void connect(Component c1, Component c2, String subComp) {
        connect(c1, c2, subComp, "", "");
    }

    public static void connect(Component c1, Component c2, String subComp, String pin1) {
        connect(c1, c2, subComp, pin1, "");
    }

    public static void connect(Component c1, Component c2, String subComp, String pin1, String pin2) {
        if (subComp.isEmpty()) {
            subComp = "~";
        }

        c1.connections.add(c2);
        c1.subComponents.add(subComp);
        c1.terminals.add(pin1);
        c1.doneConnections.add(false);

        c2.connections.add(c1);
        c2.subComponents.add("");
        c2.terminals.add(pin2);
        c2.doneConnections.add(false);
    }

    public static void set(Component comp, Circuit c) {
        set(comp, c, 0);
    }

    public static void set(Component comp, Circuit c, int type) {
        if (type == INPUT) {
            c.inputs.add(comp);
        } else if (type == OUTPUT) {
            c.outputs.add(comp);
        }
        c.vertices.add(comp);
    }
    
    public abstract Circuit build();

    public static Circuit NAND() {
        Circuit c = new Circuit();

        Component a = new Component(true);
        Component b = new Component(true);
        Component s = new Component(true);
        Component j = new Component(true);
        Component vcc = new Component(true);
        Component gnd = new Component(true);
        Component t1 = new Component();
        Component t2 = new Component();

        a.name = "a";
        set(a, c, INPUT);

        b.name = "b";
        set(b, c, INPUT);

        s.name = "s";
        set(s, c, OUTPUT);

        j.name = "j";
        set(j, c);

        vcc.name = "vcc";
        set(vcc, c);

        gnd.name = "gnd";
        set(gnd, c);

        t1.type = "transistor";
        set(t1, c);
        t2.type = "transistor";
        set(t2, c);

        connect(t1, a, "res10k", "b");
        connect(t2, b, "res10k", "b");
        connect(j, vcc, "res1k");
        connect(j, s);
        connect(t1, j, "", "c");
        connect(t1, t2, "", "e", "c");
        connect(t2, gnd, "", "e");

        return c;
    }

    public static Circuit AND() {
        Circuit c = new Circuit();

        Component a = new Component(true);
        Component b = new Component(true);
        Component s = new Component(true);
        Component j = new Component(true);
        Component vcc = new Component(true);
        Component gnd = new Component(true);
        Component t1 = new Component();
        Component t2 = new Component();

        a.name = "a";
        set(a, c, INPUT);

        b.name = "b";
        set(b, c, INPUT);

        s.name = "s";
        set(s, c, OUTPUT);

        j.name = "j";
        set(j, c);

        vcc.name = "vcc";
        set(vcc, c);

        gnd.name = "gnd";
        set(gnd, c);

        t1.type = "transistor";
        set(t1, c);
        t2.type = "transistor";
        set(t2, c);

        connect(t1, a, "res10k", "b");
        connect(t2, b, "res10k", "b");
        connect(j, gnd, "res1k");
        connect(j, s);
        connect(t2, j, "", "e");
        connect(t1, t2, "", "e", "c");
        connect(t1, vcc, "", "c");

        return c;
    }

    public static Circuit OR() {
        Circuit c = new Circuit();

        Component a = new Component(true);
        Component b = new Component(true);
        Component s = new Component(true);
        Component j1 = new Component(true);
        Component j2 = new Component(true);
        Component j3 = new Component(true);
        Component vcc = new Component(true);
        Component gnd = new Component(true);
        Component t1 = new Component();
        Component t2 = new Component();

        a.name = "a";
        set(a, c, INPUT);

        b.name = "b";
        set(b, c, INPUT);

        s.name = "s";
        set(s, c, OUTPUT);

        j1.name = "j1";
        set(j1, c);
        j2.name = "j2";
        set(j2, c);
        j3.name = "j3";
        set(j3, c);

        vcc.name = "vcc";
        set(vcc, c);

        gnd.name = "gnd";
        set(gnd, c);

        t1.type = "transistor";
        set(t1, c);
        t2.type = "transistor";
        set(t2, c);

        connect(t1, a, "res10k", "b");
        connect(t2, b, "res10k", "b");
        connect(j1, vcc, "res1k");
        connect(j1, j2);
        connect(t1, j1, "", "c");
        connect(t1, j3, "", "e");
        connect(j2, s);
        connect(t2, j2, "", "c");
        connect(t2, j3, "", "e");
        connect(j3, gnd);

        return c;
    }

    public static Circuit NOR() {
        Circuit c = new Circuit();

        Component a = new Component(true);
        Component b = new Component(true);
        Component s = new Component(true);
        Component j1 = new Component(true);
        Component j2 = new Component(true);
        Component j3 = new Component(true);
        Component vcc = new Component(true);
        Component gnd = new Component(true);
        Component t1 = new Component();
        Component t2 = new Component();

        a.name = "a";
        set(a, c, INPUT);

        b.name = "b";
        set(b, c, INPUT);

        s.name = "s";
        set(s, c, OUTPUT);

        j1.name = "j1";
        set(j1, c);
        j2.name = "j2";
        set(j2, c);
        j3.name = "j3";
        set(j3, c);

        vcc.name = "vcc";
        set(vcc, c);

        gnd.name = "gnd";
        set(gnd, c);

        t1.type = "transistor";
        set(t1, c);
        t2.type = "transistor";
        set(t2, c);

        connect(t1, a, "res10k", "b");
        connect(t2, b, "res10k", "b");
        connect(j1, vcc);
        connect(j1, j2);
        connect(t1, j1, "", "c");
        connect(t1, j3, "", "e");
        connect(j2, s);
        connect(t2, j2, "", "c");
        connect(t2, j3, "", "e");
        connect(j3, gnd, "res1k");

        return c;
    }

    public static Circuit XOR() {
        Circuit c = new Circuit();

        Component a = new Component(true);
        Component b = new Component(true);
        Component s = new Component(true);
        Component j1 = new Component(true);
        Component j2 = new Component(true);
        Component j3 = new Component(true);
        Component j4 = new Component(true);
        Component vcc = new Component(true);
        Component gnd = new Component(true);
        Component t1 = new Component();

        a.name = "a";
        set(a, c, INPUT);

        b.name = "b";
        set(b, c, INPUT);

        s.name = "s";
        set(s, c, OUTPUT);

        j1.name = "j1";
        set(j1, c);
        j2.name = "j2";
        set(j2, c);
        j3.name = "j3";
        set(j3, c);
        j4.name = "j4";
        set(j4, c);

        vcc.name = "vcc";
        set(vcc, c);

        gnd.name = "gnd";
        set(gnd, c);

        t1.type = "transistor";
        set(t1, c);

        connect(a, j1, "(-|<-)");
        connect(a, j3, "(->|-)");
        connect(b, j2, "(-|<-)");
        connect(b, j4, "(->|-)");
        connect(j2, t1, "(->|-)", "b");

        connect(j3, s, "res1k");
        connect(t1, s, "", "c");

        connect(t1, gnd, "", "e");
        connect(j4, gnd, "res2k");

        connect(j1, j2);
        connect(j3, j4);

        connect(j1, vcc, "res2k");

        return c;
    }

    public static Circuit FULL_ADDER() {
        Circuit c0 = XOR();
        Circuit c1 = XOR();
        Circuit c2 = AND();
        Circuit c3 = AND();
        Circuit c4 = OR();
        Circuit fa = Circuit.union(new Circuit[]{c0, c1, c2, c3, c4},
                "0.a + 3.a -> in.a",
                "0.b + 3.b -> in.b",
                "1.b + 2.a -> in.ci",
                "0.s + 1.a + 2.b",
                "2.s + 4.a",
                "3.s + 4.b",
                "1.s -> out.s",
                "4.s -> out.co",
                "0.vcc + 1.vcc+ 2.vcc + 3.vcc + 4.vcc -> vcc",
                "0.gnd + 1.gnd+ 2.gnd + 3.gnd + 4.gnd -> gnd"
        );

        return fa;
    }

    public static Circuit BUTTON() {
        Circuit c = new Circuit();

        Component vcc = new Component(true);
        Component s = new Component(true);
        Component gnd = new Component(true);

        vcc.name = "vcc";
        set(vcc, c);
        
        gnd.name = "gnd";
        set(gnd, c);

        s.name = "s";
        set(s, c, OUTPUT);

        connect(s, vcc, "btn");
        connect(s, gnd, "res10k");

        return c;
    }

    public static Circuit BATTERY() {
        Circuit c = new Circuit();

        Component vcc = new Component(true);
        Component gnd = new Component(true);

        vcc.name = "vcc";
        set(vcc, c);

        gnd.name = "gnd";
        set(gnd, c);

        connect(gnd, vcc, "bat");

        return c;
    }

    public static Circuit LED() {
        Circuit c = new Circuit();

        Component gnd = new Component(true);
        Component j = new Component(true);
        Component a = new Component(true);

        gnd.name = "gnd";
        set(gnd, c);

        j.name = "j";
        set(j, c);

        a.name = "a";
        set(a, c, INPUT);

        connect(j, gnd, "res1k");
        connect(a, j, "(->^^|-)");

        return c;
    }

    public static Circuit CLOSE(Circuit c) {

        int nbtns = c.inputs.size();
        int nleds = c.outputs.size();

        Circuit[] cs = new Circuit[nbtns + nleds + 1];
        cs[0] = c;

        String[] joints = new String[nbtns + nleds + 2];
        joints[joints.length - 1] = "";
        joints[joints.length - 2] = "0.gnd";
        int i = 0;

        for (Component w : c.inputs) {
            joints[joints.length - 1] += i + ".vcc + ";
            joints[i++] = (i) + ".s + 0." + w.name;
            cs[i] = BUTTON();
            //System.out.println(i + "BTN");
            joints[joints.length - 2] += " + " + i + ".gnd";
            //System.out.println(w.name);
        }
        //System.out.println("--");
        for (Component w : c.outputs) {
            joints[i++] = "0." + w.name + " + " + i + ".a";
            cs[i] = LED();
            //System.out.println(i + "LED");
            joints[joints.length - 2] += " + " + i + ".gnd";
            //System.out.println(w.name);
        }

        joints[joints.length - 1] += (i - nleds) + ".vcc -> vcc";
        joints[joints.length - 2] += " -> gnd";

//        for (String s : joints) {
//            System.out.println(s);
//        }

        c = Circuit.union(cs, joints);

        c = Circuit.union(new Circuit[]{c, BATTERY()}, "0.vcc + 1.vcc -> vcc", "0.gnd + 1.gnd -> gnd");

        return c;
    }

//    public static Circuit BUILD_ADDER(int bits) {
//        ArrayList<Circuit> cs = new ArrayList<>();
//        for (int i = 0; i < bits; i++) {
//
//        }
//    }
    public static Circuit NAND4() {
        Circuit a = NAND();
        Circuit b = NAND();
        Circuit c = NAND();
        Circuit and4 = Circuit.union(new Circuit[]{a, b, c},
                "0.a -> a",
                "0.b -> b",
                "1.a -> c",
                "1.b -> d",
                "0.s + 2.a",
                "1.s + 2.b",
                "2.s -> s",
                "0.vcc + 1.vcc+ 2.vcc -> vcc",
                "0.gnd + 1.gnd+ 2.gnd -> gnd"
        );
        return and4;
    }

}
