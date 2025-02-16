package net.punchtree.loquainteractable.player;

import com.destroystokyo.paper.ClientOption;
import com.destroystokyo.paper.Title;
import com.destroystokyo.paper.block.TargetBlockInfo;
import com.destroystokyo.paper.entity.TargetEntityInfo;
import com.destroystokyo.paper.profile.PlayerProfile;
import io.papermc.paper.entity.LookAnchor;
import io.papermc.paper.entity.PlayerGiveResult;
import io.papermc.paper.entity.TeleportFlag;
import io.papermc.paper.math.Position;
import io.papermc.paper.threadedregions.scheduler.EntityScheduler;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.TriState;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.sign.Side;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.*;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.*;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.*;
import org.jspecify.annotations.Nullable;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class PlayerDecorator implements Player {

    private final Player player;

    public PlayerDecorator(Player player) {
        this.player = player;
    }

    @Override
    public @UnmodifiableView @NotNull Iterable<? extends BossBar> activeBossBars() {
        return player.activeBossBars();
    }

    @Override
    public @NotNull Component displayName() {
        return player.displayName();
    }

    @Override
    public void displayName(@Nullable Component component) {
        player.displayName(component);
    }

    @Override
    public boolean isOnline() {
        return player.isOnline();
    }

    @Override
    public boolean isConnected() {
        return player.isConnected();
    }

    @Override
    public double getEyeHeight() {
        return player.getEyeHeight();
    }

    @Override
    public double getEyeHeight(boolean b) {
        return player.getEyeHeight(b);
    }

    @Override
    public @NotNull Location getEyeLocation() {
        return player.getEyeLocation();
    }

    @Override
    public @NotNull List<Block> getLineOfSight(@org.jetbrains.annotations.Nullable Set<Material> set, int i) {
        return player.getLineOfSight(set, i);
    }

    @Override
    public @NotNull Block getTargetBlock(@org.jetbrains.annotations.Nullable Set<Material> set, int i) {
        return player.getTargetBlock(set, i);
    }

    @Override
    @Deprecated(
            forRemoval = true,
            since = "1.19.3"
    )
    @SuppressWarnings("removal")
    public @org.jetbrains.annotations.Nullable Block getTargetBlock(int i, @NotNull TargetBlockInfo.FluidMode fluidMode) {
        return player.getTargetBlock(i, fluidMode);
    }

    @Override
    @Deprecated(
            forRemoval = true,
            since = "1.19.3"
    )
    @SuppressWarnings("removal")
    public @org.jetbrains.annotations.Nullable BlockFace getTargetBlockFace(int i, @NotNull TargetBlockInfo.FluidMode fluidMode) {
        return player.getTargetBlockFace(i, fluidMode);
    }

    @Override
    public @org.jetbrains.annotations.Nullable BlockFace getTargetBlockFace(int i, @NotNull FluidCollisionMode fluidCollisionMode) {
        return player.getTargetBlockFace(i, fluidCollisionMode);
    }

    @Override
    @Deprecated(
            forRemoval = true,
            since = "1.19.3"
    )
    @SuppressWarnings("removal")
    public @org.jetbrains.annotations.Nullable TargetBlockInfo getTargetBlockInfo(int i, @NotNull TargetBlockInfo.FluidMode fluidMode) {
        return player.getTargetBlockInfo(i, fluidMode);
    }

    @Override
    public @org.jetbrains.annotations.Nullable Entity getTargetEntity(int i, boolean b) {
        return player.getTargetEntity(i, b);
    }

    @Override
    @Deprecated(
            forRemoval = true,
            since = "1.19.3"
    )
    @SuppressWarnings("removal")
    public @org.jetbrains.annotations.Nullable TargetEntityInfo getTargetEntityInfo(int i, boolean b) {
        return player.getTargetEntityInfo(i, b);
    }

    @Override
    public @org.jetbrains.annotations.Nullable RayTraceResult rayTraceEntities(int i, boolean b) {
        return player.rayTraceEntities(i, b);
    }

    @Override
    public @NotNull List<Block> getLastTwoTargetBlocks(@org.jetbrains.annotations.Nullable Set<Material> set, int i) {
        return player.getLastTwoTargetBlocks(set, i);
    }

    @Override
    public @org.jetbrains.annotations.Nullable Block getTargetBlockExact(int i) {
        return player.getTargetBlockExact(i);
    }

    @Override
    public @org.jetbrains.annotations.Nullable Block getTargetBlockExact(int i, @NotNull FluidCollisionMode fluidCollisionMode) {
        return player.getTargetBlockExact(i, fluidCollisionMode);
    }

    @Override
    public @org.jetbrains.annotations.Nullable RayTraceResult rayTraceBlocks(double v) {
        return player.rayTraceBlocks(v);
    }

    @Override
    public @org.jetbrains.annotations.Nullable RayTraceResult rayTraceBlocks(double v, @NotNull FluidCollisionMode fluidCollisionMode) {
        return player.rayTraceBlocks(v, fluidCollisionMode);
    }

    @Override
    public int getRemainingAir() {
        return player.getRemainingAir();
    }

    @Override
    public void setRemainingAir(int i) {
        player.setRemainingAir(i);
    }

    @Override
    public int getMaximumAir() {
        return player.getMaximumAir();
    }

    @Override
    public void setMaximumAir(int i) {
        player.setMaximumAir(i);
    }

    @Override
    @Deprecated(
            forRemoval = true,
            since = "1.20.4"
    )
    @SuppressWarnings("removal")
    public @org.jetbrains.annotations.Nullable ItemStack getItemInUse() {
        return player.getItemInUse();
    }

    @Override
    @Deprecated(
            forRemoval = true,
            since = "1.20.4"
    )
    @SuppressWarnings("removal")
    public int getItemInUseTicks() {
        return player.getItemInUseTicks();
    }

    @Override
    @Deprecated(
            forRemoval = true,
            since = "1.20.4"
    )
    @SuppressWarnings("removal")
    public void setItemInUseTicks(int i) {
        player.setItemInUseTicks(i);
    }

    @Override
    public int getArrowCooldown() {
        return player.getArrowCooldown();
    }

    @Override
    public void setArrowCooldown(int i) {
        player.setArrowCooldown(i);
    }

    @Override
    public int getArrowsInBody() {
        return player.getArrowsInBody();
    }

    @Override
    public void setArrowsInBody(int i, boolean b) {
        player.setArrowsInBody(i, b);
    }

    @Override
    public void setNextArrowRemoval(@Range(from = 0L, to = 2147483647L) int i) {
        player.setNextArrowRemoval(i);
    }

    @Override
    public int getNextArrowRemoval() {
        return player.getNextArrowRemoval();
    }

    @Override
    public int getBeeStingerCooldown() {
        return player.getBeeStingerCooldown();
    }

    @Override
    public void setBeeStingerCooldown(int i) {
        player.setBeeStingerCooldown(i);
    }

    @Override
    public int getBeeStingersInBody() {
        return player.getBeeStingersInBody();
    }

    @Override
    public void setBeeStingersInBody(int i) {
        player.setBeeStingersInBody(i);
    }

    @Override
    public void setNextBeeStingerRemoval(@Range(from = 0L, to = 2147483647L) int i) {
        player.setNextBeeStingerRemoval(i);
    }

    @Override
    public int getNextBeeStingerRemoval() {
        return player.getNextBeeStingerRemoval();
    }

    @Override
    public int getMaximumNoDamageTicks() {
        return player.getMaximumNoDamageTicks();
    }

    @Override
    public void setMaximumNoDamageTicks(int i) {
        player.setMaximumNoDamageTicks(i);
    }

    @Override
    public double getLastDamage() {
        return player.getLastDamage();
    }

    @Override
    public void setLastDamage(double v) {
        player.setLastDamage(v);
    }

    @Override
    public int getNoDamageTicks() {
        return player.getNoDamageTicks();
    }

    @Override
    public void setNoDamageTicks(int i) {
        player.setNoDamageTicks(i);
    }

    @Override
    public int getNoActionTicks() {
        return player.getNoActionTicks();
    }

    @Override
    public void setNoActionTicks(int i) {
        player.setNoActionTicks(i);
    }

    @Override
    public @org.jetbrains.annotations.Nullable Player getKiller() {
        return player.getKiller();
    }

    @Override
    public void setKiller(@org.jetbrains.annotations.Nullable Player player) {
        this.player.setKiller(player);
    }

    @Override
    public boolean addPotionEffect(@NotNull PotionEffect potionEffect) {
        return player.addPotionEffect(potionEffect);
    }

    @Override
    @Deprecated
    public boolean addPotionEffect(@NotNull PotionEffect potionEffect, boolean b) {
        return player.addPotionEffect(potionEffect, b);
    }

    @Override
    public boolean addPotionEffects(@NotNull Collection<PotionEffect> collection) {
        return player.addPotionEffects(collection);
    }

    @Override
    public boolean hasPotionEffect(@NotNull PotionEffectType potionEffectType) {
        return player.hasPotionEffect(potionEffectType);
    }

    @Override
    public @org.jetbrains.annotations.Nullable PotionEffect getPotionEffect(@NotNull PotionEffectType potionEffectType) {
        return player.getPotionEffect(potionEffectType);
    }

    @Override
    public void removePotionEffect(@NotNull PotionEffectType potionEffectType) {
        player.removePotionEffect(potionEffectType);
    }

    @Override
    public @NotNull Collection<PotionEffect> getActivePotionEffects() {
        return player.getActivePotionEffects();
    }

    @Override
    public boolean clearActivePotionEffects() {
        return player.clearActivePotionEffects();
    }

    @Override
    public boolean hasLineOfSight(@NotNull Entity entity) {
        return player.hasLineOfSight(entity);
    }

    @Override
    public boolean hasLineOfSight(@NotNull Location location) {
        return player.hasLineOfSight(location);
    }

    @Override
    public boolean getRemoveWhenFarAway() {
        return player.getRemoveWhenFarAway();
    }

    @Override
    public void setRemoveWhenFarAway(boolean b) {
        player.setRemoveWhenFarAway(b);
    }

    @Override
    public EntityEquipment getEquipment() {
        return player.getEquipment();
    }

    @Override
    public void setCanPickupItems(boolean b) {
        player.setCanPickupItems(b);
    }

    @Override
    public boolean getCanPickupItems() {
        return player.getCanPickupItems();
    }

    @Override
    public boolean isLeashed() {
        return player.isLeashed();
    }

    @Override
    public @NotNull Entity getLeashHolder() throws IllegalStateException {
        return player.getLeashHolder();
    }

    @Override
    public boolean setLeashHolder(@org.jetbrains.annotations.Nullable Entity entity) {
        return player.setLeashHolder(entity);
    }

    @Override
    public boolean isGliding() {
        return player.isGliding();
    }

    @Override
    public void setGliding(boolean b) {
        player.setGliding(b);
    }

    @Override
    public boolean isSwimming() {
        return player.isSwimming();
    }

    @Override
    @Deprecated
    public void setSwimming(boolean b) {
        player.setSwimming(b);
    }

    @Override
    public boolean isRiptiding() {
        return player.isRiptiding();
    }

    @Override
    public void setRiptiding(boolean b) {
        player.setRiptiding(b);
    }

    @Override
    public boolean isSleeping() {
        return player.isSleeping();
    }

    @Override
    public boolean isClimbing() {
        return player.isClimbing();
    }

    @Override
    public void setAI(boolean b) {
        player.setAI(b);
    }

    @Override
    public boolean hasAI() {
        return player.hasAI();
    }

    @Override
    public void attack(@NotNull Entity entity) {
        player.attack(entity);
    }

    @Override
    public void swingMainHand() {
        player.swingMainHand();
    }

    @Override
    public void swingOffHand() {
        player.swingOffHand();
    }

    @Override
    public void playHurtAnimation(float v) {
        player.playHurtAnimation(v);
    }

    @Override
    public void setCollidable(boolean b) {
        player.setCollidable(b);
    }

    @Override
    public boolean isCollidable() {
        return player.isCollidable();
    }

    @Override
    public @NotNull Set<UUID> getCollidableExemptions() {
        return player.getCollidableExemptions();
    }

    @Override
    public <T> @org.jetbrains.annotations.Nullable T getMemory(@NotNull MemoryKey<T> memoryKey) {
        return player.getMemory(memoryKey);
    }

    @Override
    public <T> void setMemory(@NotNull MemoryKey<T> memoryKey, @org.jetbrains.annotations.Nullable T t) {
        player.setMemory(memoryKey, t);
    }

    @Override
    public @org.jetbrains.annotations.Nullable Sound getHurtSound() {
        return player.getHurtSound();
    }

    @Override
    public @org.jetbrains.annotations.Nullable Sound getDeathSound() {
        return player.getDeathSound();
    }

    @Override
    public @NotNull Sound getFallDamageSound(int i) {
        return player.getFallDamageSound(i);
    }

    @Override
    public @NotNull Sound getFallDamageSoundSmall() {
        return player.getFallDamageSoundSmall();
    }

    @Override
    public @NotNull Sound getFallDamageSoundBig() {
        return player.getFallDamageSoundBig();
    }

    @Override
    public @NotNull Sound getDrinkingSound(@NotNull ItemStack itemStack) {
        return player.getDrinkingSound(itemStack);
    }

    @Override
    public @NotNull Sound getEatingSound(@NotNull ItemStack itemStack) {
        return player.getEatingSound(itemStack);
    }

    @Override
    public boolean canBreatheUnderwater() {
        return player.canBreatheUnderwater();
    }

    @Override
    @Deprecated(
            since = "1.20.5",
            forRemoval = true
    )
    @SuppressWarnings("removal")
    public @NotNull EntityCategory getCategory() {
        return player.getCategory();
    }

    @Override
    public void setInvisible(boolean b) {
        player.setInvisible(b);
    }

    @Override
    public boolean isInvisible() {
        return player.isInvisible();
    }

    @Override
    public void setNoPhysics(boolean b) {
        player.setNoPhysics(b);
    }

    @Override
    public boolean hasNoPhysics() {
        return player.hasNoPhysics();
    }

    @Override
    public boolean isFreezeTickingLocked() {
        return player.isFreezeTickingLocked();
    }

    @Override
    public void lockFreezeTicks(boolean b) {
        player.lockFreezeTicks(b);
    }

    @Override
    public void remove() {
        player.remove();
    }

    @Override
    public boolean isDead() {
        return player.isDead();
    }

    @Override
    public boolean isValid() {
        return player.isValid();
    }

    @Override
    public void sendMessage(@NotNull String s) {
        player.sendMessage(s);
    }

    @Override
    public void sendMessage(@NotNull String... strings) {
        player.sendMessage(strings);
    }

    @Override
    @Deprecated
    public void sendMessage(@org.jetbrains.annotations.Nullable UUID uuid, @NotNull String s) {
        player.sendMessage(uuid, s);
    }

    @Override
    @Deprecated
    public void sendMessage(@org.jetbrains.annotations.Nullable UUID uuid, @NotNull String... strings) {
        player.sendMessage(uuid, strings);
    }

    @Override
    public @NotNull Server getServer() {
        return player.getServer();
    }

    @Override
    public boolean isPersistent() {
        return player.isPersistent();
    }

    @Override
    public void setPersistent(boolean b) {
        player.setPersistent(b);
    }

    @Override
    @Deprecated
    public @org.jetbrains.annotations.Nullable Entity getPassenger() {
        return player.getPassenger();
    }

    @Override
    @Deprecated
    public boolean setPassenger(@NotNull Entity entity) {
        return player.setPassenger(entity);
    }

    @Override
    public @NotNull List<Entity> getPassengers() {
        return player.getPassengers();
    }

    @Override
    public boolean addPassenger(@NotNull Entity entity) {
        return player.addPassenger(entity);
    }

    @Override
    public boolean removePassenger(@NotNull Entity entity) {
        return player.removePassenger(entity);
    }

    @Override
    public boolean isEmpty() {
        return player.isEmpty();
    }

    @Override
    public boolean eject() {
        return player.eject();
    }

    @Override
    public float getFallDistance() {
        return player.getFallDistance();
    }

    @Override
    public void setFallDistance(float v) {
        player.setFallDistance(v);
    }

    @Override
    @Deprecated(
            since = "1.20.4",
            forRemoval = true
    )
    @SuppressWarnings("removal")
    public void setLastDamageCause(@org.jetbrains.annotations.Nullable EntityDamageEvent entityDamageEvent) {
        player.setLastDamageCause(entityDamageEvent);
    }

    @Override
    public @org.jetbrains.annotations.Nullable EntityDamageEvent getLastDamageCause() {
        return player.getLastDamageCause();
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return player.getUniqueId();
    }

    @Override
    public int getTicksLived() {
        return player.getTicksLived();
    }

    @Override
    public void setTicksLived(int i) {
        player.setTicksLived(i);
    }

    @Override
    public void playEffect(@NotNull EntityEffect entityEffect) {
        player.playEffect(entityEffect);
    }

    @Override
    public @NotNull EntityType getType() {
        return player.getType();
    }

    @Override
    public @NotNull Sound getSwimSound() {
        return player.getSwimSound();
    }

    @Override
    public @NotNull Sound getSwimSplashSound() {
        return player.getSwimSplashSound();
    }

    @Override
    public @NotNull Sound getSwimHighSpeedSplashSound() {
        return player.getSwimHighSpeedSplashSound();
    }

    @Override
    public boolean isInsideVehicle() {
        return player.isInsideVehicle();
    }

    @Override
    public boolean leaveVehicle() {
        return player.leaveVehicle();
    }

    @Override
    public @org.jetbrains.annotations.Nullable Entity getVehicle() {
        return player.getVehicle();
    }

    @Override
    public void setCustomNameVisible(boolean b) {
        player.setCustomNameVisible(b);
    }

    @Override
    public boolean isCustomNameVisible() {
        return player.isCustomNameVisible();
    }

    @Override
    public void setVisibleByDefault(boolean b) {
        player.setVisibleByDefault(b);
    }

    @Override
    public boolean isVisibleByDefault() {
        return player.isVisibleByDefault();
    }

    @Override
    public @NotNull Set<Player> getTrackedBy() {
        return player.getTrackedBy();
    }

    @Override
    public void setGlowing(boolean b) {
        player.setGlowing(b);
    }

    @Override
    public boolean isGlowing() {
        return player.isGlowing();
    }

    @Override
    public void setInvulnerable(boolean b) {
        player.setInvulnerable(b);
    }

    @Override
    public boolean isInvulnerable() {
        return player.isInvulnerable();
    }

    @Override
    public boolean isSilent() {
        return player.isSilent();
    }

    @Override
    public void setSilent(boolean b) {
        player.setSilent(b);
    }

    @Override
    public boolean hasGravity() {
        return player.hasGravity();
    }

    @Override
    public void setGravity(boolean b) {
        player.setGravity(b);
    }

    @Override
    public int getPortalCooldown() {
        return player.getPortalCooldown();
    }

    @Override
    public void setPortalCooldown(int i) {
        player.setPortalCooldown(i);
    }

    @Override
    public @NotNull Set<String> getScoreboardTags() {
        return player.getScoreboardTags();
    }

    @Override
    public boolean addScoreboardTag(@NotNull String s) {
        return player.addScoreboardTag(s);
    }

    @Override
    public boolean removeScoreboardTag(@NotNull String s) {
        return player.removeScoreboardTag(s);
    }

    @Override
    public @NotNull PistonMoveReaction getPistonMoveReaction() {
        return player.getPistonMoveReaction();
    }

    @Override
    public @NotNull BlockFace getFacing() {
        return player.getFacing();
    }

    @Override
    public @NotNull Pose getPose() {
        return player.getPose();
    }

    @Override
    @Deprecated
    public int getArrowsStuck() {
        return player.getArrowsStuck();
    }

    @Override
    @Deprecated
    public void setArrowsStuck(int i) {
        player.setArrowsStuck(i);
    }

    @Override
    public int getShieldBlockingDelay() {
        return player.getShieldBlockingDelay();
    }

    @Override
    public void setShieldBlockingDelay(int i) {
        player.setShieldBlockingDelay(i);
    }

    @Override
    public float getSidewaysMovement() {
        return player.getSidewaysMovement();
    }

    @Override
    public float getUpwardsMovement() {
        return player.getUpwardsMovement();
    }

    @Override
    public float getForwardsMovement() {
        return player.getForwardsMovement();
    }

    @Override
    @ApiStatus.Experimental
    @SuppressWarnings("UnstableApiUsage")
    public void startUsingItem(@NotNull EquipmentSlot equipmentSlot) {
        player.startUsingItem(equipmentSlot);
    }

    @Override
    @ApiStatus.Experimental
    @SuppressWarnings("UnstableApiUsage")
    public void completeUsingActiveItem() {
        player.completeUsingActiveItem();
    }

    @Override
    public @NotNull ItemStack getActiveItem() {
        return player.getActiveItem();
    }

    @Override
    public void clearActiveItem() {
        player.clearActiveItem();
    }

    @Override
    public int getActiveItemRemainingTime() {
        return player.getActiveItemRemainingTime();
    }

    @Override
    public void setActiveItemRemainingTime(@Range(from = 0L, to = 2147483647L) int i) {
        player.setActiveItemRemainingTime(i);
    }

    @Override
    public boolean hasActiveItem() {
        return player.hasActiveItem();
    }

    @Override
    public int getActiveItemUsedTime() {
        return player.getActiveItemUsedTime();
    }

    @Override
    public @NotNull EquipmentSlot getActiveItemHand() {
        return player.getActiveItemHand();
    }

    @Override
    public @NotNull String getName() {
        return player.getName();
    }

    @Override
    public @NotNull PlayerInventory getInventory() {
        return player.getInventory();
    }

    @Override
    public @NotNull Inventory getEnderChest() {
        return player.getEnderChest();
    }

    @Override
    public @NotNull MainHand getMainHand() {
        return player.getMainHand();
    }

    @Override
    @Deprecated(
            forRemoval = true,
            since = "1.21"
    )
    @SuppressWarnings("removal")
    public boolean setWindowProperty(InventoryView.@NotNull Property property, int i) {
        return player.setWindowProperty(property, i);
    }

    @Override
    public int getEnchantmentSeed() {
        return player.getEnchantmentSeed();
    }

    @Override
    public void setEnchantmentSeed(int i) {
        player.setEnchantmentSeed(i);
    }

    @Override
    public @NotNull InventoryView getOpenInventory() {
        return player.getOpenInventory();
    }

    @Override
    public @Nullable InventoryView openInventory(@NotNull Inventory inventory) {
        return player.openInventory(inventory);
    }

    @Override
    @Deprecated(
            since = "1.21.4"
    )
    public @Nullable InventoryView openWorkbench(@Nullable Location location, boolean b) {
        return player.openWorkbench(location, b);
    }

    @Override
    @Deprecated(
            since = "1.21.4"
    )
    public @Nullable InventoryView openEnchanting(@Nullable Location location, boolean b) {
        return player.openEnchanting(location, b);
    }

    @Override
    public void openInventory(@NotNull InventoryView inventoryView) {
        player.openInventory(inventoryView);
    }

    @Override
    @Deprecated
    public @Nullable InventoryView openMerchant(@NotNull Villager villager, boolean b) {
        return player.openMerchant(villager, b);
    }

    @Override
    @Deprecated
    public @Nullable InventoryView openMerchant(@NotNull Merchant merchant, boolean b) {
        return player.openMerchant(merchant, b);
    }

    @Override
    @Deprecated
    public @Nullable InventoryView openAnvil(@Nullable Location location, boolean b) {
        return player.openAnvil(location, b);
    }

    @Override
    @Deprecated
    public @Nullable InventoryView openCartographyTable(@Nullable Location location, boolean b) {
        return player.openCartographyTable(location, b);
    }

    @Override
    @Deprecated
    public @Nullable InventoryView openGrindstone(@Nullable Location location, boolean b) {
        return player.openGrindstone(location, b);
    }

    @Override
    @Deprecated
    public @Nullable InventoryView openLoom(@Nullable Location location, boolean b) {
        return player.openLoom(location, b);
    }

    @Override
    @Deprecated
    public @Nullable InventoryView openSmithingTable(@Nullable Location location, boolean b) {
        return player.openSmithingTable(location, b);
    }

    @Override
    @Deprecated
    public @Nullable InventoryView openStonecutter(@Nullable Location location, boolean b) {
        return player.openStonecutter(location, b);
    }

    @Override
    public void closeInventory() {
        player.closeInventory();
    }

    @Override
    public void closeInventory(InventoryCloseEvent.@NotNull Reason reason) {
        player.closeInventory(reason);
    }

    @Override
    @Deprecated
    public @NotNull ItemStack getItemInHand() {
        return player.getItemInHand();
    }

    @Override
    @Deprecated
    public void setItemInHand(@Nullable ItemStack itemStack) {
        player.setItemInHand(itemStack);
    }

    @Override
    public @NotNull ItemStack getItemOnCursor() {
        return player.getItemOnCursor();
    }

    @Override
    public void setItemOnCursor(@Nullable ItemStack itemStack) {
        player.setItemOnCursor(itemStack);
    }

    @Override
    public boolean hasCooldown(@NotNull Material material) {
        return player.hasCooldown(material);
    }

    @Override
    public int getCooldown(@NotNull Material material) {
        return player.getCooldown(material);
    }

    @Override
    public void setCooldown(@NotNull Material material, int i) {
        player.setCooldown(material, i);
    }

    @Override
    public void setHurtDirection(float v) {
        player.setHurtDirection(v);
    }

    @Override
    public void knockback(double v, double v1, double v2) {
        player.knockback(v, v1, v2);
    }

    @Override
    public void broadcastSlotBreak(@NotNull EquipmentSlot equipmentSlot) {
        player.broadcastSlotBreak(equipmentSlot);
    }

    @Override
    public void broadcastSlotBreak(@NotNull EquipmentSlot equipmentSlot, @NotNull Collection<Player> collection) {
        player.broadcastSlotBreak(equipmentSlot, collection);
    }

    @Override
    public @NotNull ItemStack damageItemStack(@NotNull ItemStack itemStack, int i) {
        return player.damageItemStack(itemStack, i);
    }

    @Override
    public void damageItemStack(@NotNull EquipmentSlot equipmentSlot, int i) {
        player.damageItemStack(equipmentSlot, i);
    }

    @Override
    public float getBodyYaw() {
        return player.getBodyYaw();
    }

    @Override
    public void setBodyYaw(float v) {
        player.setBodyYaw(v);
    }

    @Override
    public boolean canUseEquipmentSlot(@NotNull EquipmentSlot equipmentSlot) {
        return player.canUseEquipmentSlot(equipmentSlot);
    }

    @Override
    public boolean isDeeplySleeping() {
        return player.isDeeplySleeping();
    }

    @Override
    public boolean hasCooldown(@NotNull ItemStack itemStack) {
        return player.hasCooldown(itemStack);
    }

    @Override
    public int getCooldown(@NotNull ItemStack itemStack) {
        return player.getCooldown(itemStack);
    }

    @Override
    public void setCooldown(@NotNull ItemStack itemStack, int i) {
        player.setCooldown(itemStack, i);
    }

    @Override
    public int getSleepTicks() {
        return player.getSleepTicks();
    }

    @Override
    public @Nullable Location getPotentialRespawnLocation() {
        return player.getPotentialRespawnLocation();
    }

    @Override
    public @Nullable FishHook getFishHook() {
        return player.getFishHook();
    }

    @Override
    public boolean sleep(@NotNull Location location, boolean b) {
        return player.sleep(location, b);
    }

    @Override
    public void wakeup(boolean b) {
        player.wakeup(b);
    }

    @Override
    public void startRiptideAttack(int i, float v, @Nullable ItemStack itemStack) {
        player.startRiptideAttack(i, v, itemStack);
    }

    @Override
    public @NotNull Location getBedLocation() {
        return player.getBedLocation();
    }

    @Override
    public @NotNull GameMode getGameMode() {
        return player.getGameMode();
    }

    @Override
    public void setGameMode(@NotNull GameMode gameMode) {
        player.setGameMode(gameMode);
    }

    @Override
    public boolean isBlocking() {
        return player.isBlocking();
    }

    @Override
    public boolean isHandRaised() {
        return player.isHandRaised();
    }

    @Override
    public boolean isJumping() {
        return player.isJumping();
    }

    @Override
    public void setJumping(boolean b) {
        player.setJumping(b);
    }

    @Override
    public void playPickupItemAnimation(@NotNull Item item, int i) {
        player.playPickupItemAnimation(item, i);
    }

    @Override
    public float getHurtDirection() {
        return player.getHurtDirection();
    }

    @Override
    public int getExpToLevel() {
        return player.getExpToLevel();
    }

    @Override
    public @Nullable Entity releaseLeftShoulderEntity() {
        return player.releaseLeftShoulderEntity();
    }

    @Override
    public @Nullable Entity releaseRightShoulderEntity() {
        return player.releaseRightShoulderEntity();
    }

    @Override
    public float getAttackCooldown() {
        return player.getAttackCooldown();
    }

    @Override
    public boolean discoverRecipe(@NotNull NamespacedKey namespacedKey) {
        return player.discoverRecipe(namespacedKey);
    }

    @Override
    public int discoverRecipes(@NotNull Collection<NamespacedKey> collection) {
        return player.discoverRecipes(collection);
    }

    @Override
    public boolean undiscoverRecipe(@NotNull NamespacedKey namespacedKey) {
        return player.undiscoverRecipe(namespacedKey);
    }

    @Override
    public int undiscoverRecipes(@NotNull Collection<NamespacedKey> collection) {
        return player.undiscoverRecipes(collection);
    }

    @Override
    public boolean hasDiscoveredRecipe(@NotNull NamespacedKey namespacedKey) {
        return player.hasDiscoveredRecipe(namespacedKey);
    }

    @Override
    public @NotNull Set<NamespacedKey> getDiscoveredRecipes() {
        return player.getDiscoveredRecipes();
    }

    @Override
    @Deprecated
    public @Nullable Entity getShoulderEntityLeft() {
        return player.getShoulderEntityLeft();
    }

    @Override
    @Deprecated
    public void setShoulderEntityLeft(@Nullable Entity entity) {
        player.setShoulderEntityLeft(entity);
    }

    @Override
    @Deprecated
    public @Nullable Entity getShoulderEntityRight() {
        return player.getShoulderEntityRight();
    }

    @Override
    @Deprecated
    public void setShoulderEntityRight(@Nullable Entity entity) {
        player.setShoulderEntityRight(entity);
    }

    @Override
    @Deprecated
    public @NotNull String getDisplayName() {
        return player.getDisplayName();
    }

    @Override
    @Deprecated
    public void setDisplayName(@Nullable String s) {
        player.setDisplayName(s);
    }

    @Override
    public void playerListName(@Nullable Component component) {
        player.playerListName(component);
    }

    @Override
    public @NotNull Component playerListName() {
        return player.playerListName();
    }

    @Override
    public @Nullable Component playerListHeader() {
        return player.playerListHeader();
    }

    @Override
    public @Nullable Component playerListFooter() {
        return player.playerListFooter();
    }

    @Override
    @Deprecated
    public @NotNull String getPlayerListName() {
        return player.getPlayerListName();
    }

    @Override
    @Deprecated
    public void setPlayerListName(@Nullable String s) {
        player.setPlayerListName(s);
    }

    @Override
    public int getPlayerListOrder() {
        return player.getPlayerListOrder();
    }

    @Override
    public void setPlayerListOrder(int i) {
        player.setPlayerListOrder(i);
    }

    @Override
    @Deprecated
    public @Nullable String getPlayerListHeader() {
        return player.getPlayerListHeader();
    }

    @Override
    @Deprecated
    public @Nullable String getPlayerListFooter() {
        return player.getPlayerListFooter();
    }

    @Override
    @Deprecated
    public void setPlayerListHeader(@Nullable String s) {
        player.setPlayerListHeader(s);
    }

    @Override
    @Deprecated
    public void setPlayerListFooter(@Nullable String s) {
        player.setPlayerListFooter(s);
    }

    @Override
    @Deprecated
    public void setPlayerListHeaderFooter(@Nullable String s, @Nullable String s1) {
        player.setPlayerListHeaderFooter(s, s1);
    }

    @Override
    public void setCompassTarget(@NotNull Location location) {
        player.setCompassTarget(location);
    }

    @Override
    public @NotNull Location getCompassTarget() {
        return player.getCompassTarget();
    }

    @Override
    public @Nullable InetSocketAddress getAddress() {
        return player.getAddress();
    }

    @Override
    public int getProtocolVersion() {
        return player.getProtocolVersion();
    }

    @Override
    public @Nullable InetSocketAddress getVirtualHost() {
        return player.getVirtualHost();
    }

    @Override
    public @Nullable InetSocketAddress getHAProxyAddress() {
        return player.getHAProxyAddress();
    }

    @Override
    public boolean isTransferred() {
        return player.isTransferred();
    }

    @Override
    public @NotNull CompletableFuture<byte[]> retrieveCookie(@NotNull NamespacedKey namespacedKey) {
        return player.retrieveCookie(namespacedKey);
    }

    @Override
    public void storeCookie(@NotNull NamespacedKey namespacedKey, byte @NotNull [] bytes) {
        player.storeCookie(namespacedKey, bytes);
    }

    @Override
    public void transfer(@NotNull String s, int i) {
        player.transfer(s, i);
    }

    @Override
    public boolean isConversing() {
        return player.isConversing();
    }

    @Override
    public void acceptConversationInput(@NotNull String s) {
        player.acceptConversationInput(s);
    }

    @Override
    public boolean beginConversation(@NotNull Conversation conversation) {
        return player.beginConversation(conversation);
    }

    @Override
    public void abandonConversation(@NotNull Conversation conversation) {
        player.abandonConversation(conversation);
    }

    @Override
    public void abandonConversation(@NotNull Conversation conversation, @NotNull ConversationAbandonedEvent conversationAbandonedEvent) {
        player.abandonConversation(conversation, conversationAbandonedEvent);
    }

    @Override
    public void sendRawMessage(@NotNull String s) {
        player.sendRawMessage(s);
    }

    @Override
    @Deprecated
    public void sendRawMessage(@org.jetbrains.annotations.Nullable UUID uuid, @NotNull String s) {
        player.sendRawMessage(uuid, s);
    }

    @Override
    @Deprecated
    public void kickPlayer(@Nullable String s) {
        player.kickPlayer(s);
    }

    @Override
    public void kick() {
        player.kick();
    }

    @Override
    public void kick(@Nullable Component component) {
        player.kick(component);
    }

    @Override
    public void kick(@Nullable Component component, PlayerKickEvent.@NotNull Cause cause) {
        player.kick(component, cause);
    }

    @Override
    public <E extends BanEntry<? super PlayerProfile>> @Nullable E ban(@Nullable String s, @Nullable Date date, @Nullable String s1, boolean b) {
        return player.ban(s, date, s1, b);
    }

    @Override
    public <E extends BanEntry<? super PlayerProfile>> @Nullable E ban(@Nullable String s, @Nullable Instant instant, @Nullable String s1, boolean b) {
        return player.ban(s, instant, s1, b);
    }

    @Override
    public <E extends BanEntry<? super PlayerProfile>> @Nullable E ban(@Nullable String s, @Nullable Duration duration, @Nullable String s1, boolean b) {
        return player.ban(s, duration, s1, b);
    }

    @Override
    public @Nullable BanEntry<InetAddress> banIp(@Nullable String s, @Nullable Date date, @Nullable String s1, boolean b) {
        return player.banIp(s, date, s1, b);
    }

    @Override
    public @Nullable BanEntry<InetAddress> banIp(@Nullable String s, @Nullable Instant instant, @Nullable String s1, boolean b) {
        return player.banIp(s, instant, s1, b);
    }

    @Override
    public @Nullable BanEntry<InetAddress> banIp(@Nullable String s, @Nullable Duration duration, @Nullable String s1, boolean b) {
        return player.banIp(s, duration, s1, b);
    }

    @Override
    public void chat(@NotNull String s) {
        player.chat(s);
    }

    @Override
    public boolean performCommand(@NotNull String s) {
        return player.performCommand(s);
    }

    @Override
    public @NotNull Location getLocation() {
        return player.getLocation();
    }

    @Override
    public @org.jetbrains.annotations.Nullable Location getLocation(@org.jetbrains.annotations.Nullable Location location) {
        return player.getLocation(location);
    }

    @Override
    public void setVelocity(@NotNull Vector vector) {
        player.setVelocity(vector);
    }

    @Override
    public @NotNull Vector getVelocity() {
        return player.getVelocity();
    }

    @Override
    public double getHeight() {
        return player.getHeight();
    }

    @Override
    public double getWidth() {
        return player.getWidth();
    }

    @Override
    public @NotNull BoundingBox getBoundingBox() {
        return player.getBoundingBox();
    }

    @Override
    @Deprecated
    public boolean isOnGround() {
        return player.isOnGround();
    }

    @Override
    public boolean isInWater() {
        return player.isInWater();
    }

    @Override
    public @NotNull World getWorld() {
        return player.getWorld();
    }

    @Override
    public boolean isSneaking() {
        return player.isSneaking();
    }

    @Override
    public void setSneaking(boolean b) {
        player.setSneaking(b);
    }

    @Override
    public void setPose(@NotNull Pose pose, boolean b) {
        player.setPose(pose, b);
    }

    @Override
    public boolean hasFixedPose() {
        return player.hasFixedPose();
    }

    @Override
    public @NotNull SpawnCategory getSpawnCategory() {
        return player.getSpawnCategory();
    }

    @Override
    public boolean isInWorld() {
        return player.isInWorld();
    }

    @Override
    @ApiStatus.Experimental
    @SuppressWarnings("UnstableApiUsage")
    public @org.jetbrains.annotations.Nullable String getAsString() {
        return player.getAsString();
    }

    @Override
    @ApiStatus.Experimental
    @SuppressWarnings("UnstableApiUsage")
    public @org.jetbrains.annotations.Nullable EntitySnapshot createSnapshot() {
        return player.createSnapshot();
    }

    @Override
    @ApiStatus.Experimental
    @SuppressWarnings("UnstableApiUsage")
    public @NotNull Entity copy() {
        return player.copy();
    }

    @Override
    @ApiStatus.Experimental
    @SuppressWarnings("UnstableApiUsage")
    public @NotNull Entity copy(@NotNull Location location) {
        return player.copy(location);
    }

    @Override
    public boolean isSprinting() {
        return player.isSprinting();
    }

    @Override
    public void setSprinting(boolean b) {
        player.setSprinting(b);
    }

    @Override
    public void saveData() {
        player.saveData();
    }

    @Override
    public void loadData() {
        player.loadData();
    }

    @Override
    public void setSleepingIgnored(boolean b) {
        player.setSleepingIgnored(b);
    }

    @Override
    public boolean isSleepingIgnored() {
        return player.isSleepingIgnored();
    }

    @Override
    @Deprecated
    public @Nullable Location getBedSpawnLocation() {
        return player.getBedSpawnLocation();
    }

    @Override
    public long getLastLogin() {
        return player.getLastLogin();
    }

    @Override
    public long getLastSeen() {
        return player.getLastSeen();
    }

    @Override
    public @Nullable Location getRespawnLocation() {
        return player.getRespawnLocation();
    }

    @Override
    public void incrementStatistic(@NotNull Statistic statistic) throws IllegalArgumentException {
        player.incrementStatistic(statistic);
    }

    @Override
    public void decrementStatistic(@NotNull Statistic statistic) throws IllegalArgumentException {
        player.decrementStatistic(statistic);
    }

    @Override
    public void incrementStatistic(@NotNull Statistic statistic, int i) throws IllegalArgumentException {
        player.incrementStatistic(statistic, i);
    }

    @Override
    public void decrementStatistic(@NotNull Statistic statistic, int i) throws IllegalArgumentException {
        player.decrementStatistic(statistic, i);
    }

    @Override
    public void setStatistic(@NotNull Statistic statistic, int i) throws IllegalArgumentException {
        player.setStatistic(statistic, i);
    }

    @Override
    public int getStatistic(@NotNull Statistic statistic) throws IllegalArgumentException {
        return player.getStatistic(statistic);
    }

    @Override
    public void incrementStatistic(@NotNull Statistic statistic, @NotNull Material material) throws IllegalArgumentException {
        player.incrementStatistic(statistic, material);
    }

    @Override
    public void decrementStatistic(@NotNull Statistic statistic, @NotNull Material material) throws IllegalArgumentException {
        player.decrementStatistic(statistic, material);
    }

    @Override
    public int getStatistic(@NotNull Statistic statistic, @NotNull Material material) throws IllegalArgumentException {
        return player.getStatistic(statistic, material);
    }

    @Override
    public void incrementStatistic(@NotNull Statistic statistic, @NotNull Material material, int i) throws IllegalArgumentException {
        player.incrementStatistic(statistic, material, i);
    }

    @Override
    public void decrementStatistic(@NotNull Statistic statistic, @NotNull Material material, int i) throws IllegalArgumentException {
        player.decrementStatistic(statistic, material, i);
    }

    @Override
    public void setStatistic(@NotNull Statistic statistic, @NotNull Material material, int i) throws IllegalArgumentException {
        player.setStatistic(statistic, material, i);
    }

    @Override
    public void incrementStatistic(@NotNull Statistic statistic, @NotNull EntityType entityType) throws IllegalArgumentException {
        player.incrementStatistic(statistic, entityType);
    }

    @Override
    public void decrementStatistic(@NotNull Statistic statistic, @NotNull EntityType entityType) throws IllegalArgumentException {
        player.decrementStatistic(statistic, entityType);
    }

    @Override
    public int getStatistic(@NotNull Statistic statistic, @NotNull EntityType entityType) throws IllegalArgumentException {
        return player.getStatistic(statistic, entityType);
    }

    @Override
    public void incrementStatistic(@NotNull Statistic statistic, @NotNull EntityType entityType, int i) throws IllegalArgumentException {
        player.incrementStatistic(statistic, entityType, i);
    }

    @Override
    public void decrementStatistic(@NotNull Statistic statistic, @NotNull EntityType entityType, int i) {
        player.decrementStatistic(statistic, entityType, i);
    }

    @Override
    public void setStatistic(@NotNull Statistic statistic, @NotNull EntityType entityType, int i) {
        player.setStatistic(statistic, entityType, i);
    }

    @Override
    @Deprecated
    public void setBedSpawnLocation(@Nullable Location location) {
        player.setBedSpawnLocation(location);
    }

    @Override
    public void setRespawnLocation(@Nullable Location location) {
        player.setRespawnLocation(location);
    }

    @Override
    @Deprecated
    public void setBedSpawnLocation(@Nullable Location location, boolean b) {
        player.setBedSpawnLocation(location, b);
    }

    @Override
    public void setRespawnLocation(@Nullable Location location, boolean b) {
        player.setRespawnLocation(location, b);
    }

    @Override
    @ApiStatus.Experimental
    @SuppressWarnings("UnstableApiUsage")
    public @NotNull Collection<EnderPearl> getEnderPearls() {
        return player.getEnderPearls();
    }

    @Override
    @ApiStatus.Experimental
    @SuppressWarnings("UnstableApiUsage")
    public @NotNull Input getCurrentInput() {
        return player.getCurrentInput();
    }

    @Override
    @Deprecated
    public void playNote(@NotNull Location location, byte b, byte b1) {
        player.playNote(location, b, b1);
    }

    @Override
    public void playNote(@NotNull Location location, @NotNull Instrument instrument, @NotNull Note note) {
        player.playNote(location, instrument, note);
    }

    @Override
    public void playSound(@NotNull Location location, @NotNull Sound sound, float v, float v1) {
        player.playSound(location, sound, v, v1);
    }

    @Override
    public void playSound(@NotNull Location location, @NotNull String s, float v, float v1) {
        player.playSound(location, s, v, v1);
    }

    @Override
    public void playSound(@NotNull Location location, @NotNull Sound sound, @NotNull SoundCategory soundCategory, float v, float v1) {
        player.playSound(location, sound, soundCategory, v, v1);
    }

    @Override
    public void playSound(@NotNull Location location, @NotNull String s, @NotNull SoundCategory soundCategory, float v, float v1) {
        player.playSound(location, s, soundCategory, v, v1);
    }

    @Override
    public void playSound(@NotNull Location location, @NotNull Sound sound, @NotNull SoundCategory soundCategory, float v, float v1, long l) {
        player.playSound(location, sound, soundCategory, v, v1, l);
    }

    @Override
    public void playSound(@NotNull Location location, @NotNull String s, @NotNull SoundCategory soundCategory, float v, float v1, long l) {
        player.playSound(location, s, soundCategory, v, v1, l);
    }

    @Override
    public void playSound(@NotNull Entity entity, @NotNull Sound sound, float v, float v1) {
        player.playSound(entity, sound, v, v1);
    }

    @Override
    public void playSound(@NotNull Entity entity, @NotNull String s, float v, float v1) {
        player.playSound(entity, s, v, v1);
    }

    @Override
    public void playSound(@NotNull Entity entity, @NotNull Sound sound, @NotNull SoundCategory soundCategory, float v, float v1) {
        player.playSound(entity, sound, soundCategory, v, v1);
    }

    @Override
    public void playSound(@NotNull Entity entity, @NotNull String s, @NotNull SoundCategory soundCategory, float v, float v1) {
        player.playSound(entity, s, soundCategory, v, v1);
    }

    @Override
    public void playSound(@NotNull Entity entity, @NotNull Sound sound, @NotNull SoundCategory soundCategory, float v, float v1, long l) {
        player.playSound(entity, sound, soundCategory, v, v1, l);
    }

    @Override
    public void playSound(@NotNull Entity entity, @NotNull String s, @NotNull SoundCategory soundCategory, float v, float v1, long l) {
        player.playSound(entity, s, soundCategory, v, v1, l);
    }

    @Override
    public void stopSound(@NotNull Sound sound) {
        player.stopSound(sound);
    }

    @Override
    public void stopSound(@NotNull String s) {
        player.stopSound(s);
    }

    @Override
    public void stopSound(@NotNull Sound sound, @Nullable SoundCategory soundCategory) {
        player.stopSound(sound, soundCategory);
    }

    @Override
    public void stopSound(@NotNull String s, @Nullable SoundCategory soundCategory) {
        player.stopSound(s, soundCategory);
    }

    @Override
    public void stopSound(@NotNull SoundCategory soundCategory) {
        player.stopSound(soundCategory);
    }

    @Override
    public void stopAllSounds() {
        player.stopAllSounds();
    }

    @Override
    @Deprecated
    public void playEffect(@NotNull Location location, @NotNull Effect effect, int i) {
        player.playEffect(location, effect, i);
    }

    @Override
    public <T> void playEffect(@NotNull Location location, @NotNull Effect effect, @Nullable T t) {
        player.playEffect(location, effect, t);
    }

    @Override
    public boolean breakBlock(@NotNull Block block) {
        return player.breakBlock(block);
    }

    @Override
    @Deprecated
    public void sendBlockChange(@NotNull Location location, @NotNull Material material, byte b) {
        player.sendBlockChange(location, material, b);
    }

    @Override
    public void sendBlockChange(@NotNull Location location, @NotNull BlockData blockData) {
        player.sendBlockChange(location, blockData);
    }

    @Override
    public void sendBlockChanges(@NotNull Collection<BlockState> collection) {
        player.sendBlockChanges(collection);
    }

    @Override
    @Deprecated
    public void sendBlockChanges(@NotNull Collection<BlockState> collection, boolean b) {
        player.sendBlockChanges(collection, b);
    }

    @Override
    public void sendBlockDamage(@NotNull Location location, float v) {
        player.sendBlockDamage(location, v);
    }

    @Override
    @ApiStatus.Experimental
    @SuppressWarnings("UnstableApiUsage")
    public void sendMultiBlockChange(@NotNull Map<? extends Position, BlockData> map) {
        player.sendMultiBlockChange(map);
    }

    @Override
    public void sendBlockDamage(@NotNull Location location, float v, @NotNull Entity entity) {
        player.sendBlockDamage(location, v, entity);
    }

    @Override
    public void sendBlockDamage(@NotNull Location location, float v, int i) {
        player.sendBlockDamage(location, v, i);
    }

    @Override
    public void sendEquipmentChange(@NotNull LivingEntity livingEntity, @NotNull EquipmentSlot equipmentSlot, @Nullable ItemStack itemStack) {
        player.sendEquipmentChange(livingEntity, equipmentSlot, itemStack);
    }

    @Override
    public void sendEquipmentChange(@NotNull LivingEntity livingEntity, @NotNull Map<EquipmentSlot, ItemStack> map) {
        player.sendEquipmentChange(livingEntity, map);
    }

    @Override
    @Deprecated
    public void sendSignChange(@NotNull Location location, @Nullable List<? extends Component> list, @NotNull DyeColor dyeColor, boolean b) throws IllegalArgumentException {
        player.sendSignChange(location, list, dyeColor, b);
    }

    @Override
    @Deprecated
    public void sendSignChange(@NotNull Location location, @Nullable String @NotNull [] strings) throws IllegalArgumentException {
        player.sendSignChange(location, strings);
    }

    @Override
    @Deprecated
    public void sendSignChange(@NotNull Location location, @Nullable String @NotNull [] strings, @NotNull DyeColor dyeColor) throws IllegalArgumentException {
        player.sendSignChange(location, strings, dyeColor);
    }

    @Override
    @Deprecated
    public void sendSignChange(@NotNull Location location, @Nullable String @NotNull [] strings, @NotNull DyeColor dyeColor, boolean b) throws IllegalArgumentException {
        player.sendSignChange(location, strings, dyeColor, b);
    }

    @Override
    @ApiStatus.Experimental
    @SuppressWarnings("UnstableApiUsage")
    public void sendBlockUpdate(@NotNull Location location, @NotNull TileState tileState) throws IllegalArgumentException {
        player.sendBlockUpdate(location, tileState);
    }

    @Override
    public void sendPotionEffectChange(@NotNull LivingEntity livingEntity, @NotNull PotionEffect potionEffect) {
        player.sendPotionEffectChange(livingEntity, potionEffect);
    }

    @Override
    public void sendPotionEffectChangeRemove(@NotNull LivingEntity livingEntity, @NotNull PotionEffectType potionEffectType) {
        player.sendPotionEffectChangeRemove(livingEntity, potionEffectType);
    }

    @Override
    public void sendMap(@NotNull MapView mapView) {
        player.sendMap(mapView);
    }

    @Override
    public void showWinScreen() {
        player.showWinScreen();
    }

    @Override
    public boolean hasSeenWinScreen() {
        return player.hasSeenWinScreen();
    }

    @Override
    public void setHasSeenWinScreen(boolean b) {
        player.setHasSeenWinScreen(b);
    }

    @Override
    @Deprecated
    public void sendActionBar(@NotNull String s) {
        player.sendActionBar(s);
    }

    @Override
    @Deprecated
    public void sendActionBar(char c, @NotNull String s) {
        player.sendActionBar(c, s);
    }

    @Override
    @Deprecated
    public void sendActionBar(BaseComponent @NotNull ... baseComponents) {
        player.sendActionBar(baseComponents);
    }

    @Override
    @Deprecated
    public void setPlayerListHeaderFooter(BaseComponent @Nullable [] baseComponents, BaseComponent @Nullable [] baseComponents1) {
        player.setPlayerListHeaderFooter(baseComponents, baseComponents1);
    }

    @Override
    @Deprecated
    public void setPlayerListHeaderFooter(@Nullable BaseComponent baseComponent, @Nullable BaseComponent baseComponent1) {
        player.setPlayerListHeaderFooter(baseComponent, baseComponent1);
    }

    @Override
    @Deprecated
    public void setTitleTimes(int i, int i1, int i2) {
        player.setTitleTimes(i, i1, i2);
    }

    @Override
    @Deprecated
    public void setSubtitle(BaseComponent @NotNull [] baseComponents) {
        player.setSubtitle(baseComponents);
    }

    @Override
    @Deprecated
    public void setSubtitle(@NotNull BaseComponent baseComponent) {
        player.setSubtitle(baseComponent);
    }

    @Override
    @Deprecated
    public void showTitle(@Nullable BaseComponent @NotNull [] baseComponents) {
        player.showTitle(baseComponents);
    }

    @Override
    @Deprecated
    public void showTitle(@Nullable BaseComponent baseComponent) {
        player.showTitle(baseComponent);
    }

    @Override
    @Deprecated
    public void showTitle(@Nullable BaseComponent @NotNull [] baseComponents, @Nullable BaseComponent @NotNull [] baseComponents1, int i, int i1, int i2) {
        player.showTitle(baseComponents, baseComponents1, i, i1, i2);
    }

    @Override
    @Deprecated
    public void showTitle(@Nullable BaseComponent baseComponent, @Nullable BaseComponent baseComponent1, int i, int i1, int i2) {
        player.showTitle(baseComponent, baseComponent1, i, i1, i2);
    }

    @Override
    @Deprecated
    public void sendTitle(@NotNull Title title) {
        player.sendTitle(title);
    }

    @Override
    @Deprecated
    public void updateTitle(@NotNull Title title) {
        player.updateTitle(title);
    }

    @Override
    @Deprecated
    public void hideTitle() {
        player.hideTitle();
    }

    @Override
    public void sendHurtAnimation(float v) {
        player.sendHurtAnimation(v);
    }

    @Override
    @ApiStatus.Experimental
    @SuppressWarnings("UnstableApiUsage")
    public void sendLinks(@NotNull ServerLinks serverLinks) {
        player.sendLinks(serverLinks);
    }

    @Override
    public void addCustomChatCompletions(@NotNull Collection<String> collection) {
        player.addCustomChatCompletions(collection);
    }

    @Override
    public void removeCustomChatCompletions(@NotNull Collection<String> collection) {
        player.removeCustomChatCompletions(collection);
    }

    @Override
    public void setCustomChatCompletions(@NotNull Collection<String> collection) {
        player.setCustomChatCompletions(collection);
    }

    @Override
    public void updateInventory() {
        player.updateInventory();
    }

    @Override
    public @Nullable GameMode getPreviousGameMode() {
        return player.getPreviousGameMode();
    }

    @Override
    public void setPlayerTime(long l, boolean b) {
        player.setPlayerTime(l, b);
    }

    @Override
    public long getPlayerTime() {
        return player.getPlayerTime();
    }

    @Override
    public long getPlayerTimeOffset() {
        return player.getPlayerTimeOffset();
    }

    @Override
    public boolean isPlayerTimeRelative() {
        return player.isPlayerTimeRelative();
    }

    @Override
    public void resetPlayerTime() {
        player.resetPlayerTime();
    }

    @Override
    public void setPlayerWeather(@NotNull WeatherType weatherType) {
        player.setPlayerWeather(weatherType);
    }

    @Override
    public @Nullable WeatherType getPlayerWeather() {
        return player.getPlayerWeather();
    }

    @Override
    public void resetPlayerWeather() {
        player.resetPlayerWeather();
    }

    @Override
    public int getExpCooldown() {
        return player.getExpCooldown();
    }

    @Override
    public void setExpCooldown(int i) {
        player.setExpCooldown(i);
    }

    @Override
    public void giveExp(int i, boolean b) {
        player.giveExp(i, b);
    }

    @Override
    public int applyMending(int i) {
        return player.applyMending(i);
    }

    @Override
    public void giveExpLevels(int i) {
        player.giveExpLevels(i);
    }

    @Override
    public float getExp() {
        return player.getExp();
    }

    @Override
    public void setExp(float v) {
        player.setExp(v);
    }

    @Override
    public int getLevel() {
        return player.getLevel();
    }

    @Override
    public void setLevel(int i) {
        player.setLevel(i);
    }

    @Override
    public int getTotalExperience() {
        return player.getTotalExperience();
    }

    @Override
    public void setTotalExperience(int i) {
        player.setTotalExperience(i);
    }

    @Override
    public @Range(from = 0L, to = 2147483647L) int calculateTotalExperiencePoints() {
        return player.calculateTotalExperiencePoints();
    }

    @Override
    public void setExperienceLevelAndProgress(@Range(from = 0L, to = 2147483647L) int i) {
        player.setExperienceLevelAndProgress(i);
    }

    @Override
    public int getExperiencePointsNeededForNextLevel() {
        return player.getExperiencePointsNeededForNextLevel();
    }

    @Override
    public void sendExperienceChange(float v) {
        player.sendExperienceChange(v);
    }

    @Override
    public void sendExperienceChange(float v, int i) {
        player.sendExperienceChange(v, i);
    }

    @Override
    public boolean getAllowFlight() {
        return player.getAllowFlight();
    }

    @Override
    public void setAllowFlight(boolean b) {
        player.setAllowFlight(b);
    }

    @Override
    public void setFlyingFallDamage(@NotNull TriState triState) {
        player.setFlyingFallDamage(triState);
    }

    @Override
    public @NotNull TriState hasFlyingFallDamage() {
        return player.hasFlyingFallDamage();
    }

    @Override
    @Deprecated
    public void hidePlayer(Player player) {
        player.hidePlayer(player);
    }

    @Override
    public void hidePlayer(@NotNull Plugin plugin, Player player) {
        player.hidePlayer(plugin, player);
    }

    @Override
    @Deprecated
    public void showPlayer(Player player) {
        player.showPlayer(player);
    }

    @Override
    public void showPlayer(@NotNull Plugin plugin, Player player) {
        player.showPlayer(plugin, player);
    }

    @Override
    public boolean canSee(Player player) {
        return player.canSee(player);
    }

    @Override
    public void hideEntity(@NotNull Plugin plugin, @NotNull Entity entity) {
        player.hideEntity(plugin, entity);
    }

    @Override
    public void showEntity(@NotNull Plugin plugin, @NotNull Entity entity) {
        player.showEntity(plugin, entity);
    }

    @Override
    public boolean canSee(@NotNull Entity entity) {
        return player.canSee(entity);
    }

    @Override
    public boolean isListed(Player player) {
        return player.isListed(player);
    }

    @Override
    public boolean unlistPlayer(Player player) {
        return player.unlistPlayer(player);
    }

    @Override
    public boolean listPlayer(Player player) {
        return player.listPlayer(player);
    }

    @Override
    public boolean isFlying() {
        return player.isFlying();
    }

    @Override
    public void setFlying(boolean b) {
        player.setFlying(b);
    }

    @Override
    public void setFlySpeed(float v) throws IllegalArgumentException {
        player.setFlySpeed(v);
    }

    @Override
    public void setWalkSpeed(float v) throws IllegalArgumentException {
        player.setWalkSpeed(v);
    }

    @Override
    public float getFlySpeed() {
        return player.getFlySpeed();
    }

    @Override
    public float getWalkSpeed() {
        return player.getWalkSpeed();
    }

    @Override
    @Deprecated
    public void setTexturePack(@NotNull String s) {
        player.setTexturePack(s);
    }

    @Override
    @Deprecated
    public void setResourcePack(@NotNull String s) {
        player.setResourcePack(s);
    }

    @Override
    @Deprecated
    public void setResourcePack(@NotNull String s, byte @Nullable [] bytes) {
        player.setResourcePack(s, bytes);
    }

    @Override
    @Deprecated
    public void setResourcePack(@NotNull String s, byte @Nullable [] bytes, @Nullable String s1) {
        player.setResourcePack(s, bytes, s1);
    }

    @Override
    @Deprecated
    public void setResourcePack(@NotNull String s, byte @Nullable [] bytes, boolean b) {
        player.setResourcePack(s, bytes, b);
    }

    @Override
    @Deprecated
    public void setResourcePack(@NotNull String s, byte @Nullable [] bytes, @Nullable String s1, boolean b) {
        player.setResourcePack(s, bytes, s1, b);
    }

    @Override
    @Deprecated
    public void setResourcePack(@NotNull UUID uuid, @NotNull String s, byte @Nullable [] bytes, @Nullable String s1, boolean b) {
        player.setResourcePack(uuid, s, bytes, s1, b);
    }

    @Override
    public void setResourcePack(@NotNull UUID uuid, @NotNull String s, byte @Nullable [] bytes, @Nullable Component component, boolean b) {
        player.setResourcePack(uuid, s, bytes, component, b);
    }

    @Override
    public PlayerResourcePackStatusEvent.@NotNull Status getResourcePackStatus() {
        return player.getResourcePackStatus();
    }

    @Override
    public void addResourcePack(@NotNull UUID uuid, @NotNull String s, byte @NotNull [] bytes, @Nullable String s1, boolean b) {
        player.addResourcePack(uuid, s, bytes, s1, b);
    }

    @Override
    public void removeResourcePack(@NotNull UUID uuid) {
        player.removeResourcePack(uuid);
    }

    @Override
    public void removeResourcePacks() {
        player.removeResourcePacks();
    }

    @Override
    public @NotNull Scoreboard getScoreboard() {
        return player.getScoreboard();
    }

    @Override
    public void setScoreboard(@NotNull Scoreboard scoreboard) throws IllegalArgumentException, IllegalStateException {
        player.setScoreboard(scoreboard);
    }

    @Override
    public @Nullable WorldBorder getWorldBorder() {
        return player.getWorldBorder();
    }

    @Override
    public void setWorldBorder(@Nullable WorldBorder worldBorder) {
        player.setWorldBorder(worldBorder);
    }

    @Override
    public void sendHealthUpdate(double v, int i, float v1) {
        player.sendHealthUpdate(v, i, v1);
    }

    @Override
    public void sendHealthUpdate() {
        player.sendHealthUpdate();
    }

    @Override
    public boolean isHealthScaled() {
        return player.isHealthScaled();
    }

    @Override
    public void setHealthScaled(boolean b) {
        player.setHealthScaled(b);
    }

    @Override
    public void setHealthScale(double v) throws IllegalArgumentException {
        player.setHealthScale(v);
    }

    @Override
    public double getHealthScale() {
        return player.getHealthScale();
    }

    @Override
    public @Nullable Entity getSpectatorTarget() {
        return player.getSpectatorTarget();
    }

    @Override
    public void setSpectatorTarget(@Nullable Entity entity) {
        player.setSpectatorTarget(entity);
    }

    @Override
    @Deprecated
    public void sendTitle(@Nullable String s, @Nullable String s1) {
        player.sendTitle(s, s1);
    }

    @Override
    @Deprecated
    public void sendTitle(@Nullable String s, @Nullable String s1, int i, int i1, int i2) {
        player.sendTitle(s, s1, i, i1, i2);
    }

    @Override
    public void resetTitle() {
        player.resetTitle();
    }

    @Override
    public void spawnParticle(@NotNull Particle particle, @NotNull Location location, int i) {
        player.spawnParticle(particle, location, i);
    }

    @Override
    public void spawnParticle(@NotNull Particle particle, double v, double v1, double v2, int i) {
        player.spawnParticle(particle, v, v1, v2, i);
    }

    @Override
    public <T> void spawnParticle(@NotNull Particle particle, @NotNull Location location, int i, @Nullable T t) {
        player.spawnParticle(particle, location, i, t);
    }

    @Override
    public <T> void spawnParticle(@NotNull Particle particle, double v, double v1, double v2, int i, @Nullable T t) {
        player.spawnParticle(particle, v, v1, v2, i, t);
    }

    @Override
    public void spawnParticle(@NotNull Particle particle, @NotNull Location location, int i, double v, double v1, double v2) {
        player.spawnParticle(particle, location, i, v, v1, v2);
    }

    @Override
    public void spawnParticle(@NotNull Particle particle, double v, double v1, double v2, int i, double v3, double v4, double v5) {
        player.spawnParticle(particle, v, v1, v2, i, v3, v4, v5);
    }

    @Override
    public <T> void spawnParticle(@NotNull Particle particle, @NotNull Location location, int i, double v, double v1, double v2, @Nullable T t) {
        player.spawnParticle(particle, location, i, v, v1, v2, t);
    }

    @Override
    public <T> void spawnParticle(@NotNull Particle particle, double v, double v1, double v2, int i, double v3, double v4, double v5, @Nullable T t) {
        player.spawnParticle(particle, v, v1, v2, i, v3, v4, v5, t);
    }

    @Override
    public void spawnParticle(@NotNull Particle particle, @NotNull Location location, int i, double v, double v1, double v2, double v3) {
        player.spawnParticle(particle, location, i, v, v1, v2, v3);
    }

    @Override
    public void spawnParticle(@NotNull Particle particle, double v, double v1, double v2, int i, double v3, double v4, double v5, double v6) {
        player.spawnParticle(particle, v, v1, v2, i, v3, v4, v5, v6);
    }

    @Override
    public <T> void spawnParticle(@NotNull Particle particle, @NotNull Location location, int i, double v, double v1, double v2, double v3, @Nullable T t) {
        player.spawnParticle(particle, location, i, v, v1, v2, v3, t);
    }

    @Override
    public <T> void spawnParticle(@NotNull Particle particle, double v, double v1, double v2, int i, double v3, double v4, double v5, double v6, @Nullable T t) {
        player.spawnParticle(particle, v, v1, v2, i, v3, v4, v5, v6, t);
    }

    @Override
    public <T> void spawnParticle(@NotNull Particle particle, @NotNull Location location, int i, double v, double v1, double v2, double v3, @Nullable T t, boolean b) {
        player.spawnParticle(particle, location, i, v, v1, v2, v3, t, b);
    }

    @Override
    public <T> void spawnParticle(@NotNull Particle particle, double v, double v1, double v2, int i, double v3, double v4, double v5, double v6, @Nullable T t, boolean b) {
        player.spawnParticle(particle, v, v1, v2, i, v3, v4, v5, v6, t, b);
    }

    @Override
    public @NotNull AdvancementProgress getAdvancementProgress(@NotNull Advancement advancement) {
        return player.getAdvancementProgress(advancement);
    }

    @Override
    public int getClientViewDistance() {
        return player.getClientViewDistance();
    }

    @Override
    public @NotNull Locale locale() {
        return player.locale();
    }

    @Override
    public int getPing() {
        return player.getPing();
    }

    @Override
    @Deprecated
    public @NotNull String getLocale() {
        return player.getLocale();
    }

    @Override
    public boolean getAffectsSpawning() {
        return player.getAffectsSpawning();
    }

    @Override
    public void setAffectsSpawning(boolean b) {
        player.setAffectsSpawning(b);
    }

    @Override
    public int getViewDistance() {
        return player.getViewDistance();
    }

    @Override
    public void setViewDistance(int i) {
        player.setViewDistance(i);
    }

    @Override
    public int getSimulationDistance() {
        return player.getSimulationDistance();
    }

    @Override
    public void setSimulationDistance(int i) {
        player.setSimulationDistance(i);
    }

    @Override
    public int getSendViewDistance() {
        return player.getSendViewDistance();
    }

    @Override
    public void setSendViewDistance(int i) {
        player.setSendViewDistance(i);
    }

    @Override
    public void updateCommands() {
        player.updateCommands();
    }

    @Override
    public void openBook(@NotNull ItemStack itemStack) {
        player.openBook(itemStack);
    }

    @Override
    @Deprecated
    public void openSign(@NotNull Sign sign) {
        player.openSign(sign);
    }

    @Override
    public void openSign(@NotNull Sign sign, @NotNull Side side) {
        player.openSign(sign, side);
    }

    @Override
    public boolean dropItem(boolean b) {
        return player.dropItem(b);
    }

    @Override
    public @Nullable Item dropItem(int i, int i1, boolean b, @Nullable Consumer<Item> consumer) {
        return player.dropItem(i, i1, b, consumer);
    }

    @Override
    public @Nullable Item dropItem(@NotNull EquipmentSlot equipmentSlot, int i, boolean b, @Nullable Consumer<Item> consumer) {
        return player.dropItem(equipmentSlot, i, b, consumer);
    }

    @Override
    public @Nullable Item dropItem(@NotNull ItemStack itemStack, boolean b, @Nullable Consumer<Item> consumer) {
        return player.dropItem(itemStack, b, consumer);
    }

    @Override
    public float getExhaustion() {
        return player.getExhaustion();
    }

    @Override
    public void setExhaustion(float v) {
        player.setExhaustion(v);
    }

    @Override
    public float getSaturation() {
        return player.getSaturation();
    }

    @Override
    public void setSaturation(float v) {
        player.setSaturation(v);
    }

    @Override
    public int getFoodLevel() {
        return player.getFoodLevel();
    }

    @Override
    public void setFoodLevel(int i) {
        player.setFoodLevel(i);
    }

    @Override
    public int getSaturatedRegenRate() {
        return player.getSaturatedRegenRate();
    }

    @Override
    public void setSaturatedRegenRate(int i) {
        player.setSaturatedRegenRate(i);
    }

    @Override
    public int getUnsaturatedRegenRate() {
        return player.getUnsaturatedRegenRate();
    }

    @Override
    public void setUnsaturatedRegenRate(int i) {
        player.setUnsaturatedRegenRate(i);
    }

    @Override
    public int getStarvationRate() {
        return player.getStarvationRate();
    }

    @Override
    public void setStarvationRate(int i) {
        player.setStarvationRate(i);
    }

    @Override
    public @Nullable Location getLastDeathLocation() {
        return player.getLastDeathLocation();
    }

    @Override
    public void setLastDeathLocation(@Nullable Location location) {
        player.setLastDeathLocation(location);
    }

    @Override
    public @Nullable Firework fireworkBoost(@NotNull ItemStack itemStack) {
        return player.fireworkBoost(itemStack);
    }

    @Override
    public void showDemoScreen() {
        player.showDemoScreen();
    }

    @Override
    public boolean isAllowingServerListings() {
        return player.isAllowingServerListings();
    }

    @Override
    public @NotNull PlayerProfile getPlayerProfile() {
        return player.getPlayerProfile();
    }

    @Override
    public boolean isBanned() {
        return player.isBanned();
    }

    @Override
    public <E extends BanEntry<? super PlayerProfile>> @Nullable E ban(@Nullable String s, @Nullable Date date, @Nullable String s1) {
        return player.ban(s, date, s1);
    }

    @Override
    public <E extends BanEntry<? super PlayerProfile>> @Nullable E ban(@Nullable String s, @Nullable Instant instant, @Nullable String s1) {
        return player.ban(s, instant, s1);
    }

    @Override
    public <E extends BanEntry<? super PlayerProfile>> @Nullable E ban(@Nullable String s, @Nullable Duration duration, @Nullable String s1) {
        return player.ban(s, duration, s1);
    }

    @Override
    public boolean isWhitelisted() {
        return player.isWhitelisted();
    }

    @Override
    public void setWhitelisted(boolean b) {
        player.setWhitelisted(b);
    }

    @Override
    public @Nullable Player getPlayer() {
        return player.getPlayer();
    }

    @Override
    public long getFirstPlayed() {
        return player.getFirstPlayed();
    }

    @Override
    @Deprecated
    public long getLastPlayed() {
        return player.getLastPlayed();
    }

    @Override
    public boolean hasPlayedBefore() {
        return player.hasPlayedBefore();
    }

    @Override
    public void setPlayerProfile(@NotNull PlayerProfile playerProfile) {
        player.setPlayerProfile(playerProfile);
    }

    @Override
    public float getCooldownPeriod() {
        return player.getCooldownPeriod();
    }

    @Override
    public float getCooledAttackStrength(float v) {
        return player.getCooledAttackStrength(v);
    }

    @Override
    public void resetCooldown() {
        player.resetCooldown();
    }

    @Override
    public <T> @NotNull T getClientOption(@NotNull ClientOption<T> clientOption) {
        return player.getClientOption(clientOption);
    }

    @Override
    public void sendOpLevel(byte b) {
        player.sendOpLevel(b);
    }

    @Override
    @Deprecated
    public void addAdditionalChatCompletions(@NotNull Collection<String> collection) {
        player.addAdditionalChatCompletions(collection);
    }

    @Override
    @Deprecated
    public void removeAdditionalChatCompletions(@NotNull Collection<String> collection) {
        player.removeAdditionalChatCompletions(collection);
    }

    @Override
    public @Nullable String getClientBrandName() {
        return player.getClientBrandName();
    }

    @Override
    public void setRotation(float v, float v1) {
        player.setRotation(v, v1);
    }

    @Override
    public boolean teleport(@NotNull Location location, @NotNull PlayerTeleportEvent.TeleportCause teleportCause, @NotNull TeleportFlag @NotNull ... teleportFlags) {
        return player.teleport(location, teleportCause, teleportFlags);
    }

    @Override
    public void lookAt(double v, double v1, double v2, @NotNull LookAnchor lookAnchor) {
        player.lookAt(v, v1, v2, lookAnchor);
    }

    @Override
    public boolean teleport(@NotNull Location location) {
        return player.teleport(location);
    }

    @Override
    public boolean teleport(@NotNull Location location, @NotNull PlayerTeleportEvent.TeleportCause teleportCause) {
        return player.teleport(location, teleportCause);
    }

    @Override
    public boolean teleport(@NotNull Entity entity) {
        return player.teleport(entity);
    }

    @Override
    public boolean teleport(@NotNull Entity entity, @NotNull PlayerTeleportEvent.TeleportCause teleportCause) {
        return player.teleport(entity, teleportCause);
    }

    @Override
    public @NotNull CompletableFuture<Boolean> teleportAsync(@NotNull Location location, @NotNull PlayerTeleportEvent.TeleportCause teleportCause, @NotNull TeleportFlag @NotNull ... teleportFlags) {
        return player.teleportAsync(location, teleportCause, teleportFlags);
    }

    @Override
    public @NotNull List<Entity> getNearbyEntities(double v, double v1, double v2) {
        return player.getNearbyEntities(v, v1, v2);
    }

    @Override
    public int getEntityId() {
        return player.getEntityId();
    }

    @Override
    public int getFireTicks() {
        return player.getFireTicks();
    }

    @Override
    public int getMaxFireTicks() {
        return player.getMaxFireTicks();
    }

    @Override
    public void setFireTicks(int i) {
        player.setFireTicks(i);
    }

    @Override
    public void setVisualFire(boolean b) {
        player.setVisualFire(b);
    }

    @Override
    public boolean isVisualFire() {
        return player.isVisualFire();
    }

    @Override
    public int getFreezeTicks() {
        return player.getFreezeTicks();
    }

    @Override
    public int getMaxFreezeTicks() {
        return player.getMaxFreezeTicks();
    }

    @Override
    public void setFreezeTicks(int i) {
        player.setFreezeTicks(i);
    }

    @Override
    public boolean isFrozen() {
        return player.isFrozen();
    }

    @Override
    public void lookAt(@NotNull Entity entity, @NotNull LookAnchor lookAnchor, @NotNull LookAnchor lookAnchor1) {
        player.lookAt(entity, lookAnchor, lookAnchor1);
    }

    @Override
    public void showElderGuardian(boolean b) {
        player.showElderGuardian(b);
    }

    @Override
    public int getWardenWarningCooldown() {
        return player.getWardenWarningCooldown();
    }

    @Override
    public void setWardenWarningCooldown(int i) {
        player.setWardenWarningCooldown(i);
    }

    @Override
    public int getWardenTimeSinceLastWarning() {
        return player.getWardenTimeSinceLastWarning();
    }

    @Override
    public void setWardenTimeSinceLastWarning(int i) {
        player.setWardenTimeSinceLastWarning(i);
    }

    @Override
    public int getWardenWarningLevel() {
        return player.getWardenWarningLevel();
    }

    @Override
    public void setWardenWarningLevel(int i) {
        player.setWardenWarningLevel(i);
    }

    @Override
    public void increaseWardenWarningLevel() {
        player.increaseWardenWarningLevel();
    }

    @Override
    public @NotNull Duration getIdleDuration() {
        return player.getIdleDuration();
    }

    @Override
    public void resetIdleDuration() {
        player.resetIdleDuration();
    }

    @Override
    @ApiStatus.Experimental
    @SuppressWarnings("UnstableApiUsage")
    public @Unmodifiable @NotNull Set<Long> getSentChunkKeys() {
        return player.getSentChunkKeys();
    }

    @Override
    @ApiStatus.Experimental
    @SuppressWarnings("UnstableApiUsage")
    public @Unmodifiable @NotNull Set<Chunk> getSentChunks() {
        return player.getSentChunks();
    }

    @Override
    public boolean isChunkSent(long l) {
        return player.isChunkSent(l);
    }

    @Override
    public @NotNull Spigot spigot() {
        return player.spigot();
    }

    @Override
    public @NotNull Component name() {
        return player.name();
    }

    @Override
    public @NotNull Component teamDisplayName() {
        return player.teamDisplayName();
    }

    @Override
    public @org.jetbrains.annotations.Nullable Location getOrigin() {
        return player.getOrigin();
    }

    @Override
    public boolean fromMobSpawner() {
        return player.fromMobSpawner();
    }

    @NotNull
    @Override
    public CreatureSpawnEvent.SpawnReason getEntitySpawnReason() {
        return player.getEntitySpawnReason();
    }

    @Override
    public boolean isUnderWater() {
        return player.isUnderWater();
    }

    @Override
    public boolean isInRain() {
        return player.isInRain();
    }

    @Override
    public boolean isInBubbleColumn() {
        return player.isInBubbleColumn();
    }

    @Override
    public boolean isInWaterOrRain() {
        return player.isInWaterOrRain();
    }

    @Override
    public boolean isInWaterOrBubbleColumn() {
        return player.isInWaterOrBubbleColumn();
    }

    @Override
    public boolean isInWaterOrRainOrBubbleColumn() {
        return player.isInWaterOrRainOrBubbleColumn();
    }

    @Override
    public boolean isInLava() {
        return player.isInLava();
    }

    @Override
    public boolean isTicking() {
        return player.isTicking();
    }

    @Override
    @Deprecated
    public @NotNull Set<Player> getTrackedPlayers() {
        return player.getTrackedPlayers();
    }

    @Override
    public boolean spawnAt(@NotNull Location location, @NotNull CreatureSpawnEvent.SpawnReason spawnReason) {
        return player.spawnAt(location, spawnReason);
    }

    @Override
    public boolean isInPowderedSnow() {
        return player.isInPowderedSnow();
    }

    @Override
    public double getX() {
        return player.getX();
    }

    @Override
    public double getY() {
        return player.getY();
    }

    @Override
    public double getZ() {
        return player.getZ();
    }

    @Override
    public float getPitch() {
        return player.getPitch();
    }

    @Override
    public float getYaw() {
        return player.getYaw();
    }

    @Override
    public boolean collidesAt(@NotNull Location location) {
        return player.collidesAt(location);
    }

    @Override
    public boolean wouldCollideUsing(@NotNull BoundingBox boundingBox) {
        return player.wouldCollideUsing(boundingBox);
    }

    @Override
    public @NotNull EntityScheduler getScheduler() {
        return player.getScheduler();
    }

    @Override
    public @NotNull String getScoreboardEntryName() {
        return player.getScoreboardEntryName();
    }

    @Override
    public void broadcastHurtAnimation(@NotNull Collection<Player> collection) {
        player.broadcastHurtAnimation(collection);
    }

    @Override
    public void sendEntityEffect(@NotNull EntityEffect entityEffect, @NotNull Entity entity) {
        player.sendEntityEffect(entityEffect, entity);
    }

    @Override
    public @NotNull PlayerGiveResult give(@NotNull Collection<ItemStack> collection, boolean b) {
        return player.give(collection, b);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return player.serialize();
    }

    @Override
    public @NotNull TriState getFrictionState() {
        return player.getFrictionState();
    }

    @Override
    public void setFrictionState(@NotNull TriState triState) {
        player.setFrictionState(triState);
    }

    @Override
    public @org.jetbrains.annotations.Nullable Component customName() {
        return player.customName();
    }

    @Override
    public void customName(@org.jetbrains.annotations.Nullable Component component) {
        player.customName(component);
    }

    @Override
    @Deprecated
    public @org.jetbrains.annotations.Nullable String getCustomName() {
        return player.getCustomName();
    }

    @Override
    @Deprecated
    public void setCustomName(@org.jetbrains.annotations.Nullable String s) {
        player.setCustomName(s);
    }

    @Override
    public @org.jetbrains.annotations.Nullable AttributeInstance getAttribute(@NotNull Attribute attribute) {
        return player.getAttribute(attribute);
    }

    @Override
    public void registerAttribute(@NotNull Attribute attribute) {
        player.registerAttribute(attribute);
    }

    @Override
    public void damage(double v) {
        player.damage(v);
    }

    @Override
    public void damage(double v, @org.jetbrains.annotations.Nullable Entity entity) {
        player.damage(v, entity);
    }

    @Override
    @ApiStatus.Experimental
    @SuppressWarnings("UnstableApiUsage")
    public void damage(double v, @NotNull DamageSource damageSource) {
        player.damage(v, damageSource);
    }

    @Override
    public double getHealth() {
        return player.getHealth();
    }

    @Override
    public void setHealth(double v) {
        player.setHealth(v);
    }

    @Override
    public void heal(double v, @NotNull EntityRegainHealthEvent.RegainReason regainReason) {
        player.heal(v, regainReason);
    }

    @Override
    public double getAbsorptionAmount() {
        return player.getAbsorptionAmount();
    }

    @Override
    public void setAbsorptionAmount(double v) {
        player.setAbsorptionAmount(v);
    }

    @Override
    @Deprecated
    public double getMaxHealth() {
        return player.getMaxHealth();
    }

    @Override
    @Deprecated
    public void setMaxHealth(double v) {
        player.setMaxHealth(v);
    }

    @Override
    @Deprecated
    public void resetMaxHealth() {
        player.resetMaxHealth();
    }

    @Override
    public void setMetadata(@NotNull String s, @NotNull MetadataValue metadataValue) {
        player.setMetadata(s, metadataValue);
    }

    @Override
    public @NotNull List<MetadataValue> getMetadata(@NotNull String s) {
        return player.getMetadata(s);
    }

    @Override
    public boolean hasMetadata(@NotNull String s) {
        return player.hasMetadata(s);
    }

    @Override
    public void removeMetadata(@NotNull String s, @NotNull Plugin plugin) {
        player.removeMetadata(s, plugin);
    }

    @Override
    public boolean isPermissionSet(@NotNull String s) {
        return player.isPermissionSet(s);
    }

    @Override
    public boolean isPermissionSet(@NotNull Permission permission) {
        return player.isPermissionSet(permission);
    }

    @Override
    public boolean hasPermission(@NotNull String s) {
        return player.hasPermission(s);
    }

    @Override
    public boolean hasPermission(@NotNull Permission permission) {
        return player.hasPermission(permission);
    }

    @Override
    public @NotNull PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String s, boolean b) {
        return player.addAttachment(plugin, s, b);
    }

    @Override
    public @NotNull PermissionAttachment addAttachment(@NotNull Plugin plugin) {
        return player.addAttachment(plugin);
    }

    @Override
    public @org.jetbrains.annotations.Nullable PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String s, boolean b, int i) {
        return player.addAttachment(plugin, s, b, i);
    }

    @Override
    public @org.jetbrains.annotations.Nullable PermissionAttachment addAttachment(@NotNull Plugin plugin, int i) {
        return player.addAttachment(plugin, i);
    }

    @Override
    public void removeAttachment(@NotNull PermissionAttachment permissionAttachment) {
        player.removeAttachment(permissionAttachment);
    }

    @Override
    public void recalculatePermissions() {
        player.recalculatePermissions();
    }

    @Override
    public @NotNull Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return player.getEffectivePermissions();
    }

    @Override
    public boolean isOp() {
        return player.isOp();
    }

    @Override
    public void setOp(boolean b) {
        player.setOp(b);
    }

    @Override
    public @NotNull PersistentDataContainer getPersistentDataContainer() {
        return player.getPersistentDataContainer();
    }

    @Override
    public void sendPluginMessage(@NotNull Plugin plugin, @NotNull String s, byte @NotNull [] bytes) {
        player.sendPluginMessage(plugin, s, bytes);
    }

    @Override
    public @NotNull Set<String> getListeningPluginChannels() {
        return player.getListeningPluginChannels();
    }

    @Override
    public <T extends Projectile> @NotNull T launchProjectile(@NotNull Class<? extends T> aClass) {
        return player.launchProjectile(aClass);
    }

    @Override
    public <T extends Projectile> @NotNull T launchProjectile(@NotNull Class<? extends T> aClass, @org.jetbrains.annotations.Nullable Vector vector) {
        return player.launchProjectile(aClass, vector);
    }

    @Override
    public <T extends Projectile> @NotNull T launchProjectile(@NotNull Class<? extends T> aClass, @org.jetbrains.annotations.Nullable Vector vector, @org.jetbrains.annotations.Nullable Consumer<? super T> consumer) {
        return player.launchProjectile(aClass, vector, consumer);
    }
}
