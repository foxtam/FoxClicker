package net.foxtam.foxclicker;

public class Time {
    public static double getCurrentSeconds() {
        return System.nanoTime() / 1_000_000_000.0;
    }
}
