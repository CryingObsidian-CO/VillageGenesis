package cn.ykcryobs.vg.event;

import cn.ykcryobs.vg.villageSystem.currency.transaction.Inquiry;
import cn.ykcryobs.vg.villageSystem.currency.transaction.QuoteCollector;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

/**
 * @author llykff
 */
public class InquiryBroadcastEvent extends Event implements IModBusEvent {

    private final QuoteCollector collector;
    private final Inquiry inquiry;

    public InquiryBroadcastEvent(QuoteCollector collector, Inquiry inquiry) {
        this.collector = collector;
        this.inquiry = inquiry;
    }

    public Inquiry getInquiry() {
        return inquiry;
    }

    public QuoteCollector getCollector() {
        return collector;
    }
}
