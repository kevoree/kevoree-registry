'use strict';

angular.module('kevoreeRegistryApp')
    .controller('TypeDefinitionDetailController', function ($scope, $state, $stateParams, TypeDefinitions) {
        $scope.tdef = null;
        TypeDefinitions.get(
            { id: $stateParams.id },
            function (du) {
                $scope.tdef = du;
                $scope.tdef.model = JSON.stringify(JSON.parse($scope.tdef.model), null, 2);
            },
            function () {
                $state.go('tdefs');
            }
        );
    });
