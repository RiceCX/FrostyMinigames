package dev.ricecx.frostygamerzone.thebridge.handler;

import dev.ricecx.frostygamerzone.common.LoggingUtils;
import dev.ricecx.frostygamerzone.minigameapi.MinigamesAPI;
import dev.ricecx.frostygamerzone.minigameapi.utils.OffloadTask;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class OreProtection implements Listener {

    @Setter @Getter private static boolean active = true;

    private static final Set<Material> ores = new HashSet<>();

    private static final Set<Material> pickaxe = Tag.MINEABLE_PICKAXE.getValues();
    private static final Set<Material> axe = Tag.MINEABLE_AXE.getValues();
    private static final Set<Material> shovel = Tag.MINEABLE_SHOVEL.getValues();

    private static final Set<Material> airMaterials = new HashSet<>();

    static {
        ores.addAll(Tag.COAL_ORES.getValues());
        ores.addAll(Tag.GOLD_ORES.getValues());
        ores.addAll(Tag.REDSTONE_ORES.getValues());
        ores.addAll(Tag.EMERALD_ORES.getValues());
        ores.addAll(Tag.DIAMOND_ORES.getValues());
        ores.addAll(Tag.LAPIS_ORES.getValues());

        airMaterials.add(Material.MELON);
        airMaterials.addAll(Tag.LOGS.getValues());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onBlockBreak(BlockBreakEvent evt) {
        if(!isActive() || !MinigamesAPI.isIngame(evt.getPlayer())) return;
        Block brokenBlock = evt.getBlock();
        Material blockMaterial = brokenBlock.getBlockData().getMaterial();
        Player player = evt.getPlayer();

        HandToolType toolType = getHandToolType(player.getInventory().getItemInMainHand().getType());

        if(toolType.canMine(blockMaterial)) {
            // add statistics here.

            if(toolType.canRegen(blockMaterial)) {
                RegenKey key = toolType.regen(blockMaterial);
                if(key == null) { evt.setCancelled(true); return; }

                regenerateBlock(brokenBlock, key.regenTime(), key.replacementMaterial());
                player.getInventory().addItem(brokenBlock.getDrops().toArray(ItemStack[]::new));
                evt.setDropItems(false);
            }

            evt.setExpToDrop(0);
        } else {
            player.sendMessage("(!) Try breaking this block with the right tool!");
            evt.setCancelled(true);
        }

    }


    public HandToolType getHandToolType(Material material) {
        if(material.name().contains("_PICKAXE")) return HandToolType.PICKAXE;
        if(material.name().contains("_SPADE") || material.name().contains("_SHOVEL")) return HandToolType.SHOVEL;
        if(material.name().contains("_AXE")) return HandToolType.AXE;

        return HandToolType.HAND;
    }

    /**
     * Regenerates a block after an amount of time
     * @param block Block to regenerate
     * @param time time to regenerate in seconds
     * @param material Material to replace the block while it's regenerating
     */
    public void regenerateBlock(Block block, int time, Material material) {
        Material previousMat = block.getType();
        OffloadTask.offloadDelayedSync(() -> block.setType(material), 1);

        OffloadTask.offloadDelayedSync(() -> block.setType(previousMat), time * 20);
    }

    public enum HandToolType {
        PICKAXE(pickaxe),
        HAND(null),
        AXE(axe),
        SHOVEL(shovel);

        @Getter private final Set<Material> acceptedMaterials;
        HandToolType(Set<Material> acceptedMaterials) {
            this.acceptedMaterials = acceptedMaterials;
        }

        /**
         * If this material is mineable with this tool type
         * @param material Material to mine
         * @return If it's mineable or not
         */
        public boolean canMine(Material material) {
            if(this == HAND) return true;
            return this.acceptedMaterials.contains(material);
        }


        public boolean canRegen(Material material) {
            return regen(material) != null;
        }

        public RegenKey regen(Material material) {

            // TODO: add wood regeneration
            if(airMaterials.contains(material)) return new RegenKey(material, Material.AIR, 10);
            if(ores.contains(material)) return new RegenKey(material, Material.BEDROCK, 20);

            return null;
        }
    }

    public record RegenKey(Material baseMaterial, Material replacementMaterial, int regenTime) { }
}
