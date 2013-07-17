package org.iplantc.core.uiapps.widgets.client.view.editors;

import org.iplantc.core.resources.client.IplantResources;
import org.iplantc.core.uiapps.widgets.client.events.ArgumentGroupSelectedEvent;
import org.iplantc.core.uiapps.widgets.client.events.RequestArgumentGroupDeleteEvent;
import org.iplantc.core.uiapps.widgets.client.events.RequestArgumentGroupDeleteEvent.RequestArgumentGroupDeleteEventHandler;
import org.iplantc.core.uiapps.widgets.client.models.Argument;
import org.iplantc.core.uiapps.widgets.client.models.ArgumentGroup;
import org.iplantc.core.uiapps.widgets.client.services.AppMetadataServiceFacade;
import org.iplantc.core.uiapps.widgets.client.view.editors.properties.ArgumentGroupPropertyEditor;
import org.iplantc.core.uiapps.widgets.client.view.editors.style.ContentPanelHoverHeaderSelectionAppearance;
import org.iplantc.de.client.UUIDServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.ValueAwareEditor;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.ContentPanel.ContentPanelAppearance;

/**
 * This class contains a {@link ContentPanel} whose header text is updated when the bound
 * <code>ArgumentGroup</code> is set/updated.
 * 
 * @author jstroot
 * 
 */
class ArgumentGroupEditor implements HasPropertyEditor, IsWidget, ValueAwareEditor<ArgumentGroup> {
    interface HeaderTextTemplates extends SafeHtmlTemplates {
        @SafeHtmlTemplates.Template("<span style=\"color: red;\">*&nbsp</span>")
        SafeHtml fieldLabelRequired();
    }

    @Ignore
    final ContentPanel groupField;
    private final HeaderTextTemplates templates = GWT.create(HeaderTextTemplates.class);
    ArgumentListEditor argumentsEditor;

    @Path("")
    ArgumentGroupPropertyEditor argGrpPropEditor;
    private final ImageElement headerErrorIcon;

    public ArgumentGroupEditor(final AppTemplateWizardPresenter presenter, final UUIDServiceAsync uuidService, final AppMetadataServiceFacade appMetadataService, final XElement scrollElement) {
        ContentPanelAppearance cpAppearance;
        if (presenter.isEditingMode()) {
            cpAppearance = new ContentPanelHoverHeaderSelectionAppearance();
        } else {
            cpAppearance = GWT.create(ContentPanelAppearance.class);
        }
        groupField = new ContentPanel(cpAppearance) {
            @Override
            protected void assertPreRender() {
                // KLUDGE JDS Do nothing. This is a workaround for the following bug (which was submitted to the GXT forums); http://www.sencha.com/forum/showthread.php?261470-Adding-new-ContentPanel-to-AccordionLayoutContainer-at-runtime-issue
            }

            @Override
            protected void onClick(Event ce) {
                super.onClick(ce);
                if (presenter.isEditingMode() && header.getElement().isOrHasChild(ce.getEventTarget().<Element> cast())) {
                    presenter.asWidget().fireEvent(new ArgumentGroupSelectedEvent(argGrpPropEditor));
                }
            }
        };

        argumentsEditor = new ArgumentListEditor(presenter, uuidService, appMetadataService, scrollElement);
        groupField.add(argumentsEditor);
        if (presenter.isEditingMode()) {
            groupField.getHeader().addStyleName(presenter.getSelectionCss().selectionTargetBg());
            argGrpPropEditor = new ArgumentGroupPropertyEditor(presenter);
            groupField.sinkEvents(Event.ONCLICK | Event.MOUSEEVENTS);
        }
        headerErrorIcon = Document.get().createImageElement();
        headerErrorIcon.setSrc(IplantResources.RESOURCES.exclamation().getSafeUri().asString());
    }
    
    /**
     * This method must return a <code>ContentPanel</code> in order to be added to the
     * <code>AccordionLayoutContainer</code> used in the {@link ArgumentGroupListEditor#groupsContainer}.
     */
    @Override
    public Widget asWidget() {
        return groupField;
    }

    @Override
    public void setValue(ArgumentGroup value) {
        SafeHtmlBuilder labelText = new SafeHtmlBuilder();
        if (argumentsEditor.hasErrors()) {
            labelText.appendHtmlConstant(headerErrorIcon.getString());
        }
        for (Argument property : value.getArguments()) {
            if (property.getRequired()) {
                // If any field is required, it needs to be marked as such.
                labelText.append(templates.fieldLabelRequired());
                break;
            }
        }
        // When the value is set, update the FieldSet header text
        labelText.appendEscaped(value.getLabel());
        groupField.setHeadingHtml(labelText.toSafeHtml());
    }

    @Override
    public void flush() {/* Do Nothing */}

    @Override
    public void setDelegate(EditorDelegate<ArgumentGroup> delegate) {/* Do Nothing */}

    @Override
    public void onPropertyChange(String... paths) {/* Do Nothing */}

    @Override
    public IsWidget getPropertyEditor() {
        return argGrpPropEditor;
    }

    public void addRequestArgumentGroupDeleteEventHandler(RequestArgumentGroupDeleteEventHandler handler) {
        argGrpPropEditor.addHandler(handler, RequestArgumentGroupDeleteEvent.TYPE);
    }

}
