package org.sakaiproject.blogwow.tool.producers;

import java.util.ArrayList;
import java.util.List;

import org.sakaiproject.blogwow.logic.BlogLogic;
import org.sakaiproject.blogwow.tool.params.BlogParams;

import uk.org.ponder.rsf.components.UICommand;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIForm;
import uk.org.ponder.rsf.components.UIInput;
import uk.org.ponder.rsf.components.UIMessage;
import uk.org.ponder.rsf.evolvers.TextInputEvolver;
import uk.org.ponder.rsf.flow.jsfnav.NavigationCase;
import uk.org.ponder.rsf.flow.jsfnav.NavigationCaseReporter;
import uk.org.ponder.rsf.view.ComponentChecker;
import uk.org.ponder.rsf.view.ViewComponentProducer;
import uk.org.ponder.rsf.viewstate.SimpleViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParamsReporter;

public class MySettingsProducer implements 
  ViewComponentProducer,
  ViewParamsReporter,
  NavigationCaseReporter
{
  public static final String VIEWID = "my_settings";
  
  public NavBarRenderer navBarRenderer;
  public TextInputEvolver richTextEvolver;
  public BlogLogic blogLogic;
  
  public String getViewID() {
    return VIEWID;
  }

  public void fillComponents(UIContainer tofill, ViewParameters viewparams, ComponentChecker checker) {
    BlogParams params = (BlogParams) viewparams;
    String blogid = params.blogid;
    
    navBarRenderer.makeNavBar(tofill, "navIntraTool:", VIEWID);
    
    UIMessage.make(tofill, "my-settings-header", "blogwow.settings.settingsheader");
    
    UIForm form = UIForm.make(tofill, "my-settings-form");
    
    UIMessage.make(form, "my-blog-profile", "blogwow.settings.profile");
    
    UIInput profiletext = UIInput.make(form, "profile-text-input:", "BlogLocator."+blogid+".profile");
    richTextEvolver.evolveTextInput(profiletext);
    
    UIMessage.make(form, "picture-url-label", "blogwow.settings.pictureURLtext");
    UIInput.make(form, "picture-url-input", "BlogLocator."+blogid+".imageUrl");
    
    UICommand.make(form, "change-settings-button", 
        UIMessage.make("blogwow.settings.save"), "BlogLocator.saveAll");
    UICommand.make(form, "cancel-settings-button", 
        UIMessage.make("blogwow.settings.cancel"), "BlogLocator.cancelAll");
  }

  public ViewParameters getViewParameters() {
    return new BlogParams();
  }

  public List reportNavigationCases() {
    List l = new ArrayList();
    l.add(new NavigationCase(null, new SimpleViewParameters(HomeProducer.VIEWID)));
    return l;
  }

}