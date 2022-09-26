package com.alexandre.core.utils;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Title {

	public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int duration, int fadeOut) {
		IChatBaseComponent chatTitle = ChatSerializer.a("{\"text\": \"" + title + "\"}");
		IChatBaseComponent chatSubtitle = ChatSerializer.a("{\"text\": \"" + subtitle + "\"}");

		PacketPlayOutTitle packetTitle = new PacketPlayOutTitle(EnumTitleAction.TITLE, chatTitle);
		PacketPlayOutTitle packetSubtitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, chatSubtitle);
		PacketPlayOutTitle length = new PacketPlayOutTitle(fadeIn, duration, fadeOut);
		

		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetTitle);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetSubtitle);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(length);
	}
	
}
