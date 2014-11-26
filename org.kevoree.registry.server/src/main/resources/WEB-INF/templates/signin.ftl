<div class="container">
    <div class="row">
        <div class="col-md-3 col-md-offset-2">
            <a href="/!/auth/connect">
                <img id="googleSignIn" src="/!/static/images/google_signin_base.png"
                     onmousedown="document.getElementById('googleSignIn').src='/!/static/images/google_signin_press.png'"
                     onmouseup="document.getElementById('googleSignIn').src='/!/static/images/google_signin_base.png'"
                     onmouseover="document.getElementById('googleSignIn').src='/!/static/images/google_signin_hover.png'"
                     onmouseout="document.getElementById('googleSignIn').src='/!/static/images/google_signin_base.png'"
                     alt="Sign in with Google"/>
            </a>
        </div>
        <div class="col-md-6 well">
            <form class="form-horizontal" role="form" action="/!/ns/add" method="post">
                <div class="form-group form-group-sm">
                    <label for="namespace" class="col-sm-3 control-label">Add namespace</label>
                    <div class="col-sm-9">
                        <input type="text" class="form-control" name="namespace" placeholder="e.g: org.kevoree.library">
                    </div>
                </div>
                <div class="form-group form-group-sm">
                    <div class="col-sm-offset-3 col-sm-9">
                        <button type="submit" class="btn btn-default">Sign in</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>