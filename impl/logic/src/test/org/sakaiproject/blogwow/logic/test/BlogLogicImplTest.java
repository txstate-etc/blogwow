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
import org.easymock.MockControl;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.blogwow.dao.BlogWowDao;
import org.sakaiproject.blogwow.logic.impl.BlogLogicImpl;
import org.sakaiproject.blogwow.model.BlogWowBlog;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.user.api.UserDirectoryService;
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

	private final String USER1_ID = "user-11111111";
	private final String SITE1_ID = "site-1111111";
	private final String USER2_ID = "user-22222222";
	private final String SITE2_ID = "site-2222222";
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
		blog1 = new BlogWowBlog(USER1_ID, SITE1_ID, BLOG1_PROFILE, "", new Date());
		blog2 = new BlogWowBlog(USER2_ID, SITE1_ID, BLOG2_PROFILE, "", new Date());
		blog3 = new BlogWowBlog(USER1_ID, SITE2_ID, BLOG3_PROFILE, "", new Date());
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

		// preload the DB for testing
		dao.save(blog1);
		dao.save(blog2);
		dao.save(blog3);
	}

	/**
	 * add some tests
	 */

	
}
