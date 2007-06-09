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
import org.sakaiproject.blogwow.model.BlogWowBlog;
import org.sakaiproject.blogwow.model.BlogWowComment;
import org.sakaiproject.blogwow.model.BlogWowEntry;
import org.sakaiproject.blogwow.tool.params.BlogParams;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserNotDefinedException;

import uk.org.ponder.rsf.components.ELReference;
import uk.org.ponder.rsf.components.ParameterList;
import uk.org.ponder.rsf.components.UIBranchContainer;
import uk.org.ponder.rsf.components.UICommand;
import uk.org.ponder.rsf.components.UIContainer;
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
import uk.org.ponder.rsf.flow.jsfnav.NavigationCase;
import uk.org.ponder.rsf.flow.jsfnav.NavigationCaseReporter;
import uk.org.ponder.rsf.view.ComponentChecker;
import uk.org.ponder.rsf.view.ViewComponentProducer;
import uk.org.ponder.rsf.viewstate.SimpleViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParamsReporter;
import uk.org.ponder.util.UniversalRuntimeException;

public class BlogViewProducer implements ViewComponentProducer,
ViewParamsReporter, NavigationCaseReporter {

    public static final String VIEWID = "blog_view";
    public String getViewID() {
        return VIEWID;
    }

    private NavBarRenderer navBarRenderer;
    private BlogLogic blogLogic;
    private EntryLogic entryLogic;
    private CommentLogic commentLogic;
    private String userid;
    private Locale locale;
    private Site site;
    private UserDirectoryService userDirectoryService;

    public void fillComponents(UIContainer tofill, ViewParameters viewparams, ComponentChecker checker) {

        // use a date which is related to the current users locale
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);

        BlogParams params = (BlogParams) viewparams;

        navBarRenderer.makeNavBar(tofill, "navIntraTool:", VIEWID);

        BlogWowBlog blog = blogLogic.getBlogById(new Long(params.blogid));

        UIOutput.make(tofill, "blog-title", blog.getTitle());
        UIVerbatim.make(tofill, "profile-verbatim-text", blog.getProfile());
        String profileImageUrl = blog.getIcon();
        if (profileImageUrl == null || profileImageUrl.equals("")) {
            profileImageUrl = "../images/sakaiger-600.png";
        }
        UILink.make(tofill, "profile-image", profileImageUrl);

        List<BlogWowEntry> entries = new ArrayList<BlogWowEntry>();
        if (params.entryid == null) {
            entries = entryLogic.getAllVisibleEntries(blog.getId(), userid, null, false, 0, 10);
        }
        else {
            entries.add(entryLogic.getEntryById(new Long(params.entryid), site.getReference()));
        }
        
        if (entries.size() <= 0) {
            UIOutput.make(tofill, "blog-entry:empty");
            return;
        }

        for (int i = 0; i < entries.size(); i++) {
            UIBranchContainer entrydiv = UIBranchContainer.make(tofill, "blog-entry:", i+"");
            BlogWowEntry entry = entries.get(i);
            UIOutput.make(entrydiv, "blog-title", entry.getTitle());
            UIOutput.make(entrydiv, "blog-date", df.format( entry.getDateCreated() ) );
            UIVerbatim.make(entrydiv, "verbatim-blog-text", entry.getText());

            UIOutput.make(entrydiv, "action-items");
            if (entryLogic.canWriteEntry(entry.getId(), userid)) {
                UIInternalLink.make(entrydiv, "edit-entry-link", UIMessage.make("blogwow.blogview.edit"),
                        new BlogParams(AddEntryProducer.VIEWID, entry.getId().toString()));

                UIForm removeform = UIForm.make(entrydiv, "remove-entry-form");

                UICommand removeCommand = UICommand.make(removeform, "remove-command", "", "RemoveBlogEntryCmd.execute");
                removeCommand.parameters = new ParameterList(new UIELBinding("RemoveBlogEntryCmd.entryId", entry.getId().toString()));

                UILink removelink = UILink.make(entrydiv, "remove-entry-link", "");
                Map<String, String> attr = new HashMap<String, String>();
                // TODO - GLARG!!!!! -AZ
                attr.put("onclick", "document.getElementById('"+removeCommand.getFullID()+"').click();return false;");
                removelink.decorators = new DecoratorList(new UIFreeAttributeDecorator(attr));
            }
            
            List<BlogWowComment> comments = commentLogic.getComments(entry.getId(), null, true, 0, 10000);
            UIInternalLink.make(entrydiv, "comments-link", UIMessage.make("blogwow.blogview.comments", new Object[] {(comments.size()+"")}),
                    new BlogParams(BlogViewProducer.VIEWID, blog.getId().toString(), entry.getId().toString(), true));
            UIInternalLink.make(entrydiv, "add-comment-link", UIMessage.make("blogwow.blogview.addcomment"), 
                    new BlogParams(BlogViewProducer.VIEWID, blog.getId().toString(), entry.getId().toString(), true, true));
        
            // Render Comments if they are visible
            if (params.showcomments) {
                for (int j = 0; j < comments.size(); j++) {
                    BlogWowComment comment = comments.get(j);
                    UIBranchContainer commentdiv = UIBranchContainer.make(entrydiv, "comment-div:", j+"");
                    String username = "";
                    try {
                        username = userDirectoryService.getUser(comment.getOwnerId()).getDisplayName();
                    } catch (UserNotDefinedException e) {
                        throw UniversalRuntimeException.accumulate(e);
                    }
                    UIMessage.make(commentdiv, "comment-header", "blogwow.comments.commentstitle", new Object[]{username, comment.getDateCreated()});
                    UIOutput.make(commentdiv, "comment-text", comment.getText());
                }
            }
            
            // Render Leave Comment if comments are visible
            if (params.showcomments) {
                UIOutput.make(entrydiv, "add-comment-div");
                UIForm addCommentForm = UIForm.make(entrydiv, "add-comment-form");
                UIMessage.make(entrydiv, "add-comment-header", "blogwow.comments.addcommenttitle");
                UIInput.make(addCommentForm, "comment-text", "CommentLocator.NEW.text", "");
                UICommand publishButton = UICommand.make(addCommentForm, "publish-button", UIMessage.make("blogwow.comments.submit"), "CommentLocator.publishAll");
                publishButton.parameters = new ParameterList(new UIELBinding("CommentLocator.NEW.entry", new ELReference("EntryLocator."+entry.getId())));
                
                UICommand.make(addCommentForm, "cancel-button", UIMessage.make("blogwow.comments.cancel"), "CommentLocator.cancelAll");
            }
        }
    }

    public ViewParameters getViewParameters() {
        return new BlogParams();
    }

    public List reportNavigationCases() {
        List<NavigationCase> l = new ArrayList<NavigationCase>();
        l.add(new NavigationCase(null,new SimpleViewParameters(HomeProducer.VIEWID)));
        return l;
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

    public void setSite(Site site) {
        this.site = site;
    }

    public void setUserDirectoryService(UserDirectoryService userDirectoryService) {
        this.userDirectoryService = userDirectoryService;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

}