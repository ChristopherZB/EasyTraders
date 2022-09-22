package net.macncheezy.easytraders.crafting;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.util.Identifier;

public class StampRecipeSerializer extends ShapelessRecipe.Serializer {

    public static final StampRecipeSerializer INSTANCE = new StampRecipeSerializer();

    @Override
    public ShapelessRecipe read(Identifier identifier, JsonObject jsonObject) {
        return new StampRecipe(super.read(identifier, jsonObject));
    }

    @Override
    public ShapelessRecipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
        return new StampRecipe(super.read(identifier, packetByteBuf));
    }

}
