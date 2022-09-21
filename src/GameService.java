import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GameService extends Remote, Serializable {
    void move(String dir) throws RemoteException;
    void sayHello() throws RemoteException;
}
