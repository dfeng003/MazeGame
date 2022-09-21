import java.io.Serializable;
import java.nio.charset.IllegalCharsetNameException;

/*
 * PlayerInfo is maintains the information of connecting players 
 */
//package edu.nus.mazegame.trackerservice;

public class PlayerInfo implements Serializable{
	private static final long serialVersionUID = 6625883990856972736L;

	private String playerID;
	private GameService stub;

	public PlayerInfo(String id, GameService game_stub){
		playerID = id;
		stub = game_stub;
	}

	public String getPlayerID() {
		return playerID;
	}

	public GameService getStub() { return stub;	}
	
	public String toString() {
		return playerID;
	}
}
