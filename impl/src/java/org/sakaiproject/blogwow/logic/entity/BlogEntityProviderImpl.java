/******************************************************************************
 * BlogEntryEntityProviderImpl.java - created by aaronz on Jun 21, 2007
 * 
 * Copyright (c) 2007 Centre for Academic Research in Educational Technologies
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 * Contributors:
 * Aaron Zeckoski (aaronz@vt.edu) - primary
 * 
 *****************************************************************************/

package org.sakaiproject.blogwow.logic.entity;

import org.sakaiproject.blogwow.logic.BlogLogic;
import org.sakaiproject.blogwow.logic.entity.BlogEntityProvider;
import org.sakaiproject.entitybroker.entityprovider.CoreEntityProvider;
import org.sakaiproject.entitybroker.entityprovider.capabilities.AutoRegisterEntityProvider;

/**
 * Implementation of blog entity provider
 * 
 * @author Aaron Zeckoski (aaronz@vt.edu)
 */
public class BlogEntityProviderImpl implements BlogEntityProvider, CoreEntityProvider, AutoRegisterEntityProvider {

    private BlogLogic blogLogic;
    public void setBlogLogic(BlogLogic blogLogic) {
        this.blogLogic = blogLogic;
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.entitybroker.entityprovider.EntityProvider#getEntityPrefix()
     */
    public String getEntityPrefix() {
        return ENTITY_PREFIX;
    }

    public boolean entityExists(String id) {
        // entity is real if there are any entries that match this id
        String blogId = id;
        if (blogLogic.getBlogById(blogId) != null) {
            return true;
        }
        return false;
    }

}
