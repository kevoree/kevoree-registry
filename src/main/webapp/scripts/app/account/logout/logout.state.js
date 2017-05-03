'use strict';

angular.module('kevoreeRegistryApp')
	.config(function ($stateProvider) {
		$stateProvider
			.state('logout', {
				parent: 'account',
				url: '/logout',
				data: {
					authorities: []
				},
				views: {
					'content@': {
						templateUrl: 'scripts/app/home/home.html',
						controller: 'LogoutController',
						controllerAs: 'vm'
					}
				}
			});
	});
