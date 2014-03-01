package org.iplantc.core.uiapps.widgets.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

import org.iplantc.core.uiapps.widgets.client.events.ArgumentRequiredChangedEvent.ArgumentRequiredChangedEventHandler;

public class ArgumentRequiredChangedEvent extends GwtEvent<ArgumentRequiredChangedEventHandler> {

    public interface ArgumentRequiredChangedEventHandler extends EventHandler {
        void onArgumentRequiredChanged(ArgumentRequiredChangedEvent event);
    }

    public static interface HasArgumentRequiredChangedHandlers {
        HandlerRegistration addArgumentRequiredChangedEventHandler(ArgumentRequiredChangedEventHandler handler);
    }

    public static final GwtEvent.Type<ArgumentRequiredChangedEventHandler> TYPE = new GwtEvent.Type<ArgumentRequiredChangedEventHandler>();
    private final boolean required;

    public ArgumentRequiredChangedEvent(Boolean value) {
        this.required = value == null ? false : value;
    }

    @Override
    public GwtEvent.Type<ArgumentRequiredChangedEventHandler> getAssociatedType() {
        return TYPE;
    }

    public boolean isRequired() {
        return required;
    }

    @Override
    protected void dispatch(ArgumentRequiredChangedEventHandler handler) {
        handler.onArgumentRequiredChanged(this);
    }
}