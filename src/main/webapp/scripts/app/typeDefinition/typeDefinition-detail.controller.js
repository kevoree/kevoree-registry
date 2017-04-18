'use strict';

angular
	.module('kevoreeRegistryApp')
	.controller('TypeDefinitionDetailController', function ($scope, $state, $stateParams, $resource, TypeDefinitions) {
		$scope.tdef = null;
		$scope.releasedDUS = null;
		$scope.latestDUS = null;

		if ($stateParams.id) {
			TypeDefinitions.get({
					id: $stateParams.id
				},
				function (tdef) {
					$scope.tdef = tdef;
					$scope.tdef.model = JSON.stringify(JSON.parse($scope.tdef.model), null, 2);
				},
				function () {
					$state.go('tdefs');
				}
			);
		} else {
			$resource('/api/namespaces/:ns/tdefs/:name/:version', {}, {})
				.get($stateParams)
				.$promise.then(function (tdef) {
					$scope.tdef = tdef;
					$scope.tdef.model = JSON.stringify(JSON.parse($scope.tdef.model), null, 2);
				}).catch(function () {
					$state.go('tdefs');
				})
		}

		$resource('/api/namespaces/:ns/tdefs/:tdefName/:tdefVersion/dus', {}, {})
			.query({
				ns: $stateParams.ns,
				tdefName: $stateParams.name,
				tdefVersion: $stateParams.version,
				version: 'release'
			})
			.$promise.then(function (dus) {
				$scope.releasedDUS = dus;
			});

		$resource('/api/namespaces/:ns/tdefs/:tdefName/:tdefVersion/dus', {}, {})
			.query({
				ns: $stateParams.ns,
				tdefName: $stateParams.name,
				tdefVersion: $stateParams.version,
				version: 'latest'
			})
			.$promise.then(function (dus) {
				$scope.latestDUS = dus;
			});
	});
