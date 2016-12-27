'use strict';

angular.module('kevoreeRegistryApp')
    .controller('DeployUnitDetailController', function ($scope, $state, $stateParams, DeployUnits) {
        $scope.du = null;
        DeployUnits.get(
            { id: $stateParams.id },
            function (du) {
                $scope.du = du;
                $scope.du.model = JSON.stringify(JSON.parse($scope.du.model), null, 2);
            },
            function () {
                $state.go('dus');
            }
        );
    });
