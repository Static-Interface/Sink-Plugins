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
import org.bukkit.Bukkit;
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
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
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

public class ProxiedPlayer extends CraftPlayer implements ProxiedObject<Player, CommandSender> {

    private Player base;
    private CommandSender faker;

    public ProxiedPlayer(Player base, CommandSender faker) {
        super((CraftServer) Bukkit.getServer(), ((CraftPlayer) base).getHandle());
        this.base = base;
        this.faker = faker;
    }

    @Override
    public Player getBase() {
        return base;
    }

    @Override
    public CommandSender getProxy() {
        return faker;
    }

    @Override
    public String getDisplayName() {
        return getBase().getDisplayName();
    }

    @Override
    public void setDisplayName(String s) {
        getBase().setDisplayName(s);
    }

    @Override
    public String getPlayerListName() {
        return getBase().getPlayerListName();
    }

    @Override
    public void setPlayerListName(String s) {
        getBase().setPlayerListName(s);
    }

    @Override
    public Location getCompassTarget() {
        return getBase().getCompassTarget();
    }

    @Override
    public void setCompassTarget(Location location) {
        getBase().setCompassTarget(location);
    }

    @Override
    public InetSocketAddress getAddress() {
        return getBase().getAddress();
    }

    @Override
    public void kickPlayer(String s) {
        getBase().kickPlayer(s);
    }

    @Override
    public void chat(String s) {
        getBase().chat(s);
    }

    @Override
    public boolean performCommand(String s) {
        return getBase().performCommand(s);
    }

    @Override
    public boolean isSneaking() {
        return getBase().isSneaking();
    }

    @Override
    public void setSneaking(boolean b) {
        getBase().setSneaking(b);
    }

    @Override
    public boolean isSprinting() {
        return getBase().isSprinting();
    }

    @Override
    public void setSprinting(boolean b) {
        getBase().setSprinting(b);
    }

    @Override
    public void saveData() {
        getBase().saveData();
    }

    @Override
    public void loadData() {
        getBase().loadData();
    }

    @Override
    public boolean isSleepingIgnored() {
        return getBase().isSleepingIgnored();
    }

    @Override
    public void setSleepingIgnored(boolean b) {
        getBase().setSleepingIgnored(b);
    }

    @Override
    @Deprecated
    public void playNote(Location location, byte b, byte b2) {
        getBase().playNote(location, b, b2);
    }

    @Override
    public void playNote(Location location, Instrument instrument, Note note) {
        getBase().playNote(location, instrument, note);
    }

    @Override
    public void playSound(Location location, Sound sound, float v, float v2) {
        getBase().playSound(location, sound, v, v2);
    }

    @Override
    @Deprecated
    public void playSound(Location location, String s, float v, float v2) {
        getBase().playSound(location, s, v, v2);
    }

    @Override
    @Deprecated
    public void playEffect(Location location, Effect effect, int i) {
        getBase().playEffect(location, effect, i);
    }

    @Override
    public <T> void playEffect(Location location, Effect effect, T t) {
        getBase().playEffect(location, effect, t);
    }

    @Override
    @Deprecated
    public void sendBlockChange(Location location, Material material, byte b) {
        getBase().sendBlockChange(location, material, b);
    }

    @Override
    @Deprecated
    public boolean sendChunkChange(Location location, int i, int i2, int i3, byte[] bytes) {
        return getBase().sendChunkChange(location, i, i2, i3, bytes);
    }

    @Override
    @Deprecated
    public void sendBlockChange(Location location, int i, byte b) {
        getBase().sendBlockChange(location, i, b);
    }

    @Override
    public void sendSignChange(Location location, String[] strings) throws IllegalArgumentException {
        getBase().sendSignChange(location, strings);
    }

    @Override
    public void sendMap(MapView mapView) {
        getBase().sendMap(mapView);
    }

    @Override
    @Deprecated
    public void updateInventory() {
        getBase().updateInventory();
    }

    @Override
    public void awardAchievement(Achievement achievement) {
        getBase().awardAchievement(achievement);
    }

