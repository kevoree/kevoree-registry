<!DOCTYPE html>
<html lang="en" data-ng-app="kevoreeRegistry">
<head>
    <title>Kevoree Registry</title>

    <meta charset="utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <meta name="description" content=""/>
    <meta name="author" content=""/>
    <link rel="icon" href="/!/static/favicon.ico"/>

    <link href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap.min.css" rel="stylesheet"/>
    <link href="/!/static/css/style.css" rel="stylesheet"/>

    <#-- TODO improve that in order to have only one file and some minification -->
    <script src="//ajax.googleapis.com/ajax/libs/angularjs/1.2.27/angular.min.js"></script>
    <script src="/!/static/js/libs/kevoree-library.min.js"></script>
    <script src="/!/static/js/modules/ui-bootstrap-0.12.0.min.js"></script>
    <script src="/!/static/js/app.js"></script>
    <script src="/!/static/js/factories/registry.js"></script>
    <script src="/!/static/js/factories/user.js"></script>
    <script src="/!/static/js/factories/namespace.js"></script>
    <script src="/!/static/js/factories/model.js"></script>
    <script src="/!/static/js/directives/match.js"></script>
    <script src="/!/static/js/directives/fqnCompliant.js"></script>
    <script src="/!/static/js/controllers/Main.js"></script>
    <script src="/!/static/js/controllers/SignIn.js"></script>
    <script src="/!/static/js/controllers/LogIn.js"></script>
    <script src="/!/static/js/controllers/Profile.js"></script>
    <script src="/!/static/js/controllers/Namespace.js"></script>
    <script src="/!/static/js/controllers/Model.js"></script>
</head>

<body data-ng-controller="Main">
    <nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
        <div class="container-fluid">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-ng-click="isCollapsed = !isCollapsed" aria-expanded="false" aria-controls="navbar">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a id="navbar-logo" class="navbar-brand" href="/">
                    <img src="/!/static/images/logo.png" alt="Kevoree logo" title="v{{version}}"/>
                </a>
            </div>
            <div id="navbar" class="navbar-collapse collapse" data-collapse="isCollapsed">
                <ul class="nav navbar-nav">
                    <li class="active"><a href="/">Registry</a></li>
                </ul>
                <form class="navbar-form navbar-left" role="search" action="/!/model/search" method="get">
                    <div class="form-group">
                        <input type="text" class="form-control" name="q" placeholder="Search model">
                    </div>
                    <button type="submit" class="btn btn-default"><span class="glyphicon glyphicon-search" aria-hidden="true"></span><span class="sr-only">Search</span></button>
                </form>
                <ul class="nav navbar-nav navbar-right">
                    <li class="dropdown" data-dropdown data-ng-show="user">
                        <a href class="dropdown-toggle" data-dropdown-toggle>{{user.name}} <span class="caret"></span></a>
                        <ul class="dropdown-menu">
                            <li><a href="/!/user">My profile</a></li>
                            <li class="divider"></li>
                            <li><a href="/!/auth/logout">Log out</a></li>
                        </ul>
                    </li>
                    <li data-ng-show="user">
                        <a id="profile-picture-link" class="navbar-brand" href="/!/user">
                            <img id="profile-picture" alt="Profile picture" data-ng-src="{{genGravatar(user.gravatarEmail)}}">
                        </a>
                    </li>
                    <li data-ng-show="!user"><a href="/!/auth/login">Log in</a></li>
                    <li data-ng-show="!user"><a href="/!/auth/signin">Sign in</a></li>
                </ul>
            </div>
        </div>
    </nav>

    <div id="content">${content}</div>
</body>
</html>
