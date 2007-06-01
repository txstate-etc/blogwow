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

	public final String USER_NAME = "username";

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
		securityService.isSuperUser(ExternalLogicStub.USER_ID); // normal user
		securityServiceControl.setReturnValue(false, MockControl.ZERO_OR_MORE); // return for above param
		securityService.isSuperUser(ExternalLogicStub.MAINT_USER_ID); // maintain user
		securityServiceControl.setReturnValue(false, MockControl.ZERO_OR_MORE); // return for above param
		securityService.isSuperUser(ExternalLogicStub.ADMIN_USER_ID); // admin user
		securityServiceControl.setReturnValue(true, MockControl.ZERO_OR_MORE); // return for above param
		securityService.isSuperUser(ExternalLogicStub.INVALID_USER_ID); // invalid user
		securityServiceControl.setReturnValue(false, MockControl.ZERO_OR_MORE); // return for above param

		siteService.siteReference(ExternalLogicStub.LOCATION1_ID); // expect this to be called
		siteServiceControl.setReturnValue(ExternalLogicStub.LOCATION1_ID, MockControl.ZERO_OR_MORE);
		siteService.siteReference(ExternalLogicStub.LOCATION2_ID); // expect this to be called
		siteServiceControl.setReturnValue(ExternalLogicStub.LOCATION2_ID, MockControl.ZERO_OR_MORE);
		try {
			siteService.getSite(ExternalLogicStub.LOCATION1_ID); // expect this to be called
			siteServiceControl.setReturnValue(new TestSite(ExternalLogicStub.LOCATION1_ID, 
					ExternalLogicStub.LOCATION1_TITLE, ExternalLogicStub.LOCATION1_ID), MockControl.ZERO_OR_MORE);
			siteService.getSite(ExternalLogicStub.LOCATION2_ID); // expect this to be called
			siteServiceControl.setReturnValue(new TestSite(ExternalLogicStub.LOCATION2_ID, 
					ExternalLogicStub.LOCATION2_TITLE, ExternalLogicStub.LOCATION2_ID), MockControl.ZERO_OR_MORE);
		} catch (IdUnusedException e) {
			// just added try-catch because we have to in order to compile
			throw new IllegalStateException("Could not create siteService test object");
		}

		toolManager.getCurrentPlacement(); // expect this to be called
		toolManagerControl.setDefaultReturnValue(new TestPlacement(ExternalLogicStub.LOCATION1_ID));

		sessionManager.getCurrentSessionUserId(); // expect this to be called
		sessionManagerControl.setDefaultReturnValue(ExternalLogicStub.USER_ID);

	}

	/**
	 * Test method for {@link org.sakaiproject.blogwow.logic.impl.ExternalLogicImpl#getUserDisplayName(java.lang.String)}.
	 */
	public void testGetUserDisplayName() {
		try {
			userDirectoryService.getUser(ExternalLogicStub.USER_ID);
		} catch (UserNotDefinedException e) { } // expect this to be called
		userDirectoryServiceControl.setReturnValue(new TestUser(ExternalLogicStub.USER_ID, USER_NAME, ExternalLogicStub.USER_DISPLAY)); // return this

		// activate the mock object
		userDirectoryServiceControl.replay();

		// mock object is needed here
		String userDisplayName = externalLogicImpl.getUserDisplayName(ExternalLogicStub.USER_ID);

		// verify the mock object was used
		userDirectoryServiceControl.verify();

		Assert.assertNotNull(userDisplayName);
		Assert.assertEquals(userDisplayName, ExternalLogicStub.USER_DISPLAY);
	}

	/**
	 * Test method for {@link org.sakaiproject.blogwow.logic.impl.ExternalLogicImpl#getCurrentLocation()}.
	 */
	public void testGetCurrentLocation() {
		// set up this mock object
		toolManager.getCurrentPlacement(); // expect this to be called
		toolManagerControl.setReturnValue(new TestPlacement(ExternalLogicStub.LOCATION1_ID)); // return this

		// activate the mock object
		toolManagerControl.replay();
		siteServiceControl.replay();

		// mock object is needed here
		String locationId = externalLogicImpl.getCurrentLocationId();

		// verify the mock object was used
		toolManagerControl.verify();
		siteServiceControl.verify();

		Assert.assertNotNull(locationId);
		Assert.assertEquals(locationId, ExternalLogicStub.LOCATION1_ID);
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
		Assert.assertEquals(userId, ExternalLogicStub.USER_ID);
	}

	public void testIsUserAdmin() {
		// set up mock objects with return values

		// activate the mock objects
		securityServiceControl.replay();

		// mock objects needed here
		Assert.assertTrue( externalLogicImpl.isUserAdmin(ExternalLogicStub.ADMIN_USER_ID) );
		Assert.assertFalse( externalLogicImpl.isUserAdmin(ExternalLogicStub.MAINT_USER_ID) );
		Assert.assertFalse( externalLogicImpl.isUserAdmin(ExternalLogicStub.USER_ID) );

		// verify the mock objects were used
		securityServiceControl.verify();
	}

	public void testIsUserAllowedInLocation() {
		// set up mock objects with return values
		securityService.unlock(ExternalLogicStub.ADMIN_USER_ID, 
				ExternalLogic.BLOG_ENTRY_WRITE_ANY, ExternalLogicStub.LOCATION1_ID);
		securityServiceControl.setReturnValue(true, MockControl.ZERO_OR_MORE);
		securityService.unlock(ExternalLogicStub.MAINT_USER_ID, 
				ExternalLogic.BLOG_ENTRY_WRITE_ANY, ExternalLogicStub.LOCATION1_ID);
		securityServiceControl.setReturnValue(true, MockControl.ZERO_OR_MORE);
		securityService.unlock(ExternalLogicStub.USER_ID, 
				ExternalLogic.BLOG_ENTRY_WRITE_ANY, ExternalLogicStub.LOCATION1_ID);
		securityServiceControl.setReturnValue(false, MockControl.ZERO_OR_MORE);

		// activate the mock objects
		securityServiceControl.replay();
		siteServiceControl.replay();

		// mock objects needed here
		Assert.assertTrue( externalLogicImpl.isUserAllowedInLocation(
				ExternalLogicStub.ADMIN_USER_ID, ExternalLogic.BLOG_ENTRY_WRITE_ANY, ExternalLogicStub.LOCATION1_ID) );
		Assert.assertTrue( externalLogicImpl.isUserAllowedInLocation(
				ExternalLogicStub.MAINT_USER_ID, ExternalLogic.BLOG_ENTRY_WRITE_ANY, ExternalLogicStub.LOCATION1_ID) );
		Assert.assertFalse( externalLogicImpl.isUserAllowedInLocation(
				ExternalLogicStub.USER_ID, ExternalLogic.BLOG_ENTRY_WRITE_ANY, ExternalLogicStub.LOCATION1_ID) );

		// verify the mock objects were used
		securityServiceControl.verify();
		siteServiceControl.verify();
	}

}
