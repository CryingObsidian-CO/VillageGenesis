package cn.ykcryobs.vg.datagen;

import cn.ykcryobs.vg.VillageGenesis;
import cn.ykcryobs.vg.init.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

/**
 * 英文语言提供器
 *
 * @author llykff
 */
public class ModEnUsLangProvider extends LanguageProvider {

    public ModEnUsLangProvider(PackOutput output) {
        super(output, VillageGenesis.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        addVillageNamePartTranslations();

        // 配置文件翻译
        add("village_genesis.configuration.section.village.genesis.common.toml", "Village Genesis Common");
        add("village_genesis.configuration.section.village.genesis.common.toml.title",
                "Village Genesis Common Settings");
        add("village_genesis.configuration.section.village.genesis.client.toml", "Client Settings");
        add("village_genesis.configuration.section.village.genesis.client.toml.title", "Client Settings");
        add("village_genesis.configuration.section.village.genesis.server.toml", "Server Settings");
        add("village_genesis.configuration.section.village.genesis.server.toml.title", "Server Settings");

        // client side
        add("village_genesis.configuration.particleRenderInterval", "Particle Render Interval");
        add("village_genesis.configuration.particleRenderInterval.tooltip",
                "The interval in ticks to render particles.");

        // server side
        add("village_genesis.configuration.facilityDurability", "Facility Durability Settings");
        add("village_genesis.configuration.durabilityInspectionInterval",
                "Facility Durability Inspection Interval");
        add("village_genesis.configuration.durabilityInspectionInterval.tooltip",
                "The interval in ticks to inspect facility durability.");
        add("village_genesis.configuration.durabilityThresholdToNeedMaintenance",
                "Facility Durability Threshold to Need Maintenance");
        add("village_genesis.configuration.durabilityThresholdToNeedMaintenance.tooltip",
                "The durability threshold to need maintenance.(0-100)%");

        // items
        addItem(ModItems.BOUNDARY_SCEPTER, "Boundary Scepter");

        // tooltips
        add("tooltip.village_genesis.boundary_scepter.bound_village", "Bound Village");
        add("tooltip.village_genesis.boundary_scepter.not_bound", "Not Bound");
        add("tooltip.village_genesis.boundary_scepter.bound_unknown", "Bound Unknown Village");

        //messages
        add("message.village_genesis.not_in_village", "You are not in any village.");
    }

    private void addVillageNamePartTranslations() {
        add("village_genesis.configuration.villageNameGenerationMode", "Village Name Generation Mode");
        add("village_genesis.configuration.villageSuffixSavanna", "Savanna Village Suffix");
        add("village_genesis.configuration.villageCorePlains", "Plains Village Core");
        add("village_genesis.configuration.villageCoreSavanna", "Savanna Village Core");
        add("village_genesis.configuration.villageSuffixTaiga", "Taiga Village Suffix");
        add("village_genesis.configuration.villageCoreTaiga", "Taiga Village Core");
        add("village_genesis.configuration.villageSuffixDesert", "Desert Village Suffix");
        add("village_genesis.configuration.villageSuffixSnowy", "Snowy Village Suffix");
        add("village_genesis.configuration.villageSuffixPlains", "Plains Village Suffix");
        add("village_genesis.configuration.villagePrefixes", "Village Prefixes");
        add("village_genesis.configuration.villageCoreSnowy", "Snowy Village Core");
        add("village_genesis.configuration.villageCoreDesert", "Desert Village Core");
        add("village_genesis.configuration.villageNameGeneration", "Village Name Generation");
        add("village_genesis.configuration.villageCorePlains.button", "Plains Village Core");
        add("village_genesis.configuration.villageCoreTaiga.button", "Taiga Village Core");
        add("village_genesis.configuration.villageCoreDesert.tooltip", "Desert Village Core Words");
        add("village_genesis.configuration.villageSuffixSavanna.tooltip", "Savanna Village Suffix Words");
        add("village_genesis.configuration.villageSuffixSnowy.button", "Snowy Village Suffix");
        add("village_genesis.configuration.villageSuffixDesert.button", "Desert Village Suffix");
        add("village_genesis.configuration.title", "Village Genesis Configuration");

        add("village_genesis.configuration.villageSuffixSavanna.button", "Savanna Village Suffix");
        add("village_genesis.configuration.villageSuffixTaiga.button", "Taiga Village Suffix");
        add("village_genesis.configuration.villageNameGeneration.button", "Village Name Generation");
        add("village_genesis.configuration.villageSuffixPlains.button", "Plains Village Suffix");

        add("village_genesis.configuration.villageCoreSnowy.button", "Snowy Village Core");
        add("village_genesis.configuration.villageCoreSnowy.tooltip", "Snowy Village Core Words");
        add("village_genesis.configuration.villageCoreTaiga.tooltip", "Taiga Village Core Words");
        add("village_genesis.configuration.villageSuffixPlains.tooltip", "Plains Village Suffix Words");
        add("village_genesis.configuration.villagePrefixes.tooltip", "Village Prefix Words");
        add("village_genesis.configuration.villageCoreDesert.button", "Desert Village Core");
        add("village_genesis.configuration.villagePrefixes.button", "Village Prefixes");
        add("village_genesis.configuration.villageCoreSavanna.button", "Savanna Village Core");
        add("village_genesis.configuration.villageSuffixDesert.tooltip", "Desert Village Suffix Words");
        add("village_genesis.configuration.villageSuffixSnowy.tooltip", "Snowy Village Suffix Words");
        add("village_genesis.configuration.villageSuffixTaiga.tooltip", "Taiga Village Suffix Words");
        add("village_genesis.configuration.villageCoreSavanna.tooltip", "Savanna Village Core Words");
        add("village_genesis.configuration.villageNameGeneration.tooltip",
                "Village Name Generation Settings");
        add("village_genesis.configuration.villageCorePlains.tooltip", "Plains Village Core Words");
        add("village_genesis.configuration.villageNameGenerationMode.tooltip",
                "Village Name Generation Mode Settings");

        // 村庄名前缀
        add("village.village_genesis.name.prefix.new", "new ");
        add("village.village_genesis.name.prefix.old", "old ");
        add("village.village_genesis.name.prefix.great", "great ");
        add("village.village_genesis.name.prefix.little", "little ");
        add("village.village_genesis.name.prefix.high", "high ");
        add("village.village_genesis.name.prefix.low", "low ");
        add("village.village_genesis.name.prefix.north", "north ");
        add("village.village_genesis.name.prefix.south", "south ");
        add("village.village_genesis.name.prefix.east", "east ");
        add("village.village_genesis.name.prefix.west", "west ");
        add("village.village_genesis.name.prefix.royal", "royal ");
        add("village.village_genesis.name.prefix.ancient", "ancient ");
        add("village.village_genesis.name.prefix.noble", "noble ");

        // 平原核心词
        add("village.village_genesis.name.core.green", "Green");
        add("village.village_genesis.name.core.meadow", "Meadow");
        add("village.village_genesis.name.core.field", "Field");
        add("village.village_genesis.name.core.farm", "Farm");
        add("village.village_genesis.name.core.grange", "Grange");
        add("village.village_genesis.name.core.acre", "Acre");
        add("village.village_genesis.name.core.vale", "Vale");
        add("village.village_genesis.name.core.hill", "Hill");
        add("village.village_genesis.name.core.brook", "Brook");
        add("village.village_genesis.name.core.crossing", "Crossing");
        add("village.village_genesis.name.core.ford", "Ford");
        add("village.village_genesis.name.core.bridge", "Bridge");
        add("village.village_genesis.name.core.plain", "Plain");
        add("village.village_genesis.name.core.grass", "Grass");

        // 沙漠核心词
        add("village.village_genesis.name.core.dune", "Dune");
        add("village.village_genesis.name.core.dunes", "Dunes");
        add("village.village_genesis.name.core.oasis", "Oasis");
        add("village.village_genesis.name.core.harbor", "Harbor");
        add("village.village_genesis.name.core.haven", "Haven");
        add("village.village_genesis.name.core.rest", "Rest");
        add("village.village_genesis.name.core.shade", "Shade");
        add("village.village_genesis.name.core.shelter", "Shelter");
        add("village.village_genesis.name.core.refuge", "Refuge");
        add("village.village_genesis.name.core.camp", "Camp");
        add("village.village_genesis.name.core.post", "Post");
        add("village.village_genesis.name.core.station", "Station");
        add("village.village_genesis.name.core.fort", "Fort");
        add("village.village_genesis.name.core.tower", "Tower");
        add("village.village_genesis.name.core.watch", "Watch");
        add("village.village_genesis.name.core.outpost", "Outpost");

        // 雪原核心词
        add("village.village_genesis.name.core.frost", "Frost");
        add("village.village_genesis.name.core.ice", "Ice");
        add("village.village_genesis.name.core.snow", "Snow");
        add("village.village_genesis.name.core.blizzard", "Blizzard");
        add("village.village_genesis.name.core.winter", "Winter");
        add("village.village_genesis.name.core.freeze", "Freeze");
        add("village.village_genesis.name.core.chill", "Chill");
        add("village.village_genesis.name.core.crystal", "Crystal");
        add("village.village_genesis.name.core.glacier", "Glacier");
        add("village.village_genesis.name.core.peak", "Peak");
        add("village.village_genesis.name.core.summit", "Summit");
        add("village.village_genesis.name.core.ridge", "Ridge");
        add("village.village_genesis.name.core.slope", "Slope");
        add("village.village_genesis.name.core.warmth", "Warmth");

        // 针叶林核心词
        add("village.village_genesis.name.core.pine", "Pine");
        add("village.village_genesis.name.core.spruce", "Spruce");
        add("village.village_genesis.name.core.fir", "Fir");
        add("village.village_genesis.name.core.hemlock", "Hemlock");
        add("village.village_genesis.name.core.forest", "Forest");
        add("village.village_genesis.name.core.grove", "Grove");
        add("village.village_genesis.name.core.wood", "Wood");
        add("village.village_genesis.name.core.thicket", "Thicket");
        add("village.village_genesis.name.core.branch", "Branch");
        add("village.village_genesis.name.core.needle", "Needle");
        add("village.village_genesis.name.core.timber", "Timber");
        add("village.village_genesis.name.core.lumber", "Lumber");
        add("village.village_genesis.name.core.moss", "Moss");
        add("village.village_genesis.name.core.fern", "Fern");
        add("village.village_genesis.name.core.wilds", "Wilds");

        // 稀树草原核心词
        add("village.village_genesis.name.core.acacia", "Acacia");
        add("village.village_genesis.name.core.savanna", "Savanna");
        add("village.village_genesis.name.core.plains", "Plains");
        add("village.village_genesis.name.core.grassland", "Grassland");
        add("village.village_genesis.name.core.steppe", "Steppe");
        add("village.village_genesis.name.core.prairie", "Prairie");
        add("village.village_genesis.name.core.outback", "Outback");
        add("village.village_genesis.name.core.scrub", "Scrub");
        add("village.village_genesis.name.core.bush", "Bush");
        add("village.village_genesis.name.core.veld", "Veld");
        add("village.village_genesis.name.core.table", "Table");
        add("village.village_genesis.name.core.plateau", "Plateau");
        add("village.village_genesis.name.core.mesa", "Mesa");
        add("village.village_genesis.name.core.butte", "Butte");
        add("village.village_genesis.name.core.view", "View");

        // 村庄后缀
        add("village.village_genesis.name.suffix.village", "Village");
        add("village.village_genesis.name.suffix.hamlet", "Hamlet");
        add("village.village_genesis.name.suffix.settlement", "Settlement");
        add("village.village_genesis.name.suffix.town", "Town");
        add("village.village_genesis.name.suffix.fort", "Fort");
        add("village.village_genesis.name.suffix.tower", "Tower");
        add("village.village_genesis.name.suffix.watch", "Watch");
        add("village.village_genesis.name.suffix.outpost", "Outpost");
        add("village.village_genesis.name.suffix.station", "Station");
        add("village.village_genesis.name.suffix.post", "Post");
        add("village.village_genesis.name.suffix.haven", "Haven");
        add("village.village_genesis.name.suffix.shelter", "Shelter");
        add("village.village_genesis.name.suffix.lodge", "Lodge");
        add("village.village_genesis.name.suffix.camp", "Camp");
    }
}
