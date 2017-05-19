'use strict';

angular.module('kevoreeRegistryApp')
	.factory('AuthServerProvider', AuthServerProvider);

AuthServerProvider.$inject = ['$q', '$http', '$localStorage', 'Base64'];

function AuthServerProvider($q, $http, $localStorage, Base64) {
  return {
    login: login,
    logout: logout,
    refresh: refresh,
    getToken: getToken,
    deleteToken: deleteToken,
    hasValidToken: hasValidToken
  };

  function oauthToken(data) {
    data += "&scope=read%20write" +
						"&client_id=kevoree_registryapp" +
						"&client_secret=kevoree_registryapp_secret";
    return $http.post('oauth/token', data, {
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
        "Accept": "application/json",
        "Authorization": "Basic " + Base64.encode("kevoree_registryapp" + ':' + "kevoree_registryapp_secret")
      }
    }).success(function (response) {
      var expiredAt = new Date();
      expiredAt.setSeconds(expiredAt.getSeconds() + response.expires_in);
      response.expires_at = expiredAt.getTime();
      $localStorage.token = response;
      return response;
    });
  }

  function login(credentials) {
    var data = "grant_type=password" +
							"&username=" + credentials.username +
							"&password=" + credentials.password;
    return oauthToken(data);
  }

  function logout() {
		// logout from the server
    return $http.post('api/logout').then(function () {
      deleteToken();
    });
  }

  function refresh() {
    var token = getToken();
    if (token) {
      var data = "grant_type=refresh_token" +
								"&refresh_token=" + token.refresh_token;
      return oauthToken(data);
    } else {
      return $q.reject();
    }
  }

  function getToken() {
    return $localStorage.token;
  }

  function deleteToken() {
    delete $localStorage.token;
  }

  function hasValidToken() {
    var token = getToken();
    return token && token.expires_at && token.expires_at > new Date().getTime();
  }
}
