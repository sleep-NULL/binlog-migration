package com.sleep.binlog.net;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Connector {

	private Connection conn;

	private String username;

	private String password;

	private String hostname;

	private int port;

	public Connector(String username, String password, String hostname, int port) throws SQLException {
		this.username = username;
		this.password = password;
		this.hostname = hostname;
		this.port = port;
		conn = DriverManager.getConnection("jdbc:mysql://" + hostname + ":" + port + "/information_schema?user="
				+ username + "&password=" + password + "&useUnicode=true&characterEncoding=UTF8");
	}

	public static void main(String[] args) throws SQLException, ClassNotFoundException, InterruptedException {
		new Connector("canal", "123456", "10.0.30.152", 3306).getTableColumns("dump_test", "user");
	}

	public List<String> getTableColumns(String schema, String table) {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select column_name from columns where table_schema = \"" + schema
					+ "\" and table_name = \"" + table + "\"");
			List<String> result = new ArrayList<String>();
			while (rs.next()) {
				result.add(rs.getString(1));
			}
			return result;
		} catch (Exception e) {
			// TODO
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					// ignore
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e1) {
					// ignore
				}
			}
		}
		return null;
	}

}
