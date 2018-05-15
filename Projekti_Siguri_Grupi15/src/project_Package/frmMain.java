package project_Package;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import net.proteanit.sql.DbUtils;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import javax.swing.ScrollPaneConstants;
import java.awt.Font;


public class frmMain extends JFrame {

	private JPanel contentPane;

	// Objekti per lidhje me db
	Connection conn = null;
	// Objekti per vendosjen e rezultatit
	ResultSet res = null;
	// Objekti per pyetsore
	PreparedStatement pst = null;
	private JTextField txtPerdoruesi;
	private JTextField txtFjalekalimi;
	private JTable tblPerdoruesit;
	private int ID;
	private JTextField txtEncrypt;
	private JTextField txtDecrypt;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frmMain frame = new frmMain();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
    String keyval = "Siguria1";
    
	public static int[] encrypt(String str, String key) {
		String str1 = null;
		for	(int i =0;i<str.length();i++) {
			int pozita=str.charAt(i)-'A';
			int pozitaRe=pozita+6;
			str1+=((char) (pozitaRe+'A'));
					
		}
		
        int[] output = new int[str.length()];
        for(int i = 0; i < str.length(); i++) {
        	
            int o = (Integer.valueOf(str1.charAt(i)) ^ Integer.valueOf(key.charAt(i % (key.length() - 1)))) + '0';
            output[i] = o;
        }
        return output;        
    }
	   public static String decrypt(int[] input, String key) {
	        String output = "";        
	        String str1=null;
	        for(int i = 0; i < input.length; i++) {
	            output += (char) ((input[i] - 48) ^ (int) key.charAt(i % (key.length() - 1)));
	        }
	        for(int i=0;i<input.length;i++) {
	        	int pozita=output.charAt(i)-'A';
	        	int pozitaRe=pozita-6;
	        	pozitaRe=(pozitaRe+26)%26;
	        	str1+=(char)(pozitaRe+'A');
	        }
	        return str1;
	    }
	/**
	 * Create the frame.
	 * 
	 * 
	 */

	public void updateTable() {

		try {

			String sql = "SELECT id as ID, perdoruesi as Perdoruesi, fjalekalimi as Fjalekalimi FROM tblperdoruesit";
			pst = conn.prepareStatement(sql);
			res = pst.executeQuery();

			// Duhet te importohet libraria rs2xml per DBUtils
			tblPerdoruesit.setModel(DbUtils.resultSetToTableModel(res));

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Gabim gjate update te tabeles. " + e.getMessage());
		}

	}

	public frmMain() {
		setBackground(Color.LIGHT_GRAY);

		
		conn = MysqlCon.connectionFiekDb();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 748, 490);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(255, 228, 225));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblNewLabel = new JLabel("Programi per Enkriptimin e passwordit ");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(214, 29, 314, 26);
		contentPane.add(lblNewLabel);

		JLabel lblPerdoruesi = new JLabel("Perdoruesi: ");
		lblPerdoruesi.setFont(new Font("Tahoma", Font.BOLD, 10));
		lblPerdoruesi.setBounds(10, 116, 90, 13);
		contentPane.add(lblPerdoruesi);

		JLabel lblFjalekalimi = new JLabel("Fjalekalimi: ");
		lblFjalekalimi.setFont(new Font("Tahoma", Font.BOLD, 10));
		lblFjalekalimi.setBounds(10, 170, 90, 13);
		contentPane.add(lblFjalekalimi);

		txtPerdoruesi = new JTextField();
		txtPerdoruesi.setBounds(81, 113, 221, 19);
		contentPane.add(txtPerdoruesi);
		txtPerdoruesi.setColumns(10);

		txtFjalekalimi = new JTextField();
		txtFjalekalimi.setColumns(10);
		txtFjalekalimi.setBounds(81, 167, 221, 19);
		contentPane.add(txtFjalekalimi);

		JButton btnInsert = new JButton("Insert");
		btnInsert.setBackground(new Color(255, 160, 122));
		btnInsert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				try {

					String sql = "INSERT INTO tblperdoruesit (id, perdoruesi, fjalekalimi) VALUES " + "(default, '"
							+ txtPerdoruesi.getText() + "','" + txtEncrypt.getText() + "')";

					pst = conn.prepareStatement(sql);
					pst.execute();
					pst.close();
					txtPerdoruesi.setText("");
					txtFjalekalimi.setText("");
					txtEncrypt.setText("");
					txtDecrypt.setText("");
					updateTable();

				} catch (Exception e) {
					System.out.println(e.getMessage());
				}

			}
		});
		btnInsert.setBounds(616, 334, 106, 21);
		contentPane.add(btnInsert);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {

				DefaultTableModel model = (DefaultTableModel) tblPerdoruesit.getModel();
				ID = (int) model.getValueAt(tblPerdoruesit.getSelectedRow(), 0);

				try {

					String sql = "SELECT * FROM tblperdoruesit WHERE id='" + ID + "'";
					pst = conn.prepareStatement(sql);
					res = pst.executeQuery();

					while (res.next()) {
						txtPerdoruesi.setText(res.getString("perdoruesi"));
						txtFjalekalimi.setText(res.getString("fjalekalimi"));
					}

				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Gabim gjate selektimit te dhenave ne DB." + e.getMessage());
				}

			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
			}
		});
		scrollPane.setBounds(323, 109, 399, 194);
		contentPane.add(scrollPane);

		tblPerdoruesit = new JTable();
		tblPerdoruesit.setBackground(new Color(255, 245, 238));
		scrollPane.setViewportView(tblPerdoruesit);
		
		
	    
	    
		JButton btnEncrypt = new JButton("Encrypt");
		btnEncrypt.setBackground(new Color(255, 160, 122));
		btnEncrypt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				int [] encrypted=encrypt(txtFjalekalimi.getText(),keyval);
				for(int i = 0; i < encrypted.length; i++) {
					txtEncrypt.setText(encrypted.toString());
				}
					
			//Kodi per enkripitm

			}
		});
		btnEncrypt.setBounds(196, 208, 106, 21);
		contentPane.add(btnEncrypt);

		JButton btnDecypt = new JButton("Decrypt");
		btnDecypt.setBackground(new Color(255, 160, 122));
		btnDecypt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int [] encrypted=encrypt(txtFjalekalimi.getText(),keyval);
				txtDecrypt.setText((decrypt(encrypted,keyval)));
			//kodi per dekriptim
				}

			
		});
		btnDecypt.setBounds(196, 324, 106, 21);
		contentPane.add(btnDecypt);
		
		txtEncrypt = new JTextField();
		txtEncrypt.setColumns(10);
		txtEncrypt.setBounds(81, 274, 221, 19);
		contentPane.add(txtEncrypt);
		
		JLabel label = new JLabel("Perdoruesi: ");
		label.setFont(new Font("Tahoma", Font.BOLD, 10));
		label.setBounds(10, 277, 90, 13);
		contentPane.add(label);
		
		JLabel label_1 = new JLabel("Fjalekalimi: ");
		label_1.setFont(new Font("Tahoma", Font.BOLD, 10));
		label_1.setBounds(10, 375, 90, 13);
		contentPane.add(label_1);
		
		txtDecrypt = new JTextField();
		txtDecrypt.setColumns(10);
		txtDecrypt.setBounds(81, 372, 221, 19);
		contentPane.add(txtDecrypt);

		updateTable();
	}
}
