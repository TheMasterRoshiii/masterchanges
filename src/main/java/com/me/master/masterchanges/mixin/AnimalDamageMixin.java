package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public class AnimalDamageMixin {

    @Inject(method = "doHurtTarget", at = @At("HEAD"), cancellable = true)
    private void makeAnimalsDamage(Entity target, CallbackInfoReturnable<Boolean> cir) {
        Mob mob = (Mob)(Object)this;

        if (!(mob instanceof Animal) || mob instanceof Axolotl) return;
        if (!DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.PASSIVE_MOBS_AGGRESSIVE)) return;

        float damage = 4.0F;
        boolean success = target.hurt(mob.damageSources().mobAttack(mob), damage);

        if (success) {
            mob.doEnchantDamageEffects(mob, target);
        }

        cir.setReturnValue(success);
    }
}
