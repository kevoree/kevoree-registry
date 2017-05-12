(function () {
	'use strict';

	angular
		.module('kevoreeRegistryApp')
		.config(stateConfig);

	stateConfig.$inject = ['$stateProvider'];

	function stateConfig($stateProvider) {
		$stateProvider
			.state('configuration', {
				parent: 'admin',
				url: '/configuration',
				data: {
					authorities: ['ROLE_ADMIN']
				},
				views: {
					'content@': {
						templateUrl: 'app/admin/configuration/configuration.html',
						controller: 'ConfigurationController',
						controllerAs: 'vm'
					}
				},
				resolve: {
					translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
						$translatePartialLoader.addPart('configuration');
						return $translate.refresh();
					}]
				}
			});
	}
})();
