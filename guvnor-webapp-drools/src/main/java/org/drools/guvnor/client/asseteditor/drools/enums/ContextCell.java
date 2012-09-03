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

import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import org.drools.guvnor.client.decisiontable.cells.AbstractPopupEditCell;

public class ContextCell extends AbstractPopupEditCell<EnumContext, EnumContext> {

    private final Label test = new Label("test");

    public ContextCell() {
        super(false);

        //TODO: Add context editor widget to vPanel using vPanel.add( contextEditor );
        vPanel.add(test);
    }

    @Override
    protected void commit() {
        //TODO: Save data to valueUpdater using valueUpdater.update(org.drools.guvnor.client.asseteditor.drools.enums.Context);
    }

    @Override
    protected void startEditing(Context context, final Element parent, EnumContext value) {
        //TODO: Fill the popup

        panel.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
            public void setPosition(int offsetWidth,
                                    int offsetHeight) {
                panel.setPopupPosition(
                        parent.getAbsoluteLeft() + offsetX,
                        parent.getAbsoluteTop() + offsetY);
            }
        });
    }

    @Override
    public void render(Context context, EnumContext value, SafeHtmlBuilder safeHtmlBuilder) {
        safeHtmlBuilder.append(value.getHtml());
    }
}
