package dev.ricecx.frostygamerzone.thebridge;


import dev.ricecx.frostygamerzone.bukkitapi.user.utils.UserRegister;
import dev.ricecx.frostygamerzone.minigameapi.Minigame;
import dev.ricecx.frostygamerzone.minigameapi.MinigamesAPI;
import dev.ricecx.frostygamerzone.minigameapi.MinigamesPlugin;
import dev.ricecx.frostygamerzone.thebridge.lobby.TheBridgeLobby;
import dev.ricecx.frostygamerzone.thebridge.map.BridgeMapManager;
import dev.ricecx.frostygamerzone.thebridge.users.BridgeUser;
import dev.ricecx.frostygamerzone.thebridge.users.BridgeUserImpl;

import java.util.UUID;

public final class TheBridgePlugin extends MinigamesPlugin implements UserRegister<BridgeUser> {

    @Override
    public void onEnable() {
        // Plugin startup logic
        super.onEnable();

        MinigamesAPI.setMinigame(Minigame.THE_BRIDGE);
        MinigamesAPI.getGameManager().createGame(new TheBridgeGame());
//        MinigamesAPI.getGameManager().createGame(new TheBridgeGame());
//        MinigamesAPI.getGameManager().createGame(new TheBridgeGame());
//        MinigamesAPI.getGameManager().createGame(new TheBridgeGame());
//       MinigamesAPI.getGameManager().createGame(new TheBridgeGame());

        MinigamesAPI.getGameManager().setLobby(new TheBridgeLobby());
        MinigamesAPI.setMapManager(new BridgeMapManager());

        MinigamesAPI.loadAPI();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public BridgeUser registerUser(String s, UUID uuid) {
        return new BridgeUserImpl(s, uuid);
    }
}
