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

    private String trackerIp;
    private int trackerPort;
    private final String playerID;
    private GameState gameState;
    public TrackerService tracker;
    public GameService server;
    public int N;
    public int K;
    public String role;
    private final String PLAYER = "Normal_Player";
    private final String PRI_SERVER = "Primary_Server";
    private final String SEC_SERVER = "Secondary_Server";

    public Game(String trackerIp, int trackerPort, String playerID) throws RemoteException, NotBoundException {
        super();
        this.trackerIp = trackerIp;
        this.trackerPort = trackerPort;
        this.playerID = playerID;
        Registry registry = LocateRegistry.getRegistry(trackerIp, trackerPort);
        this.tracker = (TrackerService) registry.lookup("Tracker");
    }

    @Override
    public GameState updateGameStateNewPlayer(String playerName) throws RemoteException {
        if (gameState == null) {
            gameState = new GameState(N, K);
        }
        gameState.initPlayerState(playerName);
        return gameState;
    }

    @Override
    public GameState move(String playerName, int diff) throws RemoteException {
        // TODO: how to ensure mutual exclusion?
        GameState.PlayerState ps = gameState.getPlayerStates().get(playerName);
        if ((diff == 0) || //refresh
                (diff == -1  && ps.position % gameState.N == 0) || // left
                (diff == gameState.N && ps.position >= gameState.N*(gameState.N-1)) || // bottom
                (diff == 1 && ps.position % gameState.N == gameState.N-1) || // right
                (diff == -gameState.N && ps.position < gameState.N)) {  // top
            return gameState;
        }
        ps.position += diff;

        if(gameState.getTreasurePositions().contains(ps.position)) {
            gameState.removeTreasures(ps.position);
            ps.score++;
        }
        return gameState;
    }

    public GameState getGameState(){
        return this.gameState;
    }

    public static void main(String[] args){

        String trackerIp = null;
        int trackerPort = 0;
        String playerID = "";
        Map<String, Object> info;
        ArrayList<PlayerInfo> playerList;


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
            Game mazeGame = new Game(trackerIp, trackerPort, playerID);
            // contact tracker to get N, K, players list
            info = mazeGame.tracker.getInfo();
            mazeGame.K = (Integer)info.get("K");
            mazeGame.N = (Integer)info.get("N");
            playerList = (ArrayList<PlayerInfo>) info.get("Players");
            System.out.println("extracted information from Tracker " + mazeGame.N + " " + mazeGame.K);

            // join game by adding self to the tracker's player list
            PlayerInfo player = new PlayerInfo(playerID, mazeGame);
            System.out.println("Joining game: " + player.toString());
            mazeGame.tracker.joinGame(player);

            // assign server
            if (playerList.size() > 0) {
                // the first in the players list is server
                mazeGame.server = playerList.get(0).getStub();
                mazeGame.role = mazeGame.PLAYER;
                System.out.println("server id: " + playerList.get(0).getPlayerID());
            } else {
                // assign self as server
                mazeGame.role = mazeGame.PRI_SERVER;
                mazeGame.server = mazeGame;
            }
            mazeGame.gameState = mazeGame.server.updateGameStateNewPlayer(playerID);
            System.out.println(mazeGame.getGameState().toString());


            System.out.println("========================  Instructions ======================== ");
            System.out.println("5 to print game state \n                                                     4  \n0 to refresh, 9 to exit. Directional controls are: 1   3\n                                                     2  ");

            // Start Game
            Scanner input = new Scanner(System.in);
            while (input.hasNext()) {
                String in = input.nextLine();
                switch (in) {
                    case "0" -> {
                        mazeGame.gameState = mazeGame.server.move(mazeGame.playerID, 0);
                        System.out.println(mazeGame.getGameState().toString());
                    }
                    case "1" -> {
                        mazeGame.gameState = mazeGame.server.move(mazeGame.playerID, -1);
                        System.out.println(mazeGame.getGameState().toString());
                    }
                    case "2" -> {
                        mazeGame.gameState = mazeGame.server.move(mazeGame.playerID, mazeGame.N);
                        System.out.println(mazeGame.getGameState().toString());
                    }
                    case "3" -> {
                        mazeGame.gameState = mazeGame.server.move(mazeGame.playerID, 1);
                        System.out.println(mazeGame.getGameState().toString());
                    }
                    case "4" -> {
                        mazeGame.gameState = mazeGame.server.move(mazeGame.playerID, -1*mazeGame.N);
                        System.out.println(mazeGame.getGameState().toString());
                    }
                    case "5" -> {
                        System.out.println(mazeGame.getGameState().toString());
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
}