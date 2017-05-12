'use strict';

angular
	.module('kevoreeRegistryApp')
	.factory('authExpiredInterceptor', function ($q, $injector, $localStorage, $sessionStorage) {
		var service = {
			responseError: responseError
		};

		return service;

		function responseError(response) {
			if (response.status === 401) {
				delete $localStorage.token;
				delete $sessionStorage.token;
				var Principal = $injector.get('Principal');
				if (Principal.isAuthenticated()) {
					var Auth = $injector.get('Auth');
					Auth.authorize(true);
				}
			}
			return $q.reject(response);
		}
	});
