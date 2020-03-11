package com.dcg.socket.netty;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This is a container to store all the 'RequestFuture'. For now it uses action as the key; but the best practise is to use
 * an unique message id (eg: message id generated before send message to server) as the key.
 *
 *
 * @author joe
 */
public final class RequestFutureCenter {

    private static Map<String, RequestFuture> map = new ConcurrentHashMap<>();

    /**
     * Save an request future into container.
     * @param action
     * @param future
     */
    public static void add(String action, RequestFuture future) {
        map.put(action, future);
    }

    /**
     * This should be called to set value when tcp client read message.
     * Will try to find the future according to the action which is an key here.
     * @param action
     * @param msg
     */
    public static void set(String action, String msg) {
    	RequestFuture future = map.get(action);
        if (future != null) {
        	future.setSuccess(msg);
            map.remove(action);
        }
    }


}