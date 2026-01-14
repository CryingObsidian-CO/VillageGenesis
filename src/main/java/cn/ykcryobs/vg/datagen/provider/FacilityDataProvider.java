package cn.ykcryobs.vg.datagen.provider;

import cn.ykcryobs.vg.VillageGenesis;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

/**
 * 设施类型数据提供器 用于生成各种设施类型的基础数据配置文件
 *
 * @author llykff
 */
public abstract class FacilityDataProvider implements DataProvider {

    private final PackOutput output;
    private final String modid;
    private final Map<String, JsonObject> data = new TreeMap<>();

    public FacilityDataProvider(PackOutput output, String modid) {
        this.output = output;
        this.modid = modid;
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
        addFacilityType();

        CompletableFuture<?>[] futures = new CompletableFuture<?>[data.size()];
        int index = 0;

        for (Map.Entry<String, JsonObject> entry : data.entrySet()) {
            futures[index++] = saveJson(cache, entry.getKey(), entry.getValue());
        }

        return CompletableFuture.allOf(futures);
    }

    protected abstract void addFacilityType();

    @Override
    public @NotNull String getName() {
        return "Base Facility Data for mod: " + modid;
    }

    /**
     * 保存设施基础数据JSON文件
     */
    private CompletableFuture<?> saveJson(CachedOutput cache, String name, JsonObject json) {

        Path outputPath = this.output.getOutputFolder(PackOutput.Target.DATA_PACK).resolve(this.modid)
                .resolve(VillageGenesis.MOD_ID).resolve("village/facilities").resolve(name + ".json");

        return DataProvider.saveStable(cache, json, outputPath);
    }

    /**
     * 添加设施类型数据
     *
     * @param jsonObject 设施类型数据的JSON对象
     */
    public void add(JsonObject jsonObject) {
        data.put(jsonObject.get("facility_type").getAsString(), jsonObject);
    }

    /**
     * 添加设施类型数据构建器生成的设施类型数据
     *
     * @param builder 设施类型数据构建器实例
     */
    public void add(FacilityDataBuilder builder) {
        add(builder.build());
    }

    /**
     * 创建设施类型数据构建器
     *
     * @return 设施类型数据构建器实例
     */
    public FacilityDataBuilder createFacilityData() {
        return new FacilityDataBuilder();
    }

    /**
     * 创建最大耐久度数据构建器
     *
     * @return 最大耐久度数据构建器实例
     */
    public MaxDurabilityBuilder createMaxDurabilityBuilder() {
        return new MaxDurabilityBuilder();
    }

    private FacilityDataBuilder createFacility(String facilityTypeIdentifier, String facilityTypeName,
            int baseCapacity, int maxLevel, int requiredVillageLevel, int buildTime,
            JsonArray maxDurability) {
        return createFacilityData().facilityTypeIdentifier(facilityTypeIdentifier)
                .facilityTypeName(facilityTypeName).baseCapacity(baseCapacity).maxLevel(maxLevel)
                .requiredVillageLevel(requiredVillageLevel).buildTime(buildTime).maxDurability(maxDurability);
    }

    public FacilityDataBuilder createVillageCenter(String facilityTypeIdentifier, String facilityTypeName,
            int baseCapacity, int maxLevel, int requiredVillageLevel, int buildTime,
            JsonArray maxDurability) {
        return createFacility(facilityTypeIdentifier, facilityTypeName, baseCapacity, maxLevel,
                requiredVillageLevel, buildTime, maxDurability);
    }

    public FacilityDataBuilder createThatchedHut(String facilityTypeIdentifier, String facilityTypeName,
            int baseCapacity, int maxLevel, int requiredVillageLevel, int buildTime, JsonArray maxDurability,
            boolean isMultiResident) {
        return createFacility(facilityTypeIdentifier, facilityTypeName, baseCapacity, maxLevel,
                requiredVillageLevel, buildTime, maxDurability).isMultiResident(isMultiResident);
    }

    public static class FacilityDataBuilder {

        private final JsonObject json = new JsonObject();

        public FacilityDataBuilder facilityTypeIdentifier(String facilityTypeIdentifier) {
            json.addProperty("type_identifier", facilityTypeIdentifier);
            return this;
        }

        public FacilityDataBuilder facilityTypeName(String facilityTypeName) {
            json.addProperty("facility_type", facilityTypeName);
            return this;
        }

        public FacilityDataBuilder baseCapacity(int baseCapacity) {
            json.addProperty("base_capacity", baseCapacity);
            return this;
        }

        public FacilityDataBuilder maxLevel(int maxLevel) {
            json.addProperty("max_level", maxLevel);
            return this;
        }

        public FacilityDataBuilder requiredVillageLevel(int requiredVillageLevel) {
            json.addProperty("required_village_level", requiredVillageLevel);
            return this;
        }

        public FacilityDataBuilder buildTime(int buildTime) {
            json.addProperty("build_time", buildTime);
            return this;
        }

        public FacilityDataBuilder isMultiResident(boolean isMultiResident) {
            json.addProperty("multi_residential", isMultiResident);
            return this;
        }

        public FacilityDataBuilder maxDurability(JsonArray maxDurability) {
            json.add("max_durability", maxDurability);
            return this;
        }

        public FacilityDataBuilder canBirthBaby(boolean canBirthBaby) {
            json.addProperty("can_birth_baby", canBirthBaby);
            return this;
        }

        public FacilityDataBuilder addProperty(String key, String value) {
            json.addProperty(key, value);
            return this;
        }

        public JsonObject build() {
            return json;
        }
    }

    public static class MaxDurabilityBuilder {

        private final JsonArray json = new JsonArray();

        public static JsonArray createEmptyMaxDurability() {
            return new MaxDurabilityBuilder().build();
        }

        public static JsonArray createMaxDurability(int durability) {
            return new MaxDurabilityBuilder().add(durability).build();
        }

        public static JsonArray createMaxDurability(int durability1, int durability2) {
            return new MaxDurabilityBuilder().add(durability1).add(durability2).build();
        }

        public static JsonArray createMaxDurability(int durability1, int durability2, int durability3) {
            return new MaxDurabilityBuilder().add(durability1).add(durability2).add(durability3).build();
        }

        public static JsonArray createMaxDurability(int durability1, int durability2, int durability3,
                int durability4) {
            return new MaxDurabilityBuilder().add(durability1).add(durability2).add(durability3)
                    .add(durability4).build();
        }

        public static JsonArray createMaxDurability(int durability1, int durability2, int durability3,
                int durability4, int durability5) {
            return new MaxDurabilityBuilder().add(durability1).add(durability2).add(durability3)
                    .add(durability4).add(durability5).build();
        }

        public MaxDurabilityBuilder add(int durability) {
            json.add(durability);
            return this;
        }

        public JsonArray build() {
            return json;
        }
    }
}
