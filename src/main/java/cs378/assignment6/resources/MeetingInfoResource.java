package cs378.assignment6.resources;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import cs378.assignment6.controller.ETLController;
import cs378.assignment6.domain.Meeting;
import cs378.assignment6.domain.MeetingList;
import cs378.assignment6.domain.Project;
import cs378.assignment6.etl.Reader;
import cs378.assignment6.service.MeetingDataMgrService;

@Path("/projects")
public class MeetingInfoResource {
	protected static Log logger = LogFactory.getLog(MeetingInfoResource.class
			.getName());

	
	private ETLController etlController;
	private Reader meetingDataReader;
	
	public MeetingInfoResource() {
		etlController = new ETLController();
		meetingDataReader = new MeetingDataMgrService();
		
		// Start data load
		(new Thread(etlController)).start();;
	}
	
	// Test method
	@GET
	@Path("/solum/getAll")
	public String getAll() {
		return "Hello, world";
	}
	
	@GET
	@Path("/{project}/meetings")
	@Produces("application/xml")
	public Response getMeetingList(@PathParam("project") String projectName) throws Exception {
			MeetingList meetingList = null;
			try {
				meetingList = (MeetingList) meetingDataReader.read(projectName);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Project project = getProject(projectName);			
			if (project == null) {
				logger.error("404: invalid project entered");
				return Response.status(404).build();
			}
			project.setMeetings(meetingList.getMeetingList());
			logger.info("count in meetingList is: " + meetingList.getMeetingList().size());
			logger.info("retrieved project: " + project.getName() + ", with description: " + project.getDescription());
			
			JAXBContext context = JAXBContext.newInstance(Project.class);
			Marshaller m = createMarshaller(context);
			new StreamingOutput() {
				public void write(OutputStream outputStream) {
					try {
						m.marshal(project, outputStream);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			
			Response rs = Response.ok(project).build();
			return rs;
	}
	
	private Project getProject(String projectName) throws Exception {
		List<Project> projects = (List<Project>) ((MeetingDataMgrService) meetingDataReader).readProject(projectName);
		if (projects.size() < 1) {
			logger.warn("no project named: " + projectName);
			return null;
		}
		else {
			return projects.get(0);
		}
	}

	protected Marshaller createMarshaller(JAXBContext context)
			throws JAXBException {
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
		return marshaller;
	}
}