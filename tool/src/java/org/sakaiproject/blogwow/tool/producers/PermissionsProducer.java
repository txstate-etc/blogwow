package org.sakaiproject.blogwow.tool.producers;

import java.util.ArrayList;
import java.util.List;

import org.sakaiproject.authz.api.PermissionsHelper;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.ToolSession;

import uk.ac.cam.caret.sakai.rsf.helper.HelperViewParameters;
import uk.org.ponder.messageutil.MessageLocator;
import uk.org.ponder.rsf.components.UICommand;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIOutput;
import uk.org.ponder.rsf.flow.jsfnav.NavigationCase;
import uk.org.ponder.rsf.view.ComponentChecker;
import uk.org.ponder.rsf.view.ViewComponentProducer;
import uk.org.ponder.rsf.viewstate.SimpleViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParameters;

public class PermissionsProducer implements ViewComponentProducer {
	public static final String HELPER = "sakai.permissions.helper";
	public static final String VIEWID = "Permissions";

	// Injection
	public SessionManager sessionManager;
	public Site site;
	public MessageLocator messageLocator;

	public String getViewID() {
		return VIEWID;
	}

	public void fillComponents(UIContainer tofill, ViewParameters viewparams, ComponentChecker checker) {
		ToolSession session = sessionManager.getCurrentToolSession();

		session.setAttribute(PermissionsHelper.TARGET_REF, site.getReference());
		session.setAttribute(PermissionsHelper.DESCRIPTION, "Set mail permissions for " +  site.getTitle());
		session.setAttribute(PermissionsHelper.PREFIX, "blogwow.");

		UIOutput.make(tofill, HelperViewParameters.HELPER_ID, HELPER);
		UICommand.make(tofill, HelperViewParameters.POST_HELPER_BINDING, "", null);
	}

	public ViewParameters getViewParameters() {
		return new HelperViewParameters();
	}

	public List reportNavigationCases() {
		List<NavigationCase> l = new ArrayList<NavigationCase>();
		l.add(new NavigationCase(null, new SimpleViewParameters(HomeProducer.VIEWID)));
		return l;
	}

}
