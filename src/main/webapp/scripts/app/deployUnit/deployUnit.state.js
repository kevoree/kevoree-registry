'use strict';

angular.module('kevoreeRegistryApp')
    .config(function ($stateProvider) {
	$stateProvider
            .state('dus', {
	parent: 'site',
	url: '/dus?page?size?sort',
	data: {
		authorities: []
	},
	views: {
		'content@': {
			templateUrl: 'scripts/app/deployUnit/deployUnit.html',
			controller: 'DeployUnitController',
			controllerAs: 'vm'
		}
	},
	resolve: {
		translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
			$translatePartialLoader.addPart('deployUnit');
			return $translate.refresh();
		}]
	}
})
            .state('duDetail', {
	parent: 'dus',
	url: '/:id',
	data: {
		authorities: []
	},
	views: {
		'content@': {
			templateUrl: 'scripts/app/deployUnit/deployUnit-detail.html',
			controller: 'DeployUnitDetailController',
			controllerAs: 'vm'
		}
	},
	resolve: {
		translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
			$translatePartialLoader.addPart('deployUnit');
			return $translate.refresh();
		}]
	}
});
});
