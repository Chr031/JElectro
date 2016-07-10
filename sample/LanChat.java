import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import com.jelectro.JElectro;
import com.jelectro.connector.multicast.MulticastConnector.ConnectionState;
import com.jelectro.exception.JElectroException;
import com.jelectro.stubs.StubSet;

public class LanChat extends JFrame {
	private JTextArea taChat;
	private JTextField tfText;
	private JElectro jeNode;
	private StubSet<Hears> stubSet;
	private int idNumber;
private Hears hears;

	public static void main(String[] args) throws IOException, JElectroException {
		
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

	public LanChat(boolean isMain) throws IOException, JElectroException {
		super("Lan chat ...");
		getContentPane().setBackground(Color.WHITE);
		this.setDefaultCloseOperation(isMain ? EXIT_ON_CLOSE : DISPOSE_ON_CLOSE);
		buildMainPanel();
		setSize (600,400);
		
		startConnection();
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

		JButton lblTalkInviter = new JButton(" > ");
		lblTalkInviter.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblTalkInviter = new GridBagConstraints();
		gbc_lblTalkInviter.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblTalkInviter.insets = new Insets(0, 0, 0, 5);
		gbc_lblTalkInviter.gridx = 0;
		gbc_lblTalkInviter.gridy = 0;
		panel.add(lblTalkInviter, gbc_lblTalkInviter);
		lblTalkInviter.addActionListener((e)-> hears.hi(""+this.idNumber));
		

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
		btSend.addActionListener((e)->say(tfText.getText()));

	}

	private void startConnection() throws IOException, JElectroException {
		idNumber = new Random(System.nanoTime()).nextInt(10000);
		jeNode = new JElectro("ChatNode-"+idNumber);
		ConnectionState cs = jeNode.startLanDiscovery(12345,12001,12100 );
		hears = new HearsImpl();
		hears.hi("From " + idNumber);
		jeNode.bind("hears-" + idNumber, hears);
		
	}

	
	
	public void say(String message)  {		
		stubSet.forEach((h)-> h.tellMe(message));	
		System.out.println(stubSet.size());
	}
	
	
	public interface Hears {
		
		public void hi(String from);	
		public void tellMe(String message) ;
		
		
	}
	
	public class HearsImpl implements Hears {

		public void hi(String from)  {
			boolean isNull = stubSet == null;
			try {
				stubSet = jeNode.lookup("hears-[0-9]+", Hears.class);
			} catch (IOException | JElectroException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			taChat.append("\n hi from " + from);
			
			if (isNull) stubSet.forEach((h)-> h.hi("" + idNumber));
			
		}
		
		
		@Override
		public void tellMe(String message) {
			taChat.append("\n >> "+ message);
		}	
		
	}

}
