package org.iplantc.core.widgets.client.appWizard.view.fields;


import java.util.List;

import org.iplantc.core.widgets.client.appWizard.models.Argument;
import org.iplantc.core.widgets.client.appWizard.models.ArgumentValidator;
import org.iplantc.core.widgets.client.appWizard.models.ArgumentValidatorType;
import org.iplantc.core.widgets.client.appWizard.util.AppWizardFieldFactory;
import org.iplantc.core.widgets.client.appWizard.view.fields.converters.SplittableToStringConverter;

import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.autobean.shared.Splittable;
import com.sencha.gxt.data.shared.Converter;
import com.sencha.gxt.widget.core.client.event.InvalidEvent.InvalidHandler;
import com.sencha.gxt.widget.core.client.event.ValidEvent.ValidHandler;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.tips.ToolTipConfig;

/**
 * 
 * XXX Needs to be a bounded text field (can't enter after limit)
 * How to do that when adding a validator.
 * 
 * @author jstroot
 * 
 */
public class AppWizardTextField implements ArgumentField, LeafValueEditor<Splittable> {
    
    final TextField field = new TextField();
    private final Converter<Splittable, String> converter;
    
    public AppWizardTextField(){
        converter = new SplittableToStringConverter();
    }

    @Override
    public void initialize(Argument property) {
        field.setAllowBlank(!property.isRequired());

        List<ArgumentValidator> validators = property.getValidators();
        if ((validators != null) && !validators.isEmpty()) {
            for (ArgumentValidator tv : validators) {
                if (tv.getType().equals(ArgumentValidatorType.CharacterLimit)) {
                    // Param should be array of one integer.
                    int limit = Double.valueOf(tv.getParams().get(0).asNumber()).intValue();
                    field.addKeyDownHandler(new PreventEntryAfterLimitHandler(field, limit));
                }
                if (tv.getType().equals(ArgumentValidatorType.CharacterLimit) 
                        || tv.getType().equals(ArgumentValidatorType.FileName) 
                        || tv.getType().equals(ArgumentValidatorType.Regex)) {
                    field.addValidator(AppWizardFieldFactory.createStringValidator(tv));
                }


            }
        }
    }

    @Override
    public HandlerRegistration addInvalidHandler(InvalidHandler handler) {
        return field.addInvalidHandler(handler);
    }

    @Override
    public HandlerRegistration addValidHandler(ValidHandler handler) {
        return field.addValidHandler(handler);
    }

    @Override
    public Widget asWidget() {
        return field;
    }

    @Override
    public void setValue(Splittable value) {
        if(value == null)
            return;
        field.setValue(converter.convertModelValue(value));
    }

    @Override
    public Splittable getValue() {
        return converter.convertFieldValue(field.getValue());
    }

    @Override
    public void setToolTip(ToolTipConfig toolTip) {
        field.setToolTipConfig(toolTip);
    }

}
