package game;

import game.ai.*;
import game.grid.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.*;
import javax.swing.*;
import jship.JShip;
import users.CurrentUser;


/**
 * Form for playing the actual game.
 * Temporary class used for visually designing the GUI.
 *
 * @author blackk100
 */
final class Game extends JFrame {

	/**
	 * Round Initialization Variables:
	 *
	 * <pre>
	 * I-----------I-----------I-----------I
	 * I Index No. I   false   I    true   I
	 * I-----------I-----------I-----------I
	 * I     0     I  10 x 10  I  15 x 15  I
	 * I-----------I-----------------------I
	 * I     1     I       Battleship      I
	 * I     2     I        Cruiser        I
	 * I     3     I       Destroyer       I
	 * I     4     I        Corvette       I
	 * I-----------I-----------------------I
	 * </pre>
	 */
	private final boolean[] initVars;

	/**
	 * Game Mode:
	 * <pre>
	 * C - Classic
	 * S - Salvo
	 * </pre>
	 */
	private final String mode;

	/**
	 * An integer show the length of the grid.
	 * if initVars[0] is true, gridSize is 15. Else 10.
	 */
	private final int gridSize;

	/**
	 * Total number of ships available in this match.
	 */
	private final int shipNos;

	/**
	 * AI Difficulty:
	 *
	 * <pre>
	 * -1 - Sandbox   (Easy)
	 *  0 - Realistic (Medium)
	 *  1 - Brutal    (Hard)
	 * </pre>
	 */
	private final int AIDiff;

	/**
	 * The AI. Uses Grid 2.
	 */
	private AI AI;

	/**
	 * The Player's Grid as a 2-Dimensional Location Array.
	 */
	private Location[][] PlayerGridL;

	/**
	 * The AI's Grid as a 2-Dimensional Location Array.
	 */
	private Location[][] AIGridL;

	/**
	 * The Player's ships.
	 */
	private Ship[] PlayerShips;

	/**
	 * The AI's ships.
	 */
	private Ship[] AIShips;

	/**
	 * The Player's Statistics:
	 *
	 * <pre>
	 * I-----------I-----------------------I
	 * I Index No. I    Value Stored       I
	 * I-----------I-----------------------I
	 * I     0     I Shots Fired  (SF)     I
	 * I     1     I Hits landed  (Hits)   I
	 * I     2     I Ships Lost   (SL)     I
	 * I-----------I-----------------------I
	 * </pre>
	 */
	private int[] PlayerStats = {0, 0, 0};

	/**
	 * The AI's Statistics:
	 *
	 * <pre>
	 * I-----------I-----------------------I
	 * I Index No. I    Value Stored       I
	 * I-----------I-----------------------I
	 * I     0     I Shots Fired  (SF)     I
	 * I     1     I Hits landed  (Hits)   I
	 * I     2     I Ships Lost   (SL)     I
	 * I-----------I-----------------------I
	 * </pre>
	 */
	private int[] AIStats = {0, 0, 0};

	/**
	 * Constant value of '-1' indicating that the match ended.
	 */
	private static final int END = -1;

	/**
	 * Constant value of '0' indicating that it is the ship placement round.
	 */
	private static final int PLACE = 0;

	/**
	 * Internal counter for current game/match round/sequence.
	 * <pre>
	 * -1  - Game Ended (this.END).
	 * 0   - Ship Placement (this.PLACE).
	 * &gt; 0 - Actual Game (until it ends).
	 * </pre>
	 */
	private int roundNo = 0;

	/**
	 * Constant value of '1' indicating that the user won.
	 */
	static final int WIN = 1;

	/**
	 * Constant value of '0' indicating that the user lost.
	 */
	static final int LOSE = 0;

	/**
	 * An integer indicating whether or not the given round was won by the user.
	 * <pre>
	 * 1 - Win
	 * 0 - Lose
	 * </pre>
	 */
	private int status;

	/**
	 * Variable for storing the state of each button (i.e., clicked or not) for each round.
	 * <pre>
	 * true  - Clicked
	 * false - Not Clicked
	 * </pre>s
	 */
	private boolean[][] buttonsClicked;

	/**
	 * Temporary variable for storing the number locations selected for placing shots.
	 * Used after the Ship Placement round.
	 */
	private int shotsSelected = 0;

	/**
	 * Temporary variable for storing the number of ships placed.
	 * Only used during the Ship Placement round.
	 */
	private int shipPlacing = 0;

	/**
	 * Creates new form Main
	 *
	 * @param initVars Initialization Variables
	 * @param mode     Game Mode
	 * @param AIDiff   AI Difficulty Level
	 */
	Game(boolean[] initVars, String mode, int AIDiff) {
		this.initVars = initVars;
		this.mode = mode;
		this.AIDiff = AIDiff;

		this.gridSize = initVars[0] ? 15 : 10;
		this.buttonsClicked = new boolean[this.gridSize][this.gridSize];

		this.shipNos = (initVars[1] ? 1 : 0) + (initVars[2] ? 2 : 0) + (initVars[3] ? 2 : 0) + (initVars[4] ? 4 : 0);
		this.PlayerShips = new Ship[this.shipNos];
		int shipNo = 0;    // Temporary variable storing the ship number being initialized.
		if (initVars[1]) { // Battleships
			for (int i = shipNo; i < shipNo + 1; i++) {
				this.PlayerShips[i] = new Ship(5);
			}
			shipNo += 1;
		}
		if (initVars[2]) { // Cruisers
			for (int i = shipNo; i < shipNo + 2; i++) {
				this.PlayerShips[i] = new Ship(4);
			}
			shipNo += 2;
		}
		if (initVars[3]) { // Destroyers
			for (int i = shipNo; i < shipNo + 2; i++) {
				this.PlayerShips[i] = new Ship(3);
			}
			shipNo += 2;
		}
		if (initVars[4]) { // Corvettes
			for (int i = shipNo; i < shipNo + 4; i++) {
				this.PlayerShips[i] = new Ship(2);
			}
		}

		this.initComponents();

		this.setTitleL(AIDiff);
		this.GridTF.setText(initVars[0] ? "15 x 15" : "10 x 10");
		this.ModeTF.setText(mode.equals("C") ? "Classic" : "Salvo");
		this.BattleshipCB.doClick();
		this.StatsUpdate();
		this.nextShip();

		Object[] temp;                              // Temporary Array for storing Grid 1 & Grid 2 objects.
		temp = this.initGrid(this.gridSize, false); // Grid 1
		this.PlayerGridB = (JButton[][]) temp[0];
		this.PlayerGridL = (Location[][]) temp[1];
		for (int y = 0; y < this.gridSize; y++) {
			for (int x = 0; x < this.gridSize; x++) {
				this.PlayerGridP.add(this.PlayerGridB[y][x]);
			}
		}
		temp = this.initGrid(this.gridSize, true);  // Grid 2
		this.AIGridB = (JButton[][]) temp[0];
		this.AIGridL = (Location[][]) temp[1];
		for (int y = 0; y < this.gridSize; y++) {
			for (int x = 0; x < this.gridSize; x++) {
				this.AIGridP.add(this.AIGridB[y][x]);
			}
		}

		this.setLocationRelativeTo(null);
	}

