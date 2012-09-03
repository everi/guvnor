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

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.client.ui.*;
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


    private VerticalPanel panel = new VerticalPanel();

    private CellTable cellTable = new CellTable<EnumRow>();


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

        data = (RuleContentText) a.getContent();

        if (data.content == null) {
            data.content = "";
        }

        cellTable.setWidth("100%");

        List<String> list = new ArrayList<String>();
        list.addAll(Arrays.asList(sce.getFactTypes()));

        for (String line : data.content.split("\n")) {
            EnumRow enumRow = new EnumRow(line);
            if (!list.contains(enumRow.getFactName())) {
                list.add(enumRow.getFactName());
            }
            dataProvider.getList().add(enumRow);
        }

        cellTable.addColumn(createAlertColumn());
        cellTable.addColumn(createDeleteButtonColumn());
        cellTable.addColumn(createFactNameColumn(list), "Fact");
        cellTable.addColumn(createFieldNameColumn(), "Field");
        cellTable.addColumn(createDependentColumn());
        cellTable.addColumn(createContextColumn(), "Context");

        // Connect the table to the data provider.
        dataProvider.addDataDisplay(cellTable);


        panel.add(cellTable);
        panel.add(createAddButton());
        initWidget(panel);

    }


    private Column<EnumRow, String> createAlertColumn() {
        final List<String> list = new ArrayList<String>();
        list.addAll(Arrays.asList(sce.getFactTypes()));

        Column<EnumRow, String> alertColumn = new Column<EnumRow, String>(new TextCell()) {


            @Override
            public String getValue(EnumRow enumRow) {

                if (!list.contains(enumRow.getFactName())) {
                    return "Fact type not found!";
                }

                return "";
            }
        };

        return alertColumn;
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

            public void update(int index, final EnumRow object, String value) {

                final DialogBox deleteDialogBox = new DialogBox();
                deleteDialogBox.setGlassEnabled(true);
                deleteDialogBox.setAnimationEnabled(true);
                deleteDialogBox.setText("Are you sure you want to delete this row?");
                deleteDialogBox.center();
                deleteDialogBox.show();

                Button deleteButton = new Button("Delete", new ClickHandler() {
                    public void onClick(ClickEvent clickEvent) {
                        deleteDialogBox.hide();
                        dataProvider.getList().remove(object);
                    }
                });

                Button cancelButton = new Button("Cancel", new ClickHandler() {
                    public void onClick(ClickEvent clickEvent) {
                        deleteDialogBox.hide();

                    }
                });
                HorizontalPanel buttonPanel = new HorizontalPanel();
                buttonPanel.add(cancelButton);
                buttonPanel.add(deleteButton);
                deleteDialogBox.add(buttonPanel);


            }
        });
        return deleteButtonColumn;
    }

    private Column<EnumRow, EnumContext> createContextColumn() {

        Column<EnumRow, EnumContext> contextColumn = new Column<EnumRow, EnumContext>(new ContextCell()) {

            @Override
            public EnumContext getValue(EnumRow enumRow) {

                return enumRow.getContext();
            }
        };

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

        ColumnSortEvent.ListHandler<EnumRow> columnSortHandler = new ColumnSortEvent.ListHandler<EnumRow>(dataProvider.getList());
        columnSortHandler.setComparator(factNameColumn,
                new Comparator<EnumRow>() {
                    public int compare(EnumRow e1, EnumRow e2) {

                        return e1.compareTo(e2);
                    }
                });
        cellTable.addColumnSortHandler(columnSortHandler);
        cellTable.getColumnSortList().push(factNameColumn);
        return factNameColumn;
    }

    private Column<EnumRow, String> createDependentColumn() {

        Column<EnumRow, String> dependentColumn = new Column<EnumRow, String>(new ButtonCell()) {
            @Override
            public String getValue(EnumRow enumRow1) {
                return enumRow1.getDependentFieldName();
            }
        };
        dependentColumn.setFieldUpdater(new FieldUpdater<EnumRow, String>() {

            public void update(int index, final EnumRow object, String value) {


                final DialogBox dialogBox = new DialogBox();
                dialogBox.setGlassEnabled(true);
                dialogBox.setAnimationEnabled(true);

                dialogBox.center();

                dialogBox.show();


                final TextBox textBox = new TextBox();
                final ListBox dropBox = new ListBox();
                String[] fieldNames = sce.getFieldCompletions(object.getFactName());
                for (int i = 0; i < fieldNames.length; i++) {
                    dropBox.addItem(fieldNames[i]);
                }


                if (object.getDependentFieldName().equals("")) {
                    textBox.setText("");
                } else {
                    String[] items = object.getDependentFieldName().split("=");

                    for (int i = 0; i < fieldNames.length; i++) {
                        if (fieldNames[i].equals(items[0])) {
                            dropBox.setSelectedIndex(i);
                        }
                    }
                    textBox.setText(items[1]);
                }

                Button button = new Button("Close", new ClickHandler() {
                    public void onClick(ClickEvent clickEvent) {
                        dialogBox.hide();
                        if (textBox.getText().equals("")) {
                            object.setDependentFieldName("");
                        } else {
                            object.setDependentFieldName(dropBox.getItemText(dropBox.getSelectedIndex()) + "=" + textBox.getText());
                        }
                        dataProvider.refresh();
                    }
                });


                VerticalPanel verticalPanel = new VerticalPanel();
                verticalPanel.add(dropBox);
                verticalPanel.add(textBox);
                verticalPanel.add(button);

                dialogBox.add(verticalPanel);


            }

        });
        return dependentColumn;
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