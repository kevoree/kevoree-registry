'use strict';

angular
	.module('kevoreeRegistryApp')
	.config(function ($stateProvider) {
		$stateProvider
			.state('tracker', {
				parent: 'admin',
				url: '/tracker',
				data: {
					authorities: ['ROLE_ADMIN']
				},
				views: {
					'content@': {
						templateUrl: 'scripts/app/admin/tracker/tracker.html',
						controller: 'TrackerController',
						controllerAs: 'vm'
					}
				},
				resolve: {
					mainTranslatePartialLoader: function ($translate, $translatePartialLoader) {
						$translatePartialLoader.addPart('tracker');
						return $translate.refresh();
					}
				},
				onEnter: function (Tracker) {
					Tracker.subscribe();
				},
				onExit: function (Tracker) {
					Tracker.unsubscribe();
				}
			});
	});
