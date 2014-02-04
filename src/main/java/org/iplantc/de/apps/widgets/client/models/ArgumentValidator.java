package org.iplantc.de.apps.widgets.client.models;

import org.iplantc.core.uicommons.client.models.HasId;

import com.google.web.bindery.autobean.shared.Splittable;

public interface ArgumentValidator extends HasId {

    String VALIDATOR = "validator";
    String KEY_DOWN_HANDLER_REG = "keyDownHandlerRegistration";
    String KEY_DOWN_HANDLER = "keyDownHandler";

    void setId(String id);

    ArgumentValidatorType getType();
    
    void setType(ArgumentValidatorType type);
    
    Splittable getParams();
    
    void setParams(Splittable params);
}