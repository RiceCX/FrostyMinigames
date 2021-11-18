package dev.ricecx.frostygamerzone.minigameapi;

import com.google.common.base.Preconditions;
import com.grinderwolf.swm.api.SlimePlugin;
import dev.ricecx.frostygamerzone.bukkitapi.CorePlugin;
import dev.ricecx.frostygamerzone.bukkitapi.Utils;
import dev.ricecx.frostygamerzone.bukkitapi.modules.scoreboard.ScoreboardModule;
import dev.ricecx.frostygamerzone.common.LoggingUtils;
import dev.ricecx.frostygamerzone.minigameapi.commands.GameManagerCommand;
import dev.ricecx.frostygamerzone.minigameapi.countdown.CountdownManager;
import dev.ricecx.frostygamerzone.minigameapi.game.Game;
import dev.ricecx.frostygamerzone.minigameapi.game.GameManager;
import dev.ricecx.frostygamerzone.minigameapi.game.GameManagerListener;
import dev.ricecx.frostygamerzone.minigameapi.gamestate.GameState;
import dev.ricecx.frostygamerzone.minigameapi.inventory.InventoryClicker;
import dev.ricecx.frostygamerzone.minigameapi.listeners.MinigamesListener;
import dev.ricecx.frostygamerzone.minigameapi.mapvoting.MapVoter;
import dev.ricecx.frostygamerzone.minigameapi.modules.chat.ChatAPI;
import dev.ricecx.frostygamerzone.minigameapi.modules.chat.ChatModule;
import dev.ricecx.frostygamerzone.minigameapi.slime.WorldManager;
import dev.ricecx.frostygamerzone.minigameapi.team.TeamManager;
import dev.ricecx.frostygamerzone.minigameapi.users.UserManager;
import dev.ricecx.frostygamerzone.minigameapi.utils.OffloadTask;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


@Data
public class MinigamesAPI {


    @Getter
    private static SlimePlugin slimePlugin;

    @Getter
    private static GameManager gameManager = new GameManager();

    @Getter
    @Setter
    private static CountdownManager countdownManager;
    @Getter
    @Setter
    private static MapVoter mapVoterManager;
    @Getter
    @Setter
    private static WorldManager worldManager = new WorldManager();
    @Getter
    @Setter
    private static ChatAPI chatAPI;
    @Getter
    private static UserManager userManager = new UserManager();
    @Getter
    private static ScoreboardModule scoreboardModule;
    private static PlayerVisibilityTask playerVisibilityTask;
    private static InventoryClicker inventoryClicker;


    public static void loadAPI() {
        long startLoad = System.currentTimeMillis();

        Plugin plugin = Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
        Preconditions.checkNotNull(plugin, "SLIME WORLD MANAGER IS NOT LOADED. PROCEED WITH CAUTION.");
        slimePlugin = (SlimePlugin) plugin;
        countdownManager = new CountdownManager();
        playerVisibilityTask = new PlayerVisibilityTask();
        inventoryClicker = InventoryClicker.getInstance();

        playerVisibilityTask.runTaskTimer(MinigamesAPI.getMinigamesPlugin(), 1, (20 * 5));

        countdownManager.startCountdowns();

        GameState.setState(GameState.WAITING);
        scoreboardModule = new ScoreboardModule();
        OffloadTask.offloadAsync(() -> getWorldManager().loadMap("final-lobby").whenComplete((sw, i) -> getWorldManager().setFollowingMapProperties(sw.getName())));
        new ChatModule();

        getMinigamesPlugin().registerListeners(new MinigamesListener(), new GameManagerListener());
        getMinigamesPlugin().registerCommands(new GameManagerCommand());
        LoggingUtils.info("MinigamesAPI loaded in " + (System.currentTimeMillis() - startLoad) + "ms!");
    }


    public static void broadcastGame(Game<?, ?> game, String ...message) {
        broadcastGame(game.getIdentifier(), message);
    }

    public static void broadcastGame(String game, String ...message) {
        for (Player player : getGameManager().getAllPlayersInGame(game)) {
            player.sendMessage(Utils.color(message));
        }
    }

    public static void broadcast(String ...messages) {
        for (Player player : CorePlugin.getAllPlayers()) {
            player.sendMessage(Utils.color(messages));
        }
    }

    public static MinigamesPlugin getMinigamesPlugin() {
        return MinigamesPlugin.getPlugin(MinigamesPlugin.class);
    }
}
