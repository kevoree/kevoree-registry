(function () {
  'use strict';

  angular.module('kevoreeRegistryApp')
    .factory('Metrics', Metrics);

  Metrics.$inject = ['$http'];

  function Metrics($http) {
    return {
      getMetrics: function () {
        return $http.get('management/metrics').then(function (response) {
          return response.data;
        });
      },

      threadDump: function () {
        return $http.get('management/dump').then(function (response) {
          return response.data;
        });
      }
    };
  }
})();
