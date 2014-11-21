package cs378.assignment6.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name="meeting")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table( name = "MEETINGS" )
public class Meeting {

	@XmlElement(name="name")
	private String name;
	
	@XmlElement(name="link")
	private String link;
	
	@XmlElement(name="year")
	private int year;
	
	@XmlTransient
	private String description;
	
	@XmlTransient
	private Project project;
	
	public Meeting() {
	}
	
	public Meeting(String name) {
		this.name = name;
	}
	
	public Meeting(String name, int year, String link, String description) {
		this.name = name;
		this.year = year;
		this.link = link;
		this.description = description;
	}
	public Meeting(String name, int year, String link, String description, Project project) {
		this.name = name;
		this.year = year;
		this.link = link;
		this.description = description;
		this.project = project;
	}
	
	@Id	
	public String getName() {
		return this.name;
	}
	
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@ManyToOne
	@JoinColumn(name="project_name")
	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}
	public void setName(String name) {
		this.name = name;
	}

	public void afterUnmarshal(Unmarshaller u, Object parent) {
		this.project = (Project) parent;
	}
	
//	public void addEvent(Event event) {
//		this.events.add(event);
//		if (!event.getMeeting().equals(this)) {
//			event.setMeeting(this);
//		}
//	}
}