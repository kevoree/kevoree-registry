'use strict';

angular
	.module('kevoreeRegistryApp')
	.config(function ($stateProvider) {
		$stateProvider
			.state('home', {
				parent: 'site',
				url: '/',
				data: {
					authorities: []
				},
				views: {
					'content@': {
						templateUrl: 'scripts/app/home/home.html',
						controller: 'HomeController',
						controllerAs: 'vm'
					}
				},
				resolve: {
					mainTranslatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
						$translatePartialLoader.addPart('home');
						return $translate.refresh();
					}]
				}
			});
	});
