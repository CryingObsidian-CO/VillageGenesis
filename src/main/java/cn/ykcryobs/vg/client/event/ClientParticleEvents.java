package cn.ykcryobs.vg.client.event;

import cn.ykcryobs.vg.VillageGenesis;
import cn.ykcryobs.vg.config.ClientConfig;
import cn.ykcryobs.vg.dataComponents.BoundaryScepterComponent;
import cn.ykcryobs.vg.item.BoundaryScepterItem;
import cn.ykcryobs.vg.utils.BoundingBox2D;
import cn.ykcryobs.vg.villageSystem.VillageData;
import cn.ykcryobs.vg.villageSystem.VillageManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.Optional;

/**
 * 客户端事件处理类，用于处理与粒子渲染相关的事件
 *
 * @author llykff
 */
@EventBusSubscriber(modid = VillageGenesis.MOD_ID, value = Dist.CLIENT)
public class ClientParticleEvents {

    private static boolean isParticleActive = false;
    private static int particleTickCounter = 0;

    public static void toggleParticleRender() {
        isParticleActive = !isParticleActive;
        particleTickCounter = ClientConfig.particleRenderInterval.get();
    }

    @SubscribeEvent
    private static void onClientTick(ClientTickEvent.Post event) {
        if (!isParticleActive) {
            return;
        }

        if (particleTickCounter < ClientConfig.particleRenderInterval.get()) {
            particleTickCounter++;
            return;
        }

        Player clientPlayer = Minecraft.getInstance().player;
        if (clientPlayer == null || !clientPlayer.isAlive()) {
            isParticleActive = false;
            return;
        }

        Level level = clientPlayer.level();
        if (level.isClientSide()) {
            ItemStack itemStack = clientPlayer.getMainHandItem();
            if (itemStack.getItem() instanceof BoundaryScepterItem) {
                Optional<VillageData> villageData = VillageManager.getVillageData(
                        BoundaryScepterComponent.getBoundedVillage(itemStack));
                if (villageData.isPresent()) {
                    renderVillageBoundary(level, villageData.get());
                    particleTickCounter = 0;
                    return;
                }
            }
        }

        isParticleActive = false;
    }

    @SubscribeEvent
    private static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        // 切换维度时，强制重置粒子状态为false
        isParticleActive = false;
    }

    /**
     * 渲染村庄边界粒子效果，在边界框的四边采样高度以保持粒子贴近地面
     *
     * @param level       客户端世界实例
     * @param villageData the 村庄数据实例
     */
    private static void renderVillageBoundary(Level level, VillageData villageData) {
        BoundingBox2D box = villageData.getBoundingBox();
        if (box == null) {
            return;
        }

        ParticleOptions particleType = ParticleTypes.HAPPY_VILLAGER;

        int minX = box.getMinX();
        int maxX = box.getMaxX();
        int minZ = box.getMinZ();
        int maxZ = box.getMaxZ();

        for (int x = minX; x <= maxX; x++) {
            int y = getGroundY(level, x, minZ);
            level.addParticle(particleType, true, x, y + 0.5, minZ, 0.0, 0.0, 0.0);

            y = getGroundY(level, x, maxZ);
            level.addParticle(particleType, true, x, y + 0.5, maxZ, 0.0, 0.0, 0.0);
        }

        for (int z = minZ; z <= maxZ; z++) {
            int y = getGroundY(level, minX, z);
            level.addParticle(particleType, true, minX, y + 0.5, z, 0.0, 0.0, 0.0);

            y = getGroundY(level, maxX, z);
            level.addParticle(particleType, true, maxX, y + 0.5, z, 0.0, 0.0, 0.0);
        }

    }

    /**
     * 获取指定位置的地面Y坐标
     *
     * @param level 客户端世界实例
     * @param x     X坐标
     * @param z     Z坐标
     * @return 地面Y坐标
     */
    private static int getGroundY(Level level, int x, int z) {
        BlockPos pos = new BlockPos(x, 0, z);
        return level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, pos).getY();
    }
}
