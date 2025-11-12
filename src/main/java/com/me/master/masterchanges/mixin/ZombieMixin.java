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
import com.me.master.masterchanges.config.MixinConfigManager;

@Mixin(Zombie.class)
public class ZombieMixin {

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void callHorde(CallbackInfo ci) {
        MixinConfigManager config = MixinConfigManager.getInstance();

        Zombie zombie = (Zombie) (Object) this;
        Level level = zombie.level();
        if (level.isClientSide() || zombie.tickCount < config.getInt("zombie_call_horde", "value", 100)) return;
        ServerLevel serverLevel = (ServerLevel) level;
        if (DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.ZOMBIE_CALL_HORDE)) {
            if (zombie.getTarget() instanceof Player player && zombie.tickCount % config.getInt("zombie_call_horde", "tickInterval", 400) == 0) {
                if (zombie.tickCount % config.getFloat("zombie_call_horde", "chance", 600f) != 0 || RandomSource.create().nextFloat() >= config.getFloat("zombie_call_horde", "chance1", 0.1f)) return;
                AABB area = zombie.getBoundingBox().inflate(config.getDouble("zombie_call_horde", "range", 20.0));
                int nearbyZombies = serverLevel.getEntitiesOfClass(Zombie.class, area, z -> z != zombie).size();
                if (nearbyZombies > config.getInt("zombie_call_horde", "value1", 10)) return;
                int toSpawn = RandomSource.create().nextInt(config.getInt("zombie_call_horde", "countMax", 2)) + 1;
                int spawned = 0;
                for (int i = 0; i < toSpawn && spawned < toSpawn; i++) {
                    double x = zombie.getX() + (RandomSource.create().nextDouble() - config.getFloat("zombie_call_horde", "multiplier", 0.5f)) * config.getFloat("zombie_call_horde", "multiplier1", 8.0f);
                    double y = zombie.getY();
                    double z = zombie.getZ() + (RandomSource.create().nextDouble() - config.getFloat("zombie_call_horde", "multiplier1", 0.5f)) * config.getFloat("zombie_call_horde", "multiplier1", 8.0f);
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
