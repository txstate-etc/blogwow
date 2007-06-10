package org.sakaiproject.blogwow.tool.otp;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.sakaiproject.blogwow.logic.CommentLogic;
import org.sakaiproject.blogwow.logic.ExternalLogic;
import org.sakaiproject.blogwow.model.BlogWowComment;

import uk.org.ponder.beanutil.BeanLocator;

public class CommentLocator implements BeanLocator {

    private ExternalLogic externalLogic;
    private CommentLogic commentLogic;

    private Map<String, BlogWowComment> commentmap = new HashMap<String, BlogWowComment>();

    public Object locateBean(String name) {
        BlogWowComment togo = commentmap.get(name);
        if (togo == null && name.startsWith("NEW")) {
            BlogWowComment comment = new BlogWowComment();
            comment.setOwnerId(externalLogic.getCurrentUserId());
            comment.setText("");
            Date d = new Date();
            comment.setDateCreated(d);
            comment.setDateModified(d);
            commentmap.put(name, comment);
            togo = comment;
        }
        else if (togo == null) {
            togo = commentLogic.getCommentById(new Long(name), externalLogic.getCurrentLocationId());
            commentmap.put(name, togo);
        }
        return togo;
    }

    public String publishAll() {
        for (Iterator i = commentmap.keySet().iterator(); i.hasNext();) {
            String key = (String) i.next();
            BlogWowComment comment = (BlogWowComment) commentmap.get(key);
            if (key.startsWith("NEW")) {
                // could do stuff
            }
            commentLogic.saveComment(comment, externalLogic.getCurrentLocationId());
        }
        return "published";
    }

    public String cancelAll() {
        commentmap.clear();
        return "cancelled";
    }


    public void setCommentLogic(CommentLogic commentLogic) {
        this.commentLogic = commentLogic;
    }

    public void setExternalLogic(ExternalLogic externalLogic) {
        this.externalLogic = externalLogic;
    }


}
