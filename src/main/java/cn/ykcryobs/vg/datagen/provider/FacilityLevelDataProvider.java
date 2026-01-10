package cn.ykcryobs.vg.datagen.provider;

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
 * 设施等级数据提供器 用于生成各种设施类型的等级数据配置文件
 *
 * @author llykff
 */
public abstract class FacilityLevelDataProvider implements DataProvider {

    private final PackOutput output;
    private final String modid;
    private final Map<String, JsonObject> levelData = new TreeMap<>();

    public FacilityLevelDataProvider(PackOutput output, String modid) {
        this.output = output;
        this.modid = modid;
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
        addFacilityLevelData();

        CompletableFuture<?>[] futures = new CompletableFuture<?>[levelData.size()];
        int index = 0;

        for (Map.Entry<String, JsonObject> entry : levelData.entrySet()) {
            futures[index++] = saveLevelJson(cache, entry.getKey(), entry.getValue());
        }

        return CompletableFuture.allOf(futures);
    }

    protected abstract void addFacilityLevelData();

    @Override
    public @NotNull String getName() {
        return "Facility Level Data for mod: " + modid;
    }

    /**
     * 保存设施等级数据JSON文件
     */
    private CompletableFuture<?> saveLevelJson(CachedOutput cache, String name, JsonObject json) {

        Path outputPath = this.output.getOutputFolder(PackOutput.Target.DATA_PACK).resolve(this.modid)
                .resolve("village/facilities_level").resolve(name + ".json");

        return DataProvider.saveStable(cache, json, outputPath);
    }

    /**
     * 添加设施等级数据
     *
     * @param jsonObject 设施等级数据的JSON对象
     */
    public void addLevelData(JsonObject jsonObject) {
        levelData.put(jsonObject.get("facility_type").getAsString(), jsonObject);
    }

    /**
     * 添加设施等级数据构建器生成的设施等级数据
     *
     * @param builder 设施等级数据构建器实例
     */
    public void addLevelData(FacilityLevelDataBuilder builder) {
        addLevelData(builder.build());
    }

    /**
     * 创建设施等级数据构建器
     *
     * @return 设施等级数据构建器实例
     */
    protected FacilityLevelDataBuilder createFacilityLevelData() {
        return new FacilityLevelDataBuilder(this.modid);
    }

    /**
     * 设施等级数据构建器
     */
    public static class FacilityLevelDataBuilder {

        private final JsonObject json = new JsonObject();
        private final JsonObject levelsObject = new JsonObject();
        private final String modid;

        public FacilityLevelDataBuilder(String modid) {
            this.modid = modid;
        }

        public FacilityLevelDataBuilder facilityType(String facilityType) {
            json.addProperty("facility_type", facilityType);
            return this;
        }

        public FacilityLevelDataBuilder addLevel(int level, String structure) {
            json.addProperty(String.valueOf(level), resolveStructure(structure));
            return this;
        }

        private String resolveStructure(String structure) {
            return this.modid + ":" + structure;
        }

        public JsonObject build() {
            return json;
        }
    }
}