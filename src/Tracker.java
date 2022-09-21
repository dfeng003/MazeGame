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
	public Map<String, Object> getInfo() throws RemoteException {
		Map<String, Object> gameInfo = new HashMap<String, Object>();

		gameInfo.put("Players", this.playerList);
		gameInfo.put("N", this.N);
		gameInfo.put("K", this.K);

		return gameInfo;
	}

	@Override
	public boolean joinGame(PlayerInfo newPlayer) throws RemoteException {
		playerList.add(newPlayer);
		System.out.println("Added to playerList: " + playerList.get(playerList.size()-1).getPlayerID());
		return true;
	}

	@Override
	public boolean removePlayer(String playerID) throws RemoteException {
		boolean status = false;

		System.err.println("remove player from tracker: " + playerID);

		for(PlayerInfo pInfo : playerList) {
			if(pInfo.getPlayerID().equals(playerID)) {
				playerList.remove(pInfo);
				status = true;
				break;
			}
		}
		return status;
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