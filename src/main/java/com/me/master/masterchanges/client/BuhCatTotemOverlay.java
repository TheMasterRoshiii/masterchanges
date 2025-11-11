package com.me.master.masterchanges.client;

import com.me.master.masterchanges.item.BuhCatTotemItem;
import com.me.master.masterchanges.item.BuhCatTotemRenderer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class BuhCatTotemOverlay {
    private static ItemStack currentTotem = ItemStack.EMPTY;
    private static int totemActivationTicks = 0;
    private static float itemActivationOffX = 0.0F;
    private static float itemActivationOffY = 0.0F;
    private static final RandomSource random = RandomSource.create();
    private static BuhCatTotemRenderer totemRenderer = null;

    public static void setActiveTotem(ItemStack stack) {
        currentTotem = stack.copy();
        totemActivationTicks = 40;
        itemActivationOffX = random.nextFloat() * 2.0F - 1.0F;
        itemActivationOffY = random.nextFloat() * 2.0F - 1.0F;
    }

    public static void tick() {
        if (totemActivationTicks > 0) {
            --totemActivationTicks;
        }
        if (totemActivationTicks == 0) {
            currentTotem = ItemStack.EMPTY;
        }
    }

    public static void render(PoseStack poseStack, float partialTicks) {
        if (currentTotem.isEmpty() || totemActivationTicks <= 0) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        if (totemRenderer == null) {
            totemRenderer = new BuhCatTotemRenderer();
        }

        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();


        int i = 40 - totemActivationTicks;
        float f = ((float)i + partialTicks) / 40.0F;
        float f1 = f * f;
        float f2 = f * f1;
        float f3 = 10.25F * f2 * f1 - 24.95F * f1 * f1 + 25.5F * f2 - 13.8F * f1 + 4.0F;
        float f4 = f3 * (float)Math.PI;
        float f5 = itemActivationOffX * (float)width / 4.0F;
        float f6 = itemActivationOffY * (float)height / 4.0F;

        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();

        poseStack.pushPose();


        poseStack.translate(
                (float)(width / 2) + f5 * Mth.abs(Mth.sin(f4 * 2.0F)),
                (float)(height / 2) + f6 * Mth.abs(Mth.sin(f4 * 2.0F)),
                -50.0F
        );


        float f7 = (50.0F + 175.0F * Mth.sin(f4)) * 0.4F;
        poseStack.scale(f7, -f7, f7);


        poseStack.mulPose(Axis.YP.rotationDegrees(900.0F * Mth.abs(Mth.sin(f4))));


        poseStack.mulPose(Axis.XP.rotationDegrees(6.0F * Mth.cos(f * 8.0F)));
        poseStack.mulPose(Axis.ZP.rotationDegrees(6.0F * Mth.cos(f * 8.0F)));

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        totemRenderer.renderByItem(currentTotem, ItemDisplayContext.FIXED, poseStack, bufferSource, 0xF000F0, OverlayTexture.NO_OVERLAY);
        bufferSource.endBatch();

        RenderSystem.enableCull();

        poseStack.popPose();
    }

    public static boolean isActive() {
        return !currentTotem.isEmpty() && totemActivationTicks > 0;
    }
}
