package org.iplantc.core.uiapps.widgets.client.view.editors.properties.lists;

import java.util.List;

import org.iplantc.core.uiapps.widgets.client.models.AppTemplateAutoBeanFactory;
import org.iplantc.core.uiapps.widgets.client.models.Argument;
import org.iplantc.core.uiapps.widgets.client.models.selection.SelectionItem;
import org.iplantc.core.uiapps.widgets.client.models.selection.SelectionItemProperties;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.ValueAwareEditor;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.data.client.editor.ListStoreEditor;
import com.sencha.gxt.data.shared.Converter;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.NumberField;
import com.sencha.gxt.widget.core.client.form.NumberPropertyEditor;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.editing.GridEditing;
import com.sencha.gxt.widget.core.client.grid.editing.GridRowEditing;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;

/**
 * 
 * TODO JDS Flushing of this as well as validator editor needs to be tested as well.
 * 
 * @author jstroot
 * 
 */
public class SelectionItemPropertyEditor extends Composite implements ValueAwareEditor<Argument> {

    private static SelectionListEditorUiBinder BINDER = GWT.create(SelectionListEditorUiBinder.class);
    interface SelectionListEditorUiBinder extends UiBinder<Widget, SelectionItemPropertyEditor> {}

    @UiField
    ListStore<SelectionItem> selectionArgStore;

    @UiField
    SimpleContainer con;

    @Ignore
    @UiField
    TextButton add;

    @Ignore
    @UiField
    TextButton delete;

    @UiField
    Grid<SelectionItem> grid;

    // The Editor for Argument.getSelectionItems()
    ListStoreEditor<SelectionItem> selectionItems;

    private ColumnConfig<SelectionItem, String> displayCol;

    private ColumnConfig<SelectionItem, String> nameCol;

    private ColumnConfig<SelectionItem, String> valueCol;

    private final GridEditing<SelectionItem> editing;

    public SelectionItemPropertyEditor() {
        initWidget(BINDER.createAndBindUi(this));
        grid.setHeight(100);

        editing = new GridRowEditing<SelectionItem>(grid);
        editing.addEditor(displayCol, new TextField());
        editing.addEditor(nameCol, new TextField());

        // Add selection handler to grid to control enabled state of "delete" button
        grid.getSelectionModel().addSelectionChangedHandler(new SelectionChangedHandler<SelectionItem>() {
            @Override
            public void onSelectionChanged(SelectionChangedEvent<SelectionItem> event) {
                if ((event.getSelection() == null) || event.getSelection().isEmpty()) {
                    delete.setEnabled(false);
                } else if ((event.getSelection() != null) && (event.getSelection().size() >= 1)) {
                    delete.setEnabled(true);
                }
            }
        });
        selectionItems = new ListStoreEditor<SelectionItem>(selectionArgStore);
    }

    @UiFactory
    ListStore<SelectionItem> createListStore() {
        return new ListStore<SelectionItem>(new ModelKeyProvider<SelectionItem>() {
            @Override
            public String getKey(SelectionItem item) {
                return item.getId();
            }
        });
    }

    @UiFactory
    ColumnModel<SelectionItem> createColumnModel() {
        List<ColumnConfig<SelectionItem, ?>> list = Lists.newArrayList();
        SelectionItemProperties props = GWT.create(SelectionItemProperties.class);
        displayCol = new ColumnConfig<SelectionItem, String>(props.display(), 90, "Display");
        nameCol = new ColumnConfig<SelectionItem, String>(props.name(), 50, "Argument");
        valueCol = new ColumnConfig<SelectionItem, String>(props.value(), 50, "Value");

        list.add(displayCol);
        list.add(nameCol);
        list.add(valueCol);
        return new ColumnModel<SelectionItem>(list);
    }

    @UiHandler("add")
    void onAddButtonClicked(SelectEvent event) {
        AppTemplateAutoBeanFactory factory = GWT.create(AppTemplateAutoBeanFactory.class);
        SelectionItem sa = factory.selectionItem().as();

        // JDS Set up a default id to satisfy ListStore's ModelKeyProvider
        sa.setId("TEMP_ID_" + Math.random() + "-" + selectionArgStore.size() + 100);
        sa.setDefault(false);
        sa.setDescription("Default Description");
        sa.setDisplay("Default Display");
        selectionArgStore.add(sa);
    }

    @UiHandler("delete")
    void onDeleteButtonClicked(SelectEvent event) {
        List<SelectionItem> selection = grid.getSelectionModel().getSelection();
        if (selection == null) {
            return;
        }
        for (SelectionItem sa : selection) {
            selectionArgStore.remove(sa);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.editor.client.ValueAwareEditor#setValue(java.lang.Object)
     * 
     * -- Determine if this control should be shown
     */
    @Override
    public void setValue(Argument value) {

        // May be able to set the proper editor by adding it at this time. The column model will use the
        // value as a String, but I can adjust the editing to be string, integer, double.
        switch (value.getType()) {
            case Selection:
            case TextSelection:
                editing.addEditor(valueCol, new TextField());
                break;

            case DoubleSelection:
                editing.addEditor(valueCol, new StringToDoubleConverter(), new NumberField<Double>(new NumberPropertyEditor.DoublePropertyEditor()));
                break;

            case IntegerSelection:
                editing.addEditor(valueCol, new StringToIntegerConverter(), new NumberField<Integer>(new NumberPropertyEditor.IntegerPropertyEditor()));
                break;

            default:
                // The current argument is not a valid type for this control.
                // So, disable and hide ourself so the user doesn't see it.
                con.setEnabled(false);
                con.setVisible(false);
                break;
        }
    }

    @Override
    public void flush() {/* Do Nothing */}

    @Override
    public void setDelegate(EditorDelegate<Argument> delegate) {/* Do Nothing */}

    @Override
    public void onPropertyChange(String... paths) {/* Do Nothing */}

    private final class StringToIntegerConverter implements Converter<String, Integer> {
        @Override
        public String convertFieldValue(Integer object) {
            if (object == null) {
                return null;
            }
            return object.toString();
        }
    
        @Override
        public Integer convertModelValue(String object) {
            if (object == null) {
                return null;
            }
            return Integer.parseInt(object);
        }
    }

    private final class StringToDoubleConverter implements Converter<String, Double> {
        @Override
        public String convertFieldValue(Double object) {
            if (object == null) {
                return null;
            }
            return object.toString();
        }
    
        @Override
        public Double convertModelValue(String object) {
            if (object == null) {
                return null;
            }
            return Double.parseDouble(object);
        }
    }

}