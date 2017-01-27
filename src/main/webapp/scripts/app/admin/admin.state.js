'use strict';

angular.module('kevoreeRegistryApp')
	.config(function ($stateProvider) {
		$stateProvider
			.state('admin', {
				abstract: true,
				parent: 'site'
			});
	});
