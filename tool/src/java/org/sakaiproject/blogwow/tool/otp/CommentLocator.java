package org.sakaiproject.blogwow.tool.otp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.sakaiproject.blogwow.logic.CommentLogic;
import org.sakaiproject.blogwow.logic.ExternalLogic;
import org.sakaiproject.blogwow.model.BlogWowComment;

import uk.org.ponder.beanutil.BeanLocator;

public class CommentLocator implements BeanLocator {

    public static final String NEW_PREFIX = "new ";
    public static String NEW_1 = NEW_PREFIX + "1";

    private ExternalLogic externalLogic;
    private CommentLogic commentLogic;

    private Map<String, BlogWowComment> delivered = new HashMap<String, BlogWowComment>();

    public Object locateBean(String name) {
        BlogWowComment togo = delivered.get(name);
        if (togo == null) {
            if (name.startsWith(NEW_PREFIX)) {
                // create the new object
                togo = new BlogWowComment(null, externalLogic.getCurrentUserId(), null, new Date(), new Date());
            } else {
                togo = commentLogic.getCommentById(name, externalLogic.getCurrentLocationId());
            }
            delivered.put(name, togo);
        }
        return togo;
    }

    public String publishAll() {
        for (String key : delivered.keySet()) {
            BlogWowComment comment = delivered.get(key);
            if (key.startsWith(NEW_PREFIX)) {
                // could do stuff here
            }
            commentLogic.saveComment(comment, externalLogic.getCurrentLocationId());
        }
        return "published";
    }


    public void setCommentLogic(CommentLogic commentLogic) {
        this.commentLogic = commentLogic;
    }

    public void setExternalLogic(ExternalLogic externalLogic) {
        this.externalLogic = externalLogic;
    }


}
