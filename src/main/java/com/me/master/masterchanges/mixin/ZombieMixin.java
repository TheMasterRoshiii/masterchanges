package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Zombie.class)
public class ZombieMixin {

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void callHorde(CallbackInfo ci) {
        Zombie zombie = (Zombie) (Object) this;
        Level level = zombie.level();
        if (level.isClientSide() || zombie.tickCount < 100) return;
        ServerLevel serverLevel = (ServerLevel) level;
        if (DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.ZOMBIE_CALL_HORDE)) {
            if (zombie.getTarget() instanceof Player player && zombie.tickCount % 400 == 0) {
                if (zombie.tickCount % 600 != 0 || RandomSource.create().nextFloat() >= 0.1f) return;
                AABB area = zombie.getBoundingBox().inflate(20.0);
                int nearbyZombies = serverLevel.getEntitiesOfClass(Zombie.class, area, z -> z != zombie).size();
                if (nearbyZombies > 10) return;
                int toSpawn = RandomSource.create().nextInt(2) + 1;
                int spawned = 0;
                for (int i = 0; i < toSpawn && spawned < toSpawn; i++) {
                    double x = zombie.getX() + (RandomSource.create().nextDouble() - 0.5) * 8.0;
                    double y = zombie.getY();
                    double z = zombie.getZ() + (RandomSource.create().nextDouble() - 0.5) * 8.0;
                    BlockPos spawnPos = BlockPos.containing(x, y, z);
                    Zombie newZombie = EntityType.ZOMBIE.create(serverLevel);
                    if (newZombie != null) {
                        newZombie.moveTo(spawnPos, 0.0f, 0.0f);
                        newZombie.setTarget(player);
                        serverLevel.addFreshEntity(newZombie);
                        spawned++;
                    }
                }
            }
        }
    }
}
