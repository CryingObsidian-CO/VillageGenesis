package cn.ykcryobs.vg.villageSystem.currency.transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 报价收集器
 *
 * @author llykff
 */
public class QuoteCollector {

    private final int minRequiredQuotes;
    private final long timeout;
    private final List<PreliminaryQuote> collectedQuotes = new ArrayList<>();
    private final CountDownLatch latch = new CountDownLatch(1);
    private boolean isCompleted = false;

    public QuoteCollector(int minRequiredQuotes, long timeout) {
        this.minRequiredQuotes = minRequiredQuotes;
        this.timeout = timeout;
    }

    public void addQuote(PreliminaryQuote quote) {
        if (isCompleted) {
            return;
        }
        collectedQuotes.add(quote);

        if (collectedQuotes.size() >= minRequiredQuotes) {
            complete();
        }
    }

    /**
     * 触发完成
     */
    private void complete() {
        isCompleted = true;
        latch.countDown();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public List<PreliminaryQuote> await() {
        try {
            latch.await(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return Collections.unmodifiableList(this.collectedQuotes);
    }

}
