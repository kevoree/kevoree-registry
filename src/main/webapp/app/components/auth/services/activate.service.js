(function () {
  'use strict';

  angular.module('kevoreeRegistryApp')
		.factory('Activate', Activate);

  Activate.$inject = ['$resource'];

  function Activate($resource) {
    return $resource('api/activate', {}, {
      'get': { method: 'GET', params: {}, isArray: false }
    });
  }
})();
