'use strict';

angular
	.module('kevoreeRegistryApp')
	.factory('AuditsService', function ($resource) {
		var service = $resource('management/audits/:id', {}, {
			'get': {
				method: 'GET',
				isArray: true
			},
			'query': {
				method: 'GET',
				isArray: true,
				params: {
					fromDate: null,
					toDate: null
				}
			}
		});

		return service;
	});
