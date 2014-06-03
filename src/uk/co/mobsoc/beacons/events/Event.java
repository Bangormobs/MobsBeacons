package uk.co.mobsoc.beacons.events;

import uk.co.mobsoc.beacons.storage.TeamData;

public interface Event {
	/**
	 * Returns true if the event is still ongoing
	 * @return
	 */
	public boolean isActive();
	/**
	 * Gets the first team involved in this event. Null is not allowed - one team must always be returned
	 * @return
	 */
	public TeamData getTeam1();
	/**
	 * Gets the second team involved in this event. Null is allowed if the event is against Mobs.
	 * @return
	 */
	public TeamData getTeam2();
	
}
