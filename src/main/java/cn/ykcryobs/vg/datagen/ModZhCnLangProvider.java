package cn.ykcryobs.vg.datagen;

import cn.ykcryobs.vg.VillageGenesis;
import cn.ykcryobs.vg.init.ModBlocks;
import cn.ykcryobs.vg.init.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

/**
 * 中文语言提供器
 *
 * @author llykff
 */
public class ModZhCnLangProvider extends LanguageProvider {

    public ModZhCnLangProvider(PackOutput output) {
        super(output, VillageGenesis.MOD_ID, "zh_cn");
    }

    @Override
    protected void addTranslations() {

        addVillageNamePartTranslations();

        // 配置文件翻译
        add("village_genesis.configuration.title", "村庄起源配置");
        add("village_genesis.configuration.section.village.genesis.common.toml", "通用设置");
        add("village_genesis.configuration.section.village.genesis.common.toml.title", "通用设置");
        add("village_genesis.configuration.section.village.genesis.client.toml", "客户端设置");
        add("village_genesis.configuration.section.village.genesis.client.toml.title", "客户端设置");
        add("village_genesis.configuration.section.village.genesis.server.toml", "服务器设置");
        add("village_genesis.configuration.section.village.genesis.server.toml.title", "服务器设置");

        // client side
        add("village_genesis.configuration.particleRenderInterval", "粒子渲染间隔");
        add("village_genesis.configuration.particleRenderInterval.tooltip", "渲染粒子的时间间隔（单位：tick）");

        // server side
        add("village_genesis.configuration.facilityDurability", "设施耐久度配置");
        add("village_genesis.configuration.durabilityInspectionInterval", "设施耐久度检查间隔");
        add("village_genesis.configuration.durabilityInspectionInterval.tooltip",
                "设施耐久度检查的时间间隔（单位：tick）");
        add("village_genesis.configuration.durabilityThresholdToNeedMaintenance", "设施耐久度需要维护的阈值");
        add("village_genesis.configuration.durabilityThresholdToNeedMaintenance.tooltip",
                "设施耐久度低于此阈值时，需要维护。（0-100)%");

        // blocks
        addBlock(ModBlocks.VILLAGE_INFO_PANEL, "村庄信息面板");

        // items
        addItem(ModItems.BOUNDARY_SCEPTER, "边界权杖");

        // tooltips
        add("tooltip.village_genesis.boundary_scepter.bound_village", "已绑定村庄");
        add("tooltip.village_genesis.boundary_scepter.not_bound", "未绑定村庄");
        add("tooltip.village_genesis.boundary_scepter.bound_unknown", "未知村庄");

        // messages
        add("message.village_genesis.not_in_village", "你不在任何村庄中");

        // evolution stages
        add("village.village_genesis.evolution_stage.primitive", "原始部落");
        add("village.village_genesis.evolution_stage.agricultural", "农业村庄");
        add("village.village_genesis.evolution_stage.handicraft", "手工业城镇");
        add("village.village_genesis.evolution_stage.commercial", "商业都市");
        add("village.village_genesis.evolution_stage.industrial", "工业城市");
        add("village.village_genesis.evolution_stage.modern", "现代都市");

        // village status
        add("village.village_genesis.status.developing", "发展中");
        add("village.village_genesis.status.stagnant", "停滞中");
        add("village.village_genesis.status.declining", "衰退中");
        add("village.village_genesis.status.abandoned", "废弃");

        // commands
        add("commands.village_genesis.economy.invalidTrader", "无效的交易对象 : %s");

        // GUI translations for Village Info Panel
        add("gui.village_genesis.village_info_title", "村庄信息面板");
        add("gui.village_genesis.close", "关闭");
        add("gui.village_genesis.loading", "加载中...");
        add("gui.village_genesis.info_category.basic_info", "基本信息");
        add("gui.village_genesis.info_category.tooltip.basic_info", "查看村庄的基本信息和概况");
        add("gui.village_genesis.info_category.villager_list", "村民列表");
        add("gui.village_genesis.info_category.tooltip.villager_list", "浏览村庄内村民的信息");
        add("gui.village_genesis.info_category.facility_list", "设施列表");
        add("gui.village_genesis.info_category.tooltip.facility_list", "查看村庄的建筑列表");
        add("village_info_panel.village_genesis.village_name", "村庄名称 : %s");
        add("village_info_panel.village_genesis.village_level", "村庄等级 : %s");
        add("village_info_panel.village_genesis.village_exp", "村庄经验 : %s");
        add("village_info_panel.village_genesis.village_next_exp", "下一级需要的经验 : %s");
        add("village_info_panel.village_genesis.village_population", "村庄人口 : %s");
        add("village_info_panel.village_genesis.village_status", "村庄状态 : %s");
        add("village_info_panel.village_genesis.facility_count", "建筑数量 : %s");
    }

