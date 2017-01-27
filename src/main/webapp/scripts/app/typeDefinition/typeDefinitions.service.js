'use strict';

angular.module('kevoreeRegistryApp')
    .factory('TypeDefinitions', function ($resource, $http) {
			var res = $resource('api/tdefs/:id', {}, {});
			res.getLatest = function () {
				return $http.get('api/tdefs/latest');
			};
			return res;
    });
