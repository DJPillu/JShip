package game;

import game.ai.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Dimension;
import jship.JShip;


/**
 * Form for playing the actual game.
 * Temporary class used for visually designing the GUI.
 *
 * @author blackk100
 */
public class Game extends JFrame {

	private final boolean[] initVars;
	private final int boardSize;
	private int shipNos = 0;
	private final int type;
	private final String mode;
	private final int AI1Diff;
	private final int AI2Diff;
	private final Sandbox[] AIS = new Sandbox[2];
	private final Regular[] AIR = new Regular[2];
	private final Brutal[] AIB = new Brutal[2];
	private Location[][] Grid1L;
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
	 */
	private int roundNo = 0;

	/**
	 * Creates new form Main
	 *
	 * @param initVars
	 * @param type
	 * @param mode
	 * @param AI1Diff
	 * @param AI2Diff
	 */
	public Game(boolean[] initVars, int type, String mode, int AI1Diff, int AI2Diff) {
		this.initVars = initVars;
		this.boardSize = initVars[0] ? 15 : 10;
		if (initVars[1]) {
			this.shipNos += 2;
		}
		if (initVars[2]) {
			this.shipNos += 2;
		}
		if (initVars[3]) {
			this.shipNos += 3;
		}
		if (initVars[4]) {
			this.shipNos += 5;
		}

		this.type = type;
		this.mode = mode;

		initComponents();

		this.GridTF.setText(initVars[0] ? "15 x 15" : "10 x 10");
		this.ModeTF.setText(mode.equals("C") ? "Classic" : "Salvo");

		if (type > 0) {
			this.TypeL.setText("PvE - " + (AI1Diff == -1 ? "Sandbox" : (AI1Diff == 0 ? "Realistic" : "Brutal")));
		} else {
			this.TypeL.setText("EvE - " + (AI1Diff == -1 ? "Sandbox" : (AI1Diff == 0 ? "Realistic" : "Brutal")) + " vs " + (AI2Diff == -1 ? "Sandbox" : (AI2Diff == 0 ? "Realistic" : "Brutal")));
		}

		this.BattleshipCB.setSelected(initVars[1]);
		this.CruiserCB.setSelected(initVars[2]);
		this.DestroyerCB.setSelected(initVars[3]);
		this.PatrolCB.setSelected(initVars[4]);
		this.ShipNoTF.setText("" + this.shipNos);

		this.initGrid1();
		this.initGrid2();

		this.AI1Diff = AI1Diff;
		this.AI2Diff = AI2Diff;

		this.P1StatsUpdate(true);
		this.P2StatsUpdate(true);
	}

	/**
	 * Updates Player 1 Statistics displays.
	 */
	private void P1StatsUpdate(boolean flag) {
		this.P1SLTF.setText("" + (this.shipNos - this.P1Stats[2]));
		this.P1SFTF.setText("" + this.P1Stats[0]);
		this.P1HitsTF.setText("" + this.P1Stats[1]);
		this.P1AccTF.setText("" + (float) (this.P1Stats[1] / (flag ? 1 : this.P1Stats[0])) + " %");
	}

	/**
	 * Updates Player 2 Statistics displays.
	 */
	private void P2StatsUpdate(boolean flag) {
		this.P2SLTF.setText("" + (this.shipNos - this.P2Stats[2]));
		this.P2SFTF.setText("" + this.P2Stats[0]);
		this.P2HitsTF.setText("" + this.P2Stats[1]);
		this.P2AccTF.setText("" + (float) (this.P2Stats[1] / (flag ? 1 : this.P2Stats[0])) + " %");
	}

