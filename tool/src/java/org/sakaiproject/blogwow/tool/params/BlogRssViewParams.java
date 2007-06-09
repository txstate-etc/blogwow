/******************************************************************************
 * BlogRssViewParams.java - created by Sakai App Builder -AZ
 * 
 * Copyright (c) 2006 Sakai Project/Sakai Foundation
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 *****************************************************************************/

package org.sakaiproject.blogwow.tool.params;

import uk.org.ponder.rsf.viewstate.SimpleViewParameters;

/**
 * View parameters which are used for RSS feeds
 * 
 * @author Sakai App Builder -AZ
 */
public class BlogRssViewParams extends SimpleViewParameters {

    public Long blogId;
    public String locationId;

    public BlogRssViewParams() {
    }

    /**
     * Used for RSS for a single blog feed
     * @param viewid
     * @param blogId
     */
    public BlogRssViewParams(String viewid, Long blogId) {
        this.viewID = viewid;
        this.blogId = blogId;
    }

    /**
     * Used for RSS for a group of blogs related to a location
     * @param viewid
     * @param locationId
     */
    public BlogRssViewParams(String viewid, String locationId) {
        this.viewID = viewid;
        this.locationId = locationId;
    }

}
