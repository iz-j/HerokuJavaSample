package iz.footprint.servlet;

import iz.footprint.HerokuEnvs;
import iz.footprint.base.ConnectionManager;
import iz.footprint.content.Footprint;
import iz.footprint.content.FootprintDao;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.StandardTemplateModeHandlers;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@SuppressWarnings("serial")
public final class FootprintServlet extends HttpServlet {
	private static final Logger logger = LoggerFactory.getLogger(FootprintServlet.class);

	private TemplateEngine templateEngine;

	@Override
	public void init(ServletConfig config) throws ServletException {
		final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
		templateResolver.setTemplateMode(StandardTemplateModeHandlers.HTML5.getTemplateModeName());
		templateResolver.setPrefix("webapp/template/");
		templateResolver.setSuffix(".html");
		templateResolver.setCharacterEncoding("utf-8");
		templateResolver.setCacheTTLMs(3600000L);
		templateResolver.setCacheable(!HerokuEnvs.isDev());

		templateEngine = new TemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		final FootprintDao dao = new FootprintDao();

		final Context ctx = new Context();
		ctx.setVariable("dtos", dao.selectAll());
		resp.setCharacterEncoding("utf-8");
		templateEngine.process("page", ctx, resp.getWriter());

		ConnectionManager.close();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		final FootprintDao dao = new FootprintDao();

		final Footprint dto = new Footprint();
		dto.comment = req.getParameter("comment");
		dto.datetime = DateTime.now();
		final long id = dao.insert(dto);
		logger.debug("New Footprint was registered. id = {}", id);

		ConnectionManager.close();
	}
}
