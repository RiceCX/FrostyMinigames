package dev.ricecx.frostygamerzone.thebridge.lobby;

import dev.ricecx.frostygamerzone.bukkitapi.CorePlugin;
import dev.ricecx.frostygamerzone.bukkitapi.ItemBuilder;
import dev.ricecx.frostygamerzone.minigameapi.MinigamesAPI;
import dev.ricecx.frostygamerzone.minigameapi.lobby.core.AbstractLobby;
import dev.ricecx.frostygamerzone.minigameapi.users.GameUser;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class TheBridgeLobby extends AbstractLobby {

    @Override
    public void giveItems(Player player) {
        ItemStack redHelmet = new ItemBuilder(Material.LEATHER_HELMET).setName("&cTeam Red").toItemStack();
        LeatherArmorMeta meta = ((LeatherArmorMeta) redHelmet.getItemMeta());
        if(meta != null) meta.setColor(Color.RED);
        redHelmet.setItemMeta(meta);

        ItemStack blueHelmet = new ItemBuilder(Material.LEATHER_HELMET).setName("&9Team Blue").toItemStack();
        LeatherArmorMeta blueMeta = ((LeatherArmorMeta) blueHelmet.getItemMeta());
        if(blueMeta != null) blueMeta.setColor(Color.BLUE);
        blueHelmet.setItemMeta(blueMeta);

        player.getInventory().setItem(3, redHelmet);
        player.getInventory().setItem(4, new ItemBuilder(Material.MAP).setName("&a&lRandom Team").toItemStack());
        player.getInventory().setItem(5, blueHelmet);

        player.getInventory().setItem(8, new ItemBuilder(Material.PAPER).setName("&c&lLeave").toItemStack());
    }

    @Override
    public void onJoin(GameUser user) {

        for (Player player : CorePlugin.getAllPlayers()) {
            player.sendMessage("[DEBUG BROADCAST] " + user.getName() + " has joined the game with STATUS " + user.getGameUserStatus() + " and joined game " + user.getGame());
        }
        MinigamesAPI.getScoreboardModule().provideScoreboard(user.getPlayer(), new BridgeLobbyBoard(user));
        user.getPlayer().teleport(new Location(Bukkit.getWorld("final-lobby"), 30,83,-51));

        MinigamesAPI.broadcastGame(user.getGame(), "> " + user.getPlayer().getDisplayName() + " has joined the lobby");
    }

}
