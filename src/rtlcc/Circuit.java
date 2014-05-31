/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rtlcc;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.jenetics.BitChromosome;
import org.jenetics.BitGene;
import org.jenetics.GeneticAlgorithm;
import org.jenetics.Genotype;
import org.jenetics.Mutator;
import org.jenetics.NumberStatistics;
import org.jenetics.Optimize;
import org.jenetics.RouletteWheelSelector;
import org.jenetics.SinglePointCrossover;
import org.jenetics.SwapMutator;
import org.jenetics.util.Factory;
import org.jenetics.util.Function;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PGraphics3D;
import quickp3d.DrawingPanel3D;
import quickp3d.graph.QuickGraph;
import quickp3d.simplegraphics.Axis;
import static rtlcc.Topology.X;
import static rtlcc.Topology.Y;
import static rtlcc.Topology.Z;

/**
 *
 * @author antunes
 */
public class Circuit {

    public ArrayList<Component> vertices;
    public ArrayList<Component> inputs;
    public ArrayList<Component> outputs;

    public Circuit() {
        vertices = new ArrayList<>();
        inputs = new ArrayList<>();
        outputs = new ArrayList<>();
    }

    public Component get(String name) {
        for (Component c : vertices) {
            if (c.name != null && c.name.equals(name)) {
                return c;
            }
        }
        return null;
    }

    public void remove(Component c) {
        vertices.remove(c);
        //se é input não é output
        if (!inputs.remove(c)) {
            outputs.remove(c);
        }
    }

    public static Circuit union(Circuit[] cset, String... joints) {
        Circuit nc = new Circuit();

        ArrayList<ArrayList<Component>> toJoin = new ArrayList<>();
        ArrayList<String> jointName = new ArrayList<>();
        ArrayList<Component> temp;
        for (String joint : joints) {
            try {
                temp = new ArrayList<>();
                String s[] = joint.split("->");
                for (String t : s[0].split("\\+")) {
                    t = t.trim();
                    String[] comp = t.split("\\.");
                    int i = Integer.parseInt(comp[0]);
                    String name = comp[1];
                    Component c = cset[i].get(name);
                    if (c == null) {
                        throw new IllegalArgumentException("arg: " + joint + " : " + name + " not found!");
                    }
                    temp.add(c);
                }
                if (s.length > 1) {
                    jointName.add(s[1].trim());
                } else {
                    jointName.add("");
                }
                toJoin.add(temp);
            } catch (Exception e) {
                throw new IllegalArgumentException("arg: " + joint);
            }
        }

        for (int i = 0; i < toJoin.size(); i++) {
            Component c1 = null;
            boolean in = false;
            boolean out = false;
            String name = jointName.get(i);
            if (name.startsWith("in.")) {
                name = name.substring(3);
                in = true;
            } else if (name.startsWith("out.")) {
                name = name.substring(4);
                out = true;
            }
            for (Component c : toJoin.get(i)) {
                if (c1 != null) {
                    c1.join(c);
                    c.consume();
                } else {
                    c1 = c;
                }
            }

            if (c1 == null) {
                throw new IllegalArgumentException("arg: " + i);
            }

            c1.name = name;

            if (!name.isEmpty()) {
                if (in) {
                    nc.inputs.add(c1);
                }
                if (out) {
                    nc.outputs.add(c1);
                }
            }
        }

        //adiciona vertices e entradas/saidas (com nome)
        int i = 0;
        for (Circuit c : cset) {
            if (c == null) {
                throw new IllegalArgumentException("arg: Circuit " + i + " == null");
            }
            i++;
            for (Component comp : c.vertices) {
                if (!comp.consumed && !nc.vertices.contains(comp)) {
                    nc.vertices.add(comp);
                }
            }

            for (Component comp : c.inputs) {
                if (!comp.consumed && comp.name != null && !comp.name.isEmpty() && !nc.vertices.contains(comp)) {
                    nc.inputs.add(comp);
                }
            }

            for (Component comp : c.outputs) {
                if (!comp.consumed && comp.name != null && !comp.name.isEmpty() && !nc.outputs.contains(comp)) {
                    nc.outputs.add(comp);
                }
            }
        }

        return nc;
    }

    public void decubeficate() {
        for (Component c : vertices) {
            c.pos = null;
        }
    }

    public final static int UNVISITED = -1;
    public final static int VISITED = -2;
    public final static int ONQUEUE = -3;

