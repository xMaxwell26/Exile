package com.koios.network.validation;

import java.util.List;

import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.data.Row;

import com.koios.GameService;
import com.koios.network.Session;

public class MySQLValidationResponseSupplier implements ValidationResponseSupplier {

	private static final String SQL_INSERT_LOGS = "INSERT INTO connection_logs " + "(username, host_address, host_name, client_hash) "
			+ "VALUES (:username, :host_address, :host_name, :client_hash)";

	private static final String SQL_SELECT_SANCTIONS = "SELECT EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::bigint as now, id, type, EXTRACT(EPOCH FROM expire)::bigint as expire " + "FROM sanctions "
			+ "WHERE ((name = :name) OR (ipaddress IS NOT NULL AND ipaddress = :ipaddress)) " + "AND closed = 0 " + "ORDER BY id DESC";

	@Override
	public ValidationResponse fetch(Session client) {
		String username = client.getPlayer().getName();
		String hostAddress = client.getHostAddress();
		String hostName = client.getHostName();
		long clientHash = client.getClientHash();
		boolean success = true;
		boolean banned = false;
		boolean muted = false;

		try (Connection connection = GameService.getConnection(); Query insert = connection.createQuery(SQL_INSERT_LOGS)) {
			insert.addParameter("username", username);
			insert.addParameter("host_address", hostAddress);
			insert.addParameter("host_name", hostName);
			insert.addParameter("client_hash", clientHash);

			insert.executeUpdate();

			try (Query select = connection.createQuery(SQL_SELECT_SANCTIONS)) {
				select.addParameter("name", username);
				select.addParameter("ipaddress", hostAddress);

				List<Row> results = select.executeAndFetchTable().rows();

				for (Row result : results) {
					int id = result.getInteger("id");
					long now = result.getLong("now");
					long expire = result.getLong("expire");
					String type = result.getString("type");

					if (expire != 0 && now >= expire) {
						try (Query delete = connection.createQuery("UPDATE sanctions SET closed = 1 WHERE id = ?")) {
							delete.addParameter("id", id);
							delete.executeUpdate();
						}
					} else if ("ban".equals(type)) {
						banned = true;
					} else if ("mute".equals(type)) {
						muted = true;
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			success = false;
		}

		return new ValidationResponse(success, banned, muted);
	}

}
