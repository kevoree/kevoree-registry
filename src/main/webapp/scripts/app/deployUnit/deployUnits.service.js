angular
	.module('kevoreeRegistryApp')
	.factory('DeployUnits', function ($resource) {
		var service = $resource('api/dus/:id', {}, {});

		return angular.extend(service, {
			latest: function (params) {
				return $resource('api/dus/page').get(params).$promise;
			}
		});
	});
