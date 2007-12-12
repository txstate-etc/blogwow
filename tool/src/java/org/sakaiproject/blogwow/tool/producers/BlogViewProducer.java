package org.sakaiproject.blogwow.tool.producers;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.sakaiproject.blogwow.logic.BlogLogic;
import org.sakaiproject.blogwow.logic.CommentLogic;
import org.sakaiproject.blogwow.logic.EntryLogic;
import org.sakaiproject.blogwow.logic.ExternalLogic;
import org.sakaiproject.blogwow.model.BlogWowBlog;
import org.sakaiproject.blogwow.model.BlogWowComment;
import org.sakaiproject.blogwow.model.BlogWowEntry;
import org.sakaiproject.blogwow.model.constants.BlogConstants;
import org.sakaiproject.blogwow.tool.otp.CommentLocator;
import org.sakaiproject.blogwow.tool.params.BlogEntryParams;
import org.sakaiproject.blogwow.tool.params.BlogParams;

import uk.org.ponder.rsf.components.ELReference;
import uk.org.ponder.rsf.components.ParameterList;
import uk.org.ponder.rsf.components.UIBranchContainer;
import uk.org.ponder.rsf.components.UICommand;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIDeletionBinding;
import uk.org.ponder.rsf.components.UIELBinding;
import uk.org.ponder.rsf.components.UIForm;
import uk.org.ponder.rsf.components.UIInput;
import uk.org.ponder.rsf.components.UIInternalLink;
import uk.org.ponder.rsf.components.UILink;
import uk.org.ponder.rsf.components.UIMessage;
import uk.org.ponder.rsf.components.UIOutput;
import uk.org.ponder.rsf.components.UIVerbatim;
import uk.org.ponder.rsf.components.decorators.DecoratorList;
import uk.org.ponder.rsf.components.decorators.UIFreeAttributeDecorator;
import uk.org.ponder.rsf.components.decorators.UIAlternativeTextDecorator;
import uk.org.ponder.rsf.components.decorators.UITooltipDecorator;
import uk.org.ponder.rsf.view.ComponentChecker;
import uk.org.ponder.rsf.view.ViewComponentProducer;
import uk.org.ponder.rsf.viewstate.ViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParamsReporter;

public class BlogViewProducer implements ViewComponentProducer, ViewParamsReporter {

    public static final String VIEW_ID = "blog_view";
    public String getViewID() {
        return VIEW_ID;
    }

    private NavBarRenderer navBarRenderer;
    private BlogLogic blogLogic;
    private EntryLogic entryLogic;
    private CommentLogic commentLogic;
    private Locale locale;
    private ExternalLogic externalLogic;

    public void fillComponents(UIContainer tofill, ViewParameters viewparams, ComponentChecker checker) {

        UIMessage.make(tofill, "page-title", "blogwow.blogview.title");

        int entriesPerPage = 10;
        
        String entryLocator = "EntryLocator";
        String commentLocator = "CommentLocator";
        String currentUserId = externalLogic.getCurrentUserId();

        // use a date which is related to the current users locale
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);

        BlogParams params = (BlogParams) viewparams;

        navBarRenderer.makeNavBar(tofill, "navIntraTool:", VIEW_ID);

        BlogWowBlog blog = blogLogic.getBlogById(params.blogid);

        UIOutput.make(tofill, "blog-title", blog.getTitle());
        UILink.make(tofill, "blog-url", externalLogic.getBlogUrl(blog.getId()) );
        String profileText = blog.getProfile();
        if (profileText == null || profileText.equals("")) {
            UIMessage.make(tofill, "profile-verbatim-text", "blogwow.blogview.noprofile");
        }
        else {
            UIVerbatim.make(tofill, "profile-verbatim-text", profileText);
        }
        String profileImageUrl = blog.getImageUrl();
        if (profileImageUrl == null || profileImageUrl.equals("")) {
            profileImageUrl = null; //this will use the default in the template
        }
        UILink.make(tofill, "profile-image", profileImageUrl);

        List<BlogWowEntry> entries = new ArrayList<BlogWowEntry>();
        if (params.entryid == null) {
        	if (params.skip == null) {
        		entries = entryLogic.getAllVisibleEntries(blog.getId(), currentUserId, null, false, 0, entriesPerPage);
        	}
        	else {
        		entries = entryLogic.getAllVisibleEntries(blog.getId(), currentUserId, null, false, params.skip, entriesPerPage);
        	}
        } else {
            entries.add(entryLogic.getEntryById(params.entryid, externalLogic.getCurrentLocationId()));
        }

