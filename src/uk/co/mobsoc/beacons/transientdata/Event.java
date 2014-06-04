package uk.co.mobsoc.beacons.transientdata;

import java.util.ArrayList;

import org.bukkit.block.Block;

import uk.co.mobsoc.beacons.storage.TeamData;

public abstract class Event {
	private int warmuptimer = 0;
	private int maintimer = 0;
	private int cooldowntimer = 0;
	/**
	 * Sets the time - in ticks (i * 20) (should be seconds - not guaranteed) that the game will count down from before the event starts in earnest
	 * @param i
	 */
	protected void setWarmUpTimer(int i){
		warmuptimer = i;
	}
	/**
	 * Sets the time - in ticks (i * 20) (should be seconds - not guaranteed) that the game will count down from while the event takes place
	 * @param i
	 */
	protected void setMainTimer(int i){
		maintimer = i;
	}
	/**
	 * 
	 * @param i
	 */
	protected void setCoolDownTimer(int i){
		cooldowntimer = i;
	}
	/**
	 * Returns true if the event is still ongoing
	 * @return
	 */
	public boolean isActive(){
		return warmuptimer > 0 || maintimer > 0 || cooldowntimer > 0;
	}
	/**
	 * Gets the first team involved in this event. Null is not allowed - one team must always be returned
	 * @return
	 */
	public abstract TeamData getTeam1();
	/**
	 * Gets the second team involved in this event. Null is allowed if the event is against Mobs.
	 * @return
	 */
	public abstract TeamData getTeam2();
	
	private ArrayList<BlockData> blockEdits = new ArrayList<BlockData>();
	public void rememberBlock(Block b){
		BlockData bd = new BlockData(b);
		blockEdits.add(bd);
		bd.removeBlock();
	}
	
	/**
	 * Fail-safe replacement of blocks. Done instantly and is kept in case of errors or sudden server shut down
	 */
	public void bailAllBlocks(){
		// Do all non-attached blocks - solid whole blocks.
		for(BlockData bd : blockEdits){
			if(!bd.attachable){
				bd.setBlock();
			}
		}
		// Do all attached blocks - signs, lights. Things that will fall off without support
		for(BlockData bd : blockEdits){
			if(bd.attachable){
				bd.setBlock();
			}
		}
	}
	
}
