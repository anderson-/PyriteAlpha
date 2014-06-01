/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rtlcc;

import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import processing.core.PGraphics;
import quickp3d.DrawingPanel3D;

/**
 *
 * @author antunes
 */
public class CircuitDrawingTool {

    private final Circuit circuit;

    public CircuitDrawingTool(Circuit circuit) {
        this.circuit = circuit;
    }

    public void drawNode(Component c, PGraphics g3d) {
        if (c.pos == null) {
            return;
        }
        g3d.pushMatrix();
        g3d.translate(c.pos[0], c.pos[1], c.pos[2]);
        g3d.box((c.joint && (c.name == null || (c.name != null && c.name.contains("ex")))) ? .03f : .20f);
        g3d.popMatrix();
    }

    public void drawAll(PGraphics g3d) {
        for (Component c : (ArrayList<Component>) circuit.vertices.clone()) {
            if (c.pos != null) {
                drawNode(c, g3d);
            }
        }
    }

    /*  OLD  */
    public void drawVector(RealVector v, PGraphics g3d) {
        if (!g3d.stroke) {
            g3d.stroke(255);
        }
        g3d.line(0, 0, 0, (float) v.getEntry(0),
                (float) v.getEntry(1),
                (float) v.getEntry(2));
    }

    public void drawVector(RealVector v1, RealVector v2, PGraphics g3d) {
        if (!g3d.stroke) {
            g3d.stroke(255);
        }
        g3d.line((float) v1.getEntry(0),
                (float) v1.getEntry(1),
                (float) v1.getEntry(2),
                (float) v2.getEntry(0),
                (float) -v2.getEntry(1),
                (float) v2.getEntry(2));
    }
}
