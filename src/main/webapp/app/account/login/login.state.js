(function () {
	'use strict';

	angular.module('kevoreeRegistryApp')
		.config(stateConfig);

	stateConfig.$inject = ['$stateProvider'];

	function stateConfig($stateProvider) {
		$stateProvider
			.state('login', {
				parent: 'account',
				url: '/login',
				data: {
					authorities: []
				},
				views: {
					'content@': {
						templateUrl: 'app/account/login/login.html',
						controller: 'LoginController',
						controllerAs: 'vm'
					}
				},
				resolve: {
					translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
						$translatePartialLoader.addPart('login');
						return $translate.refresh();
					}]
				}
			});
	}
})();
