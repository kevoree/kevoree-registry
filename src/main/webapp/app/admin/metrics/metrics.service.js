(function () {
  'use strict';

  angular.module('kevoreeRegistryApp')
    .factory('MetricsService', MetricsService);

  MetricsService.$inject = ['$http'];

  function MetricsService($http) {
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
