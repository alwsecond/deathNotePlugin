package abvgd;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public final class DeathNote extends JavaPlugin implements Listener {
    HashMap<Player, String> deathScheduler = new HashMap<>();

    @Override
    public void onEnable() {
        Objects.requireNonNull(getServer().getPluginCommand("DeathNote")).setExecutor(this);
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args)
    {
        Player player = (Player) sender;
        if (player.isOp() && command.getName().equals("DeathNote"))
        {
            if (args.length == 0)
            {
                List<String> loreList = new ArrayList<>();
                ItemStack DeathNoteItem = new ItemStack(Material.WRITABLE_BOOK, 1);
                ItemMeta DeathNoteItemMeta = DeathNoteItem.getItemMeta();

                DeathNoteItemMeta.setDisplayName("§dТетрадь смерти");

                loreList.add("§2Человек, имя которого будет записано в этой тетради, умрет.");
                loreList.add("§2Если вы не знаете, как выглядит человек, чье имя вы пишете, то ничего не произойдет.");
                loreList.add("§2Поэтому все люди с одинаковыми именами не умрут из-за одной записи.");
                loreList.add("§2Если причина смерти будет написана не позднее,");
                loreList.add("§2чем за 40 секунд после написания имени, то так оно и случится.");
                loreList.add("§2Если причина смерти не будет указана, человек умрет от сердечного приступа.");

                DeathNoteItemMeta.setLore(loreList);

                DeathNoteItem.setItemMeta(DeathNoteItemMeta);

                player.getInventory().addItem(DeathNoteItem);
                return true;
            }
            if (args.length == 1)
            {
                Player target = Bukkit.getPlayer(args[0]);
                if (target.isOnline())
                {
                    player.sendMessage("§2" + target.getName() + "§e will die after 40s");
                    deathScheduler.put(target, "умер от сердечного приступа");
                    Bukkit.getScheduler().runTaskLater(this, () -> {
                        target.damage(Math.sqrt(1000000));
                    }, 800);
                    return true;
                }
                else {
                    player.sendMessage("§cCant find player");
                }
            }
            if (args.length > 1)
            {
                if (args[0].equals("instant"))
                {
                    Player target = Bukkit.getPlayer(args[1]);
                    String deathCause = "";
                    for (int i = 2; i < args.length; i++)
                    {
                        deathCause += args[i] + ' ';
                    }

                    deathScheduler.put(target, deathCause);
                    target.damage(1000000);
                    return true;
                }
                if (args[0].equals("time"))
                {
                    Player target = Bukkit.getPlayer(args[1]);
                    int time_s = Integer.parseInt(args[2]);
                    player.sendMessage("§2" + target.getName() + "§e will die after " + time_s + "s");
                    String deathCause = "";
                    for (int i = 3; i < args.length; i++)
                    {
                        deathCause += args[i] + ' ';
                    }
                    deathScheduler.put(target, deathCause);
                    Bukkit.getScheduler().runTaskLater(this, () -> {
                        target.damage(1000000);
                    }, 20L * time_s);
                    return true;
                }
                if (args[0].equals("set"))
                {
                    Player target = Bukkit.getPlayer(args[1]);
                    String deathCause = "";
                    for (int i = 2; i < args.length; i++)
                    {
                        deathCause += args[i] + ' ';
                    }
                    player.sendMessage("§2" + target.getName() + "§e death cause set!");
                    deathScheduler.put(target, deathCause);
                    return true;
                }
            }
        }
        return false;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e)
    {
        Player player = e.getPlayer();
        if (deathScheduler.containsKey(player))
        {
            e.setDeathMessage("§2" + player.getName() + "§r " + deathScheduler.get(player));
            deathScheduler.remove(player);
        }
    }
}
