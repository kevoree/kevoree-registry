(function () {
	'use strict';

	angular
		.module('kevoreeRegistryApp')
		.config(stateConfig);

	stateConfig.$inject = ['$stateProvider'];

	function stateConfig($stateProvider) {
		$stateProvider
			.state('metrics', {
				parent: 'admin',
				url: '/metrics',
				data: {
					authorities: ['ROLE_ADMIN']
				},
				views: {
					'content@': {
						templateUrl: 'app/admin/metrics/metrics.html',
						controller: 'MetricsController',
						controllerAs: 'vm'
					}
				},
				resolve: {
					translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
						$translatePartialLoader.addPart('metrics');
						return $translate.refresh();
					}]
				}
			});
	}
})();
