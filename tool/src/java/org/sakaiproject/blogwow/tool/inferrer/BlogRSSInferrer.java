package org.sakaiproject.blogwow.tool.inferrer;

import org.sakaiproject.blogwow.logic.entity.BlogRssEntityProvider;
import org.sakaiproject.blogwow.tool.params.BlogParams;
import org.sakaiproject.blogwow.tool.producers.BlogRSSProducer;

import uk.ac.cam.caret.sakai.rsf.entitybroker.EntityViewParamsInferrer;
import uk.org.ponder.rsf.viewstate.ViewParameters;
import org.sakaiproject.entitybroker.IdEntityReference;

public class BlogRSSInferrer implements EntityViewParamsInferrer {

    public String[] getHandledPrefixes() {
        return new String[] {BlogRssEntityProvider.ENTITY_PREFIX};
    }

    public ViewParameters inferDefaultViewParameters(String reference) {
        IdEntityReference ref = new IdEntityReference(reference);
        return new BlogParams(BlogRSSProducer.VIEWID, ref.id);
    }

}
