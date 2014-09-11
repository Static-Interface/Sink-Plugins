/*
 * Copyright (c) 2013 - 2014 http://adventuria.eu, http://static-interface.de and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.static_interface.sinklibrary;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.*;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import java.net.InetSocketAddress;
import java.util.*;

public class FakeCommandSender implements Player
{
    Player base;
    CommandSender faker;
    public FakeCommandSender(Player base, CommandSender faker)
    {
        this.base = base;
        this.faker = faker;
    }

    @Override
    public String getDisplayName()
    {
        return base.getDisplayName();
    }

    @Override
    public void setDisplayName(String s)
    {
        base.setDisplayName(s);
    }

    @Override
    public String getPlayerListName()
    {
        return base.getPlayerListName();
    }

    @Override
    public void setPlayerListName(String s)
    {
        base.setPlayerListName(s);
    }

    @Override
    public void setCompassTarget(Location location)
    {
        base.setCompassTarget(location);
    }

    @Override
    public Location getCompassTarget()
    {
        return base.getCompassTarget();
    }

    @Override
    public InetSocketAddress getAddress()
    {
        return base.getAddress();
    }

    @Override
    public boolean isConversing()
    {
        return base.isConversing();
    }

    @Override
    public void acceptConversationInput(String s)
    {
        base.acceptConversationInput(s);
    }

    @Override
    public boolean beginConversation(Conversation conversation)
    {
        return base.beginConversation(conversation);
    }

    @Override
    public void abandonConversation(Conversation conversation)
    {
        base.abandonConversation(conversation);
    }

    @Override
    public void abandonConversation(Conversation conversation, ConversationAbandonedEvent conversationAbandonedEvent)
    {
        base.abandonConversation(conversation, conversationAbandonedEvent);
    }

    @Override
    public void sendRawMessage(String s)
    {
        base.sendRawMessage(s);
        if(faker instanceof Player) ((Player)faker).sendRawMessage(s);
    }

    @Override
    public void kickPlayer(String s)
    {
        base.kickPlayer(s);
    }

    @Override
    public void chat(String s)
    {
        base.chat(s);
    }

    @Override
    public boolean performCommand(String s)
    {
        return base.performCommand(s);
    }

    @Override
    public boolean isSneaking()
    {
        return base.isSneaking();
    }

    @Override
    public void setSneaking(boolean b)
    {
        base.setSneaking(b);
    }

    @Override
    public boolean isSprinting()
    {
        return base.isSprinting();
    }

    @Override
    public void setSprinting(boolean b)
    {
        base.setSprinting(b);
    }

    @Override
    public void saveData()
    {
        base.saveData();
    }

    @Override
    public void loadData()
    {
        base.loadData();
    }

    @Override
    public void setSleepingIgnored(boolean b)
    {
        base.setSleepingIgnored(b);
    }

    @Override
    public boolean isSleepingIgnored()
    {
        return base.isSleepingIgnored();
    }

    @Override
    @Deprecated
    public void playNote(Location location, byte b, byte b2)
    {
        base.playNote(location, b, b2);
    }

    @Override
    public void playNote(Location location, Instrument instrument, Note note)
    {
        base.playNote(location, instrument, note);
    }

    @Override
    public void playSound(Location location, Sound sound, float v, float v2)
    {
        base.playSound(location, sound, v, v2);
    }

    @Override
    @Deprecated
    public void playSound(Location location, String s, float v, float v2)
    {
        base.playSound(location, s, v, v2);
    }

    @Override
    @Deprecated
    public void playEffect(Location location, Effect effect, int i)
    {
        base.playEffect(location, effect, i);
    }

    @Override
    public <T> void playEffect(Location location, Effect effect, T t)
    {
        base.playEffect(location, effect, t);
    }

    @Override
    @Deprecated
    public void sendBlockChange(Location location, Material material, byte b)
    {
        base.sendBlockChange(location, material, b);
    }

    @Override
    @Deprecated
    public boolean sendChunkChange(Location location, int i, int i2, int i3, byte[] bytes)
    {
        return base.sendChunkChange(location, i, i2, i3, bytes);
    }

    @Override
    @Deprecated
    public void sendBlockChange(Location location, int i, byte b)
    {
        base.sendBlockChange(location, i, b);
    }

    @Override
    public void sendSignChange(Location location, String[] strings) throws IllegalArgumentException
    {
        base.sendSignChange(location, strings);
    }

    @Override
    public void sendMap(MapView mapView)
    {
        base.sendMap(mapView);
    }

    @Override
    @Deprecated
    public void updateInventory()
    {
        base.updateInventory();
    }

    @Override
    public void awardAchievement(Achievement achievement)
    {
        base.awardAchievement(achievement);
    }

    @Override
    public void removeAchievement(Achievement achievement)
    {
        base.removeAchievement(achievement);
    }

    @Override
    public boolean hasAchievement(Achievement achievement)
    {
        return base.hasAchievement(achievement);
    }

    @Override
    public void incrementStatistic(Statistic statistic) throws IllegalArgumentException
    {
        base.incrementStatistic(statistic);
    }

    @Override
    public void decrementStatistic(Statistic statistic) throws IllegalArgumentException
    {
        base.decrementStatistic(statistic);
    }

    @Override
    public void incrementStatistic(Statistic statistic, int i) throws IllegalArgumentException
    {
        base.incrementStatistic(statistic, i);
    }

    @Override
    public void decrementStatistic(Statistic statistic, int i) throws IllegalArgumentException
    {
        base.decrementStatistic(statistic, i);
    }

    @Override
    public void setStatistic(Statistic statistic, int i) throws IllegalArgumentException
    {
        base.setStatistic(statistic, i);
    }

    @Override
    public int getStatistic(Statistic statistic) throws IllegalArgumentException
    {
        return base.getStatistic(statistic);
    }

    @Override
    public void incrementStatistic(Statistic statistic, Material material) throws IllegalArgumentException
    {
        base.incrementStatistic(statistic, material);
    }

    @Override
    public void decrementStatistic(Statistic statistic, Material material) throws IllegalArgumentException
    {
        base.decrementStatistic(statistic, material);
    }

    @Override
    public int getStatistic(Statistic statistic, Material material) throws IllegalArgumentException
    {
        return base.getStatistic(statistic, material);
    }

    @Override
    public void incrementStatistic(Statistic statistic, Material material, int i) throws IllegalArgumentException
    {
        base.incrementStatistic(statistic, material, i);
    }

    @Override
    public void decrementStatistic(Statistic statistic, Material material, int i) throws IllegalArgumentException
    {
        base.decrementStatistic(statistic, material, i);
    }

    @Override
    public void setStatistic(Statistic statistic, Material material, int i) throws IllegalArgumentException
    {
        base.setStatistic(statistic, material, i);
    }

    @Override
    public void incrementStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException
    {
        base.incrementStatistic(statistic, entityType);
    }

    @Override
    public void decrementStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException
    {
        base.decrementStatistic(statistic, entityType);
    }

    @Override
    public int getStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException
    {
        return base.getStatistic(statistic, entityType);
    }

    @Override
    public void incrementStatistic(Statistic statistic, EntityType entityType, int i) throws IllegalArgumentException
    {
        base.incrementStatistic(statistic, entityType, i);
    }

    @Override
    public void decrementStatistic(Statistic statistic, EntityType entityType, int i)
    {
        base.decrementStatistic(statistic, entityType, i);
    }

    @Override
    public void setStatistic(Statistic statistic, EntityType entityType, int i)
    {
        base.setStatistic(statistic, entityType, i);
    }

    @Override
    public void setPlayerTime(long l, boolean b)
    {
        base.setPlayerTime(l, b);
    }

    @Override
    public long getPlayerTime()
    {
        return base.getPlayerTime();
    }

    @Override
    public long getPlayerTimeOffset()
    {
        return base.getPlayerTimeOffset();
    }

    @Override
    public boolean isPlayerTimeRelative()
    {
        return base.isPlayerTimeRelative();
    }

    @Override
    public void resetPlayerTime()
    {
        base.resetPlayerTime();
    }

    @Override
    public void setPlayerWeather(WeatherType weatherType)
    {
        base.setPlayerWeather(weatherType);
    }

    @Override
    public WeatherType getPlayerWeather()
    {
        return base.getPlayerWeather();
    }

    @Override
    public void resetPlayerWeather()
    {
        base.resetPlayerWeather();
    }

    @Override
    public void giveExp(int i)
    {
        base.giveExp(i);
    }

    @Override
    public void giveExpLevels(int i)
    {
        base.giveExpLevels(i);
    }

    @Override
    public float getExp()
    {
        return base.getExp();
    }

    @Override
    public void setExp(float v)
    {
        base.setExp(v);
    }

    @Override
    public int getLevel()
    {
        return base.getLevel();
    }

    @Override
    public void setLevel(int i)
    {
        base.setLevel(i);
    }

    @Override
    public int getTotalExperience()
    {
        return base.getTotalExperience();
    }

    @Override
    public void setTotalExperience(int i)
    {
        base.setTotalExperience(i);
    }

    @Override
    public float getExhaustion()
    {
        return base.getExhaustion();
    }

    @Override
    public void setExhaustion(float v)
    {
        base.setExhaustion(v);
    }

    @Override
    public float getSaturation()
    {
        return base.getSaturation();
    }

    @Override
    public void setSaturation(float v)
    {
        base.setSaturation(v);
    }

    @Override
    public int getFoodLevel()
    {
        return base.getFoodLevel();
    }

    @Override
    public void setFoodLevel(int i)
    {
        base.setFoodLevel(i);
    }

    @Override
    public Location getBedSpawnLocation()
    {
        return base.getBedSpawnLocation();
    }

    @Override
    public void setBedSpawnLocation(Location location)
    {
        base.setBedSpawnLocation(location);
    }

    @Override
    public void setBedSpawnLocation(Location location, boolean b)
    {
        base.setBedSpawnLocation(location, b);
    }

    @Override
    public boolean getAllowFlight()
    {
        return base.getAllowFlight();
    }

    @Override
    public void setAllowFlight(boolean b)
    {
        base.setAllowFlight(b);
    }

    @Override
    public void hidePlayer(Player player)
    {
        base.hidePlayer(player);
    }

    @Override
    public void showPlayer(Player player)
    {
        base.showPlayer(player);
    }

    @Override
    public boolean canSee(Player player)
    {
        return base.canSee(player);
    }

    @Override
    public Location getLocation()
    {
        return base.getLocation();
    }

    @Override
    public Location getLocation(Location location)
    {
        return base.getLocation(location);
    }

    @Override
    public void setVelocity(Vector vector)
    {
        base.setVelocity(vector);
    }

    @Override
    public Vector getVelocity()
    {
        return base.getVelocity();
    }

    @Override
    @Deprecated
    public boolean isOnGround()
    {
        return base.isOnGround();
    }

    @Override
    public World getWorld()
    {
        return base.getWorld();
    }

    @Override
    public boolean teleport(Location location)
    {
        return base.teleport(location);
    }

    @Override
    public boolean teleport(Location location, PlayerTeleportEvent.TeleportCause teleportCause)
    {
        return base.teleport(location, teleportCause);
    }

    @Override
    public boolean teleport(Entity entity)
    {
        return base.teleport(entity);
    }

    @Override
    public boolean teleport(Entity entity, PlayerTeleportEvent.TeleportCause teleportCause)
    {
        return base.teleport(entity, teleportCause);
    }

    @Override
    public List<Entity> getNearbyEntities(double v, double v2, double v3)
    {
        return base.getNearbyEntities(v, v2, v3);
    }

    @Override
    public int getEntityId()
    {
        return base.getEntityId();
    }

    @Override
    public int getFireTicks()
    {
        return base.getFireTicks();
    }

    @Override
    public int getMaxFireTicks()
    {
        return base.getMaxFireTicks();
    }

    @Override
    public void setFireTicks(int i)
    {
        base.setFireTicks(i);
    }

    @Override
    public void remove()
    {
        base.remove();
    }

    @Override
    public boolean isDead()
    {
        return base.isDead();
    }

    @Override
    public boolean isValid()
    {
        return base.isValid();
    }

    @Override
    public Server getServer()
    {
        return base.getServer();
    }

    @Override
    public Entity getPassenger()
    {
        return base.getPassenger();
    }

    @Override
    public boolean setPassenger(Entity entity)
    {
        return base.setPassenger(entity);
    }

    @Override
    public boolean isEmpty()
    {
        return base.isEmpty();
    }

    @Override
    public boolean eject()
    {
        return base.eject();
    }

    @Override
    public float getFallDistance()
    {
        return base.getFallDistance();
    }

    @Override
    public void setFallDistance(float v)
    {
        base.setFallDistance(v);
    }

    @Override
    public void setLastDamageCause(EntityDamageEvent entityDamageEvent)
    {
        base.setLastDamageCause(entityDamageEvent);
    }

    @Override
    public EntityDamageEvent getLastDamageCause()
    {
        return base.getLastDamageCause();
    }

    @Override
    public UUID getUniqueId()
    {
        return base.getUniqueId();
    }

    @Override
    public boolean isBanned()
    {
        return base.isBanned();
    }

    @Override
    @Deprecated
    public void setBanned(boolean b)
    {
        base.setBanned(b);
    }

    @Override
    public boolean isWhitelisted()
    {
        return base.isWhitelisted();
    }

    @Override
    public void setWhitelisted(boolean b)
    {
        base.setWhitelisted(b);
    }

    @Override
    public Player getPlayer()
    {
        return base;
    }

    @Override
    public long getFirstPlayed()
    {
        return base.getFirstPlayed();
    }

    @Override
    public long getLastPlayed()
    {
        return base.getLastPlayed();
    }

    @Override
    public boolean hasPlayedBefore()
    {
        return base.hasPlayedBefore();
    }

    @Override
    public int getTicksLived()
    {
        return base.getTicksLived();
    }

    @Override
    public void setTicksLived(int i)
    {
        base.setTicksLived(i);
    }

    @Override
    public void playEffect(EntityEffect entityEffect)
    {
        base.playEffect(entityEffect);
    }

    @Override
    public EntityType getType()
    {
        return base.getType();
    }

    @Override
    public boolean isInsideVehicle()
    {
        return base.isInsideVehicle();
    }

    @Override
    public boolean leaveVehicle()
    {
        return base.leaveVehicle();
    }

    @Override
    public Entity getVehicle()
    {
        return base.getVehicle();
    }

    @Override
    public boolean isFlying()
    {
        return base.isFlying();
    }

    @Override
    public void setFlying(boolean b)
    {
        base.setFlying(b);
    }

    @Override
    public void setFlySpeed(float v) throws IllegalArgumentException
    {
        base.setFlySpeed(v);
    }

    @Override
    public void setWalkSpeed(float v) throws IllegalArgumentException
    {
        base.setWalkSpeed(v);
    }

    @Override
    public float getFlySpeed()
    {
        return base.getFlySpeed();
    }

    @Override
    public float getWalkSpeed()
    {
        return base.getWalkSpeed();
    }

    @Override
    @Deprecated
    public void setTexturePack(String s)
    {
        base.setTexturePack(s);
    }

    @Override
    public void setResourcePack(String s)
    {
        base.setResourcePack(s);
    }

    @Override
    public Scoreboard getScoreboard()
    {
        return base.getScoreboard();
    }

    @Override
    public void setScoreboard(Scoreboard scoreboard) throws IllegalArgumentException, IllegalStateException
    {
        base.setScoreboard(scoreboard);
    }

    @Override
    public boolean isHealthScaled()
    {
        return base.isHealthScaled();
    }

    @Override
    public void setHealthScaled(boolean b)
    {
        base.setHealthScaled(b);
    }

    @Override
    public void setHealthScale(double v) throws IllegalArgumentException
    {
        base.setHealthScale(v);
    }

    @Override
    public double getHealthScale()
    {
        return base.getHealthScale();
    }

    @Override
    public void sendMessage(String s)
    {
        base.sendMessage(s);
        faker.sendMessage(s);
    }

    @Override
    public void sendMessage(String[] strings)
    {
        base.sendMessage(strings);
        faker.sendMessage(strings);
    }

    @Override
    public Map<String, Object> serialize()
    {
        return base.serialize();
    }

    @Override
    public boolean isOnline()
    {
        return base.isOnline();
    }

    @Override
    public String getName()
    {
        return base.getName();
    }

    @Override
    public PlayerInventory getInventory()
    {
        return base.getInventory();
    }

    @Override
    public Inventory getEnderChest()
    {
        return base.getEnderChest();
    }

    @Override
    public boolean setWindowProperty(InventoryView.Property property, int i)
    {
        return base.setWindowProperty(property, i);
    }

    @Override
    public InventoryView getOpenInventory()
    {
        return base.getOpenInventory();
    }

    @Override
    public InventoryView openInventory(Inventory itemStacks)
    {
        return base.openInventory(itemStacks);
    }

    @Override
    public InventoryView openWorkbench(Location location, boolean b)
    {
        return base.openWorkbench(location, b);
    }

    @Override
    public InventoryView openEnchanting(Location location, boolean b)
    {
        return base.openEnchanting(location, b);
    }

    @Override
    public void openInventory(InventoryView inventoryView)
    {
        base.openInventory(inventoryView);
    }

    @Override
    public void closeInventory()
    {
        base.closeInventory();
    }

    @Override
    public ItemStack getItemInHand()
    {
        return base.getItemInHand();
    }

    @Override
    public void setItemInHand(ItemStack itemStack)
    {
        base.setItemInHand(itemStack);
    }

    @Override
    public ItemStack getItemOnCursor()
    {
        return base.getItemOnCursor();
    }

    @Override
    public void setItemOnCursor(ItemStack itemStack)
    {
        base.setItemOnCursor(itemStack);
    }

    @Override
    public boolean isSleeping()
    {
        return base.isSleeping();
    }

    @Override
    public int getSleepTicks()
    {
        return base.getSleepTicks();
    }

    @Override
    public GameMode getGameMode()
    {
        return base.getGameMode();
    }

    @Override
    public void setGameMode(GameMode gameMode)
    {
        base.setGameMode(gameMode);
    }

    @Override
    public boolean isBlocking()
    {
        return base.isBlocking();
    }

    @Override
    public int getExpToLevel()
    {
        return base.getExpToLevel();
    }

    @Override
    public double getEyeHeight()
    {
        return base.getEyeHeight();
    }

    @Override
    public double getEyeHeight(boolean b)
    {
        return base.getEyeHeight(b);
    }

    @Override
    public Location getEyeLocation()
    {
        return base.getEyeLocation();
    }

    @Override
    @Deprecated
    public List<Block> getLineOfSight(HashSet<Byte> bytes, int i)
    {
        return base.getLineOfSight(bytes, i);
    }

    @Override
    @Deprecated
    public Block getTargetBlock(HashSet<Byte> bytes, int i)
    {
        return base.getTargetBlock(bytes, i);
    }

    @Override
    @Deprecated
    public List<Block> getLastTwoTargetBlocks(HashSet<Byte> bytes, int i)
    {
        return base.getLastTwoTargetBlocks(bytes, i);
    }

    @Override
    @Deprecated
    public Egg throwEgg()
    {
        return base.throwEgg();
    }

    @Override
    @Deprecated
    public Snowball throwSnowball()
    {
        return base.throwSnowball();
    }

    @Override
    @Deprecated
    public Arrow shootArrow()
    {
        return base.shootArrow();
    }

    @Override
    public int getRemainingAir()
    {
        return base.getRemainingAir();
    }

    @Override
    public void setRemainingAir(int i)
    {
        base.setRemainingAir(i);
    }

    @Override
    public int getMaximumAir()
    {
        return base.getMaximumAir();
    }

    @Override
    public void setMaximumAir(int i)
    {
        base.setMaximumAir(i);
    }

    @Override
    public int getMaximumNoDamageTicks()
    {
        return base.getMaximumNoDamageTicks();
    }

    @Override
    public void setMaximumNoDamageTicks(int i)
    {
        base.setMaximumNoDamageTicks(i);
    }

    @Override
    public double getLastDamage()
    {
        return base.getLastDamage();
    }

    @Override
    @Deprecated
    public int _INVALID_getLastDamage()
    {
        return base._INVALID_getLastDamage();
    }

    @Override
    public void setLastDamage(double v)
    {
        base.setLastDamage(v);
    }

    @Override
    @Deprecated
    public void _INVALID_setLastDamage(int i)
    {
        base._INVALID_setLastDamage(i);
    }

    @Override
    public int getNoDamageTicks()
    {
        return base.getNoDamageTicks();
    }

    @Override
    public void setNoDamageTicks(int i)
    {
        base.setNoDamageTicks(i);
    }

    @Override
    public Player getKiller()
    {
        return base.getKiller();
    }

    @Override
    public boolean addPotionEffect(PotionEffect potionEffect)
    {
        return base.addPotionEffect(potionEffect);
    }

    @Override
    public boolean addPotionEffect(PotionEffect potionEffect, boolean b)
    {
        return base.addPotionEffect(potionEffect, b);
    }

    @Override
    public boolean addPotionEffects(Collection<PotionEffect> potionEffects)
    {
        return base.addPotionEffects(potionEffects);
    }

    @Override
    public boolean hasPotionEffect(PotionEffectType potionEffectType)
    {
        return base.hasPotionEffect(potionEffectType);
    }

    @Override
    public void removePotionEffect(PotionEffectType potionEffectType)
    {
        base.removePotionEffect(potionEffectType);
    }

    @Override
    public Collection<PotionEffect> getActivePotionEffects()
    {
        return base.getActivePotionEffects();
    }

    @Override
    public boolean hasLineOfSight(Entity entity)
    {
        return base.hasLineOfSight(entity);
    }

    @Override
    public boolean getRemoveWhenFarAway()
    {
        return base.getRemoveWhenFarAway();
    }

    @Override
    public void setRemoveWhenFarAway(boolean b)
    {
        base.setRemoveWhenFarAway(b);
    }

    @Override
    public EntityEquipment getEquipment()
    {
        return base.getEquipment();
    }

    @Override
    public void setCanPickupItems(boolean b)
    {
        base.setCanPickupItems(b);
    }

    @Override
    public boolean getCanPickupItems()
    {
        return base.getCanPickupItems();
    }

    @Override
    public void setCustomName(String s)
    {
        base.setCustomName(s);
    }

    @Override
    public String getCustomName()
    {
        return base.getCustomName();
    }

    @Override
    public void setCustomNameVisible(boolean b)
    {
        base.setCustomNameVisible(b);
    }

    @Override
    public boolean isCustomNameVisible()
    {
        return base.isCustomNameVisible();
    }

    @Override
    public boolean isLeashed()
    {
        return base.isLeashed();
    }

    @Override
    public Entity getLeashHolder() throws IllegalStateException
    {
        return base.getLeashHolder();
    }

    @Override
    public boolean setLeashHolder(Entity entity)
    {
        return base.setLeashHolder(entity);
    }

    @Override
    public void damage(double v)
    {
        base.damage(v);
    }

    @Override
    @Deprecated
    public void _INVALID_damage(int i)
    {
        base._INVALID_damage(i);
    }

    @Override
    public void damage(double v, Entity entity)
    {
        base.damage(v, entity);
    }

    @Override
    @Deprecated
    public void _INVALID_damage(int i, Entity entity)
    {
        base._INVALID_damage(i, entity);
    }

    @Override
    public double getHealth()
    {
        return base.getHealth();
    }

    @Override
    @Deprecated
    public int _INVALID_getHealth()
    {
        return base._INVALID_getHealth();
    }

    @Override
    public void setHealth(double v)
    {
        base.setHealth(v);
    }

    @Override
    @Deprecated
    public void _INVALID_setHealth(int i)
    {
        base._INVALID_setHealth(i);
    }

    @Override
    public double getMaxHealth()
    {
        return base.getMaxHealth();
    }

    @Override
    @Deprecated
    public int _INVALID_getMaxHealth()
    {
        return base._INVALID_getMaxHealth();
    }

    @Override
    public void setMaxHealth(double v)
    {
        base.setMaxHealth(v);
    }

    @Override
    @Deprecated
    public void _INVALID_setMaxHealth(int i)
    {
        base._INVALID_setMaxHealth(i);
    }

    @Override
    public void resetMaxHealth()
    {
        base.resetMaxHealth();
    }

    @Override
    public void setMetadata(String s, MetadataValue metadataValue)
    {
        base.setMetadata(s, metadataValue);
    }

    @Override
    public List<MetadataValue> getMetadata(String s)
    {
        return base.getMetadata(s);
    }

    @Override
    public boolean hasMetadata(String s)
    {
        return base.hasMetadata(s);
    }

    @Override
    public void removeMetadata(String s, Plugin plugin)
    {
        base.removeMetadata(s, plugin);
    }

    @Override
    public boolean isPermissionSet(String s)
    {
        return base.isPermissionSet(s);
    }

    @Override
    public boolean isPermissionSet(Permission permission)
    {
        return base.isPermissionSet(permission);
    }

    @Override
    public boolean hasPermission(String s)
    {
        return base.hasPermission(s);
    }

    @Override
    public boolean hasPermission(Permission permission)
    {
        return base.hasPermission(permission);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b)
    {
        return base.addAttachment(plugin, s, b);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin)
    {
        return base.addAttachment(plugin);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b, int i)
    {
        return base.addAttachment(plugin, s, b, i);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int i)
    {
        return base.addAttachment(plugin, i);
    }

    @Override
    public void removeAttachment(PermissionAttachment permissionAttachment)
    {
        base.removeAttachment(permissionAttachment);
    }

    @Override
    public void recalculatePermissions()
    {
        base.recalculatePermissions();
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions()
    {
        return base.getEffectivePermissions();
    }

    @Override
    public void sendPluginMessage(Plugin plugin, String s, byte[] bytes)
    {
        base.sendPluginMessage(plugin, s, bytes);
        if(faker instanceof Player) ((Player)faker).sendPluginMessage(plugin, s, bytes);
    }

    @Override
    public Set<String> getListeningPluginChannels()
    {
        return base.getListeningPluginChannels();
    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> aClass)
    {
        return base.launchProjectile(aClass);
    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> aClass, Vector vector)
    {
        return base.launchProjectile(aClass, vector);
    }

    @Override
    public boolean isOp()
    {
        return base.isOp();
    }

    @Override
    public void setOp(boolean b)
    {
        base.setOp(b);
    }

    @Override
    public int hashCode()
    {
        return base.hashCode(); //???
    }

    @Override
    public boolean equals(Object o)
    {
        return base.equals(o); //???
    }
}
