package uk.co.mobsoc.beacons.transientdata;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.material.Attachable;

/**
 * Transient, non DB Data. Stores data on everything that defines what a block is so that once an event is ended it can be replaced in full.
 * I think I made this redundant.
 * @author triggerhapp
 *
 */
public class BlockData {
	private BlockState bs;
	public boolean attached=false;

	public BlockData(Block block){
		if(block==null){ return; }
		bs = block.getState();
	}
	
	public void removeBlock() {
		BlockState bs2 = bs.getBlock().getState(); // Really? No copy()/clone() ?
		if(bs2 instanceof InventoryHolder){
			InventoryHolder ih = (InventoryHolder) bs2;
			ih.getInventory().clear();
		}
		if(bs2.getData() instanceof Attachable){
			attached = true;
		}
		bs2.setType(Material.AIR);
		bs2.update(true, false);
	}

	public void setBlock() {
		bs.update(true, false);
	}
}
