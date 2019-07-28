package com.ternsip.glade.common.logic;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

/**
 * Timer class to control time
 *
 * @author Ternsip
 */
@SuppressWarnings({"unused"})
@Setter
@Getter
public class TimeNormalizer {

    private long timeout;

    private long lastTime;

    public TimeNormalizer(long timeout) {
        this.timeout = timeout;
        this.lastTime = System.currentTimeMillis();
    }

    public void drop() {
        setLastTime(System.currentTimeMillis());
    }

    @SneakyThrows
    public void rest() {
        long pastTime = System.currentTimeMillis() - getLastTime();
        long needToSleep = Math.max(getTimeout() - pastTime, 0);
        if (needToSleep > 0) {
            Thread.sleep(needToSleep);
        }
    }

}