'use strict';

angular
	.module('kevoreeRegistryApp')
	.factory('authInterceptor', authInterceptor);

authInterceptor.$inject = ['$localStorage', '$sessionStorage'];

function authInterceptor($localStorage, $sessionStorage) {
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
}
