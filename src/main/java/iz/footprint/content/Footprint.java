package iz.footprint.content;

import org.joda.time.DateTime;

public final class Footprint {
	public long id;
	public String comment;
	public DateTime datetime;

	@Override
	public String toString() {
		return "Footprint [id=" + id + ", comment=" + comment + ", datetime=" + datetime + "]";
	}
}
