import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;


public class GUI extends JFrame implements PropertyChangeListener {
    private String playerName;
    private JLabel[][] grids;
    private JLabel playerLabel;
    private JLabel serverLabel;
    private GameState gameState;

    private void updateplayerLabel(){
        StringBuilder sb = new StringBuilder("Scores  ");
        for (Map.Entry<String, GameState.PlayerState> entry : gameState.getPlayerStates().entrySet()) {
            sb.append(entry.getKey()).append(" : ").append(entry.getValue().score).append("  ");
        }
    	playerLabel.setText(sb.toString());
    }

    private void updateServerLabel(){
        StringBuilder sb = new StringBuilder("Servers ");
        sb.append("Primary: ").append(gameState.getServerName()).append(" Secondary: ").append(gameState.getBackupName());
        serverLabel.setText(sb.toString());
    }

    private void updateMapGrids() {
        int row_count = gameState.N;
        int col_count = gameState.N;
        for(int i=0; i<row_count; i++) {
            for(int j=0; j<col_count; j++) {
                int pos = i * row_count + j;
                Color backgroundColor = Color.white;
                StringBuilder cell_sb = new StringBuilder();
                if(gameState.getTreasurePositions().contains(pos)) {
                    backgroundColor = Color.yellow;
                    cell_sb.append('*');
                }
                for (Map.Entry<String, GameState.PlayerState> entry : gameState.getPlayerStates().entrySet()) {
                    if(entry.getValue().position == pos) {
                    	cell_sb.append(entry.getKey());
                        if(entry.getKey().equals(playerName)) {
                        	backgroundColor = Color.pink;
                        }
                        break;
                    }
                }
            	grids[i][j].setText(cell_sb.toString());
                grids[i][j].setBackground(backgroundColor);
            }
        }
    }

    public GUI(GameState gameState, String playerName) {
        setVisible(true);
        int rows = gameState.N;
        int cols = gameState.N;
        this.playerName = playerName;
        this.gameState = gameState;

        // Player Info
        Panel legend = new Panel(new GridLayout(2, 1));
        playerLabel = new JLabel();
        updateplayerLabel();
//        playerLabel.setSize(400, 700);
        legend.add(playerLabel);

        // Server Info
        serverLabel = new JLabel();
        updateServerLabel();
//        serverLabel.setSize(400, 700);
        legend.add(serverLabel);

        // Map
        Panel map = new Panel(new GridLayout(rows, cols));
        grids = new JLabel[rows][cols];
        for(int i=0; i<rows; i++) {
            for (int j = 0; j < cols; j++) {
            	grids[i][j] = new JLabel();
            	grids[i][j].setOpaque(true);
                grids[i][j].setBorder(BorderFactory.createLineBorder(Color.black));
            	map.add(grids[i][j]);
            }
        }
        updateMapGrids();
        setLayout(new BorderLayout());
        add(map, BorderLayout.CENTER);
        add(legend, BorderLayout.WEST);
        setTitle(playerName);
        setSize(600, 400);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Closing...");
                System.exit(0);
            }
        });
    }

    public void propertyChange(PropertyChangeEvent event) {
        gameState = (GameState) event.getNewValue();
        updateplayerLabel();
        updateServerLabel();
        updateMapGrids();
    }
}