	/**
	 * Updates TitleL's display text. Is run upon frame creation.
	 *
	 * @param AIDiff The AI Difficulty
	 */
	private void setTitleL(int AIDiff) {
		System.out.println("Difficulty - " + (AIDiff == -1 ? "Sandbox" : (AIDiff == 0 ? "Realistic" : "Brutal")));
		TitleL.setText("Difficulty - " + (AIDiff == -1 ? "Sandbox" : (AIDiff == 0 ? "Realistic" : "Brutal")));
		setTitle("Difficulty - " + (AIDiff == -1 ? "Sandbox" : (AIDiff == 0 ? "Realistic" : "Brutal")));
	}

	/**
	 * Updates the Statistics displays.
	 */
	private void StatsUpdate() {
		this.PlayerSLTF.setText(Integer.toString(this.shipNos - this.PlayerStats[2]));
		this.PlayerSFTF.setText(Integer.toString(this.PlayerStats[0]));
		this.PlayerHitsTF.setText(Integer.toString(this.PlayerStats[1]));
		this.PlayerAccTF.setText(Double.toString(Math.round((this.PlayerStats[1] * 10000.0) / ((this.roundNo == 0) ? 1 : this.PlayerStats[0])) / 100.0) + " %");
		this.AISLTF.setText(Integer.toString((this.shipNos - this.AIStats[2])));
		this.AISFTF.setText(Integer.toString(this.AIStats[0]));
		this.AIHitsTF.setText(Integer.toString(this.AIStats[1]));
		this.AIAccTF.setText(Double.toString(Math.round((this.AIStats[1] * 10000.0) / ((this.roundNo == 0) ? 1 : this.AIStats[0])) / 100.0) + " %");
	}

