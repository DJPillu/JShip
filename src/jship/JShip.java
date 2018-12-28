package jship;

import game.Pre;
import java.awt.GridLayout;
import java.awt.event.*;
import javax.swing.*;
import users.CurrentUser;


/**
 * Main form that runs when the program is started
 *
 * @author blackk100
 */
public final class JShip extends JFrame {

	/**
	 * Creates new form JShip
	 */
	public JShip() {
		this.initComponents();
		UserTF.setText(CurrentUser.getCurrentUser());
		this.setLocationRelativeTo(null);
	}

	/**
	 * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
	 * content of this method is always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    HomeP = new JPanel();
    TitleP = new JPanel();
    TitleL = new JLabel();
    UserL = new JLabel();
    UserTF = new JTextField();
    ButtonP = new JPanel();
    UserB = new JButton();
    Spacer1L = new JLabel();
    StatsB = new JButton();
    Spacer2L = new JLabel();
    PlayB = new JButton();
    Spacer3L = new JLabel();
    HelpB = new JButton();
    Spacer4L = new JLabel();
    ExitB = new JButton();

    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setTitle("JShip");
    setResizable(false);

    TitleL.setHorizontalAlignment(SwingConstants.CENTER);
    TitleL.setText("JShip");

    UserL.setLabelFor(UserTF);
    UserL.setText("Current User:");

    UserTF.setEditable(false);

    GroupLayout TitlePLayout = new GroupLayout(TitleP);
    TitleP.setLayout(TitlePLayout);
    TitlePLayout.setHorizontalGroup(TitlePLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(TitlePLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(TitlePLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
          .addComponent(TitleL, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addGroup(TitlePLayout.createSequentialGroup()
            .addComponent(UserL)
            .addGap(18, 18, 18)
            .addComponent(UserTF)))
        .addContainerGap())
    );
    TitlePLayout.setVerticalGroup(TitlePLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(TitlePLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(TitleL)
        .addGap(18, 18, 18)
        .addGroup(TitlePLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(UserL)
          .addComponent(UserTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    ButtonP.setLayout(new GridLayout(3, 3, 10, 10));

    UserB.setText("User Management");
    UserB.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        ChangeUser(evt);
      }
    });
    ButtonP.add(UserB);
    ButtonP.add(Spacer1L);

    StatsB.setText("Statistics");
    StatsB.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        ShowStats(evt);
      }
    });
    ButtonP.add(StatsB);
    ButtonP.add(Spacer2L);

    PlayB.setText("Play");
    PlayB.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        Play(evt);
      }
    });
    ButtonP.add(PlayB);
    ButtonP.add(Spacer3L);

    HelpB.setText("Help");
    HelpB.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        Help(evt);
      }
    });
    ButtonP.add(HelpB);
    ButtonP.add(Spacer4L);

    ExitB.setText("Exit");
    ExitB.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        Exit(evt);
      }
    });
    ButtonP.add(ExitB);

    GroupLayout HomePLayout = new GroupLayout(HomeP);
    HomeP.setLayout(HomePLayout);
    HomePLayout.setHorizontalGroup(HomePLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(GroupLayout.Alignment.TRAILING, HomePLayout.createSequentialGroup()
        .addGroup(HomePLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
          .addComponent(ButtonP, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(TitleP, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addContainerGap())
    );
    HomePLayout.setVerticalGroup(HomePLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(HomePLayout.createSequentialGroup()
        .addComponent(TitleP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addGap(18, 18, 18)
        .addComponent(ButtonP, GroupLayout.PREFERRED_SIZE, 125, GroupLayout.PREFERRED_SIZE)
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    GroupLayout layout = new GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(HomeP, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addContainerGap())
    );
    layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(HomeP, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

	/**
	 * Changes the current user.
	 *
	 * @param evt Button Click
	 */
	private void ChangeUser(ActionEvent evt) {//GEN-FIRST:event_ChangeUser
		ChangeUser ChangeUser = new ChangeUser(); // Creates the ChangeUser Form object
		ChangeUser.setVisible(true);              // Makes the ChangeUser Form to be visible

		this.dispose();                           // Destroys the current form object
	}//GEN-LAST:event_ChangeUser

	/**
	 * Shows the statistics for the current user.
	 *
	 * @param evt Button Click
	 */
	private void ShowStats(ActionEvent evt) {//GEN-FIRST:event_ShowStats
		ViewStats ViewStats = new ViewStats(); // Creates the ViewStats Form object
		ViewStats.setVisible(true);            // Makes the ViewStats Form to be visible

		this.dispose();                        // Destroys the current form object
	}//GEN-LAST:event_ShowStats

	/**
	 * Starts the game.
	 *
	 * @param evt Button Click
	 */
	private void Play(ActionEvent evt) {//GEN-FIRST:event_Play
		Pre Pre = new Pre();  // Creates the Pre Form object
		Pre.setVisible(true); // Makes the Pre Form to be visible

		this.dispose();       // Destroys the current form object
	}//GEN-LAST:event_Play

	/**
	 * Exits the program.
	 *
	 * @param evt Button Click
	 */
	private void Exit(ActionEvent evt) {//GEN-FIRST:event_Exit
		if (JOptionPane.showConfirmDialog(null, "Are you sure?", "Abandon Ship?", JOptionPane.YES_NO_OPTION) == 0) {
			System.exit(0);
		}
	}//GEN-LAST:event_Exit

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
	 * Sets the Nimbus look and feel.
	 * If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel. For details see
	 * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
	 *
	 * Creates and displays the form.
	 *
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		try {
			for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			java.util.logging.Logger.getLogger(JShip.class.getName()).log(java.util.logging.Level.SEVERE, null, e);
		}

		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				new JShip().setVisible(true);
			}
		});
	}

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private JPanel ButtonP;
  private JButton ExitB;
  private JButton HelpB;
  private JPanel HomeP;
  private JButton PlayB;
  private JLabel Spacer1L;
  private JLabel Spacer2L;
  private JLabel Spacer3L;
  private JLabel Spacer4L;
  private JButton StatsB;
  private JLabel TitleL;
  private JPanel TitleP;
  private JButton UserB;
  private JLabel UserL;
  private JTextField UserTF;
  // End of variables declaration//GEN-END:variables
}
