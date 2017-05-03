'use strict';

angular.module('kevoreeRegistryApp')
    .config(function ($stateProvider) {
	$stateProvider
            .state('activate', {
	parent: 'account',
	url: '/activate?key',
	data: {
		authorities: []
	},
	views: {
		'content@': {
			templateUrl: 'scripts/app/account/activate/activate.html',
			controller: 'ActivationController',
			controllerAs: 'vm'
		}
	},
	resolve: {
		translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
			$translatePartialLoader.addPart('activate');
			return $translate.refresh();
		}]
	}
});
});
