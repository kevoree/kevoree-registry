(function () {
  'use strict';

  angular.module('kevoreeRegistryApp')
    .factory('Password', Password);

  Password.$inject = ['$resource'];

  function Password($resource) {
    var changePassword = $resource('api/account/change_password', {}, {});
    var resetPassword = $resource('api/account/reset_password/init', {}, {});
    var resetPasswordFinish = $resource('api/account/reset_password/finish', {}, {});

    return {
      change: function (password, success, error) {
        return changePassword.save(password, success, error);
      },
      reset: function (email, success, error) {
        return resetPassword.save(email, success, error);
      },
      resetFinish: function (keyAndPassword, success, error) {
        return resetPasswordFinish.save(keyAndPassword, success, error);
      }
    };
  }
})();
