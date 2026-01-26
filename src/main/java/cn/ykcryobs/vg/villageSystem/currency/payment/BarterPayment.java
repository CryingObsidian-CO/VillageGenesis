package cn.ykcryobs.vg.villageSystem.currency.payment;

import cn.ykcryobs.vg.villageSystem.currency.ITrader;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

/**
 * 交换支付实现类
 *
 * @author llykff
 */
public class BarterPayment implements IPayment {

    private ItemStack barterItem; // 交换物（买方支付的物品）
    private float workPoint; // 需要的工分

    public BarterPayment() {
    }

    @Override
    public boolean canPay(ITrader payer, ITrader payee) {
        if (barterItem == null || barterItem.isEmpty()) {
            return false;
        }
        return payer.hasEnough(barterItem);
    }

    @Override
    public void deductFrom(ITrader trader) {
        trader.removeItem(barterItem);
    }

    @Override
    public void addTo(ITrader trader) {
        trader.addItem(barterItem);
    }

    @Override
    public double getTotalWorkPoints() {
        return this.workPoint;
    }

    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.BARTER;
    }

    @Override
    public void createPayment(ITrader payer, ITrader payee, float totalWorkPoint) {
        this.workPoint = totalWorkPoint;
        Set<Item> barterItems = payer.getPreferenceMultiplier().keySet();
        barterItems.addAll(payee.getPreferenceMultiplier().keySet());
        if (barterItems.isEmpty()) {
            return;
        }

        double maxScore = Double.MIN_VALUE;
        Item maxItem = null;
        for (Item item : barterItems) {
            double a = payer.getPreferenceMultiplier().getOrDefault(item, 1f);
            double b = payee.getPreferenceMultiplier().getOrDefault(item, 1f);
            if (a <= 0 || b <= 0) {
                continue;
            }
            double diff = Math.abs(a - b) / (a + b) / 2;

            double aScore = 100.0 / (1.0 + Math.log1p(a));
            double bScore = 100.0 * (1.0 - Math.exp(-b));
            double diffScore = 100.0 / (1.0 + Math.pow(diff, 2));

            // NOTE 具体总价格计算相关系数，可根据实际情况调整
            double totalScore = (aScore * 1.2 + bScore * 0.9 + diffScore * 0.9);
            if (totalScore > maxScore) {
                maxScore = totalScore;
                maxItem = item;
            }
        }
        if (maxItem != null) {
            float maxA = payer.getPreferenceMultiplier().getOrDefault(maxItem, 1f);
            float maxB = payee.getPreferenceMultiplier().getOrDefault(maxItem, 1f);
            float negotiationLevel = 0.5f;
            if (payer.getNegotiationLevel() != 0 || payee.getNegotiationLevel() != 0) {
                negotiationLevel = (float) payer.getNegotiationLevel() / (payer.getNegotiationLevel()
                        + payee.getNegotiationLevel());
            }
            double unitPrice = maxA * negotiationLevel + maxB * (1 - negotiationLevel);
            int requiredBarterCount = (int) Math.floor(this.workPoint / unitPrice);
            // 向下取整后，工分误差小于总公分的 1% ，认为是足够的
            if (this.workPoint - requiredBarterCount * unitPrice < this.workPoint * 0.01) {
                barterItem = new ItemStack(maxItem, requiredBarterCount);
            }
        }
    }
}
