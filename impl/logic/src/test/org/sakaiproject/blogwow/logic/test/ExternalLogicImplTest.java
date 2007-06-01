/******************************************************************************
 * ExternalLogicImplTest.java - created by aaronz on 1 Jun 2007
 * 
 * Copyright (c) 2007 Centre for Academic Research in Educational Technologies
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 * Contributors:
 * Aaron Zeckoski (aaronz@vt.edu) - primary
 * 
 *****************************************************************************/

package org.sakaiproject.blogwow.logic.test;

import junit.framework.Assert;

import org.easymock.MockControl;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.blogwow.logic.ExternalLogic;
import org.sakaiproject.blogwow.logic.impl.ExternalLogicImpl;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.springframework.test.AbstractTransactionalSpringContextTests;

/**
 * Tests for the external logic
 * 
 * @author Aaron Zeckoski (aaronz@vt.edu)
 */
public class ExternalLogicImplTest extends AbstractTransactionalSpringContextTests {

	protected ExternalLogicImpl externalLogicImpl;

	public final String USER_NAME = "aaronz";
	public final String USER_ID = "user-11111111";
	public final String USER_DISPLAY = "Aaron Zeckoski";
	public final String ADMIN_USER_ID = "admin";
	public final String ADMIN_USER_NAME = "admin";
	public final String ADMIN_USER_DISPLAY = "Administrator";
	public final String MAINT_USER_ID = "main-22222222";
	public final String MAINT_USER_NAME = "maintainer";
	public final String MAINT_USER_DISPLAY = "Maint User";
	public final String STUDENT_USER_ID = "student-12121212";
	public final String INVALID_USER_ID = "invalid-XXXX";

	public final String CONTEXT1 = "testContext1";
	public final String CONTEXT1_TITLE = "C1 title";
	public final String CONTEXT2 = "testContext2";
	public final String CONTEXT2_TITLE = "C2 title";
	public final String SITE_ID = "site-1111111";
	public final String SITE_REF = "siteref-1111111";
	public final String SITE2_ID = "site-22222222";
	public final String SITE2_REF = "siteref-22222222";

	private SecurityService securityService;
	private MockControl securityServiceControl;
	private SessionManager sessionManager;
	private MockControl sessionManagerControl;
	private SiteService siteService;
	private MockControl siteServiceControl;
	private ToolManager toolManager;
	private MockControl toolManagerControl;
	private UserDirectoryService userDirectoryService;
	private MockControl userDirectoryServiceControl;

	protected String[] getConfigLocations() {
		// point to the needed spring config files, must be on the classpath
		// (add component/src/webapp/WEB-INF to the build path in Eclipse),
		// they also need to be referenced in the project.xml file
		return new String[] {"hibernate-test.xml", "spring-hibernate.xml"};
	}

	// run this before each test starts
	protected void onSetUpBeforeTransaction() throws Exception {
		// create test objects
	}

	// run this before each test starts and as part of the transaction
	protected void onSetUpInTransaction() {

		// setup the mock objects
		securityServiceControl = MockControl.createControl(SecurityService.class);
		securityService = (SecurityService) securityServiceControl.getMock();
		sessionManagerControl = MockControl.createControl(SessionManager.class);
		sessionManager = (SessionManager) sessionManagerControl.getMock();
		siteServiceControl = MockControl.createControl(SiteService.class);
		siteService = (SiteService) siteServiceControl.getMock();
		toolManagerControl = MockControl.createControl(ToolManager.class);
		toolManager = (ToolManager) toolManagerControl.getMock();
		userDirectoryServiceControl = MockControl.createControl(UserDirectoryService.class);
		userDirectoryService = (UserDirectoryService) userDirectoryServiceControl.getMock();

		// create and setup the object to be tested
		externalLogicImpl = new ExternalLogicImpl();
		externalLogicImpl.setSecurityService(securityService);
		externalLogicImpl.setSessionManager(sessionManager);
		externalLogicImpl.setSiteService(siteService);
		externalLogicImpl.setToolManager(toolManager);
		externalLogicImpl.setUserDirectoryService(userDirectoryService);

		// can set up the default mock object returns here if desired
		// Note: Still need to activate them in the test methods though
		securityService.isSuperUser(USER_ID); // normal user
		securityServiceControl.setReturnValue(false, MockControl.ZERO_OR_MORE); // return for above param
		securityService.isSuperUser(MAINT_USER_ID); // maintain user
		securityServiceControl.setReturnValue(false, MockControl.ZERO_OR_MORE); // return for above param
		securityService.isSuperUser(ADMIN_USER_ID); // admin user
		securityServiceControl.setReturnValue(true, MockControl.ZERO_OR_MORE); // return for above param
		securityService.isSuperUser(INVALID_USER_ID); // invalid user
		securityServiceControl.setReturnValue(false, MockControl.ZERO_OR_MORE); // return for above param

		siteService.siteReference(CONTEXT1); // expect this to be called
		siteServiceControl.setReturnValue(SITE_REF, MockControl.ZERO_OR_MORE);
		siteService.siteReference(CONTEXT2); // expect this to be called
		siteServiceControl.setReturnValue(SITE2_REF, MockControl.ZERO_OR_MORE);
		try {
			siteService.getSite(CONTEXT1); // expect this to be called
			siteServiceControl.setReturnValue(new TestSite(CONTEXT1, CONTEXT1_TITLE), MockControl.ZERO_OR_MORE);
			siteService.getSite(CONTEXT2); // expect this to be called
			siteServiceControl.setReturnValue(new TestSite(CONTEXT1, CONTEXT2_TITLE), MockControl.ZERO_OR_MORE);
		} catch (IdUnusedException e) {
			// just added try-catch because we have to in order to compile
			throw new IllegalStateException("Could not create siteService test object");
		}

		toolManager.getCurrentPlacement(); // expect this to be called
		toolManagerControl.setDefaultReturnValue(new TestPlacement(SITE_ID));

		sessionManager.getCurrentSessionUserId(); // expect this to be called
		sessionManagerControl.setDefaultReturnValue(USER_ID);

	}

