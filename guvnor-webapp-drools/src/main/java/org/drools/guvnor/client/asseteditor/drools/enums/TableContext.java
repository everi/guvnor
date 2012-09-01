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

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class TableContext implements Context {
         private String context = "";
    private String [] array;
     //private List<String> arrayList; //listaksi, lisätään popupista
    private SafeHtml safeHtml;
    public TableContext(String text) {


       context=text.trim();
        if(context.equals("")){
            //arrayList = Arrays.asList(array);
        } else{

            array = context.split(",");
            //arrayList = Arrays.asList(array);
            for(int i=0;i<array.length;i++){
                array[i]=array[i].substring(array[i].indexOf("'") + 1,array[i].lastIndexOf("'"));
            }

        }
   }
    public void setValue(String text){
                      // context = text;

    }
    public String [] getValue(){
           return array;
    }

    @Override
    public SafeHtml getHtml() {

        SafeHtmlBuilder builder = new SafeHtmlBuilder();
           builder.appendHtmlConstant("<table>");
       if(!context.equals("")){
        for (String item : array) {
            builder.appendHtmlConstant("<tr>");
            builder.appendHtmlConstant("<td>");
            builder.appendEscaped(item).appendHtmlConstant("<td/>");
            builder.appendHtmlConstant("</tr>");
        }
       }
        builder.appendHtmlConstant("</table>");
        safeHtml = builder.toSafeHtml();

        return safeHtml;
    }
}
