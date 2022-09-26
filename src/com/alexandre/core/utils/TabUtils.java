package com.alexandre.core.utils;

import io.netty.buffer.Unpooled;
import net.minecraft.server.v1_8_R3.PacketDataSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class TabUtils {

	public static void setHeaderFooter(Player p, String header, String footer) {
		PacketDataSerializer serializer = new PacketDataSerializer(Unpooled.buffer());
		serializer.a("{\"text\": \"" + header + "\"}");
		serializer.a("{\"text\": \"" + footer + "\"}");

		PacketPlayOutPlayerListHeaderFooter listPacket = new PacketPlayOutPlayerListHeaderFooter();
		try {
			listPacket.a(serializer);
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(listPacket);
		} catch(Exception ex) {}
	}
	
}
