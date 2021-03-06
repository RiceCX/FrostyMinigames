package dev.ricecx.frostygamerzone.thebridge.team;

import dev.ricecx.frostygamerzone.common.LoggingUtils;
import dev.ricecx.frostygamerzone.minigameapi.Minigame;
import dev.ricecx.frostygamerzone.minigameapi.MinigamesAPI;
import dev.ricecx.frostygamerzone.minigameapi.map.MapMetaConsumer;
import dev.ricecx.frostygamerzone.minigameapi.team.Team;
import dev.ricecx.frostygamerzone.minigameapi.team.TeamColor;
import dev.ricecx.frostygamerzone.thebridge.TheBridgeGame;
import dev.ricecx.frostygamerzone.thebridge.map.BridgeMapManager;
import dev.ricecx.frostygamerzone.thebridge.map.BridgeMapMeta;
import dev.ricecx.frostygamerzone.thebridge.users.BridgeUser;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;

@Getter
@Setter
public class BridgeTeam extends Team<BridgeUser> implements MapMetaConsumer<BridgeMapMeta> {

    private Location spawnLocation;
    private Location nexusLocation;

    private int nexusHealth = 100;

    public BridgeTeam(int size, TeamColor color) {
        super(size);
        setTeamColor(color);
    }


    /**
     * If the nexus is destroyed or not.
     */
    public boolean isDead() {
        return nexusHealth <= 0;
    }

    /**
     * Decreases the nexus' health
     * @param amount amount to decrease by
     */
    public void decreaseNexus(int amount) {
        if(isDead()) return;

        this.nexusHealth -= amount;
    }

    public void breakNexus(BridgeUser breaker) {
        if(breaker.getGameObject(TheBridgeGame.class).isGrace()) {
            breaker.getPlayer().sendMessage("&cYou cannot damage the nexus while in the &6grace period&c!");
            return;
        }
        decreaseNexus(1);

        Block nexusBlock = breaker.getGameObject().getLocation(nexusLocation).getBlock();

        if(isDead()) {
            // This nexus was broken. Send the message that the nexus was destroyed.
            MinigamesAPI.broadcastGame(breaker.getGame(), String.format("&c%s&7 has destroyed the %s&7's nexus!", breaker.getName(), getDisplayName()));

            breaker.getGameObject().executePlayer((p) -> p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 0));

            breaker.getGameObject().endGame();
            nexusBlock.setType(Material.BEDROCK);
        } else {
            // Break the nexus normally
            MinigamesAPI.broadcastGame(breaker.getGame(), String.format("%s &7has damaged the %s&7 nexus!", breaker.getName(), getDisplayName()));
            breaker.getGameObject().executePlayer((p) -> p.playSound(nexusBlock.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1, 0.5F));
        }
    }

    @Override
    public void applyConfigMapper(BridgeMapMeta mapMeta) {
        // we use team colors to map :P
        LoggingUtils.info("Applying config mapper to " + getTeamID());
        setNexusLocation(getTeamMappedMethod(this, "nexus", mapMeta, Location.class));
        setSpawnLocation(getTeamMappedMethod(this, "spawn", mapMeta, Location.class));

    }

    @Override
    public Location getSpawn() {
        return spawnLocation;
    }

}
