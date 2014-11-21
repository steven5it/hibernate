package cs378.assignment6.domain;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="meetings")
@XmlAccessorType(XmlAccessType.FIELD)
public class MeetingList {
	
	@XmlElement(name="meeting")
	List<Meeting> meetings;
	
	public MeetingList() {
		meetings = new ArrayList<Meeting>();
	}
	
	public void addMeeting(Meeting m) {
		meetings.add(m);
	}
	
	public List<Meeting> getMeetingList() {
		return meetings;
	}

}
