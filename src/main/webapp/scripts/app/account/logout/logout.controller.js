'use strict';

angular.module('kevoreeRegistryApp')
	.controller('LogoutController', function (Auth) {
		Auth.logout();
	});
