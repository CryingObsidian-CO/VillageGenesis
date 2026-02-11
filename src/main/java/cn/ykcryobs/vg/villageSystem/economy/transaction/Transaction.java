package cn.ykcryobs.vg.villageSystem.economy.transaction;

import cn.ykcryobs.vg.villageSystem.economy.payment.IPayment;
import cn.ykcryobs.vg.villageSystem.economy.trader.ITrader;
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

    public Transaction(ITrader buyer, ITrader seller, ItemStack stack, IPayment payment) {
        this.buyer = buyer;
        this.seller = seller;
        this.tradeItem = stack;
        this.payment = payment;
    }

    public TransactionResult execute() {
        // 1. 校验商品是否充足
        if (!this.seller.hasEnough(this.tradeItem)) {
            return TransactionResult.NO_ITEM;
        }

        // 2. 校验支付是否可行
        if (!this.payment.canPay(this.buyer, this.seller)) {
            return TransactionResult.INSUFFICIENT_FUNDS;
        }

        // 3. 执行交易：扣商品、扣支付、加商品、加支付
        this.seller.removeItem(tradeItem);
        this.buyer.addItem(tradeItem);
        this.payment.deductFrom(buyer);
        this.payment.addTo(seller);

        return TransactionResult.SUCCESS;
    }
}
