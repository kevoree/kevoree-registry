(function () {
  angular
    .module('kevoreeRegistryApp')
    .factory('TypeDefinitions', TypeDefinitions);

  TypeDefinitions.$inject = ['$resource'];

  function TypeDefinitions($resource) {
    var service = $resource('api/tdefs/:id', {}, {});

    return angular.extend(service, {
      latest: function (params) {
        return $resource('api/tdefs/page').get(params).$promise;
      }
    });
  }
})();
