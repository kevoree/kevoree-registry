'use strict';

angular.module('kevoreeRegistryApp')
    .config(function ($stateProvider) {
	$stateProvider
            .state('health', {
	parent: 'admin',
	url: '/health',
	data: {
		authorities: ['ROLE_ADMIN']
	},
	views: {
		'content@': {
			templateUrl: 'scripts/app/admin/health/health.html',
			controller: 'HealthController',
			controllerAs: 'vm'
		}
	},
	resolve: {
		translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
			$translatePartialLoader.addPart('health');
			return $translate.refresh();
		}]
	}
});
});