    @Override
    public void removeAchievement(Achievement achievement) {
        getBase().removeAchievement(achievement);
    }

    @Override
    public boolean hasAchievement(Achievement achievement) {
        return getBase().hasAchievement(achievement);
    }

    @Override
    public void incrementStatistic(Statistic statistic) throws IllegalArgumentException {
        getBase().incrementStatistic(statistic);
    }

    @Override
    public void decrementStatistic(Statistic statistic) throws IllegalArgumentException {
        getBase().decrementStatistic(statistic);
    }

    @Override
    public void incrementStatistic(Statistic statistic, int i) throws IllegalArgumentException {
        getBase().incrementStatistic(statistic, i);
    }

    @Override
    public void decrementStatistic(Statistic statistic, int i) throws IllegalArgumentException {
        getBase().decrementStatistic(statistic, i);
    }

    @Override
    public void setStatistic(Statistic statistic, int i) throws IllegalArgumentException {
        getBase().setStatistic(statistic, i);
    }

    @Override
    public int getStatistic(Statistic statistic) throws IllegalArgumentException {
        return getBase().getStatistic(statistic);
    }

    @Override
    public void incrementStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
        getBase().incrementStatistic(statistic, material);
    }

    @Override
    public void decrementStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
        getBase().decrementStatistic(statistic, material);
    }

    @Override
    public int getStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
        return getBase().getStatistic(statistic, material);
    }

    @Override
    public void incrementStatistic(Statistic statistic, Material material, int i) throws IllegalArgumentException {
        getBase().incrementStatistic(statistic, material, i);
    }

    @Override
    public void decrementStatistic(Statistic statistic, Material material, int i) throws IllegalArgumentException {
        getBase().decrementStatistic(statistic, material, i);
    }

    @Override
    public void setStatistic(Statistic statistic, Material material, int i) throws IllegalArgumentException {
        getBase().setStatistic(statistic, material, i);
    }

    @Override
    public void incrementStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
        getBase().incrementStatistic(statistic, entityType);
    }

    @Override
    public void decrementStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
        getBase().decrementStatistic(statistic, entityType);
    }

    @Override
    public int getStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
        return getBase().getStatistic(statistic, entityType);
    }

    @Override
    public void incrementStatistic(Statistic statistic, EntityType entityType, int i) throws IllegalArgumentException {
        getBase().incrementStatistic(statistic, entityType, i);
    }

    @Override
    public void decrementStatistic(Statistic statistic, EntityType entityType, int i) {
        getBase().decrementStatistic(statistic, entityType, i);
    }

    @Override
    public void setStatistic(Statistic statistic, EntityType entityType, int i) {
        getBase().setStatistic(statistic, entityType, i);
    }

    @Override
    public void setPlayerTime(long l, boolean b) {
        getBase().setPlayerTime(l, b);
    }

    @Override
    public long getPlayerTime() {
        return getBase().getPlayerTime();
    }

    @Override
    public long getPlayerTimeOffset() {
        return getBase().getPlayerTimeOffset();
    }

    @Override
    public boolean isPlayerTimeRelative() {
        return getBase().isPlayerTimeRelative();
    }

    @Override
    public void resetPlayerTime() {
        getBase().resetPlayerTime();
    }

    @Override
    public WeatherType getPlayerWeather() {
        return getBase().getPlayerWeather();
    }

    @Override
    public void setPlayerWeather(WeatherType weatherType) {
        getBase().setPlayerWeather(weatherType);
    }

    @Override
    public void resetPlayerWeather() {
        getBase().resetPlayerWeather();
    }

    @Override
    public void giveExp(int i) {
        getBase().giveExp(i);
    }

    @Override
    public void giveExpLevels(int i) {
        getBase().giveExpLevels(i);
    }

    @Override
    public float getExp() {
        return getBase().getExp();
    }

    @Override
    public void setExp(float v) {
        getBase().setExp(v);
    }

    @Override
    public int getLevel() {
        return getBase().getLevel();
    }

    @Override
    public void setLevel(int i) {
        getBase().setLevel(i);
    }

    @Override
    public int getTotalExperience() {
        return getBase().getTotalExperience();
    }

    @Override
    public void setTotalExperience(int i) {
        getBase().setTotalExperience(i);
    }

    @Override
    public float getExhaustion() {
        return getBase().getExhaustion();
    }

    @Override
    public void setExhaustion(float v) {
        getBase().setExhaustion(v);
    }

    @Override
    public float getSaturation() {
        return getBase().getSaturation();
    }

    @Override
    public void setSaturation(float v) {
        getBase().setSaturation(v);
    }

    @Override
    public int getFoodLevel() {
        return getBase().getFoodLevel();
    }

    @Override
    public void setFoodLevel(int i) {
        getBase().setFoodLevel(i);
    }

    @Override
    public Location getBedSpawnLocation() {
        return getBase().getBedSpawnLocation();
    }

    @Override
    public void setBedSpawnLocation(Location location) {
        getBase().setBedSpawnLocation(location);
    }

    @Override
    public void setBedSpawnLocation(Location location, boolean b) {
        getBase().setBedSpawnLocation(location, b);
    }

    @Override
    public boolean getAllowFlight() {
        return getBase().getAllowFlight();
    }

    @Override
    public void setAllowFlight(boolean b) {
        getBase().setAllowFlight(b);
    }

    @Override
    public void hidePlayer(Player player) {
        getBase().hidePlayer(player);
    }

    @Override
    public void showPlayer(Player player) {
        getBase().showPlayer(player);
    }

    @Override
    public boolean canSee(Player player) {
        return getBase().canSee(player);
    }

    @Override
    public Location getLocation() {
        return getBase().getLocation();
    }

    @Override
    public Location getLocation(Location location) {
        return getBase().getLocation(location);
    }

    @Override
    public Vector getVelocity() {
        return getBase().getVelocity();
    }

    @Override
    public void setVelocity(Vector vector) {
        getBase().setVelocity(vector);
    }

    @Override
    @Deprecated
    public boolean isOnGround() {
        return getBase().isOnGround();
    }

    @Override
    public World getWorld() {
        return getBase().getWorld();
    }

    @Override
    public boolean teleport(Location location) {
        return getBase().teleport(location);
    }

    @Override
    public boolean teleport(Location location, PlayerTeleportEvent.TeleportCause teleportCause) {
        return getBase().teleport(location, teleportCause);
    }

    @Override
    public boolean teleport(Entity entity) {
        return getBase().teleport(entity);
    }

    @Override
    public boolean teleport(Entity entity, PlayerTeleportEvent.TeleportCause teleportCause) {
        return getBase().teleport(entity, teleportCause);
    }

    @Override
    public List<Entity> getNearbyEntities(double v, double v2, double v3) {
        return getBase().getNearbyEntities(v, v2, v3);
    }

    @Override
    public int getEntityId() {
        return getBase().getEntityId();
    }

    @Override
    public int getFireTicks() {
        return getBase().getFireTicks();
    }

    @Override
    public void setFireTicks(int i) {
        getBase().setFireTicks(i);
    }

    @Override
    public int getMaxFireTicks() {
        return getBase().getMaxFireTicks();
    }

    @Override
    public void remove() {
        getBase().remove();
    }

    @Override
    public boolean isDead() {
        return getBase().isDead();
    }

    @Override
    public boolean isValid() {
        return getBase().isValid();
    }

    @Override
    public Entity getPassenger() {
        return getBase().getPassenger();
    }

    @Override
    public boolean setPassenger(Entity entity) {
        return getBase().setPassenger(entity);
    }

    @Override
    public boolean isEmpty() {
        return getBase().isEmpty();
    }

    @Override
    public boolean eject() {
        return getBase().eject();
    }

    @Override
    public float getFallDistance() {
        return getBase().getFallDistance();
    }

    @Override
    public void setFallDistance(float v) {
        getBase().setFallDistance(v);
    }

    @Override
    public EntityDamageEvent getLastDamageCause() {
        return getBase().getLastDamageCause();
    }

    @Override
    public void setLastDamageCause(EntityDamageEvent entityDamageEvent) {
        getBase().setLastDamageCause(entityDamageEvent);
    }

    @Override
    public UUID getUniqueId() {
        return getBase().getUniqueId();
    }

    @Override
    public boolean isBanned() {
        return getBase().isBanned();
    }

    @Override
    @Deprecated
    public void setBanned(boolean b) {
        getBase().setBanned(b);
    }

    @Override
    public boolean isWhitelisted() {
        return getBase().isWhitelisted();
    }

    @Override
    public void setWhitelisted(boolean b) {
        getBase().setWhitelisted(b);
    }

    @Override
    public Player getPlayer() {
        return getBase();
    }

    @Override
    public long getFirstPlayed() {
        return getBase().getFirstPlayed();
    }

    @Override
    public long getLastPlayed() {
        return getBase().getLastPlayed();
    }

    @Override
    public boolean hasPlayedBefore() {
        return getBase().hasPlayedBefore();
    }

    @Override
    public int getTicksLived() {
        return getBase().getTicksLived();
    }

    @Override
    public void setTicksLived(int i) {
        getBase().setTicksLived(i);
    }

    @Override
    public void playEffect(EntityEffect entityEffect) {
        getBase().playEffect(entityEffect);
    }

    @Override
    public EntityType getType() {
        return getBase().getType();
    }

    @Override
    public boolean isInsideVehicle() {
        return getBase().isInsideVehicle();
    }

    @Override
    public boolean leaveVehicle() {
        return getBase().leaveVehicle();
    }

    @Override
    public Entity getVehicle() {
        return getBase().getVehicle();
    }

    @Override
    public boolean isFlying() {
        return getBase().isFlying();
    }

    @Override
    public void setFlying(boolean b) {
        getBase().setFlying(b);
    }

    @Override
    public float getFlySpeed() {
        return getBase().getFlySpeed();
    }

    @Override
    public void setFlySpeed(float v) throws IllegalArgumentException {
        getBase().setFlySpeed(v);
    }

    @Override
    public float getWalkSpeed() {
        return getBase().getWalkSpeed();
    }

    @Override
    public void setWalkSpeed(float v) throws IllegalArgumentException {
        getBase().setWalkSpeed(v);
    }

    @Override
    @Deprecated
    public void setTexturePack(String s) {
        getBase().setTexturePack(s);
    }

    @Override
    public void setResourcePack(String s) {
        getBase().setResourcePack(s);
    }

    @Override
    public CraftScoreboard getScoreboard() {
        return (CraftScoreboard) getBase().getScoreboard();
    }

    @Override
    public void setScoreboard(Scoreboard scoreboard) throws IllegalArgumentException, IllegalStateException {
        getBase().setScoreboard(scoreboard);
    }

    @Override
    public boolean isHealthScaled() {
        return getBase().isHealthScaled();
    }

    @Override
    public void setHealthScaled(boolean b) {
        getBase().setHealthScaled(b);
    }

    @Override
    public double getHealthScale() {
        return getBase().getHealthScale();
    }

    @Override
    public void setHealthScale(double v) throws IllegalArgumentException {
        getBase().setHealthScale(v);
    }

    @Override
    public Player.Spigot spigot() {
        return getBase().spigot();
    }

    @Override
    public Map<String, Object> serialize() {
        return getBase().serialize();
    }

    @Override
    public boolean isOnline() {
        return getBase().isOnline();
    }

    @Override
    public PlayerInventory getInventory() {
        return getBase().getInventory();
    }

    @Override
    public Inventory getEnderChest() {
        return getBase().getEnderChest();
    }

    @Override
    public boolean setWindowProperty(InventoryView.Property property, int i) {
        return getBase().setWindowProperty(property, i);
    }

    @Override
    public InventoryView getOpenInventory() {
        return getBase().getOpenInventory();
    }

    @Override
    public InventoryView openInventory(Inventory itemStacks) {
        return getBase().openInventory(itemStacks);
    }

    @Override
    public InventoryView openWorkbench(Location location, boolean b) {
        return getBase().openWorkbench(location, b);
    }

    @Override
    public InventoryView openEnchanting(Location location, boolean b) {
        return getBase().openEnchanting(location, b);
    }

    @Override
    public void openInventory(InventoryView inventoryView) {
        getBase().openInventory(inventoryView);
    }

    @Override
    public void closeInventory() {
        getBase().closeInventory();
    }

    @Override
    public ItemStack getItemInHand() {
        return getBase().getItemInHand();
    }

    @Override
    public void setItemInHand(ItemStack itemStack) {
        getBase().setItemInHand(itemStack);
    }

    @Override
    public ItemStack getItemOnCursor() {
        return getBase().getItemOnCursor();
    }

    @Override
    public void setItemOnCursor(ItemStack itemStack) {
        getBase().setItemOnCursor(itemStack);
    }

    @Override
    public boolean isSleeping() {
        return getBase().isSleeping();
    }

    @Override
    public int getSleepTicks() {
        return getBase().getSleepTicks();
    }

    @Override
    public GameMode getGameMode() {
        return getBase().getGameMode();
    }

    @Override
    public void setGameMode(GameMode gameMode) {
        getBase().setGameMode(gameMode);
    }

    @Override
    public boolean isBlocking() {
        return getBase().isBlocking();
    }

    @Override
    public int getExpToLevel() {
        return getBase().getExpToLevel();
    }

    @Override
    public double getEyeHeight() {
        return getBase().getEyeHeight();
    }

    @Override
    public double getEyeHeight(boolean b) {
        return getBase().getEyeHeight(b);
    }

    @Override
    public Location getEyeLocation() {
        return getBase().getEyeLocation();
    }

    @Override
    @Deprecated
    public List<Block> getLineOfSight(HashSet<Byte> bytes, int i) {
        return getBase().getLineOfSight(bytes, i);
    }

    @Override
    public List<Block> getLineOfSight(Set<Material> set, int i) {
        return getBase().getLineOfSight(set, i);
    }

    @Override
    @Deprecated
    public Block getTargetBlock(HashSet<Byte> bytes, int i) {
        return getBase().getTargetBlock(bytes, i);
    }

    @Override
    public Block getTargetBlock(Set<Material> set, int i) {
        return getBase().getTargetBlock(set, i);
    }

    @Override
    @Deprecated
    public List<Block> getLastTwoTargetBlocks(HashSet<Byte> bytes, int i) {
        return getBase().getLastTwoTargetBlocks(bytes, i);
    }

    @Override
    public List<Block> getLastTwoTargetBlocks(Set<Material> set, int i) {
        return getBase().getLastTwoTargetBlocks(set, i);
    }

    @Override
    @Deprecated
    public Egg throwEgg() {
        return getBase().throwEgg();
    }

    @Override
    @Deprecated
    public Snowball throwSnowball() {
        return getBase().throwSnowball();
    }

    @Override
    @Deprecated
    public Arrow shootArrow() {
        return getBase().shootArrow();
    }

    @Override
    public int getRemainingAir() {
        return getBase().getRemainingAir();
    }

    @Override
    public void setRemainingAir(int i) {
        getBase().setRemainingAir(i);
    }

    @Override
    public int getMaximumAir() {
        return getBase().getMaximumAir();
    }

    @Override
    public void setMaximumAir(int i) {
        getBase().setMaximumAir(i);
    }

    @Override
    public int getMaximumNoDamageTicks() {
        return getBase().getMaximumNoDamageTicks();
    }

    @Override
    public void setMaximumNoDamageTicks(int i) {
        getBase().setMaximumNoDamageTicks(i);
    }

    @Override
    public double getLastDamage() {
        return getBase().getLastDamage();
    }

    @Override
    public void setLastDamage(double v) {
        getBase().setLastDamage(v);
    }

    @Override
    public int getNoDamageTicks() {
        return getBase().getNoDamageTicks();
    }

    @Override
    public void setNoDamageTicks(int i) {
        getBase().setNoDamageTicks(i);
    }

    @Override
    public Player getKiller() {
        return getBase().getKiller();
    }

    @Override
    public boolean addPotionEffect(PotionEffect potionEffect) {
        return getBase().addPotionEffect(potionEffect);
    }

    @Override
    public boolean addPotionEffect(PotionEffect potionEffect, boolean b) {
        return getBase().addPotionEffect(potionEffect, b);
    }

    @Override
    public boolean addPotionEffects(Collection<PotionEffect> potionEffects) {
        return getBase().addPotionEffects(potionEffects);
    }

    @Override
    public boolean hasPotionEffect(PotionEffectType potionEffectType) {
        return getBase().hasPotionEffect(potionEffectType);
    }

    @Override
    public void removePotionEffect(PotionEffectType potionEffectType) {
        getBase().removePotionEffect(potionEffectType);
    }

    @Override
    public Collection<PotionEffect> getActivePotionEffects() {
        return getBase().getActivePotionEffects();
    }

    @Override
    public boolean hasLineOfSight(Entity entity) {
        return getBase().hasLineOfSight(entity);
    }

    @Override
    public boolean getRemoveWhenFarAway() {
        return getBase().getRemoveWhenFarAway();
    }

    @Override
    public void setRemoveWhenFarAway(boolean b) {
        getBase().setRemoveWhenFarAway(b);
    }

    @Override
    public EntityEquipment getEquipment() {
        return getBase().getEquipment();
    }

    @Override
    public boolean getCanPickupItems() {
        return getBase().getCanPickupItems();
    }

    @Override
    public void setCanPickupItems(boolean b) {
        getBase().setCanPickupItems(b);
    }

    @Override
    public String getCustomName() {
        return getBase().getCustomName();
    }

    @Override
    public void setCustomName(String s) {
        getBase().setCustomName(s);
    }

    @Override
    public boolean isCustomNameVisible() {
        return getBase().isCustomNameVisible();
    }

    @Override
    public void setCustomNameVisible(boolean b) {
        getBase().setCustomNameVisible(b);
    }

    @Override
    public boolean isLeashed() {
        return getBase().isLeashed();
    }

    @Override
    public Entity getLeashHolder() throws IllegalStateException {
        return getBase().getLeashHolder();
    }

    @Override
    public boolean setLeashHolder(Entity entity) {
        return getBase().setLeashHolder(entity);
    }

    @Override
    public void damage(double v) {
        getBase().damage(v);
    }

    @Override
    public void damage(double v, Entity entity) {
        getBase().damage(v, entity);
    }

    @Override
    public double getHealth() {
        return getBase().getHealth();
    }

    @Override
    public void setHealth(double v) {
        getBase().setHealth(v);
    }

    @Override
    public double getMaxHealth() {
        return getBase().getMaxHealth();
    }

    @Override
    public void setMaxHealth(double v) {
        getBase().setMaxHealth(v);
    }

    @Override
    public void resetMaxHealth() {
        getBase().resetMaxHealth();
    }

    @Override
    public void setMetadata(String s, MetadataValue metadataValue) {
        getBase().setMetadata(s, metadataValue);
    }

    @Override
    public List<MetadataValue> getMetadata(String s) {
        return getBase().getMetadata(s);
    }

    @Override
    public boolean hasMetadata(String s) {
        return getBase().hasMetadata(s);
    }

    @Override
    public void removeMetadata(String s, Plugin plugin) {
        getBase().removeMetadata(s, plugin);
    }

    @Override
    public void sendPluginMessage(Plugin plugin, String s, byte[] bytes) {
        getBase().sendPluginMessage(plugin, s, bytes);
        if (getProxy() instanceof PluginMessageRecipient) {
            ((PluginMessageRecipient) getProxy()).sendPluginMessage(plugin, s, bytes);
        }
    }

    @Override
    public Set<String> getListeningPluginChannels() {
        return getBase().getListeningPluginChannels();
    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> aClass) {
        return getBase().launchProjectile(aClass);
    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> aClass, Vector vector) {
        return getBase().launchProjectile(aClass, vector);
    }
}
