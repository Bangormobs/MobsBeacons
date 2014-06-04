package uk.co.mobsoc.beacons.transientdata;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Attachable;
/**
 * Transient, non DB Data. Stores data on everything that defines what a block is so that once an event is ended it can be replaced in full.
 * @author triggerhapp
 *
 */
public class BlockData {
	public Location location;
	public Block block;
	public int id;
	public byte meta;
	public boolean attachable=false;
	public String signText=null;
	// TODO : Containers - of all sorts
	

	public BlockData(Block block){
		if(block==null){ return; }
		this.location = block.getLocation(); this.id = block.getTypeId(); this.meta=block.getData();
		this.block = block;
		if(block instanceof Attachable){
			attachable=true;
		}
		if(block.getState() instanceof Sign){
			Sign s = (Sign) block.getState();
			signText="";
			String sep="";
			for(String line : s.getLines()){
				signText = signText+sep+line;
				sep="\n";
			}
		}
	}
	
	public void removeBlock() {
		block.setTypeId(0,false);
		for(Player p : block.getWorld().getPlayers()){
			p.sendBlockChange(block.getLocation(), 0, (byte) 0);
		}
	}

	public void setBlock() {
		Block block = location.getBlock();
		if(block.getType()!= Material.AIR){
			// If it dropped into place... move it to top
			if(block.getType() == Material.SAND || block.getType() == Material.GRAVEL){
				World w = block.getWorld();
				int x = block.getX(), y = block.getY(), z = block.getZ();
				for(int yInc = 1; yInc < 128; yInc++){
					Block newBlock = w.getBlockAt(x, y+yInc, z);
					if(newBlock.getType()==Material.AIR){
						newBlock.setType(block.getType());
						break;
					}
				}
			}else{
				// Placed by defender
				ItemStack s = new ItemStack(Material.DIAMOND_PICKAXE);
				Enchantment e = Enchantment.SILK_TOUCH;
				s.addEnchantment(e, e.getMaxLevel());
				block.breakNaturally(s);
			}
			
		}
		block.setTypeIdAndData(id, meta, false);
		for(Player p : block.getWorld().getPlayers()){
			p.sendBlockChange(block.getLocation(), block.getTypeId(), block.getData());
		}
		if(signText!=null){
			Sign sign = (Sign) block.getState();
			int i = 0;
			for(String s : signText.split("\n")){
				sign.setLine(i, s);
				i++;
			}
			sign.update();
		}
	}
}
