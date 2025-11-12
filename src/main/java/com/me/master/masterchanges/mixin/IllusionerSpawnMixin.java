package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Illusioner;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.me.master.masterchanges.config.MixinConfigManager;

@Mixin(ServerLevel.class)
public class IllusionerSpawnMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void trySpawnIllusioner(CallbackInfo ci) {
        MixinConfigManager config = MixinConfigManager.getInstance();

        if (!DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.ILLUSIONER_NATURAL_SPAWN)) return;
        ServerLevel level = (ServerLevel) (Object) this;
        long gameTime = level.getGameTime();
        if (level.isClientSide || gameTime % config.getInt("illusioner_spawn", "duration", 200) != 0) return;

        RandomSource random = level.random;
        int attemptCount = config.getInt("illusioner_spawn", "value", 5);
        for (int attempt = 0; attempt < attemptCount; attempt++) {
            int x = random.nextInt(config.getInt("illusioner_spawn", "maxCount", 32)) * 16;
            int z = random.nextInt(config.getInt("illusioner_spawn", "maxCount1", 32)) * 16;
            BlockPos basePos = new BlockPos(x, 0, z);
            BlockPos pos = level.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, basePos).below();

            int light = level.getBrightness(LightLayer.BLOCK, pos) + level.getBrightness(LightLayer.SKY, pos);
            if (light > config.getInt("illusioner_spawn", "value1", 7) || !level.isNight()) continue;
            FluidState fluid = level.getFluidState(pos);
            if (!fluid.isEmpty()) continue;

            if (!level.getBiome(pos).is(Biomes.DARK_FOREST) && !level.getBiome(pos).is(Biomes.SWAMP) && !level.getBiome(pos).is(Biomes.SOUL_SAND_VALLEY)) continue;

            if (random.nextFloat() < config.getFloat("illusioner_spawn", "chance", 0.005f)) {
                Illusioner illusioner = EntityType.ILLUSIONER.create(level);
                if (illusioner != null) {
                    illusioner.setPos(pos.getX(), pos.getY(), pos.getZ());
                    illusioner.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.NATURAL, null, null);
                    level.addFreshEntity(illusioner);
                    return;
                }
            }
        }
    }
}
