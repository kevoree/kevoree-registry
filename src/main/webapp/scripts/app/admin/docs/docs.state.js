'use strict';

angular
	.module('kevoreeRegistryApp')
	.config(function ($stateProvider) {
		$stateProvider
			.state('docs', {
				parent: 'admin',
				url: '/docs',
				data: {
					authorities: ['ROLE_ADMIN']
				},
				views: {
					'content@': {
						templateUrl: 'scripts/app/admin/docs/docs.html'
					}
				}
			});
	});
