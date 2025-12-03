package cn.ykcryobs.vg.datagen;

import cn.ykcryobs.vg.VillageGenesis;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author llykff
 */
public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {

    public ModRecipeProvider(PackOutput output,
            CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
    }

    protected static <T extends AbstractCookingRecipe> void modOreCooking(RecipeOutput recipeOutput,
            RecipeSerializer<T> serializer, AbstractCookingRecipe.Factory<T> recipeFactory,
            List<ItemLike> ingredients, RecipeCategory category, ItemLike result, float experience,
            int cookingTime, String group, String suffix) {
        for (ItemLike itemlike : ingredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), category, result,
                            experience, cookingTime, serializer, recipeFactory).group(group)
                    .unlockedBy(getHasName(itemlike), has(itemlike)).save(recipeOutput,
                            VillageGenesis.MOD_ID + ":" + getItemName(result) + suffix + "_" + getItemName(
                                    itemlike));
        }
    }
}
