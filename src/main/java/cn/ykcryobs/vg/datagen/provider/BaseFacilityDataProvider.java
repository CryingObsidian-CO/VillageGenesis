package cn.ykcryobs.vg.datagen.provider;

import cn.ykcryobs.vg.VillageGenesis;
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
 * 设施类型数据生成器 用于生成各种设施类型的基础数据配置文件
 *
 * @author llykff
 */
public abstract class BaseFacilityDataProvider implements DataProvider {

    private final PackOutput output;
    private final String modid;
    private final Map<String, JsonObject> data = new TreeMap<>();

    public BaseFacilityDataProvider(PackOutput output, String modid) {
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
     * 保存JSON文件
     */
    private CompletableFuture<?> saveJson(CachedOutput cache, String name, JsonObject json) {

        Path outputPath = this.output.getOutputFolder(PackOutput.Target.DATA_PACK)
                .resolve(VillageGenesis.MOD_ID).resolve("facilities").resolve(name + ".json");

        return DataProvider.saveStable(cache, json, outputPath);
    }

    /**
     * 添加设施类型数据
     *
     * @param jsonObject 设施类型数据的JSON对象
     */
    public void add(JsonObject jsonObject) {
        data.put(jsonObject.get("type").getAsString(), jsonObject);
    }

    public void addBaseFacilityType(String typeName, int baseCapacity, int requiredVillageLevel,
            int buildTime) {
        add(createFacilityData().typeName(typeName).baseCapacity(baseCapacity)
                .requiredVillageLevel(requiredVillageLevel).buildTime(buildTime).build());
    }

    /**
     * 创建设施类型数据构建器
     *
     * @return 设施类型数据构建器实例
     */
    protected FacilityDataBuilder createFacilityData() {
        return new FacilityDataBuilder();
    }


    protected static class FacilityDataBuilder {

        private final JsonObject json = new JsonObject();

        public FacilityDataBuilder typeName(String typeName) {
            json.addProperty("type", typeName);
            return this;
        }


        public FacilityDataBuilder baseCapacity(int baseCapacity) {
            json.addProperty("base_capacity", baseCapacity);
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

        public JsonObject build() {
            return json;
        }
    }
}
