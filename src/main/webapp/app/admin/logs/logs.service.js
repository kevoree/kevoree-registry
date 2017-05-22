(function () {
  'use strict';

  angular
    .module('kevoreeRegistryApp')
    .factory('Logs', Logs);

  Logs.$inject = ['$resource'];

  function Logs($resource) {
    var service = $resource('management/logs', {}, {
      'findAll': { method: 'GET', isArray: true },
      'changeLevel': { method: 'PUT' }
    });

    return service;
  }
})();
