<div class="container">
    <div class="row">
        <div class="col-md-6 col-md-push-3 well">
            <form class="form-horizontal" role="form" action="/!/user/edit" method="post">
                <div class="form-group form-group-sm">
                    <label class="col-sm-3 control-label">Name</label>
                    <div class="col-sm-9">
                        <p class="form-control-static">${user.getName()}</p>
                    </div>
                </div>
                <div class="form-group form-group-sm">
                    <label class="col-sm-3 control-label">Email</label>
                    <div class="col-sm-9">
                        <p class="form-control-static">${user.getId()}</p>
                    </div>
                </div>
                <div class="form-group form-group-sm">
                    <label for="gravatar_email" class="col-sm-3 control-label">Gravatar email</label>
                    <div class="col-sm-9">
                        <input type="email" class="form-control" name="gravatar_email" value="${user.getGravatarEmail()}">
                    </div>
                </div>
                <div class="form-group form-group-sm">
                    <div class="col-sm-offset-3 col-sm-9">
                        <button type="submit" class="btn btn-default">Save changes</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
    <div class="row">
        <div class="col-md-6 col-md-push-3 well">
            <form class="form-horizontal" role="form" action="/!/ns/add" method="post">
                <div class="form-group form-group-sm">
                    <label class="col-sm-3 control-label">Namespaces</label>
                    <div class="col-sm-9">
                        <table id="namespaces" class="table table-condensed table-hover">
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
                </div>
                <div class="form-group form-group-sm">
                    <label for="namespace" class="col-sm-3 control-label">Add namespace</label>
                    <div class="col-sm-9">
                        <input type="text" class="form-control" name="namespace" placeholder="e.g: org.kevoree.library">
                    </div>
                </div>
                <div class="form-group form-group-sm">
                    <div class="col-sm-offset-3 col-sm-9">
                        <button type="submit" class="btn btn-default">Add namespace</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>