import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.LinkedList;

public class GameState implements Serializable{

	private static final long serialVersionUID = 6625883990856972736L;
	public int[][] treasureXY;
	public Hashtable<String,Integer> playersScore;
	public Hashtable<String,int[]> playerXY;
	public PlayerInfo primaryServer;
	public PlayerInfo backupServer;

	GameState(int k){
		this.treasureXY  =  new int[k][2]; //list of k elements, each element has 2 element x,y
		this.playersScore = new Hashtable<String,Integer>();
		this.playerXY = new Hashtable<String,int[]>();
	}

//	if call getPlayerScore without id, will return the whole hashtable
	public Hashtable<String, Integer> getPlayerScore(){
		return this.playersScore;
	}

//	if call getPlayerScore with id, will return the score of the id
	public int getPlayerScore(String id){
		return this.playersScore.get(id);
	}
	
	public int setPlayerXY(String id, int x, int y){
		int[] newPos = {x,y};
		this.playerXY.put(id, newPos);
		return 1;
	}

	public int dropPlayerXY(String id){
		this.playerXY.remove(id);
		return 1;
	}
}
