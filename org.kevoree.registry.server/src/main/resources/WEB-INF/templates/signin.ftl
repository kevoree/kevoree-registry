<div class="container" data-ng-controller="SignIn">
    <div class="col-md-offset-1 col-md-5">
        <div class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title">Using OpenIDs</h3>
            </div>
            <div class="panel-body">
                <a href="/!/auth/gconnect" class="align-middle">
                    <img id="googleSignIn" src="/!/static/images/google_signin_base.png"
                         onmousedown="document.getElementById('googleSignIn').src='/!/static/images/google_signin_press.png'"
                         onmouseup="document.getElementById('googleSignIn').src='/!/static/images/google_signin_base.png'"
                         onmouseover="document.getElementById('googleSignIn').src='/!/static/images/google_signin_hover.png'"
                         onmouseout="document.getElementById('googleSignIn').src='/!/static/images/google_signin_base.png'"
                         alt="Sign in with Google"/>
                </a>
            </div>
        </div>
    </div>

    <div class="col-md-5">
        <div class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title">Or using your email</h3>
            </div>
            <ul class="list-group" data-ng-show="error">
                <li class="list-group-item list-group-item-danger">
                    <h4 class="list-group-item-heading">Invalid inputs</h4>
                    <p class="list-group-item-text">{{error}}</p>
                </li>
            </ul>
            <div class="panel-body">
                <form name="form" class="form-horizontal col-md-8 col-md-push-2">
                    <div class="form-group" data-ng-class="{'has-error': form.name.$invalid && form.name.$dirty}">
                        <label class="control-label" for="name">Username</label>
                        <input class="form-control" type="text" id="name" name="name" data-ng-model="user.name" placeholder="Username" data-ng-minlength="1" data-ng-keyup="$event.keyCode == 13 && !form.$invalid" required>
                        <span class="help-block" data-ng-show="form.name.$invalid && form.name.$dirty">Required</span>
                    </div>
                    <div class="form-group" data-ng-class="{'has-error': form.email.$invalid && form.email.$dirty}">
                        <label class="control-label" for="email">Email</label>
                        <input class="form-control" type="email" id="email" name="email" data-ng-model="user.email" placeholder="Email" data-ng-keyup="$event.keyCode == 13 && !form.$invalid && validate(user)" required>
                        <span class="help-block" data-ng-show="form.email.$invalid && form.email.$dirty">Malformed email</span>
                    </div>
                    <div class="form-group" data-ng-class="{'has-error': form.password.$invalid && form.password.$dirty}">
                        <label class="control-label" for="password">Password</label>
                        <input class="form-control" type="password" id="password" name="password" data-ng-model="user.password" data-ng-minlength="8" data-match="user.password1" data-ng-keyup="$event.keyCode == 13 && !form.$invalid" placeholder="Password" required>
                        <span class="help-block" data-ng-show="form.password.$invalid.minLength">Too short (minimum = 8)</span>
                        <span class="help-block" data-ng-show="form.password.$error.match">Must be equal to the other password</span>
                    </div>
                    <div class="form-group" data-ng-class="{'has-error': form.password1.$invalid && form.password1.$dirty}">
                        <label class="control-label" for="password1">Retype password</label>
                        <input class="form-control" type="password" id="password1" name="password1" data-ng-model="user.password1" data-ng-minlength="8" placeholder="Retype password" data-match="user.password" data-ng-keyup="$event.keyCode == 13 && !form.$invalid" required>
                        <span class="help-block" data-ng-show="form.password1.$invalid.minLength">Too short (minimum = 8)</span>
                        <span class="help-block" data-ng-show="form.password1.$error.match">Must be equal to the other password</span>
                    </div>
                    <div class="form-group">
                        <button type="submit" class="btn btn-primary align-middle" data-ng-click="validate(user)" data-ng-disabled="form.$invalid">Sign in</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>