package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.client.BuhCatTotemOverlay;
import com.me.master.masterchanges.item.BuhCatTotemItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererOverrideMixin {

    @Shadow
    private ItemStack itemActivationItem;

    @Shadow
    private int itemActivationTicks;

    @Shadow
    @Final
    private Minecraft minecraft;

    private boolean buhCatDetected = false;

    @Inject(method = "displayItemActivation", at = @At("HEAD"))
    private void detectAndActivateBuhCat(ItemStack stack, CallbackInfo ci) {
        if (minecraft.player != null) {
            ItemStack mainHand = minecraft.player.getMainHandItem();
            ItemStack offHand = minecraft.player.getOffhandItem();

            if (mainHand.getItem() instanceof BuhCatTotemItem) {
                buhCatDetected = true;
                BuhCatTotemOverlay.setActiveTotem(mainHand);
            } else if (offHand.getItem() instanceof BuhCatTotemItem) {
                buhCatDetected = true;
                BuhCatTotemOverlay.setActiveTotem(offHand);
            }
        }
    }

    @Inject(method = "displayItemActivation", at = @At("TAIL"))
    private void clearIfBuhCat(ItemStack stack, CallbackInfo ci) {
        if (buhCatDetected) {
            this.itemActivationItem = ItemStack.EMPTY;
            this.itemActivationTicks = 0;
            buhCatDetected = false;
        }
    }

    @Inject(method = "renderItemActivationAnimation", at = @At("HEAD"), cancellable = true)
    private void cancelVanillaRender(int screenWidth, int screenHeight, float partialTicks, CallbackInfo ci) {
        if (BuhCatTotemOverlay.isActive()) {
            if (this.itemActivationItem != null) {
                this.itemActivationItem = ItemStack.EMPTY;
            }
            this.itemActivationTicks = 0;
            ci.cancel();
        }
    }
}
