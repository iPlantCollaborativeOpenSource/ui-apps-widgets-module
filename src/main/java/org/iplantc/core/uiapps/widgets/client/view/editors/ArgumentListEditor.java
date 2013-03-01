package org.iplantc.core.uiapps.widgets.client.view.editors;

import java.util.Iterator;

import org.iplantc.core.uiapps.widgets.client.models.Argument;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor.Ignore;
import com.google.gwt.editor.client.IsEditor;
import com.google.gwt.editor.client.adapters.EditorSource;
import com.google.gwt.editor.client.adapters.ListEditor;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.FormPanelHelper;

/**
 * This class implements <code>HasWidgets</code> in order to support manual validation of view via
 * {@link FormPanelHelper#isValid(HasWidgets)}
 * 
 * @author jstroot
 * 
 */
public class ArgumentListEditor extends Composite implements HasWidgets, IsEditor<ListEditor<Argument, ArgumentEditorAdapter>> {

    interface ArgumentListEditorUiBinder extends UiBinder<Widget, ArgumentListEditor> {}
    private static ArgumentListEditorUiBinder BINDER = GWT.create(ArgumentListEditorUiBinder.class);
    
    private class PropertyListEditorSource extends EditorSource<ArgumentEditorAdapter>{
        private static final int DEF_ARGUMENT_MARGIN = 10;
        private final VerticalLayoutContainer con;

        public PropertyListEditorSource(VerticalLayoutContainer con) {
            this.con = con;
        }

        @Override
        public ArgumentEditorAdapter create(int index) {
            ArgumentEditorAdapter subEditor = new ArgumentEditorAdapter();
            con.add(subEditor, new VerticalLayoutData(1, -1, new Margins(DEF_ARGUMENT_MARGIN)));
            return subEditor;
        }
        
        @Override
        public void dispose(ArgumentEditorAdapter subEditor){
            subEditor.removeFromParent();
        }
        
        @Override
        public void setIndex(ArgumentEditorAdapter editor, int index){
            con.insert(editor, index, new VerticalLayoutData(1, -1, new Margins(DEF_ARGUMENT_MARGIN)));
        }
    }

    @Ignore
    @UiField
    VerticalLayoutContainer argumentsContainer;

    private final ListEditor<Argument, ArgumentEditorAdapter> editor;

    public ArgumentListEditor() {
        initWidget(BINDER.createAndBindUi(this));
        editor = ListEditor.of(new PropertyListEditorSource(argumentsContainer));
    }

    @Override
    public ListEditor<Argument, ArgumentEditorAdapter> asEditor() {
        return editor;
    }

    @Override
    public void add(Widget w) {
        argumentsContainer.add(w);
    }

    @Override
    public void clear() {
        argumentsContainer.clear();
    }

    @Override
    public Iterator<Widget> iterator() {
        return argumentsContainer.iterator();
    }

    @Override
    public boolean remove(Widget w) {
        return argumentsContainer.remove(w);
    }

}