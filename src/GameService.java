import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GameService extends Remote, Serializable {
//    void setGameState(GameState gameState) throws RemoteException;
    GameState getGameState() throws RemoteException;
    GameState move(String playerID, int diff) throws RemoteException;
    GameState updateGameStateNewPlayer(String playerID) throws RemoteException;
}
