'use strict';

angular.module('kevoreeRegistryApp')
	.config(function ($stateProvider) {
		$stateProvider
			.state('register', {
				parent: 'account',
				url: '/register',
				data: {
					authorities: []
				},
				views: {
					'content@': {
						templateUrl: 'scripts/app/account/register/register.html',
						controller: 'RegisterController',
						controllerAs: 'vm'
					}
				},
				resolve: {
					translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
						$translatePartialLoader.addPart('register');
						return $translate.refresh();
					}]
				}
			});
	});
