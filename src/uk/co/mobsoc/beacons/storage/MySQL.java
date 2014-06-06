package uk.co.mobsoc.beacons.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import org.bukkit.block.Block;


/**
 * Utility class of static functions related to gathering or altering data in MySQL
 * @author triggerhapp
 *
 */
public class MySQL {
	private static Random rand = new Random();
	private static PreparedStatement getPlayer, getPlayersFromTeam;
	private static PreparedStatement getTeam, getAllTeams;
	private static PreparedStatement getBeaconsFromTeam, getBeaconFromBlock, getAllBeacons, insertBeacon;
	
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
			insertBeacon = conn.prepareStatement("INSERT INTO `Beacons` ("+BeaconDataValues+") VALUES ( ?, ?, ?, ?, ? )");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("The config settings for MobsBeacons have not been set to usable MySQL user and DB");
			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			System.exit(1);
		}
	}
	
	public static PlayerData getPlayer(UUID id){
		PlayerData pd = new PlayerData();
		// TODO : Anything
		return pd;
	}
	
	public static ArrayList<PlayerData> getPlayersFromTeam(TeamData td){
		ArrayList<PlayerData> pdList = new ArrayList<PlayerData>();
		// TODO : Anything
		return pdList;
	}
	
	public static TeamData getTeam(int id){
		// TODO : Anything
		return null;
	}
	
	public static ArrayList<TeamData> getAllTeams(){
		ArrayList<TeamData> tdList = new ArrayList<TeamData>();
		// TODO : Anything
		return tdList;
	}
	
	public static ArrayList<BeaconData> getBeaconsFromTeam(TeamData td){
		ArrayList<BeaconData> bdList = new ArrayList<BeaconData>();
		// TODO : Anything
		return bdList;
	}
	
	public static BeaconData getBeaconFromBlock(Block block){
		try {
			getBeaconFromBlock.setInt(1, block.getX());
			getBeaconFromBlock.setInt(2, block.getY());
			getBeaconFromBlock.setInt(3, block.getZ());
			getBeaconFromBlock.execute();
			ResultSet rs = getBeaconFromBlock.getResultSet();
			if(rs.next()){
				return new BeaconData(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

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
		ArrayList<BeaconData> bdList = new ArrayList<BeaconData>();
		// TODO : Anything
		return bdList;
	}

	public static void createWildBeacon(Block b) {
		if(b == null){ System.out.println("Error - adding wild null beacon. How?"); }
		BeaconData bd = new BeaconData();
		bd.setBeacon(b);
		bd.setRadius(rand.nextInt(6)+7);
		bd.setTeamId(-1);
		insertBeacon(bd, b);
	}

	/**
	 * Doubles up usage of Block because getBeacon can return null during world load
	 * @param bd
	 * @param beacon
	 */
	private static void insertBeacon(BeaconData bd, Block beacon) {
		try {
			insertBeacon.setInt(1, beacon.getX());
			insertBeacon.setInt(2, beacon.getY());
			insertBeacon.setInt(3, beacon.getZ());
			insertBeacon.setInt(4, bd.getTeamId());
			insertBeacon.setInt(5, bd.getRadius());
			insertBeacon.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
