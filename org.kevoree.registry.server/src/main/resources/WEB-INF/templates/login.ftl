<div class="container" data-ng-controller="LogInCtrl">
    <div class="col-md-offset-3 col-md-6">
        <div class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title">Log in using your email</h3>
            </div>
            <ul class="list-group" ng-show="error">
                <li class="list-group-item list-group-item-danger">
                    <h4 class="list-group-item-heading">Invalid inputs</h4>
                    <p class="list-group-item-text">{{error}}</p>
                </li>
            </ul>
            <div class="panel-body">
                <form name="form" class="form-horizontal col-md-8 col-md-push-2">
                    <div class="form-group" data-ng-class="{'has-error': form.email.$invalid && form.email.$dirty}">
                        <label class="control-label" for="email">Email</label>
                        <input class="form-control" type="email" id="email" name="email" data-ng-model="user.email" placeholder="Email" data-ng-keyup="$event.keyCode == 13 && !form.$invalid" required>
                        <span class="help-block" data-ng-show="form.email.$invalid && form.email.$dirty">Malformed email</span>
                    </div>
                    <div class="form-group" data-ng-class="{'has-error': form.password.$invalid && form.password.$dirty}">
                        <label class="control-label" for="password">Password</label>
                        <input class="form-control" type="password" id="password" name="password" data-ng-model="user.password" data-ng-minlength="8" data-ng-keyup="$event.keyCode == 13 && !form.$invalid" placeholder="Password" required>
                    </div>
                    <div class="form-group">
                        <button type="submit" class="btn btn-primary align-middle" data-ng-click="validate(user)" data-ng-disabled="form.$invalid">Log in</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>