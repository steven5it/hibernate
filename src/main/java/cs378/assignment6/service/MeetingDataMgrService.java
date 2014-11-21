package cs378.assignment6.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import cs378.assignment6.domain.Meeting;
import cs378.assignment6.domain.MeetingList;
import cs378.assignment6.domain.Project;
import cs378.assignment6.etl.Loader;
import cs378.assignment6.etl.Reader;

public class MeetingDataMgrService implements Loader, Reader {
	protected static Log logger = LogFactory.getLog(MeetingDataMgrService.class
			.getName());
	
	private SessionFactory sessionFactory;
	
	public MeetingDataMgrService() {
		logger.info("in MeetingDataMgrService constructor" );
		// A SessionFactory is set up once for an application
        sessionFactory = new Configuration()
                .configure() // configures settings from hibernate.cfg.xml
                .buildSessionFactory();
        
        // add default solum project
        Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save( new Project( "solum", "solum project" ) );
		session.getTransaction().commit();
		session.close();
        
	}

	public void load(Object objectToLoad) throws Exception {
		insertMeetingsRecord(objectToLoad);
	}
	
	private void insertMeetingsRecord(Object obj) {
		logger.info("inserting meetings records");
		ArrayList<Meeting> meetings = (ArrayList) obj;
		logger.info("meetings count in loader is: " + meetings.size());
		for (Meeting meeting: meetings) {
			insertMeetingRecord(meeting);
		}

	}

	private void insertMeetingRecord(Meeting meeting) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save(meeting);
		session.getTransaction().commit();
		session.close();
	}

	public Object read(Object source) throws Exception {
		String projectName = (String) source;
		logger.info("reading meetingslist for project: " + projectName);
		Session session = sessionFactory.openSession();
		session.beginTransaction();

		// Build the list of meetings - ONLY meetings
		String hql = "from Meeting where project_name=:project_name";
		List<Meeting> result = session.createQuery(hql).setParameter("project_name", projectName).list();
		
		Project project = null;
		hql = "from Project where name=:project_name";
		List<Project> projectResult = session.createQuery(hql).setParameter("project_name", projectName).list();
		if (projectResult.size() > 0) {
			project = projectResult.get(0);
		}
		logger.info("number of projects: " + projectResult.size());
		project.setMeetings(result);
		
        logger.info("number of solum project meetings: " + result.size());
        session.getTransaction().commit();
        session.close();
        
		MeetingList meetingList = new MeetingList();
		for ( Meeting meeting : (List<Meeting>) result ) {
			meetingList.addMeeting(meeting);
		}
		
//		//Build with join of project and meetings
//		String hql = "from Meeting m join m.project p where p.name =:project_name";
//		List<Object[]> meetings = session.createQuery(hql).setParameter("project_name", projectName).list();
//		MeetingList mList = new MeetingList();
//		for ( int i = 0; i < meetings.size(); i++ ) {
//			Object [] arr = meetings1.get(i);
//			Meeting m = (Meeting) arr[0];
//			Project p = (Project) arr[1];
//			
//			mList.addMeeting(m);
//			
//			System.out.println( "Meeting (" + m.getName() + ") " );
//			System.out.println( "Project (" + p.getName() + ") " );
//		}
//		
//        session.getTransaction().commit();
//        session.close();
		
		return meetingList;
	}
	
	public Object readProject(Object source) throws Exception {
		String projectName = (String) source;
		logger.info("reading project: " + projectName);
		Session session = sessionFactory.openSession();
		session.beginTransaction();

		// Build the list of meetings - ONLY meetings
		String hql = "from Project where name=:project_name";
		List<Project> result = session.createQuery(hql).setParameter("project_name", projectName).list();
		
        logger.info("number of solum projects: " + result.size());
        session.getTransaction().commit();
        session.close();
        return result;
	}
	
	
}