package com.me.master.masterchanges.command;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.util.Arrays;

public class DifficultyCommand {
    private static final SuggestionProvider<CommandSourceStack> FEATURE_SUGGESTIONS = (context, builder) ->
            SharedSuggestionProvider.suggest(
                    Arrays.stream(DifficultyFeature.values())
                            .map(DifficultyFeature::getId),
                    builder
            );

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("difficulty")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("enable")
                        .then(Commands.argument("feature", StringArgumentType.word())
                                .suggests(FEATURE_SUGGESTIONS)
                                .executes(context -> enableFeature(context, StringArgumentType.getString(context, "feature")))
                        )
                        .then(Commands.literal("all")
                                .executes(DifficultyCommand::enableAll)
                        )
                )
                .then(Commands.literal("disable")
                        .then(Commands.argument("feature", StringArgumentType.word())
                                .suggests(FEATURE_SUGGESTIONS)
                                .executes(context -> disableFeature(context, StringArgumentType.getString(context, "feature")))
                        )
                        .then(Commands.literal("all")
                                .executes(DifficultyCommand::disableAll)
                        )
                )
                .then(Commands.literal("list")
                        .executes(DifficultyCommand::listFeatures)
                )
                .then(Commands.literal("ghastexplosion")
                        .then(Commands.argument("multiplier", FloatArgumentType.floatArg(1.0f, 10.0f))
                                .executes(DifficultyCommand::setGhastExplosion)
                        )
                        .executes(DifficultyCommand::getGhastExplosion)
                )
        );
    }

    private static int enableFeature(CommandContext<CommandSourceStack> context, String featureName) {
        DifficultyFeature feature = findFeature(featureName);
        if (feature == null) {
            context.getSource().sendFailure(Component.literal("Feature no encontrada: " + featureName));
            return 0;
        }

        DifficultyManager.getInstance().setFeature(feature, true);
        context.getSource().sendSuccess(() -> Component.literal("§aActivada: §f" + feature.getDescription()), true);
        return 1;
    }

    private static int disableFeature(CommandContext<CommandSourceStack> context, String featureName) {
        DifficultyFeature feature = findFeature(featureName);
        if (feature == null) {
            context.getSource().sendFailure(Component.literal("Feature no encontrada: " + featureName));
            return 0;
        }

        DifficultyManager.getInstance().setFeature(feature, false);
        context.getSource().sendSuccess(() -> Component.literal("§cDesactivada: §f" + feature.getDescription()), true);
        return 1;
    }

    private static int enableAll(CommandContext<CommandSourceStack> context) {
        DifficultyManager.getInstance().enableAll();
        context.getSource().sendSuccess(() -> Component.literal("§aTodas las features han sido activadas"), true);
        return 1;
    }

    private static int disableAll(CommandContext<CommandSourceStack> context) {
        DifficultyManager.getInstance().disableAll();
        context.getSource().sendSuccess(() -> Component.literal("§cTodas las features han sido desactivadas"), true);
        return 1;
    }

    private static int listFeatures(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.literal("§e§l=== Features de Dificultad ==="), false);

        for (var entry : DifficultyManager.getInstance().getAllFeatures().entrySet()) {
            DifficultyFeature feature = entry.getKey();
            boolean enabled = entry.getValue();
            String status = enabled ? "§a✔ ACTIVA" : "§c✘ INACTIVA";

            context.getSource().sendSuccess(() -> Component.literal(
                    status + " §7- §f" + feature.getDescription() + " §8(" + feature.getId() + ")"
            ), false);
        }

        float multiplier = DifficultyManager.getInstance().getGhastExplosionMultiplier();
        context.getSource().sendSuccess(() -> Component.literal(
                "§e§lMultiplicador de explosión Ghast: §f" + multiplier + "x"
        ), false);

        return 1;
    }

    private static int setGhastExplosion(CommandContext<CommandSourceStack> context) {
        float multiplier = FloatArgumentType.getFloat(context, "multiplier");
        DifficultyManager.getInstance().setGhastExplosionMultiplier(multiplier);
        context.getSource().sendSuccess(() -> Component.literal(
                "§aMultiplicador de explosión Ghast establecido a: §f" + multiplier + "x"
        ), true);
        return 1;
    }

    private static int getGhastExplosion(CommandContext<CommandSourceStack> context) {
        float multiplier = DifficultyManager.getInstance().getGhastExplosionMultiplier();
        context.getSource().sendSuccess(() -> Component.literal(
                "§eMultiplicador de explosión Ghast actual: §f" + multiplier + "x"
        ), false);
        return 1;
    }

    private static DifficultyFeature findFeature(String id) {
        for (DifficultyFeature feature : DifficultyFeature.values()) {
            if (feature.getId().equals(id)) {
                return feature;
            }
        }
        return null;
    }
}
