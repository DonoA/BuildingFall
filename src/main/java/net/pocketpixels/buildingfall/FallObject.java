/*
 * This file is part of BuildingFall.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import lombok.Getter;
import static net.pocketpixels.buildingfall.BuildingFall.LocToString;
import net.pocketpixels.buildingfall.FallBlock.Point;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 *
 * @author donoa_000
 */
public class FallObject {
    @Getter
    private Location corner1;
    @Getter
    private Location corner2;
    
    private Location pivot;
    
    @Getter
    private ArrayList<FallBlock> blocks = new ArrayList<FallBlock>();
    
    private HashMap<Location, FallBlock> ResetStructure = new HashMap<Location, FallBlock>();
    
    @Getter
    private boolean falling;
    
    @Getter
    private int dir = -1;
    
    @Getter
    private double angle;
    
    @Getter
    private static HashMap<String, FallObject> FallObjects = new HashMap<String, FallObject>();
    
    @Getter
    private static ArrayList<FallObject> FallingObjects = new ArrayList<FallObject>();
    
    public FallObject(Location c1, Location c2){
        this.corner1 = c1;
        this.corner2 = c2;
        int MaxX = Math.max(c1.getBlockX(), c2.getBlockX());
        int MaxY = Math.max(c1.getBlockY(), c2.getBlockY());
        int MaxZ = Math.max(c1.getBlockZ(), c2.getBlockZ());
        this.pivot = new Location(c1.getWorld(), (c1.getBlockX() + c2.getBlockX())/2, Math.min(c1.getBlockY(), c2.getBlockY()), (c1.getBlockZ() + c2.getBlockZ())/2);
        for(int X = Math.min(c1.getBlockX(), c2.getBlockX()); X <= MaxX; X++){
            for(int Y = Math.min(c1.getBlockY(), c2.getBlockY()); Y <= MaxY; Y++){
                for(int Z = Math.min(c1.getBlockZ(), c2.getBlockZ()); Z <= MaxZ; Z++){
                    Location l = new Location(c1.getWorld(), X, Y, Z);
                    Point rel = new Point(X-pivot.getBlockX(), Y-pivot.getBlockY(), Z-pivot.getBlockZ());
                    double relAngleX;
                    double relAngleZ;
                    if(rel.X != 0){
                        if(rel.X < 0){
                            relAngleX = Math.atan(rel.Y/rel.X) + Math.PI;
                        }else{
                            relAngleX = Math.atan(rel.Y/rel.X);
                        }
                    }else{
                        relAngleX = Math.PI/2;
                    }
                    if(rel.Z != 0){
                        relAngleZ = Math.atan(rel.Y/rel.Z);
                    }else{
                        relAngleZ = Math.PI/2;
                    }
                    double hypX = Math.sqrt(Math.pow(rel.Y, 2) + Math.pow(rel.X, 2));
                    double hypZ = Math.sqrt(Math.pow(rel.Y, 2) + Math.pow(rel.Z, 2));
                    blocks.add(new FallBlock(l, rel, new double[] {relAngleX, relAngleZ}, new double[] {hypX, hypZ}));
                    ResetStructure.put(l, new FallBlock(l));
                    System.out.println(new FallBlock(l, rel, new double[] {relAngleX, relAngleZ}, new double[] {hypX, hypZ}).toString());
                }
            }
        }
        Bukkit.getScheduler().scheduleSyncRepeatingTask(BuildingFall.getInstance(), new Runnable(){
            @Override
            public void run() {
                for(FallObject fo : (ArrayList<FallObject>) FallingObjects.clone()){
                    
                    if(angle <= 0){
                        boolean done = true;
                        for(FallBlock fb: fo.getBlocks()){
                            Location loc = fb.getLoc().clone();
                            loc.add(0,-1, 0);
                            if(loc.getBlock().getType().equals(Material.AIR)){
                                fb.getLoc().getBlock().setType(Material.AIR);
                                loc.getBlock().setType(fb.getM());
                                fb.setLoc(loc);
                                done = false;
                            }
//                            System.out.println(fb.toString());
                        }
                        if(done){
                            fo.falling = false;
                            FallingObjects.remove(fo);
                        }
                    }else{
                        for(FallBlock fb: fo.getBlocks()){
                            System.out.println("Update obj " + (Math.PI + (angle + fb.getRelAngleX())));
                            Location newLoc = new Location(fo.getCorner1().getWorld(), 
                                    pivot.getBlockX() + fb.getHypX()*Math.cos(Math.PI/2 + (angle + fb.getRelAngleX())), 
                                    pivot.getBlockY() + Math.abs(fb.getHypX()*Math.sin(Math.PI/2 + (angle + fb.getRelAngleX()))),
                                    fb.getLoc().getBlockZ());
                            fb.getLoc().getBlock().setType(Material.AIR);
                            newLoc.getBlock().setType(fb.getM());
                            fb.setLoc(newLoc);
                            
//                            System.out.println(fb.toString());
                        }
                        angle-= Math.PI/18;
                    }
                }
            }
        }, 20, 10);
    }
    
    public void Start(){
        Random r = new Random();
        dir = r.nextInt(3);
        System.out.println(LocToString(pivot));
        falling = true;
        FallingObjects.add(this);
        angle = Math.PI/2;
    }
    
    public void Stop(){
        falling = false;
        FallingObjects.remove(this);
    }
    
    public void Reset(){
        for(FallBlock fb : blocks){
            fb.getLoc().getBlock().setType(Material.AIR);
        }
        int MaxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int MaxY = Math.max(corner1.getBlockY(), corner2.getBlockY());
        int MaxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());
        for(int X = Math.min(corner1.getBlockX(), corner2.getBlockX()); X <= MaxX; X++){
            for(int Y = Math.min(corner1.getBlockY(), corner2.getBlockY()); Y <= MaxY; Y++){
                for(int Z = Math.min(corner1.getBlockZ(), corner2.getBlockZ()); Z <= MaxZ; Z++){
                    Location l = new Location(corner1.getWorld(), X, Y, Z);
                    l.getBlock().setType(ResetStructure.get(l).getM());
                    l.getBlock().setData(ResetStructure.get(l).getD());
                    
                }
            }
        }
    }
}
