package org.iplantc.core.client.widgets.appWizard.view.fields;

import java.util.List;
import java.util.Set;

import org.iplantc.core.client.widgets.I18N;
import org.iplantc.core.client.widgets.appWizard.models.TemplateProperty;
import org.iplantc.core.uidiskresource.client.models.autobeans.DiskResource;
import org.iplantc.core.uidiskresource.client.models.autobeans.DiskResourceModelKeyProvider;
import org.iplantc.core.uidiskresource.client.models.autobeans.DiskResourceProperties;
import org.iplantc.core.uidiskresource.client.util.DiskResourceUtil;
import org.iplantc.core.uidiskresource.client.views.dialogs.FileSelectDialog;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.autobean.shared.Splittable;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.InvalidEvent.InvalidHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.ValidEvent.ValidHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridView;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

/**
 * TODO JDS Implement drag and drop
 * 
 * 
 * @author jstroot
 * 
 */
public class AppWizardMultiFileSelector extends Composite implements TemplatePropertyField, LeafValueEditor<Splittable> {

    interface AppWizardMultiFileSelectorUiBinder extends UiBinder<Widget, AppWizardMultiFileSelector> {}

    private static AppWizardMultiFileSelectorUiBinder BINDER = GWT.create(AppWizardMultiFileSelectorUiBinder.class);

    @UiField
    ToolBar toolbar;

    @UiField
    TextButton addButton;

    @UiField
    TextButton deleteButton;

    @UiField
    Grid<DiskResource> grid;

    @UiField
    GridView<DiskResource> gridView;

    @UiField
    ListStore<DiskResource> listStore;

    @UiField
    ColumnModel<DiskResource> cm;

    public AppWizardMultiFileSelector() {
        initWidget(BINDER.createAndBindUi(this));

        grid.getSelectionModel().addSelectionChangedHandler(new SelectionChangedHandler<DiskResource>() {
            @Override
            public void onSelectionChanged(SelectionChangedEvent<DiskResource> event) {
                List<DiskResource> selection = event.getSelection();
                deleteButton.setEnabled((selection != null) && !selection.isEmpty());
            }
        });
    }

    @Override
    public void initialize(TemplateProperty property) {
        // TBI JDS
        throw new UnsupportedOperationException("Not Yet Implemented");
    }

    @UiFactory
    ColumnModel<DiskResource> createColumnModel() {
        List<ColumnConfig<DiskResource, ?>> list = Lists.newArrayList();
        DiskResourceProperties props = GWT.create(DiskResourceProperties.class);

        ColumnConfig<DiskResource, String> name = new ColumnConfig<DiskResource, String>(props.name(), 130, I18N.DISPLAY.name());
        list.add(name);
        return new ColumnModel<DiskResource>(list);
    }

    @UiFactory
    ListStore<DiskResource> createListStore() {
        return new ListStore<DiskResource>(new DiskResourceModelKeyProvider());
    }

    @UiHandler("addButton")
    void onAddButtonSelected(SelectEvent event) {
        // Open a multiselect file selector
        FileSelectDialog dlg = new FileSelectDialog();
        dlg.addHideHandler(new FileSelectDialogHideHandler(dlg, listStore));
        dlg.show();
    }

    @UiHandler("deleteButton")
    void onDeleteButtonSelected(SelectEvent event) {
        for (DiskResource dr : grid.getSelectionModel().getSelectedItems()) {
            listStore.remove(dr);
        }
    }

    @Override
    public void setValue(Splittable value) {
        if (!value.isIndexed())
            return;

        // TBI JDS Assume the incoming value is a JSON array of ..... ?

    }

    @Override
    public Splittable getValue() {
        // Convert list store items into indexed splittable
        Splittable split = DiskResourceUtil.createStringIdListSplittable(listStore.getAll());
        return split;
    }

    @Override
    public HandlerRegistration addInvalidHandler(InvalidHandler handler) {return null;}

    @Override
    public HandlerRegistration addValidHandler(ValidHandler handler) {return null;}

    private final class FileSelectDialogHideHandler implements HideHandler {
        private final FileSelectDialog dlg;
        private final ListStore<DiskResource> store;

        public FileSelectDialogHideHandler(final FileSelectDialog dlg, final ListStore<DiskResource> store) {
            this.dlg = dlg;
            this.store = store;
        }

        @Override
        public void onHide(HideEvent event) {
            Set<DiskResource> diskResources = dlg.getDiskResources();
            if ((diskResources == null) || diskResources.isEmpty())
                return;
            store.addAll(diskResources);
        }
    }

}
