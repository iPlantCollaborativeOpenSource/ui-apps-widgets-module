package org.iplantc.core.client.widgets.appWizard.models.legacy;

import java.util.List;

import org.iplantc.core.uicommons.client.models.HasId;
import org.iplantc.core.uicommons.client.models.HasLabel;

import com.google.gwt.user.client.ui.HasName;

public interface LegacyAppTemplate extends HasId, HasName, HasLabel {

    String getType();

    void setType(String type);

    List<LegacyTemplateGroup> getGroups();

    void setGroups(List<LegacyTemplateGroup> groups);
}