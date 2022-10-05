package net.macncheezy.easytraders.items;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.math.Vector3d;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class VillagerTrap extends Item {
    public VillagerTrap(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        ItemStack itemStack = context.getStack();
        NbtCompound nbt = itemStack.getNbt();
        World world = context.getWorld();
        Vec3d pos = context.getHitPos();
        Direction dir = context.getSide();

        if (nbt != null && !nbt.isEmpty() && nbt.contains("IsFull")) {
            boolean IsFull = nbt.getBoolean("IsFull");
            if (IsFull) {
                context.getPlayer().playSound(SoundEvents.BLOCK_BAMBOO_FALL, 1.0F, 1.0F);
                VillagerEntity entity = EntityType.VILLAGER.create(world);
                float planeOffset = 0.7f;
                Vector3d offset = new Vector3d(dir.getOffsetX() * planeOffset, dir == Direction.DOWN ? -2 : 0, dir.getOffsetZ() * planeOffset);
                ((MobEntity) entity).refreshPositionAndAngles(pos.getX() + offset.x, pos.getY() + offset.y, pos.getZ() + offset.z, 0.0F, 0.0F);

                world.spawnEntity((Entity) entity);

                NbtCompound villagerData = itemStack.getSubNbt("villager");
                entity.readCustomDataFromNbt(villagerData);

                nbt.putBoolean("IsFull", false);
                itemStack.setNbt(nbt);
                NbtCompound nbtEmpty = new NbtCompound();
                itemStack.setSubNbt("villager", nbtEmpty);
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    public ActionResult pickUpVillager(ItemStack itemStack, PlayerEntity user, LivingEntity entity) {
        boolean AlreadyFull = false;
        NbtCompound nbt = itemStack.getNbt();
        if (nbt != null && !nbt.isEmpty() && nbt.contains("IsFull")) {
            AlreadyFull = nbt.getBoolean("IsFull");
        }

        if (!AlreadyFull && entity.getType() == EntityType.VILLAGER && user.isSneaking()) {
            user.playSound(SoundEvents.BLOCK_ANVIL_USE, 1.0F, 1.0F);

            NbtCompound fullNbt = new NbtCompound();
            fullNbt.putBoolean("IsFull", true);
            itemStack.setNbt(fullNbt);

            NbtCompound villagerNbt = new NbtCompound();
            entity.writeCustomDataToNbt(villagerNbt);
            entity.remove(Entity.RemovalReason.DISCARDED);
            itemStack.setSubNbt("villager", villagerNbt);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        NbtCompound nbt = itemStack.getNbt();
        if (nbt != null && !nbt.isEmpty() && nbt.contains("IsFull") && nbt.getBoolean("IsFull")) {
            tooltip.add(Text.translatable("item.easytraders.villager_trap.tooltip", "Right click to place down"));
            tooltip.add(Text.translatable("item.easytraders.villager_trap.tooltip", "Full of a villager").formatted(Formatting.GREEN));
        } else {
            tooltip.add(Text.translatable("item.easytraders.villager_trap.tooltip", "Shift-right click to pick up"));
            tooltip.add(Text.translatable("item.easytraders.villager_trap.tooltip", "Empty").formatted(Formatting.RED));
        }
    }
}
