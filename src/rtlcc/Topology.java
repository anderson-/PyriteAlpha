/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rtlcc;

import java.util.ArrayList;

/**
 *
 * @author antunes
 */
public abstract class Topology {

    public static final int X = 10;
    public static final int Y = 10;
    public static final int Z = 10;

    public static final Topology SIMPLE = new Topology() {
        @Override
        public ArrayList<int[]> getNeighborhood(int... pos) {
            ArrayList<int[]> nd = new ArrayList<>();

            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    for (int k = -1; k <= 1; k++) {
                        int x = pos[0] + i;
                        int y = pos[1] + j;
                        int z = pos[2] + k;

                        if (x >= 0 && y >= 0 && z >= 0 && x < X && y < Y && z < Z) {
                            if (Math.abs(i) + Math.abs(j) + Math.abs(k) == 1) {
                                nd.add(new int[]{x, y, z});
                            }
                        }
                    }
                }
            }
            return nd;
        }
    };

    public static final Topology SIMPLE2 = new Topology() {
        @Override
        public ArrayList<int[]> getNeighborhood(int... pos) {
            ArrayList<int[]> nd = new ArrayList<>();

            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    for (int k = -1; k <= 1; k++) {
                        int x = pos[0] + i;
                        int y = pos[1] + j;
                        int z = pos[2] + k;

                        if (x >= 0 && y >= 0 && z >= 0 && x < X && y < Y && z < Z) {
                            if (Math.abs(i) + Math.abs(j) + Math.abs(k) <= 2) {
                                //if (Math.abs(i) + Math.abs(j) + Math.abs(k) > 2) {
                                nd.add(new int[]{x, y, z});
                            }
                        }
                    }
                }
            }
            return nd;
        }
    };

    public static final Topology SIMPLE3 = new Topology() {
        @Override
        public ArrayList<int[]> getNeighborhood(int... pos) {
            ArrayList<int[]> nd = new ArrayList<>();

            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    for (int k = -1; k <= 1; k++) {
                        int x = pos[0] + i;
                        int y = pos[1] + j;
                        int z = pos[2] + k;

                        if (x >= 0 && y >= 0 && z >= 0 && x < X && y < Y && z < Z) {
                            //if (Math.abs(i) + Math.abs(j) + Math.abs(k) <= 2) { //apenas planos
                            if (Math.abs(i) + Math.abs(j) + Math.abs(k) != 0) { //3d
                                //if (Math.abs(i) + Math.abs(j) + Math.abs(k) > 2) {
                                nd.add(new int[]{x, y, z});
                            }
                        }
                    }
                }
            }
            return nd;
        }
    };

    public static final Topology SIMPLE4 = new Topology() {

        ArrayList<Boolean> a = new ArrayList<>();

        {
            getNeighborhood(0, 0, 0, 0);
        }

        @Override
        public ArrayList<int[]> getNeighborhood(int... pos) {
            if (pos.length > 3) {
                a.clear();
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        for (int k = -1; k <= 1; k++) {
                            a.add(Circuit.rand.nextBoolean());
                        }
                    }
                }
            }

            ArrayList<int[]> nd = new ArrayList<>();

            int e = 0;
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    for (int k = -1; k <= 1; k++) {
                        int x = pos[0] + i;
                        int y = pos[1] + j;
                        int z = pos[2] + k;

                        if (x >= 0 && y >= 0 && z >= 0 && x < X && y < Y && z < Z) {
                            if (Math.abs(i) + Math.abs(j) + Math.abs(k) != 0 && a.get(e)) {
                                nd.add(new int[]{x, y, z});
                            }
                        }
                        e++;
                    }
                }
            }
            return nd;
        }
    };

    public abstract ArrayList<int[]> getNeighborhood(int... pos);

    public ArrayList<Integer> getNeighborhood(int pos) {
        ArrayList<Integer> nd = new ArrayList<>();

        int p0 = (pos >> 16) & 0xFF;
        int p1 = (pos >> 8) & 0xFF;
        int p2 = pos & 0xFF;
        int xyz;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                for (int k = -1; k <= 1; k++) {
                    int x = p0 + i;
                    int y = p1 + j;
                    int z = p2 + k;

                    if (x >= 0 && y >= 0 && z >= 0 && x < X && y < Y && z < Z) {
                        if (Math.abs(i) + Math.abs(j) + Math.abs(k) == 1) {
                            xyz = x;
                            xyz = (xyz << 8) + y;
                            xyz = (xyz << 8) + z;
                            nd.add(xyz);
                        }
                    }
                }
            }
        }
        return nd;
    }

}
