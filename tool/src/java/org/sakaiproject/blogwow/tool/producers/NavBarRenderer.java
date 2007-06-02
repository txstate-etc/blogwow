package org.sakaiproject.blogwow.tool.producers;

import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIInternalLink;
import uk.org.ponder.rsf.components.UIJointContainer;
import uk.org.ponder.rsf.components.UILink;
import uk.org.ponder.rsf.components.UIMessage;
import uk.org.ponder.rsf.components.UIOutput;
import uk.org.ponder.rsf.viewstate.SimpleViewParameters;

public class NavBarRenderer {
  
  public void makeNavBar(UIContainer tofill, String divID, String currentViewID) {
    UIJointContainer joint = new UIJointContainer(tofill, divID, "blog-wow-navigation:", ""+1);
    
    UILink.make(joint, "item:icon", "../images/page_white_edit.png");
    if (currentViewID.equals(AddEntryProducer.VIEWID))
      UIMessage.make(joint, "item:text", "blogwow.navbar.add");
    else
      UIInternalLink.make(joint, "item:link", UIMessage.make("blogwow.navbar.add"), new SimpleViewParameters(AddEntryProducer.VIEWID));
    
    UIOutput.make(joint, "item:separator");
    
    UILink.make(joint, "item:icon", "../images/page_white_edit.png");
    if (currentViewID.equals(HomeProducer.VIEWID))
      UIMessage.make(joint, "item:text", "blogwow.navbar.bloglist");
    else
      UIInternalLink.make(joint, "item:link", UIMessage.make("blogwow.navbar.bloglist"), new SimpleViewParameters(HomeProducer.VIEWID));

    UIOutput.make(joint, "item:separator");
    
    UILink.make(joint, "item:icon", "../images/cog.png");
    if (currentViewID.equals(MySettingsProducer.VIEWID))
      UIMessage.make(joint, "item:text", "blogwow.navbar.settings");
    else 
      UIInternalLink.make(joint, "item:link", UIMessage.make("blogwow.navbar.settings"), new SimpleViewParameters(MySettingsProducer.VIEWID));   
   
    UIOutput.make(joint, "item:separator");
    
    UILink.make(joint, "item:icon", "../images/group_gear.png");
    if (currentViewID.equals(PermissionsProducer.VIEWID))
      UIMessage.make(joint, "item:text", "blogwow.navbar.permissions");
    else
      UIInternalLink.make(joint, "item:link", UIMessage.make("blogwow.navbar.permissions"), new SimpleViewParameters(PermissionsProducer.VIEWID));  
    
  }
}
