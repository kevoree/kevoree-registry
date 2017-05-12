(function () {
	angular
		.module('kevoreeRegistryApp')
		.factory('DeployUnits', DeployUnits);

	DeployUnits.$inject = ['$resource'];

	function DeployUnits($resource) {
		var service = $resource('api/dus/:id', {}, {});

		return angular.extend(service, {
			latest: function (params) {
				return $resource('api/dus/page').get(params).$promise;
			}
		});
	}
})();
