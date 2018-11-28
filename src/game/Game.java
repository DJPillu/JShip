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
	 * Game Type:
	 * <pre>
	 *  1 - PvE
	 *  0 - EvE
	 * </pre>
	 */
	private final int type;

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
	 * AI 1 Difficulty:
	 *
	 * <pre>
	 * -1 - Sandbox       (Easy)
	 *  0 - Realistic     (Medium)
	 *  1 - Brutal        (Hard)
	 * </pre>
	 */
	private final int AI1Diff;

	/**
	 * AI 2 Difficulty (Present during EvE only):
	 *
	 * <pre>
	 * -2 - Disabled
	 * -1 - Sandbox       (Easy)
	 *  0 - Realistic     (Medium)
	 *  1 - Brutal        (Hard)
	 * </pre>
	 */
	private final int AI2Diff;

	/**
	 * AI 1.
	 * Always uses Grid 2.
	 */
	private AI AI1;

	/**
	 * AI 2.
	 * Present during EvE only.
	 * Always uses Grid 1 (during EvE).
	 */
	private AI AI2;

	/**
	 * Grid 1 as a 2-Dimensional Location Array.
	 */
	private Location[][] Grid1L;

	/**
	 * Grid 2 as a 2-Dimensional Location Array.
	 */
	private Location[][] Grid2L;

	/**
	 * true if P1 wins.
	 * false if P2 wins.
	 */
	private boolean roundStatus = false;

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
	private int[] P1Stats = {0, 0, 0};

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
	private int[] P2Stats = {0, 0, 0};

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
	 * Internal counter for number of times a button was clicked within a round.
	 */
	private int timesClicked = 0;

	/**
	 * Temporary variable for storing the number of ships placed.
	 * Only used during the Ship Placement round.
	 */
	private int shipsPlaced = 0;

	/**
	 * Temporary variable for storing the current ship being placed.
	 * Only used during the Ship Placement round.
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
	 * @param type     Game Type
	 * @param mode     Game Mode
	 * @param AI1Diff  AI 1 Difficulty Level
	 * @param AI2Diff  AI 2 Diffiuclty Level
	 */
	public Game(boolean[] initVars, int type, String mode, int AI1Diff, int AI2Diff) {
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
		this.type = type;
		this.mode = mode;

		initComponents();

		this.GridTF.setText(initVars[0] ? "15 x 15" : "10 x 10");
		this.ModeTF.setText(mode.equals("C") ? "Classic" : "Salvo");

		this.setTitleL(type);

		this.BattleshipCB.doClick();

		this.P1StatsUpdate(true);
		this.P2StatsUpdate(true);

		this.AI1Diff = AI1Diff;
		this.AI2Diff = AI2Diff;

		Object[] temp;                               // Temporary Array for storing Grid 1 & Grid 2 objects.
		temp = this.initGrid(this.boardSize, false); // Grid 1
		this.Grid1B = (JButton[][]) temp[0];
		this.Grid1L = (Location[][]) temp[1];
		for (int y = 0; y < this.boardSize; y++) {
			for (int x = 0; x < this.boardSize; x++) {
				Grid1.add(this.Grid1B[y][x]);
			}
		}
		temp = this.initGrid(this.boardSize, true);  // Grid 2
		this.Grid2B = (JButton[][]) temp[0];
		this.Grid2L = (Location[][]) temp[1];
		for (int y = 0; y < this.boardSize; y++) {
			for (int x = 0; x < this.boardSize; x++) {
				Grid2.add(this.Grid2B[y][x]);
			}
		}
		temp = null;                                 // Implicit Garbage Collecting temp (hopefully)
		System.gc();

		this.buttonsClicked = new boolean[this.boardSize][this.boardSize];

		// Sleeps for 2 seconds.
		// Do I need this?
		try {
			java.util.concurrent.TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException ex) {
			System.out.println(ex);
		}
	}

	/**
	 * Updates TitleL's display text. Is run upon frame creation.
	 *
	 * @param type Game Type
	 */
	private void setTitleL(int type) {
		if (type > 0) {
			System.out.println("PvE - " + (AI1Diff == -1 ? "Sandbox" : (AI1Diff == 0 ? "Realistic" : "Brutal")));
			this.TypeL.setText("PvE - " + (AI1Diff == -1 ? "Sandbox" : (AI1Diff == 0 ? "Realistic" : "Brutal")));
			this.setTitle("PvE - " + (AI1Diff == -1 ? "Sandbox" : (AI1Diff == 0 ? "Realistic" : "Brutal")));
		} else {
			System.out.println("EvE - " + (AI1Diff == -1 ? "Sandbox" : (AI1Diff == 0 ? "Realistic" : "Brutal")) + " vs " + (AI2Diff == -1 ? "Sandbox" : (AI2Diff == 0 ? "Realistic" : "Brutal")));
			this.TypeL.setText("EvE - " + (AI1Diff == -1 ? "Sandbox" : (AI1Diff == 0 ? "Realistic" : "Brutal")) + " vs " + (AI2Diff == -1 ? "Sandbox" : (AI2Diff == 0 ? "Realistic" : "Brutal")));
			this.setTitle("EvE - " + (AI1Diff == -1 ? "Sandbox" : (AI1Diff == 0 ? "Realistic" : "Brutal")) + " vs " + (AI2Diff == -1 ? "Sandbox" : (AI2Diff == 0 ? "Realistic" : "Brutal")));
		}
	}

	/**
	 * Updates Player 1 Statistics displays.
	 *
	 * @param flag Used for identifying whether or not its the Ship Placement Round
	 */
	private void P1StatsUpdate(boolean flag) {
		this.P1SLTF.setText("" + (this.shipNos - this.P1Stats[2]));
		this.P1SFTF.setText("" + this.P1Stats[0]);
		this.P1HitsTF.setText("" + this.P1Stats[1]);
		this.P1AccTF.setText("" + (float) (this.P1Stats[1] / (flag ? 1 : this.P1Stats[0])) + " %");
	}

	/**
	 * Updates Player 2 Statistics displays.
	 *
	 * @param flag Used for identifying whether or not its the Ship Placement Round
	 */
	private void P2StatsUpdate(boolean flag) {
		this.P2SLTF.setText("" + (this.shipNos - this.P2Stats[2]));
		this.P2SFTF.setText("" + this.P2Stats[0]);
		this.P2HitsTF.setText("" + this.P2Stats[1]);
		this.P2AccTF.setText("" + (float) (this.P2Stats[1] / (flag ? 1 : this.P2Stats[0])) + " %");
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
				GridB[y][x].addActionListener(this::ButtonHandler);

				GridL[y][x] = new Location();
				GridL[y][x].shipAbsent();
			}
		}

		return new Object[] {GridB, GridL};
	}

	/**
	 * Initializes the AI.
	 *
	 * @param AIDiff AI Difficulty
	 *
	 * @return An AI object of the specific difficulty
	 */
	private AI initAI(int AIDiff) {
		if (AIDiff == -1) {
			return new Sandbox(this.initVars, this.mode, this.Grid1L);
		} else if (AIDiff == 0) {
			return new Regular(this.initVars, this.mode, this.Grid1L);
		} else {
			return new Brutal(this.initVars, this.mode, this.Grid1L);
		}
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
    TypeL = new JLabel();
    MainP = new JPanel();
    Grid1 = new JPanel();
    Grid2 = new JPanel();
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
    ModeTF = new JTextField();
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
    StatusP = new JPanel();
    P2StatusP = new JPanel();
    P2StatusTitleL = new JLabel();
    P2SLL = new JLabel();
    P2SLTF = new JTextField();
    P2SFL = new JLabel();
    P2SFTF = new JTextField();
    P2AccL = new JLabel();
    P2AccTF = new JTextField();
    P2HitsL = new JLabel();
    P2HitsTF = new JTextField();
    P1StatusP = new JPanel();
    P1StatusTitleL = new JLabel();
    P1SLL = new JLabel();
    P1SLTF = new JTextField();
    P1SFL = new JLabel();
    P1SFTF = new JTextField();
    P1AccL = new JLabel();
    P1AccTF = new JTextField();
    P1HitsL = new JLabel();
    P1HitsTF = new JTextField();

    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setBackground(new Color(255, 255, 0));
    setResizable(false);

    TypeL.setHorizontalAlignment(SwingConstants.CENTER);
    TypeL.setText("PvE - Sandbox");

    GroupLayout TitlePLayout = new GroupLayout(TitleP);
    TitleP.setLayout(TitlePLayout);
    TitlePLayout.setHorizontalGroup(TitlePLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(TitlePLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(TypeL, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addContainerGap())
    );
    TitlePLayout.setVerticalGroup(TitlePLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(TitlePLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(TypeL)
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    Grid1.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
    Grid1.setLayout(new GridLayout(this.boardSize, this.boardSize, 0, 0));

    Grid2.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
    Grid2.setLayout(new GridLayout(this.boardSize, this.boardSize, 0, 0));

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

    ModeTF.setEditable(false);
    ModeTF.setText("Classic");

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
        nextRound(evt);
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
          .addGroup(RoundStatusPLayout.createSequentialGroup()
            .addGroup(RoundStatusPLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
              .addComponent(ModeL, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(GirdL, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(RoundStatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
              .addComponent(ModeTF)
              .addComponent(GridTF, GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)))
          .addGroup(RoundStatusPLayout.createSequentialGroup()
            .addGroup(RoundStatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
              .addComponent(ShipsNoL, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
              .addComponent(RoundL, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(RoundStatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
              .addComponent(RoundTF)
              .addComponent(ShipNoTF))))
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
        .addGap(9, 9, 9)
        .addGroup(RoundStatusPLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(ModeTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addComponent(ModeL, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
        .addGap(8, 8, 8)
        .addComponent(AlertsSP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(ButtonsP, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
        .addContainerGap())
    );

    GroupLayout MainPLayout = new GroupLayout(MainP);
    MainP.setLayout(MainPLayout);
    MainPLayout.setHorizontalGroup(MainPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(MainPLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(Grid1, GroupLayout.PREFERRED_SIZE, 450, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(RoundStatusP, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(Grid2, GroupLayout.PREFERRED_SIZE, 450, GroupLayout.PREFERRED_SIZE)
        .addContainerGap())
    );
    MainPLayout.setVerticalGroup(MainPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(MainPLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(MainPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
          .addComponent(RoundStatusP, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addGroup(MainPLayout.createSequentialGroup()
            .addGroup(MainPLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
              .addComponent(Grid2, GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
              .addComponent(Grid1, GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE))
            .addGap(0, 0, Short.MAX_VALUE)))
        .addContainerGap())
    );

    P2StatusP.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));

    P2StatusTitleL.setHorizontalAlignment(SwingConstants.CENTER);
    P2StatusTitleL.setText("Player 2 Status");

    P2SLL.setText("Ships Left:");

    P2SLTF.setEditable(false);

    P2SFL.setText("Shots Fired:");

    P2SFTF.setEditable(false);

    P2AccL.setText("Accuracy:");

    P2AccTF.setEditable(false);

    P2HitsL.setText("Hits Landed:");

    P2HitsTF.setEditable(false);

    GroupLayout P2StatusPLayout = new GroupLayout(P2StatusP);
    P2StatusP.setLayout(P2StatusPLayout);
    P2StatusPLayout.setHorizontalGroup(P2StatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(P2StatusPLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(P2StatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
          .addComponent(P2StatusTitleL, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addGroup(P2StatusPLayout.createSequentialGroup()
            .addGroup(P2StatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
              .addComponent(P2SLL, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(P2AccL, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGap(18, 18, 18)
            .addGroup(P2StatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
              .addComponent(P2SLTF, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
              .addComponent(P2AccTF, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
            .addGroup(P2StatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
              .addComponent(P2HitsL, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(P2SFL, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGap(18, 18, 18)
            .addGroup(P2StatusPLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
              .addComponent(P2HitsTF, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
              .addComponent(P2SFTF, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))))
        .addContainerGap())
    );
    P2StatusPLayout.setVerticalGroup(P2StatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(P2StatusPLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(P2StatusTitleL)
        .addGap(18, 18, 18)
        .addGroup(P2StatusPLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(P2SLL)
          .addComponent(P2SLTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addComponent(P2SFL)
          .addComponent(P2SFTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        .addGap(18, 18, 18)
        .addGroup(P2StatusPLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(P2AccL)
          .addComponent(P2HitsTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addComponent(P2HitsL)
          .addComponent(P2AccTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    P1StatusP.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));

    P1StatusTitleL.setHorizontalAlignment(SwingConstants.CENTER);
    P1StatusTitleL.setText("Player 1 Status");

    P1SLL.setText("Ships Left:");

    P1SLTF.setEditable(false);

    P1SFL.setText("Shots Fired:");

    P1SFTF.setEditable(false);

    P1AccL.setText("Accuracy:");

    P1AccTF.setEditable(false);

    P1HitsL.setText("Hits Landed:");

    P1HitsTF.setEditable(false);

    GroupLayout P1StatusPLayout = new GroupLayout(P1StatusP);
    P1StatusP.setLayout(P1StatusPLayout);
    P1StatusPLayout.setHorizontalGroup(P1StatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(P1StatusPLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(P1StatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
          .addComponent(P1StatusTitleL, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addGroup(P1StatusPLayout.createSequentialGroup()
            .addGroup(P1StatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
              .addComponent(P1SLL, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(P1AccL, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGap(18, 18, 18)
            .addGroup(P1StatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
              .addComponent(P1SLTF, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
              .addComponent(P1AccTF, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
            .addGroup(P1StatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
              .addComponent(P1HitsL, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(P1SFL, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGap(18, 18, 18)
            .addGroup(P1StatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
              .addComponent(P1HitsTF, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
              .addComponent(P1SFTF, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))))
        .addContainerGap())
    );
    P1StatusPLayout.setVerticalGroup(P1StatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(P1StatusPLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(P1StatusTitleL)
        .addGap(18, 18, 18)
        .addGroup(P1StatusPLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(P1SLL)
          .addComponent(P1SLTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addComponent(P1SFTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addComponent(P1SFL))
        .addGap(18, 18, 18)
        .addGroup(P1StatusPLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(P1AccL)
          .addComponent(P1AccTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addComponent(P1HitsL)
          .addComponent(P1HitsTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    GroupLayout StatusPLayout = new GroupLayout(StatusP);
    StatusP.setLayout(StatusPLayout);
    StatusPLayout.setHorizontalGroup(StatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(StatusPLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(P1StatusP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(P2StatusP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addContainerGap())
    );
    StatusPLayout.setVerticalGroup(StatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(GroupLayout.Alignment.TRAILING, StatusPLayout.createSequentialGroup()
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addGroup(StatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
          .addComponent(P2StatusP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addComponent(P1StatusP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
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
	 */
	private void goToPost() {
		int[] temp = new int[] {(this.roundStatus ? 1 : 0), this.P1Stats[0], this.P1Stats[1], this.P2Stats[1], this.P2Stats[2], this.P1Stats[2]};

		Post Post = new Post(temp, this.mode, this.AI1Diff);
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
	private void nextRound(ActionEvent evt) {//GEN-FIRST:event_nextRound
		if (this.type == 1) {            // Checks if it a PvE Round
			if (this.roundNo != 0) {       // Checks if it isn't the Ship Placement round.
				if ((this.mode.equals("C") && this.timesClicked == 1) || (this.mode.equals("S") && this.timesClicked == (this.shipNos - this.P1Stats[2]))) { // Checks if the required number of selections have been made
					// TODO: Make user shoot.

					this.roundStatus = (this.shipNos - this.P2Stats[2]) == 0; // Variable storing whether the user won or not.

					if (this.roundStatus) {    // Checks if the user won.
						this.goToPost();
					} else {                   // User did not win. AI shoots.
						// TODO: Make AI shoot.

						if (!this.roundStatus) { // AI won.
							this.goToPost();
						} else {                 // AI did not win. Next round.
							this.roundNo += 1;
							this.RoundTF.setText("" + this.roundNo);

							this.P1StatsUpdate(false);
							this.P2StatsUpdate(false);

							// TODO: Other stuff.
						}

					}
				} else {
					this.AlertsTA.append("\tNot all selections made! " + (this.mode.equals("C") ? 1 : (this.shipNos - this.P1Stats[2])) + " selections left!\n");
				}
			} else {                       // Stuff to do during ship placement.
				/**
				 * TODO: Fix this. Used for multi-tile ships.
				 *
				 * <code>
				 * <pre>
				 *	if (this.shipNo == 0) {         // Checks if no ship has been placed yet.
				 *		if (this.timesClicked == 0) { // Checks if it the first click
				 *			this.AlertsTA.append("Ship Placement:\n");
				 *			this.AlertsTA.append("\tPlace the initial point of the current ship first.\n");
				 *			this.AlertsTA.append("\tThen place the final point.\n");
				 *			this.AlertsTA.append("\tThe final point must be vertically or horizontally aligned, and must be as long as the ship's length.\n");
				 *
				 *			int shipLength = this.shipLengths[this.shipNo];
				 *
				 *			while (shipLength == 0) {
				 *				this.shipNo += 1;
				 *				shipLength = this.shipLengths[this.shipNo];
				 *			}
				 *
				 *			System.out.println("Placing ship no. " + this.shipsPlaced + " - " + shipLength);
				 *			this.AlertsTA.append("\tPlacing ship no. " + this.shipsPlaced + " of length " + shipLength);
				 *		} else {                         // Not the first click
				 *			if (true) {
				 *				// TODO: Do something
				 *			}
				 *		}
				 *	}
				 * </pre>
				 * </code>
				 */
				if (this.shipsPlaced == this.shipNos) { // Checks if all ships have been placed.
					for (int y = 0; y < this.boardSize; y++) {
						for (int x = 0; x < this.boardSize; x++) {
							if (this.buttonsClicked[y][x]) {  // Ship is placed here.
								this.Grid1L[y][x].hasShip();
								this.Grid1B[y][x].setBackground(new Color(176, 196, 222, 255));
							} else {                          // Ship isn't placed here. Is this statement required?
								this.Grid1B[y][x].setBackground(new Color(5, 218, 255, 255));
							}
						}
					}

					this.roundNo += 1;
					this.RoundTF.setText("" + this.roundNo);

					this.P1StatsUpdate(true);
					this.P2StatsUpdate(true);

					this.NextB.setText("<html>Next<br/>Round</html");

					// TODO: Initialize the AI.
				} else {                     // Not all ships placed.
					this.AlertsTA.append("\tNot all ships placed! " + this.shipsPlaced + " ships left!\n");
				}
			}
		} else {                         // Stuff to do during a EvE Round
			// TODO: Do stuff.
		}
	}//GEN-LAST:event_nextRound

	/**
	 * A series of JOptionPanes/JMessage Dialogs for explaining the game.
	 *
	 * @param evt Button Click
	 */
	private void Help(ActionEvent evt) {//GEN-FIRST:event_Help
		// TODO: Do stuff
	}//GEN-LAST:event_Help

	/**
	 * Sets all ShipP CheckBoxes to their desired state every time one of them is clicked.
	 * Also called during the constructor.
	 *
	 * @param evt Button Click
	 */
	private void ShipCBClicked(ActionEvent evt) {//GEN-FIRST:event_ShipCBClicked
		this.BattleshipCB.setSelected(initVars[1]);
		this.CruiserCB.setSelected(initVars[2]);
		this.DestroyerCB.setSelected(initVars[3]);
		this.PatrolCB.setSelected(initVars[4]);
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
				if (this.Grid1L[this.initLoc[0] + l][this.initLoc[1]].hasShip()) { // Y-Coordinate is incremented if vertical
					System.out.println("Ship exists at vertical length: " + (l + 1));
					this.AlertsTA.append("\t\tA Ship exists at vertical length " + (l + 1) + " from the initial point!\n");
					return;
				}
			}
		} else {                                                               // Horizontal Orientation
			for (int l = 0; l < shipLength; l++) {
				if (this.Grid1L[this.initLoc[0]][this.initLoc[1] + l].hasShip()) { // X-Coordinate is incremented if horizontal
					System.out.println("Ship exists at horizontal length: " + (l + 1));
					this.AlertsTA.append("\t\tA Ship exists at horizontal length " + (l + 1) + " from the initial point!\n");
					return;
				}
			}
		}

		// Sets the location values
		if (this.direction) {                                                // Verical Orientation
			for (int l = 0; l < shipLength; l++) {
				this.Grid1L[this.initLoc[0] + l][this.initLoc[1]].shipPresent(); // Y-Coordinate is incremented if vertical
			}
		} else {                                                             // Horizontal Orientation
			for (int l = 0; l < shipLength; l++) {
				this.Grid1L[this.initLoc[0]][this.initLoc[1] + l].shipPresent(); // X-Coordinate is incremented if horizontal
			}
		}

		System.out.println("Ship no. " + this.shipsPlaced + " placed");
		this.AlertsTA.append("\tShip no. " + this.shipsPlaced + " placed");

	}

	/**
	 * ActionEvent handler for the buttons in Grid 1 for PvE matches.
	 *
	 * @param evt Button Click
	 */
	private void ButtonHandler(ActionEvent evt) {
		JButton button = (JButton) evt.getSource();
		String coords = button.getActionCommand();

		System.out.println(coords);

		if (type == 1) {                                     // Checks if its a PvE match.
			int[] xy = this.extractCoordinates(coords);

			if (this.roundNo == 0) {                           // Checks if it is the Ship Placement Round.
				if (coords.charAt(0) == '1') {                   // Checks if the button clicked is from Grid 1.
					if (this.buttonsClicked[xy[1]][xy[0]]) {       // Checks if the button has already been clicked.
						button.setBackground(new Color(5, 218, 255, 255));
						this.buttonsClicked[xy[1]][xy[0]] = false;
						this.timesClicked -= 1;
						this.shipsPlaced -= 1;
					} else {                                       // Button hasn't been clicked.
						if (this.shipsPlaced < this.shipNos) {       // Checks if the number of ships placed is less than the max ships available.
							button.setBackground(new Color(242, 236, 0, 255));
							this.buttonsClicked[xy[1]][xy[0]] = true;
							this.timesClicked += 1;
							this.shipsPlaced += 1;
						} else {                                     // More ships than limit allowed added.
							this.AlertsTA.append("\tShip limit reached!\n");
						}
					}
				} else {                                         // Grid 2.
					this.AlertsTA.append("\tWrong grid!\n");
				}
			} else {                                           // Not Ship Placement Round. Has to be from Grid 2 in this case.
				if (coords.charAt(0) == '2') {                   // Checks if the button clicked is from Grid 2
					if (this.Grid2L[xy[1]][xy[0]].isUnguessed()) { // Checks if the location is unguessed.
						if (this.mode.equals("C")) {                 // Checks if the match is in Classic Mode.
							if (this.buttonsClicked[xy[1]][xy[0]]) {   // Checks if the button has already been clicked.
								button.setBackground(new Color(5, 218, 255, 255));
								this.buttonsClicked[xy[1]][xy[0]] = false;
								this.timesClicked -= 1;
							} else {                                   // Button hasn't been clicked.
								if (this.timesClicked == 0) {            // Checks if another location hasn't been selected.
									button.setBackground(new Color(242, 236, 0, 255));
									this.buttonsClicked[xy[1]][xy[0]] = true;
									this.timesClicked += 1;
								} else {                                 // Another location has been selected.
									this.AlertsTA.append("\tMaximum locations selected!\n");
								}
							}
						} else {                                     // Salvo Mode.
							if (this.buttonsClicked[xy[1]][xy[0]]) {   // Checks if the button has already been clicked.
								button.setBackground(new Color(5, 218, 255, 255));
								this.buttonsClicked[xy[1]][xy[0]] = false;
								this.timesClicked -= 1;
							} else {                                   // Button hasn't been clicked.
								if (this.timesClicked == (this.shipNos - this.P1Stats[2])) { // Checks if max locations haven't been selected.
									button.setBackground(new Color(242, 236, 0, 255));
									this.buttonsClicked[xy[1]][xy[0]] = true;
									this.timesClicked += 1;
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

		} else {                                             // EvE Match.
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
	 */
	private void ButtonHandlerAI() {
		// TODO: Do stuff.
	}

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private JScrollPane AlertsSP;
  private JTextArea AlertsTA;
  private JCheckBox BattleshipCB;
  private JPanel ButtonsP;
  private JCheckBox CruiserCB;
  private JCheckBox DestroyerCB;
  private JButton ExitB;
  private JLabel GirdL;
  private JPanel Grid1;
  private JPanel Grid2;
  private JTextField GridTF;
  private JButton HelpB;
  private JPanel MainP;
  private JLabel ModeL;
  private JTextField ModeTF;
  private JButton NextB;
  private JLabel P1AccL;
  private JTextField P1AccTF;
  private JLabel P1HitsL;
  private JTextField P1HitsTF;
  private JLabel P1SFL;
  private JTextField P1SFTF;
  private JLabel P1SLL;
  private JTextField P1SLTF;
  private JPanel P1StatusP;
  private JLabel P1StatusTitleL;
  private JLabel P2AccL;
  private JTextField P2AccTF;
  private JLabel P2HitsL;
  private JTextField P2HitsTF;
  private JLabel P2SFL;
  private JTextField P2SFTF;
  private JLabel P2SLL;
  private JTextField P2SLTF;
  private JPanel P2StatusP;
  private JLabel P2StatusTitleL;
  private JCheckBox PatrolCB;
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
  private JPanel TitleP;
  private JLabel TypeL;
  // End of variables declaration//GEN-END:variables
	// Start of special variables declaration
	private JButton[][] Grid1B;
	private JButton[][] Grid2B;
	// End of special variables decralation
}
