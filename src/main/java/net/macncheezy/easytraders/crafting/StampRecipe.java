package net.macncheezy.easytraders.crafting;

import net.macncheezy.easytraders.Main;
import net.macncheezy.easytraders.items.DyedTradeStamp;
import net.macncheezy.easytraders.items.MagicTradeStamp;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

public class StampRecipe extends ShapelessRecipe {

    public StampRecipe(ShapelessRecipe shapeless) {
        super(shapeless.getId(), shapeless.getGroup(), shapeless.getOutput(), shapeless.getIngredients());
    }
    public StampRecipe(Identifier id, String group, ItemStack output, DefaultedList<Ingredient> input) {
        super(id, group, output, input);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return StampRecipeSerializer.INSTANCE;
    }

    public DefaultedList<ItemStack> getRemainder(CraftingInventory inventory) {
        DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY);

        for(int i = 0; i < defaultedList.size(); ++i) {
            ItemStack stack = inventory.getStack(i);
            Item item = stack.getItem();
            Main.LOGGER.info("Test1");
            if (item instanceof MagicTradeStamp) {
                stack = stack.copy();
                defaultedList.set(i, stack);
                Main.LOGGER.info("Test2");
            } else if (item instanceof DyedTradeStamp) {
                Main.LOGGER.info("Test3");
                int newDamage = stack.getDamage() + 1;
                if (newDamage < stack.getMaxDamage()) {
                    stack = stack.copy();
                    stack.setDamage(newDamage);
                    defaultedList.set(i, stack);
                } else {
                    defaultedList.set(i, new ItemStack(item.getRecipeRemainder()));
                }
            } else if (item.hasRecipeRemainder()) {
                defaultedList.set(i, new ItemStack(item.getRecipeRemainder()));
            }
        }

        return defaultedList;
    }
}
