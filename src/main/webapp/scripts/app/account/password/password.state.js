'use strict';

angular.module('kevoreeRegistryApp')
	.config(function ($stateProvider) {
		$stateProvider
			.state('password', {
				parent: 'account',
				url: '/password',
				data: {
					authorities: ['ROLE_USER']
				},
				views: {
					'content@': {
						templateUrl: 'scripts/app/account/password/password.html',
						controller: 'PasswordController',
						controllerAs: 'vm'
					}
				},
				resolve: {
					translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
						$translatePartialLoader.addPart('password');
						return $translate.refresh();
					}]
				}
			});
	});
