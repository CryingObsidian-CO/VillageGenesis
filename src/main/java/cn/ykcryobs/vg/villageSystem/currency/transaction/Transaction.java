package cn.ykcryobs.vg.villageSystem.currency.transaction;

import cn.ykcryobs.vg.villageSystem.currency.ITrader;
import cn.ykcryobs.vg.villageSystem.currency.payment.IPayment;
import com.mojang.logging.LogUtils;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

/**
 * 交易包
 *
 * @author llykff
 */
public class Transaction {

    private static final Logger LOGGER = LogUtils.getLogger();

    private final ITrader buyer;          // 买方
    private final ITrader seller;         // 卖方
    private final ItemStack tradeItem;   // 交易的商品
    private final IPayment payment;      // 支付方式

    private Transaction(Builder builder) {
        this.buyer = builder.buyer;
        this.seller = builder.seller;
        this.tradeItem = builder.tradeItemStack;
        this.payment = builder.payment;
    }

    public TransactionResult execute() {
        // 1. 校验商品是否充足
        if (!seller.hasEnough(tradeItem)) {
            return TransactionResult.FAIL;
        }

        // 2. 校验支付是否可行
        if (!payment.canPay(buyer, seller)) {
            return TransactionResult.FAIL;
        }

        // 3. 执行交易：扣商品、扣支付、加商品、加支付
        seller.removeItem(tradeItem);
        payment.deductFrom(buyer);
        buyer.addItem(tradeItem);
        payment.addTo(seller);

        // 4. 更新供需
        seller.getEconomyData()
                .ifPresent(data -> data.addExtraSupply(tradeItem.getItem(), -tradeItem.getCount()));
        buyer.getEconomyData()
                .ifPresent(data -> data.addExtraSupply(tradeItem.getItem(), tradeItem.getCount()));

        return TransactionResult.SUCCESS;
    }

    public static class Builder {

        private ITrader buyer;
        private ITrader seller;
        private ItemStack tradeItemStack;
        private IPayment payment;


        public Builder buyer(ITrader buyer) {
            this.buyer = buyer;
            return this;
        }

        public Builder seller(ITrader seller) {
            this.seller = seller;
            return this;
        }

        public Builder tradeItem(ItemStack tradeItemStack) {
            this.tradeItemStack = tradeItemStack;
            return this;
        }

        public Builder payment(IPayment payment) {
            this.payment = payment;
            return this;
        }


        // 构建前校验
        public Transaction build() {
            if (buyer == null || seller == null || tradeItemStack == null || payment == null) {
                LOGGER.error("Transaction build failed: buyer={}, seller={}, tradeItemStack={}, payment={}",
                        buyer, seller, tradeItemStack, payment);
                return null;
            }
            if (buyer.getTraderId().equals(seller.getTraderId())) {
                LOGGER.error("Transaction build failed: seller and buyer cannot be the same trader!");
                return null;
            }
            return new Transaction(this);
        }
    }
}
