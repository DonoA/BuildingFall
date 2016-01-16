/*
 * This file is part of BuildingFall.
 * 
 * BuildingFall is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * BuildingFall is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with BuildingFall.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 */
package net.pocketpixels.buildingfall;

import lombok.Getter;
import lombok.Setter;
import static net.pocketpixels.buildingfall.BuildingFall.LocToString;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 *
 * @author donoa_000
 */
public class FallBlock {
    @Getter
    private Material m;
    
    @Getter
    private byte d;
    
    @Getter @Setter
    private Location loc;
    
    @Getter
    private Point relPoint;

    @Getter
    private double relAngleX;
    
    @Getter
    private double relAngleZ;
    
    @Getter
    private double hypX;
    
    @Getter
    private double hypZ;
    
    public FallBlock(Location l, Point p, double[] a, double[] hyp){
        m = l.getBlock().getType();
        d = l.getBlock().getData();
        loc = l;
        relPoint = p;
        relAngleX = a[0];
        relAngleZ = a[1];
        hypX = hyp[0];
        hypZ = hyp[1];
    }
    
    public FallBlock(Location l){
        m = l.getBlock().getType();
        d = l.getBlock().getData();
        loc = l;
    }
    
    public static class Point{
        public int X;
        public int Y;
        public int Z;
        
        public Point(int x, int y, int z){
            X=x;
            Y=y;
            Z=z;
        }
    }
    
    public String toString(){
        String rtn = LocToString(loc) + ", " + m.name();
        return rtn;
    }
}
