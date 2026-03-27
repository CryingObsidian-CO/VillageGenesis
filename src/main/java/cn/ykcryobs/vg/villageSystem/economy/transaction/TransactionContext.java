package cn.ykcryobs.vg.villageSystem.economy.transaction;

import cn.ykcryobs.vg.item.ITradableItem;
import cn.ykcryobs.vg.villageSystem.economy.tradeRoute.TradeType;
import cn.ykcryobs.vg.villageSystem.economy.trader.ITrader;
import cn.ykcryobs.vg.villageSystem.economy.trader.TraderSnapshot;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * 交易上下文类，封装交易所需的所有上下文数据。
 * 
 * <p>
 * <b>线程安全：<b>sellerSnapshots 和 buyerSnapshot 是不可变快照，可以安全地在异步线程中使用。
 * sellerMap 和 buyer 引用仅应在主线程上访问。
 * </p>
 *
 * @author llykff
 */
public final class TransactionContext {

    private final Item item;
    private final ITradableItem tradableItem;
    private final int requestedAmount;
    private final TraderSnapshot buyerSnapshot;
    private final ITrader buyer;
    private final List<TraderSnapshot> sellerSnapshots;
    private final Map<UUID, ITrader> sellerMap;
    private final TradeType preferredType;

    /**
     * 私有构造函数，使用 Builder 创建实例
     *
     * @param builder 构建器
     */
    private TransactionContext(Builder builder) {
        this.item = builder.item;
        this.tradableItem = builder.tradableItem;
        this.requestedAmount = builder.requestedAmount;
        this.buyerSnapshot = builder.buyerSnapshot;
        this.buyer = builder.buyer;
        this.sellerSnapshots = Collections.unmodifiableList(new ArrayList<>(builder.sellerSnapshots));
        this.sellerMap = Collections.unmodifiableMap(new HashMap<>(builder.sellerMap));
        this.preferredType = builder.preferredType;
    }

    /**
     * 获取交易物品
     *
     * @return 交易物品
     */
    public Item getItem() {
        return this.item;
    }

    /**
     * 获取可交易物品
     *
     * @return 可交易物品
     */
    public ITradableItem getTradableItem() {
        return this.tradableItem;
    }

    /**
     * 获取请求数量
     *
     * @return 请求数量
     */
    public int getRequestedAmount() {
        return this.requestedAmount;
    }

    /**
     * 获取买家快照
     *
     * @return 买家快照
     */
    public TraderSnapshot getBuyerSnapshot() {
        return this.buyerSnapshot;
    }

    /**
     * 获取买家（仅应在主线程上访问）
     *
     * @return 买家
     */
    public ITrader getBuyer() {
        return this.buyer;
    }

    /**
     * 获取卖家快照列表（线程安全）
     *
     * @return 卖家快照列表
     */
    public List<TraderSnapshot> getSellerSnapshots() {
        return this.sellerSnapshots;
    }

    /**
     * 根据ID获取卖家（仅应在主线程上访问）
     *
     * @param sellerId 卖家ID
     * @return 卖家（如果存在）
     */
    public ITrader getSeller(UUID sellerId) {
        return this.sellerMap.get(sellerId);
    }

    /**
     * 获取首选交易类型
     *
     * @return 首选交易类型
     */
    public TradeType getPreferredType() {
        return this.preferredType;
    }

    /**
     * 创建构建器
     *
     * @return 构建器实例
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 交易上下文构建器
     */
    public static final class Builder {

        private Item item;
        private ITradableItem tradableItem;
        private int requestedAmount;
        private TraderSnapshot buyerSnapshot;
        private ITrader buyer;
        private List<TraderSnapshot> sellerSnapshots = new ArrayList<>();
        private Map<UUID, ITrader> sellerMap = new HashMap<>();
        private TradeType preferredType = TradeType.ANY;

        /**
         * 设置交易物品
         *
         * @param item 交易物品
         * @return 构建器
         */
        public Builder item(Item item) {
            this.item = item;
            this.tradableItem = (ITradableItem) item;
            return this;
        }

        /**
         * 设置请求数量
         *
         * @param amount 请求数量
         * @return 构建器
         */
        public Builder requestedAmount(int amount) {
            this.requestedAmount = amount;
            return this;
        }

        /**
         * 设置买家
         *
         * @param buyer 买家
         * @return 构建器
         */
        public Builder buyer(ITrader buyer) {
            Objects.requireNonNull(buyer, "buyer must not be null");
            this.buyer = buyer;
            this.buyerSnapshot = TraderSnapshot.fromTrader(buyer, this.tradableItem);
            return this;
        }

        /**
         * 添加卖家
         *
         * @param seller 卖家
         * @return 构建器
         */
        public Builder addSeller(ITrader seller) {
            Objects.requireNonNull(seller, "seller must not be null");
            TraderSnapshot snapshot = TraderSnapshot.fromTrader(seller, this.tradableItem);
            this.sellerSnapshots.add(snapshot);
            this.sellerMap.put(seller.getTraderId(), seller);
            return this;
        }

        /**
         * 设置首选交易类型
         *
         * @param type 首选交易类型
         * @return 构建器
         */
        public Builder preferredType(TradeType type) {
            this.preferredType = type;
            return this;
        }

        /**
         * 构建交易上下文
         *
         * @return 交易上下文
         */
        public TransactionContext build() {
            Objects.requireNonNull(this.item, "item must not be null");
            Objects.requireNonNull(this.tradableItem, "tradableItem must not be null");
            Objects.requireNonNull(this.buyerSnapshot, "buyerSnapshot must not be null");
            Objects.requireNonNull(this.buyer, "buyer must not be null");

            return new TransactionContext(this);
        }
    }
}
