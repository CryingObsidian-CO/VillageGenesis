package cn.ykcryobs.vg.villageSystem.economy.tradeRoute;

import cn.ykcryobs.vg.VillageGenesis;
import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

/**
 * 村庄交易关系类，代表两个村庄之间的交易关系。 村庄交易关系的创建表示两个村庄之间的第一次接触。
 *
 * @author llykff
 */
public class VillageTradeRelation {

    private final UUID relationId;
    private final UUID villageA;
    private final UUID villageB;
    private TradeRelationType relationType;
    private int relationLevel;
    private long establishedTime;
    private long lastTradeTime;
    private long totalTradeVolume;
    private float trustLevel;

    public VillageTradeRelation(UUID villageA, UUID villageB) {
        this.relationId = UUID.randomUUID();
        this.villageA = villageA;
        this.villageB = villageB;
        this.relationType = TradeRelationType.NEUTRAL;
        this.relationLevel = 50;
        this.establishedTime = VillageGenesis.getGameTime();
        this.lastTradeTime = this.establishedTime;
        this.totalTradeVolume = 0;
        this.trustLevel = 0.5f;
    }

    private VillageTradeRelation(UUID relationId, UUID villageA, UUID villageB,
            TradeRelationType relationType, int relationLevel, long establishedTime, long lastTradeTime,
            long totalTradeVolume, float trustLevel) {
        this.relationId = relationId;
        this.villageA = villageA;
        this.villageB = villageB;
        this.relationType = relationType;
        this.relationLevel = relationLevel;
        this.establishedTime = establishedTime;
        this.lastTradeTime = lastTradeTime;
        this.totalTradeVolume = totalTradeVolume;
        this.trustLevel = trustLevel;
    }

    public static VillageTradeRelation deserializeNBT(CompoundTag tag) {
        UUID relationId = tag.getUUID("relationId");
        UUID villageA = tag.getUUID("villageA");
        UUID villageB = tag.getUUID("villageB");
        TradeRelationType relationType = TradeRelationType.valueOf(tag.getString("relationType"));
        int relationLevel = tag.getInt("relationLevel");
        long establishedTime = tag.getLong("establishedTime");
        long lastTradeTime = tag.getLong("lastTradeTime");
        long totalTradeVolume = tag.getLong("totalTradeVolume");
        float trustLevel = tag.getFloat("trustLevel");

        return new VillageTradeRelation(relationId, villageA, villageB, relationType, relationLevel,
                establishedTime, lastTradeTime, totalTradeVolume, trustLevel);
    }

    public UUID getRelationId() {
        return this.relationId;
    }

    public UUID getVillageA() {
        return this.villageA;
    }

    public UUID getVillageB() {
        return this.villageB;
    }

    public TradeRelationType getRelationType() {
        return this.relationType;
    }

    public int getRelationLevel() {
        return this.relationLevel;
    }

    public void setRelationLevel(int level) {
        this.relationLevel = Math.max(0, Math.min(100, level));
        this.updateRelationType();
    }

    public long getEstablishedTime() {
        return this.establishedTime;
    }

    public long getLastTradeTime() {
        return this.lastTradeTime;
    }

    public long getTotalTradeVolume() {
        return this.totalTradeVolume;
    }

    public float getTrustLevel() {
        return this.trustLevel;
    }

    public boolean involvesVillage(UUID villageId) {
        return this.villageA.equals(villageId) || this.villageB.equals(villageId);
    }

    public boolean involvesBoth(UUID villageA, UUID villageB) {
        return (this.villageA.equals(villageA) && this.villageB.equals(villageB)) || (
                this.villageA.equals(villageB) && this.villageB.equals(villageA));
    }

    public boolean canTrade() {
        return this.relationType.canTrade();
    }

    public float calculateTaxModifier() {
        return this.relationType.getTaxModifier();
    }

    public void improveRelation(int amount) {
        this.relationLevel = Math.min(100, this.relationLevel + amount);
        this.updateRelationType();
    }

    public void worsenRelation(int amount) {
        this.relationLevel = Math.max(0, this.relationLevel - amount);
        this.updateRelationType();
    }

    private void updateRelationType() {
        TradeRelationType newType = TradeRelationType.fromRelationLevel(this.relationLevel);
        if (newType != this.relationType) {
            this.relationType = newType;
        }
    }

    public void recordTrade(long volume) {
        this.lastTradeTime = VillageGenesis.getGameTime();
        this.totalTradeVolume += volume;
        this.improveTrust(0.01f * volume);
        if (volume > 0) {
            this.improveRelation(1);
        }
    }

    public void improveTrust(float amount) {
        this.trustLevel = Math.min(1.0f, this.trustLevel + amount);
    }

    public void reduceTrust(float amount) {
        this.trustLevel = Math.max(0.0f, this.trustLevel - amount);
    }

    public void tickDecay() {
        long currentTime = VillageGenesis.getGameTime();
        long ticksSinceLastTrade = currentTime - this.lastTradeTime;
        if (ticksSinceLastTrade > 24000) {
            int daysSinceLastTrade = (int) (ticksSinceLastTrade / 24000);
            if (daysSinceLastTrade > 7) {
                this.worsenRelation(1);
                this.reduceTrust(0.001f);
            }
        }
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("relationId", this.relationId);
        tag.putUUID("villageA", this.villageA);
        tag.putUUID("villageB", this.villageB);
        tag.putString("relationType", this.relationType.name());
        tag.putInt("relationLevel", this.relationLevel);
        tag.putLong("establishedTime", this.establishedTime);
        tag.putLong("lastTradeTime", this.lastTradeTime);
        tag.putLong("totalTradeVolume", this.totalTradeVolume);
        tag.putFloat("trustLevel", this.trustLevel);
        return tag;
    }
}
