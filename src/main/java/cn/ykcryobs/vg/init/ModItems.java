package cn.ykcryobs.vg.init;

import cn.ykcryobs.vg.VillageGenesis;
import cn.ykcryobs.vg.item.BoundaryScepterItem;
import cn.ykcryobs.vg.item.currency.CopperCoinItem;
import cn.ykcryobs.vg.item.currency.FiftyYuanPaperItem;
import cn.ykcryobs.vg.item.currency.FiveYuanPaperItem;
import cn.ykcryobs.vg.item.currency.GoldCoinItem;
import cn.ykcryobs.vg.item.currency.OneHundredYuanPaperItem;
import cn.ykcryobs.vg.item.currency.OneYuanPaperItem;
import cn.ykcryobs.vg.item.currency.SilverCoinItem;
import cn.ykcryobs.vg.item.currency.TenYuanPaperItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * 物品注册器
 *
 * @author llykff
 */
public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(VillageGenesis.MOD_ID);

    public static final Supplier<Item> BOUNDARY_SCEPTER = ITEMS.register("boundary_scepter",
            BoundaryScepterItem::new);

    // 金属硬币
    public static final Supplier<Item> COPPER_COIN = ITEMS.register("copper_coin",
            CopperCoinItem::new);
    public static final Supplier<Item> SILVER_COIN = ITEMS.register("silver_coin",
            SilverCoinItem::new);
    public static final Supplier<Item> GOLD_COIN = ITEMS.register("gold_coin",
            GoldCoinItem::new);

    // 基础纸币
    public static final Supplier<Item> ONE_YUAN_PAPER = ITEMS.register("one_yuan_paper",
            OneYuanPaperItem::new);
    public static final Supplier<Item> FIVE_YUAN_PAPER = ITEMS.register("five_yuan_paper",
            FiveYuanPaperItem::new);
    public static final Supplier<Item> TEN_YUAN_PAPER = ITEMS.register("ten_yuan_paper",
            TenYuanPaperItem::new);

    // 现代纸币
    public static final Supplier<Item> FIFTY_YUAN_PAPER = ITEMS.register("fifty_yuan_paper",
            FiftyYuanPaperItem::new);
    public static final Supplier<Item> ONE_HUNDRED_YUAN_PAPER = ITEMS.register("one_hundred_yuan_paper",
            OneHundredYuanPaperItem::new);

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