	/**
	 * Initializes the buttons for Grid <code>gridNo</code>.
	 *
	 * @param gridSize Size of Game Board
	 * @param gridNo   Whether it's the 1st Grid or 2nd Grid
	 *
	 * @return An object array containing the JButton and Location arrays of gridNo.
	 */
	private Object[] initGrid(int gridSize, boolean gridNo) {
		JButton[][] GridB = new JButton[gridSize][gridSize];
		Location[][] GridL = new Location[gridSize][gridSize];

		for (int y = 0; y < gridSize; y++) {
			for (int x = 0; x < gridSize; x++) {
				GridB[y][x] = new JButton("");
				GridB[y][x].setPreferredSize(new Dimension(gridSize * 3, gridSize * 3));
				GridB[y][x].setActionCommand((gridNo ? 2 : 1) + " " + x + " " + y);
				GridB[y][x].setBackground(new Color(5, 218, 255, 255));
				GridB[y][x].addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent evt) {
						if (roundNo == Game.PLACE) {      // Checks if is the ship placement round
							if ((evt.getModifiers() & (ActionEvent.SHIFT_MASK | ActionEvent.CTRL_MASK)) != 0) { // Checks if SHIFT or CTRL was held down.
								System.out.println((((evt.getModifiers() & ActionEvent.SHIFT_MASK) != 0) ? "SHIFT" : "") + " " + (((evt.getModifiers() & ActionEvent.CTRL_MASK) != 0) ? "CTRL" : ""));
								placeShip(((JButton) evt.getSource()).getActionCommand(), true);  // Vertical
							} else {                   // SHIFT or CTRL weren't held down.
								placeShip(((JButton) evt.getSource()).getActionCommand(), false); // Horizontal
							}
							setBorders();
						} else if (roundNo != Game.END) { // Normal Round
							fire(((JButton) evt.getSource()).getActionCommand());
						}
						setColors();
					}
				});
				GridL[y][x] = new Location();
			}
		}

		return new Object[] {GridB, GridL};
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    Help1 = new JPanel();
    jLabel1 = new JLabel();
    Help2 = new JPanel();
    jLabel2 = new JLabel();
    Help3 = new JPanel();
    jLabel3 = new JLabel();
    TitleP = new JPanel();
    TitleL = new JLabel();
    MainP = new JPanel();
    PlayerL = new JLabel();
    PlayerGridP = new JPanel();
    AIL = new JLabel();
    AIGridP = new JPanel();
    RoundStatusP = new JPanel();
    RoundL = new JLabel();
    RoundTF = new JTextField();
    ShipsNoL = new JLabel();
    ShipNoTF = new JTextField();
    ShipsL = new JLabel();
    ShipsP = new JPanel();
    BattleshipCB = new JCheckBox();
    CruiserCB = new JCheckBox();
    DestroyerCB = new JCheckBox();
    CorvetteCB = new JCheckBox();
    ModeL = new JLabel();
    GirdL = new JLabel();
    GridTF = new JTextField();
    RoundStatusL = new JLabel();
    AlertsSP = new JScrollPane();
    AlertsTA = new JTextArea();
    ButtonsP = new JPanel();
    HelpB = new JButton();
    Spacer1L = new JLabel();
    NextB = new JButton();
    ClearB = new JButton();
    Spacer2L = new JLabel();
    ExitB = new JButton();
    ModeTF = new JTextField();
    StatusP = new JPanel();
    PlayerStatsP = new JPanel();
    PlayerStatusTitleL = new JLabel();
    PlayerrSLL = new JLabel();
    PlayerSLTF = new JTextField();
    PlayerSFL = new JLabel();
    PlayerSFTF = new JTextField();
    PlayerAccL = new JLabel();
    PlayerAccTF = new JTextField();
    PlayerHitsL = new JLabel();
    PlayerHitsTF = new JTextField();
    AIStatsP = new JPanel();
    AIStatusTitleL = new JLabel();
    AISLL = new JLabel();
    AISLTF = new JTextField();
    AISFL = new JLabel();
    AISFTF = new JTextField();
    AIAccL = new JLabel();
    AIAccTF = new JTextField();
    AIHitsL = new JLabel();
    AIHitsTF = new JTextField();

    jLabel1.setFont(new Font("Tahoma", 0, 12)); // NOI18N
    jLabel1.setText("<html> <body>  Between the grids assigned to the two players, there is a number of components that describe the settings selected by the user.<br><br>  Below that there is a textarea which logs the events happening throughout the match such as ship placement and also prompts if the user is doing something that is not allowed.<br><br>  Always keep a check on this to know what's going on at any time.    </body>  </html>");

    GroupLayout Help1Layout = new GroupLayout(Help1);
    Help1.setLayout(Help1Layout);
    Help1Layout.setHorizontalGroup(Help1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(Help1Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, 372, GroupLayout.PREFERRED_SIZE)
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    Help1Layout.setVerticalGroup(Help1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(Help1Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, 270, GroupLayout.PREFERRED_SIZE)
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    jLabel2.setFont(new Font("Tahoma", 0, 12)); // NOI18N
    jLabel2.setText("<html> <body> The ships can placed on the grid by clicking on a tile, by default the ships are placed horizontally towards the right from the selected tile.<br>  If you want to place the ships vertically downwards, then  shift/control + click on the tile you wish to start from.<br>  Check the textarea for which ship is placed.<br>  A placed ship or a shot can be removed by clicking on the tile once again. <br><br>  Tile color indication:<br> Sky/Light blue - Empty water<br> Yellow - Selected tile/tiles<br> Light grey - Ship border(only shown in placement round)<br> Dark grey - Placed ships<br> Dark blue - A missed shot<br> Red - Shot that hit the enemy ship<br><br>  Rules:<br> 1. No two ships can touch each other head on or horizontally but they can diagonally(the area is indicated around the ship in light grey border).<br>  2. After you have selected a tiles on the enemy grid, Click next  round to continue to the next round.<br>  3. Game ends when all of the ships of either one of the players are sunk.  4. From there, the user can play another match or exit to main menue.  </body> </html>");

    GroupLayout Help2Layout = new GroupLayout(Help2);
    Help2.setLayout(Help2Layout);
    Help2Layout.setHorizontalGroup(Help2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(Help2Layout.createSequentialGroup()
        .addGap(18, 18, 18)
        .addComponent(jLabel2, GroupLayout.PREFERRED_SIZE, 393, GroupLayout.PREFERRED_SIZE)
        .addContainerGap(26, Short.MAX_VALUE))
    );
    Help2Layout.setVerticalGroup(Help2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(GroupLayout.Alignment.TRAILING, Help2Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel2, GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE))
    );

    jLabel3.setFont(new Font("Tahoma", 0, 12)); // NOI18N
    jLabel3.setText("<html> <body>  start/next round/end button - This button can firstly, be used to start the match after placing ships, secondly, to enter the next round and lastly, to end the match(when someone wins) and move on to the post game screen stats screen.<br><br>  Clear Alters button - Clears the log in the alters textarea. <br><br>  Exit button  - quits the match and returns the user to main screen   </body> </html>");

    GroupLayout Help3Layout = new GroupLayout(Help3);
    Help3.setLayout(Help3Layout);
    Help3Layout.setHorizontalGroup(Help3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(Help3Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel3, GroupLayout.PREFERRED_SIZE, 338, GroupLayout.PREFERRED_SIZE)
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    Help3Layout.setVerticalGroup(Help3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(Help3Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel3, GroupLayout.PREFERRED_SIZE, 215, GroupLayout.PREFERRED_SIZE)
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    setTitle("JShip");
    setResizable(false);

    TitleL.setHorizontalAlignment(SwingConstants.CENTER);
    TitleL.setText("Difficulty - Sandbox");

    GroupLayout TitlePLayout = new GroupLayout(TitleP);
    TitleP.setLayout(TitlePLayout);
    TitlePLayout.setHorizontalGroup(TitlePLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(TitlePLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(TitleL, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addContainerGap())
    );
    TitlePLayout.setVerticalGroup(TitlePLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(TitlePLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(TitleL)
        .addContainerGap(14, Short.MAX_VALUE))
    );

    PlayerL.setHorizontalAlignment(SwingConstants.CENTER);
    PlayerL.setText("Player's Grid");

    PlayerGridP.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
    PlayerGridP.setLayout(new GridLayout(this.gridSize, this.gridSize, 0, 0));

    AIL.setHorizontalAlignment(SwingConstants.CENTER);
    AIL.setText("AI's Grid");

    AIGridP.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
    AIGridP.setLayout(new GridLayout(this.gridSize, this.gridSize, 0, 0));

    RoundL.setLabelFor(RoundTF);
    RoundL.setText("Round No.:");

    RoundTF.setEditable(false);
    RoundTF.setText("Ship Placement");

    ShipsNoL.setLabelFor(ShipNoTF);
    ShipsNoL.setText("<html>Total No. of<br>Ships  Available:</html>");

    ShipNoTF.setEditable(false);
    ShipNoTF.setText("5");

    ShipsL.setLabelFor(ShipsP);
    ShipsL.setText("Ships Available:");

    ShipsP.setLayout(new GridLayout(2, 2, 10, 0));

    BattleshipCB.setText("<html>Battleship<br/>5 Tiles; x1</html>");
    BattleshipCB.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        ShipCBClicked(evt);
      }
    });
    ShipsP.add(BattleshipCB);

    CruiserCB.setText("<html>Cruisers<br/>4 Tiles; x2</html>");
    CruiserCB.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        ShipCBClicked(evt);
      }
    });
    ShipsP.add(CruiserCB);

    DestroyerCB.setText("<html>Destroyers<br/>3 Tiles; x2</html>");
    DestroyerCB.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        ShipCBClicked(evt);
      }
    });
    ShipsP.add(DestroyerCB);

    CorvetteCB.setText("<html>Corvettes<br/>2 Tiles; x4</html>");
    CorvetteCB.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        ShipCBClicked(evt);
      }
    });
    ShipsP.add(CorvetteCB);

    ModeL.setLabelFor(ModeTF);
    ModeL.setText("Mode:");

    GirdL.setLabelFor(GridTF);
    GirdL.setText("Grid Size:");

    GridTF.setEditable(false);
    GridTF.setText("10 x 10");

    RoundStatusL.setHorizontalAlignment(SwingConstants.CENTER);
    RoundStatusL.setText("Round Status");

    AlertsTA.setEditable(false);
    AlertsTA.setColumns(30);
    AlertsTA.setRows(5);
    AlertsTA.setTabSize(2);
    AlertsTA.setText("Alerts:\n");
    AlertsTA.setToolTipText("");
    AlertsSP.setViewportView(AlertsTA);

    ButtonsP.setLayout(new GridLayout(2, 3, 0, 10));

    HelpB.setText("Help");
    HelpB.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        Help(evt);
      }
    });
    ButtonsP.add(HelpB);
    ButtonsP.add(Spacer1L);

    NextB.setText("Start");
    NextB.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        NextRound(evt);
      }
    });
    ButtonsP.add(NextB);

    ClearB.setText("<html><center>Clear<br/>Alerts</center></html>");
    ClearB.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        Clear(evt);
      }
    });
    ButtonsP.add(ClearB);
    ButtonsP.add(Spacer2L);

    ExitB.setText("Exit");
    ExitB.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        Exit(evt);
      }
    });
    ButtonsP.add(ExitB);

    ModeTF.setEditable(false);
    ModeTF.setText("Classic");

    GroupLayout RoundStatusPLayout = new GroupLayout(RoundStatusP);
    RoundStatusP.setLayout(RoundStatusPLayout);
    RoundStatusPLayout.setHorizontalGroup(RoundStatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(RoundStatusPLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(RoundStatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
          .addComponent(AlertsSP)
          .addComponent(ButtonsP, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE)
          .addComponent(ShipsP, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(RoundStatusL, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(ShipsL, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addGroup(GroupLayout.Alignment.TRAILING, RoundStatusPLayout.createSequentialGroup()
            .addGroup(RoundStatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
              .addComponent(GirdL, GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)
              .addComponent(ModeL, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(RoundStatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
              .addComponent(GridTF)
              .addComponent(ModeTF)))
          .addGroup(RoundStatusPLayout.createSequentialGroup()
            .addGroup(RoundStatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
              .addComponent(ShipsNoL)
              .addComponent(RoundL, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(RoundStatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
              .addComponent(ShipNoTF)
              .addComponent(RoundTF))))
        .addContainerGap())
    );
    RoundStatusPLayout.setVerticalGroup(RoundStatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(GroupLayout.Alignment.TRAILING, RoundStatusPLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(RoundStatusL)
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(RoundStatusPLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(GirdL)
          .addComponent(GridTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(RoundStatusPLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(ModeL)
          .addComponent(ModeTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(ShipsL)
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(ShipsP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(RoundStatusPLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
          .addComponent(ShipsNoL, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addComponent(ShipNoTF, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(RoundStatusPLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(RoundL, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
          .addComponent(RoundTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(AlertsSP)
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(ButtonsP, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
        .addContainerGap())
    );

    GroupLayout MainPLayout = new GroupLayout(MainP);
    MainP.setLayout(MainPLayout);
    MainPLayout.setHorizontalGroup(MainPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(MainPLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(MainPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
          .addComponent(PlayerL, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addGroup(MainPLayout.createSequentialGroup()
            .addComponent(PlayerGridP, GroupLayout.PREFERRED_SIZE, 450, GroupLayout.PREFERRED_SIZE)
            .addGap(0, 0, Short.MAX_VALUE)))
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(RoundStatusP, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(MainPLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
          .addComponent(AIGridP, GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
          .addComponent(AIL, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addContainerGap())
    );
    MainPLayout.setVerticalGroup(MainPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(MainPLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(MainPLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
          .addComponent(RoundStatusP, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addGroup(MainPLayout.createSequentialGroup()
            .addComponent(PlayerL)
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(PlayerGridP, GroupLayout.PREFERRED_SIZE, 450, GroupLayout.PREFERRED_SIZE))
          .addGroup(MainPLayout.createSequentialGroup()
            .addComponent(AIL)
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(AIGridP, GroupLayout.PREFERRED_SIZE, 450, GroupLayout.PREFERRED_SIZE)))
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    PlayerStatsP.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));

    PlayerStatusTitleL.setHorizontalAlignment(SwingConstants.CENTER);
    PlayerStatusTitleL.setText("Player's Statistics");

    PlayerrSLL.setText("Ships Left:");

    PlayerSLTF.setEditable(false);

    PlayerSFL.setText("Shots Fired:");

    PlayerSFTF.setEditable(false);

    PlayerAccL.setText("Accuracy:");

    PlayerAccTF.setEditable(false);

    PlayerHitsL.setText("Hits Landed:");

    PlayerHitsTF.setEditable(false);

    GroupLayout PlayerStatsPLayout = new GroupLayout(PlayerStatsP);
    PlayerStatsP.setLayout(PlayerStatsPLayout);
    PlayerStatsPLayout.setHorizontalGroup(PlayerStatsPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(PlayerStatsPLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(PlayerStatsPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
          .addComponent(PlayerStatusTitleL, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addGroup(PlayerStatsPLayout.createSequentialGroup()
            .addGroup(PlayerStatsPLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
              .addComponent(PlayerrSLL, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(PlayerAccL, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGap(18, 18, 18)
            .addGroup(PlayerStatsPLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
              .addComponent(PlayerSLTF, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
              .addComponent(PlayerAccTF, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
            .addGroup(PlayerStatsPLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
              .addComponent(PlayerHitsL, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(PlayerSFL, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGap(18, 18, 18)
            .addGroup(PlayerStatsPLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
              .addComponent(PlayerHitsTF, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
              .addComponent(PlayerSFTF, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))))
        .addContainerGap())
    );
    PlayerStatsPLayout.setVerticalGroup(PlayerStatsPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(PlayerStatsPLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(PlayerStatusTitleL)
        .addGap(18, 18, 18)
        .addGroup(PlayerStatsPLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(PlayerrSLL)
          .addComponent(PlayerSLTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addComponent(PlayerSFTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addComponent(PlayerSFL))
        .addGap(18, 18, 18)
        .addGroup(PlayerStatsPLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(PlayerAccL)
          .addComponent(PlayerAccTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addComponent(PlayerHitsL)
          .addComponent(PlayerHitsTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    AIStatsP.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));

    AIStatusTitleL.setHorizontalAlignment(SwingConstants.CENTER);
    AIStatusTitleL.setText("AI's Statistics");

    AISLL.setText("Ships Left:");

    AISLTF.setEditable(false);

    AISFL.setText("Shots Fired:");

    AISFTF.setEditable(false);

    AIAccL.setText("Accuracy:");

    AIAccTF.setEditable(false);

    AIHitsL.setText("Hits Landed:");

    AIHitsTF.setEditable(false);

    GroupLayout AIStatsPLayout = new GroupLayout(AIStatsP);
    AIStatsP.setLayout(AIStatsPLayout);
    AIStatsPLayout.setHorizontalGroup(AIStatsPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(AIStatsPLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(AIStatsPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
          .addComponent(AIStatusTitleL, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addGroup(AIStatsPLayout.createSequentialGroup()
            .addGroup(AIStatsPLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
              .addComponent(AISLL, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(AIAccL, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGap(18, 18, 18)
            .addGroup(AIStatsPLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
              .addComponent(AISLTF, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
              .addComponent(AIAccTF, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
            .addGroup(AIStatsPLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
              .addComponent(AIHitsL, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(AISFL, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGap(18, 18, 18)
            .addGroup(AIStatsPLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
              .addComponent(AIHitsTF, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
              .addComponent(AISFTF, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))))
        .addContainerGap())
    );
    AIStatsPLayout.setVerticalGroup(AIStatsPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(AIStatsPLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(AIStatusTitleL)
        .addGap(18, 18, 18)
        .addGroup(AIStatsPLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(AISLL)
          .addComponent(AISLTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addComponent(AISFL)
          .addComponent(AISFTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        .addGap(18, 18, 18)
        .addGroup(AIStatsPLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(AIAccL)
          .addComponent(AIHitsTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addComponent(AIHitsL)
          .addComponent(AIAccTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    GroupLayout StatusPLayout = new GroupLayout(StatusP);
    StatusP.setLayout(StatusPLayout);
    StatusPLayout.setHorizontalGroup(StatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(StatusPLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(PlayerStatsP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 101, Short.MAX_VALUE)
        .addComponent(AIStatsP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addContainerGap())
    );
    StatusPLayout.setVerticalGroup(StatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(GroupLayout.Alignment.TRAILING, StatusPLayout.createSequentialGroup()
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addGroup(StatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
          .addComponent(AIStatsP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addComponent(PlayerStatsP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        .addContainerGap())
    );

    GroupLayout layout = new GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
          .addComponent(TitleP, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(MainP, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(StatusP, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addContainerGap())
    );
    layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(TitleP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(MainP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(StatusP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

	/**
	 * What to do match completion. Also makes any undamaged enemy ships visible.
	 */
	private void end() {
		this.RoundTF.setText(this.roundNo + " - End");
		this.roundNo = Game.END;
		this.NextB.setText("End");
		this.HelpB.setEnabled(false);
		this.ClearB.setEnabled(false);
		this.ExitB.setEnabled(false);

		if (this.status == Game.WIN) {         // Checks if the user won
			System.out.println("Match Won!");
			this.TitleL.setText("Match Won!");
			this.AlertsTA.append("Match Won!\n");
			this.setTitle("Match Won!");
		} else if (this.status == Game.LOSE) { // Checks if the user lost
			System.out.println("Match Lost!");
			this.TitleL.setText("Match Lost!");
			this.AlertsTA.append("Match Lost!\n");
			this.setTitle("Match Lost!");
		} else {                               // Should not have happened.
			System.out.println("Match Ended!");
			this.TitleL.setText("Match Ended!");
			this.AlertsTA.append("Match Ended!\n");
			this.setTitle("Match Ended!");
		}

		this.setColors();
	}

	/**
	 * Exits to the main menu.
	 *
	 * @param evt Button Click
	 */
	private void Exit(ActionEvent evt) {//GEN-FIRST:event_Exit
		if (JOptionPane.showConfirmDialog(null, "Are you sure?", "Abandon Ship?", JOptionPane.YES_NO_OPTION) == 0) {
			JShip JShip = new JShip(); // Creates the JShip Form object
			JShip.setVisible(true);    // Makes the JShip Form to be visible

			this.dispose();            // Destroys the current form object
		}
	}//GEN-LAST:event_Exit

	/**
	 * If match ended, go to Post.
	 * If ship placement, setup player ships, enemy AI, and AI ships.
	 * Otherwise, run <code>this.shoot()</code> if the required number of shot selections have been made.
	 *
	 * @param evt Button Click
	 */
	private void NextRound(ActionEvent evt) {//GEN-FIRST:event_NextRound
		if (this.roundNo == Game.END) {           // Checks if the match ended.
			int[] statsList = new int[] {this.status, this.PlayerStats[0], this.PlayerStats[1], this.AIStats[1], this.AIStats[2], this.PlayerStats[2]};

			Post Post = new Post(statsList, this.mode, this.AIDiff);
			Post.setVisible(true);

			this.dispose();
		} else if (this.roundNo == Game.PLACE) {  // Checks if it's the Ship Placement round.
			if (this.shipPlacing == this.shipNos) { // Checks if all ships have been placed.
				this.buttonsClicked = new boolean[this.gridSize][this.gridSize]; // Resets the array to all false.

				if (this.AIDiff == -1) {       // Initializes the AI to Sandbox.
					this.AI = new Sandbox(this.initVars, this.PlayerGridL, this.PlayerShips);
				} else if (this.AIDiff == 0) { // Initializes the AI to Regular.
					this.AI = new Regular(this.initVars, this.PlayerGridL, this.PlayerShips);
				} else {                       // Initializes the AI to Brutal.
					this.AI = new Brutal(this.initVars, this.PlayerGridL, this.PlayerShips);
				}

				this.AIGridL = this.AI.getGridSelf();
				this.AIShips = this.AI.getShipsSelf();
				for (int ship = 0; ship < this.shipNos; ship++) {
					int[] xy = this.AIShips[ship].getStart();

					if (CurrentUser.getCurrentUser().equals("admin")) { // Used for debugging.
						System.out.println("AI Ship at: " + xy[0] + " " + xy[1]);
					}
				}

				this.roundNo++;
				this.RoundTF.setText(Integer.toString(this.roundNo));
				this.NextB.setText("<html><center>Next<br/>Round</center></html>");
				this.StatsUpdate();
				this.setColors();
			} else {                                  // Not all ships placed.
				this.AlertsTA.append("Not all ships placed! " + (this.shipNos - this.shipPlacing) + " ships left!\n");
			}
		} else {                                    // Normal/Firing Rounds
			// Used for checking if the required number of selections have been made
			if (this.mode.equals("C")) { // Checks if it a Classic Round.
				if (this.shotsSelected == 1) {
					this.shoot();
				} else {
					this.AlertsTA.append("Not all selections made! 1 selection left!\n");
				}
			} else {                     // Checks if it a Salvo Round.
				if (this.shotsSelected == (this.shipNos - this.PlayerStats[2])) {
					this.shoot();
				} else {
					// Checks if the number of selectable locations doesn't exceed the number of selections to be required.
					int left = 0;
					int required = (this.shipNos - this.PlayerStats[2] - this.shotsSelected);

					for (int y = 0; y < this.gridSize; y++) {
						for (int x = 0; x < this.gridSize; x++) {
							if (this.AIGridL[y][x].isUnguessed()) {
								left++;
							}
						}
					}

					if (left > required) {
						this.AlertsTA.append("Not all selections made! " + required + " selections left!\n");
					} else {
						this.shoot();
					}
				}
			}
		}
	}//GEN-LAST:event_NextRound

	/**
	 * Marks the user's shots.
	 * Checks if user has won.
	 * If the user has won, set match to have ended.
	 * If the user hasn't won, the AI shoots and checks if the AI has won.
	 * If the AI has won, set match to have ended.
	 * If neither the user nor the AI have won, advances the game to the next round.
	 */
	private void shoot() {
		shotChecker:
		for (int y = 0; y < this.gridSize; y++) {
			for (int x = 0; x < this.gridSize; x++) {
				if (this.buttonsClicked[y][x]) {    // Checks if a shot was placed here.
					this.buttonsClicked[y][x] = false;
					this.AIGridL[y][x].markShot();
					this.PlayerStats[0]++;

					if (this.AIGridL[y][x].isHit()) { // Checks if a Ship was hit.
						this.AlertsTA.append("Enemy Ship hit!\n");
						this.PlayerStats[1]++;

						shipChecker:
						for (int ship = 0; ship < this.shipNos; ship++) {              // Used for finding which ship was hit.
							if (this.AIShips[ship].getPosition(new int[] {x, y}) > -1) { // Checks if this ship was hit
								this.AIShips[ship].sectionHit(new int[] {x, y});           // Marks the section as hit.

								if (this.AIShips[ship].isSunk()) {                         // Checks if the ship was sunk due to this shot.
									if (this.AIShips[ship].length == 5) {
										this.AlertsTA.append("Enemy Battleship sunk!\n");
									} else if (this.AIShips[ship].length == 4) {
										this.AlertsTA.append("Enemy Cruiser sunk!\n");
									} else if (this.AIShips[ship].length == 3) {
										this.AlertsTA.append("Enemy Destroyer sunk!\n");
									} else {
										this.AlertsTA.append("Enemy Corvette sunk!\n");
									}

									this.AIStats[2]++;
									if (this.shipNos - this.AIStats[2] == 0) {               // Checks if the user won (allows 100% accuracy scores).
										break shotChecker;
									}
								}

								break shipChecker;
							}
						}
					}
				}
			}
		}

		this.shotsSelected = 0;
		this.StatsUpdate();
		this.setColors();

		if (this.shipNos == this.AIStats[2]) {       // Checks if the user won.
			this.status = Game.WIN;
			this.end();
		} else {                                     // User did not win.
			this.AI.updateGridSelf(this.AIGridL);      // Updates the AI's "self" grid
			int[][] shotsSunk = this.fireAI();         // AI Shoots.
			if (this.AIDiff == 0) {                    // Updates the Regular AI's "critical" shots list
				this.AI.updateShotsSunk(shotsSunk);
			} else if (this.AIDiff == 1) {             // Updates the Brutal AI's Player's ship list
				this.AI.updateShipsOpp(PlayerShips);
			}
			this.AI.updateGridOpp(this.PlayerGridL);   // Updates the AI's hostile grid. Placed after cause of how the Regular AI works.
			this.StatsUpdate();
			this.setColors();

			if (this.shipNos == this.PlayerStats[2]) { // Checks if the AI won.
				this.status = Game.LOSE;
				this.end();
			} else {                                   // AI did not win. Next round.
				this.roundNo++;
				this.RoundTF.setText(Integer.toString(this.roundNo));
			}

		}
	}

	/**
	 * A series of <code>JOptionPane.MessageDialog</code>-s for explaining the game.
	 * TODO: Need to write this.
	 *
	 * @param evt Button Click
	 */
	private void Help(ActionEvent evt) {//GEN-FIRST:event_Help
		boolean[] responses = {false, false, false};

		one:
		while (true) {
			responses[0] = JOptionPane.showConfirmDialog(null, this.Help1, "Help - 1", JOptionPane.YES_NO_OPTION) == 0;
			two:
			while (responses[0]) {   // One   -> Two
				responses[1] = JOptionPane.showConfirmDialog(null, this.Help2, "Help - 2", JOptionPane.YES_NO_OPTION) == 0;
				three:
				while (responses[1]) { // Two   -> Three
					responses[2] = JOptionPane.showConfirmDialog(null, this.Help3, "Help - 3", JOptionPane.YES_NO_OPTION) == 0;
					if (responses[2]) {  // Three -> Game
						return;
					} else {             // Three -> Two
						continue two;
					}
				}
				continue one;          // Two   -> One
			}
			return;                  // One   -> Game
		}
	}//GEN-LAST:event_Help

	/**
	 * Sets all ShipP CheckBoxes to their desired state every time one of them is clicked.
	 * Also called during the constructor.
	 *
	 * @param evt Button Click
	 */
	private void ShipCBClicked(ActionEvent evt) {//GEN-FIRST:event_ShipCBClicked
		this.BattleshipCB.setSelected(this.initVars[1]);
		this.CruiserCB.setSelected(this.initVars[2]);
		this.DestroyerCB.setSelected(this.initVars[3]);
		this.CorvetteCB.setSelected(this.initVars[4]);
		this.ShipNoTF.setText(Integer.toString(this.shipNos));
	}//GEN-LAST:event_ShipCBClicked

	/**
	 * Clears the alerts/notifications text area.
	 *
	 * @param evt Button Click
	 */
  private void Clear(ActionEvent evt) {//GEN-FIRST:event_Clear
		AlertsTA.setText("Alerts:\n");
  }//GEN-LAST:event_Clear

	/**
	 * Used for placing ships.
	 *
	 * @param coords    the coordinates of the button clicked
	 * @param direction orientation of the ship to be placed.
	 *                  true: Vertical ; false: Horizontal
	 */
	private void placeShip(String coords, boolean direction) {
		int[] xy = this.extractCoordinates(coords);
		int length = this.PlayerShips[this.shipPlacing - (this.shipPlacing == this.shipNos ? 1 : 0)].length;

		if (coords.charAt(0) == '1') {             // Checks if the button clicked is from Grid 1.
			if (this.buttonsClicked[xy[1]][xy[0]]) { // Checks if the button has already been clicked.
				boolean flag = false;                    // Boolean flag to check if a ship was matched.
				int ship = 0;                            // The ship to which the given coordinates belong to.
				for (int s = 0; s < this.shipNos; s++) { // Checks which ship the given coordinates belong to.
					if (this.PlayerShips[s].getPosition(xy) > -1) {
						ship = s;
						flag = true;
						break;                               // Ship determined.
					}
				}
				if (!flag) {                             // Checks if a ship wasn't matched.
					System.out.println("ERROR!! No ship matched!");
					return;
				}

				length = this.PlayerShips[ship].length;              // Length
				int[] start = this.PlayerShips[ship].getStart();     // Starting Coordinates
				boolean dir = this.PlayerShips[ship].getDirection(); // Direction
				flag = false;                                        // Boolean flag to check if any other ship is present
				for (int l = 0; l < length; l++) {
					this.PlayerGridL[start[1] + (dir ? l : 0)][start[0] + (dir ? 0 : l)].shipAbsent();
					this.buttonsClicked[start[1] + (dir ? l : 0)][start[0] + (dir ? 0 : l)] = false;
				}
				this.PlayerShips[ship].remove();    // "Removes" the ship.

				this.shipPlacing = this.nextShip(); // Determines the next ship to place.
			} else {                                 // Button hasn't been clicked.
				if (this.shipPlacing < this.shipNos) { // Checks if the number of ships placed is less than the max ships available.
					for (int l = 0; l < length; l++) {   // Checks for intersections, direct contact, and if the ship is within the board.
						if (xy[1] + (direction ? l : 0) >= this.gridSize || xy[0] + (direction ? 0 : l) >= this.gridSize) {
							// Out of bounds!
							System.out.println("Ship out of bounds at position: " + (l + 1));
							this.AlertsTA.append("Ship out of bounds at position: " + (l + 1) + "\n");
							return;
						} else if (this.buttonsClicked[xy[1] + (direction ? l : 0)][xy[0] + (direction ? 0 : l)]) {
							// An intersection occurred!
							System.out.println("Ship exists at position: " + (l + 1));
							this.AlertsTA.append("Ship exists at position: " + (l + 1) + "\n");
							return;
						} else if (this.PlayerGridL[xy[1] + (direction ? l : 0)][xy[0] + (direction ? 0 : l)].isBorder()) {
							// Contact with another ship!
							System.out.println("Bordering another ship at position: " + (l + 1));
							this.AlertsTA.append("Bordering another ship at position:: " + (l + 1) + "\n");
							return;
						}
					}
					// No intersections

					for (int l = 0; l < length; l++) {
						this.PlayerGridL[xy[1] + (direction ? l : 0)][xy[0] + (direction ? 0 : l)].shipPresent();
						this.buttonsClicked[xy[1] + (direction ? l : 0)][xy[0] + (direction ? 0 : l)] = true;
					}
					this.PlayerShips[this.shipPlacing].add(xy, direction);

					this.shipPlacing = this.nextShip();     // Determines the next ship to place.
					if (this.shipPlacing == this.shipNos) { // Checks if all ships were placed.
						this.AlertsTA.append("All ships placed!\n");
					}
				} else {                                  // More ships than limit allowed added.
					this.AlertsTA.append("Ship limit reached!\n");
				}
			}
		} else {                                      // Grid 2.
			this.AlertsTA.append("Wrong grid!\n");
		}
	}

	/**
	 * Manages what to do if a button was clicked.
	 *
	 * @param coords the coordinates of the button clicked.
	 */
	private void fire(String coords) {
		int[] xy = this.extractCoordinates(coords);
		System.out.println("Player clicked at: " + xy[0] + " " + xy[1]);

		if (coords.charAt(0) == '2') {                    // Checks if the button clicked is from Grid 2.
			if (this.AIGridL[xy[1]][xy[0]].isUnguessed()) { // Checks if the location is unguessed.
				if (this.buttonsClicked[xy[1]][xy[0]]) {      // Checks if the button has already been clicked.
					this.buttonsClicked[xy[1]][xy[0]] = false;
					this.shotsSelected--;
				} else {                                      // Button hasn't been clicked.
					if (this.mode.equals("C")) {                // Checks if the match is in Classic Mode.
						if (this.shotsSelected == 0) {            // Checks if another location hasn't been selected.
							this.buttonsClicked[xy[1]][xy[0]] = true;
							this.shotsSelected++;
						} else {                                  // Another location has been selected.
							this.AlertsTA.append("Maximum locations selected!\n");
						}
					} else {                                    // Salvo Mode.
						if (this.shotsSelected < (this.shipNos - this.PlayerStats[2])) { // Checks if max locations haven't been selected.
							this.buttonsClicked[xy[1]][xy[0]] = true;
							this.shotsSelected++;

							if (this.shotsSelected == (this.shipNos - this.PlayerStats[2])) {
								this.AlertsTA.append("All firing locations selected!\n");
							}
						} else {                                  // Max locations have been selected.
							this.AlertsTA.append("Maximum firing locations selected!\n");
						}
					}
				}
			} else {                                        // Already guessed.
				this.AlertsTA.append("This location is already guessed!\n");
			}
		} else {                                          // Grid 1.
			this.AlertsTA.append("Wrong grid!\n");
		}
	}

	/**
	 * Function for handling the AI input.
	 *
	 * @return an integer array having the coordinates of the shots which sunk a ship.
	 *         The 1st value within any element is the X-Coordinate; the 2nd value is the Y-Coordinate.
	 */
	private int[][] fireAI() {
		int[][] shotsSunk = new int[4][2];                             // Coordinates of the shots which sunk a ship.
		int sunk;
		for (sunk = 0; sunk < shotsSunk.length; sunk++) {              // Initialises shotsSunk to {-1, -1} values.
			shotsSunk[sunk] = new int[] {-1, -1};
		}
		sunk = 0;

		int numShots = (this.mode.equals("C") ? 1 : (this.shipNos - this.AIStats[2]));
		int[][] xy = new int[numShots][2];                             // Coordinates of the shots

		for (int shot = 0; shot < numShots; shot++) {                  // Obtains the firing coordinates. In a separate loop to prevent the regular AI from knowing the hit status of previous shots.
			xy[shot] = this.AI.fire();
			System.out.println("AI fires at: " + xy[shot][0] + " " + xy[shot][1]);
		}

		for (int shot = 0; shot < numShots; shot++) {                  // Marks the locations and updates statistics.
			this.PlayerGridL[xy[shot][1]][xy[shot][0]].markShot();
			this.AIStats[0]++;

			if (this.PlayerGridL[xy[shot][1]][xy[shot][0]].isHit()) {    // Checks if a Ship was hit.
				this.AIStats[1]++;

				shipChecker:
				for (int ship = 0; ship < this.shipNos; ship++) {          // Used for finding which ship was hit.
					if (this.PlayerShips[ship].getPosition(xy[shot]) > -1) {
						this.PlayerShips[ship].sectionHit(xy[shot]);           // Marks the section as hit.

						if (this.PlayerShips[ship].isSunk()) {                 // Checks if the ship was sunk.
							shotsSunk[sunk] = xy[shot];
							sunk++;

							this.PlayerStats[2]++;
							if (this.shipNos == this.PlayerStats[2]) {           // Checks if the AI won.
								return shotsSunk;
							}
						}

						break shipChecker;
					}
				}
			}
		}

		return shotsSunk;
	}

	/**
	 * Determines which ship to place next, since ships can be removed from the grid.
	 * Returns the number of ships if all ships have been placed.
	 *
	 * @return an integer equivalent to the ship number to be placed.
	 */
	private int nextShip() {
		for (int ship = 0; ship < this.shipNos; ship++) {
			if (!this.PlayerShips[ship].isPlaced()) {
				this.AlertsTA.append("Placing ");
				if (this.PlayerShips[ship].length == 5) {        // Battleship
					this.AlertsTA.append("Placing Battleship - 5");
				} else if (this.PlayerShips[ship].length == 4) { // Cruiser
					this.AlertsTA.append("Placing Cruiser - 4");
				} else if (this.PlayerShips[ship].length == 3) { // Destroyer
					this.AlertsTA.append("Placing Destroyer - 3");
				} else {                                         // Corvette
					this.AlertsTA.append("Placing Corvette - 2");
				}
				this.AlertsTA.append(" spaces.\n");

				return ship;                                     // Ship determined.
			}
		}

		return this.shipNos;                                 // All ships placed.
	}

	/**
	 * Sets the border property for each tile.
	 * TODO: Make it more efficient (not requiring to remove all borders at the start).
	 */
	private void setBorders() {
		// Removes all borders
		for (int y = 0; y < this.gridSize; y++) {
			for (int x = 0; x < this.gridSize; x++) {
				if (this.PlayerGridL[y][x].isBorder()) {
					this.PlayerGridL[y][x].noBordersShip();
				}
			}
		}

		// Adds borders on a ship-by-ship basis (identical to the border setting code in AI.place()).
		for (int ship = 0; ship < this.shipNos; ship++) {
			if (this.PlayerShips[ship].isPlaced()) { // Checks if the ship has been placed.
				int shipLength = this.PlayerShips[ship].length;            // Length
				int[] start = this.PlayerShips[ship].getStart();           // Starting Coordinates
				boolean direction = this.PlayerShips[ship].getDirection(); // Direction

				for (int l = 0; l < shipLength; l++) {
					// Sets the borders at the terminal positions of the ship.
					if ((l == 0) && (start[direction ? 1 : 0] != 0)) {                                                      // Checks if the 1st tile isn't at the edge of the Board
						this.PlayerGridL[start[1] - (direction ? 1 : 0)][start[0] - (direction ? 0 : 1)].bordersShip();
					} else if ((l == shipLength - 1) && (start[direction ? 1 : 0] + shipLength - 1 != this.gridSize - 1)) { // Checks if the last tile isn't at the edge of the Board
						this.PlayerGridL[start[1] + (direction ? shipLength : 0)][start[0] + (direction ? 0 : shipLength)].bordersShip();
					}
					// Sets the borders along the length of the ship
					if (start[direction ? 0 : 1] == 0) {                        // At the Top/Left edge
						this.PlayerGridL[start[1] + (direction ? l : 1)][start[0] + (direction ? 1 : l)].bordersShip();
					} else if (start[direction ? 0 : 1] == this.gridSize - 1) { // At the Bottom/Right edge
						this.PlayerGridL[start[1] + (direction ? l : -1)][start[0] + (direction ? -1 : l)].bordersShip();
					} else {                                                    // Not at the edges
						this.PlayerGridL[start[1] + (direction ? l : -1)][start[0] + (direction ? -1 : l)].bordersShip();
						this.PlayerGridL[start[1] + (direction ? l : 1)][start[0] + (direction ? 1 : l)].bordersShip();
					}
				}
			}
		}
	}

	/**
	 * Sets the colors of all buttons in both grids.
	 */
	private void setColors() {
		for (int y = 0; y < this.gridSize; y++) {
			for (int x = 0; x < this.gridSize; x++) {
				// Player Grid
				this.PlayerGridB[y][x].setBackground(new Color(5, 218, 255, 255)); // Base Colour

				if (this.PlayerGridL[y][x].hasShip()) {         // Checks if the current location has a ship.
					this.PlayerGridB[y][x].setBackground(new Color(67, 70, 75, 255));
				}
				if (this.roundNo == Game.PLACE) {               // Checks if the current round is the ship placement round. Border is not to be shown otherwise.
					if (this.PlayerGridL[y][x].isBorder()) {      // Checks if the current location borders a ship.
						this.PlayerGridB[y][x].setBackground(new Color(176, 196, 222, 255));
					}
					if (this.buttonsClicked[y][x]) {              // Checks if the current location has been clicked.
						this.PlayerGridB[y][x].setBackground(new Color(242, 236, 0, 255));
					}
				} else {                                        // Not ship placement round.
					if (this.PlayerGridL[y][x].isHit()) {         // Checks if the current location has been hit.
						this.PlayerGridB[y][x].setBackground(new Color(205, 0, 0, 255));
					} else if (this.PlayerGridL[y][x].isMiss()) { // No ship present, and hence no hit.
						this.PlayerGridB[y][x].setBackground(new Color(0, 0, 128, 255));
					}
				}

				// AI Grid
				this.AIGridB[y][x].setBackground(new Color(5, 218, 255, 255)); // Base Colour

				if (CurrentUser.getCurrentUser().equals("admin") || this.roundNo == -1) { // Cheats for the admin account for "debugging", or when the game ends.
					if (this.AIGridL[y][x].hasShip()) {                                     // Checks if the current location has a ship.
						this.AIGridB[y][x].setBackground(new Color(67, 70, 75, 255));
					}
				}
				if (this.AIGridL[y][x].isHit()) {                             // Checks if the current location has been hit.
					this.AIGridB[y][x].setBackground(new Color(205, 0, 0, 255));
				} else if (this.AIGridL[y][x].isMiss()) {                     // No ship present, and hence no hit.
					this.AIGridB[y][x].setBackground(new Color(0, 0, 128, 255));
				}
				if (this.roundNo > Game.PLACE && this.buttonsClicked[y][x]) { // Checks if the current location has been clicked.
					this.AIGridB[y][x].setBackground(new Color(242, 236, 0, 255));
				}
			}
		}
	}

	/**
	 * Extracts the X- and Y-Coordinates form the Grid JButton action command String format:
	 * <code>grid_no x_coordinate y_coordinate</code>
	 *
	 * @param coords Coordinates of the Button
	 *
	 * @return An integer array of 2 integers. 1st integer is X-Coordinate. 2nd integer is Y-Coordinate.
	 */
	private int[] extractCoordinates(String coords) {
		int[] xy = new int[2];               // X-, Y-Coordinates

		if (this.gridSize == 10) {           // Checks if the board size is 10 x 10. 1 digit for each coordinate.
			xy[0] = Integer.parseInt(Character.toString(coords.charAt(2)));
			xy[1] = Integer.parseInt(Character.toString(coords.charAt(4)));
		} else {                             // 15 x 15 Grid
			if (coords.length() == 5) {        // 1 digit for each coordinate
				xy[0] = Integer.parseInt(Character.toString(coords.charAt(2)));
				xy[1] = Integer.parseInt(Character.toString(coords.charAt(4)));
			} else if (coords.length() == 6) { // 1 digit for 1 coordinate. 2 digits for the other.
				if (coords.charAt(3) == ' ') {   // Y-Coordinate has 2 digits
					xy[0] = Integer.parseInt(Character.toString(coords.charAt(2)));
					xy[1] = 10 + Integer.parseInt(Character.toString(coords.charAt(5)));
				} else {                         // X-Coordinate has 2 digits
					xy[0] = 10 + Integer.parseInt(Character.toString(coords.charAt(3)));
					xy[1] = Integer.parseInt(Character.toString(coords.charAt(5)));
				}
			} else {                           // 2 digits for each coordinate
				xy[0] = 10 + Integer.parseInt(Character.toString(coords.charAt(3)));
				xy[1] = 10 + Integer.parseInt(Character.toString(coords.charAt(6)));
			}
		}

		return xy;
	}

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private JLabel AIAccL;
  private JTextField AIAccTF;
  private JPanel AIGridP;
  private JLabel AIHitsL;
  private JTextField AIHitsTF;
  private JLabel AIL;
  private JLabel AISFL;
  private JTextField AISFTF;
  private JLabel AISLL;
  private JTextField AISLTF;
  private JPanel AIStatsP;
  private JLabel AIStatusTitleL;
  private JScrollPane AlertsSP;
  private JTextArea AlertsTA;
  private JCheckBox BattleshipCB;
  private JPanel ButtonsP;
  private JButton ClearB;
  private JCheckBox CorvetteCB;
  private JCheckBox CruiserCB;
  private JCheckBox DestroyerCB;
  private JButton ExitB;
  private JLabel GirdL;
  private JTextField GridTF;
  private JPanel Help1;
  private JPanel Help2;
  private JPanel Help3;
  private JButton HelpB;
  private JPanel MainP;
  private JLabel ModeL;
  private JTextField ModeTF;
  private JButton NextB;
  private JLabel PlayerAccL;
  private JTextField PlayerAccTF;
  private JPanel PlayerGridP;
  private JLabel PlayerHitsL;
  private JTextField PlayerHitsTF;
  private JLabel PlayerL;
  private JLabel PlayerSFL;
  private JTextField PlayerSFTF;
  private JTextField PlayerSLTF;
  private JPanel PlayerStatsP;
  private JLabel PlayerStatusTitleL;
  private JLabel PlayerrSLL;
  private JLabel RoundL;
  private JLabel RoundStatusL;
  private JPanel RoundStatusP;
  private JTextField RoundTF;
  private JTextField ShipNoTF;
  private JLabel ShipsL;
  private JLabel ShipsNoL;
  private JPanel ShipsP;
  private JLabel Spacer1L;
  private JLabel Spacer2L;
  private JPanel StatusP;
  private JLabel TitleL;
  private JPanel TitleP;
  private JLabel jLabel1;
  private JLabel jLabel2;
  private JLabel jLabel3;
  // End of variables declaration//GEN-END:variables
	// Start of custom GUI variables declaration
	private JButton[][] PlayerGridB;
	private JButton[][] AIGridB;
	// End of custom GUI variables decralation
}
