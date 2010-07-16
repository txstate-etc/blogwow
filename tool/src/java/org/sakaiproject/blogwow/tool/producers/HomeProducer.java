package org.sakaiproject.blogwow.tool.producers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.sakaiproject.blogwow.logic.BlogLogic;
import org.sakaiproject.blogwow.logic.EntryLogic;
import org.sakaiproject.blogwow.logic.ExternalLogic;
import org.sakaiproject.blogwow.model.BlogWowBlog;
import org.sakaiproject.blogwow.model.BlogWowEntry;
import org.sakaiproject.blogwow.tool.beans.MugshotGenerator;
import org.sakaiproject.blogwow.tool.params.SimpleBlogParams;

import uk.org.ponder.rsf.components.UIBranchContainer;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIInternalLink;
import uk.org.ponder.rsf.components.UILink;
import uk.org.ponder.rsf.components.UIMessage;
import uk.org.ponder.rsf.components.UIOutput;
import uk.org.ponder.rsf.view.ComponentChecker;
import uk.org.ponder.rsf.view.DefaultView;
import uk.org.ponder.rsf.view.ViewComponentProducer;
import uk.org.ponder.rsf.viewstate.ViewParameters;

public class HomeProducer implements ViewComponentProducer, DefaultView {

    public static final String VIEW_ID = "home";
    public String getViewID() {
        return VIEW_ID;
    }

    private static final long DAY = 1000 * 60 * 60 * 24;
    
    private NavBarRenderer navBarRenderer;
    private BlogLogic blogLogic;
    private EntryLogic entryLogic;
    private Locale locale;
    private MugshotGenerator mugshotGenerator;
    private ExternalLogic externalLogic;

    public void fillComponents(UIContainer tofill, ViewParameters viewparams, ComponentChecker checker) {

        String currentUserId = externalLogic.getCurrentUserId();
        String locationId = externalLogic.getCurrentLocationId();

        UIMessage.make(tofill, "page-title", "blogwow.homepage.title");

        navBarRenderer.makeNavBar(tofill, "navIntraTool:", VIEW_ID);
        
        UIMessage.make(tofill, "blogger", "blogwow.homepage.nameheader");
        UIMessage.make(tofill, "entries", "blogwow.homepage.entryheader");
        UIMessage.make(tofill, "last-updated", "blogwow.homepage.lastupdated");

        BlogWowBlog myblog = blogLogic.makeBlogByLocationAndUser(locationId, currentUserId );
        if (myblog != null && myblog.getId() != null) {
            UIBranchContainer myBlogInfo = UIBranchContainer.make(tofill, "my-blog-exists:");
            UIInternalLink.make(myBlogInfo, "my-blog-link", 
                    UIMessage.make("blogwow.homepage.userbloglink"), 
                    new SimpleBlogParams(BlogViewProducer.VIEW_ID, myblog.getId()));
            // use a date which is related to the current users locale
            DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
            DateFormat tf = DateFormat.getTimeInstance(DateFormat.MEDIUM, locale);

            List<BlogWowEntry> myentries = entryLogic.getAllVisibleEntries(myblog.getId(), currentUserId, null, true, 0, 1);
            if (myentries.size() > 0) {
                UIMessage.make(myBlogInfo, "last-blogged-date", "blogwow.homepage.userlastblogged", 
                        new Object[] { df.format( myentries.get(0).getDateModified() ),
                        tf.format( myentries.get(0).getDateModified() ) });
            }

            UILink.make(myBlogInfo, "my-rss-link", externalLogic.getBlogRssUrl(myblog.getId()));
        } else {
            UIMessage.make(tofill, "no-my-blog", "blogwow.homepage.noblog");
        }

        UIMessage.make(tofill, "all-blogs-header", "blogwow.homepage.listofblogs");

        // This needs more work due to URL-encoding of path segments
//        UILink.make(tofill, "all-blog-rss", UIMessage.make("blogwow.homepage.RSStext"), 
//                externalLogic.getBlogLocationRssUrl(locationId));

        List<BlogWowBlog> blogs = blogLogic.getAllVisibleBlogs(locationId, null, true, 0, 0);
        UIBranchContainer blogsTable = UIBranchContainer.make(tofill, "blog-list-table:");
        
        // use a date which is related to the current users locale
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, locale);
        DateFormat dfTime = DateFormat.getTimeInstance(DateFormat.SHORT, locale);
        
        // use a simple day of week for recent dates
        String justDayOfWeekFormat = "EEEE";
        SimpleDateFormat dayOfWeek = new SimpleDateFormat(justDayOfWeekFormat);

        for (int i = 0; i < blogs.size(); i++) {
            BlogWowBlog blog = blogs.get(i);
            Integer entriesCount = entryLogic.getVisibleEntryCount(blog.getId(), currentUserId);

            if (entriesCount > 0) {
	            UIBranchContainer row = UIBranchContainer.make(blogsTable, "row:", i+"");
	            UILink.make(row, "user-icon", mugshotGenerator.getMugshotUrl(blog.getOwnerId()));
	            UIInternalLink.make(row, "blog-title-link", blog.getTitle(), 
	                    new SimpleBlogParams(BlogViewProducer.VIEW_ID, blog.getId()));
	            UIOutput.make(row, "number-of-entries", entriesCount +"");
            	List<BlogWowEntry> entries = entryLogic.getAllVisibleEntries(blog.getId(), currentUserId, null, true, 0, 1);
								try
								{
									Date date = entries.get(0).getDateModified();
									Date dateNow = new Date();
									long dateNowMillis = dateNow.getTime();
									Date dateYesterday = new Date(dateNowMillis - DAY);
									Date dateLastWeek = new Date(dateNowMillis - (DAY*5));
									
									// get start of today; midnight
									Calendar cal = Calendar.getInstance();
									cal.setTime(dateNow);
									cal.set(Calendar.SECOND, 0);
									cal.set(Calendar.MINUTE, 0);
									cal.set(Calendar.HOUR_OF_DAY, 0);
									long midnightMillis = cal.getTimeInMillis();
									Date dateMidnight = new Date(midnightMillis);
									
									if (date.after(dateMidnight)) {
										UIOutput.make(row, "time-last-updated", dfTime.format(date));
									}
									else if (date.after(dateLastWeek)) {
										UIOutput.make(row, "time-last-updated", dayOfWeek.format(date) + ", " + dfTime.format(date));
									}
									else {
										UIOutput.make(row, "time-last-updated", df.format(date));
									}        
                	
								}catch (IndexOutOfBoundsException e) {
									// This shouldn't happen, our count doesn't agree with the number of entries we get
									UIOutput.make(row, "time-last-updated", "???" );
								}

	            // UIInternalLink.make(row, "rss-link", new BlogParams(BlogRSSProducer.VIEWID, blog.getId().toString()));
	            UILink.make(row, "rss-link", externalLogic.getBlogRssUrl(blog.getId()));
	            
            }
        }

    }

    public void setBlogLogic(BlogLogic blogLogic) {
        this.blogLogic = blogLogic;
    }

    public void setEntryLogic(EntryLogic entryLogic) {
        this.entryLogic = entryLogic;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void setMugshotGenerator(MugshotGenerator mugshotGenerator) {
        this.mugshotGenerator = mugshotGenerator;
    }

    public void setNavBarRenderer(NavBarRenderer navBarRenderer) {
        this.navBarRenderer = navBarRenderer;
    }

    public void setExternalLogic(ExternalLogic externalLogic) {
        this.externalLogic = externalLogic;
    }

}
