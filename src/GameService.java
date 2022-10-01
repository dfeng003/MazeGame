import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GameService extends Remote, Serializable {
    void exitGame(String playerID, String role) throws RemoteException;
    GameState getGameState() throws RemoteException;
    GameState move(String playerID, int diff, String role) throws RemoteException;
    GameState updateGameStateNewPlayer(String playerID, String role, PlayerInfo player) throws RemoteException;
    void setGameState(GameState gs) throws RemoteException;
    void setServer(GameService server, String name) throws RemoteException;
    void setBackupServer(GameService backup, String name) throws RemoteException;
    void setRole (String newRole) throws RemoteException;
    boolean ping() throws RemoteException;
    PlayerInfo handleCrashedPlayer(String playerName) throws RemoteException;
}
