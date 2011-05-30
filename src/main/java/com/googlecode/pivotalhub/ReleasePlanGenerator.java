/*
 * Copyright (c) 2010-2011, Dmitry Sidorenko. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.googlecode.pivotalhub;

import freemarker.ext.dom.NodeModel;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class ReleasePlanGenerator {
    public static void main(String[] args) throws IOException, SAXException, TemplateException, ParserConfigurationException {
        HttpClient client = new DefaultHttpClient();
        HttpHost host = new HttpHost("www.pivotaltracker.com");
        HttpRequest request = new HttpGet("http://www.pivotaltracker.com/services/v3/projects/102363/stories");
        request.addHeader("X-TrackerToken", "c548939923824022039c7217b63e9ee7");
        HttpResponse response = client.execute(host, request);
        HttpEntity responseEntity = response.getEntity();

        initFreemarker(new InputSource(responseEntity.getContent()));
    }

    private static void initFreemarker(InputSource inputSource) throws IOException, TemplateException, SAXException, ParserConfigurationException {
        Configuration cfg = new Configuration();
        cfg.setDirectoryForTemplateLoading(new File("."));
        cfg.setObjectWrapper(new DefaultObjectWrapper());

        Template temp = cfg.getTemplate("ReleasePlanGenerated.md.ftl");
        Writer out = new OutputStreamWriter(System.out);
        Map root = new HashMap();

//        root.put("doc", NodeModel.parse(inputSource));
        root.put("doc", NodeModel.parse(new File("resp.xml")));
        temp.process(root, out);

        out.flush();
    }
}
