'use strict';

angular
	.module('kevoreeRegistryApp')
	.factory('authInterceptor', function ($rootScope, $q, $location, $localStorage, $sessionStorage) {
		var service = {
			request: request
		};

		return service;

		function request(config) {
			config.headers = config.headers || {};
			var token = $localStorage.token || $sessionStorage.token;
			if (token && token.expires_at && token.expires_at > new Date().getTime()) {
				config.headers.Authorization = 'Bearer ' + token.access_token;
			}
			return config;
		}
	});