        if (entries.size() <= 0) {
            UIOutput.make(tofill, "blog-entry:empty");
            return;
        }

        for (int i = 0; i < entries.size(); i++) {
            BlogWowEntry entry = entries.get(i);
            String entryOTP = entryLocator + "." + entry.getId();

            UIBranchContainer entrydiv = UIBranchContainer.make(tofill, "blog-entry:", i + "");
            UIOutput.make(entrydiv, "blog-title", entry.getTitle());
            UIOutput.make(entrydiv, "blog-date", df.format(entry.getDateCreated()));

            String privSetting = entry.getPrivacySetting();
            if (privSetting.equals(BlogConstants.PRIVACY_PRIVATE))
						{
							fillEntryIcon(entrydiv, "../images/lock.png", "blogwow.blogview.draftviewalt", "blogwow.blogview.draftviewtitle", true);
						} 
						else if (privSetting.equals(BlogConstants.PRIVACY_GROUP_LEADER))
            {
							fillEntryIcon(entrydiv, "../images/user_gray.png", "blogwow.blogview.privviewalt", "blogwow.blogview.privviewtitle", false);
            }
            else if (privSetting.equals(BlogConstants.PRIVACY_GROUP))
            {
							fillEntryIcon(entrydiv, "../images/group.png", "blogwow.blogview.siteviewalt", "blogwow.blogview.siteviewtitle", false);
						}
            else if (privSetting.equals(BlogConstants.PRIVACY_PUBLIC))
            {
							fillEntryIcon(entrydiv, "../images/world.png", "blogwow.blogview.pubviewalt", "blogwow.blogview.pubviewtitle", false);
            }

            UIVerbatim.make(entrydiv, "verbatim-blog-text", entry.getText());

            UIOutput.make(entrydiv, "action-items");
            if (entryLogic.canWriteEntry(entry.getId(), currentUserId)) {
                UIInternalLink.make(entrydiv, "entry-link:", UIMessage.make("blogwow.blogview.edit-entry"), 
                        new BlogEntryParams(AddEntryProducer.VIEW_ID, blog.getId(), entry.getId()));

                // remove the entry using a OTP deletion binding
                UIForm removeform = UIForm.make(entrydiv, "remove-entry-form");
                UICommand removeCommand = UICommand.make(removeform, "remove-command");
                removeCommand.parameters.add(new UIDeletionBinding(entryOTP));

                // create a fake link that submits the form
                UILink removelink = UILink.make(entrydiv, "entry-link:", 
                        UIMessage.make("blogwow.blogview.remove-entry"), null);
                Map<String, String> attr = new HashMap<String, String>();

                // TODO ack! Inline Java Script
                attr.put("onclick", "document.getElementById('" + removeCommand.getFullID() + "').click();return false;");
                removelink.decorators = new DecoratorList(new UIFreeAttributeDecorator(attr));
            }

            List<BlogWowComment> comments = commentLogic.getComments(entry.getId(), null, true, 0, 0);
            UIInternalLink.make(entrydiv, "entry-link:", UIMessage.make("blogwow.blogview.comments",
                    new Object[] { (comments.size() + "") }), 
                    new BlogParams(BlogViewProducer.VIEW_ID, blog.getId(), entry.getId(), true));
            UIInternalLink.make(entrydiv, "entry-link:", UIMessage.make("blogwow.blogview.add-comment"), new BlogParams(
                    BlogViewProducer.VIEW_ID, blog.getId(), entry.getId(), true, true));

            UIInternalLink.make(entrydiv, "entry-link:", UIMessage.make("blogwow.permalink.permalinktitle"), externalLogic.getBlogEntryUrl(entry.getId()));

            // Render Comments if they are visible
            if (params.showcomments) {
                for (int j = 0; j < comments.size(); j++) {
                    BlogWowComment comment = comments.get(j);
                    UIBranchContainer commentdiv = UIBranchContainer.make(entrydiv, "comment-div:", j + "");
                    String username = externalLogic.getUserDisplayName(comment.getOwnerId());

                    UIMessage.make(commentdiv, "comment-header", "blogwow.comments.commentstitle", 
                            new Object[] { username, df.format(comment.getDateCreated()) });
                    UIOutput.make(commentdiv, "comment-text", comment.getText());
                }
            }

            // Render Leave Comment if comments are visible
            if (params.addcomment) {
                String commentOTP = commentLocator + "." + CommentLocator.NEW_1;

                UIOutput.make(entrydiv, "add-comment-div");
                UIForm addCommentForm = UIForm.make(entrydiv, "add-comment-form");
                UIMessage.make(entrydiv, "add-comment-header", "blogwow.comments.addcommenttitle");
                UIInput commentInput = UIInput.make(addCommentForm, "comment-text", commentOTP + ".text", "");

                UICommand publishButton = UICommand.make(addCommentForm, "publish-button", UIMessage.make("blogwow.comments.submit"),
                        commentLocator + ".publishAll");
                publishButton.parameters = new ParameterList(new UIELBinding(commentOTP + ".entry", 
                        new ELReference(entryLocator + "." + entry.getId())));
                UICommand.make(addCommentForm, "cancel-button", UIMessage.make("blogwow.comments.cancel"));

                //TODO ack! Inline Java Script
                UIVerbatim.make(entrydiv, "scoll-here-script", "document.getElementById('" + commentInput.getFullID()
                        + "').scrollIntoView(true);");
                UIVerbatim.make(entrydiv, "scoll-here-script", "document.getElementById('" + commentInput.getFullID() + "').focus();");
            }

						// Render forward and back buttons if we have more entries
						if (params.skip != null) {
							if (params.skip + entriesPerPage < entryLogic.getVisibleEntryCount(blog.getId(), currentUserId))
						  {
						     UIMessage um = UIMessage.make("blogwow.blogview.previous", new Object[] { entriesPerPage });
						     BlogParams bp = new BlogParams(BlogViewProducer.VIEW_ID, blog.getId(), params.skip+entriesPerPage);
						     UIInternalLink.make(tofill, "previous-page", um, bp);

                 UILink ul = UILink.make(tofill, "back-img", null);
                 DecoratorList dl = new DecoratorList();
                 dl.add(new UIAlternativeTextDecorator(UIMessage.make("blogwow.blogview.previousalt")));
                 ul.decorators = dl;
               }
               if (params.skip > 0)
               {
                 UIMessage um = UIMessage.make("blogwow.blogview.next", new Object[] { entriesPerPage });
                 BlogParams bp = new BlogParams(BlogViewProducer.VIEW_ID, blog.getId(), params.skip-entriesPerPage);
                 UIInternalLink.make(tofill, "next-page", um, bp);
 
                 UILink ul = UILink.make(tofill, "forward-img", null);
                 DecoratorList dl = new DecoratorList();
                 dl.add(new UIAlternativeTextDecorator(UIMessage.make("blogwow.blogview.nextalt")));
                 ul.decorators = dl;
               }
             }
             else if (entryLogic.getVisibleEntryCount(blog.getId(), currentUserId) > entriesPerPage)
             {
               UIMessage um = UIMessage.make("blogwow.blogview.previous", new Object[] { entriesPerPage });
               BlogParams bp = new BlogParams(BlogViewProducer.VIEW_ID, blog.getId(), entriesPerPage);
               UIInternalLink.make(tofill, "previous-page", um, bp);
 
               UILink ul = UILink.make(tofill, "back-img", null);
               DecoratorList dl = new DecoratorList();
               dl.add(new UIAlternativeTextDecorator(UIMessage.make("blogwow.blogview.previousalt")));
               ul.decorators = dl;
             }
        }
    }

		private void fillEntryIcon(UIBranchContainer entrydiv, String imgUrl, String altKey, String titleKey, Boolean draft)
		{
			UILink ul = UILink.make(entrydiv, "blog-visibility", imgUrl);
			DecoratorList dl = new DecoratorList();
			dl.add(new UIAlternativeTextDecorator(UIMessage.make(altKey)));
			dl.add(new UITooltipDecorator(UIMessage.make(titleKey)));
			ul.decorators = dl;

			if (draft)
			{
				UIMessage.make(entrydiv, "blog-draft", "blogwow.blogview.draft");
			}
			else
			{
				UIOutput.make(entrydiv, "blog-draft", " ");
			}
		}

    public ViewParameters getViewParameters() {
        return new BlogParams();
    }

    public void setBlogLogic(BlogLogic blogLogic) {
        this.blogLogic = blogLogic;
    }

    public void setCommentLogic(CommentLogic commentLogic) {
        this.commentLogic = commentLogic;
    }

    public void setEntryLogic(EntryLogic entryLogic) {
        this.entryLogic = entryLogic;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void setNavBarRenderer(NavBarRenderer navBarRenderer) {
        this.navBarRenderer = navBarRenderer;
    }

    public void setExternalLogic(ExternalLogic externalLogic) {
        this.externalLogic = externalLogic;
    }

}
