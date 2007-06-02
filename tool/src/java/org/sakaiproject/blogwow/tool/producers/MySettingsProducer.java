package org.sakaiproject.blogwow.tool.producers;

import uk.org.ponder.rsf.components.UICommand;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIForm;
import uk.org.ponder.rsf.components.UIInput;
import uk.org.ponder.rsf.components.UIMessage;
import uk.org.ponder.rsf.evolvers.TextInputEvolver;
import uk.org.ponder.rsf.view.ComponentChecker;
import uk.org.ponder.rsf.view.ViewComponentProducer;
import uk.org.ponder.rsf.viewstate.ViewParameters;

public class MySettingsProducer implements ViewComponentProducer {
  public static final String VIEWID = "my_settings";
  
  public NavBarRenderer navBarRenderer;
  public TextInputEvolver richTextEvolver;
  
  public String getViewID() {
    return VIEWID;
  }

  public void fillComponents(UIContainer tofill, ViewParameters viewparams, ComponentChecker checker) {
    navBarRenderer.makeNavBar(tofill, "navIntraTool:", VIEWID);
    
    UIMessage.make(tofill, "my-settings-header", "blogwow.settings.settingsheader");
    
    UIForm form = UIForm.make(tofill, "my-settings-form");
    
    UIMessage.make(form, "my-blog-profile", "blogwow.settings.profile");
    
    UIInput profiletext = UIInput.make(form, "profile-text-input:", "todo.binding");
    richTextEvolver.evolveTextInput(profiletext);
    
    UIMessage.make(form, "picture-url-label", "blogwow.settings.pictureURLtext");
    UIInput.make(form, "picture-url-input", "todo.binding");
    
    UICommand.make(tofill, "change-settings-button", UIMessage.make("blogwow.settings.save"));
    UICommand.make(tofill, "cancel-settings-button", UIMessage.make("blogwow.settings.cancel"));
  }

}