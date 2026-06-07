package me.miran.bedrockcracker.cracker.nether;

import me.miran.bedrockcracker.util.CrackProgress;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

class DefaultNetherCracker extends AbstractNetherCracker {

    private static final long PROGRESS_BATCH_SIZE = 1L << 24;

    @Override
    protected @NotNull List<Long> getSeedCandidates(Test[] testArr) {
        List<Long> results = Collections.synchronizedList(new ArrayList<>());

        int threadCount = (int) (Runtime.getRuntime().availableProcessors() * 0.75);
        threadCount = Math.max(1, threadCount);

        long limit = 1L << 36;
        long chunkSize = limit / threadCount;

        AtomicLong progress = new AtomicLong(0L);
        CountDownLatch latch = new CountDownLatch(threadCount);

        CrackProgress.reset("Nether CPU seed search");

        for (int t = 0; t < threadCount; t++) {
            long start = t * chunkSize;
            long end;

            if (t == threadCount - 1) {
                end = limit;
            } else {
                end = (t + 1) * chunkSize;
            }

            new Thread(() -> {
                try {
                    for (long batchStart = start; batchStart < end; batchStart += PROGRESS_BATCH_SIZE) {
                        long batchEnd = Math.min(batchStart + PROGRESS_BATCH_SIZE, end);

                        for (long i = batchStart; i < batchEnd; i++) {
                            runChecks(testArr, i << 12, 12, results);
                        }

                        long done = progress.addAndGet(batchEnd - batchStart);
                        CrackProgress.report("Nether CPU seed search", done, limit);
                    }
                } finally {
                    latch.countDown();
                }
            }, "BedrockCracker-Nether-" + t).start();
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        CrackProgress.done("Nether CPU seed search");

        return results;
    }
}