package iz.footprint.content;

import iz.footprint.base.ConnectionManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FootprintBatch {
	private static final Logger logger = LoggerFactory.getLogger(FootprintBatch.class);

	private FootprintBatch() {
	}

	public static void main(String[] args) {
		final StopWatch sw = new StopWatch();
		sw.start();
		try {
			new FootprintBatch().execute();
		} catch (Throwable e) {
			logger.error("Failed!", e);
		} finally {
			ConnectionManager.commitAndClose();
			sw.stop();
			logger.info("Batch finished. time = {}s", Math.floor(sw.getTime() / 1000));
		}
	}

	private void execute() {
		final FootprintDao dao = new FootprintDao();

		final int total = dao.selectCount();

		if (total <= 1000) {
			logger.info("Nothing to remove.");
			return;
		}

		final List<Footprint> beRemoved = dao.selectOlds(total - 1000);

		final Set<Long> ids = new HashSet<>();
		for (Footprint f : beRemoved) {
			ids.add(f.id);
		}

		dao.delectBy(ids);
		logger.info("{} footprints were removed.", ids.size());
	}

}
