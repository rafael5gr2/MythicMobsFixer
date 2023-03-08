package me.rafael5gr2.mythicmobsfixer.listeners;

import me.rafael5gr2.mythicmobsfixer.MythicMobsFixer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class EntityDeathListeners implements Listener {

    private final @NotNull MythicMobsFixer mythicMobsFixer;

    private final @NotNull Map<UUID, EntityStorage> knownMythicMobs;

    public EntityDeathListeners(final @NotNull MythicMobsFixer mythicMobsFixer) {
        this.mythicMobsFixer = mythicMobsFixer;
        this.knownMythicMobs = new HashMap<>();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDeathEventLOWEST(EntityDeathEvent event) {
        final LivingEntity livingEntity = event.getEntity();

        if (!this.isMythicMob(livingEntity)) {
            mythicMobsFixer.getComponentLogger().info(
                    Component.text("DEBUG(EntityDeathEvent[LOWEST]) The mob that died is not a MythicMob!").color(NamedTextColor.GREEN)
            );
            return;
        }

        if (!this.isDespawnDenied(livingEntity)) {
            mythicMobsFixer.getComponentLogger().info(
                    Component.text(
                            "DEBUG(EntityDeathEvent[LOWEST]) The mob that died does not have the Despawn option set to false or persistent!"
                    ).color(NamedTextColor.GREEN)
            );
            return;
        }

        mythicMobsFixer.getComponentLogger().info(
                Component.text(
                        "DEBUG(EntityDeathEvent[LOWEST]) The mob that died is a MythicMob and it has the Despawn option set to false or persistent!"
                ).color(NamedTextColor.GREEN)
        );

        final UUID uuid = livingEntity.getUniqueId();

        if (!this.knownMythicMobs.containsKey(uuid)) {
            mythicMobsFixer.getComponentLogger().info(
                    Component.text(
                            "DEBUG(EntityDeathEvent[LOWEST]) The mob that died isn't known! " +
                                    "Adding it to the knownMythicMobs HashMap... (UUID: " + uuid + ")"
                    ).color(NamedTextColor.GREEN)
            );
            this.knownMythicMobs.put(uuid, new EntityStorage(livingEntity, event.getDrops(), event.getDroppedExp()));
        } else {
            mythicMobsFixer.getComponentLogger().info(
                    Component.text("DEBUG(EntityDeathEvent[LOWEST]) The mob that died is known!").color(NamedTextColor.GREEN)
            );
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDeathEventHIGHEST(EntityDeathEvent event) {
        final LivingEntity livingEntity = event.getEntity();

        if (!this.isMythicMob(livingEntity)) {
            mythicMobsFixer.getComponentLogger().info(
                    Component.text("DEBUG(EntityDeathEvent[HIGHEST]) The mob that died is not a MythicMob!").color(NamedTextColor.DARK_GREEN)
            );
            return;
        }

        if (!this.isDespawnDenied(livingEntity)) {
            mythicMobsFixer.getComponentLogger().info(
                    Component.text(
                            "DEBUG(EntityDeathEvent[HIGHEST]) The mob that dies does not have the Despawn option set to false or persistent!"
                    ).color(NamedTextColor.DARK_GREEN)
            );
            return;
        }

        mythicMobsFixer.getComponentLogger().info(
                Component.text(
                        "DEBUG(EntityDeathEvent[HIGHEST]) The mob that died is a MythicMob and it has the Despawn option set to false or persistent!"
                ).color(NamedTextColor.DARK_GREEN)
        );

        final UUID uuid = livingEntity.getUniqueId();

        if (this.knownMythicMobs.containsKey(uuid)) {

            mythicMobsFixer.getComponentLogger().info(
                    Component.text("DEBUG(EntityDeathEvent[HIGHEST]) The mob that died is known!").color(NamedTextColor.DARK_GREEN)
            );

            final EntityStorage initialEntityStorage = this.knownMythicMobs.get(uuid);
            final List<ItemStack> initialMobDrops = initialEntityStorage.mobDrops;
            final int initialDroppedExp = initialEntityStorage.droppedExp;

            final List<ItemStack> currentMobDrops = event.getDrops();
            final int currentDroppedExp = event.getDroppedExp();

            if (initialMobDrops.isEmpty()) {
                if (currentMobDrops.isEmpty()) {
                    if (initialDroppedExp == currentDroppedExp) {
                        livingEntity.setNoDamageTicks(20);
                        livingEntity.setFireTicks(0);
                        livingEntity.getActivePotionEffects().clear();
                        final AttributeInstance genericMaxHealth = livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                        if (genericMaxHealth != null) {
                            event.setReviveHealth(genericMaxHealth.getValue() >= 10 ? 10 : genericMaxHealth.getValue());
                        }
                        event.setCancelled(true);
                        mythicMobsFixer.getComponentLogger().info(
                                Component.text(
                                        "DEBUG(EntityDeathEvent[HIGHEST]) The mob that died triggered the check(Exp)! " +
                                                "The death of the mob has been cancelled... (UUID: " + uuid + "). " +
                                                "Both drop lists were empty, but the exp drops were the same."
                                ).color(NamedTextColor.RED)
                        );
                        return;
                    }
                }
            } else {
                if (!currentMobDrops.isEmpty()) {
                    if (initialMobDrops.get(0).getType() == currentMobDrops.get(0).getType() && initialDroppedExp == currentDroppedExp) {
                        livingEntity.setNoDamageTicks(20);
                        livingEntity.setFireTicks(0);
                        livingEntity.getActivePotionEffects().clear();
                        final AttributeInstance genericMaxHealth = livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                        if (genericMaxHealth != null) {
                            event.setReviveHealth(genericMaxHealth.getValue() >= 10 ? 10 : genericMaxHealth.getValue());
                        }
                        event.setCancelled(true);
                        mythicMobsFixer.getComponentLogger().info(
                                Component.text(
                                        "DEBUG(EntityDeathEvent[HIGHEST]) The mob that died triggered the check(Drops + Exp)! " +
                                                "The death of the mob has been cancelled... (UUID: " + uuid + "). " +
                                                "Both drop lists were not empty, the first item of each list had the same material and " +
                                                "the exp drops were the same."
                                ).color(NamedTextColor.RED)
                        );
                        return;
                    }
                }
            }

            mythicMobsFixer.getComponentLogger().info(
                    Component.text(
                            "DEBUG(EntityDeathEvent[HIGHEST]) The mob that died did not trigger the check! " +
                            "Removing it from the knownMythicMobs HashMap... (UUID: " + uuid + ")"
                    ).color(NamedTextColor.DARK_GREEN)
            );
            this.knownMythicMobs.remove(uuid);
        } else {
            livingEntity.setNoDamageTicks(20);
            livingEntity.setFireTicks(0);
            livingEntity.getActivePotionEffects().clear();
            final AttributeInstance genericMaxHealth = livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            if (genericMaxHealth != null) {
                event.setReviveHealth(genericMaxHealth.getValue() >= 10 ? 10 : genericMaxHealth.getValue());
            }
            event.setCancelled(true);
            mythicMobsFixer.getComponentLogger().info(
                    Component.text(
                            "DEBUG(EntityDeathEvent[HIGHEST]) The mob that died isn't known! " +
                                    "This cannot normally happen in the EntityDeathEvent with priority HIGHEST! " +
                                    "Probably an error occurred! " +
                                    "The death of the mob has been cancelled... (UUID: " + uuid + "). "
                    ).color(NamedTextColor.RED)
            );
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isMythicMob(final @NotNull LivingEntity livingEntity) {
        return livingEntity.getPersistentDataContainer().getKeys().stream()
                .filter(namespacedKey -> namespacedKey.getNamespace().equalsIgnoreCase("mythicmobs"))
                .map(NamespacedKey::getKey)
                .toList().contains("type");
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isDespawnDenied(final @NotNull LivingEntity livingEntity) {
        final PersistentDataContainer persistentDataContainer = livingEntity.getPersistentDataContainer();
        final Optional<NamespacedKey> optionalNamespacedKey = persistentDataContainer.getKeys().stream()
                .filter(namespacedKey -> namespacedKey.getNamespace().equalsIgnoreCase("mythicmobs"))
                .filter(namespacedKey -> namespacedKey.getKey().equalsIgnoreCase("despawns"))
                .findFirst();
        if (optionalNamespacedKey.isPresent()) {
            final NamespacedKey namespacedKey = optionalNamespacedKey.get();
            if (persistentDataContainer.has(namespacedKey, PersistentDataType.BYTE)) {
                final Byte value = persistentDataContainer.get(namespacedKey, PersistentDataType.BYTE);
                if (value != null) {
                    return value.equals((byte) 0);
                }
            }
        }
        return false;
    }

    private record EntityStorage(@NotNull LivingEntity livingEntity, @NotNull List<ItemStack> mobDrops, int droppedExp) {}
}
