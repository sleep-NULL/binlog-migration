package com.sleep.binlog.net;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connector {

	private static final Logger logger = LoggerFactory.getLogger(Connector.class);

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
		conn = createConn(username, password, hostname, port);
	}

	private Connection createConn(String username, String password, String hostname, int port) throws SQLException {
		return DriverManager.getConnection("jdbc:mysql://" + hostname + ":" + port + "/information_schema?user="
				+ username + "&password=" + password + "&useUnicode=true&characterEncoding=UTF8");
	}

	public List<String> getTableColumns(String schema, String table) {
		for (int i = 0; i < 3; i++) {
			Statement stmt = null;
			ResultSet rs = null;
			try {
				if (i > 0) {
					conn = createConn(username, password, hostname, port);
				}
				stmt = conn.createStatement();
				rs = stmt.executeQuery("select column_name from columns where table_schema = \"" + schema
						+ "\" and table_name = \"" + table + "\"");
				List<String> result = new ArrayList<String>();
				while (rs.next()) {
					result.add(rs.getString(1));
				}
				return result;
			} catch (Exception e) {
				if (conn != null) {
					try {
						conn.close();
					} catch (SQLException e1) {
						// ignore
					}
				}
				logger.error("Get table columns failed.", e);
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
		}
		return null;
	}

}
