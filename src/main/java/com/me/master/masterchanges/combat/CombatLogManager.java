package com.me.master.masterchanges.combat;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatLogManager {
    private static final CombatLogManager INSTANCE = new CombatLogManager();
    private final Map<UUID, Long> combatTimers = new HashMap<>();
    private static final long COMBAT_DURATION_MS = 30000;

    private CombatLogManager() {}

    public static CombatLogManager getInstance() {
        return INSTANCE;
    }

    public void enterCombat(Player player) {
        combatTimers.put(player.getUUID(), System.currentTimeMillis());
    }

    public boolean isInCombat(Player player) {
        Long combatStart = combatTimers.get(player.getUUID());
        if (combatStart == null) return false;

        long elapsed = System.currentTimeMillis() - combatStart;
        if (elapsed >= COMBAT_DURATION_MS) {
            combatTimers.remove(player.getUUID());
            return false;
        }
        return true;
    }

    public long getRemainingCombatTime(Player player) {
        Long combatStart = combatTimers.get(player.getUUID());
        if (combatStart == null) return 0;

        long elapsed = System.currentTimeMillis() - combatStart;
        return Math.max(0, COMBAT_DURATION_MS - elapsed);
    }

    public void removeCombat(UUID playerId) {
        combatTimers.remove(playerId);
    }
}
