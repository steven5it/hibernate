package cs378.assignment6.etl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Loader {
	static final Logger logger = LoggerFactory.getLogger(Loader.class);

	public void load(Object source) throws Exception;
}
