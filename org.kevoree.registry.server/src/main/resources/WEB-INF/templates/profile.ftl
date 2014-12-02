<div class="container" data-ng-controller="ProfileCtrl">
    <div class="col-md-offset-1 col-md-5">
        <div class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title">Edit your profile</h3>
            </div>
            <ul class="list-group">
                <li class="list-group-item">
                    <h4 class="list-group-item-heading">{{user.name}}</h4>
                    <p class="list-group-item-text">{{user.id}}</p>
                </li>
            </ul>
            <li class="list-group-item list-group-item-danger" data-ng-show="gravatarError">
                <h4 class="list-group-item-heading">Error</h4>
                <p class="list-group-item-text">{{gravatarError}}</p>
            </li>
            <li class="list-group-item list-group-item-success" data-ng-show="gravatarSuccess">
                <p class="list-group-item-text">{{gravatarSuccess}}</p>
            </li>
            <ul class="list-group">
                <li class="list-group-item">
                    <h4 class="list-group-item-heading">Gravatar settings</h4>
                    <div class="list-group-item-content">
                        <form name="gravatarForm" class="form-horizontal">
                            <div class="form-group" data-ng-class="{'has-error': gravatarForm.gravatar_email.$invalid && gravatarForm.gravatar_email.$dirty}">
                                <label class="control-label" for="gravatar_email"><a href="https://gravatar.com" target="_blank">Gravatar</a> email</label>
                                <input class="form-control" type="email" id="gravatar_email" name="gravatar_email" data-ng-model="user.gravatarEmail" data-ng-keyup="$event.keyCode == 13 && !gravatarForm.$invalid">
                                <span class="help-block" data-ng-show="gravatarForm.gravatar_email.$invalid && gravatarForm.gravatar_email.$dirty">Malformed email</span>
                            </div>
                            <div class="form-group">
                                <button type="submit" class="btn btn-primary align-middle" data-ng-click="updateGravatar(user.gravatarEmail)" data-ng-disabled="gravatarForm.$invalid">Save changes</button>
                            </div>
                        </form>
                    </div>
                </li>
                <li class="list-group-item list-group-item-danger" data-ng-show="userError">
                    <h4 class="list-group-item-heading">Error</h4>
                    <p class="list-group-item-text">{{userError}}</p>
                </li>
                <li class="list-group-item list-group-item-success" data-ng-show="userSuccess">
                    <p class="list-group-item-text">{{userSuccess}}</p>
                </li>
                <li class="list-group-item">
                    <h4 class="list-group-item-heading">Password settings</h4>
                    <div class="list-group-item-content">
                        <form name="passForm" class="form-horizontal">
                            <div class="form-group" data-ng-class="{'has-error': passForm.old_pass.$invalid && passForm.old_pass.$dirty}" data-ng-hide="user.oauthOnly">
                                <label class="control-label" for="old_pass">Old password</label>
                                <input class="form-control" type="password" id="old_pass" name="old_pass" placeholder="Old password" data-ng-model="password.old_pass" data-ng-minlength="8" data-ng-keyup="$event.keyCode == 13 && !passForm.$invalid">
                                <span class="help-block" data-ng-show="passForm.old_pass.$invalid.minLength">Too short (minimum = 8)</span>
                            </div>
                            <div class="form-group" data-ng-class="{'has-error': passForm.new_pass.$invalid && passForm.new_pass.$dirty}">
                                <label class="control-label" for="new_pass">New password</label>
                                <input class="form-control" type="password" id="new_pass" name="new_pass" placeholder="New password" data-ng-minlength="8" data-match="password.new_pass1" data-ng-model="password.new_pass" data-ng-keyup="$event.keyCode == 13 && !passForm.$invalid" required>
                                <span class="help-block" data-ng-show="passForm.new_pass.$invalid.minLength">Too short (minimum = 8)</span>
                                <span class="help-block" data-ng-show="passForm.new_pass.$error.match">Must be equal to the other password</span>
                            </div>
                            <div class="form-group" data-ng-class="{'has-error': passForm.new_pass1.$invalid && passForm.new_pass1.$dirty}">
                                <label class="control-label" for="new_pass1">Retype new password</label>
                                <input class="form-control" type="password" id="new_pass1" name="new_pass1" placeholder="Retype new password" data-ng-minlength="8" data-match="password.new_pass" data-ng-model="password.new_pass1" data-ng-keyup="$event.keyCode == 13 && !passForm.$invalid" required>
                                <span class="help-block" data-ng-show="passForm.new_pass1.$invalid.minLength">Too short (minimum = 8)</span>
                                <span class="help-block" data-ng-show="passForm.new_pass1.$error.match">Must be equal to the other password</span>
                            </div>
                            <div class="form-group">
                                <button type="submit" class="btn btn-primary align-middle" data-ng-click="updatePassword(password)" data-ng-disabled="passForm.$invalid">Save changes</button>
                            </div>
                        </form>
                    </div>
                </li>
            </ul>
        </div>
    </div>
    <div class="col-md-5">
        <div class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title">Manage your namespaces</h3>
            </div>
            <ul class="list-group" data-ng-show="nsError">
                <li class="list-group-item list-group-item-danger">
                    <h4 class="list-group-item-heading">Error</h4>
                    <p class="list-group-item-text">{{nsError}}</p>
                </li>
            </ul>
            <ul class="list-group">
                <li class="list-group-item">
                    <h4 class="list-group-item-heading">Register a new namespace</h4>
                    <div class="list-group-item-content">
                        <form name="nsForm" class="form-horizontal">
                            <div class="form-group" data-ng-class="{'has-error': nsForm.namespace.$invalid && nsForm.namespace.$dirty}">
                                <label class="control-label" for="namespace">Fully qualified name</label>
                                <input class="form-control" type="text" id="namespace" name="namespace" placeholder="e.g: org.kevoree.library" data-ng-model="namespace" data-fqn-compliant data-ng-keyup="$event.keyCode == 13 && !nsForm.$invalid" required>
                                <span class="help-block" data-ng-show="nsForm.namespace.$error.fqnCompliant">Allowed pattern is {{fqnRegex}}</span>
                            </div>
                            <div class="form-group">
                                <button type="submit" class="btn btn-primary align-middle" data-ng-click="registerNamespace(namespace)" data-ng-disabled="nsForm.$invalid">Register</button>
                            </div>
                        </form>
                    </div>
                </li>
            </ul>
            <ul class="list-group">
                <li class="list-group-item">
                    <table class="table table-condensed">
                        <thead>
                        <tr>
                            <th><a href="" data-ng-click="reverse=!reverse;order(reverse)">FQN</a></th>
                            <th class="align-right" data-ng-show="user.namespaces.length > 0">Action</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr data-ng-show="!user.namespaces.length">
                            <td colspan="2" style="text-align: center">
                                <em>You do not have <strong>namespaces</strong> registered yet</em>
                            </td>
                        </tr>
                        <tr data-ng-repeat="ns in user.namespaces">
                            <td class="col-md-9">{{ns.fqn}}</td>
                            <td class="col-md-3 align-right">
                                <a href data-ng-show="ns.owner === user.id" data-ng-click="deleteNs(ns.fqn)">Delete <span class="glyphicon glyphicon-trash"></span></a>
                                <a href data-ng-show="ns.owner !== user.id" data-ng-click="leaveNs(ns.fqn)">Leave <span class="glyphicon glyphicon-remove"></span></a>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </li>
            </ul>
        </div>
    </div>
</div>