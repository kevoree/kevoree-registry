(function () {
  'use strict';

  angular.module('kevoreeRegistryApp')
		.factory('Namespaces', Namespaces);

  Namespaces.$inject = ['$resource'];

  function Namespaces($resource) {
    return $resource('api/namespaces/:name', {}, {});
  }
})();
