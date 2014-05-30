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

    public static final int X = 5;
    public static final int Y = 5;
    public static final int Z = 5;

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
                            //if (Math.abs(i) + Math.abs(j) + Math.abs(k) == 1) {
                                nd.add(new int[]{x, y, z});
                            //}
                        }
                    }
                }
            }
            return nd;
        }
    };

    public abstract ArrayList<int[]> getNeighborhood(int... pos);
}
