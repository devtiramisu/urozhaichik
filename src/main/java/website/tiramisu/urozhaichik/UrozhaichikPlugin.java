package website.tiramisu.urozhaichik;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class UrozhaichikPlugin extends JavaPlugin {

    private int originalRandomTickSpeed = -1;
    private boolean active = false;

    @Override
    public void onEnable() {
        getLogger().info("urozhaichik plugin enabled!");
    }

    @Override
    public void onDisable() {
        if (active) {
            restoreRandomTickSpeed();
        }
        getLogger().info("urozhaichik plugin disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("urozhaichik")) {

            if (!sender.hasPermission("website.tiramisu.urozhaichik")) {
                sender.sendMessage("You do not have permission to use this command.");
                return true;
            }

            if (active) {
                sender.sendMessage("urozhaichik is already active!");
                return true;
            }

            activateUrozhaichik();
            return true;
        }
        return false;
    }

    private void activateUrozhaichik() {
        World world = Bukkit.getWorlds().get(0); // первый мир
        if (world == null) return;

        originalRandomTickSpeed = world.getGameRuleValue(org.bukkit.GameRule.RANDOM_TICK_SPEED);

        // Устанавливаем 300
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamerule randomTickSpeed 300");

        // Отправляем титл всем игрокам
        String title = "вам звонит урожайчик";
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendActionBar(title);
        }

        // Лог в консоли
        Bukkit.getConsoleSender().sendMessage("urozhaichik activated");

        active = true;

        // Планируем автоматическое восстановление через 15 секунд
        new BukkitRunnable() {
            @Override
            public void run() {
                restoreRandomTickSpeed();
            }
        }.runTaskLater(this, 20L * 15); // 15 секунд
    }

    private void restoreRandomTickSpeed() {
        if (!active) return;

        if (originalRandomTickSpeed < 0) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamerule randomTickSpeed 3");
        } else {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "gamerule randomTickSpeed " + originalRandomTickSpeed);
        }

        active = false;
        Bukkit.getConsoleSender().sendMessage("urozhaichik deactivated");
    }
}
