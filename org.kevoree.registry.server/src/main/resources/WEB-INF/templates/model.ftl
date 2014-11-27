<div class="container-fluid">
<#if isEmpty>
    <div class="panel panel-default">
        <div class="panel-heading">Error</div>
        <ul class="list-group">
            <li class="list-group-item list-group-item-warning">
                Unable to find <strong>${relativePath}</strong>
            </li>
        </ul>
    </div>
<#else>
    <#list elements as elem>
        <div class="panel panel-default">
            <div class="panel-heading">${elem.name}</div>
            <ul class="list-group">
                <#list elem.attributes as attr>
                    <li class="list-group-item">${attr}</a></li>
                </#list>
            </ul>
        </div>
    </#list>

    <div class="panel panel-default">
        <div class="panel-heading">Children (${children?size + 1})</div>
        <ul class="list-group">
            <li class="list-group-item"><a href="${previousPath}">parent</a></li>
            <#list children as child>
                <li class="list-group-item"><a href="${child.link}">${child.key}</a></li>
            </#list>
        </ul>
    </div>
</#if>
</div>