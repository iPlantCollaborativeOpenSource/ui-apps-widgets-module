package org.iplantc.core.uiapps.widgets.client.view.fields.treeSelector;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.core.jsonutil.JsonUtil;
import org.iplantc.core.metadata.client.validation.ListRuleArgument;
import org.iplantc.core.metadata.client.validation.ListRuleArgumentFactory;
import org.iplantc.core.metadata.client.validation.ListRuleArgumentGroup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.event.BeforeCheckChangeEvent;
import com.sencha.gxt.widget.core.client.event.BeforeCheckChangeEvent.BeforeCheckChangeHandler;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sencha.gxt.widget.core.client.tree.TreeView;

/**
 * A Checkable Tree for displaying ListRuleArgument in a wizard.
 * 
 * @author psarando
 * 
 */
public class ListRuleArgumentTree extends Tree<ListRuleArgument, String> {
    private final ListRuleArgumentFactory factory = GWT.create(ListRuleArgumentFactory.class);
    private ListRuleArgumentGroup root;
    private Command updateCmd;
    private boolean forceSingleSelection = false;

    public ListRuleArgumentTree(TreeStore<ListRuleArgument> store,
            ValueProvider<ListRuleArgument, String> valueProvider) {
        super(store, valueProvider);

        setBorders(true);
        setAutoLoad(true);

        setCheckable(true);
        setCheckStyle(CheckCascade.TRI);

        addBeforeCheckChangeHandler(new BeforeCheckChangeHandler<ListRuleArgument>() {
            @Override
            public void onBeforeCheckChange(BeforeCheckChangeEvent<ListRuleArgument> event) {
                if (forceSingleSelection) {
                    if (event.getChecked() == CheckState.UNCHECKED) {
                        boolean isGroup = event.getItem() instanceof ListRuleArgumentGroup;
                        boolean cascadeToChildren = getCheckStyle() == CheckCascade.TRI
                                || getCheckStyle() == CheckCascade.CHILDREN;

                        if (isGroup && cascadeToChildren) {
                            // Do not allow groups to be checked if SingleSelection is enabled and
                            // selections cascade to children.
                            event.setCancelled(true);
                            return;
                        }

                        List<ListRuleArgument> checked = getCheckedSelection();

                        if (checked != null && !checked.isEmpty()) {
                            // uncheck all other selections first.
                            setCheckedSelection(null);
                        }
                    }
                }
            }
        });
    }

    @Override
    protected SafeHtml renderChild(ListRuleArgument parent, ListRuleArgument child, int depth,
            TreeView.TreeViewRenderMode renderMode) {
        SafeHtml html = super.renderChild(parent, child, depth, renderMode);

        String tooltip = child.getDescription();
        if (tooltip == null || tooltip.isEmpty()) {
            // This node has no description.
            return html;
        }

        // Apply the description as a tool-tip to this node.
        SafeHtmlBuilder builder = new SafeHtmlBuilder();
        builder.appendHtmlConstant("<span title='" + tooltip + "'>"); //$NON-NLS-1$ //$NON-NLS-2$
        builder.append(html);
        builder.appendHtmlConstant("</span>"); //$NON-NLS-1$

        return builder.toSafeHtml();
    }

    @Override
    protected void onCheckCascade(ListRuleArgument model, CheckState checked) {
        // temporarily enable group selections during cascade events, if SingleSelection is enabled.
        boolean restoreForceSingleSelection = forceSingleSelection;
        forceSingleSelection = false;

        super.onCheckCascade(model, checked);

        forceSingleSelection = restoreForceSingleSelection;
    }

    @Override
    protected void onCheckClick(Event event, TreeNode<ListRuleArgument> node) {
        super.onCheckClick(event, node);

        // Keep track of which node the user clicked on in the isDefault field.
        // This helps with restoring checked state if the tree gets filtered, and will allow any checked
        // args to be submitted to the job, even when filtered out.
        ListRuleArgument ruleArg = node.getModel();
        ruleArg.setDefault(getChecked(ruleArg) != CheckState.UNCHECKED);

        callCheckChangedUpdateCommand();
    }

    /**
     * Resets the tree's contents with the ListRuleArguments in the given root JSON string.
     * 
     * @param root A JSON string of a ListRuleArgumentGroup.
     */
    public void setItems(String json) {
        setItems(AutoBeanCodex.decode(factory, ListRuleArgumentGroup.class, json).as());
    }

