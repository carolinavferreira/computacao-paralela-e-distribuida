package Hashing;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class ConsistentHashing implements IConsistentHashing {

    /**
     *Number of virtual nodes
     * @since 0.0.1
     */
    private final int virtualNum;

    /**
     *Hash policy
     * @since 0.0.1
     */
    private final HashFunction hash;

    /**
     *Node map node information
     *
     *Key: node hash
     *Node: node
     * @since 0.0.1
     */
    private final TreeMap<Integer, String> nodeMap = new TreeMap<>();

    public ConsistentHashing(int virtualNum, HashFunction hash) {
        this.virtualNum = virtualNum;
        this.hash = hash;
    }

    /**
     *Find the virtual node clockwise along the ring
     * @param key key
     *@ return result
     * @since 0.0.1
     */
    @Override
    public String get(String key) {
        final int hashCode = hash.hash(key);
        Integer target = hashCode;

        //Processing when not included
        if (!nodeMap.containsKey(hashCode)) {
            target = nodeMap.ceilingKey(hashCode);
            if (target == null && !nodeMap.isEmpty()) {
                target = nodeMap.firstKey();
            }
        }
        return nodeMap.get(target);
    }

    @Override
    public IConsistentHashing add(String node) {
        //Initialize virtual node
        for (int i = 0; i < virtualNum; i++) {
            int nodeKey = hash.hash(node.toString() + "-" + i);
            nodeMap.put(nodeKey, node);
        }

        return this;
    }

    @Override
    public IConsistentHashing remove(String node) {
        for (int i = 0; i < virtualNum; i++) {
            int nodeKey = hash.hash(node.toString() + "-" + i);
            nodeMap.remove(nodeKey);
        }

        return this;
    }

    @Override
    public Map<Integer, String> nodeMap() {
        return Collections.unmodifiableMap(this.nodeMap);
    }

}