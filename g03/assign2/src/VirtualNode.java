
public class VirtualNode {
    private Node physicalNode;
    private final int v_node_index;
    VirtualNode(Node physicalNode,int v_node_index) {
        this.physicalNode = physicalNode;
        this.v_node_index = v_node_index;
    }
    boolean isVirtualNodeOf(Node physicalNode) {
        return this.physicalNode.getKey().equals(physicalNode.getKey());
      }

    public Node getPhysicalNode() {
        return physicalNode;
    }
}