	/**
	 * Test method for {@link org.sakaiproject.blogwow.logic.impl.ExternalLogicImpl#getUserDisplayName(java.lang.String)}.
	 */
	public void testGetUserDisplayName() {
		try {
			userDirectoryService.getUser(USER_ID);
		} catch (UserNotDefinedException e) { } // expect this to be called
		userDirectoryServiceControl.setReturnValue(new TestUser(USER_ID, USER_NAME, USER_DISPLAY)); // return this

		// activate the mock object
		userDirectoryServiceControl.replay();

		// mock object is needed here
		String userDisplayName = externalLogicImpl.getUserDisplayName(USER_ID);

		// verify the mock object was used
		userDirectoryServiceControl.verify();

		Assert.assertNotNull(userDisplayName);
		Assert.assertEquals(userDisplayName, USER_DISPLAY);
	}

	/**
	 * Test method for {@link org.sakaiproject.blogwow.logic.impl.ExternalLogicImpl#getCurrentContext()}.
	 */
	public void testGetCurrentContext() {
		// set up this mock object
		toolManager.getCurrentPlacement(); // expect this to be called
		toolManagerControl.setReturnValue(new TestPlacement(CONTEXT1)); // return this

		// activate the mock object
		toolManagerControl.replay();

		// mock object is needed here
		String siteId = externalLogicImpl.getCurrentContext();

		// verify the mock object was used
		toolManagerControl.verify();

		Assert.assertNotNull(siteId);
		Assert.assertEquals(siteId, CONTEXT1);
	}

	/**
	 * Test method for {@link org.sakaiproject.blogwow.logic.impl.ExternalLogicImpl#getCurrentUserId()}.
	 */
	public void testGetCurrentUserId() {
		// activate the mock object
		sessionManagerControl.replay();

		// mock object is needed here
		String userId = externalLogicImpl.getCurrentUserId();

		// verify the mock object was used
		sessionManagerControl.verify();
		
		Assert.assertNotNull(userId);
		Assert.assertEquals(userId, USER_ID);
	}

	public void testIsUserAdmin() {
		// set up mock objects with return values

		// activate the mock objects
		securityServiceControl.replay();

		// mock objects needed here
		Assert.assertTrue( externalLogicImpl.isUserAdmin(ADMIN_USER_ID) );
		Assert.assertFalse( externalLogicImpl.isUserAdmin(MAINT_USER_ID) );
		Assert.assertFalse( externalLogicImpl.isUserAdmin(USER_ID) );

		// verify the mock objects were used
		securityServiceControl.verify();
	}

	public void testIsUserAllowedInContext() {
		// set up mock objects with return values
		securityService.unlock(ADMIN_USER_ID, 
				ExternalLogic.BLOG_ENTRY_WRITE_ANY, SITE_REF);
		securityServiceControl.setReturnValue(true, MockControl.ZERO_OR_MORE);
		securityService.unlock(MAINT_USER_ID, 
				ExternalLogic.BLOG_ENTRY_WRITE_ANY, SITE_REF);
		securityServiceControl.setReturnValue(true, MockControl.ZERO_OR_MORE);
		securityService.unlock(USER_ID, 
				ExternalLogic.BLOG_ENTRY_WRITE_ANY, SITE_REF);
		securityServiceControl.setReturnValue(false, MockControl.ZERO_OR_MORE);

		// activate the mock objects
		securityServiceControl.replay();
		siteServiceControl.replay();

		// mock objects needed here
		Assert.assertTrue( externalLogicImpl.isUserAllowedInContext(
				ADMIN_USER_ID, ExternalLogic.BLOG_ENTRY_WRITE_ANY, CONTEXT1) );
		Assert.assertTrue( externalLogicImpl.isUserAllowedInContext(
				MAINT_USER_ID, ExternalLogic.BLOG_ENTRY_WRITE_ANY, CONTEXT1) );
		Assert.assertFalse( externalLogicImpl.isUserAllowedInContext(
				USER_ID, ExternalLogic.BLOG_ENTRY_WRITE_ANY, CONTEXT1) );

		// verify the mock objects were used
		securityServiceControl.verify();
		siteServiceControl.verify();
	}

}
