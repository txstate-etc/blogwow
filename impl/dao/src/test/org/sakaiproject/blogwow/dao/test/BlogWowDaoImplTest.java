/******************************************************************************
 * BlogWowDaoImplTest.java - created by Sakai App Builder -AZ
 * 
 * Copyright (c) 2006 Sakai Project/Sakai Foundation
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 *****************************************************************************/

package org.sakaiproject.blogwow.dao.test;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.blogwow.dao.BlogWowDao;
import org.sakaiproject.blogwow.logic.test.stubs.ExternalLogicStub;
import org.sakaiproject.blogwow.model.BlogWowBlog;
import org.sakaiproject.blogwow.model.BlogWowComment;
import org.sakaiproject.blogwow.model.BlogWowEntry;
import org.sakaiproject.blogwow.model.constants.BlogConstants;
import org.springframework.test.AbstractTransactionalSpringContextTests;

/**
 * Testing for the specialized DAO methods (do not test the Generic Dao methods)
 * @author Sakai App Builder -AZ
 */
public class BlogWowDaoImplTest extends AbstractTransactionalSpringContextTests {

	private static Log log = LogFactory.getLog(BlogWowDaoImplTest.class);

	protected BlogWowDao dao;

	protected BlogWowBlog blog1;
	protected BlogWowBlog blog2;
	protected BlogWowBlog blog3;

	protected BlogWowEntry entry1_b1;
	protected BlogWowEntry entry2_b1;
	protected BlogWowEntry entry3_b1;
	protected BlogWowEntry entry4_b1;
	protected BlogWowEntry entry5_b2;
	protected BlogWowEntry entry6_b2;
	protected BlogWowEntry entry7_b3;

	protected BlogWowComment comment1_e1_b1;
	protected BlogWowComment comment2_e1_b1;
	protected BlogWowComment comment3_e4_b2;

	
	protected String[] getConfigLocations() {
		// point to the needed spring config files, must be on the classpath
		// (add component/src/webapp/WEB-INF to the build path in Eclipse),
		// they also need to be referenced in the project.xml file
		return new String[] {"hibernate-test.xml", "spring-hibernate.xml"};
	}

	// run this before each test starts
	protected void onSetUpBeforeTransaction() throws Exception {
		// create test objects
		blog1 = new BlogWowBlog(ExternalLogicStub.USER_ID, ExternalLogicStub.LOCATION1_ID, "blog1 title", "blog1 profile", null, new Date());
		blog2 = new BlogWowBlog(ExternalLogicStub.MAINT_USER_ID, ExternalLogicStub.LOCATION1_ID, "blog2 title", "blog2 profile", null, new Date());
		blog3 = new BlogWowBlog(ExternalLogicStub.ADMIN_USER_ID, ExternalLogicStub.LOCATION2_ID, "blog3 title", "blog3 profile", null, new Date());

		entry1_b1 = new BlogWowEntry(blog1, ExternalLogicStub.USER_ID, "entry 1", "entry text", BlogConstants.PRIVACY_PUBLIC, new Date(), new Date());
		entry2_b1 = new BlogWowEntry(blog1, ExternalLogicStub.USER_ID, "entry 2", "entry text", BlogConstants.PRIVACY_GROUP, new Date(), new Date());
		entry3_b1 = new BlogWowEntry(blog1, ExternalLogicStub.USER_ID, "entry 3", "entry text", BlogConstants.PRIVACY_GROUP_LEADER, new Date(), new Date());
		entry4_b1 = new BlogWowEntry(blog1, ExternalLogicStub.USER_ID, "entry 4", "entry text", BlogConstants.PRIVACY_PRIVATE, new Date(), new Date());
		entry5_b2 = new BlogWowEntry(blog2, ExternalLogicStub.MAINT_USER_ID, "entry 5", "entry text", BlogConstants.PRIVACY_PUBLIC, new Date(), new Date());
		entry6_b2 = new BlogWowEntry(blog2, ExternalLogicStub.MAINT_USER_ID, "entry 6", "entry text", BlogConstants.PRIVACY_PRIVATE, new Date(), new Date());
		entry7_b3 = new BlogWowEntry(blog3, ExternalLogicStub.ADMIN_USER_ID, "entry 7", "entry text", BlogConstants.PRIVACY_PRIVATE, new Date(), new Date());

		comment1_e1_b1 = new BlogWowComment(entry1_b1, ExternalLogicStub.MAINT_USER_ID, "comment 1", new Date(), new Date());
		comment2_e1_b1 = new BlogWowComment(entry1_b1, ExternalLogicStub.MAINT_USER_ID, "comment 2", new Date(), new Date());
		comment3_e4_b2 = new BlogWowComment(entry5_b2, ExternalLogicStub.USER_ID, "comment 3", new Date(), new Date());		
	}

