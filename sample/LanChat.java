import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JSplitPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.UIManager;

import java.awt.FlowLayout;
import javax.swing.JTextField;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Color;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.UIManager.LookAndFeelInfo;

public class LanChat extends JFrame {
	private JTextArea taChat;
	private JTextField tfText;

	public static void main(String[] args) {
		
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			// If Nimbus is not available, you can set the GUI to another look
			// and feel.
		}

		
		new LanChat(true).setVisible(true);
	}

	public LanChat(boolean isMain) {
		super("Lan chat ...");
		getContentPane().setBackground(Color.WHITE);
		this.setDefaultCloseOperation(isMain ? EXIT_ON_CLOSE : DISPOSE_ON_CLOSE);
		buildMainPanel();
		setSize (600,400);

	}

	private void buildMainPanel() {
		BorderLayout borderLayout = new BorderLayout();
		borderLayout.setVgap(5);
		borderLayout.setHgap(5);
		getContentPane().setLayout(borderLayout);
		taChat = new JTextArea();
		taChat.setEditable(false);
		taChat.setWrapStyleWord(true);
		taChat.setLineWrap(true);

		getContentPane().add(taChat, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		getContentPane().add(panel, BorderLayout.SOUTH);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 8, 86, 0, 0 };
		gbl_panel.rowHeights = new int[] { 20, 0 };
		gbl_panel.columnWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		JLabel lblTalkInviter = new JLabel(">");
		lblTalkInviter.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblTalkInviter = new GridBagConstraints();
		gbc_lblTalkInviter.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblTalkInviter.insets = new Insets(0, 0, 0, 5);
		gbc_lblTalkInviter.gridx = 0;
		gbc_lblTalkInviter.gridy = 0;
		panel.add(lblTalkInviter, gbc_lblTalkInviter);

		tfText = new JTextField();
		GridBagConstraints gbc_tfText = new GridBagConstraints();
		gbc_tfText.insets = new Insets(0, 0, 0, 5);
		gbc_tfText.fill = GridBagConstraints.BOTH;
		gbc_tfText.gridx = 1;
		gbc_tfText.gridy = 0;
		panel.add(tfText, gbc_tfText);
		tfText.setColumns(10);

		JButton btSend = new JButton("Send");
		GridBagConstraints gbc_btSend = new GridBagConstraints();
		gbc_btSend.anchor = GridBagConstraints.NORTH;
		gbc_btSend.gridx = 2;
		gbc_btSend.gridy = 0;
		panel.add(btSend, gbc_btSend);

	}

	class NetworkFinder extends Thread {

		public void run() {

		}

	}

}
