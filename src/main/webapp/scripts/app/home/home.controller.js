'use strict';

angular.module('kevoreeRegistryApp')
	.controller('HomeController', function ($scope, $state, Principal) {
		Principal.identity().then(function (account) {
			$scope.account = account;
			$scope.isAuthenticated = Principal.isAuthenticated;
		});
	});
