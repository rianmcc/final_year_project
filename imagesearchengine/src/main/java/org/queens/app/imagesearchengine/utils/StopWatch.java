package org.queens.app.imagesearchengine.utils;

public class StopWatch {
    long sumTime = 0, startTime;

    public void start() {
        startTime = System.currentTimeMillis();
    }

    public void stop() {
        sumTime += System.currentTimeMillis() - startTime;
    }

    public long getTimeSinceStart() {
        return  System.currentTimeMillis() - startTime;
    }

    public long getTime() {
        return sumTime;
    }
}