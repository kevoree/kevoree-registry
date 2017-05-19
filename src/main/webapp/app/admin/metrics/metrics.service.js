'use strict';

angular.module('kevoreeRegistryApp')
	.factory('MetricsService', function ($http) {
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
});
