package net.macncheezy.easytraders.mixin;

import net.minecraft.entity.passive.VillagerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(VillagerEntity.class)
public interface VillagerEntityInvoker {
    //@Invoker("fillRecipes")
    //public void fillRecipes();
}
