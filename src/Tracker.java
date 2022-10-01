/*
 * Tracker acts as a point of contact to players only on two occasions
 *  
 * 1) when a new player wants to join the MazeGame
 * 
 * 2) when there is a player crash
 * 
 * Tracker Resposibility
 * 
 * 1) Accept new player join request
 * 
 * 2) Always maintain the active player list 
 * 
 * 3) Accept player crash info and update the active player list
 *
 * 4) Accept existing player exit info and update the active player list
 */

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Tracker extends UnicastRemoteObject implements TrackerService {
	private static final long serialVersionUID = 6625883990856972736L;
	private String trackerIP;
	private String trackerPort;
	private Integer N;
	private Integer K;
	private ArrayList<PlayerInfo> playerList = new ArrayList<>();

	protected Tracker(String ip, String port, int N, int K) throws RemoteException {
		super();
		this.trackerIP = ip;
		this.trackerPort = port;
		this.N = N;
		this.K = K;
	}

	@Override
	public Map<String, Object> joinGame(PlayerInfo newPlayer) throws RemoteException {
		playerList.add(newPlayer);
		System.out.println(LocalDateTime.now() + "Added to playerList: " + newPlayer.getPlayerID());

		Map<String, Object> gameInfo = new HashMap<String, Object>();
		gameInfo.put("Players", this.playerList);
		gameInfo.put("N", this.N);
		gameInfo.put("K", this.K);
		return gameInfo;
	}

	@Override
	public boolean removePlayer(String playerID) throws RemoteException {
		boolean status = false;

		System.out.println(LocalDateTime.now() + "removing player from tracker: " + playerID);

		for(PlayerInfo pInfo : playerList) {
			if(pInfo.getPlayerID().equals(playerID)) {
				playerList.remove(pInfo);
				status = true;
				break;
			}
		}
		return status;
	}

	@Override
	public PlayerInfo handleCrashedPlayer(String playerID) throws RemoteException {
		System.out.println(LocalDateTime.now() + "removing crashed player from tracker: " + playerID);
		for (int i = 0; i < playerList.size(); i ++) {
			if (playerList.get(i).getPlayerID().equals(playerID)){
				playerList.remove(i);
				if (i == 0){
					// server crashed, reassign both server and backup server, since backup server is the new server
					GameService newServer = playerList.get(0).getStub();
					newServer.setRole(Game.PRI_SERVER);
					reassignServer(newServer, Game.PRI_SERVER, playerList.get(0).getPlayerID());
				}
				if ( i <= 1 ){
					// server or backup server crashed
					GameService newBackup = null;
					String name = "null";
					if (playerList.size() >= 2) {
						newBackup = playerList.get(1).getStub();
						newBackup.setRole(Game.SEC_SERVER);
						name = playerList.get(1).getPlayerID();
					}
					reassignServer(newBackup, Game.SEC_SERVER, name);
				}

				if (playerList.size() == 1){
					return null;
				} else if ( i == 0){
					return playerList.get(playerList.size()-1);
				} else {
					return playerList.get(i-1);
				}
			}
		}
		return null;
	}

	private void reassignServer(GameService server, String role, String name) throws RemoteException{
		System.out.println(LocalDateTime.now() + "reassigning " + role + " "+ name);
		for(PlayerInfo pInfo : playerList) {
			if(role.equals(Game.PRI_SERVER)) {
				pInfo.getStub().setServer(server, name);
			} else {
				pInfo.getStub().setBackupServer(server, name);
			}
		}
	}

	public static void main(String[] args) {
		Registry registry = null;
		System.setProperty("java.rmi.server.codebase", TrackerService.class.getProtectionDomain().getCodeSource().getLocation().toString());
		System.setProperty("java.security.policy", "/java.policy");

		try {
			if (args.length < 3) {
				System.err.println("One or more command line options missing");
				System.err.println("Usage:" + "\n" + "java Tracker <port> <grid-N> <treasure-K>");
			} else {
				String trackerIp = InetAddress.getLocalHost().getHostAddress();
				System.out.println("My IP is " + trackerIp);
				String port = args[0];
				int N = Integer.parseInt(args[1]);
				int K = Integer.parseInt(args[2]);

				Tracker mazeGameTracker = new Tracker(trackerIp, port, N, K);

				// add tracker to registry
				try {
//					stub = (TrackerService) UnicastRemoteObject.exportObject(mazeGameTracker, 0);
					registry = LocateRegistry.getRegistry();
					registry.bind("Tracker", mazeGameTracker);
					System.out.println("Tracker ready");
				} catch (Exception e) {
					System.err.println("error: could not register Tracker: " + e.toString());
					System.exit(1);
				}
			}
		} catch (Exception e) {
			System.err.println("Tracker exception: " + e.toString());
			e.printStackTrace();
		}
	}

	public String getTrackerIP() {
		return trackerIP;
	}

	public void setTrackerIP(String trackerIP) {
		this.trackerIP = trackerIP;
	}

	public String getTrackerPort() {
		return trackerPort;
	}

	public void setTrackerPort(String trackerPort) {
		this.trackerPort = trackerPort;
	}

	public Integer getN() {
		return N;
	}

	public void setN(Integer N) {
		this.N = N;
	}

	public Integer getK() {
		return K;
	}

	public void setK(Integer treasureK) {
		this.K = K;
	}


}