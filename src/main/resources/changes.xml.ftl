<?xml version="1.0" encoding="utf-8"?>
<document xmlns="http://maven.apache.org/changes/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/changes/1.0.0 http://maven.apache.org/plugins/maven-changes-plugin/xsd/changes-1.0.0.xsd">
    <body>
    <#list doc.stories.story as row>
        <#if row.story_type == 'release'>

        <release version="${row.name}" description="${row.description}">
        </release>
        </#if>
    </#list>
    </body>
</document>
