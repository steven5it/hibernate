package cs378.assignment6.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@XmlRootElement(name = "project")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "PROJECTS")
public class Project{
	protected static Log logger = LogFactory.getLog(Project.class
			.getName());

	@XmlElement(name = "name")
	private String name;

	@XmlElement(name = "description")
	private String description;

	@XmlElement(name = "meetings")
	private List<Meeting> meetings;

	public Project() {
		this.meetings = new ArrayList<Meeting>();
	}

	public Project(String name) {
		this.name = name;
		this.meetings = new ArrayList<Meeting>();
	}

	public Project(String name, String description) {
		this.name = name;
		this.description = description;
		this.meetings = new ArrayList<Meeting>();
	}

	@Id
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void addMeeting(Meeting meeting) {
		this.meetings.add(meeting);
		if (!meeting.getProject().equals(this)) {
			meeting.setProject(this);
		}
	}

	@OneToMany(mappedBy = "project", fetch = FetchType.EAGER)
	public List<Meeting> getMeetings() {
		return meetings;
	}

	public void setMeetings(List<Meeting> meetings) {
		this.meetings = meetings;
	}
}
