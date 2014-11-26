<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <meta name="description" content=""/>
    <meta name="author" content=""/>
    <link rel="icon" href="/!/static/favicon.ico"/>

    <title>Kevoree Registry ${version}</title>

    <link href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap.min.css" rel="stylesheet"/>
    <link href="/!/static/css/style.css" rel="stylesheet"/>
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
            <a id="navbar-logo" class="navbar-brand" href="/">
                <img src="/!/static/images/logo.png" alt="Kevoree logo"/>
            </a>
        </div>
        <div id="navbar" class="navbar-collapse collapse">
            <ul class="nav navbar-nav">
                <li class="active"><a href="/">Registry</a></li>
            </ul>
            <form class="navbar-form navbar-left" role="search" action="/!/search" method="get">
                <div class="form-group">
                    <input type="text" class="form-control" name="q" placeholder="Search model">
                </div>
                <button type="submit" class="btn btn-default"><span class="glyphicon glyphicon-search" aria-hidden="true"></span><span class="sr-only">Search</span></button>
            </form>
            <ul class="nav navbar-nav navbar-right">
            <#if connected>
                <li class="dropdown">
                    <a href="/!/user" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">${user.getName()} <span class="caret"></span></a>
                    <ul class="dropdown-menu" role="menu">
                        <li><a href="/!/user">My profile</a></li>
                        <li class="divider"></li>
                        <li><a href="/!/auth/logout">Log out</a></li>
                    </ul>
                </li>
                <li>
                    <a id="profile-picture-link" class="navbar-brand" href="/!/user">
                        <img id="profile-picture" alt="Profile picture" src="${gravatar}">
                    </a>
                </li>
            <#else>
                <li><a href="/!/auth/signin">Sign in</a></li>
            </#if>
            </ul>
        </div>
    </div>
</nav>

<div id="content">${content}</div>

<!-- Bootstrap core JavaScript
================================================== -->
<!-- Placed at the end of the document so the pages load faster -->
<script src="//code.jquery.com/jquery-2.1.1.min.js"></script>
<script src="//maxcdn.bootstrapcdn.com/bootstrap/3.3.1/js/bootstrap.min.js"></script>
</body>
</html>
