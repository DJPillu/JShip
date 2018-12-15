package game;

import game.ai.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.*;
import javax.swing.*;
import jship.JShip;


/**
 * Form for playing the actual game.
 * Temporary class used for visually designing the GUI.
 *
 * @author blackk100
 */
public class Game extends JFrame {

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
	 * I     4     I      Patrol Boat      I
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
	 * Total Number of ships available in this match.
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
	 * Grid 1 as a 2-Dimensional Location Array.
	 */
	private Location[][] PlayerGridL;

	/**
	 * Grid 2 as a 2-Dimensional Location Array.
	 */
	private Location[][] AIGridL;

	/**
	 * Player 1 Statistics:
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
	 * Player 2 Statistics:
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
	 * 0   - Ship Placement
	 * &gt; 0 - Actual Game (until it ends).
	 * </pre>
	 */
	private int roundNo = 0;

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
	private int shipsPlaced = 0;

	/**
	 * Temporary variable for storing the current ship being placed.
	 * Only used during the Ship Placement round, for multi-tile ships.
	 */
	private int shipNo = 0;

	/**
	 * Temporary variable for the length of each ship.
	 * Only used during the Ship Placement round.
	 */
	int[] shipLengths = new int[38];

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
	public Game(boolean[] initVars, String mode, int AIDiff) {
		this.initVars = initVars;
		this.boardSize = initVars[0] ? 15 : 10;
		if (initVars[1]) {
			this.shipNos += 5;
		}
		if (initVars[2]) {
			this.shipNos += 8;
		}
		if (initVars[3]) {
			this.shipNos += 10;
		}
		if (initVars[4]) {
			this.shipNos += 15;
		}
		for (int i = 0; i < 12; i++) {
			if (i < 2) {
				this.shipLengths[i] = initVars[1] ? 5 : 0;
			} else if (i < 4) {
				this.shipLengths[i] = initVars[2] ? 4 : 0;
			} else if (i < 7) {
				this.shipLengths[i] = initVars[3] ? 3 : 0;
			} else {
				this.shipLengths[i] = initVars[4] ? 2 : 0;
			}
		}
		this.mode = mode;

		this.AIDiff = AIDiff;

		this.initComponents();

		this.setTitleL(AIDiff);
		this.GridTF.setText(initVars[0] ? "15 x 15" : "10 x 10");
		this.ModeTF.setText(mode.equals("C") ? "Classic" : "Salvo");
		this.BattleshipCB.doClick();

		P1StatsUpdate(true);
		P2StatsUpdate(true);

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

		this.buttonsClicked = new boolean[this.boardSize][this.boardSize];

		// Sleeps for 0 seconds.
		// Do I need this?
		try {
			java.util.concurrent.TimeUnit.SECONDS.sleep(0);
		} catch (InterruptedException ex) {
			System.out.println(ex);
		} finally {
			this.setLocationRelativeTo(null);
		}
	}

	/**
	 * Updates TitleL's display text. Is run upon frame creation.
	 *
	 * @param AIDiff The AI Difficulty
	 */
	private void setTitleL(int AIDiff) {
		System.out.println(AIDiff);
		System.out.println("PvE - " + (AIDiff == -1 ? "Sandbox" : (AIDiff == 0 ? "Realistic" : "Brutal")));
		TitleL.setText("PvE - " + (AIDiff == -1 ? "Sandbox" : (AIDiff == 0 ? "Realistic" : "Brutal")));
		setTitle("PvE - " + (AIDiff == -1 ? "Sandbox" : (AIDiff == 0 ? "Realistic" : "Brutal")));
	}

