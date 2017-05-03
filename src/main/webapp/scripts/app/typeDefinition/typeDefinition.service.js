angular.module('kevoreeRegistryApp')
	.factory('TypeDefinition', function ($resource) {

		function jsonifyModel(tdef) {
			tdef.model = JSON.stringify(JSON.parse(tdef.model), null, 2);
			return tdef;
		}

		return {
			getByID: function (id) {
				return $resource('/api/tdefs/:id')
					.get({ id: id })
					.$promise
					.then(jsonifyModel);
			},

			get: function (namespace, name, version) {
				return $resource('/api/namespaces/:namespace/tdefs/:tdefName/:tdefVersion')
					.get({
						namespace: namespace,
						tdefName: name,
						tdefVersion: version
					})
					.$promise
					.then(jsonifyModel);
			},

			getLatest: function (namespace, name)  {
				return $resource('api/namespaces/:namespace/tdefs/:name')
					.query({
						namespace: namespace,
						name: name,
						version: 'latest'
					})
					.$promise
					.then(function (resp) {
						if (angular.isArray(resp)) {
							return resp.map(jsonifyModel);
						} else {
							return jsonifyModel(resp);
						}
					});
			},

			getLatestDeployUnits: function (namespace, name, version) {
				return $resource('/api/namespaces/:namespace/tdefs/:tdefName/:tdefVersion/dus')
					.query({
						namespace: namespace,
						tdefName: name,
						tdefVersion: version,
						version: 'latest'
					}).$promise;
			},

			getReleaseDeployUnits: function (namespace, name, version) {
				return $resource('/api/namespaces/:namespace/tdefs/:tdefName/:tdefVersion/dus')
					.query({
						namespace: namespace,
						tdefName: name,
						tdefVersion: version,
						version: 'release'
					}).$promise;
			}
		};
	});
