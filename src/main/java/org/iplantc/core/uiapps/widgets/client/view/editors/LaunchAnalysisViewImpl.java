package org.iplantc.core.uiapps.widgets.client.view.editors;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import com.sencha.gxt.data.shared.Converter;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.event.InvalidEvent;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.ConverterEditorAdapter;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.validator.MaxLengthValidator;

import org.iplantc.core.resources.client.uiapps.widgets.AppsWidgetsDisplayMessages;
import org.iplantc.core.uiapps.widgets.client.models.metadata.JobExecution;
import org.iplantc.core.uiapps.widgets.client.view.LaunchAnalysisView;
import org.iplantc.core.uiapps.widgets.client.view.editors.style.AppTemplateWizardAppearance;
import org.iplantc.core.uiapps.widgets.client.view.editors.validation.AnalysisOutputValidator;
import org.iplantc.core.uicommons.client.models.CommonModelUtils;
import org.iplantc.core.uicommons.client.models.HasId;
import org.iplantc.core.uicommons.client.models.UserInfo;
import org.iplantc.core.uicommons.client.models.UserSettings;
import org.iplantc.core.uicommons.client.validators.DiskResourceNameValidator;
import org.iplantc.core.uicommons.client.widgets.PreventEntryAfterLimitHandler;
import org.iplantc.core.uidiskresource.client.views.widgets.FolderSelectorField;

import java.util.List;

/**
 * @author jstroot
 * 
 */
public class LaunchAnalysisViewImpl implements LaunchAnalysisView {

    interface EditorDriver extends SimpleBeanEditorDriver<JobExecution, LaunchAnalysisViewImpl> {}

    @UiTemplate("LaunchAnalysisView.ui.xml")
    interface LaunchAnalysisWidgetUiBinder extends UiBinder<Widget, LaunchAnalysisViewImpl> {}

    @UiField(provided = true)
    AppsWidgetsDisplayMessages appWidgetStrings;

    @Ignore
    @UiField
    FolderSelectorField awFolderSel;

    @Ignore
    @UiField
    ContentPanel contentPanel;

    @UiField
    TextArea description;

    @UiField
    TextField name;

    @Path("outputDirectory")
    ConverterEditorAdapter<String, HasId, FolderSelectorField> outputDirectory;

    @UiField
    CheckBox retainInputs;

    @Inject
    private AppTemplateWizardAppearance appearance;

    private final EditorDriver editorDriver = GWT.create(EditorDriver.class);

    private UserSettings userSettings;

    @Inject
    public LaunchAnalysisViewImpl(LaunchAnalysisWidgetUiBinder binder, AppsWidgetsDisplayMessages appWidgetStrings, final UserSettings userSettings) {
    	this.appWidgetStrings = appWidgetStrings;
        this.userSettings = userSettings;
        binder.createAndBindUi(this);
        name.addValidator(new DiskResourceNameValidator());
        name.addKeyDownHandler(new PreventEntryAfterLimitHandler(name));
        name.addValidator(new MaxLengthValidator(PreventEntryAfterLimitHandler.DEFAULT_LIMIT));
        name.setAllowBlank(false);
        description.addKeyDownHandler(new PreventEntryAfterLimitHandler(description));
        description.addValidator(new MaxLengthValidator(PreventEntryAfterLimitHandler.DEFAULT_LIMIT));
        outputDirectory = new ConverterEditorAdapter<String, HasId, FolderSelectorField>(awFolderSel, new Converter<String, HasId>() {
            @Override
            public String convertFieldValue(HasId object) {
                if (object == null) {
                    return null;
                }
                return object.getId();
            }

            @Override
            public HasId convertModelValue(String object) {
                return CommonModelUtils.createHasIdFromString(object);
            }
        });
        awFolderSel.setValidatePermissions(true);
        awFolderSel.addValidator(new AnalysisOutputValidator());
        this.editorDriver.initialize(this);
    }

    @Override
    public Widget asWidget() {
        return contentPanel;
    }

    @Override
    public void edit(JobExecution je) {
        editorDriver.edit(je);
        // Update header on initial binding.
        updateHeader(je.getName());
    }

    @Override
    public JobExecution flushJobExecution() {
        JobExecution flush = editorDriver.flush();
        flush.setWorkspaceId(UserInfo.getInstance().getWorkspaceId());
        updateHeader(flush.getName());
        return flush;
    }

    @Override
    public List<EditorError> getErrors() {
        List<EditorError> errors = Lists.newArrayList(editorDriver.getErrors());
        errors.addAll(awFolderSel.getErrors());
        return errors;
    }

    @Override
    public boolean hasErrors() {
        return ((editorDriver.getErrors() != null) && editorDriver.hasErrors()) || !awFolderSel.getErrors().isEmpty();
    }

    @UiHandler("awFolderSel")
    void onFolderChanged(ValueChangeEvent<HasId> event) {
        if ((event.getValue() != null) && !event.getValue().getId().equals(userSettings.getDefaultOutputFolder())) {
            awFolderSel.setInfoErrorText(appWidgetStrings.nonDefaultFolderWarning());
        } else {
            awFolderSel.setInfoErrorText(null);
        }

        flushJobExecution();
    }

    /**
     * @param event
     */
    @UiHandler("awFolderSel")
    void onInvalid(InvalidEvent event) {
        updateHeader(name.getCurrentValue());
    }

    /**
     * @param event
     */
    @UiHandler("name")
    void onNameChange(ValueChangeEvent<String> event) {
        updateHeader(name.getCurrentValue());
    }

    private void updateHeader(String appName) {
        SafeHtmlBuilder headerLabel = new SafeHtmlBuilder();
        if (hasErrors()) {
            headerLabel.appendHtmlConstant(appearance.getErrorIconImg().getString());
        }
        headerLabel.appendEscaped(appWidgetStrings.launchAnalysisDetailsHeadingPrefix() + appName);
        contentPanel.setHeadingHtml(headerLabel.toSafeHtml());
    }
}
