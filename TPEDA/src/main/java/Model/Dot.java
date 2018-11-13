package Model;


import java.io.Serializable;

public class Dot implements Serializable {

    private StringBuilder tree = new StringBuilder();

    public String getTree(){
        return tree.toString();
    }


    public void drawTree(Node node){
        tree.append("digraph Tree {\n");
        tree.append(node.getId()).append("[label=\"").append("START ").append(node.getScore()).append("\" shape = ").append(node.getShape());

        if(node.isSelected())
            tree.append(", style=filled, color=pink");
        tree.append("];\n");
        for(Node son : node.getDescendants()) {
            drawInternalNode(son);
            tree.append(node.getId()).append("->").append(son.getId()).append("\n");
        }
        tree.append("}");
    }

    public void drawInternalNode(Node node) {
        tree.append(node.getId()).append(" [label=\"");
        if (node.getDiscPlaced() == null) {
            tree.append("(pass) ");
        } else tree.append(node.getDiscPlaced().move.toString());
        tree.append(node.getScore()).append("\", shape = ").append(node.getShape());

        if (node.isSelected()) {
            tree.append(", style=filled, color=pink");
        }
        if (node.isPruned()) {
            tree.append(", style=filled");
        }
        tree.append("];\n");
        for (Node son : node.getDescendants()) {
            drawInternalNode(son);
            tree.append(node.getId()).append("->").append(son.getId()).append("\n");
        }
    }


    public void resetString(){
        this.tree = new StringBuilder();
    }

}