    private void addVillageNamePartTranslations() {
        add("village_genesis.configuration.villageNameGenerationMode", "村庄名称生成模式");
        add("village_genesis.configuration.villageSuffixSavanna", "热带草原村庄后缀");
        add("village_genesis.configuration.villageCorePlains", "平原村庄核心词");
        add("village_genesis.configuration.villageCoreSavanna", "热带草原村庄核心词");
        add("village_genesis.configuration.villageSuffixTaiga", "针叶林村庄后缀");
        add("village_genesis.configuration.villageCoreTaiga", "针叶林村庄核心词");
        add("village_genesis.configuration.villageSuffixDesert", "沙漠村庄后缀");
        add("village_genesis.configuration.villageSuffixSnowy", "雪原村庄后缀");
        add("village_genesis.configuration.villageSuffixPlains", "平原村庄后缀");
        add("village_genesis.configuration.villagePrefixes", "村庄前缀");
        add("village_genesis.configuration.villageCoreSnowy", "雪原村庄核心词");
        add("village_genesis.configuration.villageCoreDesert", "沙漠村庄核心词");
        add("village_genesis.configuration.villageNameGeneration", "村庄名称生成");
        add("village_genesis.configuration.villageCorePlains.button", "平原村庄核心词");
        add("village_genesis.configuration.villageCoreTaiga.button", "针叶林村庄核心词");
        add("village_genesis.configuration.villageCoreDesert.tooltip", "沙漠村庄核心词列表");
        add("village_genesis.configuration.villageSuffixSavanna.tooltip", "热带草原村庄后缀词列表");
        add("village_genesis.configuration.villageSuffixSnowy.button", "雪原村庄后缀");
        add("village_genesis.configuration.villageSuffixDesert.button", "沙漠村庄后缀");
        add("village_genesis.configuration.villageSuffixSavanna.button", "热带草原村庄后缀");
        add("village_genesis.configuration.villageSuffixTaiga.button", "针叶林村庄后缀");
        add("village_genesis.configuration.villageNameGeneration.button", "村庄名称生成");
        add("village_genesis.configuration.villageSuffixPlains.button", "平原村庄后缀");
        add("village_genesis.configuration.villageCoreSnowy.button", "雪原村庄核心词");
        add("village_genesis.configuration.villageCoreSnowy.tooltip", "雪原村庄核心词列表");
        add("village_genesis.configuration.villageCoreTaiga.tooltip", "针叶林村庄核心词列表");
        add("village_genesis.configuration.villageSuffixPlains.tooltip", "平原村庄后缀词列表");
        add("village_genesis.configuration.villagePrefixes.tooltip", "村庄前缀词列表");
        add("village_genesis.configuration.villageCoreDesert.button", "沙漠村庄核心词");
        add("village_genesis.configuration.villagePrefixes.button", "村庄前缀");
        add("village_genesis.configuration.villageCoreSavanna.button", "热带草原村庄核心词");
        add("village_genesis.configuration.villageSuffixDesert.tooltip", "沙漠村庄后缀词列表");
        add("village_genesis.configuration.villageSuffixSnowy.tooltip", "雪原村庄后缀词列表");
        add("village_genesis.configuration.villageSuffixTaiga.tooltip", "针叶林村庄后缀词列表");
        add("village_genesis.configuration.villageCoreSavanna.tooltip", "热带草原村庄核心词列表");
        add("village_genesis.configuration.villageNameGeneration.tooltip", "村庄名称生成设置");
        add("village_genesis.configuration.villageCorePlains.tooltip", "平原村庄核心词列表");
        add("village_genesis.configuration.villageNameGenerationMode.tooltip", "村庄名称生成模式设置");
        // 村庄前缀
        add("village.village_genesis.name.prefix.new", "新");
        add("village.village_genesis.name.prefix.old", "旧");
        add("village.village_genesis.name.prefix.great", "大");
        add("village.village_genesis.name.prefix.little", "小");
        add("village.village_genesis.name.prefix.high", "高");
        add("village.village_genesis.name.prefix.low", "低");
        add("village.village_genesis.name.prefix.north", "北");
        add("village.village_genesis.name.prefix.south", "南");
        add("village.village_genesis.name.prefix.east", "东");
        add("village.village_genesis.name.prefix.west", "西");
        add("village.village_genesis.name.prefix.royal", "皇家");
        add("village.village_genesis.name.prefix.ancient", "古");
        add("village.village_genesis.name.prefix.noble", "贵族");

        // 平原核心词
        add("village.village_genesis.name.core.green", "绿地");
        add("village.village_genesis.name.core.meadow", "牧场");
        add("village.village_genesis.name.core.field", "田地");
        add("village.village_genesis.name.core.farm", "农场");
        add("village.village_genesis.name.core.grange", "农庄");
        add("village.village_genesis.name.core.acre", "英亩");
        add("village.village_genesis.name.core.vale", "山谷");
        add("village.village_genesis.name.core.hill", "丘陵");
        add("village.village_genesis.name.core.brook", "溪流");
        add("village.village_genesis.name.core.crossing", "十字路口");
        add("village.village_genesis.name.core.ford", "浅滩");
        add("village.village_genesis.name.core.bridge", "桥梁");
        add("village.village_genesis.name.core.plain", "平原");
        add("village.village_genesis.name.core.grass", "草地");

        // 沙漠核心词
        add("village.village_genesis.name.core.dune", "沙丘");
        add("village.village_genesis.name.core.dunes", "沙丘");
        add("village.village_genesis.name.core.oasis", "绿洲");
        add("village.village_genesis.name.core.harbor", "港口");
        add("village.village_genesis.name.core.haven", "避风港");
        add("village.village_genesis.name.core.rest", "休息站");
        add("village.village_genesis.name.core.shade", "荫蔽处");
        add("village.village_genesis.name.core.shelter", "庇护所");
        add("village.village_genesis.name.core.refuge", "避难所");
        add("village.village_genesis.name.core.camp", "营地");
        add("village.village_genesis.name.core.post", "哨站");
        add("village.village_genesis.name.core.station", "驿站");
        add("village.village_genesis.name.core.fort", "堡垒");
        add("village.village_genesis.name.core.tower", "塔楼");
        add("village.village_genesis.name.core.watch", "瞭望台");
        add("village.village_genesis.name.core.outpost", "前哨");

        // 雪原核心词
        add("village.village_genesis.name.core.frost", "霜");
        add("village.village_genesis.name.core.ice", "冰");
        add("village.village_genesis.name.core.snow", "雪");
        add("village.village_genesis.name.core.blizzard", "暴风雪");
        add("village.village_genesis.name.core.winter", "冬");
        add("village.village_genesis.name.core.freeze", "冰冻");
        add("village.village_genesis.name.core.chill", "寒");
        add("village.village_genesis.name.core.crystal", "水晶");
        add("village.village_genesis.name.core.glacier", "冰川");
        add("village.village_genesis.name.core.peak", "山峰");
        add("village.village_genesis.name.core.summit", "山顶");
        add("village.village_genesis.name.core.ridge", "山脊");
        add("village.village_genesis.name.core.slope", "山坡");
        add("village.village_genesis.name.core.warmth", "温暖");

        // 针叶林核心词
        add("village.village_genesis.name.core.pine", "松林");
        add("village.village_genesis.name.core.spruce", "云杉");
        add("village.village_genesis.name.core.fir", "冷杉");
        add("village.village_genesis.name.core.hemlock", "铁杉");
        add("village.village_genesis.name.core.forest", "森林");
        add("village.village_genesis.name.core.grove", "小树林");
        add("village.village_genesis.name.core.wood", "林地");
        add("village.village_genesis.name.core.thicket", "灌木丛");
        add("village.village_genesis.name.core.branch", "树枝");
        add("village.village_genesis.name.core.needle", "针叶");
        add("village.village_genesis.name.core.timber", "伐木场");
        add("village.village_genesis.name.core.lumber", "木材场");
        add("village.village_genesis.name.core.moss", "苔藓");
        add("village.village_genesis.name.core.fern", "蕨类");
        add("village.village_genesis.name.core.wilds", "荒野");

        // 稀树草原核心词
        add("village.village_genesis.name.core.acacia", "金合欢");
        add("village.village_genesis.name.core.savanna", "热带草原");
        add("village.village_genesis.name.core.plains", "平原");
        add("village.village_genesis.name.core.grassland", "草地");
        add("village.village_genesis.name.core.steppe", "北美草原");
        add("village.village_genesis.name.core.prairie", "大草原");
        add("village.village_genesis.name.core.outback", "内陆");
        add("village.village_genesis.name.core.scrub", "灌木丛");
        add("village.village_genesis.name.core.bush", "灌木丛");
        add("village.village_genesis.name.core.veld", "非洲草原");
        add("village.village_genesis.name.core.table", "台地");
        add("village.village_genesis.name.core.plateau", "高原");
        add("village.village_genesis.name.core.mesa", "平顶山");
        add("village.village_genesis.name.core.butte", "孤山");
        add("village.village_genesis.name.core.view", "观景");

        // 村庄后缀
        add("village.village_genesis.name.suffix.village", "村");
        add("village.village_genesis.name.suffix.hamlet", "小村");
        add("village.village_genesis.name.suffix.settlement", "定居点");
        add("village.village_genesis.name.suffix.town", "镇");
        add("village.village_genesis.name.suffix.fort", "堡");
        add("village.village_genesis.name.suffix.tower", "塔");
        add("village.village_genesis.name.suffix.watch", "瞭望台");
        add("village.village_genesis.name.suffix.outpost", "前哨");
        add("village.village_genesis.name.suffix.station", "驿站");
        add("village.village_genesis.name.suffix.post", "哨站");
        add("village.village_genesis.name.suffix.haven", "港");
        add("village.village_genesis.name.suffix.shelter", "庇护所");
        add("village.village_genesis.name.suffix.lodge", "小屋");
        add("village.village_genesis.name.suffix.camp", "营地");
    }
}
