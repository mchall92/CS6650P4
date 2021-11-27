package server;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This is the class the store key/value pairs
 */
public class KeyValue {

    ConcurrentHashMap<String, String> map;

    /**
     * Constructor to instantiate a map.
     */
    public KeyValue() {
        map = new ConcurrentHashMap<>();
    }

    /**
     * Put Key Vaule pair
     * @param key key
     * @param value value
     * @return true is key is valid (not null and length of key is greater than 1)
     */
    public synchronized boolean put(String key, String value) {
        if (key != null && key.length() > 0) {
            map.put(key, value);
            return true;
        }
        return false;
    }

    /**
     * Get the value of key
     * @param key key
     * @return the mapped value of key
     */
    public synchronized String get(String key) {
        if (key != null && map.containsKey(key)) {
            return map.get(key);
        }
        return null;
    }

    /**
     * Delete key
     * @param key key to be deleted
     */
    public synchronized void delete(String key) {
        if (key != null && map.containsKey(key)) {
            map.remove(key);
        }
    }

    /**
     * Return true if map contains key, otherwise false
     * @param key key
     * @return true if map contains key, otherwise false
     */
    public boolean containsKey(String key) {
        return (key != null) && (key.length() > 0) && map.containsKey(key);
    }
}