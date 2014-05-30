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
    public int [] pos;
    //nome dos terminais (indice anda junto)
    public ArrayList<String> terminals = new ArrayList<>();
    public ArrayList<Component> connections = new ArrayList<>();
    public ArrayList<String> subComponents = new ArrayList<>();
    public ArrayList<Boolean> doneConnections = new ArrayList<>();
    //junta
    public final boolean joint;
    public boolean consumed;
    public ArrayList<Component> ends; //componentes conectados à junta

    public Component() {
        uid = ID++;
        joint = false;
    }

    public Component(boolean joint) {
        uid = ID++;
        this.joint = joint;
        if (joint) {
            ends = new ArrayList<>();
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


    void consume() {
        if (joint){
            consumed = true;
        }
    }
    
    public void join(Component c) {
        if (joint && c.joint) {
            for (Component adj : c.connections) {
                int i = adj.connections.indexOf(c);
                adj.connections.set(i, this);
            }
            this.ends.addAll(c.ends);
            this.terminals.addAll(c.terminals);
            this.connections.addAll(c.connections);
            this.subComponents.addAll(c.subComponents);
            this.doneConnections.addAll(c.doneConnections);
        } else {
            throw new IllegalArgumentException("this or c is not joint");
        }
    }

}
