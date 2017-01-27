'use strict';

angular.module('kevoreeRegistryApp')
	.controller('ConfigurationController', function ($scope, ConfigurationService) {
		$scope.allConfiguration = null;
		$scope.configuration = null;

		ConfigurationService.get().then(function (configuration) {
			$scope.configuration = configuration;
		});
		ConfigurationService.getEnv().then(function (configuration) {
			$scope.allConfiguration = configuration;
		});
	});
