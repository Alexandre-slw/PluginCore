package com.alexandre.core.utils;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

public class NmsManager {
    private static JavaPlugin plugin;
    private static String versionSuffix;
    private static Method getHandleMethod;
    private static Field playerConnectionField;
    private static Method sendPacketMethod;
    private static Constructor<?> packetPlayOutCustomPayloadConstructor;
    private static Class<?> packetDataSerializerClass;
    private static Constructor<?> packetDataSerializerConstructor;
    private static Class<?> minecraftKeyClass;
    private static Constructor<?> minecraftKeyConstructor;
    private static Method wrappedBufferMethod;

    public static void init(JavaPlugin plugin) {
        NmsManager.plugin = plugin;
        final String packageName = plugin.getServer().getClass().getPackage().getName();
        final String[] parts = packageName.split("\\.");
        if (parts.length > 0) {
            final String suffix = parts[parts.length - 1];
            if (!suffix.startsWith("v")) {
                throw new RuntimeException("Failed to find version for running Minecraft server, got suffix " + suffix);
            }
            NmsManager.versionSuffix = suffix;
            plugin.getLogger().info("Found version " + NmsManager.versionSuffix);
        }
        final Class<?> craftPlayerClass = getClass("org.bukkit.craftbukkit." + NmsManager.versionSuffix + ".entity.CraftPlayer");
        if (craftPlayerClass == null) {
            throw new RuntimeException("Failed to find CraftPlayer class");
        }
        final Class<?> nmsPlayerClass = getClass("net.minecraft.server." + NmsManager.versionSuffix + ".EntityPlayer");
        if (nmsPlayerClass == null) {
            throw new RuntimeException("Failed to find EntityPlayer class");
        }
        final Class<?> playerConnectionClass = getClass("net.minecraft.server." + NmsManager.versionSuffix + ".PlayerConnection");
        if (playerConnectionClass == null) {
            throw new RuntimeException("Failed to find PlayerConnection class");
        }
        final Class<?> packetPlayOutCustomPayloadClass = getClass("net.minecraft.server." + NmsManager.versionSuffix + ".PacketPlayOutCustomPayload");
        if (packetPlayOutCustomPayloadClass == null) {
            throw new RuntimeException("Failed to find PacketPlayOutCustomPayload class");
        }
        NmsManager.packetPlayOutCustomPayloadConstructor = getConstructor(packetPlayOutCustomPayloadClass, String.class, byte[].class);
        if (NmsManager.packetPlayOutCustomPayloadConstructor == null) {
            NmsManager.packetDataSerializerClass = getClass("net.minecraft.server." + NmsManager.versionSuffix + ".PacketDataSerializer");
            if (NmsManager.packetDataSerializerClass == null) {
                throw new RuntimeException("Failed to find PacketPlayOutCustomPayload constructor or PacketDataSerializer class");
            }
            final Class<?> byteBufClass = getClass("io.netty.buffer.ByteBuf");
            if (byteBufClass == null) {
                throw new RuntimeException("Failed to find PacketPlayOutCustomPayload constructor or ByteBuf class");
            }
            NmsManager.packetDataSerializerConstructor = getConstructor(NmsManager.packetDataSerializerClass, byteBufClass);
            if (NmsManager.packetDataSerializerConstructor == null) {
                throw new RuntimeException("Failed to find PacketPlayOutCustomPayload constructor or PacketDataSerializer constructor");
            }
            final Class<?> unpooledClass = getClass("io.netty.buffer.Unpooled");
            if (unpooledClass == null) {
                throw new RuntimeException("Failed to find PacketPlayOutCustomPayload constructor or Unpooled class");
            }
            NmsManager.wrappedBufferMethod = getMethod(unpooledClass, "wrappedBuffer", byte[].class);
            if (NmsManager.wrappedBufferMethod == null) {
                throw new RuntimeException("Failed to find PacketPlayOutCustomPayload constructor or wrappedBuffer()");
            }
            NmsManager.packetPlayOutCustomPayloadConstructor = getConstructor(packetPlayOutCustomPayloadClass, String.class, NmsManager.packetDataSerializerClass);
            if (NmsManager.packetPlayOutCustomPayloadConstructor == null) {
                NmsManager.minecraftKeyClass = getClass("net.minecraft.server." + NmsManager.versionSuffix + ".MinecraftKey");
                if (NmsManager.minecraftKeyClass == null) {
                    throw new RuntimeException("Failed to find PacketPlayOutCustomPayload constructor or MinecraftKey class");
                }
                NmsManager.minecraftKeyConstructor = getConstructor(NmsManager.minecraftKeyClass, String.class, String.class);
                if (NmsManager.minecraftKeyConstructor == null) {
                    throw new RuntimeException("Failed to find PacketPlayOutCustomPayload constructor or MinecraftKey constructor");
                }
                NmsManager.packetPlayOutCustomPayloadConstructor = getConstructor(packetPlayOutCustomPayloadClass, NmsManager.minecraftKeyClass, NmsManager.packetDataSerializerClass);
                if (NmsManager.packetPlayOutCustomPayloadConstructor == null) {
                    throw new RuntimeException("Failed to find PacketPlayOutCustomPayload constructor");
                }
            }
        }
        NmsManager.getHandleMethod = getMethod(craftPlayerClass, "getHandle");
        if (NmsManager.getHandleMethod == null) {
            throw new RuntimeException("Failed to find CraftPlayer.getHandle()");
        }
        NmsManager.playerConnectionField = getField(nmsPlayerClass, "playerConnection");
        if (NmsManager.playerConnectionField == null) {
            throw new RuntimeException("Failed to find EntityPlayer.playerConnection");
        }
        NmsManager.sendPacketMethod = getMethod(playerConnectionClass, "sendPacket");
        if (NmsManager.sendPacketMethod == null) {
            throw new RuntimeException("Failed to find PlayerConnection.sendPacket()");
        }
    }

