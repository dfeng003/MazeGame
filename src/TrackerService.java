import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

public interface TrackerService extends Remote {

	Map<String, Object> joinGame(PlayerInfo newPlayer) throws RemoteException;
	PlayerInfo handleCrashedPlayer(String playerName) throws RemoteException;

}
