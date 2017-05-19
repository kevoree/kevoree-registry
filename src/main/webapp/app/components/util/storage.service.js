(function () {
  angular.module('kevoreeRegistryApp')
		.factory('StorageService', StorageService);

  StorageService.$inject = ['$window'];

  function StorageService($window) {
    return {
      get: function (key) {
        return angular.fromJson($window.localStorage.getItem(key));
      },

      save: function (key, data) {
        $window.localStorage.setItem(key, angular.toJson(data));
      },

      remove: function (key) {
        $window.localStorage.removeItem(key);
      },

      clearAll: function () {
        $window.localStorage.clear();
      }
    };
  }
})();
