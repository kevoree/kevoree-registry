(function () {
	'use strict';

	angular.module('kevoreeRegistryApp')
		.factory('LogsService', LogsService);

	LogsService.$inject = ['$resource'];

	function LogsService($resource) {
		return $resource('api/logs', {}, {
			'findAll': { method: 'GET', isArray: true },
			'changeLevel': { method: 'PUT' }
		});
	}
})();
