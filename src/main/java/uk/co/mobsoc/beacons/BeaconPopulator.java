package uk.co.mobsoc.beacons;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

import uk.co.mobsoc.beacons.storage.MySQL;

public class BeaconPopulator extends BlockPopulator {

	@Override
	public void populate(World world, Random rand, Chunk chunk) {
		if(chunk.getX() % 8 != 0){ return; }
		if(chunk.getZ() % 8 != 0){ return; }
		ArrayList<PossibleBeaconPosition> bPosList = new ArrayList<PossibleBeaconPosition>();
		// Populate a list with all possible options from a 2d/top-down view
		for(int x = 3; x<13; x++){
			for(int z = 3; z<13; z++){
				bPosList.add(new PossibleBeaconPosition(x,z));
			}
		}
		while(bPosList.size() > 0){
			int nextIndex = rand.nextInt(bPosList.size()); // Choose a random location from the list made before
			PossibleBeaconPosition maybeThisOne = bPosList.get(nextIndex);
			int height = getBeaconHeight(world, chunk, maybeThisOne.x, maybeThisOne.z);
			if(height > 0 && height < (world.getMaxHeight()-2)){ // Be sure it's a valid value
				Material toPlace = Material.BEACON;
				MySQL.createWildBeacon(chunk.getBlock(maybeThisOne.x, height, maybeThisOne.z));
				for(int y=height; y<(world.getMaxHeight()-1); y++){
					chunk.getBlock(maybeThisOne.x, y, maybeThisOne.z).setType(toPlace);
					toPlace = Material.AIR;
				}
				return;
			}
			bPosList.remove(maybeThisOne); // It wasn't this one
		}
		// Pass. No place to put a beacon
	}
	
	public int getBeaconHeight(World world, Chunk chunk, int x, int z){
		for(int y = world.getMaxHeight()-1; y>0; y--){
			Material m = chunk.getBlock(x, y, z).getType();
			if(m == Material.GRASS || m == Material.DIRT || m == Material.GRAVEL || m == Material.SAND || m == Material.MYCEL || m == Material.STAINED_CLAY || m == Material.HARD_CLAY){
				// Allow placing above an obvious ground-type block
				return y + 1;
			}else if(m == Material.LEAVES || m == Material.LEAVES_2 || m == Material.RED_MUSHROOM || m == Material.RED_ROSE || m == Material.YELLOW_FLOWER || m == Material.BROWN_MUSHROOM || m == Material.AIR || m == Material.LONG_GRASS || m == Material.DEAD_BUSH){
				// Ignore certain types. These will be erased after if this spot is chosen
				continue;
			}else{
				// Disallow placement on top of other types
				return -1;
			}
		}
		return -1;
	}

}
