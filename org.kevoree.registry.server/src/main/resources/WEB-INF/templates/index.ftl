<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <meta name="description" content=""/>
    <meta name="author" content=""/>
    <link rel="icon" href="/static/favicon.ico"/>

    <title>Kevoree Registry ${version}</title>

    <link href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap.min.css" rel="stylesheet"/>
    <link href="/static/css/style.css" rel="stylesheet"/>
</head>

<body>
    <nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
        <div class="container-fluid">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a id="navbar-logo" class="navbar-brand" href="#">
                    <img src="/static/images/logo.png" alt="Kevoree logo"/>
                </a>
            </div>
            <div id="navbar" class="navbar-collapse collapse">
                <ul class="nav navbar-nav navbar-right">
                    <li><a href="/v${version}">${version}</a></li>
                </ul>
            </div>
        </div>
    </nav>

    <div id="content" class="container-fluid">
        <#list elements as elem>
            <div class="panel panel-default">
                <div class="panel-heading">${elem.name}</div>
                <div class="panel-body">
                    <ul class="list-group">
                        <#list elem.attributes as attr>
                            <li class="list-group-item">${attr}</a></li>
                        </#list>
                    </ul>
                </div>
            </div>
        </#list>

        <div class="panel panel-default">
            <div class="panel-heading">Children (${children?size - 1})</div>
            <div class="panel-body">
                <ul class="list-group">
                    <li class="list-group-item"><a href="${previousPath}">parent</a></li>
                    <#list children as child>
                        <li class="list-group-item"><a href="${child.link}">${child.key}</a></li>
                    </#list>
                </ul>
            </div>
        </div>
    </div>

    <!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="//code.jquery.com/jquery-2.1.1.min.js"></script>
    <script src="//maxcdn.bootstrapcdn.com/bootstrap/3.3.1/js/bootstrap.min.js"></script>
</body>
</html>
