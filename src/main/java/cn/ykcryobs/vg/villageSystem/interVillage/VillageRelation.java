package cn.ykcryobs.vg.villageSystem.interVillage;

import cn.ykcryobs.vg.VillageGenesis;
import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

// TODO 依然是重写 村庄间关系的具体类型，比如优化 RelationStatus 及其转移方式 （当前版本仅作为相关模块的占位）
public class VillageRelation {

    private final UUID relationId;
    private final UUID villageA;
    private final UUID villageB;
    private final long establishedTime;
    private RelationStatus status;
    private float relationScore;
    private long lastUpdateTime;

    public VillageRelation(UUID villageA, UUID villageB) {
        this.relationId = UUID.randomUUID();
        this.villageA = villageA;
        this.villageB = villageB;
        this.status = RelationStatus.NEUTRAL;
        this.relationScore = 0.0f;
        this.establishedTime = VillageGenesis.getGameTime();
        this.lastUpdateTime = this.establishedTime;
    }

    private VillageRelation(UUID relationId, UUID villageA, UUID villageB, RelationStatus status,
            float relationScore, long establishedTime, long lastUpdateTime) {
        this.relationId = relationId;
        this.villageA = villageA;
        this.villageB = villageB;
        this.status = status;
        this.relationScore = relationScore;
        this.establishedTime = establishedTime;
        this.lastUpdateTime = lastUpdateTime;
    }

    public static VillageRelation deserializeNBT(CompoundTag tag) {
        return new VillageRelation(tag.getUUID("relationId"), tag.getUUID("villageA"),
                tag.getUUID("villageB"), RelationStatus.valueOf(tag.getString("status")),
                tag.getFloat("relationScore"), tag.getLong("establishedTime"), tag.getLong("lastUpdateTime"));
    }

    public void tick() {
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("relationId", this.relationId);
        tag.putUUID("villageA", this.villageA);
        tag.putUUID("villageB", this.villageB);
        tag.putString("status", this.status.name());
        tag.putFloat("relationScore", this.relationScore);
        tag.putLong("establishedTime", this.establishedTime);
        tag.putLong("lastUpdateTime", this.lastUpdateTime);
        return tag;
    }

    public UUID getRelationId() {
        return relationId;
    }

    public UUID getVillageA() {
        return villageA;
    }

    public UUID getVillageB() {
        return villageB;
    }

    public RelationStatus getStatus() {
        return status;
    }

    public void setStatus(RelationStatus status) {
        this.status = status;
        this.markDirty();
    }

    public float getRelationScore() {
        return relationScore;
    }

    // TODO  评分更改后，status 也应该更改
    public void setRelationScore(float relationScore) {
        this.relationScore = relationScore;
        this.markDirty();
    }

    public long getEstablishedTime() {
        return establishedTime;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    private void markDirty() {
        this.lastUpdateTime = VillageGenesis.getGameTime();
        InterVillageManager.markDirty();
    }

    public enum RelationStatus {
        HOSTILE, UNFRIENDLY, NEUTRAL, FRIENDLY, ALLIED
    }
}
