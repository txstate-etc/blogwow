package org.sakaiproject.blogwow.tool.producers;

import uk.org.ponder.messageutil.MessageLocator;
import uk.org.ponder.rsf.components.UICommand;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIForm;
import uk.org.ponder.rsf.components.UIInput;
import uk.org.ponder.rsf.components.UIMessage;
import uk.org.ponder.rsf.components.UISelect;
import uk.org.ponder.rsf.components.UISelectChoice;
import uk.org.ponder.rsf.components.UIVerbatim;
import uk.org.ponder.rsf.components.decorators.DecoratorList;
import uk.org.ponder.rsf.components.decorators.UILabelTargetDecorator;
import uk.org.ponder.rsf.evolvers.TextInputEvolver;
import uk.org.ponder.rsf.view.ComponentChecker;
import uk.org.ponder.rsf.view.ViewComponentProducer;
import uk.org.ponder.rsf.viewstate.ViewParameters;

public class AddEntryProducer implements ViewComponentProducer {
  public static final String VIEWID = "add_entry";
  
  public NavBarRenderer navBarRenderer;
  public TextInputEvolver richTextEvolver;
  public MessageLocator messageLocator;
  
  public String getViewID() {
    return VIEWID;
  }

  public void fillComponents(UIContainer tofill, ViewParameters viewparams, ComponentChecker checker) {
    navBarRenderer.makeNavBar(tofill, "navIntraTool:", VIEWID);
    
    // If this is existing, should be: blogwow.add_edit.editheader
    UIMessage.make(tofill, "add-entry-header", "blogwow.add_edit.addheader");
    
    UIForm form = UIForm.make(tofill, "edit-blog-entry-form");
    
    UIMessage.make(form, "title-label", "blogwow.add_edit.title");
    UIInput.make(form, "title-input", "todo.binding");
    
    UIInput blogtext = UIInput.make(form, "blog-text-input:", "todo.binding");
    richTextEvolver.evolveTextInput(blogtext);
    
    UIMessage.make(form, "privacy-instructions", "blogwow.add_edit.accesstext");
    
    String [] privacyRadioValues = new String[] {"instructors_only","all_members","public_viewable"};
    String [] privacyRadioLabelKeys = new String[] {"blogwow.add_edit.private","blogwow.add_edit.sitemembers","blogwow.add_edit.public"};
  
    UISelect privacyRadios = UISelect.make(form, "privacy-radio-holder", privacyRadioValues, privacyRadioLabelKeys, "todo.binding", null).setMessageKeys();
    
    String selectID = privacyRadios.getFullID();
    UISelectChoice instructorsOnlyRadio = UISelectChoice.make(form, "instructors-only-radio", selectID, 0);
    UIVerbatim instructorsOnlyLabel = UIVerbatim.make(form, "instructors-only-label", messageLocator.getMessage("blogwow.add_edit.private"));
    instructorsOnlyLabel.decorators = new DecoratorList(new UILabelTargetDecorator(instructorsOnlyRadio));
    
    UISelectChoice allMembersRadio = UISelectChoice.make(form, "all-members-radio", selectID, 1);
    UIVerbatim allMembersLabel = UIVerbatim.make(form, "all-members-label", messageLocator.getMessage("blogwow.add_edit.sitemembers"));
    allMembersLabel.decorators = new DecoratorList(new UILabelTargetDecorator(allMembersRadio));
    
    UISelectChoice publicViewableRadio = UISelectChoice.make(form, "public-viewable-radio", selectID, 2);
    UIVerbatim publicViewableLabel = UIVerbatim.make(form, "public-viewable-label", messageLocator.getMessage("blogwow.add_edit.public"));
    publicViewableLabel.decorators = new DecoratorList(new UILabelTargetDecorator(publicViewableRadio));
    
    UICommand.make(form, "publish-button", UIMessage.make("blogwow.add_edit.publish"));
    UICommand.make(form, "save-button", UIMessage.make("blogwow.add_edit.save"));
    UICommand.make(form, "cancel-button", UIMessage.make("blogwow.add_edit.cancel"));
  }

}