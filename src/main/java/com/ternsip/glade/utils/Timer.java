package com.ternsip.glade.utils;

/**
 * Timer class to control time
 *
 * @author Ternsip
 */
@SuppressWarnings({"unused"})
public class Timer {

    private long timeout = 0;

    /**
     * Holding last tick
     */
    private long lastTime = 0;

    /**
     * Construct time and register current time
     *
     * @param timeout Timer timeout in milliseconds
     */
    public Timer(long timeout) {
        this.timeout = timeout;
        this.lastTime = System.currentTimeMillis();
    }

    /**
     * How much time spent in milliseconds
     *
     * @return How much time spent
     */
    public long spent() {
        return System.currentTimeMillis() - lastTime;
    }

    /**
     * How much time left in milliseconds
     *
     * @return How much time left
     */
    public long left() {
        return Math.max(0, timeout - spent());
    }

    /**
     * Drop timer time
     */
    public void drop() {
        this.lastTime = System.currentTimeMillis();
    }

    /**
     * Is timer counter is over
     *
     * @return timer is over
     */
    public boolean isOver() {
        return spent() > timeout;
    }

}