    private int countNg(int[][][] cube, int[] pos, Topology t) {
        int i = 0;
        for (int[] l : t.getNeighborhood(pos)) {
            int p = cube[l[0]][l[1]][l[2]];
            if (p < 0) {
                i++;
            } else {
                i += (p == UNVISITED) ? 1 : 0;
            }
        }
        return i;
    }

    private void whut(Component v, Component j) {
        show2D();
        System.out.println(v + " " + v.getUID());
        for (int i = 0; i < v.connections.size(); i++) {
            System.out.println(i + " " + v.connections.get(i) + " " + v.connections.get(i).getUID());
        }
        System.out.println(j + " " + j.getUID());
        for (int i = 0; i < j.connections.size(); i++) {
            System.out.println(i + " " + j.connections.get(i) + " " + j.connections.get(i).getUID());
        }
    }

    private Component expand(Component v, Component j) {
        int a = v.connections.indexOf(j);
        int b = j.connections.indexOf(v);
        Component n = new Component(true);
        v.connections.set(a, n);
        j.connections.set(b, n);

        n.type = "ex";

        n.connections.add(v);
        n.subComponents.add(v.subComponents.get(a));
        n.terminals.add("");
        n.doneConnections.add(true);

        n.connections.add(j);
        n.subComponents.add("");
        n.terminals.add("");
        n.doneConnections.add(true);

        n.ends.add(v);
        n.ends.add(j);

        j.subComponents.set(b, "");

        vertices.add(n);

        return n;
    }

    private void makePathAndPlace(Component v, Component j, int[][][] cube, Topology t) {
        ArrayDeque<int[]> queue = new ArrayDeque<>();
//        queue.add(v.pos);
//        queue.addAll(t.getNeighborhood(v.pos));
        for (int[] w : t.getNeighborhood(v.pos)) {
            if (cube[w[0]][w[1]][w[2]] == UNVISITED) {
                queue.add(w);
            }
        }

        int[] ini = v.pos;
        int[] old = null;
        int[] pos = ini;
        int vi = j.connections.indexOf(v);
        int ji = v.connections.indexOf(j);

        HashMap<int[], int[]> map = new HashMap<>();
        while (!queue.isEmpty()) {
            old = pos;
            pos = queue.remove();
            if (old != null) {
                map.put(pos, old);
            }

            ArrayList<int[]> neighborhood = t.getNeighborhood(pos);
            Collections.shuffle(neighborhood);
            for (int[] w : neighborhood) {
                if (cube[w[0]][w[1]][w[2]] == UNVISITED) {
                    queue.add(w);
                    cube[w[0]][w[1]][w[2]] = ONQUEUE;
                }
            }

            if (countNg(cube, pos, t) > j.connections.size()) {
                //define a posição de j
                j.pos = pos;
                cube[j.pos[0]][j.pos[1]][j.pos[2]] = vertices.indexOf(j);

                //conecta o caminho
                int[] i = ini;
                Component n = v;
                //System.out.println("place");
                //System.out.println(map.containsKey(i) + " " + map.size());
                while (map.containsKey(i)) {
                    //System.out.println(".");
                    i = map.get(i);
                    n = expand(n, j);
                    n.pos = i;
                    cube[i[0]][i[1]][i[2]] = vertices.indexOf(n);
                }
                v.doneConnections.set(ji, true);
                j.doneConnections.set(vi, true);
                return;
            }
        }
    }

    private void mapToGraph2D(Map<int[], int[]> m) {
        SparseMultigraph<String, String> graph = new SparseMultigraph<>();

        for (int[] i : m.keySet()) {
            graph.addVertex(i + "");
        }

        for (int[] i : m.values()) {
            graph.addVertex(i + "");
        }

        int i = 0;
        for (Entry<int[], int[]> e : m.entrySet()) {
            graph.addEdge(i + "", e.getKey() + "", e.getValue() + "");
            i++;
        }

        Layout<Integer, String> layout = new FRLayout(graph);
        layout.setSize(new Dimension(1300, 1300));
        VisualizationViewer<Integer, String> vv = new VisualizationViewer<>(layout);
        vv.setPreferredSize(new Dimension(350, 350));
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
        DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
        gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
        vv.setGraphMouse(gm);
        JFrame frame = new JFrame("Interactive Graph 2D View");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(vv);
        frame.pack();
        frame.setVisible(true);
    }

