package org.iplantc.core.uiapps.widgets.client.view;

import java.util.List;

import org.iplantc.core.uiapps.widgets.client.models.AppTemplateAutoBeanFactory;
import org.iplantc.core.uiapps.widgets.client.models.selection.SelectionItem;
import org.iplantc.core.uiapps.widgets.client.models.selection.SelectionItemGroup;
import org.iplantc.core.uiapps.widgets.client.view.fields.treeSelector.SelectionItemTree;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.ValueAwareEditor;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.data.client.editor.ListStoreEditor;
import com.sencha.gxt.data.shared.TreeStore;

/**
 * Binds a {@link TreeStore} of {@link SelectionItem}s to a {@link List} property in an edited model
 * object.
 * This class is modeled after {@link ListStoreEditor}.
 * <p>
 * If bound to a null value, no changes will be made when flushed.
 * </p>
 * 
 * @author jstroot
 * 
 */
public class SelectionItemTreeStoreEditor implements ValueAwareEditor<List<SelectionItem>> {
    private final TreeStore<SelectionItem> store;
    private List<SelectionItem> model;
    private final SelectionItemTree tree;
    private final AppTemplateAutoBeanFactory factory = GWT.create(AppTemplateAutoBeanFactory.class);

    public SelectionItemTreeStoreEditor(TreeStore<SelectionItem> store, SelectionItemTree tree) {
        this.store = store;
        this.tree = tree;
    }

    @Override
    public void flush() {
        store.commitChanges();

        if (model != null) {
            model.clear();
            model.addAll(store.getRootItems());
        }
    }

    public TreeStore<SelectionItem> getStore() {
        return store;
    }

    @Override
    public void setValue(List<SelectionItem> value) {
        if (value == null) {
            return;
        }
        store.clear();
        /*
         * JDS This list of SelectionItems is for TreeSelection, and therefore, should only consist of
         * one element which can be cast to a SelectionItemGroup. This item is used to set the selection
         * and selection cascade modes of the tree.
         */
        if (value.size() == 1) {
            /*
             * JDS Have to deserialize the root SelectionItem and reserialize it to a SelectionItemGroup
             * since it is illegal to "downcast" types (we can't directly cast from SelectionItem to
             * SelectionItemGroup
             */
            SelectionItemGroup rootSelectionItemGroup = AutoBeanCodex.decode(factory, SelectionItemGroup.class, AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(value.get(0)))).as();

            // JDS Populate TreeStore.
            tree.setItems(rootSelectionItemGroup);

            // JDS Get the "isSingleSelect" and "selectionCascade" items from the root SelectionItemGroup
            boolean isSingleSelect = rootSelectionItemGroup.isSingleSelect();
            if (isSingleSelect) {
                tree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            } else {
                tree.getSelectionModel().setSelectionMode(SelectionMode.MULTI);
            }
            // JDS Propagate tree check style.
            if (rootSelectionItemGroup.getSelectionCascade() != null) {
                tree.setCheckStyle(rootSelectionItemGroup.getSelectionCascade().getTreeCheckCascade());
            }
        } else {
            GWT.log(this.getClass().getName() + ".setValue(List<SelectionItem>) given list which is not equal to 1.");
        }
    }

    @Override
    public void onPropertyChange(String... paths) {/* Do Nothing */}

    @Override
    public void setDelegate(EditorDelegate<List<SelectionItem>> delegate) {
        // ignore for now, this could be used to pass errors into the view
    }

}