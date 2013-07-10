package org.iplantc.core.uiapps.widgets.client.models.metadata;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface DataSourceProperties extends PropertyAccess<DataSource> {

    ModelKeyProvider<DataSource> id();

    LabelProvider<DataSource> label();

    @Path("label")
    ValueProvider<DataSource, String> labelValue();

}
