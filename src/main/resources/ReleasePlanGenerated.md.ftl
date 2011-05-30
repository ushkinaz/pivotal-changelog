# Release Plan

This is automatically generated release plan.
The source is https://www.pivotaltracker.com/projects/102363/

Please note that version number **follows** stories to be released in that version.

<#list doc.stories.story as row>
    <#if row.story_type == 'release'>

## [${row.name}](${row.url})
        <#if row.description !=''>
### Summary:
```
${row.description}
 ```
        </#if>

        <#elseif row.story_type == 'feature'>
* [${row.id}](${row.url}) ${row.name}
    </#if>
</#list>
