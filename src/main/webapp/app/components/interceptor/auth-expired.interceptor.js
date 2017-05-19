angular
  .module('kevoreeRegistryApp')
  .factory('authExpiredInterceptor', authExpiredInterceptor);

authExpiredInterceptor.$inject = ['$q', '$injector'];

function authExpiredInterceptor($q, $injector) {
  return {
    responseError: responseError
  };

  function responseError(response) {
    if (response.status === 401) {
      var AuthServerProvider = $injector.get('AuthServerProvider');
      var token = AuthServerProvider.getToken();
      if (token) {
        // try to refresh access_token
        return AuthServerProvider.refresh()
          .then(function () {
            // refresh success proceed with the previously failed request
            return $injector.get('$http')(response.config);
          })
          .catch(function () {
            // refresh failed
            return cleanAndReject(response);
          });
      } else {
        // no token found locally
        return cleanAndReject(response);
      }
    }

    return $q.reject(response);
  }

  function cleanAndReject(response) {
    var AuthServerProvider = $injector.get('AuthServerProvider');
    AuthServerProvider.deleteToken();
    var Principal = $injector.get('Principal');
    if (Principal.isAuthenticated()) {
      var Auth = $injector.get('Auth');
      Auth.authorize(true);
    }
    return $q.reject(response);
  }
}
