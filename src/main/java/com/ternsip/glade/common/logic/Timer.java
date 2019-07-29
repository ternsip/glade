package com.ternsip.glade.common.logic;

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

    /**
     * How much time spent in milliseconds
     *
     * @return How much time spent
     */
    private long spent() {
        return System.currentTimeMillis() - lastTime;
    }

    /**
     * How much time in milliseconds is needed to be left to finish timer
     *
     * @return How much time is demanded
     */
    public long demand() {
        return Math.max(0, timeout - spent());
    }

}