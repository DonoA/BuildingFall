/*
 * This file is part of BuildingFall
 * 
 * FallingBuildings is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * FallingBuildings is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with FallingBuildings.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 */
package net.pocketpixels.buildingfall;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


/**
 *
 * @author donoa_000
 */
public class BuildingFall extends JavaPlugin implements CommandExecutor, Listener{
    
    @Getter
    private static final String prefix = ChatColor.YELLOW + "[" + ChatColor.AQUA + "BuildingFall" + ChatColor.YELLOW + "]" + ChatColor.GREEN;
    
    public static final Material selectionTool = Material.WOOD_AXE;
    
    @Getter
    private static BuildingFall instance;
    
    private static HashMap<Player, Location[]> ObjForEdit = new HashMap<Player, Location[]>();
    
    @Override
    public void onEnable(){
        for(String s : new String[] {"create", "start", "stop", "reset", "delete"}){
            getCommand(s+"fall").setExecutor(this);
        }
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(this, this);
        instance = this;
    }
    
    @Override
    public void onDisable(){
        
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args){
        String baseCommand = cmd.getName().toLowerCase().replace("fall", "");
        switch(baseCommand){
            case "create":
                if(sender instanceof Player && sender.hasPermission("buildingfall.create") && args.length > 0){
                    Player p = (Player) sender;
                    p.sendMessage(prefix + "Sending selection tool");
                    ItemStack select = new ItemStack(selectionTool);
                    ItemMeta selectMeta = select.getItemMeta();
                    selectMeta.setDisplayName(ChatColor.GREEN + "Object Selector");
                    selectMeta.setLore(Arrays.asList(new String[] {"Right click for point 1", "Left click for point 2", "If not looking at block will select block at feet", "Drop this item when finished", "axe for editing " + args[0]}));
                    selectMeta.addEnchant(Enchantment.DURABILITY, 1, true);
                    select.setItemMeta(selectMeta);
                    p.getInventory().setItem(0, select);
                    ObjForEdit.put(p, new Location[2]);
                }
                return true;
            case "start":
                if(sender.hasPermission("buildingfall.start") && args.length > 0){
                    if(FallObject.getFallObjects().containsKey(args[0])){
                        FallObject.getFallObjects().get(args[0]).Start();
                        sender.sendMessage(prefix + "Starting fall for " + args[0]);
                    }else{
                        sender.sendMessage(prefix + "No such fall object " + args[0]);
                    }
                }
                return true;
            case "stop":
                if(sender.hasPermission("buildingfall.stop") && args.length > 0){
                    if(FallObject.getFallObjects().containsKey(args[0])){
                        FallObject fo = FallObject.getFallObjects().get(args[0]);
                        if(fo.isFalling()){
                            FallObject.getFallObjects().get(args[0]).Stop();
                            sender.sendMessage(prefix + "Stopping fall for " + args[0]);
                            if(args.length > 1 && args[1].equalsIgnoreCase("true")){
                                fo.Reset();
                                sender.sendMessage(prefix + "Resetting fall object " + args[0]);
                            }
                        }else{
                            sender.sendMessage(prefix + "Falling object " + args[0] + "is not currently falling");
                        }
                    }else{
                        sender.sendMessage(prefix + "No such fall object " + args[0]);
                    }
                }
                return true;
            case "reset":
                if(sender.hasPermission("buildingfall.reset") && args.length > 0){
                    if(FallObject.getFallObjects().containsKey(args[0])){
                        FallObject.getFallObjects().get(args[0]).Reset();
                        sender.sendMessage(prefix + "Resetting fall object " + args[0]);
                    }else{
                        sender.sendMessage(prefix + "No such fall object " + args[0]);
                    }
                }
                return true;
            case "delete":
                if(sender.hasPermission("buildingfall.delete") && args.length > 0){
                    if(FallObject.getFallObjects().containsKey(args[0])){
                        FallObject.getFallObjects().remove(args[0]);
                        sender.sendMessage(prefix + "Deleting fall object " + args[0]);
                    }else{
                        sender.sendMessage(prefix + "No such fall object " + args[0]);
                    }
                }
                return true;
        }
        return false;
    }
    
    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        if(e.hasItem()){
            if(e.getItem().hasItemMeta() && e.getItem().getType().equals(selectionTool)){
                if(e.getItem().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Object Selector")){
                    Action a = e.getAction();
                    e.setCancelled(true);
                    if(a.equals(Action.LEFT_CLICK_AIR)){
                        ObjForEdit.get(e.getPlayer())[1] = e.getPlayer().getLocation();
                        e.getPlayer().sendMessage(prefix + "Set point 2 to " + LocToString(e.getPlayer().getLocation()));
                    }else if(a.equals(Action.RIGHT_CLICK_AIR)){
                        ObjForEdit.get(e.getPlayer())[0] = e.getPlayer().getLocation();
                        e.getPlayer().sendMessage(prefix + "Set point 1 to " + LocToString(e.getPlayer().getLocation()));
                    }else if(a.equals(Action.LEFT_CLICK_BLOCK)){
                        ObjForEdit.get(e.getPlayer())[1] = e.getClickedBlock().getLocation();
                        e.getPlayer().sendMessage(prefix + "Set point 2 to " + LocToString(e.getClickedBlock().getLocation()));
                    }else if(a.equals(Action.RIGHT_CLICK_BLOCK)){
                        ObjForEdit.get(e.getPlayer())[0] = e.getClickedBlock().getLocation();
                        e.getPlayer().sendMessage(prefix + "Set point 1 to " + LocToString(e.getClickedBlock().getLocation()));
                    }
                }
            }
        }
    }
    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        if(e.getItemDrop().getItemStack().hasItemMeta() && e.getItemDrop().getItemStack().getType().equals(selectionTool)){
            if(e.getItemDrop().getItemStack().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Object Selector")){
                Location[] loc = ObjForEdit.get(e.getPlayer());
                String n = e.getItemDrop().getItemStack().getItemMeta().getLore().get(4).replace("axe for editing ", "");
                e.getPlayer().sendMessage(prefix + "Creating fall object between " + LocToString(loc[0]) + " and  " + LocToString(loc[1]) + " with name " + n);
                FallObject.getFallObjects().put(n, new FallObject(loc[0], loc[1]));
                e.getItemDrop().remove();
            }
        }
        for(Entry<String, FallObject> entry : FallObject.getFallObjects().entrySet()){
            System.out.println(entry.getKey() + ": " + LocToString(entry.getValue().getCorner1()) + ", " + LocToString(entry.getValue().getCorner2()));
        }
    }
    
    public static String LocToString(Location loc){
        return "(" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")";
    }
}
