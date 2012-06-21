/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.client.asseteditor.drools.enums;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;

import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.client.ui.Button;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;
import org.drools.guvnor.client.asseteditor.EditorWidget;
import org.drools.guvnor.client.asseteditor.RuleViewer;
import org.drools.guvnor.client.asseteditor.SaveEventListener;
import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.moduleeditor.drools.SuggestionCompletionCache;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.RuleContentText;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;

import java.util.*;

/**
 * This is the default rule editor widget (just text editor based) - more to come later.
 */
public class EnumEditor extends DirtyableComposite implements EditorWidget, SaveEventListener {


    private VerticalPanel panel;

    private CellTable cellTable;
    /*private Column<EnumRow, String> column = new Column<EnumRow, String>(new EditTextCell()) {


        @Override
        public String getValue(EnumRow enumRow) {
            return enumRow.getText();
        }
    } ; */


    final private RuleContentText data;
    private ListDataProvider<EnumRow> dataProvider = new ListDataProvider<EnumRow>();
    private SuggestionCompletionEngine sce;


    public EnumEditor(Asset a,
                      RuleViewer v,
                      ClientFactory clientFactory,
                      EventBus eventBus) {

        this(a);

    }

    public EnumEditor(Asset a) {
        this(a,
                -1);
    }

    public EnumEditor(Asset a,
                      int visibleLines) {

        sce = SuggestionCompletionCache.getInstance().getEngineFromCache(a.getMetaData().getModuleName());
        sce.getFactTypes();
        //sce.getFieldCompletions();


        data = (RuleContentText) a.getContent();

        if (data.content == null) {
            data.content = "";
        }

        cellTable = new CellTable<EnumRow>();
        cellTable.setWidth("100%");

        List<String> list =  new ArrayList<String>();
        list.addAll(Arrays.asList(sce.getFactTypes()));

        panel = new VerticalPanel();


        String[] array = data.content.split("\n");
        for (String line : array) {
            EnumRow enumRow = new EnumRow(line);
            if (!list.contains(enumRow.getFactName())) {
                list.add(enumRow.getFactName());
            }
            dataProvider.getList().add(enumRow);
        }


        Column<EnumRow, String> deleteButtonColumn = createDeleteButtonColumn();

        Column<EnumRow, String> factNameColumn = createFactNameColumn(list);

        Column<EnumRow, String> fieldNameColumn = createFieldNameColumn();
        Column<EnumRow, String> contextColumn = createContextColumn();




        ColumnSortEvent.ListHandler<EnumRow> columnSortHandler = new ColumnSortEvent.ListHandler<EnumRow>(dataProvider.getList());
        columnSortHandler.setComparator(factNameColumn,
                new Comparator<EnumRow>() {
                    public int compare(EnumRow e1, EnumRow e2) {

                        return e1.compareTo(e2);
                    }
                });
        cellTable.addColumnSortHandler(columnSortHandler);
        cellTable.getColumnSortList().push(factNameColumn);

        cellTable.addColumn(deleteButtonColumn);
        cellTable.addColumn(factNameColumn, "Fact");
        cellTable.addColumn(fieldNameColumn, "Field");
        cellTable.addColumn(contextColumn, "Context");

        // Connect the table to the data provider.
        dataProvider.addDataDisplay(cellTable);


        Button addButton = createAddButton();


        panel.add(cellTable);
        panel.add(addButton);
        initWidget(panel);

    }

    private Button createAddButton() {
        return new Button("+", new ClickHandler() {
                public void onClick(ClickEvent clickEvent) {
                    EnumRow enumRow = new EnumRow("");
                    dataProvider.getList().add(enumRow);
                }
            });
    }

    private Column<EnumRow, String> createDeleteButtonColumn() {
        Column<EnumRow, String> deleteButtonColumn = new Column<EnumRow, String>(new DeleteButtonCell()) {
            @Override
            public String getValue(EnumRow enumRow1) {
                return "";
            }
        };
        deleteButtonColumn.setFieldUpdater(new FieldUpdater<EnumRow, String>() {

            public void update(int index, EnumRow object, String value) {
                dataProvider.getList().remove(object);
            }
        });
        return deleteButtonColumn;
    }

    private Column<EnumRow, String> createContextColumn() {
        Column<EnumRow, String> contextColumn = new Column<EnumRow, String>(new EditTextCell()) {


            @Override
            public String getValue(EnumRow enumRow) {
                return enumRow.getContext();
            }
        };
        contextColumn.setFieldUpdater(new FieldUpdater<EnumRow, String>() {

            public void update(int index, EnumRow object, String value) {

                object.setContext(value);
            }
        });
        return contextColumn;
    }

    private Column<EnumRow, String> createFieldNameColumn() {

        Column<EnumRow, String> fieldNameColumn = new Column<EnumRow, String>(new FieldSelectionCell(sce)) {


            @Override
            public String getValue(EnumRow enumRow) {
                return enumRow.getFieldName();
            }
        };
        fieldNameColumn.setFieldUpdater(new FieldUpdater<EnumRow, String>() {
            public void update(int index, EnumRow object, String value) {

                object.setFieldName(value);

            }
        });
        return fieldNameColumn;
    }

    private Column<EnumRow, String> createFactNameColumn(final List<String> list) {
        Collections.sort(list);
        list.add(0, "-----");
        Column<EnumRow, String> factNameColumn = new Column<EnumRow, String>(new SelectionCell(list)) {


            @Override
            public String getValue(EnumRow enumRow) {
                return enumRow.getFactName();
            }
        };
        factNameColumn.setSortable(true);

        factNameColumn.setFieldUpdater(new FieldUpdater<EnumRow, String>() {

            public void update(int index, EnumRow object, String value) {
                object.setFactName(value);
                dataProvider.refresh();
            }
        });
        return factNameColumn;
    }


    public void onSave() {
        data.content = "";


        for (EnumRow enumRow : dataProvider.getList()) {
            data.content += enumRow.getText() + "\n";

        }
    }

    public void onAfterSave() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

}