    /**
     * Resets the tree's contents with the ListRuleArguments in the given root.
     * 
     * @param root A ListRuleArgumentGroup containing the items to populate in this tree.
     */
    public void setItems(ListRuleArgumentGroup root) {
        store.clear();
        this.root = root;

        if (root == null) {
            return;
        }

        forceSingleSelection = root.isSingleSelect();
        setCheckCascade(root);

        List<ListRuleArgument> defaultSelection = new ArrayList<ListRuleArgument>();

        if (root.getGroups() != null) {
            for (ListRuleArgumentGroup group : root.getGroups()) {
                defaultSelection.addAll(addGroupToStore(null, group));
            }
        }

        if (root.getArguments() != null) {
            for (ListRuleArgument ruleArg : root.getArguments()) {
                store.add(ruleArg);

                if (ruleArg.isDefault()) {
                    defaultSelection.add(ruleArg);
                }
            }
        }

        if (!defaultSelection.isEmpty()) {
            setCheckedSelection(defaultSelection);
        }
    }

    private void setCheckCascade(ListRuleArgumentGroup root) {
        CheckCascade cascade = null;

        if (root != null && root.getSelectionCascade() != null) {
            cascade = root.getSelectionCascade().getTreeCheckCascade();
        }

        if (cascade != null) {
            setCheckStyle(cascade);
        }
    }

    /**
     * Adds the group to the parent, or as a root if parent is null, then adds group's children to the
     * store. Returns a list of any items that should be selected by default.
     * 
     * @param parent The parent of group.
     * @param group The group to add to the store.
     * @return A list of any items that should be selected by default.
     */
    private List<ListRuleArgument> addGroupToStore(ListRuleArgumentGroup parent,
            ListRuleArgumentGroup group) {
        List<ListRuleArgument> defaultSelection = new ArrayList<ListRuleArgument>();

        if (group != null) {
            if (parent != null) {
                store.add(parent, group);
            } else {
                store.add(group);
            }

            setLeaf(group, false);
            store.update(group);

            if (group.getGroups() != null) {
                for (ListRuleArgumentGroup child : group.getGroups()) {
                    defaultSelection.addAll(addGroupToStore(group, child));
                }
            }

            if (group.getArguments() != null) {
                for (ListRuleArgument child : group.getArguments()) {
                    store.add(group, child);

                    if (child.isDefault()) {
                        defaultSelection.add(child);
                    }
                }
            }

            if (group.isDefault()) {
                defaultSelection.add(group);
            }
        }

        return defaultSelection;
    }

    /**
     * Returns items selected in the tree, even if they are currently filtered out by the view.
     * 
     * @return List of selected ListRuleArguments and groups.
     */
    public List<ListRuleArgument> getSelection() {
        List<ListRuleArgument> selected = new ArrayList<ListRuleArgument>();

        addSelectedFromGroup(selected, root);

        return selected;
    }

    private void addSelectedFromGroup(List<ListRuleArgument> selected, ListRuleArgumentGroup group) {
        if (group == null) {
            return;
        }

        if (group.getArguments() != null) {
            for (ListRuleArgument ruleArg : group.getArguments()) {
                if (ruleArg.isDefault()) {
                    selected.add(ruleArg);
                }
            }
        }

        if (group.getGroups() != null) {
            for (ListRuleArgumentGroup subgroup : group.getGroups()) {
                if (subgroup.isDefault()) {
                    selected.add(subgroup);
                }

                addSelectedFromGroup(selected, subgroup);
            }
        }
    }

    /**
     * Sets the tree's checked selection with the ListRuleArguments in the given JSON Array string.
     * 
     * @param value A string representation of a JSON Array of the values to select.
     */
    public void setSelection(String value) {
        if (value == null || value.isEmpty()) {
            return;
        }

        try {
            JSONArray selectedValues = JSONParser.parseStrict(value).isArray();

            if (selectedValues != null && selectedValues.size() > 0) {
                TreeStore<ListRuleArgument> treeStore = getStore();

                for (int i = 0; i < selectedValues.size(); i++) {
                    JSONObject jsonRule = JsonUtil.getObjectAt(selectedValues, i);

                    if (jsonRule != null) {
                        ListRuleArgument ruleArg = AutoBeanCodex.decode(factory, ListRuleArgument.class,
                                jsonRule.toString()).as();

                        // Ensure the actual model used in the store is the one sent in checked events.
                        setChecked(treeStore.findModelWithKey(ruleArg.getId()), CheckState.CHECKED);
                    }
                }

                callCheckChangedUpdateCommand();
            }
        } catch (Exception e) {
            // ignore JSON parse errors
            GWT.log(e.getMessage());
        }
    }

    /**
     * Sets the Command to execute after the tree's checked selection changes.
     * 
     * @param updateCmd The component value table update Command to execute after selection changes.
     */
    public void setCheckChangedUpdateCommand(Command updateCmd) {
        this.updateCmd = updateCmd;
    }

    private void callCheckChangedUpdateCommand() {
        if (updateCmd != null) {
            updateCmd.execute();
        }
    }
}