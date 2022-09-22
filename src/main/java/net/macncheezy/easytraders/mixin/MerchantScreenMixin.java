package net.macncheezy.easytraders.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.macncheezy.easytraders.Main;
import net.minecraft.client.gui.screen.ingame.BeaconScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.village.Merchant;
import net.minecraft.village.SimpleMerchant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantScreen.class)
public abstract class MerchantScreenMixin extends HandledScreen<MerchantScreenHandler> {

    public MerchantScreenMixin(MerchantScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    CycleButtonWidget cycle;

    @Inject(method = "init()V", at = @At("TAIL"))
    public void init(CallbackInfo ci) {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        cycle = (CycleButtonWidget)this.addDrawableChild(new CycleButtonWidget(i+111,j+16,16,16, Text.translatable("container.easytraders.villager_screen.cycle")));
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V", at = @At("TAIL"))
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if(cycle.shouldRenderTooltip()) {
            cycle.renderTooltip(matrices, mouseX, mouseY);
            //((MerchantScreen)(Object)this).renderTooltip(matrices, Text.translatable("container.easytraders.villager_screen.cycle"), mouseX, mouseY);
        }

        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    class CycleButtonWidget extends PressableWidget {

        public CycleButtonWidget(int i, int j, int k, int l, Text text) {
            super(i, j, k, l, text);
        }

        public void onPress() {
            if (canCycle(((MerchantScreenHandler)MerchantScreenMixin.this.handler))) {
                Main.LOGGER.info("Leveled");
                Merchant merchant = ((MerchantScreenHandlerMixin)MerchantScreenMixin.this.handler).getMerchant();
                Main.LOGGER.info("Instance1: " + (merchant instanceof VillagerEntity));
                Main.LOGGER.info("Instance2: " + (merchant instanceof MerchantEntity));
                Main.LOGGER.info("Instance3: " + (merchant instanceof SimpleMerchant));
                if (merchant instanceof VillagerEntity) {
                    //((VillagerEntityInvoker)((VillagerEntity)merchant)).fillRecipes();
                    Main.LOGGER.info("Cycling recipes");
                }
            }
        }

        public boolean shouldRenderTooltip() {
            return this.hovered;
        }

        public void renderTooltip(MatrixStack matrices, int mouseX, int mouseY) {
            MerchantScreenMixin.this.renderTooltip(matrices, this.getMessage(), mouseX, mouseY);
        }

        private boolean canCycle(MerchantScreenHandler handler) {
            return handler.getExperience() == 0 && handler.getLevelProgress() == 1;
        }

        public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, new Identifier("easytraders:textures/gui/container/cycle_arrow.png"));
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexture(matrices, this.x, this.y, MerchantScreenMixin.this.getZOffset(), 0.0F, 0.0F, 16, 16, 16, 16);
        }

        public void appendNarrations(NarrationMessageBuilder builder) {
            this.appendDefaultNarrations(builder);
        }
    }
}
