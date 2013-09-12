/**
 *
 */
package org.iplantc.core.uiapps.widgets.client.view.deployedComponents;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.iplantc.core.resources.client.messages.I18N;
import org.iplantc.core.uiapps.client.views.dialogs.NewToolRequestDialog;
import org.iplantc.core.uiapps.widgets.client.services.DCSearchRPCProxy;
import org.iplantc.core.uiapps.widgets.client.view.deployedComponents.cells.DCNameHyperlinkCell;
import org.iplantc.core.uicommons.client.models.deployedcomps.DeployedComponent;
import org.iplantc.core.uicommons.client.models.deployedcomps.DeployedComponentProperties;
import org.iplantc.core.uicommons.client.widgets.SearchField;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfigBean;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.container.AbstractHtmlLayoutContainer.HtmlData;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;

/**
 * A grid that displays list of available deployed components (bin/tools) in Condor
 *
 * @author sriram
 *
 */
public class DeployedComponentsListingViewImpl extends Composite implements
        DeployedComponentsListingView {

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @UiTemplate("DeployedComponentsListingView.ui.xml")
    interface MyUiBinder extends UiBinder<Widget, DeployedComponentsListingViewImpl> {
    }

    interface DCDetailsRenderer extends XTemplates {
        @XTemplate(source = "DCDetails.html")
        SafeHtml render();
    }

    @UiField(provided = true)
    ListStore<DeployedComponent> store;

    private final Widget widget;

    private DeployedComponentsListingView.Presenter presenter;

    @UiField
    SearchField<DeployedComponent> searchField;

    @UiField
    Grid<DeployedComponent> grid;

    @UiField
    VerticalLayoutContainer container;
    
    DCSearchRPCProxy searchProxy;
    
    PagingLoader<FilterPagingLoadConfig, PagingLoadResult<DeployedComponent>> loader;

    
    
    @UiFactory
    SearchField<DeployedComponent> createAppSearchField() {
        return new SearchField<DeployedComponent>(loader);
    }

    public DeployedComponentsListingViewImpl(ListStore<DeployedComponent> listStore,
            SelectionChangedHandler<DeployedComponent> handler) {
        this.store = listStore;
        searchProxy = new DCSearchRPCProxy();
        loader = buildLoader();
        widget = uiBinder.createAndBindUi(this);
        grid.setLoader(loader);
        grid.getSelectionModel().addSelectionChangedHandler(handler);
        loader.addLoadHandler(new LoadResultListStoreBinding<FilterPagingLoadConfig, DeployedComponent, PagingLoadResult<DeployedComponent>>(store));
        loader.load(new FilterPagingLoadConfigBean());
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

     @Override
    public void setPresenter(DeployedComponentsListingView.Presenter presenter) {
        this.presenter = presenter;

    }
    
    private PagingLoader<FilterPagingLoadConfig, PagingLoadResult<DeployedComponent>> buildLoader() {
        final PagingLoader<FilterPagingLoadConfig, PagingLoadResult<DeployedComponent>> loader = new PagingLoader<FilterPagingLoadConfig, PagingLoadResult<DeployedComponent>>(
                searchProxy);
        loader.useLoadConfig(new FilterPagingLoadConfigBean());
        return loader;
    }

    @UiFactory
    ColumnModel<DeployedComponent> createColumnModel() {
        DeployedComponentProperties properties = GWT.create(DeployedComponentProperties.class);
        IdentityValueProvider<DeployedComponent> provider = new IdentityValueProvider<DeployedComponent>("name");
        List<ColumnConfig<DeployedComponent, ?>> configs = new LinkedList<ColumnConfig<DeployedComponent, ?>>();

        ColumnConfig<DeployedComponent, DeployedComponent> name = new ColumnConfig<DeployedComponent, DeployedComponent>(provider, 100);
        name.setComparator(new Comparator<DeployedComponent>() {

            @Override
            public int compare(DeployedComponent o1, DeployedComponent o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        name.setSortable(true);
        name.setHeader(I18N.DISPLAY.name());
        configs.add(name);
        name.setCell(new DCNameHyperlinkCell(this));
        name.setMenuDisabled(true);

        ColumnConfig<DeployedComponent, String> version = new ColumnConfig<DeployedComponent, String>(properties.version(), 100);
        version.setHeader(I18N.DISPLAY.version());
        configs.add(version);
        version.setMenuDisabled(true);

        ColumnConfig<DeployedComponent, String> path = new ColumnConfig<DeployedComponent, String>(properties.location(), 100);
        path.setHeader(I18N.DISPLAY.path());
        configs.add(path);
        path.setMenuDisabled(true);
        return new ColumnModel<DeployedComponent>(configs);
    }

    @Override
    public void loadDC(List<DeployedComponent> list) {
        store.clear();
        store.addAll(list);
    }

    @Override
    public void showInfo(DeployedComponent dc) {
        DCDetailsRenderer templates = GWT.create(DCDetailsRenderer.class);
        HtmlLayoutContainer c = new HtmlLayoutContainer(templates.render());
        c.add(new Label(I18N.DISPLAY.attribution() + ": "), new HtmlData(".cell1"));
        c.add(new Label(dc.getAttribution()), new HtmlData(".cell3"));
        c.add(new Label(I18N.DISPLAY.description() + ": "), new HtmlData(".cell5"));
        c.add(new Label(dc.getDescription()), new HtmlData(".cell7"));
        Dialog d = buildDetailsDialog(dc.getName());
        d.add(c);
        d.show();
    }

    private Dialog buildDetailsDialog(String heading) {
        Dialog d = new Dialog();
        d.getButtonBar().clear();
        d.setModal(true);
        d.setSize("300px", "200px");
        d.setHeadingText(heading);
        return d;
    }

    @Override
    public void mask() {
        container.mask(I18N.DISPLAY.loadingMask());

    }

    @Override
    public void unmask() {
        container.unmask();

    }

//    @UiHandler({"searchBtn"})
//    void onSearchBtnClick(SelectEvent event) {
//        String currentValue = searchField.getCurrentValue();
//        if (currentValue == null || currentValue.isEmpty()) {
//            presenter.loadDeployedComponents();
//            return;
//        }
//        if (currentValue.length() >= 3) {
//            presenter.searchDC(currentValue);
//        } else {
//            searchField.markInvalid(I18N.DISPLAY.searchEmptyText());
//        }
//    }

    @UiHandler({"newToolBtn"})
    void onNewToolRequestBtnClick(SelectEvent event) {
        NewToolRequestDialog dialog = new NewToolRequestDialog();
        dialog.show();
    }

    @Override
    public DeployedComponent getSelectedDC() {
        return grid.getSelectionModel().getSelectedItem();
    }

}
