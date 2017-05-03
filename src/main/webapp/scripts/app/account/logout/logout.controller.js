'use strict';

angular.module('kevoreeRegistryApp')
	.controller('LogoutController', function ($state, Auth, AlertService) {
		Auth.logout()
			.then(function () {
				$state.go('home').then(function () {
					AlertService.success('Successfully logged-out');
				});
			});
	});
