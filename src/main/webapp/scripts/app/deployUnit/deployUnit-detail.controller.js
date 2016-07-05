'use strict';

angular.module('kevoreeRegistryApp')
    .controller('DeployUnitDetailController', function ($scope, $state, $stateParams, DeployUnits) {
        $scope.du = null;
        DeployUnits.get(
            { id: $stateParams.id },
            function (du) {
                $scope.du = du;
            },
            function () {
                $state.go('dus');
            }
        );
    });
