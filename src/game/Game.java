package game;

import game.ai.*;
import game.grid.Location;
import game.grid.Ship;
import java.awt.Color;
import java.awt.Dimension;
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
	 * if initVars[0] is true, boardSize is 15. Else 10.
	 */
	private final int boardSize;

	/**
	 * Total number of ships available in this match.
	 */
	private int shipNos = 0;

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
	 * Internal counter for current game/match round/sequence.
	 * <pre>
	 * -1  - Game Ended
	 * 0   - Ship Placement
	 * &gt; 0 - Actual Game (until it ends).
	 * </pre>
	 */
	private int roundNo = 0;

	/**
	 * An integer indicating whether or not the given round was won by the user.
	 * <pre>
	 * 1 - Win
	 * 2 - Lose
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
	 * Temporary variable for storing the coordinates of the button clicked of the ship's initial point.
	 * Only used during the Ship Placement round.
	 * 0 : X-Coordinate
	 * 1 : Y-Coordinate
	 */
	int[] initLoc = {-1, -1};

	/**
	 * Temporary variable for storing the coordinates of the button clicked of the ship's final point.
	 * Only used during the Ship Placement round.
	 * 0 : X-Coordinate
	 * 1 : Y-Coordinate
	 */
	int[] finalLoc = {-1, -1};

	/**
	 * Temporary variable for storing the direction of the ship placement.
	 * Only used during the Ship Placement round.
	 * <pre>
	 * true  : Vertical
	 * false : Horizontal
	 * </pre>
	 */
	boolean direction = true;

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

		this.boardSize = initVars[0] ? 15 : 10;
		this.buttonsClicked = new boolean[this.boardSize][this.boardSize];

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

		Object[] temp;                               // Temporary Array for storing Grid 1 & Grid 2 objects.
		temp = this.initGrid(this.boardSize, false); // Grid 1
		this.PlayerGridB = (JButton[][]) temp[0];
		this.PlayerGridL = (Location[][]) temp[1];
		for (int y = 0; y < this.boardSize; y++) {
			for (int x = 0; x < this.boardSize; x++) {
				this.PlayerGridP.add(this.PlayerGridB[y][x]);
			}
		}
		temp = this.initGrid(this.boardSize, true);  // Grid 2
		this.AIGridB = (JButton[][]) temp[0];
		this.AIGridL = (Location[][]) temp[1];
		for (int y = 0; y < this.boardSize; y++) {
			for (int x = 0; x < this.boardSize; x++) {
				this.AIGridP.add(this.AIGridB[y][x]);
			}
		}
		// Implicit Garbage Collecting temp (hopefully). Remove if not necessary.
		temp = null;
		System.gc();

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
		this.PlayerSLTF.setText("" + (this.shipNos - this.PlayerStats[2]));
		this.PlayerSFTF.setText("" + this.PlayerStats[0]);
		this.PlayerHitsTF.setText("" + this.PlayerStats[1]);
		this.PlayerAccTF.setText("" + (Math.round((this.PlayerStats[1] * 10000.0) / ((this.roundNo == 0) ? 1 : this.PlayerStats[0])) / 100.0) + " %");
		this.AISLTF.setText("" + (this.shipNos - this.AIStats[2]));
		this.AISFTF.setText("" + this.AIStats[0]);
		this.AIHitsTF.setText("" + this.AIStats[1]);
		this.AIAccTF.setText("" + (Math.round((this.AIStats[1] * 10000.0) / ((this.roundNo == 0) ? 1 : this.AIStats[0])) / 100.0) + " %");
	}

	/**
	 * Initializes the buttons for Grid <code>gridNo</code>.
	 *
	 * @param boardSize Size of Game Board
	 * @param gridNo    Whether it's the 1st Grid or 2nd Grid
	 *
	 * @return An object array containing the JButton and Location arrays of gridNo.
	 */
	private Object[] initGrid(int boardSize, boolean gridNo) {
		JButton[][] GridB = new JButton[boardSize][boardSize];
		Location[][] GridL = new Location[boardSize][boardSize];

		for (int y = 0; y < boardSize; y++) {
			for (int x = 0; x < boardSize; x++) {
				GridB[y][x] = new JButton("");
				GridB[y][x].setPreferredSize(new Dimension(boardSize * 3, boardSize * 3));
				GridB[y][x].setActionCommand((gridNo ? 2 : 1) + " " + x + " " + y);
				GridB[y][x].setBackground(new Color(5, 218, 255, 255));
				GridB[y][x].addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent evt) {
						if (roundNo == 0) {         // Checks if is the ship placement round
							if ((evt.getModifiers() & (ActionEvent.SHIFT_MASK | ActionEvent.CTRL_MASK)) != 0) { // Checks if SHIFT or CTRL was held down.
								System.out.println((((evt.getModifiers() & ActionEvent.SHIFT_MASK) != 0) ? "SHIFT" : "") + " " + (((evt.getModifiers() & ActionEvent.CTRL_MASK) != 0) ? "CTRL" : ""));
								placeShip((JButton) evt.getSource(), true);  // Vertical
							} else {                  // SHIFT or CTRL weren't held down.
								placeShip((JButton) evt.getSource(), false); // Horizontal
							}
						} else if (roundNo != -1) { // Normal Round
							fire((JButton) evt.getSource());
						}
					}
				});
				GridL[y][x] = new Location();
			}
		}

		return new Object[] {GridB, GridL};
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

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

    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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
    PlayerGridP.setLayout(new GridLayout(this.boardSize, this.boardSize, 0, 0));

    AIL.setHorizontalAlignment(SwingConstants.CENTER);
    AIL.setText("AI's Grid");

    AIGridP.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
    AIGridP.setLayout(new GridLayout(this.boardSize, this.boardSize, 0, 0));

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

    BattleshipCB.setText("<html>Battleships<br/>5 Tiles; x1</html>");
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
		this.roundNo = -1;
		this.NextB.setText("End");
		this.HelpB.setEnabled(false);
		this.ClearB.setEnabled(false);
		this.ExitB.setEnabled(false);

		this.AlertsTA.append("\nMatch Ended!");

		for (int y = 0; y < this.boardSize; y++) {
			for (int x = 0; x < this.boardSize; x++) {
				if (this.AIGridL[y][x].hasShip() && !this.AIGridL[y][x].isHit()) {
					this.AIGridB[y][x].setBackground(new Color(176, 196, 222, 255));
				}
			}
		}
	}

	/**
	 * Exits to the main menu.
	 *
	 * @param evt Button Click
	 */
	private void Exit(ActionEvent evt) {//GEN-FIRST:event_Exit
		if (JOptionPane.showConfirmDialog(null, "Are you a wuss?", "Abandon Ship?", JOptionPane.YES_NO_OPTION) == 0) {
			JShip JShip = new JShip(); // Creates the JShip Form object
			JShip.setVisible(true);	   // Makes the JShip Form to be visible

			this.dispose();            // Destroys the current form object
		}
	}//GEN-LAST:event_Exit

	/**
	 * If match ended, go to Post.
	 * If ship placement, setup player ships, enemy AI, and AI ships.
	 * If user shot, checks if user has won.
	 * If the user has won, set match to have ended.
	 * If the user hasn't won, the AI shoots and checks if the AI has won.
	 * If the AI has won, set match to have ended.
	 * If neither the user nor the AI have won, advances the game to the next round.
	 *
	 * @param evt Button Click
	 */
	private void NextRound(ActionEvent evt) {//GEN-FIRST:event_NextRound
		if (this.roundNo == -1) {                 // Checks if the match ended.
			int[] statsList = new int[] {this.status, this.PlayerStats[0], this.PlayerStats[1], this.AIStats[1], this.AIStats[2], this.PlayerStats[2]};

			Post Post = new Post(statsList, this.mode, this.AIDiff);
			Post.setVisible(true);

			this.dispose();
		} else if (this.roundNo == 0) {           // Checks if it's the Ship Placement round.
			if (this.shipPlacing == this.shipNos) { // Checks if all ships have been placed.
				for (int y = 0; y < this.boardSize; y++) { // Finalizes the player's ships placement.
					for (int x = 0; x < this.boardSize; x++) {
						if (this.buttonsClicked[y][x]) {
							this.PlayerGridB[y][x].setBackground(new Color(176, 196, 222, 255));
							this.buttonsClicked[y][x] = false;
						}
					}
				}

				if (this.AIDiff == -1) {       // Initializes the AI to Sandbox
					this.AI = new Sandbox(this.initVars, this.PlayerGridL, this.PlayerShips);
				} else if (this.AIDiff == 0) { // Initializes the AI to Regular
					this.AI = new Regular(this.initVars, this.PlayerGridL, this.PlayerShips);
				} else {                       // Initializes the AI to Brutal
					this.AI = new Brutal(this.initVars, this.PlayerGridL, this.PlayerShips);
				}

				this.AIGridL = this.AI.getGridSelf();
				this.AIShips = this.AI.getShipsSelf();
				for (int ship = 0; ship < this.shipNos; ship++) {
					int[] xy = this.AIShips[ship].getStart();

					if (CurrentUser.getCurrentUser().equals("admin")) { // Used for debugging
						System.out.println("AI Ship at: " + xy[0] + " " + xy[1]);
						for (int l = 0; l < this.AIShips[ship].length; l++) {
							this.AIGridB[xy[1] + (this.AIShips[ship].getDirection() ? l : 0)][xy[0] + (this.AIShips[ship].getDirection() ? 0 : l)].setBackground(new Color(176, 196, 222, 255));
						}
					}
				}

				this.roundNo++;
				this.RoundTF.setText("" + this.roundNo);
				this.NextB.setText("<html><center>Next<br/>Round</center></html>");
				this.StatsUpdate();
			} else {                                  // Not all ships placed.
				this.AlertsTA.append("Not all ships placed! " + (this.shipNos - this.shipPlacing) + " ships left!\n");
			}
		} else {                                    // Normal Rounds
			if ((this.mode.equals("C") && (this.shotsSelected == 1)) || (this.mode.equals("S") && (this.shotsSelected == (this.shipNos - this.PlayerStats[2])))) { // Checks if the required number of selections have been made
				shotChecker:
				for (int y = 0; y < this.boardSize; y++) {
					for (int x = 0; x < this.boardSize; x++) {
						if (this.buttonsClicked[y][x]) {    // Checks if a shot was placed here.
							this.buttonsClicked[y][x] = false;
							this.AIGridL[y][x].markShot();
							this.PlayerStats[0]++;

							if (this.AIGridL[y][x].isHit()) { // Checks if a Ship was hit.
								this.AIGridB[y][x].setBackground(new Color(205, 0, 0, 255));
								this.PlayerStats[1]++;

								shipChecker:
								for (int ship = 0; ship < this.shipNos; ship++) {    // Used for finding which ship was hit.
									if (this.AIShips[ship].isSunk()) {                 // Checks if the ship was already sunk.
										continue shipChecker;
									}
									if (this.AIShips[ship].getPosition(new int[] {x, y}) > -1) {
										this.AIShips[ship].sectionHit(new int[] {x, y}); // Marks the section as hit.

										if (this.AIShips[ship].isSunk()) {               // Checks if the ship was sunk due to this shot.
											this.AIStats[2]++;
										}

										if (this.shipNos - this.AIStats[2] == 0) {       // Checks if the user won (to allow 100% accuracy scores).
											break shotChecker;
										}

										break shipChecker;
									}
								}
							} else {                          // No ship was hit.
								this.AIGridB[y][x].setBackground(new Color(0, 0, 128, 255));
							}
						}                                   // No shot was placed here.
					}
				}

				this.shotsSelected = 0;
				this.StatsUpdate();

				if (this.shipNos - this.AIStats[2] == 0) {       // Checks if the user won.
					this.end();
					this.status = 1;
				} else {                                         // User did not win.
					this.AI.updateGridSelf(this.AIGridL);          // Updates the AI's self grid
					this.fireAI();                                 // AI Shoots.
					this.StatsUpdate();

					if (this.shipNos - this.PlayerStats[2] == 0) { // Checks if the AI won.
						this.end();
						this.status = 0;
					} else {                                       // AI did not win. Next round.
						this.roundNo++;
						this.RoundTF.setText("" + this.roundNo);

						// TODO: Some stuff?
					}

				}

			} else {                                // Not enough selections made
				this.AlertsTA.append("Not all selections made! " + (this.mode.equals("C") ? 1 : (this.shipNos - this.shotsSelected)) + " selections left!\n");
			}

		}
	}//GEN-LAST:event_NextRound

	/**
	 * A series of <code>JOptionPane.MessageDialog</code>-s for explaining the game.
	 *
	 * @param evt Button Click
	 */
	private void Help(ActionEvent evt) {//GEN-FIRST:event_Help
		boolean[] responses = {false, false, false};

		one:
		while (true) {
			responses[0] = JOptionPane.showConfirmDialog(null, "This is where all help text will go.", "Help - 1", JOptionPane.YES_NO_OPTION) == 0;
			two:
			while (responses[0]) {   // One   -> Two
				responses[1] = JOptionPane.showConfirmDialog(null, "There will be multiple ones like this.", "Help - 2", JOptionPane.YES_NO_OPTION) == 0;
				three:
				while (responses[1]) { // Two   -> Three
					responses[2] = JOptionPane.showConfirmDialog(null, "3 should be enough for being a placeholder, no?", "Help - 3", JOptionPane.YES_NO_OPTION) == 0;
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
		this.ShipNoTF.setText("" + this.shipNos);
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
	 * @param button    JButton which was clicked.
	 * @param direction Orientation of the Ship to be placed.
	 *                  true: Vertical ; false: Horizontal
	 */
	private void placeShip(JButton button, boolean direction) {
		String coords = button.getActionCommand();
		int[] xy = this.extractCoordinates(coords);
		int length = this.PlayerShips[this.shipPlacing - (this.shipPlacing == this.shipNos ? 1 : 0)].length;

		if (coords.charAt(0) == '1') {             // Checks if the button clicked is from Grid 1.
			if (this.buttonsClicked[xy[1]][xy[0]]) { // Checks if the button has already been clicked.
				boolean flag = false;                        // Boolean flag to check if a ship was matched.
				int ship = 0;                                // The ship to which the given coordinates belong to.
				for (int s = 0; s < this.shipPlacing; s++) { // Checks which ship the given coordinates belong to.
					System.out.println(this.PlayerShips[s].getPosition(xy));
					if (this.PlayerShips[s].getPosition(xy) > -1) {
						ship = s;
						flag = true;
						break;                                   // Ship determined.
					}
				}
				if (!flag) {                                 // Checks if a ship wasn't matched.
					System.out.println("ERROR!! No ship matched!");
					return;
				}

				length = this.PlayerShips[ship].length;              // Length
				int[] start = this.PlayerShips[ship].getStart();     // Starting Coordinates
				boolean dir = this.PlayerShips[ship].getDirection(); // Direction
				flag = false;                                        // Boolean flag to check if any other ship is present
				for (int l = 0; l < length; l++) {
					this.PlayerGridL[start[1] + (dir ? l : 0)][start[0] + (dir ? 0 : l)].shipAbsent();
					this.PlayerGridB[start[1] + (dir ? l : 0)][start[0] + (dir ? 0 : l)].setBackground(new Color(5, 218, 255, 255));
					this.buttonsClicked[start[1] + (dir ? l : 0)][start[0] + (dir ? 0 : l)] = false;
				}
				this.PlayerShips[ship].remove();

				this.shipPlacing = this.nextShip();    // Determines the next ship to place.
			} else {                                 // Button hasn't been clicked.
				if (this.shipPlacing < this.shipNos) { // Checks if the number of ships placed is less than the max ships available.
					for (int l = 0; l < length; l++) {   // Checks for intersections and if the ship is within the board.
						if (xy[1] + (direction ? l : 0) >= this.boardSize || xy[0] + (direction ? 0 : l) >= this.boardSize) {
							// Out of bounds!
							System.out.println("Ship out of bounds at position: " + (l + 1));
							this.AlertsTA.append("Ship out of bounds at position: " + (l + 1) + "\n");
							return;
						} else if (this.buttonsClicked[xy[1] + (direction ? l : 0)][xy[0] + (direction ? 0 : l)]) {
							// An intersection occurred!
							System.out.println("Ship exists at position: " + (l + 1));
							this.AlertsTA.append("Ship exists at position: " + (l + 1) + "\n");
							return;
						}
					}
					// No intersections

					for (int l = 0; l < length; l++) {
						this.PlayerGridL[xy[1] + (direction ? l : 0)][xy[0] + (direction ? 0 : l)].shipPresent();
						this.PlayerGridB[xy[1] + (direction ? l : 0)][xy[0] + (direction ? 0 : l)].setBackground(new Color(242, 236, 0, 255));
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
	 * @param button JButton which was clicked
	 */
	private void fire(JButton button) {
		String coords = button.getActionCommand();
		int[] xy = this.extractCoordinates(coords);
		System.out.println("Player: " + xy[0] + " " + xy[1]);

		if (coords.charAt(0) == '2') {                    // Checks if the button clicked is from Grid 2.
			if (this.AIGridL[xy[1]][xy[0]].isUnguessed()) { // Checks if the location is unguessed.
				if (this.buttonsClicked[xy[1]][xy[0]]) {      // Checks if the button has already been clicked.
					button.setBackground(new Color(5, 218, 255, 255));
					this.buttonsClicked[xy[1]][xy[0]] = false;
					this.shotsSelected--;
				} else {                                      // Button hasn't been clicked.
					if (this.mode.equals("C")) {                // Checks if the match is in Classic Mode.
						if (this.shotsSelected == 0) {            // Checks if another location hasn't been selected.
							button.setBackground(new Color(242, 236, 0, 255));
							this.buttonsClicked[xy[1]][xy[0]] = true;
							this.shotsSelected++;
						} else {                                  // Another location has been selected.
							this.AlertsTA.append("Maximum locations selected!\n");
						}
					} else {                                    // Salvo Mode.
						if (this.shotsSelected < (this.shipNos - this.PlayerStats[2])) { // Checks if max locations haven't been selected.
							button.setBackground(new Color(242, 236, 0, 255));
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
	 */
	private void fireAI() {
		int[] xy = this.AI.fire();

		if (xy[1] == -1 || xy[0] == -1) { // Method overriding in AI class didn't work properly.
			System.out.println("ERROR!! TODO: Figure out what's wrong. Method overriding in AI class didn't work properly.");
		} else {                          // Everything's fine.
			for (int i = 0; i < (this.mode.equals("C") ? 1 : (this.shipNos - this.AIStats[2])); i++) {
				System.out.println("AI fires at: " + xy[0] + " " + xy[1]);

				this.PlayerGridL[xy[1]][xy[0]].markShot();
				this.AIStats[0]++;

				if (this.PlayerGridL[xy[1]][xy[0]].isHit()) {                  // Checks if a Ship was hit.
					this.PlayerGridB[xy[1]][xy[0]].setBackground(new Color(205, 0, 0, 255));
					this.AIStats[1]++;

					shipChecker:
					for (int ship = 0; ship < this.shipNos; ship++) { // Used for finding which ship was hit.
						if (this.PlayerShips[ship].getPosition(xy) > -1) {
							this.PlayerShips[ship].sectionHit(xy);                   // Marks the section as hit.

							if (this.PlayerShips[ship].isSunk()) {                   // Checks if the ship was sunk.
								this.PlayerStats[2]++;
							}

							break shipChecker;
						}
					}
				} else {                                                       // No ship was hit.
					this.PlayerGridB[xy[1]][xy[0]].setBackground(new Color(0, 0, 128, 255));
				}

				this.AI.updateGridOpp(this.PlayerGridL);                       // Updates the AI's hostile grid.
				if (this.AIDiff == 1) {                                        // Updates the Brutal AI's Player's ship list
					this.AI.updateShipsOpp(PlayerShips);
				}

				xy = this.AI.fire();
			}
		}
	}

	/**
	 * Determines which ship to place next, since ships can be removed from the grid.
	 * Returns the number of ships if all ships have been placed.
	 *
	 * @return an integer equivalent to the ship number to be placed.
	 */
	private int nextShip() {
		for (int ship = 0; ship < this.shipNos; ship++) {
			if (this.PlayerShips[ship].getStart()[0] == -1 || this.PlayerShips[ship].getStart()[0] == -1) {
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
	 * Extracts the X- and Y-Coordinates form the Grid JButton action command String format:
	 * <code>grid_no x_coordinate y_coordinate</code>
	 *
	 * @param coords Coordinates of the Button
	 *
	 * @return An integer array of 2 integers. 1st integer is X-Coordinate. 2nd integer is Y-Coordinate.
	 */
	private int[] extractCoordinates(String coords) {
		int[] xy = new int[2];               // X-, Y-Coordinates

		if (this.boardSize == 10) {          // Checks if the board size is 10 x 10. 1 digit for each coordinate.
			xy[0] = Integer.parseInt("" + coords.charAt(2));
			xy[1] = Integer.parseInt("" + coords.charAt(4));
		} else {                             // 15 x 15 Grid
			if (coords.length() == 5) {        // 1 digit for each coordinate
				xy[0] = Integer.parseInt("" + coords.charAt(2));
				xy[1] = Integer.parseInt("" + coords.charAt(4));
			} else if (coords.length() == 6) { // 1 digit for 1 coordinate. 2 digits for the other.
				if (coords.charAt(3) == ' ') {   // Y-Coordinate has 2 digits
					xy[0] = Integer.parseInt("" + coords.charAt(2));
					xy[1] = 10 + Integer.parseInt("" + coords.charAt(5));
				} else {                         // X-Coordinate has 2 digits
					xy[0] = 10 + Integer.parseInt("" + coords.charAt(3));
					xy[1] = Integer.parseInt("" + coords.charAt(5));
				}
			} else {                           // 2 digits for each coordinate
				xy[0] = 10 + Integer.parseInt("" + coords.charAt(3));
				xy[1] = 10 + Integer.parseInt("" + coords.charAt(6));
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
  // End of variables declaration//GEN-END:variables
	// Start of custom GUI variables declaration
	private JButton[][] PlayerGridB;
	private JButton[][] AIGridB;
	// End of custom GUI variables decralation
}