    public static void sendPluginMessage(final Player player, final String channel, final byte[] message) {
        try {
            Object packet;
            if (NmsManager.minecraftKeyClass != null) {
                final Object minecraftKey = NmsManager.minecraftKeyConstructor.newInstance("badlion", "timers");
                final Object byteBuf = NmsManager.wrappedBufferMethod.invoke(null, message);
                final Object packetDataSerializer = NmsManager.packetDataSerializerConstructor.newInstance(byteBuf);
                packet = NmsManager.packetPlayOutCustomPayloadConstructor.newInstance(minecraftKey, packetDataSerializer);
            } else if (NmsManager.packetDataSerializerClass != null) {
                final Object byteBuf2 = NmsManager.wrappedBufferMethod.invoke(null, message);
                final Object packetDataSerializer2 = NmsManager.packetDataSerializerConstructor.newInstance(byteBuf2);
                packet = NmsManager.packetPlayOutCustomPayloadConstructor.newInstance(channel, packetDataSerializer2);
            } else {
                packet = NmsManager.packetPlayOutCustomPayloadConstructor.newInstance(channel, message);
            }
            final Object nmsPlayer = NmsManager.getHandleMethod.invoke(player);
            final Object playerConnection = NmsManager.playerConnectionField.get(nmsPlayer);
            NmsManager.sendPacketMethod.invoke(playerConnection, packet);
        } catch (Exception ex) {
            NmsManager.plugin.getLogger().severe("Failed to send plugin message packet");
            ex.printStackTrace();
        }
    }

    public static void sendPacket(Player player, final String packetName, Object... params) {
        try {
            Object packet;
            final Class<?> packetClass = getClass("net.minecraft.server." + NmsManager.versionSuffix + "." + packetName);
            if (packetClass == null) throw new RuntimeException("Failed to find " + packetName + " class");

            Class<?>[] classes = new Class[params.length];
            for (int i = 0; i < params.length; i++) {
                classes[i] = params[i].getClass();
            }

            Constructor<?> constructor = getConstructor(packetClass, classes);
            packet = constructor.newInstance(params);
            final Object nmsPlayer = NmsManager.getHandleMethod.invoke(player);
            final Object playerConnection = NmsManager.playerConnectionField.get(nmsPlayer);
            NmsManager.sendPacketMethod.invoke(playerConnection, packet);
        } catch (Exception ex) {
            NmsManager.plugin.getLogger().severe("Failed to send " + packetName);
            ex.printStackTrace();
        }
    }

    private static Class<?> getClass(final String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private static Constructor<?> getConstructor(final Class<?> clazz, final Class<?>... params) {
        for (final Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (Arrays.equals(constructor.getParameterTypes(), params)) {
                constructor.setAccessible(true);
                return constructor;
            }
        }
        if (clazz.getDeclaredConstructors().length > 0) return clazz.getDeclaredConstructors()[0];
        return null;
    }

    private static Method getMethod(final Class<?> clazz, final String methodName, final Class<?>... params) {
        for (final Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                if (params.length <= 0) {
                    method.setAccessible(true);
                    return method;
                }
                if (Arrays.equals(method.getParameterTypes(), params)) {
                    method.setAccessible(true);
                    return method;
                }
            }
        }
        return null;
    }

    private static Field getField(final Class<?> clazz, final String fieldName) {
        for (final Field field : clazz.getDeclaredFields()) {
            if (field.getName().equals(fieldName)) {
                field.setAccessible(true);
                return field;
            }
        }
        return null;
    }
}
