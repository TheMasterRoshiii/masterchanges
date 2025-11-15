package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.combat.CombatLogManager;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class CombatLogMixin {
    
    @Shadow
    public ServerPlayer player;

    @Shadow
    @Final
    private Connection connection;

    @Inject(method = "onDisconnect", at = @At("HEAD"))
    private void onPlayerDisconnect(net.minecraft.network.chat.Component reason, CallbackInfo ci) {
        if (CombatLogManager.getInstance().isInCombat(player)) {
            player.kill();
        }
    }
}
