(function () {
  angular.module('kevoreeRegistryApp')
		.factory('Users', Users);

  Users.$inject = ['$resource'];

  function Users($resource) {
    return $resource('api/users', {}, {});
  }
})();
