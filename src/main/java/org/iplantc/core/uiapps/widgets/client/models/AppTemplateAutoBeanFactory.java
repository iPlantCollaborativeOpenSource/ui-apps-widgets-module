package org.iplantc.core.uiapps.widgets.client.models;

import org.iplantc.core.uiapps.widgets.client.models.selection.SelectionItem;
import org.iplantc.core.uiapps.widgets.client.models.selection.SelectionItemGroup;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

public interface AppTemplateAutoBeanFactory extends AutoBeanFactory {
    
    AutoBean<AppTemplate> appTemplate();
    
    AutoBean<ArgumentGroup> argumentGroup();
    
    AutoBean<Argument> argument();

    AutoBean<ArgumentValidator> argumentValidator();

    AutoBean<SelectionItem> selectionItem();

    AutoBean<SelectionItemGroup> selectionItemGroup();

    AutoBean<DataObject> dataObject();

    AutoBean<JobExecution> jobExecution();

}
