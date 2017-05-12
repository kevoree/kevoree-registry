(function () {
	'use strict';

	angular
		.module('kevoreeRegistryApp')
		.config(appConfig);

	appConfig.$inject = ['$stateProvider'];

	function appConfig($stateProvider) {
		$stateProvider.state('site', {
			'abstract': true,
			views: {
				'navbar@': {
					templateUrl: 'app/components/navbar/navbar.html',
					controller: 'NavbarController',
					controllerAs: 'vm'
				}
			},
			resolve: {
				authorize: [
					'Auth',
					function(Auth) {
						return Auth.authorize();
					}
				],
				translatePartialLoader: [
					'$translate',
					'$translatePartialLoader',
					function($translate, $translatePartialLoader) {
						$translatePartialLoader.addPart('global');
						$translatePartialLoader.addPart('error');
						$translatePartialLoader.addPart('language');
						return $translate.refresh();
					}
				]
			}
		});
	}

})();
