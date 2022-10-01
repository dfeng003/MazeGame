import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

public interface TrackerService extends Remote {
	
	boolean joinGame(PlayerInfo newPlayer) throws RemoteException;
	Map<String, Object> getInfo() throws RemoteException;
	boolean removePlayer(String playerName) throws RemoteException;
	PlayerInfo handleCrashedPlayer(String playerName) throws RemoteException;

}
