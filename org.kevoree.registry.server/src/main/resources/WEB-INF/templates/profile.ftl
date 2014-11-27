<div class="container">
    <div class="col-md-offset-1 col-md-5">
        <div class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title">Edit your profile</h3>
            </div>
            <div class="panel-body">
                <form class="form-horizontal col-md-8 col-md-push-2" role="form" action="/!/user/edit" method="post">
                    <div class="form-group">
                        <label class="control-label" for="name">Username</label>
                        <input class="form-control" type="text" id="name" name="name" placeholder="Username" value="${user.getName()}">
                    </div>
                    <div class="form-group">
                        <label class="control-label" for="email">Email</label>
                        <input class="form-control" type="email" id="email" value="${user.getId()}" disabled>
                    </div>
                    <div class="form-group">
                        <label class="control-label" for="gravatar_email">Gravatar email</label>
                        <input class="form-control" type="email" id="gravatar_email" name="gravatar_email" value="${user.getGravatarEmail()}">
                    </div>
                    <div class="form-group">
                        <label class="control-label" for="old_password">Old password</label>
                        <input class="form-control" type="password" id="old_password" name="old_password" placeholder="Old password">
                    </div>
                    <div class="form-group">
                        <label class="control-label" for="password">New password</label>
                        <input class="form-control" type="password" id="password" name="password" placeholder="New password">
                    </div>
                    <div class="form-group">
                        <label class="control-label" for="password1">Retype new password</label>
                        <input class="form-control" type="password" id="password1" name="password1" placeholder="Retype new password">
                    </div>
                    <div class="form-group">
                        <button type="submit" class="btn btn-primary align-middle">Save changes</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
    <div class="col-md-5">
        <div class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title">Manage your namespaces</h3>
            </div>
            <div class="panel-body">
                <div class="col-md-10 col-md-push-1">
                    <table class="table table-condensed">
                        <thead>
                        <tr>
                        <#if namespaces?size == 0>
                            <th>FQN</th>
                        <#else>
                            <th>FQN</th>
                            <th class="align-right">Action</th>
                        </#if>
                        </tr>
                        </thead>
                        <tbody>
                        <#if namespaces?size == 0>
                        <tr>
                            <td colspan="2" style="text-align: center">
                                <em>You do not have <strong>namespaces</strong> registered yet</em>
                            </td>
                        </tr>
                        <#else>
                            <#list namespaces as ns>
                            <tr>
                                <td>${ns.getFqn()}</td>
                                <td class="align-right">
                                    <#if ns.getOwner().getId() == user.getId()>
                                        <a href="/!/ns/delete/${ns.getFqn()}">Delete <span class="glyphicon glyphicon-trash"></span></a>
                                    <#else>
                                        <a href="/!/ns/leave/${ns.getFqn()}">Leave <span class="glyphicon glyphicon-remove"></span></a>
                                    </#if>
                                </td>
                            </tr>
                            </#list>
                        </#if>
                        </tbody>
                    </table>
                </div>
                <form class="form-horizontal col-md-8 col-md-push-2" role="form" action="/!/ns/add" method="post">
                    <div class="form-group">
                        <label for="namespace" class="control-label">Add namespace</label>
                        <input type="text" class="form-control" id="namespace" name="namespace" placeholder="e.g: org.kevoree.library">
                    </div>
                    <div class="form-group">
                        <button type="submit" class="btn btn-primary align-middle">Add namespace</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>