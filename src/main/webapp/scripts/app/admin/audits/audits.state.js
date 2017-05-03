'use strict';

angular.module('kevoreeRegistryApp')
    .config(function ($stateProvider) {
	$stateProvider
            .state('audits', {
	parent: 'admin',
	url: '/audits',
	data: {
		authorities: ['ROLE_ADMIN']
	},
	views: {
		'content@': {
			templateUrl: 'scripts/app/admin/audits/audits.html',
			controller: 'AuditsController',
			controllerAs: 'vm'
		}
	},
	resolve: {
		translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
			$translatePartialLoader.addPart('audits');
			return $translate.refresh();
		}]
	}
});
});
