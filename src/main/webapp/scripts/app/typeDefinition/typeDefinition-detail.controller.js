'use strict';

angular
	.module('kevoreeRegistryApp')
	.controller('TypeDefinitionDetailController', function ($scope, $state, $stateParams, TypeDefinitions) {
		$scope.tdef = null;

		function processDeployUnits(tdef) {
			tdef.deployUnits = tdef.deployUnits.reduce(function (dus, du) {
				var platform = du.platform;
				delete du.platform;
				var dusByPlatform = dus[platform] || [];
				dusByPlatform.push(du);
				dus[platform] = dusByPlatform;
				return dus;
			}, {});
			return tdef;
		}

		TypeDefinitions.get({
				id: $stateParams.id
			},
			function (tdef) {
				$scope.tdef = processDeployUnits(tdef);
				$scope.tdef.model = JSON.stringify(JSON.parse($scope.tdef.model), null, 2);
			},
			function () {
				$state.go('tdefs');
			}
		);
	});
