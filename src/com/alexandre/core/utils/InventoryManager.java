package com.alexandre.core.utils;

import com.alexandre.core.api.inventory.ItemBuilder;
import com.alexandre.core.players.GamePlayer;
import com.alexandre.core.players.GamePlayersRegistry;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class InventoryManager {

	public static InventoryManager EMPTY = new InventoryManager();

	private HashMap<Integer, ItemStack> inventory = new HashMap<>();

	public void add(int slot, ItemStack item, String name) {
		ItemBuilder builder = new ItemBuilder(item);
		
		if (name != null) {
			builder.name(name);
		}

		builder.flags();
		item = builder.build();
		if (this.inventory.containsKey(slot)) this.inventory.replace(slot, item);
		else this.inventory.put(slot, item);
	}
	
	public void addSameMultiple(int fromSlot, int toSlot, ItemStack item, String name) {
		for (int slot = fromSlot; slot < toSlot; slot++) {			
			this.add(slot, item, name);
		}
	}
	
	public HashMap<Integer, ItemStack> getInventory() {
		return this.inventory;
	}
	
	public ItemStack getItem(int slot) {
		return this.getInventory().get(slot);
	}
	
	public static void setInventory(Player player, InventoryManager inventory) {
		GamePlayer gameplayer = GamePlayersRegistry.getPlayer(player);
		if (gameplayer != null) gameplayer.removeGui();
		player.closeInventory();
		
		player.getInventory().clear();
		for (int slot : inventory.getInventory().keySet()) {
			player.getInventory().setItem(slot, inventory.getItem(slot));
		}
		
		player.updateInventory();
	}
	
	public static void addInventory(Player player, InventoryManager inventory) {
		for (int slot : inventory.getInventory().keySet()) {
			player.getInventory().setItem(slot, inventory.getItem(slot));
		}
	}
	
}
