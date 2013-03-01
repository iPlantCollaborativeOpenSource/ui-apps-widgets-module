package org.iplantc.core.uiapps.widgets.client.view.fields;

import org.iplantc.core.uiapps.widgets.client.models.AppTemplateAutoBeanFactory;
import org.iplantc.core.uiapps.widgets.client.models.Argument;
import org.iplantc.core.uiapps.widgets.client.models.selection.SelectionArgument;
import org.iplantc.core.uiapps.widgets.client.models.selection.SelectionProperties;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.shared.Splittable;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.tips.ToolTipConfig;

/**
 * @author jstroot
 * 
 */
public class AppWizardComboBox implements ArgumentField {
    private final SelectionProperties props = GWT.create(SelectionProperties.class);
    private final ListStore<SelectionArgument> store = new ListStore<SelectionArgument>(props.id());

    ComboBox<SelectionArgument> field = new ComboBox<SelectionArgument>(store, props.display());

    public AppWizardComboBox(Argument argument) {
        store.addAll(argument.getArguments());

    }

    // @Override
    // public HandlerRegistration addInvalidHandler(InvalidHandler handler) {
    // return field.addInvalidHandler(handler);
    // }
    //
    // @Override
    // public HandlerRegistration addValidHandler(ValidHandler handler) {
    // return field.addValidHandler(handler);
    // }

    @Override
    public Widget asWidget() {
        return field;
    }

    @Override
    public void setValue(Splittable value) {
        // Assume that this value is a SelectionArgument
        AppTemplateAutoBeanFactory factory = GWT.create(AppTemplateAutoBeanFactory.class);
        AutoBean<SelectionArgument> bean = AutoBeanCodex.decode(factory, SelectionArgument.class, value);
        // SelectionArgument arg = store.findModelWithKey(bean.as().getId());
        // field.expand();
        // field.select(arg);
        // field.collapse();
    }

    @Override
    public Splittable getValue() {
        return AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(field.getCurrentValue()));
    }

    @Override
    public void setToolTipConfig(ToolTipConfig toolTip) {
        field.setToolTipConfig(toolTip);
    }

    @Override
    public void clear() {
        field.clear();
    }

    @Override
    public void clearInvalid() {
        field.clearInvalid();
    }

    @Override
    public void reset() {
        field.reset();
    }

    @Override
    public boolean isValid(boolean preventMark) {
        return field.isValid(preventMark);
    }

    @Override
    public boolean validate(boolean preventMark) {
        return field.validate(preventMark);
    }

}