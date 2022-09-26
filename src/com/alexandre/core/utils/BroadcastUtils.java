package com.alexandre.core.utils;

import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class BroadcastUtils {

	public static String expendColor(String text) {
		String coloredText = "";
		for (String t : text.split(" ")) {
			if (!coloredText.isEmpty()) {
				String color = getLastColors(coloredText);
				coloredText += " ";
				coloredText += color + t;
			} else {
				coloredText += t;
			}
		}
		return coloredText;
	}

	public static String getLastColors(String input) {
		StringBuilder result = new StringBuilder();
		int length = input.length();

		for (int index = length - 1; index > -1; index--) {
			char section = input.charAt(index);
			if (section == '\u00A7' && index < length - 1) {
				char c = input.charAt(index + 1);
				ChatColor color = ChatColor.getByChar(c);

				if (color != null) {
					result.insert(0, "§" + c);

					if (color.isColor() || color.equals(ChatColor.RESET)) {
						break;
					}
				}
			}
		}

		return result.toString();
	}

	public static void actionBar(String message) {
		Bukkit.getOnlinePlayers().stream().forEach(player -> {
			PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(message), (byte) 2);
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
		});
	}
	
	public static void chat(String message) {
		String m = expendColor(message);
		Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(m));
	}
	
	public static void chat(TextComponent message) {
		chat(message.getText());
	}
	
	public static void title(String title, String subtitle, int fadeIn, int duration, int fadeOut) {
		Bukkit.getOnlinePlayers().forEach(player -> Title.sendTitle(player, title, subtitle, fadeIn, duration, fadeOut));
	}
	
	public static void sound(Sound sound, float volume, float pitch) {
		Bukkit.getOnlinePlayers().forEach(player -> sound(player, player.getLocation(), sound, volume, pitch));
	}
	
	public static void sound(Location location, Sound sound, float volume, float pitch) {
		Bukkit.getOnlinePlayers().forEach(player -> sound(player, location, sound, volume, pitch));
	}
	
	private static void sound(Player player, Location location, Sound sound, float volume, float pitch) {
		player.playSound(location, sound, volume, pitch);
	}
	
	public static void xp(int level, float progress) {
		float xp = Math.min(1f, Math.max(0f, progress));
		
		Bukkit.getOnlinePlayers().forEach(player -> {
			player.setLevel(level);
			player.setExp(xp);
		});
	}
	
}
