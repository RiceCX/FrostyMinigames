package dev.ricecx.frostygamerzone.thebridge;

import dev.ricecx.frostygamerzone.common.LoggingUtils;
import dev.ricecx.frostygamerzone.minigameapi.MinigamesAPI;
import dev.ricecx.frostygamerzone.minigameapi.game.AbstractGame;
import dev.ricecx.frostygamerzone.minigameapi.game.Game;
import dev.ricecx.frostygamerzone.minigameapi.map.MapMeta;

import dev.ricecx.frostygamerzone.thebridge.countdowns.GameStartCountdown;
import dev.ricecx.frostygamerzone.thebridge.lobby.BridgeMapVoter;
import dev.ricecx.frostygamerzone.thebridge.lobby.boards.BridgeGameBoard;
import dev.ricecx.frostygamerzone.thebridge.map.BridgeMapMeta;
import dev.ricecx.frostygamerzone.thebridge.team.BridgeTeam;
import dev.ricecx.frostygamerzone.thebridge.team.BridgeTeamManager;
import dev.ricecx.frostygamerzone.thebridge.users.BridgeUser;
import lombok.Getter;


@Getter
public class TheBridgeGame extends AbstractGame<BridgeUser, BridgeTeam> implements Game<BridgeTeam, BridgeUser> {

    public TheBridgeGame() {
        setTeamManager(new BridgeTeamManager(this));
        setGameCountdown(new GameStartCountdown(this));
        setMapVoter(new BridgeMapVoter(this));
        setMaxPlayers(32);
    }

    @Override
    public String getPrefix() {
        return "tb-";
    }

    @Override
    public void applyMapMeta(MapMeta meta) {
        LoggingUtils.info("Applying map data to " + getIdentifier() + " " + meta.getName());
        BridgeMapMeta bridgeMapMeta = (BridgeMapMeta) meta;
        for (BridgeTeam value : getTeamManager().getRegisteredTeams().values()) {
            value.applyConfigMapper(bridgeMapMeta);
        }
    }

    @Override
    public void onStartGame() {
        setStartTime(System.currentTimeMillis());
    }

    @Override
    public void onPlayerStartGame(BridgeUser player) {
        if(player.getTeam() == null) {
            BridgeTeam randomTeam = getTeamManager().getAvailableTeam();
            randomTeam.addPlayer(player);
            player.setTeam(randomTeam);
        }

        teleport(player.getTeam().getSpawn(), player);

        MinigamesAPI.getScoreboardModule().provideScoreboard(player.getPlayer(), new BridgeGameBoard(player));

        player.clearInventory();
        player.provideKit();
    }

    @Override
    public void endGame() {

    }
}
