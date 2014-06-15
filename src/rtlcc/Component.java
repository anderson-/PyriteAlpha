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
public class Component {

    public static int ID = 0;
    //id
    public int uid;
    public String type;
    public String name; //apenas para procura no grafo
    //posição
    public int[] pos;
    //nome dos terminais (indice anda junto)
    public ArrayList<String> terminals = new ArrayList<>();
    public ArrayList<Component> connections = new ArrayList<>();
    public ArrayList<String> subComponents = new ArrayList<>();
    public ArrayList<Boolean> doneConnections = new ArrayList<>();
    //reset
    public ArrayList<String> FIXED_terminals;
    public ArrayList<Component> FIXED_connections;
    public ArrayList<String> FIXED_subComponents;
    public ArrayList<Boolean> FIXED_doneConnections;
    public ArrayList<Component> FIXED_ends;
    public ArrayList<String> FIXED_endTerminals;
    //junta
    public final boolean joint;
    public boolean consumed;
    public ArrayList<Component> ends; //componentes conectados à junta
    public ArrayList<String> endTerminals; //e seus respectivos terminais
    public boolean fixed = false;

    public Component() {
        uid = ID++;
        joint = false;
    }

    public Component(boolean joint) {
        uid = ID++;
        this.joint = joint;
        if (joint) {
            ends = new ArrayList<>();
            endTerminals = new ArrayList<>();
        }
    }

    public String getUID() {
        String UID;
        if (name == null || name.isEmpty()) {
            if (type == null || type.isEmpty()) {
                UID = "j";
            } else {
                UID = type;
            }
        } else {
            UID = name;
        }

        //return name + "." + type + "." + uid;
        return UID + ((joint) ? "*" : "") + uid;
    }

    private void consume() {
        if (joint) {
            consumed = true;
            fixed = false;
            pos = null;
        }
    }

    public void joinAndConsume(Component c) {
        if (joint && c.joint) {
            type = "&";
            for (Component adj : c.connections) {
                int i = adj.connections.indexOf(c);
                adj.connections.set(i, this);
                
                i = adj.FIXED_connections.indexOf(c);
                adj.FIXED_connections.set(i, this);
            }
            this.ends.addAll(c.ends);
            this.endTerminals.addAll(c.endTerminals);
            this.terminals.addAll(c.terminals);
            this.connections.addAll(c.connections);
            this.subComponents.addAll(c.subComponents);
            this.doneConnections.addAll(c.doneConnections);

            this.FIXED_terminals.addAll(c.FIXED_terminals);
            this.FIXED_connections.addAll(c.FIXED_connections);
            this.FIXED_subComponents.addAll(c.FIXED_subComponents);
            this.FIXED_doneConnections.addAll(c.FIXED_doneConnections);
            this.fixed = true;
            c.consume();
        } else {
            throw new IllegalArgumentException("this or c is not joint");
        }
    }

    public void fixed() {
        if (!fixed) {
            FIXED_terminals = new ArrayList<>();
            FIXED_connections = new ArrayList<>();
            FIXED_subComponents = new ArrayList<>();
            FIXED_doneConnections = new ArrayList<>();
            fixed = true;
        }
    }

    public void reset() {
        if (fixed) {
            terminals.clear();
            terminals.addAll(FIXED_terminals);
            connections.clear();
            connections.addAll(FIXED_connections);
            subComponents.clear();
            subComponents.addAll(FIXED_subComponents);
            doneConnections.clear();
            doneConnections.addAll(FIXED_doneConnections);
        }
    }

}
