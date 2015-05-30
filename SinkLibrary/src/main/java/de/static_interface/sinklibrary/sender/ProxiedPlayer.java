/*
 * Copyright (c) 2013 - 2014 http://static-interface.de and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.static_interface.sinklibrary.sender;

import org.bukkit.Achievement;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.scoreboard.CraftScoreboard;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageRecipient;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ProxiedPlayer extends ProxiedCommandSenderConversable implements Player {

    public ProxiedPlayer(Player base, CommandSender faker) {
        super(base, faker);
    }

    @Override
    public String getDisplayName() {
        return ((Player) getCallee()).getDisplayName();
    }

    @Override
    public void setDisplayName(String s) {
        ((Player) getCallee()).setDisplayName(s);
    }

    @Override
    public String getPlayerListName() {
        return ((Player) getCallee()).getPlayerListName();
    }

    @Override
    public void setPlayerListName(String s) {
        ((Player) getCallee()).setPlayerListName(s);
    }

    @Override
    public Location getCompassTarget() {
        return ((Player) getCallee()).getCompassTarget();
    }

    @Override
    public void setCompassTarget(Location location) {
        ((Player) getCallee()).setCompassTarget(location);
    }

    @Override
    public InetSocketAddress getAddress() {
        return ((Player) getCallee()).getAddress();
    }

    @Override
    public void kickPlayer(String s) {
        ((Player) getCallee()).kickPlayer(s);
    }

    @Override
    public void chat(String s) {
        ((Player) getCallee()).chat(s);
    }

    @Override
    public boolean performCommand(String s) {
        return ((Player) getCallee()).performCommand(s);
    }

    @Override
    public boolean isSneaking() {
        return ((Player) getCallee()).isSneaking();
    }

    @Override
    public void setSneaking(boolean b) {
        ((Player) getCallee()).setSneaking(b);
    }

    @Override
    public boolean isSprinting() {
        return ((Player) getCallee()).isSprinting();
    }

    @Override
    public void setSprinting(boolean b) {
        ((Player) getCallee()).setSprinting(b);
    }

    @Override
    public void saveData() {
        ((Player) getCallee()).saveData();
    }

    @Override
    public void loadData() {
        ((Player) getCallee()).loadData();
    }

    @Override
    public boolean isSleepingIgnored() {
        return ((Player) getCallee()).isSleepingIgnored();
    }

    @Override
    public void setSleepingIgnored(boolean b) {
        ((Player) getCallee()).setSleepingIgnored(b);
    }

    @Override
    @Deprecated
    public void playNote(Location location, byte b, byte b2) {
        ((Player) getCallee()).playNote(location, b, b2);
    }

    @Override
    public void playNote(Location location, Instrument instrument, Note note) {
        ((Player) getCallee()).playNote(location, instrument, note);
    }

    @Override
    public void playSound(Location location, Sound sound, float v, float v2) {
        ((Player) getCallee()).playSound(location, sound, v, v2);
    }

    @Override
    @Deprecated
    public void playSound(Location location, String s, float v, float v2) {
        ((Player) getCallee()).playSound(location, s, v, v2);
    }

    @Override
    @Deprecated
    public void playEffect(Location location, Effect effect, int i) {
        ((Player) getCallee()).playEffect(location, effect, i);
    }

    @Override
    public <T> void playEffect(Location location, Effect effect, T t) {
        ((Player) getCallee()).playEffect(location, effect, t);
    }

    @Override
    @Deprecated
    public void sendBlockChange(Location location, Material material, byte b) {
        ((Player) getCallee()).sendBlockChange(location, material, b);
    }

    @Override
    @Deprecated
    public boolean sendChunkChange(Location location, int i, int i2, int i3, byte[] bytes) {
        return ((Player) getCallee()).sendChunkChange(location, i, i2, i3, bytes);
    }

    @Override
    @Deprecated
    public void sendBlockChange(Location location, int i, byte b) {
        ((Player) getCallee()).sendBlockChange(location, i, b);
    }

    @Override
    public void sendSignChange(Location location, String[] strings) throws IllegalArgumentException {
        ((Player) getCallee()).sendSignChange(location, strings);
    }

    @Override
    public void sendMap(MapView mapView) {
        ((Player) getCallee()).sendMap(mapView);
    }

    @Override
    @Deprecated
    public void updateInventory() {
        ((Player) getCallee()).updateInventory();
    }

    @Override
    public void awardAchievement(Achievement achievement) {
        ((Player) getCallee()).awardAchievement(achievement);
    }

    @Override
    public void removeAchievement(Achievement achievement) {
        ((Player) getCallee()).removeAchievement(achievement);
    }

    @Override
    public boolean hasAchievement(Achievement achievement) {
        return ((Player) getCallee()).hasAchievement(achievement);
    }

    @Override
    public void incrementStatistic(Statistic statistic) throws IllegalArgumentException {
        ((Player) getCallee()).incrementStatistic(statistic);
    }

    @Override
    public void decrementStatistic(Statistic statistic) throws IllegalArgumentException {
        ((Player) getCallee()).decrementStatistic(statistic);
    }

    @Override
    public void incrementStatistic(Statistic statistic, int i) throws IllegalArgumentException {
        ((Player) getCallee()).incrementStatistic(statistic, i);
    }

    @Override
    public void decrementStatistic(Statistic statistic, int i) throws IllegalArgumentException {
        ((Player) getCallee()).decrementStatistic(statistic, i);
    }

    @Override
    public void setStatistic(Statistic statistic, int i) throws IllegalArgumentException {
        ((Player) getCallee()).setStatistic(statistic, i);
    }

    @Override
    public int getStatistic(Statistic statistic) throws IllegalArgumentException {
        return ((Player) getCallee()).getStatistic(statistic);
    }

    @Override
    public void incrementStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
        ((Player) getCallee()).incrementStatistic(statistic, material);
    }

    @Override
    public void decrementStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
        ((Player) getCallee()).decrementStatistic(statistic, material);
    }

    @Override
    public int getStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
        return ((Player) getCallee()).getStatistic(statistic, material);
    }

    @Override
    public void incrementStatistic(Statistic statistic, Material material, int i) throws IllegalArgumentException {
        ((Player) getCallee()).incrementStatistic(statistic, material, i);
    }

    @Override
    public void decrementStatistic(Statistic statistic, Material material, int i) throws IllegalArgumentException {
        ((Player) getCallee()).decrementStatistic(statistic, material, i);
    }

    @Override
    public void setStatistic(Statistic statistic, Material material, int i) throws IllegalArgumentException {
        ((Player) getCallee()).setStatistic(statistic, material, i);
    }

    @Override
    public void incrementStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
        ((Player) getCallee()).incrementStatistic(statistic, entityType);
    }

    @Override
    public void decrementStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
        ((Player) getCallee()).decrementStatistic(statistic, entityType);
    }

    @Override
    public int getStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
        return ((Player) getCallee()).getStatistic(statistic, entityType);
    }

    @Override
    public void incrementStatistic(Statistic statistic, EntityType entityType, int i) throws IllegalArgumentException {
        ((Player) getCallee()).incrementStatistic(statistic, entityType, i);
    }

    @Override
    public void decrementStatistic(Statistic statistic, EntityType entityType, int i) {
        ((Player) getCallee()).decrementStatistic(statistic, entityType, i);
    }

    @Override
    public void setStatistic(Statistic statistic, EntityType entityType, int i) {
        ((Player) getCallee()).setStatistic(statistic, entityType, i);
    }

    @Override
    public void setPlayerTime(long l, boolean b) {
        ((Player) getCallee()).setPlayerTime(l, b);
    }

    @Override
    public long getPlayerTime() {
        return ((Player) getCallee()).getPlayerTime();
    }

    @Override
    public long getPlayerTimeOffset() {
        return ((Player) getCallee()).getPlayerTimeOffset();
    }

    @Override
    public boolean isPlayerTimeRelative() {
        return ((Player) getCallee()).isPlayerTimeRelative();
    }

    @Override
    public void resetPlayerTime() {
        ((Player) getCallee()).resetPlayerTime();
    }

    @Override
    public WeatherType getPlayerWeather() {
        return ((Player) getCallee()).getPlayerWeather();
    }

    @Override
    public void setPlayerWeather(WeatherType weatherType) {
        ((Player) getCallee()).setPlayerWeather(weatherType);
    }

    @Override
    public void resetPlayerWeather() {
        ((Player) getCallee()).resetPlayerWeather();
    }

    @Override
    public void giveExp(int i) {
        ((Player) getCallee()).giveExp(i);
    }

    @Override
    public void giveExpLevels(int i) {
        ((Player) getCallee()).giveExpLevels(i);
    }

    @Override
    public float getExp() {
        return ((Player) getCallee()).getExp();
    }

    @Override
    public void setExp(float v) {
        ((Player) getCallee()).setExp(v);
    }

    @Override
    public int getLevel() {
        return ((Player) getCallee()).getLevel();
    }

    @Override
    public void setLevel(int i) {
        ((Player) getCallee()).setLevel(i);
    }

    @Override
    public int getTotalExperience() {
        return ((Player) getCallee()).getTotalExperience();
    }

    @Override
    public void setTotalExperience(int i) {
        ((Player) getCallee()).setTotalExperience(i);
    }

    @Override
    public float getExhaustion() {
        return ((Player) getCallee()).getExhaustion();
    }

    @Override
    public void setExhaustion(float v) {
        ((Player) getCallee()).setExhaustion(v);
    }

    @Override
    public float getSaturation() {
        return ((Player) getCallee()).getSaturation();
    }

    @Override
    public void setSaturation(float v) {
        ((Player) getCallee()).setSaturation(v);
    }

    @Override
    public int getFoodLevel() {
        return ((Player) getCallee()).getFoodLevel();
    }

    @Override
    public void setFoodLevel(int i) {
        ((Player) getCallee()).setFoodLevel(i);
    }

    @Override
    public Location getBedSpawnLocation() {
        return ((Player) getCallee()).getBedSpawnLocation();
    }

    @Override
    public void setBedSpawnLocation(Location location) {
        ((Player) getCallee()).setBedSpawnLocation(location);
    }

    @Override
    public void setBedSpawnLocation(Location location, boolean b) {
        ((Player) getCallee()).setBedSpawnLocation(location, b);
    }

    @Override
    public boolean getAllowFlight() {
        return ((Player) getCallee()).getAllowFlight();
    }

    @Override
    public void setAllowFlight(boolean b) {
        ((Player) getCallee()).setAllowFlight(b);
    }

    @Override
    public void hidePlayer(Player player) {
        ((Player) getCallee()).hidePlayer(player);
    }

    @Override
    public void showPlayer(Player player) {
        ((Player) getCallee()).showPlayer(player);
    }

    @Override
    public boolean canSee(Player player) {
        return ((Player) getCallee()).canSee(player);
    }

    @Override
    public Location getLocation() {
        return ((Player) getCallee()).getLocation();
    }

    @Override
    public Location getLocation(Location location) {
        return ((Player) getCallee()).getLocation(location);
    }

    @Override
    public Vector getVelocity() {
        return ((Player) getCallee()).getVelocity();
    }

    @Override
    public void setVelocity(Vector vector) {
        ((Player) getCallee()).setVelocity(vector);
    }

    @Override
    @Deprecated
    public boolean isOnGround() {
        return ((Player) getCallee()).isOnGround();
    }

    @Override
    public World getWorld() {
        return ((Player) getCallee()).getWorld();
    }

    @Override
    public boolean teleport(Location location) {
        return ((Player) getCallee()).teleport(location);
    }

    @Override
    public boolean teleport(Location location, PlayerTeleportEvent.TeleportCause teleportCause) {
        return ((Player) getCallee()).teleport(location, teleportCause);
    }

    @Override
    public boolean teleport(Entity entity) {
        return ((Player) getCallee()).teleport(entity);
    }

    @Override
    public boolean teleport(Entity entity, PlayerTeleportEvent.TeleportCause teleportCause) {
        return ((Player) getCallee()).teleport(entity, teleportCause);
    }

    @Override
    public List<Entity> getNearbyEntities(double v, double v2, double v3) {
        return ((Player) getCallee()).getNearbyEntities(v, v2, v3);
    }

    @Override
    public int getEntityId() {
        return ((Player) getCallee()).getEntityId();
    }

    @Override
    public int getFireTicks() {
        return ((Player) getCallee()).getFireTicks();
    }

    @Override
    public void setFireTicks(int i) {
        ((Player) getCallee()).setFireTicks(i);
    }

    @Override
    public int getMaxFireTicks() {
        return ((Player) getCallee()).getMaxFireTicks();
    }

    @Override
    public void remove() {
        ((Player) getCallee()).remove();
    }

    @Override
    public boolean isDead() {
        return ((Player) getCallee()).isDead();
    }

    @Override
    public boolean isValid() {
        return ((Player) getCallee()).isValid();
    }

    @Override
    public Entity getPassenger() {
        return ((Player) getCallee()).getPassenger();
    }

    @Override
    public boolean setPassenger(Entity entity) {
        return ((Player) getCallee()).setPassenger(entity);
    }

    @Override
    public boolean isEmpty() {
        return ((Player) getCallee()).isEmpty();
    }

    @Override
    public boolean eject() {
        return ((Player) getCallee()).eject();
    }

    @Override
    public float getFallDistance() {
        return ((Player) getCallee()).getFallDistance();
    }

    @Override
    public void setFallDistance(float v) {
        ((Player) getCallee()).setFallDistance(v);
    }

    @Override
    public EntityDamageEvent getLastDamageCause() {
        return ((Player) getCallee()).getLastDamageCause();
    }

    @Override
    public void setLastDamageCause(EntityDamageEvent entityDamageEvent) {
        ((Player) getCallee()).setLastDamageCause(entityDamageEvent);
    }

    @Override
    public UUID getUniqueId() {
        return ((Player) getCallee()).getUniqueId();
    }

    @Override
    public boolean isBanned() {
        return ((Player) getCallee()).isBanned();
    }

    @Override
    @Deprecated
    public void setBanned(boolean b) {
        ((Player) getCallee()).setBanned(b);
    }

    @Override
    public boolean isWhitelisted() {
        return ((Player) getCallee()).isWhitelisted();
    }

    @Override
    public void setWhitelisted(boolean b) {
        ((Player) getCallee()).setWhitelisted(b);
    }

    @Override
    public Player getPlayer() {
        return ((Player) getCallee());
    }

    @Override
    public long getFirstPlayed() {
        return ((Player) getCallee()).getFirstPlayed();
    }

    @Override
    public long getLastPlayed() {
        return ((Player) getCallee()).getLastPlayed();
    }

    @Override
    public boolean hasPlayedBefore() {
        return ((Player) getCallee()).hasPlayedBefore();
    }

    @Override
    public int getTicksLived() {
        return ((Player) getCallee()).getTicksLived();
    }

    @Override
    public void setTicksLived(int i) {
        ((Player) getCallee()).setTicksLived(i);
    }

    @Override
    public void playEffect(EntityEffect entityEffect) {
        ((Player) getCallee()).playEffect(entityEffect);
    }

    @Override
    public EntityType getType() {
        return ((Player) getCallee()).getType();
    }

    @Override
    public boolean isInsideVehicle() {
        return ((Player) getCallee()).isInsideVehicle();
    }

    @Override
    public boolean leaveVehicle() {
        return ((Player) getCallee()).leaveVehicle();
    }

    @Override
    public Entity getVehicle() {
        return ((Player) getCallee()).getVehicle();
    }

    @Override
    public boolean isFlying() {
        return ((Player) getCallee()).isFlying();
    }

    @Override
    public void setFlying(boolean b) {
        ((Player) getCallee()).setFlying(b);
    }

    @Override
    public float getFlySpeed() {
        return ((Player) getCallee()).getFlySpeed();
    }

    @Override
    public void setFlySpeed(float v) throws IllegalArgumentException {
        ((Player) getCallee()).setFlySpeed(v);
    }

    @Override
    public float getWalkSpeed() {
        return ((Player) getCallee()).getWalkSpeed();
    }

    @Override
    public void setWalkSpeed(float v) throws IllegalArgumentException {
        ((Player) getCallee()).setWalkSpeed(v);
    }

    @Override
    @Deprecated
    public void setTexturePack(String s) {
        ((Player) getCallee()).setTexturePack(s);
    }

    @Override
    public void setResourcePack(String s) {
        ((Player) getCallee()).setResourcePack(s);
    }

    @Override
    public CraftScoreboard getScoreboard() {
        return (CraftScoreboard) ((Player) getCallee()).getScoreboard();
    }

    @Override
    public void setScoreboard(Scoreboard scoreboard) throws IllegalArgumentException, IllegalStateException {
        ((Player) getCallee()).setScoreboard(scoreboard);
    }

    @Override
    public boolean isHealthScaled() {
        return ((Player) getCallee()).isHealthScaled();
    }

    @Override
    public void setHealthScaled(boolean b) {
        ((Player) getCallee()).setHealthScaled(b);
    }

    @Override
    public double getHealthScale() {
        return ((Player) getCallee()).getHealthScale();
    }

    @Override
    public void setHealthScale(double v) throws IllegalArgumentException {
        ((Player) getCallee()).setHealthScale(v);
    }

    @Override
    public Spigot spigot() {
        return ((Player) getCallee()).spigot();
    }

    @Override
    public Map<String, Object> serialize() {
        return ((Player) getCallee()).serialize();
    }

    @Override
    public boolean isOnline() {
        return ((Player) getCallee()).isOnline();
    }

    @Override
    public PlayerInventory getInventory() {
        return ((Player) getCallee()).getInventory();
    }

    @Override
    public Inventory getEnderChest() {
        return ((Player) getCallee()).getEnderChest();
    }

    @Override
    public boolean setWindowProperty(InventoryView.Property property, int i) {
        return ((Player) getCallee()).setWindowProperty(property, i);
    }

    @Override
    public InventoryView getOpenInventory() {
        return ((Player) getCallee()).getOpenInventory();
    }

    @Override
    public InventoryView openInventory(Inventory itemStacks) {
        return ((Player) getCallee()).openInventory(itemStacks);
    }

    @Override
    public InventoryView openWorkbench(Location location, boolean b) {
        return ((Player) getCallee()).openWorkbench(location, b);
    }

    @Override
    public InventoryView openEnchanting(Location location, boolean b) {
        return ((Player) getCallee()).openEnchanting(location, b);
    }

    @Override
    public void openInventory(InventoryView inventoryView) {
        ((Player) getCallee()).openInventory(inventoryView);
    }

    @Override
    public void closeInventory() {
        ((Player) getCallee()).closeInventory();
    }

    @Override
    public ItemStack getItemInHand() {
        return ((Player) getCallee()).getItemInHand();
    }

    @Override
    public void setItemInHand(ItemStack itemStack) {
        ((Player) getCallee()).setItemInHand(itemStack);
    }

    @Override
    public ItemStack getItemOnCursor() {
        return ((Player) getCallee()).getItemOnCursor();
    }

    @Override
    public void setItemOnCursor(ItemStack itemStack) {
        ((Player) getCallee()).setItemOnCursor(itemStack);
    }

    @Override
    public boolean isSleeping() {
        return ((Player) getCallee()).isSleeping();
    }

    @Override
    public int getSleepTicks() {
        return ((Player) getCallee()).getSleepTicks();
    }

    @Override
    public GameMode getGameMode() {
        return ((Player) getCallee()).getGameMode();
    }

    @Override
    public void setGameMode(GameMode gameMode) {
        ((Player) getCallee()).setGameMode(gameMode);
    }

    @Override
    public boolean isBlocking() {
        return ((Player) getCallee()).isBlocking();
    }

    @Override
    public int getExpToLevel() {
        return ((Player) getCallee()).getExpToLevel();
    }

    @Override
    public double getEyeHeight() {
        return ((Player) getCallee()).getEyeHeight();
    }

    @Override
    public double getEyeHeight(boolean b) {
        return ((Player) getCallee()).getEyeHeight(b);
    }

    @Override
    public Location getEyeLocation() {
        return ((Player) getCallee()).getEyeLocation();
    }

    @Override
    @Deprecated
    public List<Block> getLineOfSight(HashSet<Byte> bytes, int i) {
        return ((Player) getCallee()).getLineOfSight(bytes, i);
    }

    @Override
    public List<Block> getLineOfSight(Set<Material> set, int i) {
        return ((Player) getCallee()).getLineOfSight(set, i);
    }

    @Override
    @Deprecated
    public Block getTargetBlock(HashSet<Byte> bytes, int i) {
        return ((Player) getCallee()).getTargetBlock(bytes, i);
    }

    @Override
    public Block getTargetBlock(Set<Material> set, int i) {
        return ((Player) getCallee()).getTargetBlock(set, i);
    }

    @Override
    @Deprecated
    public List<Block> getLastTwoTargetBlocks(HashSet<Byte> bytes, int i) {
        return ((Player) getCallee()).getLastTwoTargetBlocks(bytes, i);
    }

    @Override
    public List<Block> getLastTwoTargetBlocks(Set<Material> set, int i) {
        return ((Player) getCallee()).getLastTwoTargetBlocks(set, i);
    }

    @Override
    @Deprecated
    public Egg throwEgg() {
        return ((Player) getCallee()).throwEgg();
    }

    @Override
    @Deprecated
    public Snowball throwSnowball() {
        return ((Player) getCallee()).throwSnowball();
    }

    @Override
    @Deprecated
    public Arrow shootArrow() {
        return ((Player) getCallee()).shootArrow();
    }

    @Override
    public int getRemainingAir() {
        return ((Player) getCallee()).getRemainingAir();
    }

    @Override
    public void setRemainingAir(int i) {
        ((Player) getCallee()).setRemainingAir(i);
    }

    @Override
    public int getMaximumAir() {
        return ((Player) getCallee()).getMaximumAir();
    }

    @Override
    public void setMaximumAir(int i) {
        ((Player) getCallee()).setMaximumAir(i);
    }

    @Override
    public int getMaximumNoDamageTicks() {
        return ((Player) getCallee()).getMaximumNoDamageTicks();
    }

    @Override
    public void setMaximumNoDamageTicks(int i) {
        ((Player) getCallee()).setMaximumNoDamageTicks(i);
    }

    @Override
    public double getLastDamage() {
        return ((Player) getCallee()).getLastDamage();
    }

    @Override
    public void setLastDamage(double v) {
        ((Player) getCallee()).setLastDamage(v);
    }

    @Override
    public int getNoDamageTicks() {
        return ((Player) getCallee()).getNoDamageTicks();
    }

    @Override
    public void setNoDamageTicks(int i) {
        ((Player) getCallee()).setNoDamageTicks(i);
    }

    @Override
    public Player getKiller() {
        return ((Player) getCallee()).getKiller();
    }

    @Override
    public boolean addPotionEffect(PotionEffect potionEffect) {
        return ((Player) getCallee()).addPotionEffect(potionEffect);
    }

    @Override
    public boolean addPotionEffect(PotionEffect potionEffect, boolean b) {
        return ((Player) getCallee()).addPotionEffect(potionEffect, b);
    }

    @Override
    public boolean addPotionEffects(Collection<PotionEffect> potionEffects) {
        return ((Player) getCallee()).addPotionEffects(potionEffects);
    }

    @Override
    public boolean hasPotionEffect(PotionEffectType potionEffectType) {
        return ((Player) getCallee()).hasPotionEffect(potionEffectType);
    }

    @Override
    public void removePotionEffect(PotionEffectType potionEffectType) {
        ((Player) getCallee()).removePotionEffect(potionEffectType);
    }

    @Override
    public Collection<PotionEffect> getActivePotionEffects() {
        return ((Player) getCallee()).getActivePotionEffects();
    }

    @Override
    public boolean hasLineOfSight(Entity entity) {
        return ((Player) getCallee()).hasLineOfSight(entity);
    }

    @Override
    public boolean getRemoveWhenFarAway() {
        return ((Player) getCallee()).getRemoveWhenFarAway();
    }

    @Override
    public void setRemoveWhenFarAway(boolean b) {
        ((Player) getCallee()).setRemoveWhenFarAway(b);
    }

    @Override
    public EntityEquipment getEquipment() {
        return ((Player) getCallee()).getEquipment();
    }

    @Override
    public boolean getCanPickupItems() {
        return ((Player) getCallee()).getCanPickupItems();
    }

    @Override
    public void setCanPickupItems(boolean b) {
        ((Player) getCallee()).setCanPickupItems(b);
    }

    @Override
    public String getCustomName() {
        return ((Player) getCallee()).getCustomName();
    }

    @Override
    public void setCustomName(String s) {
        ((Player) getCallee()).setCustomName(s);
    }

    @Override
    public boolean isCustomNameVisible() {
        return ((Player) getCallee()).isCustomNameVisible();
    }

    @Override
    public void setCustomNameVisible(boolean b) {
        ((Player) getCallee()).setCustomNameVisible(b);
    }

    @Override
    public boolean isLeashed() {
        return ((Player) getCallee()).isLeashed();
    }

    @Override
    public Entity getLeashHolder() throws IllegalStateException {
        return ((Player) getCallee()).getLeashHolder();
    }

    @Override
    public boolean setLeashHolder(Entity entity) {
        return ((Player) getCallee()).setLeashHolder(entity);
    }

    @Override
    public void damage(double v) {
        ((Player) getCallee()).damage(v);
    }

    @Override
    public void damage(double v, Entity entity) {
        ((Player) getCallee()).damage(v, entity);
    }

    @Override
    public double getHealth() {
        return ((Player) getCallee()).getHealth();
    }

    @Override
    public void setHealth(double v) {
        ((Player) getCallee()).setHealth(v);
    }

    @Override
    public double getMaxHealth() {
        return ((Player) getCallee()).getMaxHealth();
    }

    @Override
    public void setMaxHealth(double v) {
        ((Player) getCallee()).setMaxHealth(v);
    }

    @Override
    public void resetMaxHealth() {
        ((Player) getCallee()).resetMaxHealth();
    }

    @Override
    public void setMetadata(String s, MetadataValue metadataValue) {
        ((Player) getCallee()).setMetadata(s, metadataValue);
    }

    @Override
    public List<MetadataValue> getMetadata(String s) {
        return ((Player) getCallee()).getMetadata(s);
    }

    @Override
    public boolean hasMetadata(String s) {
        return ((Player) getCallee()).hasMetadata(s);
    }

    @Override
    public void removeMetadata(String s, Plugin plugin) {
        ((Player) getCallee()).removeMetadata(s, plugin);
    }

    @Override
    public void sendPluginMessage(Plugin plugin, String s, byte[] bytes) {
        ((Player) getCallee()).sendPluginMessage(plugin, s, bytes);
        if (getCaller() instanceof PluginMessageRecipient) {
            ((PluginMessageRecipient) getCaller()).sendPluginMessage(plugin, s, bytes);
        }
    }

    @Override
    public Set<String> getListeningPluginChannels() {
        return ((Player) getCallee()).getListeningPluginChannels();
    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> aClass) {
        return ((Player) getCallee()).launchProjectile(aClass);
    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> aClass, Vector vector) {
        return ((Player) getCallee()).launchProjectile(aClass, vector);
    }
}
