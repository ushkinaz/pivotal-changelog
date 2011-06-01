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

package com.googlecode.pivotalchangelog;

import com.googlecode.commandme.CLIParser;
import com.googlecode.commandme.annotations.Action;
import com.googlecode.commandme.annotations.Option;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class ReleasePlanGenerator {
    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(ReleasePlanGenerator.class);

    private NodeModel model;

    private int    projectId;
    private String token;
    private String filter;
    private String changelog = "changelog.xml";
    private String outputDir = "./";

    public ReleasePlanGenerator() throws UnsupportedEncodingException {
        filter = URLEncoder.encode("type:Release", "UTF-8");
    }

    public static void main(String[] args) throws IOException, SAXException, TemplateException, ParserConfigurationException {
        CLIParser.createModule(ReleasePlanGenerator.class).execute(args);
    }


    @Action
    public void process() throws TemplateException, IOException {
        LOGGER.debug(">> process");

        Configuration cfg = new Configuration();
        File templatesDirectory = new File(".");
        cfg.setDirectoryForTemplateLoading(templatesDirectory);
        cfg.setObjectWrapper(new DefaultObjectWrapper());


        for (File entry : templatesDirectory.listFiles(new TemplatesFilenameFilter())) {
            Template template1 = cfg.getTemplate(entry.getName());
            Map<String, NodeModel> root = new HashMap<String, NodeModel>();
            root.put("doc", model);

            String outputFileName = entry.getName().replaceFirst(".ftl$", "");
            Writer out = new OutputStreamWriter(new FileOutputStream(outputDir + outputFileName));
            template1.process(root, out);

            out.flush();
        }

        LOGGER.debug("<< process");
    }

    @Action
    public void fetch() throws IOException, SAXException, ParserConfigurationException {
        LOGGER.debug(">> fetch");
        HttpClient client = new DefaultHttpClient();
        HttpHost host = new HttpHost("www.pivotaltracker.com");
        HttpRequest request = new HttpGet("http://www.pivotaltracker.com/services/v3/projects/" + projectId + "/stories?filter=" + filter);
        request.addHeader("X-TrackerToken", token);
        HttpResponse response = client.execute(host, request);
        HttpEntity responseEntity = response.getEntity();
        model = NodeModel.parse(new InputSource(responseEntity.getContent()));
        LOGGER.debug("<< fetch");
    }

    @Action
    public void load() throws IOException, SAXException, ParserConfigurationException {
        LOGGER.debug(">> load");
        model = NodeModel.parse(new File(changelog));
        LOGGER.debug("<< load");
    }

    @Option(longName = "id", shortName = "i")
    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    @Option
    public void setToken(String token) {
        this.token = token;
    }

    @Option
    public void setChangelog(String changelog) {
        this.changelog = changelog;
    }

    @Option
    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    private class TemplatesFilenameFilter implements FilenameFilter {
        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(".ftl");
        }
    }
}