	private void initGrid1() {
		this.Grid1B = new JButton[this.boardSize][this.boardSize];
		this.Grid1L = new Location[this.boardSize][this.boardSize];

		for (int y = 0; y < this.boardSize; y++) {
			for (int x = 0; x < this.boardSize; x++) {
				this.Grid1B[y][x] = new JButton("");
				this.Grid1B[y][x].setPreferredSize(new Dimension(this.initVars[0] ? 30 : 45, this.initVars[0] ? 30 : 45));
				this.Grid1B[y][x].setActionCommand("" + x + " " + y);
				this.Grid1B[y][x].setBackground(new Color(5, 218, 255, 255));
				this.Grid1B[y][x].addActionListener(this::ButtonHandler);
				this.Grid1B[y][x].setEnabled(type == 1);

				this.Grid1L[y][x] = new Location();
				this.Grid1.add(Grid1B[y][x]);
			}
		}
	}

	private void initGrid2() {
		this.Grid2B = new JButton[this.boardSize][this.boardSize];
		this.Grid2L = new Location[this.boardSize][this.boardSize];

		for (int y = 0; y < this.boardSize; y++) {
			for (int x = 0; x < this.boardSize; x++) {
				this.Grid2B[y][x] = new JButton("");
				this.Grid2B[y][x].setPreferredSize(new Dimension(this.initVars[0] ? 30 : 45, this.initVars[0] ? 30 : 45));
				this.Grid2B[y][x].setActionCommand("" + x + " " + y);
				this.Grid2B[y][x].setBackground(new Color(5, 218, 255, 255));
				this.Grid2B[y][x].addActionListener(this::ButtonHandler);
				this.Grid1B[y][x].setEnabled(type == 1);

				this.Grid2L[y][x] = new Location();
				this.Grid2.add(Grid2B[y][x]);
			}
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
		setResizable(false);

		TypeL.setHorizontalAlignment(SwingConstants.CENTER);
		TypeL.setText("PvE - Sandbox");

		GroupLayout TitlePLayout = new GroupLayout(TitleP);
		TitleP.setLayout(TitlePLayout);
		TitlePLayout.setHorizontalGroup(
			TitlePLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(TitlePLayout.createSequentialGroup()
				.addContainerGap()
				.addComponent(TypeL, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addContainerGap())
		);
		TitlePLayout.setVerticalGroup(
			TitlePLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
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
		ShipNoTF.setText("2");

		ShipsL.setLabelFor(ShipsP);
		ShipsL.setText("Ships Available:");

		ShipsP.setLayout(new GridLayout(2, 2, 10, 10));

		BattleshipCB.setText("Battleship");
		BattleshipCB.setEnabled(false);
		ShipsP.add(BattleshipCB);

		CruiserCB.setText("Cruiser");
		CruiserCB.setEnabled(false);
		ShipsP.add(CruiserCB);

		DestroyerCB.setText("Destroyer");
		DestroyerCB.setEnabled(false);
		ShipsP.add(DestroyerCB);

		PatrolCB.setText("Patrol Boat");
		PatrolCB.setEnabled(false);
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

		ButtonsP.setLayout(new GridLayout(2, 3, 10, 10));

		HelpB.setText("Help");
		ButtonsP.add(HelpB);
		ButtonsP.add(Spacer1L);

		NextB.setText("Next");
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
		RoundStatusPLayout.setHorizontalGroup(
			RoundStatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(RoundStatusPLayout.createSequentialGroup()
				.addContainerGap()
				.addGroup(RoundStatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(ButtonsP, GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE)
					.addComponent(ShipsP, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(RoundStatusL, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(ShipsL, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addGroup(RoundStatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(RoundStatusPLayout.createSequentialGroup()
							.addComponent(RoundL, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(RoundTF))
						.addGroup(GroupLayout.Alignment.TRAILING, RoundStatusPLayout.createSequentialGroup()
							.addGap(0, 0, Short.MAX_VALUE)
							.addGroup(RoundStatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
								.addGroup(RoundStatusPLayout.createSequentialGroup()
									.addGroup(RoundStatusPLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
										.addComponent(ModeL, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(GirdL, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
									.addGroup(RoundStatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
										.addComponent(ModeTF, GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
										.addComponent(GridTF)))
								.addGroup(RoundStatusPLayout.createSequentialGroup()
									.addComponent(ShipsNoL, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
									.addComponent(ShipNoTF)))
							.addGap(0, 0, 0))))
				.addContainerGap())
		);
		RoundStatusPLayout.setVerticalGroup(
			RoundStatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(GroupLayout.Alignment.TRAILING, RoundStatusPLayout.createSequentialGroup()
				.addContainerGap()
				.addComponent(RoundStatusL)
				.addGap(18, 18, 18)
				.addGroup(RoundStatusPLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(GirdL)
					.addComponent(GridTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGap(18, 18, 18)
				.addGroup(RoundStatusPLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(ModeL)
					.addComponent(ModeTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGap(18, 18, 18)
				.addComponent(ShipsL)
				.addGap(18, 18, 18)
				.addComponent(ShipsP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addGap(18, 18, 18)
				.addGroup(RoundStatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(ShipsNoL, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addComponent(ShipNoTF, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE))
				.addGap(18, 18, 18)
				.addGroup(RoundStatusPLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(RoundL)
					.addComponent(RoundTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGap(37, 37, 37)
				.addComponent(ButtonsP, GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
				.addGap(40, 40, 40))
		);

		GroupLayout MainPLayout = new GroupLayout(MainP);
		MainP.setLayout(MainPLayout);
		MainPLayout.setHorizontalGroup(
			MainPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(MainPLayout.createSequentialGroup()
				.addContainerGap()
				.addComponent(Grid1, GroupLayout.PREFERRED_SIZE, 450, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(RoundStatusP, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(Grid2, GroupLayout.PREFERRED_SIZE, 450, GroupLayout.PREFERRED_SIZE)
				.addContainerGap())
		);
		MainPLayout.setVerticalGroup(
			MainPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(MainPLayout.createSequentialGroup()
				.addContainerGap()
				.addGroup(MainPLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
					.addComponent(Grid2, GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
					.addComponent(RoundStatusP, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(Grid1, GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE))
				.addContainerGap(27, Short.MAX_VALUE))
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
		P2StatusPLayout.setHorizontalGroup(
			P2StatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
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
		P2StatusPLayout.setVerticalGroup(
			P2StatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
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
		P1StatusPLayout.setHorizontalGroup(
			P1StatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
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
		P1StatusPLayout.setVerticalGroup(
			P1StatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
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
		StatusPLayout.setHorizontalGroup(
			StatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(StatusPLayout.createSequentialGroup()
				.addContainerGap()
				.addComponent(P1StatusP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(P2StatusP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addContainerGap())
		);
		StatusPLayout.setVerticalGroup(
			StatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(GroupLayout.Alignment.TRAILING, StatusPLayout.createSequentialGroup()
				.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addGroup(StatusPLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(P2StatusP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addComponent(P1StatusP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addContainerGap())
		);

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
				.addContainerGap()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(TitleP, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(MainP, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(StatusP, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				.addContainerGap())
		);
		layout.setVerticalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
				.addContainerGap()
				.addComponent(TitleP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addGap(18, 18, 18)
				.addComponent(MainP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addGap(18, 18, 18)
				.addComponent(StatusP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);

		pack();
	}// </editor-fold>//GEN-END:initComponents

	private void ButtonHandler(ActionEvent evt) {
		JButton button = (JButton) evt.getSource();

		button.setBackground(new Color(5, 218, 229, 255));
		System.out.println(button.getActionCommand());
	}

	private void Exit(ActionEvent evt) {//GEN-FIRST:event_Exit
		JShip JShip = new JShip(); // Creates the JShip Form object
		JShip.setVisible(true);	   // Makes the JShip Form to be visible

		this.dispose();            // Destroys the current form object
	}//GEN-LAST:event_Exit

	private void nextRound(ActionEvent evt) {//GEN-FIRST:event_nextRound
		this.roundNo += 1;
		this.RoundTF.setText("" + this.roundNo);

		this.P1StatsUpdate(false);
		this.P2StatsUpdate(false);

		// TODO: Other stuff
	}//GEN-LAST:event_nextRound

	// Variables declaration - do not modify//GEN-BEGIN:variables
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
