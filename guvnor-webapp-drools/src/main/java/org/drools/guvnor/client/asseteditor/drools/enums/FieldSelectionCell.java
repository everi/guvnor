/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.client.asseteditor.drools.enums;

import com.google.gwt.cell.client.AbstractInputCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import org.drools.guvnor.client.moduleeditor.drools.SuggestionCompletionCache;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class FieldSelectionCell extends AbstractInputCell<String, String> {

    private final SuggestionCompletionEngine sce;

    interface Template extends SafeHtmlTemplates {
        @Template("<option value=\"{0}\">{0}</option>")
        SafeHtml deselected(String option);

        @Template("<option value=\"{0}\" selected=\"selected\">{0}</option>")
        SafeHtml selected(String option);
    }

    private static Template template = GWT.create(Template.class);

    private HashMap<String, Integer> indexForOption = new HashMap<String, Integer>();


    public FieldSelectionCell(SuggestionCompletionEngine sce) {
        super("change");
        this.sce = sce;

    }


    private List<String> getFieldNames(String factName) {
        List<String> list = new ArrayList<String>();
        for (String fieldName : sce.getFieldCompletions(factName)) {
            list.add(fieldName);
        }
        return list;
    }

    @Override
    public void onBrowserEvent(Context context, Element parent, String value,
                               NativeEvent event, ValueUpdater<String> valueUpdater) {
        Object key = context.getKey();


        if (key instanceof EnumRow) {
            int index = 0;
            EnumRow enumRow = (EnumRow) key;


            List<String> fieldNames = getFieldNames(enumRow.getFactName());
            for (String option : fieldNames) {
                indexForOption.put(option, index++);
            }


            super.onBrowserEvent(context, parent, value, event, valueUpdater);
            String type = event.getType();
            if ("change".equals(type)) {
                SelectElement select = parent.getFirstChild().cast();
                String newValue = fieldNames.get(select.getSelectedIndex());
                setViewData(key, newValue);
                finishEditing(parent, newValue, key, valueUpdater);
                if (valueUpdater != null) {
                    valueUpdater.update(newValue);
                }
            }
        }
    }


    @Override
    public void render(Context context, String value, SafeHtmlBuilder sb) {
        Object key = context.getKey();
        String viewData = getViewData(key);
        if (viewData != null && viewData.equals(value)) {
            clearViewData(key);
            viewData = null;
        }
        if (key instanceof EnumRow) {
            int index = 0;
            EnumRow enumRow = (EnumRow) key;


            int selectedIndex = getSelectedIndex(viewData == null ? value : viewData);
            sb.appendHtmlConstant("<select tabindex=\"-1\">");

            List<String> fieldNames = getFieldNames(enumRow.getFactName());
            for (String option : fieldNames) {
                if (option.equals(enumRow.getFieldName())) {
                    sb.append(template.selected(option));
                } else {
                    sb.append(template.deselected(option));
                }
            }
            sb.appendHtmlConstant("</select>");
        }

    }

    private int getSelectedIndex(String value) {
        Integer index = indexForOption.get(value);
        if (index == null) {
            return -1;
        }
        return index.intValue();
    }
}
