/*
 * Copyright 2011 JBoss Inc
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

package org.jboss.bpm.console.client.navigation.reporting;

import com.google.gwt.core.client.GWT;
import org.drools.guvnor.client.common.StackItemHeaderViewImpl;
import org.drools.guvnor.client.messages.ConstantsCore;

public class ReportingHeaderViewImpl extends StackItemHeaderViewImpl implements ReportingHeaderView {

    private static ConstantsCore constants = GWT.create(ConstantsCore.class);

    public ReportingHeaderViewImpl() {
        setText(constants.Reporting());
    }
}
