package net.macncheezy.easytraders.mixin;

import net.macncheezy.easytraders.Main;
import net.macncheezy.easytraders.items.TradeCycler;
import net.macncheezy.easytraders.items.VillagerTrap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.InteractionObserver;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends MerchantEntity implements InteractionObserver, VillagerDataContainer {
    public VillagerEntityMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "interactMob(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;", at = @At("HEAD"), cancellable = true)
    public void interactMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> ci) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.getItem() instanceof VillagerTrap && ((VillagerEntity)(Object)this).isAlive() && !((VillagerEntity)(Object)this).hasCustomer() && !((VillagerEntity)(Object)this).isSleeping()) {
            VillagerTrap villagerTrap = (VillagerTrap)itemStack.getItem();
            ActionResult result = villagerTrap.pickUpVillager(itemStack, player, ((VillagerEntity)(Object)this));
        }
        if (itemStack.getItem() instanceof TradeCycler && ((VillagerEntity)(Object)this).isAlive() && !((VillagerEntity)(Object)this).hasCustomer() && !((VillagerEntity)(Object)this).isSleeping()) {
            if (canCycle() && !this.isClient()) {
                TradeOfferList oldOffers = ((VillagerEntity)(Object)this).getOffers();
                ((VillagerEntity)(Object)this).setOffers(new TradeOfferList());
                this.fillRecipes();
                if (!player.isCreative()) {
                    itemStack.decrement(1);
                }
                TradeOfferList newOffers = ((VillagerEntity)(Object)this).getOffers();
                player.sendMessage(Text.translatable("item.easytraders.trade_cycler.cycle").formatted(Formatting.DARK_AQUA));
                printOffers(player, oldOffers, Formatting.RED);
                player.sendMessage(Text.translatable("item.easytraders.trade_cycler.cycle_to").formatted(Formatting.DARK_AQUA));
                printOffers(player, newOffers, Formatting.GREEN);
                ci.setReturnValue(ActionResult.SUCCESS);
            }

        }
    }

    private void printOffers(PlayerEntity player, TradeOfferList offers, Formatting color) {
        offers.forEach((item) -> {
            if (item.getSecondBuyItem().getCount() != 0) {
                if (item.getSellItem().getItem() == Items.ENCHANTED_BOOK || item.getSellItem().getItem() == Items.FILLED_MAP) {
                    player.sendMessage(Text.translatable("item.easytraders.trade_cycler.cycle_2_special",
                            item.getAdjustedFirstBuyItem().getCount(),
                            item.getOriginalFirstBuyItem().getName(),
                            item.getSecondBuyItem().getCount(),
                            item.getSecondBuyItem().getName(),
                            printEnchantment(item.getSellItem())).formatted(color));
                } else {
                    player.sendMessage(Text.translatable("item.easytraders.trade_cycler.cycle_2",
                            item.getAdjustedFirstBuyItem().getCount(),
                            item.getOriginalFirstBuyItem().getName(),
                            item.getSecondBuyItem().getCount(),
                            item.getSecondBuyItem().getName(),
                            item.getSellItem().getCount(),
                            item.getSellItem().getName()).formatted(color));
                }

            } else {
                player.sendMessage(Text.translatable("item.easytraders.trade_cycler.cycle_1",
                        item.getAdjustedFirstBuyItem().getCount(),
                        item.getOriginalFirstBuyItem().getName(),
                        item.getSellItem().getCount(),
                        item.getSellItem().getName()).formatted(color));
            }
        });
    }

    private Text printEnchantment(ItemStack itemStack) {
        NbtCompound nbt = itemStack.getNbt();

        if (nbt != null && nbt.contains("StoredEnchantments")) {
            NbtList enchantments = nbt.getList("StoredEnchantments", 10);
            if (enchantments != null && enchantments.size() > 0) {
                NbtCompound enchant = enchantments.getCompound(0);
                if (enchant != null && enchant.contains("id")) {
                    String id = enchant.getString("id");
                    Enchantment ench = Registry.ENCHANTMENT.get(Identifier.tryParse(id));
                    return Text.translatable("item.easytraders.trade_cycler.enchantment", Text.translatable(ench.getTranslationKey()), enchant.getInt("lvl"));
                }
            }
        }
        return itemStack.getName();
    }

    private boolean canCycle() {
        return this.getExperience() == 0 && this.getVillagerData().getLevel() == 1 && (this.offers != null && !this.offers.isEmpty());
    }
}
