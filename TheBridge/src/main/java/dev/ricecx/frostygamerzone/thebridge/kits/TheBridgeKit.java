package dev.ricecx.frostygamerzone.thebridge.kits;

import dev.ricecx.frostygamerzone.api.game.thebridge.TheBridgeKits;
import dev.ricecx.frostygamerzone.bukkitapi.ItemBuilder;
import dev.ricecx.frostygamerzone.common.LoggingUtils;
import dev.ricecx.frostygamerzone.minigameapi.kits.Kit;
import dev.ricecx.frostygamerzone.minigameapi.team.Team;
import dev.ricecx.frostygamerzone.minigameapi.team.TeamColor;
import dev.ricecx.frostygamerzone.minigameapi.users.GameUser;
import dev.ricecx.frostygamerzone.thebridge.users.BridgeUser;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class TheBridgeKit implements Kit<TheBridgeKits, BridgeUser> {

    @Override
    public void giveKit(BridgeUser player) {
        Team<GameUser> team = player.getGameObject().getTeamManager().getPlayerTeam(player);
        TeamColor color = team.getTeamColor();
        player.setHelmet(new ItemBuilder(Material.LEATHER_HELMET).setColor(color.getColor()).setName(team.getDisplayName() + " Helmet").toItemStack());
        player.setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE).setColor(color.getColor()).setName(team.getDisplayName() + " Chestplate").toItemStack());
        player.setLeggings(new ItemBuilder(Material.LEATHER_LEGGINGS).setColor(color.getColor()).setName(team.getDisplayName() + " Leggings").toItemStack());
        player.setBoots(new ItemBuilder(Material.LEATHER_BOOTS).setColor(color.getColor()).setName(team.getDisplayName() + " Boots").toItemStack());

        List<ItemStack> items = setItems(player);

        for (int i = 0; i < items.size(); i++) {
            ItemStack clonedItem = items.get(i);

            if (clonedItem.getItemMeta() != null && clonedItem.getItemMeta().getLore() != null) {
                for (String loreLine : clonedItem.getItemMeta().getLore()) {
                    LoggingUtils.debug("Lore Line: " + loreLine);
                }
            }
            player.getPlayer().getInventory().setItem(i, clonedItem);
        }
    }
}
