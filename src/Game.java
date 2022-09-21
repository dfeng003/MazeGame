import java.beans.PropertyChangeSupport;
import java.net.InetAddress;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class Game extends UnicastRemoteObject implements GameService{
    private static final long serialVersionUID = -3671463448485643888L;

//    private GameState gameState;
    private String playerID;
    private String trackerIp;
    private int trackerPort;

    public Game(String trackerIp, int trackerPort, String playerID) throws RemoteException {
        super();
//       this.gameState = gs;
        this.trackerIp = trackerIp;
        this.trackerPort = trackerPort;
        this.playerID = playerID;
    }

    public static void main(String[] args){

        String trackerIp = null;
        int trackerPort = 0;
        String playerID = "";
        TrackerService tracker;
        Map<String, Object> info;
        ArrayList<PlayerInfo> playerList;
        GameService server;


        if (args.length < 3) {
            System.err.println("One or more command line options missing");
            System.err.println("Usage:" + "\n" + "java Game <trackerIP> <trackerPort> <playerID>");
            System.exit(0);
        } else {
            trackerIp = args[0];
            trackerPort = Integer.parseInt(args[1]);
            playerID = args[2];
        }

        try {
            // contact tracker to get N, K, players list
            Registry registry = LocateRegistry.getRegistry(trackerIp, trackerPort);
            tracker = (TrackerService) registry.lookup("Tracker");
            info = tracker.getInfo();
            int K = (Integer)info.get("K");
            int N = (Integer)info.get("N");
            playerList = (ArrayList<PlayerInfo>) info.get("Players");

            System.out.println("extracted information from Tracker " + N + " " + K);

//            GameState gs = new GameState(K);
            Game mazeGame = new Game(trackerIp, trackerPort, playerID);
            PlayerInfo player = new PlayerInfo(playerID, mazeGame);

            if (playerList.size() > 0) {
                // the first in the players list is server
                server = playerList.get(0).getStub();
                System.out.println("server id: " + playerList.get(0).getPlayerID());
            } else {
                // assign self as server
                server = mazeGame;
            }

            // Adding new player
            System.out.println("Joining game: " + player.toString());
            tracker.joinGame(player);

            System.out.println("========================  Instructions ======================== ");
            System.out.println("5 to ping Server \n                                                     4  \n0 to refresh, 9 to exit. Directional controls are: 1   3\n                                                     2  ");

            // Start Game
            Scanner input = new Scanner(System.in);
            while (input.hasNext()) {
                String in = input.nextLine();
                switch (in) {
                    case "0" -> System.out.println("refresh");
                    case "1" -> {
                        mazeGame.move("left");
                    }
                    case "2" -> {
                        mazeGame.move("down");
                    }
                    case "3" -> {
                        mazeGame.move("right");
                    }
                    case "4" -> {
                        mazeGame.move("up");
                    }
                    case "5" -> {
                        System.out.println("Ping server");
                        server.sayHello();
                    }
                    case "9" -> {
                        System.out.println("exit");
                        System.exit(0);
                    }
                    default -> {
                        System.out.println("Invalid Input!");
                        System.out.println("========================  Instructions ======================== ");
                        System.out.println("                                                     4  \n0 to refresh, 9 to exit. Directional controls are: 1   3\n                                                     2  ");
                    }
                }
            }
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    @Override
    public void move(String dir) throws RemoteException {
        //TODO: update gameState when it moves
        //thinking of creating a GameEngine class, inherited by Server and normal Player.
        //move the move method to the engine.
        System.out.println("moved" + dir);
    }

    @Override
    public void sayHello() throws RemoteException {
        // for testing only
        System.out.println( this.playerID + ": Hello");
    }
}