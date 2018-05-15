package project_Package;


import java.sql.Connection;
import java.sql.DriverManager;

import javax.swing.JOptionPane;

public class MysqlCon {

	Connection conn = null;

	public static Connection connectionFiekDb() {

		try {

			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/dbfiek2018", "root", "root");
			return conn;

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Gabim gjate lidhjes me DB");
			return null;
		}

	}

}
