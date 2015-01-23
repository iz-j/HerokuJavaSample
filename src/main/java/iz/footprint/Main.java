package iz.footprint;

import iz.footprint.base.ConnectionManager;
import iz.footprint.servlet.FootprintServlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Main {
	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) throws Exception {
		ConnectionManager.prepareDataSource();

		final int port = HerokuEnvs.getPort();
		logger.info("PORT from env = {}", port);

		final Server server = new Server(port);

		final ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setResourceBase(Main.class.getClassLoader().getResource("webapp").toExternalForm());
		resourceHandler.setDirectoriesListed(false);
		resourceHandler.setCacheControl(HerokuEnvs.isDev() ? "no-cache" : "private,max-age=86400");

		final ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		servletContextHandler.setHandler(resourceHandler);
		servletContextHandler.addServlet(new ServletHolder(new FootprintServlet()), "/servlet/*");

		server.setHandler(servletContextHandler);
		server.start();
		logger.info("Server started.");
		server.join();
	}

}
