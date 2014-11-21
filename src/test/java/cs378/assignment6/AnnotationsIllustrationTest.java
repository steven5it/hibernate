/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package cs378.assignment6;

import java.util.Date;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import junit.framework.TestCase;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import cs378.assignment6.domain.Meeting;
import cs378.assignment6.domain.MeetingList;
import cs378.assignment6.domain.Project;

/**
 * Illustrates the use of Hibernate native APIs.  The code here is unchanged from the {@code basic} example, the
 * only difference being the use of annotations to supply the metadata instead of Hibernate mapping files.
 *
 * @author Steve Ebersole
 */
public class AnnotationsIllustrationTest extends TestCase {
	private SessionFactory sessionFactory;

	@Override
	protected void setUp() throws Exception {
		// A SessionFactory is set up once for an application
        sessionFactory = new Configuration()
                .configure() // configures settings from hibernate.cfg.xml
                .buildSessionFactory();
	}

	@Override
	protected void tearDown() throws Exception {
		if ( sessionFactory != null ) {
			sessionFactory.close();
		}
	}

	@SuppressWarnings({ "unchecked" })
	public void testBasicUsage() {
		// create a couple of events...
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save( new Meeting( "name", 2000, "http://localhost:8080/test", "test meeting1" ) );
		session.save( new Meeting( "another name", 2000, "http://localhost:8080/test", "test meeting2" ) );
		session.getTransaction().commit();
		session.close();

		// now lets pull events from the database and list them
		session = sessionFactory.openSession();
        session.beginTransaction();
        List result = session.createQuery( "from Meeting" ).list();
		for ( Meeting meeting : (List<Meeting>) result ) {
			System.out.println( "Meeting (" + meeting.getName() + ") : " + meeting.getDescription() );
		}
        session.getTransaction().commit();
        session.close();
	}

	@SuppressWarnings({ "unchecked" })
	public void testMeetingInsert() {
		// create a couple of events...
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save( new Meeting( "Meeting 1") );
		session.save( new Meeting( "Meeting 2") );
		session.getTransaction().commit();
		session.close();

		// now lets pull events from the database and list them
		session = sessionFactory.openSession();
        session.beginTransaction();
        List result = session.createQuery( "from Meeting" ).list();
		for ( Meeting meeting : (List<Meeting>) result ) {
			System.out.println( "Meeting (" + meeting.getName() + ") " );
		}
        session.getTransaction().commit();
        session.close();
	}
	
	@SuppressWarnings({ "unchecked" })
	public void testMeetingEventsInsert() {
		// create a couple of events...
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		
		Project p1 = new Project("Project_1");
		session.save(p1);
		
		Meeting m1 = new Meeting( "Project_1 meeting 1");
		Meeting m2 = new Meeting( "Project_1 meeting 2");
		
		m1.setProject(p1);
		m2.setProject(p1);
		
		session.save(m1);
		session.save(m2);

		Project p2 = new Project("Project_2");
		session.save(p2);
		
		Project p3 = new Project("Project_3");
		session.save(p3);
		
		Project p4 = new Project("Project_4");
		session.save(p4);

		Meeting m12 = new Meeting( "Project_2 meeting 1");
		m12.setProject(p2);
		//m2.addEvent(e12);
		
		session.save(m12);		

		session.getTransaction().commit();
		session.close();

		// now lets pull events from the database and list them
		session = sessionFactory.openSession();
        session.beginTransaction();

        // Select criteria
        Criteria criteria = session.createCriteria(Meeting.class).
        		add(Restrictions.eq("name", "Project_1 meeting 1"));  
        List result = criteria.list();
        
		for ( Meeting meeting : (List<Meeting>) result ) {
			System.out.println( "SelectCriteria: Meeting (" + meeting.getName() + ") " );
		}
		
		// Selection criteria
		List<Meeting> meetings = session.createQuery("from Meeting where project_name='Project_1'").list();
		for ( Meeting meeting : meetings ) {
			System.out.println( "SelectionCriteria: Meeting (" + meeting.getName() + ") " );
		}
		
		// Join criteria, querying from owning side
		System.out.println("Join Criteria from owning side:");
		List<Object[]> meetings1 = session.createQuery(
				"from Meeting m join m.project p where p.name ='Project_1'").list();
		MeetingList mList = new MeetingList();
		for ( int i = 0; i < meetings1.size(); i++ ) {
			Object [] arr = meetings1.get(i);
			Meeting m = (Meeting) arr[0];
			Project p = (Project) arr[1];
			
			mList.addMeeting(m);
			
			System.out.println( "Meeting (" + m.getName() + ") " );
			System.out.println( "Project (" + p.getName() + ") " );
		}
		
        session.getTransaction().commit();
        session.close();
        
        // Delete -- this will throw ConstraintViolationException
        try { 
        session = sessionFactory.openSession();
        session.beginTransaction();
        
        Project projectToDelete = (Project)session.get(Project.class, "Project_2");
        session.delete(projectToDelete);
        session.getTransaction().commit();
        } catch (ConstraintViolationException e) {        	
        	session.getTransaction().rollback();
        	session.close();
        	
        	session = sessionFactory.openSession();
            session.beginTransaction();
            
        	Project projectToDelete = (Project)session.get(Project.class, "Project_3");
            session.delete(projectToDelete);
            session.getTransaction().commit();
            session.close();
        }
        
        System.out.println("Parameterized delete of Project_4");
        // Parameterized delete --
        session = sessionFactory.openSession();
        session.beginTransaction();
        
        Query q = session.createQuery("from Project where name = :name ");
        q.setParameter("name", "Project_4");
        Project m = (Project)q.list().get(0);
        session.delete(m);

        session.getTransaction().commit();
        session.close();
	}
	
	public void testGetValidProject() {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		Project p1 = new Project("solum", "description");
		session.save(p1);
		Meeting m1 = new Meeting("solum meeting 1");
		Meeting m2 = new Meeting("solum meeting 2");
		m1.setProject(p1);
		m2.setProject(p1);
		session.save(m1);
		session.save(m2);
        session.getTransaction().commit();
        session.close();
		
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target("http://localhost:8080/myeavesdrop/projects/solum/meetings");
		Invocation.Builder invocationBuilder =
		        webTarget.request(MediaType.APPLICATION_XML);
		try {
			System.out.println("*** GET Valid Created Project **");
			Response response = invocationBuilder.get();
			System.out.println("Response: " + response.getStatus());
			assertEquals(200, response.getStatus());
		} finally {
			client.close();
		}
	}
	
	public void testGetInvalidProject() {		
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target("http://localhost:8080/myeavesdrop/projects/Random/meetings");
		Invocation.Builder invocationBuilder =
		        webTarget.request(MediaType.APPLICATION_XML);
		try {
			System.out.println("*** GET Invalid Created Project **");
			Response response = invocationBuilder.get();
			System.out.println("Response: " + response.getStatus());
			assertEquals(404, response.getStatus());
		} finally {
			client.close();
		}
	}
}