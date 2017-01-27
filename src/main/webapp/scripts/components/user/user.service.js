'use strict';

angular.module('kevoreeRegistryApp')
	.service('User', function ($resource) {
		var service = $resource('api/users/:login', {}, {
			'query': { method: 'GET', isArray: true },
			'get': {
				method: 'GET',
				transformResponse: function (data) {
					data = angular.fromJson(data);
					return data;
				}
			},
			'save': { method: 'POST' },
			'update': { method: 'PUT' },
			'delete': { method: 'DELETE' }
		});

		return service;
	});
