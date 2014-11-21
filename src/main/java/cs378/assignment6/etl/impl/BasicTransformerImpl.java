package cs378.assignment6.etl.impl;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cs378.assignment6.domain.Meeting;
import cs378.assignment6.domain.Project;
import cs378.assignment6.etl.Transformer;
import cs378.assignment6.service.EavesdropReaderService;

public class BasicTransformerImpl implements Transformer {
	protected static Log logger = LogFactory.getLog(BasicTransformerImpl.class
			.getName());
	
	public Object transform(Object source) throws Exception {
		//transform an array of String links (description) into row in meetings table
		ArrayList<Meeting> meetings = descriptionToMeeting((ArrayList)source);
		
		return meetings;
	}

	private ArrayList<Meeting> descriptionToMeeting(ArrayList<String> links) {
		logger.info("in transformer, link count is: " + links.size());
		ArrayList<Meeting> meetings = new ArrayList<Meeting>();
		for (String link: links) {
			String [] fields = link.split("/");
			int fieldLength = fields.length - 1; //last field is name, next is year
			
			String project_name = "Solum";
			String description = link;
			String name = fields[fieldLength];
			String localLink = "http://localhost:8080/myeavesdrop/projects/Solum/meetings/" + name;
			int year = Integer.parseInt(fields[fieldLength-1]);
//			logger.info("pname = " + project_name + ", description = " + description + ", name = " 
//					+ name + ", locallink = " + localLink + ", year = " + year);
			
			Meeting meeting = new Meeting();
			meeting.setProject(new Project(project_name));
			meeting.setDescription(description);
			meeting.setName(name);
			meeting.setYear(year);
			meeting.setLink(localLink);
			
			meetings.add(meeting);
		}
		return meetings;
	}
}
