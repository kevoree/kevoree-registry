'use strict';

angular.module('kevoreeRegistryApp')
    .controller('DeployUnitDetailController', function ($scope, $state, $stateParams, DeployUnits) {
        $scope.du = {};
        DeployUnits.query({
            namespace: $stateParams.namespace,
            tdefName: $stateParams.tdefName,
            tdefVersion: $stateParams.tdefVersion,
            name: $stateParams.name,
            version: $stateParams.version,
            platform: $stateParams.platform
        }, function (result) {
            $scope.du = result;
        });
    });
