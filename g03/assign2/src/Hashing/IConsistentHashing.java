package Hashing;

import java.util.Map;

public interface IConsistentHashing {

    /**
     *Get the corresponding node
     * @param key key
     *@ return node
     * @since 0.0.1
     */
    String get(final String key);

    /**
     *Add node
     *@ param node node
     * @return this
     * @since 0.0.1
     */
    IConsistentHashing add(final String node);

    /**
     *Remove node
     *@ param node node
     * @return this
     * @since 0.0.1
     */
    IConsistentHashing remove(final String node);

    /**
     *Get node information
     *@ return node
     * @since 0.0.1
     */
    Map<Integer, String> nodeMap();

}
