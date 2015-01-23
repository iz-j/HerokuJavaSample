package iz.footprint;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang3.StringUtils;

public final class HerokuEnvs {
	private HerokuEnvs() {
	}

	public static boolean isDev() {
		return (StringUtils.isEmpty(System.getenv("DATABASE_URL")));
	}

	public static int getPort() {
		final String port = System.getenv("PORT");
		if (StringUtils.isEmpty(port)) {
			return 8080;
		} else {
			return Integer.valueOf(port);
		}
	}

	public static class DatabaseInfo {
		public final String url;
		public final String username;
		public final String password;

		private DatabaseInfo(String url, String username, String password) {
			this.url = url;
			this.username = username;
			this.password = password;
		}
	}

	public static DatabaseInfo getDatabaseInfo() {
		String url = System.getenv("DATABASE_URL");
		boolean isDev = StringUtils.isEmpty(url);

		if (isDev) {
			url = "postgres://<username>:<password>@<host>:<port>/<database>";
		}

		try {
			final URI dbUri = new URI(url);
			final String username = dbUri.getUserInfo().split(":")[0];
			final String password = dbUri.getUserInfo().split(":")[1];

			String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + dbUri.getPath();

			if (isDev) {
				dbUrl += "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
			}

			return new DatabaseInfo(dbUrl, username, password);

		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		}
	}
}
