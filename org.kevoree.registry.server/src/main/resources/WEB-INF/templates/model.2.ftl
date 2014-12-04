<div class="container-fluid">
    <div class="panel panel-default" data-ng-show="model.length === 0">
        <div class="panel-heading">Error</div>
        <ul class="list-group">
            <li class="list-group-item list-group-item-warning list-group-item-ellipsis">
                Unable to find <strong>{{relativePath}}</strong>
            </li>
        </ul>
    </div>

    <div class="panel panel-default" data-ng-hide="model.length === 0" data-ng-repeat="elem in model">
        <div class="panel-heading">{{elem.name}}</div>
        <ul class="list-group">
            <li class="list-group-item list-group-item-ellipsis" data-ng-repeat="attr in elem.attributes">{{attr}}</li>
        </ul>
    </div>

    <div class="panel panel-default" data-ng-hide="model.length === 0">
        <div class="panel-heading">Children ({{children.length + 1}})</div>
        <ul class="list-group">
            <li class="list-group-item list-group-item-ellipsis">
                <a href="" data-ng-href="{{previousPath}}">parent</a>
            </li>
            <li class="list-group-item list-group-item-ellipsis" data-ng-repeat="child in children">
                <a href="" data-ng-href="{{child.link}}">{{child.key}}</a>
            </li>
        </ul>
    </div>
</div>