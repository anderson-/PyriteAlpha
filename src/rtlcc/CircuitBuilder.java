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
public class CircuitBuilder {

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
        c1.connections.add(c2);
        c1.subComponents.add(subComp);
        c1.terminals.add(pin1);
        c1.doneConnections.add(false);

        c2.connections.add(c1);
        c2.subComponents.add(subComp);
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
        connect(j, vcc, "res1k");
        connect(j, s);
        connect(t1, j, "c");
        connect(t1, t2, "", "e", "c");
        connect(t2, gnd);

        return c;
    }

    public static Circuit AND4() {
        Circuit a = AND();
        Circuit b = AND();
        Circuit c = AND();
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
