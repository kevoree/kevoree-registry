angular
	.module('kevoreeRegistryApp')
	.factory('TypeDefinitions', function ($resource) {
		var service = $resource('api/tdefs/:id', {}, {});

		return angular.extend(service, {
			latest: function (params) {
				return $resource('api/tdefs/page').get(params).$promise;
			}
		});
	});