    public List<int[]> getDirections(int[] sourceNode, int[] destinationNode, Topology t) {
        //Initialization.
        Map<int[], int[]> nextNodeMap = new TreeMap<>(new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                return Arrays.equals(o1, o2) ? 0 : 1;
            }
        });
        int[] currentNode = sourceNode;

        //Queue
        Queue<int[]> queue = new LinkedList<int[]>();
        queue.add(currentNode);

        /*
         * The set of visited nodes doesn't have to be a Map, and, since order
         * is not important, an ordered collection is not needed. HashSet is 
         * fast for add and lookup, if configured properly.
         */
        Set<int[]> visitedNodes = new TreeSet<>(new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                boolean s = Arrays.equals(o1, o2);
                if (s) {
//                    System.out.println(Arrays.toString(o1) + " == " + Arrays.toString(o2));
                }

                return s ? 0 : 1;
            }
        });
        visitedNodes.add(currentNode);

        //Search.
        while (!queue.isEmpty()) {
//            System.out.println("q:" + queue.size());
            currentNode = queue.remove();
            if (Arrays.equals(currentNode, destinationNode)) {
                break;
            } else {
                for (int[] nextNode : t.getNeighborhood(currentNode)) {
                    if (!visitedNodes.contains(nextNode)) {
                        boolean empty = true;
//                        for (Component c : vertices) {
//                            if (c.pos != null && Arrays.equals(nextNode, c.pos)) {
//                                if (Arrays.equals(nextNode, destinationNode)){
//                                    
//                                } else {
//                                    empty = false;
//                                    visitedNodes.add(nextNode);
//                                }
//                            }
//                        }

                        if (empty) {
                            queue.add(nextNode);
                            visitedNodes.add(nextNode);

                            //Look up of next node instead of previous.
                            nextNodeMap.put(nextNode, currentNode);

                        }
                    }

                }
            }
        }

        //If all nodes are explored and the destination node hasn't been found.
        if (!Arrays.equals(currentNode, destinationNode)) {
            throw new RuntimeException("No feasible path.");
        }

        //Reconstruct path. No need to reverse.
        List<int[]> directions = new LinkedList<int[]>();
        for (int[] node = destinationNode; node != null; node = nextNodeMap.get(node)) {
            if (!Arrays.equals(node, destinationNode) && !Arrays.equals(node, sourceNode)) {
                directions.add(node);
            }
        }

        return directions;
    }

    private int getDisconectedConnections() {
        int dc = 0;
        for (Component c : vertices) {
            for (boolean b : c.doneConnections) {
                if (!b) {
                    dc++;
                }
            }
        }
        return dc;
    }

    private int getVolume() {
        return 1;
    }

    class Dijkstra {

        int toInt(int x, int y, int z) {
            int rgb = x;
            rgb = (rgb << 8) + y;
            rgb = (rgb << 8) + z;
            return rgb;
        }

        int toInt(int... i) {
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

        Map<Integer, Integer> distances = new HashMap<>();

        Map<Integer, Integer> prev = new HashMap<>();

        int source = -1;
        int target = -1;

        public void computePaths(int[] source, int[] target, Topology t) {
            this.source = toInt(source);
            this.target = toInt(target);

            for (Component c : vertices) {
                if (c.pos != null) {
                    int i = toInt(c.pos);
                    if (i != this.source && i != this.target) {
                        distances.put(i, 12000);
                    }
                }
            }

            distances.put(this.source, 0);
            LinkedList<Integer> vertexQueue = new LinkedList<>();
            vertexQueue.add(toInt(source));

            while (!vertexQueue.isEmpty()) {
                int u = vertexQueue.poll();

                // Visit each edge exiting u
                for (int[] vv : t.getNeighborhood(toVet(u))) {
                    int v = toInt(vv);
                    Integer distV = distances.get(v);
                    if (distV == null) {
                        distV = 2000;
                    }

                    Integer distU = distances.get(u);
                    if (distU == null) {
                        distU = 2000;
                    }

                    int distanceThroughU = distU + 1;
                    if (distanceThroughU < distV && distV != 12000) {
                        vertexQueue.remove((Integer) v);
                        distances.put(v, distanceThroughU);
                        prev.put(v, u);
                        vertexQueue.add(v);
                    }
                }
            }
        }

        public List<int[]> getShortestPathTo(int[] target) {
            List<int[]> path = new ArrayList<>();
            for (Integer vertex = toInt(target); vertex != null; vertex = prev.get(vertex)) {
                if (vertex != toInt(target) && vertex != source) {
                    path.add(toVet(vertex));
                }
            }
            Collections.reverse(path);
            return path;
        }
    }

//    
//    public List<Node> getDirections2(Node start, Node finish){
//    List<Node> directions = new LinkedList<Node>();
//    Queue<Node> q = new LinkedList<Node>();
//    Node current = start;
//    q.add(current);
//    while(!q.isEmpty()){
//        current = q.remove();
//        directions.add(current);
//        if (current.equals(finish)){
//            break;
//        }else{
//            for(Node node : current.getOutNodes()){
//                if(!q.contains(node)){
//                    q.add(node);
//                }
//            }
//        }
//    }
//    if (!current.equals(finish)){
//        System.out.println("can't reach destination");
//    }
//    return directions;
//}
//    
    public boolean makePathTo(Component v, Component j, Topology t) {
        try {
            //System.out.println("find");
            int vi = j.connections.indexOf(v);
            int ji = v.connections.indexOf(j);

            Dijkstra d = new Dijkstra();
            d.computePaths(v.pos, j.pos, t);
            List<int[]> directions = null, dt;
            Component f = null;
            for (Component c : vertices) {
                if (c == j || (c.joint && c.ends.contains(j))) {
                    dt = d.getShortestPathTo(c.pos);
                    if (directions == null || dt.size() < directions.size()) {
                        directions = dt;
                        f = c;
                        if (directions.size() == 1) {
                            break;
                        }
                    }
                }
            }

            if (f.joint) {
                //System.out.println("joint join");
            }

            boolean x = false;

            Component n = v;
            for (int[] c : directions) {
                //System.out.println("*");
                n = expand(n, j);
                //definir que todas as juntas compartilham os mesmos ends
                n.pos = c;
                x = true;
            }

            if (!x) {
                for (int[] ng : t.getNeighborhood(v.pos)) {
                    if (Arrays.equals(ng, j.pos)) {
                        x = true;
                        break;
                    }
                }
            }

            if (x) {
                v.doneConnections.set(ji, true);
                j.doneConnections.set(vi, true);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            //System.out.println("fail");
            e.printStackTrace();
            return false;
        }

//        ArrayDeque<int[]> queue = new ArrayDeque<>();
////        queue.add(v.pos);
////        queue.addAll(t.getNeighborhood(v.pos));
//
//        for (int[] w : t.getNeighborhood(v.pos)) {
//            if (cube[w[0]][w[1]][w[2]] == UNVISITED) {
//                queue.add(w);
//            }
//        }
//
//        int[] ini = v.pos;
//        int[] old = null;
//        int[] current = ini;
//        int vi = j.connections.indexOf(v);
//        int ji = v.connections.indexOf(j);
//
//        Map<int[], int[]> prev = new TreeMap<>(new Comparator<int[]>() {
//            @Override
//            public int compare(int[] o1, int[] o2) {
//                return Arrays.equals(o1, o2) ? 0 : 1;
//            }
//        });
//
//        while (!queue.isEmpty()) {
////            System.out.println("find: " + queue.size());
//
//            old = current;
//            current = queue.remove();
//            if (old != null) {
//                prev.put(current, old);
//            }
//            
//            if (!prev.containsKey(old)) {
//                prev.put(old, current);
//                q.add(node);
//            }
//
//            for (int[] w : t.getNeighborhood(current)) {
//                if (Arrays.equals(w, v.pos)) {
//                    continue;
//                }
//                int p = cube[w[0]][w[1]][w[2]];
//                if (p == UNVISITED) {
//                    queue.add(w);
//                    cube[w[0]][w[1]][w[2]] = ONQUEUE;
//
//                }
//                if (p >= 0 && (vertices.get(p) == j || (vertices.get(p).connections.contains(j))
//                        || (vertices.get(p).joint && vertices.get(p).ends.contains(j)))) {
//
////                    map.put(w, pos);
//                    //conecta o caminho até j
//                    int[] i = current;
//                    int[] o = null;
//                    Component n = v;
//                    System.out.println("find");
//                    System.out.println(prev.containsKey(i) + " " + prev.size());
//                    if (prev.containsKey(i)) {
//                        mapToGraph2D(prev);
//                        while (prev.containsKey(i)) {
//                            System.out.println(".");
//                            sleep();
////                            if (Arrays.equals(i, v.pos)) {
////                                System.out.println("exit");
////                                break;
////                            }
//                            o = i;
//                            i = prev.get(i);
//                            n = expand(n, j);
//
//                            if (o != null) {
//                                boolean ok = false;
//                                for (int[] ng : t.getNeighborhood(o)) {
//                                    if (Arrays.equals(ng, i)) {
//                                        ok = true;
//                                        break;
//                                    }
//                                }
//                                if (!ok) {
//                                    System.out.println(Arrays.toString(i) + " is not next to " + Arrays.toString(o));
//                                }
//                            }
//
//                            n.pos = i;
//                            cube[i[0]][i[1]][i[2]] = vertices.indexOf(n);
//                        }
////                        n = expand(n, j);
////                        n.pos = i;
////                        cube[i[0]][i[1]][i[2]] = vertices.indexOf(n);
//
//                        v.doneConnections.set(ji, true);
//                        j.doneConnections.set(vi, true);
//                        return true;
//                    }
//                }
//            }
//        }
//        return false;
    }

    public void resetCube(int[][][] cube) {
        for (int x = 0; x < X; x++) {
            for (int y = 0; y < Y; y++) {
                for (int z = 0; z < Z; z++) {
                    cube[x][y][z] = UNVISITED;
                }
            }
        }
        int i = 0;
        for (Component v : vertices) {
            if (v.pos != null) {
                cube[v.pos[0]][v.pos[1]][v.pos[2]] = i;
            }
            i++;
        }
    }

    public void clearLastSearch(int[][][] cube) {
        for (int x = 0; x < X; x++) {
            for (int y = 0; y < Y; y++) {
                for (int z = 0; z < Z; z++) {
                    if (cube[x][y][z] < 0) {
                        cube[x][y][z] = UNVISITED;
                    }
                }
            }
        }
    }

    public void sleep() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {

        }
    }

    public void cubeficate(Topology t) {
        //guarda a posição no vetor vertices
        int[][][] cube = new int[X][Y][Z];
        resetCube(cube);
        boolean done = false;
        while (!done) {
            done = true;
            for (int vi = 0; vi < vertices.size(); vi++) {
                Component v = vertices.get(vi);
                if (v.pos == null) {
                    done = false;
                } else {
                    int i = 0;
                    for (Component j : v.connections) {
                        if (j.pos == null) {
                            /*
                             coloca j no lugar mais proximo, com expansão 
                             de caminhos se necessario;
                             */
                            makePathAndPlace(v, j, cube, t);
                            sleep();
                            clearLastSearch(cube);
//                            break;
                        } else {
                            if (!v.doneConnections.get(i)) {
                                /*
                                 procura o no mais proximo* para satisfazer a 
                                 conexão;
                                 */
                                if (makePathTo(v, j, t)) {
                                    sleep();
                                } else {
                                    //System.out.println(v.getUID() + " -/-> " + j.getUID());
                                }
//                                break;
                            }
                        }
                        i++;
                    }
                }
            }
        }
    }

    public int bits = 3;//7

    public int placeComponentsByGenotype(Genotype<BitGene> genotype, Topology t) {
        BitChromosome ch = ((BitChromosome) genotype.getChromosome());
        for (int i = 0; i < vertices.size(); i++) {
            int[] p = new int[3];
            for (int j = 0; j < 3; j++) {
                p[j] = 0;
                for (int k = 0; k < bits; k++) {
                    if (ch.getGene((i * bits * 3) + (j * bits) + k).getBit()) {
                        p[j] += Math.pow(2, k);
                    }
                }
            }

//            if (!t.isValid(p)) {
//                return 0;
//            }
            for (Component w : vertices) {
                if (w.pos != null && Arrays.equals(w.pos, p)) {
                    return 0;
                }
            }

            vertices.get(i).pos = p;
        }
        return 1;
    }

    static class OneCounter implements Function<Genotype<BitGene>, Integer> {

        public int bits;
        public static int count = 0;
        public Topology t;
        public CircuitBuilder cb;

        private OneCounter(Topology t, int bits, CircuitBuilder cb) {
            this.t = t;
            this.bits = bits;
            this.cb = cb;
        }

        @Override
        public Integer apply(final Genotype<BitGene> genotype) {
            long time = System.currentTimeMillis();
            Circuit c = cb.build();
            int n1 = c.vertices.size();
            int a = c.placeComponentsByGenotype(genotype, t);
            if (a == 1) {
                c.cubeficate(t);
            }
            int dc = c.getDisconectedConnections();
            int v = c.getVolume();
            int n2 = c.vertices.size();
            double p = a * ((1.0 / v + 1.0 / (dc + 1) + 1.0 / (n2 - n1 + 1)) * 1000);
            System.out.println("done " + count++ + " in " + (System.currentTimeMillis() - time) + " ms, vertices: " + n1 + " -> " + n2 + ", disc: " + dc + " | p: " + p);
            System.gc();

            if (dc == 0) {
                c.show3D();
            }

            return (int) p;
        }
    }

    public void geneticPlaceComponents(int pop, int gen, Topology t, CircuitBuilder cb) {
        System.out.println("" + (this.vertices.size()));
        Factory<Genotype<BitGene>> gtf = Genotype.of(
                BitChromosome.of(this.vertices.size() * bits * 3, 0.15)
        );

        OneCounter oneCounter = new OneCounter(t, bits, cb);

        Function<Genotype<BitGene>, Integer> ff = oneCounter;
        GeneticAlgorithm<BitGene, Integer> ga
                = new GeneticAlgorithm<>(
                        gtf, ff, Optimize.MAXIMUM
                );

        ga.setStatisticsCalculator(
                new NumberStatistics.Calculator<BitGene, Integer>()
        );
        ga.setPopulationSize(pop);
        ga.setSelectors(
                new RouletteWheelSelector<BitGene, Integer>()
        );
        ga.setAlterers(
                new Mutator<BitGene>(0.55),
                new SinglePointCrossover<BitGene>(0.06),
                new SwapMutator<BitGene>(0.2)
        );

        ga.setup();
        ga.evolve(gen);
        System.out.println(ga.getBestStatistics());
        System.out.println(ga.getBestPhenotype());
        System.out.println("" + (this.vertices.size() * bits));
        placeComponentsByGenotype(ga.getBestPhenotype().getGenotype(), t);
    }

    public void show2D() {
        SparseMultigraph<String, String> graph = new SparseMultigraph<>();

        //adiciona vertices
        for (Component v : vertices) {
            graph.addVertex(v.getUID());
        }

        //adciona arestas
        int id = 0;
        for (Component v : vertices) {
            int i = 0;
            for (Component e : v.connections) {
                int eTerm = e.connections.indexOf(v);

                String c1 = v.getUID();
                String c1t = v.terminals.get(i);
                String comp = v.subComponents.get(i);
                String c2t = e.terminals.get(eTerm);
                String c2 = e.getUID();

                //graph.addEdge(c1 + "." + c1t + "-" + comp + "-" + c2 + "." + c2t, v.getUID(), e.getUID());
                graph.addEdge(comp + "[" + id + "]", v.getUID(), e.getUID());
                id++;
                i++;
            }
        }

        Layout<Integer, String> layout = new FRLayout(graph);
        layout.setSize(new Dimension(1300, 1300));
        VisualizationViewer<Integer, String> vv = new VisualizationViewer<>(layout);
        vv.setPreferredSize(new Dimension(350, 350));
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller() {
            @Override
            public String transform(Object v) {
                String s = v.toString();
                return s.substring(0, s.indexOf('['));
            }
        });
        DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
        gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
        vv.setGraphMouse(gm);
        JFrame frame = new JFrame("Interactive Graph 2D View");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(vv);
        frame.pack();
        frame.setVisible(true);

    }

    public void show3D() {

        // instanciando um painel de desenho de 350px x 350px
        DrawingPanel3D drawingPanel = new DrawingPanel3D(350, 350);
        // criando e exibindo a janela principal
        drawingPanel.createFrame("Interactive Graph 3D View");
        // incluindo um objeto 3D (no caso as setas RGB no ponto [0,0,0])
        drawingPanel.append(new Axis());
        drawingPanel.append(new QuickGraph() {
            @Override
            public void draw(PGraphics g2d) {
                int unc = 0;
                for (Component c : vertices) {
                    unc += (c.pos == null) ? 1 : 0;
                }
                g2d.clear();
                g2d.fill(0);
                g2d.textSize(15);
                g2d.textAlign = PApplet.RIGHT;
                g2d.text("nodes: " + vertices.size() + " unc: " + unc, g2d.width - 30, g2d.height - 40);
            }

            @Override
            public void draw(PGraphics3D g3d) {
                for (Component c : vertices) {
                    if (c.pos != null) {

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

                        g3d.pushMatrix();
                        g3d.translate(c.pos[0], c.pos[1], c.pos[2]);
                        g3d.box(.20f);
                        g3d.fill(0);
                        g3d.scale(DrawingPanel3D.RESET_SCALE / 10);
                        g3d.textSize(100);
                        g3d.text(c.getUID() + Arrays.toString(c.pos), 120, 0, 0);
                        g3d.popMatrix();

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
                    }
                }
            }
        });
    }
}
