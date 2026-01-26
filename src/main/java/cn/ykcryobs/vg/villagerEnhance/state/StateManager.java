package cn.ykcryobs.vg.villagerEnhance.state;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

/**
 * 村民状态管理器 负责管理村民的各种状态，包括激活、更新和切换
 *
 * @author llykff
 */
public class StateManager {

    // 当前激活的状态集合 - 使用TreeSet按优先级排序，优先级高的在前
    private final Set<VillagerStates> activeStates = new TreeSet<>(
            (a, b) -> Integer.compare(b.getPriority(), a.getPriority()));

    /**
     * 更新所有状态 检查状态激活条件，更新激活状态，调用状态的tick方法
     *
     */
    public void update() {
        for (VillagerStates state : activeStates) {
            if (state.hasTick()) {
                state.tick();
            }
        }
    }


    /**
     * 激活指定状态
     *
     * @param state 要激活的状态
     */
    public void activateState(VillagerStates state) {
        activeStates.add(state);
        state.onActivate();
    }

    /**
     * 结束指定状态
     *
     * @param state 要结束的状态
     */
    public void deactivateState(VillagerStates state) {
        activeStates.remove(state);
        state.onDeactivate();
    }

    /**
     * 检查村民是否处于指定状态
     *
     * @param state 要检查的状态
     * @return 是否处于该状态
     */
    public boolean hasState(VillagerStates state) {
        return activeStates.contains(state);
    }

    /**
     * 获取当前激活的所有状态
     *
     * @return 当前激活的状态集合，按优先级排序
     */
    public Set<VillagerStates> getActiveStates() {
        return Collections.unmodifiableSet(activeStates);
    }

    /**
     * 获取最高优先级的激活状态 由于activeStates是TreeSet，第一个元素就是优先级最高的
     *
     * @return 最高优先级的状态，如果没有激活状态则返回null
     */
    public VillagerStates getHighestPriorityState() {
        return activeStates.isEmpty() ? null : activeStates.iterator().next();
    }


    /**
     * 清除所有激活状态
     */
    public void clearAllStates() {
        for (VillagerStates state : new TreeSet<>(activeStates)) {
            state.onDeactivate();
        }
        activeStates.clear();
    }

    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        ListTag activeStatesNbt = new ListTag();
        ListTag villagerList = new ListTag();
        for (VillagerStates state : activeStates) {
            activeStatesNbt.add(StringTag.valueOf(state.getName().getString()));

        }
        nbt.put("activeStates", activeStatesNbt);
        return nbt;
    }

    public void deserializeNBT(CompoundTag nbt) {
        ListTag activeStatesNbt = nbt.getList("activeStates", 8);
        for (int i = 0; i < activeStatesNbt.size(); i++) {
            String stateName = activeStatesNbt.getString(i);
            VillagerStates state = VillagerStates.valueOf(stateName);
            activateState(state);
        }
    }

}