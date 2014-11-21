package cs378.assignment6.etl;

import java.util.List;

import cs378.assignment6.domain.Project;

public interface Reader {	
	public Object read(Object source) throws Exception;
}
