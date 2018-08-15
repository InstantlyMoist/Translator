package me.kyllian.translator.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

public class PlayerData {

    private UUID uuid;
    private Language language;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getMinecraftLanguage() {
        try {
            Player player = Bukkit.getPlayer(uuid);
            Object ep = getMethod("getHandle", player.getClass()).invoke(player, (Object[]) null);
            Field field = ep.getClass().getField("locale");
            field.setAccessible(true);
            String language = (String) field.get(ep);
            return language.split("_")[0];
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        return null;
    }

    private Method getMethod(String name, Class<?> minecraftClass) {
        for (Method method : minecraftClass.getDeclaredMethods()) {
            if (method.getName().equals(name)) return method;
        }
        return null;
    }
}