	/**
	 * Updates Player 1 Statistics displays.
	 *
	 * @param flag Used for identifying whether or not its the Ship Placement Round
	 */
	private void P1StatsUpdate(boolean flag) {
		this.PlayerSLTF.setText("" + (this.shipNos - this.PlayerStats[2]));
		this.PlayerSFTF.setText("" + this.PlayerStats[0]);
		this.PlayerHitsTF.setText("" + this.PlayerStats[1]);
		this.PlayerAccTF.setText("" + (Math.round((this.PlayerStats[1] * 10000.0) / (flag ? 1 : this.PlayerStats[0])) / 100.0) + " %");
	}

	/**
	 * Updates Player 2 Statistics displays.
	 *
	 * @param flag Used for identifying whether or not its the Ship Placement Round
	 */
	private void P2StatsUpdate(boolean flag) {
		this.AISLTF.setText("" + (this.shipNos - this.AIStats[2]));
		this.AISFTF.setText("" + this.AIStats[0]);
		this.AIHitsTF.setText("" + this.AIStats[1]);
		this.AIAccTF.setText("" + (Math.round((this.AIStats[1] * 10000.0) / (flag ? 1 : this.AIStats[0])) / 100.0) + " %");
	}

	/**
	 * Initializes the buttons for Grid gridNo.
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
						buttonClick((JButton) evt.getSource(), true);
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
    PlayerGridP = new JPanel();
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
    PatrolCB = new JCheckBox();
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
    Spacer2L = new JLabel();
    ExitB = new JButton();
    ModeTF = new JTextField();
    StatusP = new JPanel();
    PlayerStatusP = new JPanel();
    PlayerStatusTitleL = new JLabel();
    PlayerrSLL = new JLabel();
    PlayerSLTF = new JTextField();
    PlayerSFL = new JLabel();
    PlayerSFTF = new JTextField();
    PlayerAccL = new JLabel();
    PlayerAccTF = new JTextField();
    PlayerHitsL = new JLabel();
    PlayerHitsTF = new JTextField();
    AIStatusP = new JPanel();
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
    setBackground(new Color(255, 255, 0));
    setResizable(false);

    TitleL.setHorizontalAlignment(SwingConstants.CENTER);
    TitleL.setText("PvE - Sandbox");

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

    PlayerGridP.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
    PlayerGridP.setLayout(new GridLayout(this.boardSize, this.boardSize, 0, 0));

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

    ShipsP.setLayout(new GridLayout(2, 2, 10, 10));

    BattleshipCB.setText("Battleship");
    BattleshipCB.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        ShipCBClicked(evt);
      }
    });
    ShipsP.add(BattleshipCB);

    CruiserCB.setText("Cruiser");
    CruiserCB.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        ShipCBClicked(evt);
      }
    });
    ShipsP.add(CruiserCB);

    DestroyerCB.setText("Destroyer");
    DestroyerCB.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        ShipCBClicked(evt);
      }
    });
    ShipsP.add(DestroyerCB);

    PatrolCB.setText("Patrol Boat");
    PatrolCB.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        ShipCBClicked(evt);
      }
    });
    ShipsP.add(PatrolCB);

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

    ButtonsP.setLayout(new GridLayout(2, 3, 10, 10));

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
          .addComponent(ButtonsP, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
        .addComponent(AlertsSP, GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(ButtonsP, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
        .addContainerGap())
    );

    GroupLayout MainPLayout = new GroupLayout(MainP);
    MainP.setLayout(MainPLayout);
    MainPLayout.setHorizontalGroup(MainPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(MainPLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(PlayerGridP, GroupLayout.PREFERRED_SIZE, 450, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(RoundStatusP, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(AIGridP, GroupLayout.PREFERRED_SIZE, 450, GroupLayout.PREFERRED_SIZE)
        .addContainerGap())
    );
    MainPLayout.setVerticalGroup(MainPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(MainPLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(MainPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
          .addComponent(RoundStatusP, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addGroup(MainPLayout.createSequentialGroup()
            .addGroup(MainPLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
              .addComponent(AIGridP, GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
              .addComponent(PlayerGridP, GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE))
            .addGap(0, 0, Short.MAX_VALUE)))
        .addContainerGap())
    );

    PlayerStatusP.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));

    PlayerStatusTitleL.setHorizontalAlignment(SwingConstants.CENTER);
    PlayerStatusTitleL.setText("Player 1 Status");

    PlayerrSLL.setText("Ships Left:");

    PlayerSLTF.setEditable(false);

    PlayerSFL.setText("Shots Fired:");

    PlayerSFTF.setEditable(false);

    PlayerAccL.setText("Accuracy:");

    PlayerAccTF.setEditable(false);

    PlayerHitsL.setText("Hits Landed:");

    PlayerHitsTF.setEditable(false);

    GroupLayout PlayerStatusPLayout = new GroupLayout(PlayerStatusP);
    PlayerStatusP.setLayout(PlayerStatusPLayout);
    PlayerStatusPLayout.setHorizontalGroup(PlayerStatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(PlayerStatusPLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(PlayerStatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
          .addComponent(PlayerStatusTitleL, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addGroup(PlayerStatusPLayout.createSequentialGroup()
            .addGroup(PlayerStatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
              .addComponent(PlayerrSLL, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(PlayerAccL, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGap(18, 18, 18)
            .addGroup(PlayerStatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
              .addComponent(PlayerSLTF, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
              .addComponent(PlayerAccTF, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
            .addGroup(PlayerStatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
              .addComponent(PlayerHitsL, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(PlayerSFL, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGap(18, 18, 18)
            .addGroup(PlayerStatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
              .addComponent(PlayerHitsTF, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
              .addComponent(PlayerSFTF, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))))
        .addContainerGap())
    );
    PlayerStatusPLayout.setVerticalGroup(PlayerStatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(PlayerStatusPLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(PlayerStatusTitleL)
        .addGap(18, 18, 18)
        .addGroup(PlayerStatusPLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(PlayerrSLL)
          .addComponent(PlayerSLTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addComponent(PlayerSFTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addComponent(PlayerSFL))
        .addGap(18, 18, 18)
        .addGroup(PlayerStatusPLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(PlayerAccL)
          .addComponent(PlayerAccTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addComponent(PlayerHitsL)
          .addComponent(PlayerHitsTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    AIStatusP.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));

    AIStatusTitleL.setHorizontalAlignment(SwingConstants.CENTER);
    AIStatusTitleL.setText("Player 2 Status");

    AISLL.setText("Ships Left:");

    AISLTF.setEditable(false);

    AISFL.setText("Shots Fired:");

    AISFTF.setEditable(false);

    AIAccL.setText("Accuracy:");

    AIAccTF.setEditable(false);

    AIHitsL.setText("Hits Landed:");

    AIHitsTF.setEditable(false);

    GroupLayout AIStatusPLayout = new GroupLayout(AIStatusP);
    AIStatusP.setLayout(AIStatusPLayout);
    AIStatusPLayout.setHorizontalGroup(AIStatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(AIStatusPLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(AIStatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
          .addComponent(AIStatusTitleL, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addGroup(AIStatusPLayout.createSequentialGroup()
            .addGroup(AIStatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
              .addComponent(AISLL, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(AIAccL, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGap(18, 18, 18)
            .addGroup(AIStatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
              .addComponent(AISLTF, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
              .addComponent(AIAccTF, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
            .addGroup(AIStatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
              .addComponent(AIHitsL, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(AISFL, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGap(18, 18, 18)
            .addGroup(AIStatusPLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
              .addComponent(AIHitsTF, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
              .addComponent(AISFTF, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))))
        .addContainerGap())
    );
    AIStatusPLayout.setVerticalGroup(AIStatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(AIStatusPLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(AIStatusTitleL)
        .addGap(18, 18, 18)
        .addGroup(AIStatusPLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(AISLL)
          .addComponent(AISLTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addComponent(AISFL)
          .addComponent(AISFTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        .addGap(18, 18, 18)
        .addGroup(AIStatusPLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
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
        .addComponent(PlayerStatusP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(AIStatusP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addContainerGap())
    );
    StatusPLayout.setVerticalGroup(StatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(GroupLayout.Alignment.TRAILING, StatusPLayout.createSequentialGroup()
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addGroup(StatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
          .addComponent(AIStatusP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addComponent(PlayerStatusP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
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
	 * Goes to Post upon match completion.
	 *
	 * @param status A integer indicating whether or not the given round was won by the user. 1: Win; 2: Lose
	 */
	private void goToPost(int status) {
		int[] temp = new int[] {status, this.PlayerStats[0], this.PlayerStats[1], this.AIStats[1], this.AIStats[2], this.PlayerStats[2]};

		Post Post = new Post(temp, this.mode, this.AIDiff);
		Post.setVisible(true);

		this.dispose();
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
	 * If the user has won, moves onto Post.
	 * If the user hasn't won, the AI shoots and checks if the AI has won.
	 * If the AI has won, moves onto Post.
	 * If neither the user nor the AI have won, advances the game to the next round.
	 *
	 * @param evt Button Click
	 */
	private void NextRound(ActionEvent evt) {//GEN-FIRST:event_NextRound
		if (this.roundNo == 0) {                  // Checks if it's the Ship Placement round.
			if (this.shipsPlaced == this.shipNos) { // Checks if all ships have been placed.
				/**
				 * TODO: Fix this. Used for multi-tile ships.
				 *
				 * <code>
				 * <pre>
				 *	if (shipNo == 0) {         // Checks if no ship has been placed yet.
				 *		if (timesClicked == 0) { // Checks if it the first click
				 *			AlertsTA.append("Ship Placement:\n");
				 *			AlertsTA.append("\tPlace the initial point of the current ship first.\n");
				 *			AlertsTA.append("\tThen place the final point.\n");
				 *			AlertsTA.append("\tThe final point must be vertically or horizontally aligned, and must be as long as the ship's length.\n");
				 *
				 *			int shipLength = shipLengths[shipNo];
				 *
				 *			while (shipLength == 0) {
				 *				shipNo++;
				 *				shipLength = shipLengths[shipNo];
				 *			}
				 *
				 *			System.out.println("Placing ship no. " + shipsPlaced + " - " + shipLength);
				 *			AlertsTA.append("\tPlacing ship no. " + shipsPlaced + " of length " + shipLength);
				 *		} else {                         // Not the first click
				 *			if (true) {
				 *				// TODO: Do something
				 *			}
				 *		}
				 *	}
				 * </pre>
				 * </code>
				 */
				for (int y = 0; y < this.boardSize; y++) {
					for (int x = 0; x < this.boardSize; x++) {
						if (this.buttonsClicked[y][x]) { // Ship is placed here.
							this.PlayerGridL[y][x].hasShip();
							this.PlayerGridB[y][x].setBackground(new Color(176, 196, 222, 255));
						}
						this.buttonsClicked[y][x] = false;
					}
				}

				this.shipsPlaced = 0;

				if (this.AIDiff == -1) {             // Initializes the AI to Sandbox
					this.AI = new Sandbox(this.initVars);
				} else if (this.AIDiff == 0) {       // Initializes the AI to Regular
					this.AI = new Regular(this.initVars);
				} else {                             // Initializes the AI to Brutal
					this.AI = new Brutal(this.initVars, this.PlayerGridL);
				}

				this.AIGridL = this.AI.getGrid();
				System.out.println("Debug start!"); // Debug start
				for (int y = 0; y < this.boardSize; y++) {
					for (int x = 0; x < this.boardSize; x++) {
						if (this.AIGridL[y][x].hasShip()) {
							System.out.println("Ship at: " + x + " " + y);
							this.AIGridB[y][x].setBackground(new Color(176, 196, 222, 255));
						}
					}
				}
				System.out.println("Debug end!");   // Debug end

				this.roundNo++;
				this.RoundTF.setText("" + this.roundNo);

				this.P1StatsUpdate(true);
				this.P2StatsUpdate(true);

				this.NextB.setText("<html><center>Next<br/>Round</center></html>");
			} else {                                // Not all ships placed.
				this.AlertsTA.append("\tNot all ships placed! " + this.shipsPlaced + " ships left!\n");
			}
		} else {                                  // Normal Rounds
			if ((this.mode.equals("C") && (this.shotsSelected == 1)) || (this.mode.equals("S") && (this.shotsSelected == (this.shipNos - this.PlayerStats[2])))) { // Checks if the required number of selections have been made

				for (int y = 0; y < this.boardSize; y++) {
					for (int x = 0; x < this.boardSize; x++) {
						if (this.buttonsClicked[y][x]) {   // Shot was placed here.
							this.AIGridL[y][x].markShot();
							this.PlayerStats[0]++;
							if (this.AIGridL[y][x].isHit()) { // A Ship was hit
								this.AIGridB[y][x].setBackground(new Color(205, 0, 0, 255));
								this.PlayerStats[1]++;
								this.AIStats[2]++;
							} else {
								this.AIGridB[y][x].setBackground(new Color(5, 218, 229, 255));
							}

						}
						this.buttonsClicked[y][x] = false;
					}
				}

				this.shotsSelected = 0;
				this.P1StatsUpdate(false);

				if (this.shipNos - this.AIStats[2] == 0) {   // Checks if the user won.
					this.goToPost(1);
				} else {                                     // User did not win. AI shoots.
					// TODO: Make AI shoot.
					for (int y = 0; y < this.boardSize; y++) {
						for (int x = 0; x < this.boardSize; x++) {
							if (this.buttonsClicked[y][x]) {   // Shot was placed here.
								this.PlayerGridL[y][x].markShot();
								this.AIStats[0]++;
								if (this.PlayerGridL[y][x].isHit()) { // A Ship was hit
									this.PlayerGridB[y][x].setBackground(new Color(205, 0, 0, 255));
									this.AIStats[1]++;
									this.PlayerStats[2]++;
								} else {
									this.PlayerGridB[y][x].setBackground(new Color(5, 218, 229, 255));
								}

							}
							this.buttonsClicked[y][x] = false;
						}
					}
					this.P2StatsUpdate(false);

					if (this.shipNos - this.PlayerStats[2] == 0) { // Checks if the AI won.
						this.goToPost(0);
					} else {                                   // AI did not win. Next round.
						this.roundNo++;
						this.RoundTF.setText("" + this.roundNo);

						// TODO: Some stuff?
					}

				}

			} else {                                // Not enough selections made
				this.AlertsTA.append("\tNot all selections made! " + (this.mode.equals("C") ? 1 : (this.shipNos - this.shipsPlaced)) + " selections left!\n");
			}

		}
	}//GEN-LAST:event_NextRound

	/**
	 * A series of <code>JOptionPane.MessageDialog</code>-s for explaining the game.
	 *
	 * @param evt Button Click
	 */
	private void Help(ActionEvent evt) {//GEN-FIRST:event_Help
		JOptionPane.showMessageDialog(null, "This is where all help text will go.", "Help - 1", JOptionPane.INFORMATION_MESSAGE);
		JOptionPane.showMessageDialog(null, "There will be multiple ones like this.", "Help - 2", JOptionPane.INFORMATION_MESSAGE);
		JOptionPane.showMessageDialog(null, "3 should be enough for being a placeholder, no?", "Help - 3", JOptionPane.INFORMATION_MESSAGE);
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
		this.PatrolCB.setSelected(this.initVars[4]);
		this.ShipNoTF.setText("" + this.shipNos);
	}//GEN-LAST:event_ShipCBClicked

	/**
	 * TODO: Need to fix. Used for multi-tile ships.
	 *
	 * @param shipLength Length of the Ship to be placed
	 */
	private void placeShip(int shipLength) {
		// Checks if the initial and final points of the ship are aligned
		if (this.initLoc[0] == this.finalLoc[0]) {        // Checks for horizontal alignment.
			this.direction = false;
		} else if (this.initLoc[1] == this.finalLoc[1]) { // Checks for vertical alignment.
			this.direction = true;
		} else {                                          // Ships aren't aligned.
			this.AlertsTA.append("\tThe initial and final points of the ship aren't in the same row/coloumn!\n");
			return;
		}

		// Checks if the initial and final points of the ship correspond to the length of the ship.
		if (this.direction && (Math.abs(this.finalLoc[0] - this.initLoc[0]) + 2 == shipLength)) {

		} else if (!this.direction && (Math.abs(this.finalLoc[1] - this.initLoc[1]) + 2 == shipLength)) {

		} else {
			this.AlertsTA.append("\tThe initial and final points of the ship don't correspond to its length!\n");
			return;
		}

		// Checks if the ship will intersect any other ship
		if (this.direction) {                                                  // Vertical Orientation
			for (int l = 0; l < shipLength; l++) {
				if (this.PlayerGridL[this.initLoc[0] + l][this.initLoc[1]].hasShip()) { // Y-Coordinate is incremented if vertical
					System.out.println("Ship exists at vertical length: " + (l + 1));
					this.AlertsTA.append("\t\tA Ship exists at vertical length " + (l + 1) + " from the initial point!\n");
					return;
				}
			}
		} else {                                                               // Horizontal Orientation
			for (int l = 0; l < shipLength; l++) {
				if (this.PlayerGridL[this.initLoc[0]][this.initLoc[1] + l].hasShip()) { // X-Coordinate is incremented if horizontal
					System.out.println("Ship exists at horizontal length: " + (l + 1));
					this.AlertsTA.append("\t\tA Ship exists at horizontal length " + (l + 1) + " from the initial point!\n");
					return;
				}
			}
		}

		// Sets the location values
		if (this.direction) {                                                // Verical Orientation
			for (int l = 0; l < shipLength; l++) {
				this.PlayerGridL[this.initLoc[0] + l][this.initLoc[1]].shipPresent(); // Y-Coordinate is incremented if vertical
			}
		} else {                                                             // Horizontal Orientation
			for (int l = 0; l < shipLength; l++) {
				this.PlayerGridL[this.initLoc[0]][this.initLoc[1] + l].shipPresent(); // X-Coordinate is incremented if horizontal
			}
		}

		System.out.println("Ship no. " + this.shipsPlaced + " placed");
		this.AlertsTA.append("\tShip no. " + this.shipsPlaced + " placed");

	}

	/**
	 * Manages what to do if a button was clicked.
	 *
	 * @param button    JButton which was clicked
	 * @param userClick Whether the click originated from the user
	 */
	private void buttonClick(JButton button, boolean userClick) {
		String coords = button.getActionCommand();

		System.out.println(coords);

		if (userClick) {                                     // User Clicked the button
			int[] xy = this.extractCoordinates(coords);

			if (this.roundNo == 0) {                           // Checks if it is the Ship Placement Round.
				if (coords.charAt(0) == '1') {                   // Checks if the button clicked is from Grid 1.
					if (this.buttonsClicked[xy[1]][xy[0]]) {       // Checks if the button has already been clicked.
						button.setBackground(new Color(5, 218, 255, 255));
						this.buttonsClicked[xy[1]][xy[0]] = false;
						this.shipsPlaced--;
					} else {                                       // Button hasn't been clicked.
						if (this.shipsPlaced < this.shipNos) {       // Checks if the number of ships placed is less than the max ships available.
							button.setBackground(new Color(242, 236, 0, 255));
							this.buttonsClicked[xy[1]][xy[0]] = true;
							this.shipsPlaced++;

							if (this.shipsPlaced == this.shipNos) {
								this.AlertsTA.append("\tAll ships placed!\n");
							}
						} else {                                     // More ships than limit allowed added.
							this.AlertsTA.append("\tShip limit reached!\n");
						}
					}
				} else {                                         // Grid 2.
					this.AlertsTA.append("\tWrong grid!\n");
				}
			} else {                                           // Not Ship Placement Round. Has to be from Grid 2 in this case.
				if (coords.charAt(0) == '2') {                   // Checks if the button clicked is from Grid 2
					if (this.AIGridL[xy[1]][xy[0]].isUnguessed()) { // Checks if the location is unguessed.
						if (this.buttonsClicked[xy[1]][xy[0]]) {     // Checks if the button has already been clicked.
							button.setBackground(new Color(5, 218, 255, 255));
							this.buttonsClicked[xy[1]][xy[0]] = false;
							this.shotsSelected--;
						} else {                                     // Button hasn't been clicked.
							if (this.mode.equals("C")) {               // Checks if the match is in Classic Mode.
								if (this.shotsSelected == 0) {           // Checks if another location hasn't been selected.
									button.setBackground(new Color(242, 236, 0, 255));
									this.buttonsClicked[xy[1]][xy[0]] = true;
									this.shotsSelected++;
								} else {                                 // Another location has been selected.
									this.AlertsTA.append("\tMaximum locations selected!\n");
								}
							} else {                                   // Salvo Mode.
								if (this.shotsSelected < (this.shipNos - this.PlayerStats[2])) { // Checks if max locations haven't been selected.
									button.setBackground(new Color(242, 236, 0, 255));
									this.buttonsClicked[xy[1]][xy[0]] = true;
									this.shotsSelected++;

									if (this.shipNos == this.PlayerStats[2]) {
										this.AlertsTA.append("\tAll locations selected!\n");
									}
								} else {                                 // Max locations have been selected.
									this.AlertsTA.append("\tMaximum locations selected!\n");
								}
							}
						}
					} else {                                       // Already guessed.
						this.AlertsTA.append("\tThis location is already guessed!\n");
					}
				} else {                                         // Grid 1
					this.AlertsTA.append("\tWrong grid!\n");
				}
			}
		} else {                                             // AI Clicked the button
			// TODO: Do stuff.
		}
	}

	/**
	 * Extracts the X- and Y-Coordinates form the Grid JButton action command String format: "&lt;grid_no&gt;
	 * &lt;x-coordinate&gt; &lt;y-coordinate&gt;"
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

	/**
	 * Function for handling the AI input in Grid 2 for PvE matches
	 * Also handles the AI input in Grids 1, 2 for EvE matches.
	 *
	 * @param button JButton to be clicked. May be removed?
	 */
	private void buttonClickAI(JButton button) {
		// TODO: Do stuff.
		this.buttonClick(button, false);
	}

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private JLabel AIAccL;
  private JTextField AIAccTF;
  private JPanel AIGridP;
  private JLabel AIHitsL;
  private JTextField AIHitsTF;
  private JLabel AISFL;
  private JTextField AISFTF;
  private JLabel AISLL;
  private JTextField AISLTF;
  private JPanel AIStatusP;
  private JLabel AIStatusTitleL;
  private JScrollPane AlertsSP;
  private JTextArea AlertsTA;
  private JCheckBox BattleshipCB;
  private JPanel ButtonsP;
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
  private JCheckBox PatrolCB;
  private JLabel PlayerAccL;
  private JTextField PlayerAccTF;
  private JPanel PlayerGridP;
  private JLabel PlayerHitsL;
  private JTextField PlayerHitsTF;
  private JLabel PlayerSFL;
  private JTextField PlayerSFTF;
  private JTextField PlayerSLTF;
  private JPanel PlayerStatusP;
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
	// Start of special variables declaration
	private JButton[][] PlayerGridB;
	private JButton[][] AIGridB;
	// End of special variables decralation
}
