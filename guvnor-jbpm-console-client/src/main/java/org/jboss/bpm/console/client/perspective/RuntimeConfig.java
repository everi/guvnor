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

package org.jboss.bpm.console.client.perspective;

import com.google.gwt.core.client.GWT;
import org.jboss.bpm.console.client.Config;

public class RuntimeConfig
        implements Config {

    private final String consoleServerUrl;

    public RuntimeConfig() {
        // extract host
        String base = GWT.getHostPageBaseURL();
        String protocol = base.substring(0, base.indexOf("//") + 2);
        String noProtocol = base.substring(base.indexOf(protocol) + protocol.length(), base.length());
        String host = noProtocol.substring(0, noProtocol.indexOf("/"));

        // default url
        consoleServerUrl = protocol + host + "/gwt-console-server";
    }

    public String getConsoleServerUrl() {
        return consoleServerUrl;
    }

    public String getProfileName() {
        return "jBPM Console";
    }
}
