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
    private boolean silent;

    public ProxiedPlayer(Player base, CommandSender faker) {
        this(base, faker, false);
    }

    public ProxiedPlayer(Player base, CommandSender faker, boolean silent) {
        super((CraftServer) Bukkit.getServer(), ((CraftPlayer) base).getHandle());
        this.base = base;
        this.faker = faker;
        this.silent = silent;
    }

    @Override
    public void sendMessage(String message) {
        if (!silent) {
            base.sendMessage(message);
        }
        faker.sendMessage(message);
    }

    @Override
    public void sendMessage(String[] messages) {
        if (!silent) {
            base.sendMessage(messages);
        }
        faker.sendMessage(messages);
    }

    @Override
    public Player getBaseObject() {
        return base;
    }

    @Override
    public CommandSender getProxy() {
        return faker;
    }

    @Override
    public String getDisplayName() {
        return getBaseObject().getDisplayName();
    }

    @Override
    public void setDisplayName(String s) {
        getBaseObject().setDisplayName(s);
    }

    @Override
    public String getPlayerListName() {
        return getBaseObject().getPlayerListName();
    }

    @Override
    public void setPlayerListName(String s) {
        getBaseObject().setPlayerListName(s);
    }

    @Override
    public Location getCompassTarget() {
        return getBaseObject().getCompassTarget();
    }

    @Override
    public void setCompassTarget(Location location) {
        getBaseObject().setCompassTarget(location);
    }

    @Override
    public InetSocketAddress getAddress() {
        return getBaseObject().getAddress();
    }

    @Override
    public void kickPlayer(String s) {
        getBaseObject().kickPlayer(s);
    }

    @Override
    public void chat(String s) {
        getBaseObject().chat(s);
    }

    @Override
    public boolean performCommand(String s) {
        return getBaseObject().performCommand(s);
    }

    @Override
    public boolean isSneaking() {
        return getBaseObject().isSneaking();
    }

    @Override
    public void setSneaking(boolean b) {
        getBaseObject().setSneaking(b);
    }

    @Override
    public boolean isSprinting() {
        return getBaseObject().isSprinting();
    }

    @Override
    public void setSprinting(boolean b) {
        getBaseObject().setSprinting(b);
    }

    @Override
    public void saveData() {
        getBaseObject().saveData();
    }

    @Override
    public void loadData() {
        getBaseObject().loadData();
    }

    @Override
    public boolean isSleepingIgnored() {
        return getBaseObject().isSleepingIgnored();
    }

    @Override
    public void setSleepingIgnored(boolean b) {
        getBaseObject().setSleepingIgnored(b);
    }

    @Override
    @Deprecated
    public void playNote(Location location, byte b, byte b2) {
        getBaseObject().playNote(location, b, b2);
    }

    @Override
    public void playNote(Location location, Instrument instrument, Note note) {
        getBaseObject().playNote(location, instrument, note);
    }

    @Override
    public void playSound(Location location, Sound sound, float v, float v2) {
        getBaseObject().playSound(location, sound, v, v2);
    }

    @Override
    @Deprecated
    public void playSound(Location location, String s, float v, float v2) {
        getBaseObject().playSound(location, s, v, v2);
    }

    @Override
    @Deprecated
    public void playEffect(Location location, Effect effect, int i) {
        getBaseObject().playEffect(location, effect, i);
    }

    @Override
    public <T> void playEffect(Location location, Effect effect, T t) {
        getBaseObject().playEffect(location, effect, t);
    }

    @Override
    @Deprecated
    public void sendBlockChange(Location location, Material material, byte b) {
        getBaseObject().sendBlockChange(location, material, b);
    }

    @Override
    @Deprecated
    public boolean sendChunkChange(Location location, int i, int i2, int i3, byte[] bytes) {
        return getBaseObject().sendChunkChange(location, i, i2, i3, bytes);
    }

    @Override
    @Deprecated
    public void sendBlockChange(Location location, int i, byte b) {
        getBaseObject().sendBlockChange(location, i, b);
    }

    @Override
    public void sendSignChange(Location location, String[] strings) throws IllegalArgumentException {
        getBaseObject().sendSignChange(location, strings);
    }

    @Override
    public void sendMap(MapView mapView) {
        getBaseObject().sendMap(mapView);
    }

    @Override
    @Deprecated
    public void updateInventory() {
        getBaseObject().updateInventory();
    }

    @Override
    public void awardAchievement(Achievement achievement) {
        getBaseObject().awardAchievement(achievement);
    }

    @Override
    public void removeAchievement(Achievement achievement) {
        getBaseObject().removeAchievement(achievement);
    }

    @Override
    public boolean hasAchievement(Achievement achievement) {
        return getBaseObject().hasAchievement(achievement);
    }

    @Override
    public void incrementStatistic(Statistic statistic) throws IllegalArgumentException {
        getBaseObject().incrementStatistic(statistic);
    }

    @Override
    public void decrementStatistic(Statistic statistic) throws IllegalArgumentException {
        getBaseObject().decrementStatistic(statistic);
    }

    @Override
    public void incrementStatistic(Statistic statistic, int i) throws IllegalArgumentException {
        getBaseObject().incrementStatistic(statistic, i);
    }

    @Override
    public void decrementStatistic(Statistic statistic, int i) throws IllegalArgumentException {
        getBaseObject().decrementStatistic(statistic, i);
    }

    @Override
    public void setStatistic(Statistic statistic, int i) throws IllegalArgumentException {
        getBaseObject().setStatistic(statistic, i);
    }

    @Override
    public int getStatistic(Statistic statistic) throws IllegalArgumentException {
        return getBaseObject().getStatistic(statistic);
    }

    @Override
    public void incrementStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
        getBaseObject().incrementStatistic(statistic, material);
    }

    @Override
    public void decrementStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
        getBaseObject().decrementStatistic(statistic, material);
    }

    @Override
    public int getStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
        return getBaseObject().getStatistic(statistic, material);
    }

    @Override
    public void incrementStatistic(Statistic statistic, Material material, int i) throws IllegalArgumentException {
        getBaseObject().incrementStatistic(statistic, material, i);
    }

    @Override
    public void decrementStatistic(Statistic statistic, Material material, int i) throws IllegalArgumentException {
        getBaseObject().decrementStatistic(statistic, material, i);
    }

    @Override
    public void setStatistic(Statistic statistic, Material material, int i) throws IllegalArgumentException {
        getBaseObject().setStatistic(statistic, material, i);
    }

    @Override
    public void incrementStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
        getBaseObject().incrementStatistic(statistic, entityType);
    }

    @Override
    public void decrementStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
        getBaseObject().decrementStatistic(statistic, entityType);
    }

    @Override
    public int getStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
        return getBaseObject().getStatistic(statistic, entityType);
    }

    @Override
    public void incrementStatistic(Statistic statistic, EntityType entityType, int i) throws IllegalArgumentException {
        getBaseObject().incrementStatistic(statistic, entityType, i);
    }

    @Override
    public void decrementStatistic(Statistic statistic, EntityType entityType, int i) {
        getBaseObject().decrementStatistic(statistic, entityType, i);
    }

    @Override
    public void setStatistic(Statistic statistic, EntityType entityType, int i) {
        getBaseObject().setStatistic(statistic, entityType, i);
    }

    @Override
    public void setPlayerTime(long l, boolean b) {
        getBaseObject().setPlayerTime(l, b);
    }

    @Override
    public long getPlayerTime() {
        return getBaseObject().getPlayerTime();
    }

    @Override
    public long getPlayerTimeOffset() {
        return getBaseObject().getPlayerTimeOffset();
    }

    @Override
    public boolean isPlayerTimeRelative() {
        return getBaseObject().isPlayerTimeRelative();
    }

    @Override
    public void resetPlayerTime() {
        getBaseObject().resetPlayerTime();
    }

    @Override
    public WeatherType getPlayerWeather() {
        return getBaseObject().getPlayerWeather();
    }

    @Override
    public void setPlayerWeather(WeatherType weatherType) {
        getBaseObject().setPlayerWeather(weatherType);
    }

    @Override
    public void resetPlayerWeather() {
        getBaseObject().resetPlayerWeather();
    }

    @Override
    public void giveExp(int i) {
        getBaseObject().giveExp(i);
    }

    @Override
    public void giveExpLevels(int i) {
        getBaseObject().giveExpLevels(i);
    }

    @Override
    public float getExp() {
        return getBaseObject().getExp();
    }

    @Override
    public void setExp(float v) {
        getBaseObject().setExp(v);
    }

    @Override
    public int getLevel() {
        return getBaseObject().getLevel();
    }

    @Override
    public void setLevel(int i) {
        getBaseObject().setLevel(i);
    }

    @Override
    public int getTotalExperience() {
        return getBaseObject().getTotalExperience();
    }

    @Override
    public void setTotalExperience(int i) {
        getBaseObject().setTotalExperience(i);
    }

    @Override
    public float getExhaustion() {
        return getBaseObject().getExhaustion();
    }

    @Override
    public void setExhaustion(float v) {
        getBaseObject().setExhaustion(v);
    }

    @Override
    public float getSaturation() {
        return getBaseObject().getSaturation();
    }

    @Override
    public void setSaturation(float v) {
        getBaseObject().setSaturation(v);
    }

    @Override
    public int getFoodLevel() {
        return getBaseObject().getFoodLevel();
    }

    @Override
    public void setFoodLevel(int i) {
        getBaseObject().setFoodLevel(i);
    }

    @Override
    public Location getBedSpawnLocation() {
        return getBaseObject().getBedSpawnLocation();
    }

    @Override
    public void setBedSpawnLocation(Location location) {
        getBaseObject().setBedSpawnLocation(location);
    }

    @Override
    public void setBedSpawnLocation(Location location, boolean b) {
        getBaseObject().setBedSpawnLocation(location, b);
    }

    @Override
    public boolean getAllowFlight() {
        return getBaseObject().getAllowFlight();
    }

    @Override
    public void setAllowFlight(boolean b) {
        getBaseObject().setAllowFlight(b);
    }

    @Override
    public void hidePlayer(Player player) {
        getBaseObject().hidePlayer(player);
    }

    @Override
    public void showPlayer(Player player) {
        getBaseObject().showPlayer(player);
    }

    @Override
    public boolean canSee(Player player) {
        return getBaseObject().canSee(player);
    }

    @Override
    public Location getLocation() {
        return getBaseObject().getLocation();
    }

    @Override
    public Location getLocation(Location location) {
        return getBaseObject().getLocation(location);
    }

    @Override
    public Vector getVelocity() {
        return getBaseObject().getVelocity();
    }

    @Override
    public void setVelocity(Vector vector) {
        getBaseObject().setVelocity(vector);
    }

    @Override
    @Deprecated
    public boolean isOnGround() {
        return getBaseObject().isOnGround();
    }

    @Override
    public World getWorld() {
        return getBaseObject().getWorld();
    }

    @Override
    public boolean teleport(Location location) {
        return getBaseObject().teleport(location);
    }

    @Override
    public boolean teleport(Location location, PlayerTeleportEvent.TeleportCause teleportCause) {
        return getBaseObject().teleport(location, teleportCause);
    }

    @Override
    public boolean teleport(Entity entity) {
        return getBaseObject().teleport(entity);
    }

    @Override
    public boolean teleport(Entity entity, PlayerTeleportEvent.TeleportCause teleportCause) {
        return getBaseObject().teleport(entity, teleportCause);
    }

    @Override
    public List<Entity> getNearbyEntities(double v, double v2, double v3) {
        return getBaseObject().getNearbyEntities(v, v2, v3);
    }

    @Override
    public int getEntityId() {
        return getBaseObject().getEntityId();
    }

    @Override
    public int getFireTicks() {
        return getBaseObject().getFireTicks();
    }

    @Override
    public void setFireTicks(int i) {
        getBaseObject().setFireTicks(i);
    }

    @Override
    public int getMaxFireTicks() {
        return getBaseObject().getMaxFireTicks();
    }

    @Override
    public void remove() {
        getBaseObject().remove();
    }

    @Override
    public boolean isDead() {
        return getBaseObject().isDead();
    }

    @Override
    public boolean isValid() {
        return getBaseObject().isValid();
    }

    @Override
    public Entity getPassenger() {
        return getBaseObject().getPassenger();
    }

    @Override
    public boolean setPassenger(Entity entity) {
        return getBaseObject().setPassenger(entity);
    }

    @Override
    public boolean isEmpty() {
        return getBaseObject().isEmpty();
    }

    @Override
    public boolean eject() {
        return getBaseObject().eject();
    }

    @Override
    public float getFallDistance() {
        return getBaseObject().getFallDistance();
    }

    @Override
    public void setFallDistance(float v) {
        getBaseObject().setFallDistance(v);
    }

    @Override
    public EntityDamageEvent getLastDamageCause() {
        return getBaseObject().getLastDamageCause();
    }

    @Override
    public void setLastDamageCause(EntityDamageEvent entityDamageEvent) {
        getBaseObject().setLastDamageCause(entityDamageEvent);
    }

    @Override
    public UUID getUniqueId() {
        if (getBaseObject() == null) {
            return null;
        }
        return getBaseObject().getUniqueId();
    }

    @Override
    public boolean isBanned() {
        return getBaseObject().isBanned();
    }

    @Override
    @Deprecated
    public void setBanned(boolean b) {
        getBaseObject().setBanned(b);
    }

    @Override
    public boolean isWhitelisted() {
        return getBaseObject().isWhitelisted();
    }

    @Override
    public void setWhitelisted(boolean b) {
        getBaseObject().setWhitelisted(b);
    }

    @Override
    public Player getPlayer() {
        return getBaseObject();
    }

    @Override
    public long getFirstPlayed() {
        return getBaseObject().getFirstPlayed();
    }

    @Override
    public long getLastPlayed() {
        return getBaseObject().getLastPlayed();
    }

    @Override
    public boolean hasPlayedBefore() {
        return getBaseObject().hasPlayedBefore();
    }

    @Override
    public int getTicksLived() {
        return getBaseObject().getTicksLived();
    }

    @Override
    public void setTicksLived(int i) {
        getBaseObject().setTicksLived(i);
    }

    @Override
    public void playEffect(EntityEffect entityEffect) {
        getBaseObject().playEffect(entityEffect);
    }

    @Override
    public EntityType getType() {
        return getBaseObject().getType();
    }

    @Override
    public boolean isInsideVehicle() {
        return getBaseObject().isInsideVehicle();
    }

    @Override
    public boolean leaveVehicle() {
        return getBaseObject().leaveVehicle();
    }

    @Override
    public Entity getVehicle() {
        return getBaseObject().getVehicle();
    }

    @Override
    public boolean isFlying() {
        return getBaseObject().isFlying();
    }

    @Override
    public void setFlying(boolean b) {
        getBaseObject().setFlying(b);
    }

    @Override
    public float getFlySpeed() {
        return getBaseObject().getFlySpeed();
    }

    @Override
    public void setFlySpeed(float v) throws IllegalArgumentException {
        getBaseObject().setFlySpeed(v);
    }

    @Override
    public float getWalkSpeed() {
        return getBaseObject().getWalkSpeed();
    }

    @Override
    public void setWalkSpeed(float v) throws IllegalArgumentException {
        getBaseObject().setWalkSpeed(v);
    }

    @Override
    @Deprecated
    public void setTexturePack(String s) {
        getBaseObject().setTexturePack(s);
    }

    @Override
    public void setResourcePack(String s) {
        getBaseObject().setResourcePack(s);
    }

    @Override
    public CraftScoreboard getScoreboard() {
        return (CraftScoreboard) getBaseObject().getScoreboard();
    }

    @Override
    public void setScoreboard(Scoreboard scoreboard) throws IllegalArgumentException, IllegalStateException {
        getBaseObject().setScoreboard(scoreboard);
    }

    @Override
    public boolean isHealthScaled() {
        return getBaseObject().isHealthScaled();
    }

    @Override
    public void setHealthScaled(boolean b) {
        getBaseObject().setHealthScaled(b);
    }

    @Override
    public double getHealthScale() {
        return getBaseObject().getHealthScale();
    }

    @Override
    public void setHealthScale(double v) throws IllegalArgumentException {
        getBaseObject().setHealthScale(v);
    }

    @Override
    public Player.Spigot spigot() {
        return getBaseObject().spigot();
    }

    @Override
    public Map<String, Object> serialize() {
        return getBaseObject().serialize();
    }

    @Override
    public boolean isOnline() {
        return getBaseObject().isOnline();
    }

    @Override
    public PlayerInventory getInventory() {
        return getBaseObject().getInventory();
    }

    @Override
    public Inventory getEnderChest() {
        return getBaseObject().getEnderChest();
    }

    @Override
    public boolean setWindowProperty(InventoryView.Property property, int i) {
        return getBaseObject().setWindowProperty(property, i);
    }

    @Override
    public InventoryView getOpenInventory() {
        return getBaseObject().getOpenInventory();
    }

    @Override
    public InventoryView openInventory(Inventory itemStacks) {
        return getBaseObject().openInventory(itemStacks);
    }

    @Override
    public InventoryView openWorkbench(Location location, boolean b) {
        return getBaseObject().openWorkbench(location, b);
    }

    @Override
    public InventoryView openEnchanting(Location location, boolean b) {
        return getBaseObject().openEnchanting(location, b);
    }

    @Override
    public void openInventory(InventoryView inventoryView) {
        getBaseObject().openInventory(inventoryView);
    }

    @Override
    public void closeInventory() {
        getBaseObject().closeInventory();
    }

    @Override
    public ItemStack getItemInHand() {
        return getBaseObject().getItemInHand();
    }

    @Override
    public void setItemInHand(ItemStack itemStack) {
        getBaseObject().setItemInHand(itemStack);
    }

    @Override
    public ItemStack getItemOnCursor() {
        return getBaseObject().getItemOnCursor();
    }

    @Override
    public void setItemOnCursor(ItemStack itemStack) {
        getBaseObject().setItemOnCursor(itemStack);
    }

    @Override
    public boolean isSleeping() {
        return getBaseObject().isSleeping();
    }

    @Override
    public int getSleepTicks() {
        return getBaseObject().getSleepTicks();
    }

    @Override
    public GameMode getGameMode() {
        return getBaseObject().getGameMode();
    }

    @Override
    public void setGameMode(GameMode gameMode) {
        getBaseObject().setGameMode(gameMode);
    }

    @Override
    public boolean isBlocking() {
        return getBaseObject().isBlocking();
    }

    @Override
    public int getExpToLevel() {
        return getBaseObject().getExpToLevel();
    }

    @Override
    public double getEyeHeight() {
        return getBaseObject().getEyeHeight();
    }

    @Override
    public double getEyeHeight(boolean b) {
        return getBaseObject().getEyeHeight(b);
    }

    @Override
    public Location getEyeLocation() {
        return getBaseObject().getEyeLocation();
    }

    @Override
    @Deprecated
    public List<Block> getLineOfSight(HashSet<Byte> bytes, int i) {
        return getBaseObject().getLineOfSight(bytes, i);
    }

    @Override
    public List<Block> getLineOfSight(Set<Material> set, int i) {
        return getBaseObject().getLineOfSight(set, i);
    }

    @Override
    @Deprecated
    public Block getTargetBlock(HashSet<Byte> bytes, int i) {
        return getBaseObject().getTargetBlock(bytes, i);
    }

    @Override
    public Block getTargetBlock(Set<Material> set, int i) {
        return getBaseObject().getTargetBlock(set, i);
    }

    @Override
    @Deprecated
    public List<Block> getLastTwoTargetBlocks(HashSet<Byte> bytes, int i) {
        return getBaseObject().getLastTwoTargetBlocks(bytes, i);
    }

    @Override
    public List<Block> getLastTwoTargetBlocks(Set<Material> set, int i) {
        return getBaseObject().getLastTwoTargetBlocks(set, i);
    }

    @Override
    @Deprecated
    public Egg throwEgg() {
        return getBaseObject().throwEgg();
    }

    @Override
    @Deprecated
    public Snowball throwSnowball() {
        return getBaseObject().throwSnowball();
    }

    @Override
    @Deprecated
    public Arrow shootArrow() {
        return getBaseObject().shootArrow();
    }

    @Override
    public int getRemainingAir() {
        return getBaseObject().getRemainingAir();
    }

    @Override
    public void setRemainingAir(int i) {
        getBaseObject().setRemainingAir(i);
    }

    @Override
    public int getMaximumAir() {
        return getBaseObject().getMaximumAir();
    }

    @Override
    public void setMaximumAir(int i) {
        getBaseObject().setMaximumAir(i);
    }

    @Override
    public int getMaximumNoDamageTicks() {
        return getBaseObject().getMaximumNoDamageTicks();
    }

    @Override
    public void setMaximumNoDamageTicks(int i) {
        getBaseObject().setMaximumNoDamageTicks(i);
    }

    @Override
    public double getLastDamage() {
        return getBaseObject().getLastDamage();
    }

    @Override
    public void setLastDamage(double v) {
        getBaseObject().setLastDamage(v);
    }

    @Override
    public int getNoDamageTicks() {
        return getBaseObject().getNoDamageTicks();
    }

    @Override
    public void setNoDamageTicks(int i) {
        getBaseObject().setNoDamageTicks(i);
    }

    @Override
    public Player getKiller() {
        return getBaseObject().getKiller();
    }

    @Override
    public boolean addPotionEffect(PotionEffect potionEffect) {
        return getBaseObject().addPotionEffect(potionEffect);
    }

    @Override
    public boolean addPotionEffect(PotionEffect potionEffect, boolean b) {
        return getBaseObject().addPotionEffect(potionEffect, b);
    }

    @Override
    public boolean addPotionEffects(Collection<PotionEffect> potionEffects) {
        return getBaseObject().addPotionEffects(potionEffects);
    }

    @Override
    public boolean hasPotionEffect(PotionEffectType potionEffectType) {
        return getBaseObject().hasPotionEffect(potionEffectType);
    }

    @Override
    public void removePotionEffect(PotionEffectType potionEffectType) {
        getBaseObject().removePotionEffect(potionEffectType);
    }

    @Override
    public Collection<PotionEffect> getActivePotionEffects() {
        return getBaseObject().getActivePotionEffects();
    }

    @Override
    public boolean hasLineOfSight(Entity entity) {
        return getBaseObject().hasLineOfSight(entity);
    }

    @Override
    public boolean getRemoveWhenFarAway() {
        return getBaseObject().getRemoveWhenFarAway();
    }

    @Override
    public void setRemoveWhenFarAway(boolean b) {
        getBaseObject().setRemoveWhenFarAway(b);
    }

    @Override
    public EntityEquipment getEquipment() {
        return getBaseObject().getEquipment();
    }

    @Override
    public boolean getCanPickupItems() {
        return getBaseObject().getCanPickupItems();
    }

    @Override
    public void setCanPickupItems(boolean b) {
        getBaseObject().setCanPickupItems(b);
    }

    @Override
    public String getCustomName() {
        return getBaseObject().getCustomName();
    }

    @Override
    public void setCustomName(String s) {
        getBaseObject().setCustomName(s);
    }

    @Override
    public boolean isCustomNameVisible() {
        return getBaseObject().isCustomNameVisible();
    }

    @Override
    public void setCustomNameVisible(boolean b) {
        getBaseObject().setCustomNameVisible(b);
    }

    @Override
    public boolean isLeashed() {
        return getBaseObject().isLeashed();
    }

    @Override
    public Entity getLeashHolder() throws IllegalStateException {
        return getBaseObject().getLeashHolder();
    }

    @Override
    public boolean setLeashHolder(Entity entity) {
        return getBaseObject().setLeashHolder(entity);
    }

    @Override
    public void damage(double v) {
        getBaseObject().damage(v);
    }

    @Override
    public void damage(double v, Entity entity) {
        getBaseObject().damage(v, entity);
    }

    @Override
    public double getHealth() {
        return getBaseObject().getHealth();
    }

    @Override
    public void setHealth(double v) {
        getBaseObject().setHealth(v);
    }

    @Override
    public double getMaxHealth() {
        return getBaseObject().getMaxHealth();
    }

    @Override
    public void setMaxHealth(double v) {
        getBaseObject().setMaxHealth(v);
    }

    @Override
    public void resetMaxHealth() {
        getBaseObject().resetMaxHealth();
    }

    @Override
    public void setMetadata(String s, MetadataValue metadataValue) {
        getBaseObject().setMetadata(s, metadataValue);
    }

    @Override
    public List<MetadataValue> getMetadata(String s) {
        return getBaseObject().getMetadata(s);
    }

    @Override
    public boolean hasMetadata(String s) {
        return getBaseObject().hasMetadata(s);
    }

    @Override
    public void removeMetadata(String s, Plugin plugin) {
        getBaseObject().removeMetadata(s, plugin);
    }

    @Override
    public void sendPluginMessage(Plugin plugin, String s, byte[] bytes) {
        if (!silent) {
            getBaseObject().sendPluginMessage(plugin, s, bytes);
        }
        if (getProxy() instanceof PluginMessageRecipient) {
            ((PluginMessageRecipient) getProxy()).sendPluginMessage(plugin, s, bytes);
        }
    }

    @Override
    public Set<String> getListeningPluginChannels() {
        return getBaseObject().getListeningPluginChannels();
    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> aClass) {
        return getBaseObject().launchProjectile(aClass);
    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> aClass, Vector vector) {
        return getBaseObject().launchProjectile(aClass, vector);
    }
}
