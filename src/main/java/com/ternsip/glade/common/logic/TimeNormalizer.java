package com.ternsip.glade.common.logic;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.function.Supplier;

/**
 * Timer class to control time
 *
 * @author Ternsip
 */
@SuppressWarnings({"unused"})
@Setter
@Getter
public class TimeNormalizer {

    private Supplier<Long> timeoutSupplier;

    private long lastTime;

    public TimeNormalizer(Supplier<Long> timeoutSupplier) {
        this.timeoutSupplier = timeoutSupplier;
        this.lastTime = System.currentTimeMillis();
    }

    public void drop() {
        setLastTime(System.currentTimeMillis());
    }

    @SneakyThrows
    public void rest() {
        long pastTime = System.currentTimeMillis() - getLastTime();
        long needToSleep = Math.max(getTimeoutSupplier().get() - pastTime, 0);
        if (needToSleep > 0) {
            Thread.sleep(needToSleep);
        }
    }

}