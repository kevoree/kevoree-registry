(function () {
	'use strict';

	angular.module('kevoreeRegistryApp')
		.config(stateConfig);

	stateConfig.$inject = ['$stateProvider'];

	function stateConfig($stateProvider) {
		$stateProvider
			.state('tdefs', {
				parent: 'site',
				url: '/tdefs?page?size?sort',
				data: {
					authorities: []
				},
				views: {
					'content@': {
						templateUrl: 'app/typeDefinition/typeDefinitions.html',
						controller: 'TypeDefinitionController',
						controllerAs: 'vm'
					}
				},
				resolve: {
					translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
						$translatePartialLoader.addPart('typeDefinition');
						return $translate.refresh();
					}]
				}
			})
			.state('tdefDetail', {
				parent: 'tdefs',
				url: '/:id',
				data: {
					authorities: []
				},
				views: {
					'content@': {
						templateUrl: 'app/typeDefinition/typeDefinition-detail.html',
						controller: 'TypeDefinitionDetailController',
						controllerAs: 'vm'
					}
				},
				resolve: {
					translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
						$translatePartialLoader.addPart('typeDefinition');
						return $translate.refresh();
					}]
				}
			});
	}
})();
