package com.koios.network.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.concurrent.Future;

import com.koios.Constants;
import com.koios.GameService;

public class SanctionDatabaseConnector {

	/*
	 * Production credentials must be supplied by the deployment environment.
	 * Offline development never constructs this connector. Keeping these values
	 * outside source control also lets operators rotate them without rebuilding.
	 */
	private static final String DATABASE_NAME = "EXILE_SANCTIONS_DB_NAME";
	private static final String DATABASE_PASSWORD = "EXILE_SANCTIONS_DB_PASSWORD";
	private static final String DATABASE_USERNAME = "EXILE_SANCTIONS_DB_USERNAME";

	/* Database Credentials */

	/**
	 * A singleton reference to <i>the</i> database connector.
	 */
	private static final SanctionDatabaseConnector instance;
	private static final String SQL_FORCE_CLOSE = "UPDATE sanctions SET closed = 1 WHERE id = ?";
	private static final String SQL_UPDATE_REASON = "UPDATE sanctions SET reason = ? WHERE id = ?";

	/* SQL Statement Constants */

	static {
		if (GameService.isOnlineMode()) {
			instance = new SanctionDatabaseConnector();
		} else {
			instance = null;
		}
	}

	/**
	 * Gets <i>the</i> singleton instance of this class.
	 * <p>
	 * <p>
	 * In a debug environment this will return <code>null</code>.
	 *
	 * @return The singleton instance; or <code>null</code> in debug environments.
	 */
	public static SanctionDatabaseConnector instance() {
		return instance;
	}

	private static String environment(String name) {
		String value = System.getenv(name);
		if (value == null || value.trim().isEmpty()) {
			throw new IllegalStateException("Missing required production environment variable: " + name);
		}
		return value;
	}

	public Future<String> editReason(int sanctionId, String reason) {
		return GameService.submit(() -> {
			try (Connection connection = connection()) {
				try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_REASON)) {
					statement.setString(1, reason);
					statement.setInt(2, sanctionId);

					if (statement.executeUpdate() == 1) {
						return "Successfully changed reason for sanction <col=255>#" + sanctionId + "</col> to: <col=800000>" + reason;
					} else {
						return "Unable to edit reason for sanction <col=255>#" + sanctionId;
					}
				}
			}
		});
	}

	public Future<String> forceClose(int sanctionId) {
		return GameService.submit(() -> {
			try (Connection connection = connection()) {
				try (PreparedStatement stmt = connection.prepareStatement(SQL_FORCE_CLOSE)) {
					stmt.setInt(1, sanctionId);

					if (stmt.executeUpdate() == 1) {
						return "Successfully closed sanction <col=255>#" + sanctionId + "</col>.";
					} else {
						return String.format("Unable to close sanction #<col=255>%d</col>. Perhaps it's already closed?", sanctionId);
					}
				}
			}
		});
	}

	private static Connection connection() throws java.sql.SQLException {
		return DriverManager.getConnection("jdbc:mysql://" + Constants.MYSQL_HOST_ADDRESS + '/' + environment(DATABASE_NAME),
				environment(DATABASE_USERNAME), environment(DATABASE_PASSWORD));
	}

}
