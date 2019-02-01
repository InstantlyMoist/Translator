package me.kyllian.translator;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.kyllian.translator.commands.LanguageCommand;
import me.kyllian.translator.commands.TranslatorCommand;
import me.kyllian.translator.listeners.AsyncPlayerChatListener;
import me.kyllian.translator.listeners.PlayerJoinListener;
import me.kyllian.translator.utils.*;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class TranslatorPlugin extends JavaPlugin {

    private HashMap<UUID, PlayerData> playerDataHashMap;
    private MessageHandler messageHandler;
    private DataHandler dataHandler;
    private TranslationHandler translationHandler;
    private String apiKey;
    public static TranslatorPlugin plugin;
    private UpdateChecker updateChecker;
    private boolean updateCheck;

    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        updateCheck = getConfig().getBoolean("UpdateChecking");
        updateChecker = new UpdateChecker(this, 59799);

        apiKey = getConfig().getString("APIKey");

        Metrics metrics = new Metrics(this);

        playerDataHashMap = new HashMap<>();

        new PlayerJoinListener(this);
        new AsyncPlayerChatListener(this);

        getCommand("language").setExecutor(new LanguageCommand(this));
        getCommand("translator").setExecutor(new TranslatorCommand(this));

        messageHandler = new MessageHandler(this);
        dataHandler = new DataHandler(this);
        translationHandler = new TranslationHandler();

        Bukkit.getOnlinePlayers().forEach(player -> {
            PlayerData playerData = getPlayerData(player.getUniqueId());
            playerData.setLanguage(Language.valueOf(getDataHandler().getData().getString(player.getUniqueId().toString())));
        });

        if (getConfig().getString("APIKey").equalsIgnoreCase("enterhere")) {
            Bukkit.getLogger().warning("The API key needs to be entered. Make sure to do so. Disabling plugin until you have a correct API key...");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") != null) {
            ProtocolLibrary.getProtocolManager().addPacketListener(
                    new PacketAdapter(this, PacketType.Play.Server.CHAT) {
                        @Override
                        public void onPacketSending(PacketEvent event) {
                            Player player = event.getPlayer();
                            if (!getEnabled(player)) return;
                            PlayerData playerData = getPlayerData(player.getUniqueId());
                            if (playerData.getLanguage() == Language.unknown) return;
                            try {
                                PacketContainer packet = event.getPacket();
                                String initialJsonS = ((WrappedChatComponent) packet.getChatComponents().read(0)).getJson();
                                if ((initialJsonS != null) && (!initialJsonS.equals("")) && (!initialJsonS.equals("\"\""))) {
                                    JsonObject packetJson = (JsonObject) new Gson().fromJson(initialJsonS, JsonObject.class);
                                    if (packetJson.get("extra") != null) {
                                        JsonArray jsonArray = packetJson.get("extra").getAsJsonArray();
                                        JsonArray newArray = new JsonArray();
                                        StringBuilder sb = new StringBuilder();
                                        for (JsonElement element : jsonArray) {
                                            JsonObject object = element.getAsJsonObject();
                                            sb.append(object.get("text").getAsString());
                                        }
                                        String finalString = sb.toString().trim();
                                        Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
                                            if (finalString.contains(onlinePlayer.getDisplayName())) return;
                                        });
                                        for (JsonElement element : jsonArray) {
                                            JsonObject newObject = new JsonObject();
                                            String initialMsg;
                                            if (!element.isJsonObject()) {
                                                initialMsg = element.getAsString();
                                            } else {
                                                newObject = element.getAsJsonObject();
                                                initialMsg = newObject.get("text").getAsString();
                                            }
                                            if ((initialMsg != null) && (!initialMsg.equals(""))) {
                                                String msg = initialMsg.startsWith(" ") ? initialMsg.substring(1) : initialMsg;
                                                String tMsg = getTranslationHandler().translate(initialMsg, Language.unknown, playerData.getLanguage(), getApiKey());

                                                newObject.addProperty("text", tMsg + " ");
                                                if (newObject.get("hoverEvent") == null) {
                                                    if (!msg.equals(tMsg.startsWith(" ") ? tMsg.substring(1) : tMsg)) {
                                                        JsonObject hoverInfo = new JsonObject();
                                                        hoverInfo.addProperty("action", "show_text");

                                                        JsonObject hoverText = new JsonObject();
                                                        hoverText.addProperty("text", msg);
                                                        hoverInfo.add("value", hoverText);

                                                        newObject.add("hoverEvent", hoverInfo);
                                                    }
                                                }
                                            }
                                            newArray.add(newObject);
                                        }
                                        packetJson.remove("extra");
                                        packetJson.add("extra", newArray);
                                    }
                                    packet.getChatComponents().write(0, WrappedChatComponent.fromJson(packetJson.toString()));
                                }
                            } catch (Exception exc) {

                            }
                        }
                    }
            );
        } else {
            Bukkit.getLogger().warning("ProtocolLib API is needed for this plugin to work. Please download it on Spigot!");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    public void onDisable() {
        getDataHandler().save();
    }

    public PlayerData getPlayerData(UUID uuid) {
        return playerDataHashMap.computeIfAbsent(uuid, f -> new PlayerData(uuid));
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public DataHandler getDataHandler() {
        return dataHandler;
    }

    public TranslationHandler getTranslationHandler() {
        return translationHandler;
    }

    public String getApiKey() {
        return apiKey;
    }

    public boolean getEnabled(Player player) {
        if (getDataHandler().getData().get(player.getUniqueId().toString() + ".enabled") == null) {
            getDataHandler().getData().set(player.getUniqueId().toString() + ".enabled", true);
            getDataHandler().save();
        }
        return getDataHandler().getData().getBoolean(player.getUniqueId().toString() + ".enabled");
    }

    public UpdateChecker getUpdateChecker() {
        return updateChecker;
    }

    public boolean isUpdateCheck() {
        return updateCheck;
    }
}
