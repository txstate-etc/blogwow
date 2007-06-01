/******************************************************************************
 * BlogWowLogicImplTest.java - created by Sakai App Builder -AZ
 * 
 * Copyright (c) 2006 Sakai Project/Sakai Foundation
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 *****************************************************************************/

package org.sakaiproject.blogwow.logic.test;

import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.blogwow.dao.BlogWowDao;
import org.sakaiproject.blogwow.logic.impl.BlogLogicImpl;
import org.sakaiproject.blogwow.model.BlogWowBlog;
import org.springframework.test.AbstractTransactionalSpringContextTests;


/**
 * Testing the Logic implementation methods
 * @author Sakai App Builder -AZ
 */
public class BlogLogicImplTest extends AbstractTransactionalSpringContextTests {

	private static Log log = LogFactory.getLog(BlogLogicImplTest.class);

	protected BlogLogicImpl logicImpl;

	private BlogWowBlog blog1;
	private BlogWowBlog blog2;
	private BlogWowBlog blog3;

	private final String BLOG1_PROFILE = "My blog profile 1";
	private final String BLOG2_PROFILE = "My blog profile 2";
	private final String BLOG3_PROFILE = "My blog profile 3";


	protected String[] getConfigLocations() {
		// point to the needed spring config files, must be on the classpath
		// (add component/src/webapp/WEB-INF to the build path in Eclipse),
		// they also need to be referenced in the project.xml file
		return new String[] {"hibernate-test.xml", "spring-hibernate.xml"};
	}

	// run this before each test starts
	protected void onSetUpBeforeTransaction() throws Exception {
		// create test objects
		blog1 = new BlogWowBlog(ExternalLogicStub.USER_ID, ExternalLogicStub.LOCATION1_ID, BLOG1_PROFILE, "", new Date());
		blog2 = new BlogWowBlog(ExternalLogicStub.MAINT_USER_ID, ExternalLogicStub.LOCATION1_ID, BLOG2_PROFILE, "", new Date());
		blog3 = new BlogWowBlog(ExternalLogicStub.ADMIN_USER_ID, ExternalLogicStub.LOCATION2_ID, BLOG3_PROFILE, "", new Date());
	}

	// run this before each test starts and as part of the transaction
	protected void onSetUpInTransaction() {
		// load the spring created dao class bean from the Spring Application Context
		BlogWowDao dao = (BlogWowDao) applicationContext.
			getBean("org.sakaiproject.blogwow.dao.BlogWowDao");
		if (dao == null) {
			log.error("onSetUpInTransaction: DAO could not be retrieved from spring context");
		}

		// init the class if needed

		// create and setup the object to be tested
		logicImpl = new BlogLogicImpl();
		logicImpl.setDao(dao);
		logicImpl.setExternalLogic( new ExternalLogicStub() ); // use the stub for testing

		// preload the DB for testing
		dao.save(blog1);
		dao.save(blog2);
		dao.save(blog3);
	}

	/**
	 * add some tests
	 */

	/**
	 * Test method for {@link org.sakaiproject.blogwow.logic.impl.BlogLogicImpl#canWriteBlog(org.sakaiproject.blogwow.model.BlogWowBlog, java.lang.String, java.lang.String)}.
	 */
	public void testCanWriteBlog() {
		assertTrue( logicImpl.canWriteBlog(blog1, ExternalLogicStub.LOCATION1_ID, ExternalLogicStub.USER_ID) );
		assertTrue( logicImpl.canWriteBlog(blog2, ExternalLogicStub.LOCATION1_ID, ExternalLogicStub.MAINT_USER_ID) );
		assertTrue( logicImpl.canWriteBlog(blog3, ExternalLogicStub.LOCATION2_ID, ExternalLogicStub.ADMIN_USER_ID) );

		// make sure we cannot write in other sites
		assertFalse( logicImpl.canWriteBlog(blog1, ExternalLogicStub.LOCATION2_ID, ExternalLogicStub.USER_ID) );
		assertFalse( logicImpl.canWriteBlog(blog2, ExternalLogicStub.LOCATION2_ID, ExternalLogicStub.MAINT_USER_ID) );

		// make sure we cannot write other blogs
		assertFalse( logicImpl.canWriteBlog(blog2, ExternalLogicStub.LOCATION1_ID, ExternalLogicStub.USER_ID) );
		assertFalse( logicImpl.canWriteBlog(blog3, ExternalLogicStub.LOCATION2_ID, ExternalLogicStub.MAINT_USER_ID) );

		// make sure admin can write all of them
		assertTrue( logicImpl.canWriteBlog(blog1, ExternalLogicStub.LOCATION1_ID, ExternalLogicStub.ADMIN_USER_ID) );
		assertTrue( logicImpl.canWriteBlog(blog2, ExternalLogicStub.LOCATION1_ID, ExternalLogicStub.ADMIN_USER_ID) );
		assertTrue( logicImpl.canWriteBlog(blog3, ExternalLogicStub.LOCATION2_ID, ExternalLogicStub.ADMIN_USER_ID) );
	}

	/**
	 * Test method for {@link org.sakaiproject.blogwow.logic.impl.BlogLogicImpl#getAllVisibleBlogs(java.lang.String)}.
	 */
	public void testGetAllVisibleBlogs() {
		List l = null;

		l = logicImpl.getAllVisibleBlogs( ExternalLogicStub.LOCATION1_ID );
		assertNotNull(l);
		assertEquals(2, l.size());
		assertTrue( l.contains(blog1) );
		assertTrue( l.contains(blog2) );

		l = logicImpl.getAllVisibleBlogs( ExternalLogicStub.LOCATION2_ID );
		assertNotNull(l);
		assertEquals(1, l.size());
		assertTrue( l.contains(blog3) );

		l = logicImpl.getAllVisibleBlogs( ExternalLogicStub.INVALID_LOCATION_ID );
		assertNotNull(l);
		assertEquals(0, l.size());
	}

	/**
	 * Test method for {@link org.sakaiproject.blogwow.logic.impl.BlogLogicImpl#getBlogById(java.lang.Long)}.
	 */
	public void testGetBlogById() {
		BlogWowBlog blog = null;

		// test getting valid items by id
		blog = logicImpl.getBlogById(blog1.getId());
		Assert.assertNotNull(blog);
		Assert.assertEquals(blog1, blog);

		// test get eval by invalid id returns null
		blog = logicImpl.getBlogById( new Long(-1) );
		Assert.assertNull(blog);
	}

	/**
	 * Test method for {@link org.sakaiproject.blogwow.logic.impl.BlogLogicImpl#saveBlog(org.sakaiproject.blogwow.model.BlogWowBlog)}.
	 */
	public void testSaveBlog() {
		BlogWowBlog blog = new BlogWowBlog(ExternalLogicStub.ADMIN_USER_ID, ExternalLogicStub.LOCATION1_ID);
		logicImpl.saveBlog(blog);
		assertNotNull(blog.getId());

		blog1.setProfile("Changed");
		logicImpl.saveBlog(blog1);
	}
	
}
