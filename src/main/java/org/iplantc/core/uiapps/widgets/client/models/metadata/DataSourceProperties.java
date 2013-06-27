package org.iplantc.core.uiapps.widgets.client.models.metadata;

import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface DataSourceProperties extends PropertyAccess<DataSource> {

    ModelKeyProvider<DataSource> id();

    LabelProvider<DataSource> label();

}