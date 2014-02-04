package org.iplantc.de.apps.widgets.client.models.metadata;

import java.util.Date;

import org.iplantc.de.commons.client.models.HasId;

import com.google.gwt.user.client.ui.HasName;
import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

public interface ReferenceGenome extends HasName, HasId {

    @PropertyName("created_by")
    String getCreatedBy();

    @PropertyName("created_on")
    Date getCreationDate();

    boolean isDeleted();

    @PropertyName("last_modified_by")
    String getLastModifiedBy();

    @PropertyName("last_modified_on")
    Date getModifiedDate();

    @PropertyName("uuid")
    @Override
    String getId();

    String getPath();

}
