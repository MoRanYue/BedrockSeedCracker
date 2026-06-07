package me.miran.bedrockcracker.util;

import me.miran.bedrockcracker.BedrockCracker;

public final class CrackProgress {
    private static final Object LOCK = new Object();

    private static String currentStage = "";
    private static int lastPercent = -1;
    private static long lastMessageTime = 0L;

    private static final int PERCENT_STEP = 5;
    private static final long MIN_INTERVAL_MS = 3000L;

    private CrackProgress() {
    }

    public static void reset(String stage) {
        synchronized (LOCK) {
            currentStage = stage;
            lastPercent = -1;
            lastMessageTime = 0L;
        }

        report(stage, 0, 100);
    }

    public static void report(String stage, long done, long total) {
        if (total <= 0) return;

        int percent = (int) Math.min(100L, Math.max(0L, done * 100L / total));
        long now = System.currentTimeMillis();

        synchronized (LOCK) {
            if (!stage.equals(currentStage)) {
                currentStage = stage;
                lastPercent = -1;
                lastMessageTime = 0L;
            }

            boolean shouldPrint =
                percent >= 100 ||
                lastPercent < 0 ||
                percent >= lastPercent + PERCENT_STEP ||
                now - lastMessageTime >= MIN_INTERVAL_MS;

            if (!shouldPrint) return;

            lastPercent = percent;
            lastMessageTime = now;
        }

        BedrockCracker.sendChatMessage(
            "§7" + stage + ": §e" + percent + "% §8(" + done + "/" + total + ")"
        );
    }

    public static void done(String stage) {
        report(stage, 100, 100);
    }
}