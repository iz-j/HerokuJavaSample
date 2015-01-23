package iz.footprint.content;

import iz.footprint.base.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FootprintDao {
	private static final Logger logger = LoggerFactory.getLogger(FootprintDao.class);

	static {
		final String create = "CREATE TABLE IF NOT EXISTS footprint ("
				+ " id BIGSERIAL NOT NULL PRIMARY KEY,"
				+ " comment TEXT,"
				+ " datetime BIGINT"
				+ ")";

		Connection conn = null;
		Statement stmt = null;
		try {
			conn = ConnectionManager.getConnection();
			stmt = conn.createStatement();
			stmt.execute(create);
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		} finally {
			DbUtils.closeQuietly(stmt);
			ConnectionManager.commitAndClose();
			logger.debug("Table : footprint initialized.");
		}
	}

	public long insert(Footprint dto) {
		final String sql = "INSERT INTO footprint (comment, datetime) VALUES (?, ?)";

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = ConnectionManager.getConnection();

			stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
			stmt.setString(1, dto.comment);
			stmt.setLong(2, dto.datetime.getMillis());
			stmt.executeUpdate();

			rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				return rs.getLong(1);
			} else {
				throw new IllegalStateException("Why could not get generated key!");
			}

		} catch (SQLException e) {
			throw new IllegalStateException(e);
		} finally {
			DbUtils.closeQuietly(stmt);
		}
	}

	public List<Footprint> selectAll() {
		final String sql = "SELECT * FROM footprint ORDER BY datetime DESC";

		final List<Footprint> dtos = new ArrayList<>();

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {

			conn = ConnectionManager.getConnection();
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();

			while (rs.next()) {
				dtos.add(mapRow(rs));
			}

			return dtos;

		} catch (SQLException e) {
			throw new IllegalStateException(e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(stmt);
		}
	}

	public List<Footprint> selectOlds(int limit) {
		final String sql = "SELECT * FROM footprint ORDER BY datetime ASC LIMIT ?";

		final List<Footprint> dtos = new ArrayList<>();

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {

			conn = ConnectionManager.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, limit);
			rs = stmt.executeQuery();

			while (rs.next()) {
				dtos.add(mapRow(rs));
			}

			return dtos;

		} catch (SQLException e) {
			throw new IllegalStateException(e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(stmt);
		}
	}

	public int selectCount() {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = ConnectionManager.getConnection();
			stmt = conn.prepareStatement("SELECT COUNT(*) FROM footprint");
			rs = stmt.executeQuery();

			if (rs.next()) {
				return rs.getInt(1);
			} else {
				return 0;
			}

		} catch (SQLException e) {
			throw new IllegalStateException(e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(stmt);
		}
	}

	public void delectBy(Collection<Long> ids) {
		final StringBuilder sql = new StringBuilder("DELETE FROM footprint WHERE id = ?");

		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = ConnectionManager.getConnection();
			stmt = conn.prepareStatement(sql.toString());

			for (long id : ids) {
				stmt.setLong(1, id);
				stmt.addBatch();
			}

			stmt.executeBatch();

		} catch (SQLException e) {
			throw new IllegalStateException(e);
		} finally {
			DbUtils.closeQuietly(stmt);
		}
	}

	private Footprint mapRow(ResultSet rs) throws SQLException {
		Footprint dto = new Footprint();
		dto.id = rs.getLong("id");
		dto.comment = rs.getString("comment");
		dto.datetime = new DateTime(rs.getLong("datetime"));
		return dto;
	}
}
