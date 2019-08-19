[#-- Assigns: Get Content --]
[#assign extension = ctx.getAggregationState().getExtension()!]

[#-- Create XML --]
[#if extension = "xml"]
    [#include "/sitemap/pages/mainXml.ftl" ]
[#else]
    [#include "/sitemap/pages/mainConfiguration.ftl" ]
[/#if]
