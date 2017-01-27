'use strict';

angular
	.module('kevoreeRegistryApp')
	.factory('errorHandlerInterceptor', function ($rootScope, $q) {
		var service = {
			responseError: responseError
		};

		return service;

		function responseError(response) {
			if (!(response.status === 401 && (response.data === '' || (response.data.path && response.data.path.indexOf('/api/account') === 0)))) {
				$rootScope.$emit('kevoreeRegistryApp.httpError', response);
			}
			return $q.reject(response);
		}
	});
