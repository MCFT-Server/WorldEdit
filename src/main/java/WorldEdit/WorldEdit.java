package WorldEdit;

import WorldEdit.Utils.Utils;
import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemAxeWood;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;

import java.util.HashMap;

public class WorldEdit extends PluginBase implements Listener {
    private HashMap<String, PlayerData> data = new HashMap<>();
    private CommandManager commandManager = new CommandManager();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(TextFormat.RED + "Use this command in-game only!");
            return false;
        }

        Player p = (Player) sender;

        if (!p.hasPermission("worldedit.command")) {
            p.sendMessage(cmd.getPermissionMessage());
            return false;
        }

        if (cmd.getName().toLowerCase().equals("/")) {
            PlayerData data = getPlayerData(p);
            Selection sel = data.getSelection();

            switch (label.toLowerCase()) {
                case "/":
                case "/help":
                    if (args.length == 0) {
                        p.sendMessage(getHelp(0));
                    } else if (args.length == 1) {
                        p.sendMessage(getHelp(Integer.valueOf(args[0])));
                    } else {
                        p.sendMessage(TextFormat.GRAY + "use: " + TextFormat.YELLOW + "//help [page]");
                        return false;
                    }
                    return true;
                case "/wand":
                    p.getInventory().setItemInHand(new ItemAxeWood());
                    p.getInventory().sendHeldItem(p);

                    p.sendMessage(TextFormat.YELLOW + "Tap block: " + TextFormat.GRAY + "select pos " + TextFormat.GREEN + "#1" + TextFormat.GRAY + "; " + TextFormat.YELLOW + "Destroy block: " + TextFormat.GRAY + "select pos " + TextFormat.GREEN + "#2");
                    return true;
                case "/sel":
                case "/;":
                case "/desel":
                case "/deselect":
                    data.getSelection().pos1 = null;
                    data.getSelection().pos2 = null;

                    p.sendMessage(TextFormat.GREEN + "Selection cleared.");
                    return true;
                case "/pos1":
                case "/1":
                    data.getSelection().pos1 = p.getPosition().clone();
                    p.sendMessage(TextFormat.GREEN + "First Position set to (" + TextFormat.BLUE + p.getFloorX() + ".0" + TextFormat.GREEN + ", " + TextFormat.BLUE + p.getFloorY() + ".0" + TextFormat.GREEN + ", " + TextFormat.BLUE + p.getFloorZ() + ".0" + TextFormat.GREEN + ").");
                    //p.sendMessage(TextFormat.GREEN+"Selected the first position at "+TextFormat.BLUE+p.getFloorX()+TextFormat.GREEN+", "+TextFormat.BLUE+p.getFloorY()+TextFormat.GREEN+", "+TextFormat.BLUE+p.getFloorZ()+TextFormat.GREEN);
                    return true;
                case "/pos2":
                case "/2":
                    data.getSelection().pos1 = p.getPosition().clone();
                    p.sendMessage(TextFormat.GREEN + "Second Position set to (" + TextFormat.BLUE + p.getFloorX() + ".0" + TextFormat.GREEN + ", " + TextFormat.BLUE + p.getFloorY() + ".0" + TextFormat.GREEN + ", " + TextFormat.BLUE + p.getFloorZ() + ".0" + TextFormat.GREEN + ").");
                    return true;
                case "/undo":
                    return true;
                case "/redo":
                    return true;
                case "/paste":
                    BlocksCopy copy = data.copiedBlocks;

                    if (copy == null) {
                        p.sendMessage(TextFormat.RED + "Use //copy first!");
                        return false;
                    }

                    p.sendMessage(TextFormat.BLUE + commandManager.paste(p, copy) + TextFormat.GREEN + " block(s) have been pasted.");
                    data.copiedBlocks = null;
                    return true;
            }

            Block b;

            switch (label.toLowerCase()) {
                case "/cyl":
                    if (args.length != 3) {
                        p.sendMessage(TextFormat.YELLOW + "Use //cyl <block> <radius> <height>");
                        return false;
                    }

                    b = Utils.fromString(args[0]);
                    if (b == null) {
                        sender.sendMessage(TextFormat.RED + "Block '" + args[0] + "' doesn't exist");
                        return false;
                    }

                    p.sendMessage(TextFormat.BLUE + commandManager.cyl(p.getPosition(), Integer.valueOf(args[1]), Integer.valueOf(args[2]), b) + TextFormat.GREEN + " block(s) have been changed.");
                    return true;
                case "/hcyl":
                    if (args.length != 3) {
                        p.sendMessage(TextFormat.YELLOW + "Use //hcyl <block> <radius> <height>");
                        return false;
                    }

                    b = Utils.fromString(args[0]);
                    if (b == null) {
                        sender.sendMessage(TextFormat.RED + "Block '" + args[0] + "' doesn't exist");
                        return false;
                    }

                    p.sendMessage(TextFormat.BLUE + commandManager.hcyl(p.getPosition(), Integer.valueOf(args[1]), Integer.valueOf(args[2]), b) + TextFormat.GREEN + " block(s) have been changed.");
                    return true;
                case "/sphere":
                    if (args.length != 2) {
                        p.sendMessage(TextFormat.YELLOW + "Use //sphere <block> <radius>");
                        return false;
                    }

                    b = Utils.fromString(args[0]);
                    if (b == null) {
                        sender.sendMessage(TextFormat.RED + "Block '" + args[0] + "' doesn't exist");
                        return false;
                    }

                    p.sendMessage(TextFormat.BLUE + commandManager.sphere(p.getPosition(), Integer.valueOf(args[1]), b) + TextFormat.GREEN + " block(s) have been changed.");
                    return true;
                case "/hsphere":
                    if (args.length != 2) {
                        p.sendMessage(TextFormat.YELLOW + "Use //hsphere <block> <radius>");
                        return false;
                    }

                    b = Utils.fromString(args[0]);
                    if (b == null) {
                        sender.sendMessage(TextFormat.RED + "Block '" + args[0] + "' doesn't exist");
                        return false;
                    }

                    p.sendMessage(TextFormat.BLUE + commandManager.hsphere(p.getPosition(), Integer.valueOf(args[1]), b) + TextFormat.GREEN + " block(s) have been changed.");
                    return true;
            }

            if (!isPosSet(p)) {
                p.sendMessage(TextFormat.RED + "You have to select both positions first!");
                return false;
            }

            if (data.getSelection().pos1.level.getId() != data.getSelection().pos2.level.getId()) {
                p.sendMessage(TextFormat.RED + "Both positions must be in the same level!");
                return false;
            }

            switch (label.toLowerCase()) {
                case "/cut":
                    p.sendMessage(TextFormat.BLUE + commandManager.set(sel.pos1, sel.pos2, new BlockAir()) + TextFormat.GREEN + " block(s) have been changed.");
                    return true;
                case "/copy":
                    data.copiedBlocks = new BlocksCopy();

                    p.sendMessage(TextFormat.BLUE + commandManager.copy(sel.pos1, sel.pos2, p, data.copiedBlocks) + TextFormat.GREEN + " block(s) have been copied.");
                    return true;
            }

            switch (label.toLowerCase()) {
                case "/set":
                    if (args.length != 1) {
                        sender.sendMessage(TextFormat.YELLOW + "Use //set <block>");
                        return false;
                    }

                    b = Utils.fromString(args[0]);

                    if (b == null) {
                        sender.sendMessage(TextFormat.RED + "Block '" + args[0] + "' doesn't exist");
                        return false;
                    }
                    p.sendMessage(TextFormat.BLUE + commandManager.set(sel.pos1, sel.pos2, b) + TextFormat.GREEN + " block(s) have been changed.");
                    return true;
                case "/walls":
                case "/wall":
                    if (args.length != 1) {
                        sender.sendMessage(TextFormat.YELLOW + "Use //walls <block>");
                        return false;
                    }

                    b = Utils.fromString(args[0]);

                    if (b == null) {
                        sender.sendMessage(TextFormat.RED + "Block '" + args[0] + "' doesn't exist");
                        return false;
                    }
                    p.sendMessage(TextFormat.BLUE + commandManager.walls(sel.pos1, sel.pos2, b) + TextFormat.GREEN + " block(s) have been changed.");
                    return true;
                case "/replace":
                    if (args.length != 2) {
                        sender.sendMessage(TextFormat.YELLOW + "Use //replace <block> <replace>");
                        return false;
                    }

                    b = Utils.fromString(args[0]);
                    Block b2 = Utils.fromString(args[1]);

                    if (b == null) {
                        sender.sendMessage(TextFormat.RED + "Block '" + args[0] + "' doesn't exist");
                        return false;
                    }

                    if (b2 == null) {
                        sender.sendMessage(TextFormat.RED + "Block '" + args[1] + "' doesn't exist");
                        return false;
                    }

                    p.sendMessage(TextFormat.BLUE + commandManager.replace(sel.pos1, sel.pos2, b, b2) + TextFormat.GREEN + " block(s) have been replaced.");
                    return true;
            }
        }

        return true;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        Block b = e.getBlock();

        if (this.isSelector(p) && p.getInventory().getItemInHand().getId() == Item.WOODEN_AXE) {
            PlayerData data = getPlayerData(p);

            data.getSelection().pos2 = b.getLocation().clone();
            p.sendMessage(TextFormat.GREEN + "Selected the second position at " + TextFormat.BLUE + b.x + TextFormat.GREEN + ", " + TextFormat.BLUE + b.y + TextFormat.GREEN + ", " + TextFormat.BLUE + b.z + TextFormat.GREEN);
            e.setCancelled();
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Block b = e.getBlock();

        if (isSelector(p) && p.getInventory().getItemInHand().getId() == Item.WOODEN_AXE) {
            PlayerData data = getPlayerData(p);

            switch (e.getAction()) {
                case PlayerInteractEvent.LEFT_CLICK_BLOCK:
                    data.getSelection().pos2 = b.getLocation().clone();
                    p.sendMessage(TextFormat.GREEN + "Selected the second position at " + TextFormat.BLUE + b.x + TextFormat.GREEN + ", " + TextFormat.BLUE + b.y + TextFormat.GREEN + ", " + TextFormat.BLUE + b.z + TextFormat.GREEN);
                    break;
                case PlayerInteractEvent.RIGHT_CLICK_BLOCK:
                    data.getSelection().pos1 = b.getLocation().clone();
                    p.sendMessage(TextFormat.GREEN + "Selected the first position at " + TextFormat.BLUE + b.x + TextFormat.GREEN + ", " + TextFormat.BLUE + b.y + TextFormat.GREEN + ", " + TextFormat.BLUE + b.z + TextFormat.GREEN);
                    break;
            }
        }
    }

    public boolean isPosSet(Player p) {
        PlayerData data = getPlayerData(p);

        return data.getSelection().pos1 != null && data.getSelection().pos2 != null;
    }

    private boolean isSelector(Player p) {
        return p.hasPermission("worldedit.command");
    }

    public PlayerData getPlayerData(Player p) {
        PlayerData data = this.data.get(p.getName().toLowerCase());

        if (data == null) {
            data = new PlayerData(p);
            this.data.put(p.getName().toLowerCase(), data);
        }

        return data;
    }

    private String getHelp(int page) {
        String msg = "";

        switch (page) {
            case 0:
            case 1:
                msg += TextFormat.GRAY + "Showing help page " + TextFormat.GREEN + "1/3:";
                msg += "\n" + TextFormat.YELLOW + "   //pos1";
                msg += "\n" + TextFormat.YELLOW + "   //pos2";
                msg += "\n" + TextFormat.YELLOW + "   //set <block>";
                msg += "\n" + TextFormat.YELLOW + "   //walls <block>";
                msg += "\n" + TextFormat.YELLOW + "   //replace <block> <replace>";
                break;
            case 2:
                msg += TextFormat.GRAY + "Showing help page " + TextFormat.GREEN + "2/3:";
                msg += "\n" + TextFormat.YELLOW + "   //cyl <block> <radius>";
                msg += "\n" + TextFormat.YELLOW + "   //hcyl <block> <radius>";
                msg += "\n" + TextFormat.YELLOW + "   //sphere <block> <radius>";
                msg += "\n" + TextFormat.YELLOW + "   //hsphere <block> <radius>";
                msg += "\n" + TextFormat.YELLOW + "   //cut";
                break;
            case 3:
                msg += TextFormat.GRAY + "Showing help page " + TextFormat.GREEN + "3/3:";
                msg += "\n" + TextFormat.YELLOW + "   //copy";
                msg += "\n" + TextFormat.YELLOW + "   //paste";
                msg += "\n" + TextFormat.YELLOW + "   //wand";
                break;
        }

        return msg;
    }
}
