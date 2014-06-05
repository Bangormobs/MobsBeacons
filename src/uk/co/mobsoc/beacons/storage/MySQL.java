package uk.co.mobsoc.beacons.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.block.Block;


/**
 * Utility class of static functions related to gathering or altering data in MySQL
 * @author triggerhapp
 *
 */
public class MySQL {
	private static PreparedStatement getPlayer, getPlayersFromTeam;
	private static PreparedStatement getTeam, getAllTeams;
	private static PreparedStatement getBeaconsFromTeam, getBeaconFromBlock, getAllBeacons;
	
	public static void prepareMySQL(String IP, String userName, String passWord, String dataBase){
		Statement stat;

		try {
			Connection conn = DriverManager.getConnection("jdbc:mysql://"+IP+"/"+dataBase+"?user="+userName+"&password="+passWord);
			stat = conn.createStatement();
			stat.execute("drop procedure if exists AddColumnUnlessExists;");
			String s = "create procedure AddColumnUnlessExists( ";
			       s+= " IN dbName tinytext, ";
			       s+= " IN tableName tinytext, ";
			       s+= " IN fieldName tinytext, ";
			       s+= " IN fieldDef text ) ";
			       s+= "begin ";
			       s+= " IF NOT EXISTS ( ";
			       s+= "  SELECT * FROM information_schema.COLUMNS ";
			       s+= "  WHERE column_name=fieldName ";
			       s+= "  and table_name = tableName ";
			       s+= "  and table_schema = dbName ";
			       s+= "  ) ";
			       s+= " THEN ";
			       s+= "  set @ddl=CONCAT('ALTER TABLE ', dbName, '.', tableName, ' ADD COLUMN ',fieldName,' ',fieldDef); ";
			       s+= "  prepare stmt from @ddl; ";
			       s+= "  execute stmt; ";
			       s+= " END IF; END; ";
			stat.executeUpdate(s);
			stat.execute("CREATE TABLE IF NOT EXISTS `Beacons` (`x` int (11), `y` int(11), `z` int(11), `team` int(11) DEFAULT '-1', `radius` int(11), PRIMARY KEY(`x`,`y`,`z`));");
			stat.execute("CREATE TABLE IF NOT EXISTS `Player` (`uuid` binary(16), `name` varchar(16), `team` int(11), PRIMARY KEY(`uuid`));");
			stat.execute("CREATE TABLE IF NOT EXISTS `Team` (`id` int(11), `name` varchar(20), PRIMARY KEY(`id`));");
			
			String PlayerDataValues = "`uuid`, `name`, `team`";
			getPlayer = conn.prepareStatement("SELECT "+PlayerDataValues+" FROM `Player` WHERE `uuid`=?");
			getPlayersFromTeam = conn.prepareStatement("SELECT "+PlayerDataValues+" FROM `Player` WHERE `team`=?");
			String TeamDataValues = "`id`, `name`";
			getTeam = conn.prepareStatement("SELECT "+TeamDataValues+" FROM `Team` WHERE `id`=?");
			getAllTeams = conn.prepareStatement("SELECT "+TeamDataValues+" FROM `Team`");
			String BeaconDataValues = "`x`, `y`, `z`, `team`, `radius`";
			getBeaconsFromTeam = conn.prepareStatement("SELECT "+BeaconDataValues+" FROM `Beacons` WHERE `team`=?");
			getBeaconFromBlock = conn.prepareStatement("SELECT "+BeaconDataValues+" FROM `Beacons` WHERE `x` = ? AND `y` = ? AND `z` = ?");
			getAllBeacons = conn.prepareStatement("SELECT "+BeaconDataValues+" FROM `Beacons`");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static PlayerData getPlayer(UUID id){
		// TODO : Anything
		return null;
	}
	
	public static ArrayList<PlayerData> getPlayersFromTeam(TeamData td){
		// TODO : Anything
		return null;
	}
	
	public static TeamData getTeam(int id){
		// TODO : Anything
		return null;
	}
	
	public static ArrayList<TeamData> getAllTeams(){
		// TODO : Anything
		return null;
	}
	
	public static ArrayList<BeaconData> getBeaconsFromTeam(TeamData td){
		// TODO : Anything
		return null;
	}
	
	public static BeaconData getBeaconFromBlock(Block block){
		// TODO : Anything
		return null;
	}
	
    public static byte[] uuidToBytes(UUID uuid){
    	long msb = uuid.getMostSignificantBits(), lsb = uuid.getLeastSignificantBits();
        byte[] buffer = new byte[16];

        for (int i = 0; i < 8; i++) {
                buffer[i] = (byte) (msb >>> 8 * (7 - i));
        }
        for (int i = 8; i < 16; i++) {
                buffer[i] = (byte) (lsb >>> 8 * (7 - i));
        }
        return buffer;
    }

	public static UUID bytesToUuid(byte[] uuid) {
        long msb = 0;
        long lsb = 0;
        if(uuid!=null && uuid.length == 16){
        	for (int i=0; i<8; i++)
                	msb = (msb << 8) | (uuid[i] & 0xff);
        	for (int i=8; i<16; i++)
                	lsb = (lsb << 8) | (uuid[i] & 0xff);
        }
        UUID u = new UUID(msb, lsb);
		return u;
	}

	public static ArrayList<BeaconData> getAllBeacons() {
		// TODO Anything
		return null;
	}
}