	// run this before each test starts and as part of the transaction
	protected void onSetUpInTransaction() {
		// load the spring created dao class bean from the Spring Application Context
		dao = (BlogWowDao) applicationContext.
			getBean("org.sakaiproject.blogwow.dao.BlogWowDao");
		if (dao == null) {
			log.error("onSetUpInTransaction: DAO could not be retrieved from spring context");
		}

		// init the class if needed

		// check the preloaded data

		// preload data if desired
		dao.save(blog1);
		dao.save(blog2);
		dao.save(blog3);
		dao.save(entry1_b1);
		dao.save(entry2_b1);
		dao.save(entry3_b1);
		dao.save(entry4_b1);
		dao.save(entry5_b2);
		dao.save(entry6_b2);
		dao.save(entry7_b3);
		dao.save(comment1_e1_b1);
		dao.save(comment2_e1_b1);
		dao.save(comment3_e4_b2);
	}


	/**
	 * ADD unit tests below here, use testMethod as the name of the unit test,
	 * Note that if a method is overloaded you should include the arguments in the
	 * test name like so: testMethodClassInt (for method(Class, int);
	 */

	/**
	 * Test method for {@link org.sakaiproject.blogwow.dao.impl.BlogWowDaoImpl#getLocationsForBlogsIds(java.lang.Long[])}.
	 */
	public void testGetLocationsForBlogsIds() {
		List locs = null;

		locs = dao.getLocationsForBlogsIds(new Long[] {blog1.getId(), blog2.getId(), blog3.getId()});
		assertNotNull(locs);
		assertEquals(2, locs.size());
		assertTrue(locs.contains(ExternalLogicStub.LOCATION1_ID));
		assertTrue(locs.contains(ExternalLogicStub.LOCATION2_ID));

		locs = dao.getLocationsForBlogsIds(new Long[] {blog1.getId()});
		assertNotNull(locs);
		assertEquals(1, locs.size());
		assertTrue(locs.contains(ExternalLogicStub.LOCATION1_ID));

		locs = dao.getLocationsForBlogsIds(new Long[] {blog3.getId()});
		assertNotNull(locs);
		assertEquals(1, locs.size());
		assertTrue(locs.contains(ExternalLogicStub.LOCATION2_ID));

		locs = dao.getLocationsForBlogsIds(new Long[] {});
		assertNotNull(locs);
		assertEquals(0, locs.size());
	}

	/**
	 * Test method for {@link org.sakaiproject.blogwow.dao.impl.BlogWowDaoImpl#getBlogPermEntries(Long[], String, String[], String[], String, boolean, int, int)}.
	 */
	public void testGetBlogPermEntries() {
		List entries = null;

		// get all public entries
		entries = dao.getBlogPermEntries(new Long[] {blog1.getId(), blog2.getId(), blog3.getId()}, 
				null, null, null, null, false, 0, 0);
		assertNotNull(entries);
		assertEquals(2, entries.size());
		assertTrue(entries.contains(entry1_b1));
		assertTrue(entries.contains(entry5_b2));

		// get only blog 1 public entries
		entries = dao.getBlogPermEntries(new Long[] {blog1.getId()}, 
				null, null, null, null, false, 0, 0);
		assertNotNull(entries);
		assertEquals(1, entries.size());
		assertTrue(entries.contains(entry1_b1));

		// get all entries for user
		entries = dao.getBlogPermEntries(new Long[] {blog1.getId(), blog2.getId(), blog3.getId()}, 
				ExternalLogicStub.USER_ID, new String[] {ExternalLogicStub.LOCATION1_ID}, null, null, false, 0, 0);
		assertNotNull(entries);
		assertEquals(5, entries.size());
		assertTrue(entries.contains(entry1_b1));
		assertTrue(entries.contains(entry2_b1));
		assertTrue(entries.contains(entry3_b1));
		assertTrue(entries.contains(entry4_b1));
		assertTrue(entries.contains(entry5_b2));

		// get all entries for maint user
		entries = dao.getBlogPermEntries(new Long[] {blog1.getId(), blog2.getId(), blog3.getId()}, 
				ExternalLogicStub.MAINT_USER_ID, new String[] {ExternalLogicStub.LOCATION1_ID}, 
				new String[] {ExternalLogicStub.LOCATION1_ID}, null, false, 0, 0);
		assertNotNull(entries);
		assertEquals(5, entries.size());
		assertTrue(entries.contains(entry1_b1));
		assertTrue(entries.contains(entry2_b1));
		assertTrue(entries.contains(entry3_b1));
		assertTrue(entries.contains(entry5_b2));
		assertTrue(entries.contains(entry6_b2));

		// get all entries for user with limits
		entries = dao.getBlogPermEntries(new Long[] {blog1.getId(), blog2.getId(), blog3.getId()}, 
				ExternalLogicStub.USER_ID, new String[] {ExternalLogicStub.LOCATION1_ID}, null, null, false, 3, 0);
		assertNotNull(entries);
		assertEquals(2, entries.size());
		assertTrue(entries.contains(entry2_b1));
		assertTrue(entries.contains(entry1_b1));

		entries = dao.getBlogPermEntries(new Long[] {blog1.getId(), blog2.getId(), blog3.getId()}, 
				ExternalLogicStub.USER_ID, new String[] {ExternalLogicStub.LOCATION1_ID}, null, null, false, 2, 2);
		assertNotNull(entries);
		assertEquals(2, entries.size());
		assertTrue(entries.contains(entry3_b1));
		assertTrue(entries.contains(entry2_b1));

	}

	/**
	 * Add anything that supports the unit tests below here
	 */
}
