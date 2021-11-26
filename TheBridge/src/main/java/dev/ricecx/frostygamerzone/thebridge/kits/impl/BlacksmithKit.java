package dev.ricecx.frostygamerzone.thebridge.kits.impl;

import dev.ricecx.frostygamerzone.api.game.thebridge.TheBridgeKits;
import dev.ricecx.frostygamerzone.minigameapi.kits.KitUser;
import dev.ricecx.frostygamerzone.thebridge.kits.TheBridgeKit;
import dev.ricecx.frostygamerzone.thebridge.users.BridgeUser;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BlacksmithKit extends TheBridgeKit {
    @Getter
    TheBridgeKits kit = TheBridgeKits.BLACKSMITH;

    @Override
    public ItemStack[] setItems(KitUser<BridgeUser, TheBridgeKits> player) {
        return new ItemStack[] {
                new ItemStack(Material.STONE_SWORD),
                new ItemStack(Material.WOODEN_PICKAXE),
                new ItemStack(Material.WOODEN_AXE),
                new ItemStack(Material.CRAFTING_TABLE)
        };
    }
}
