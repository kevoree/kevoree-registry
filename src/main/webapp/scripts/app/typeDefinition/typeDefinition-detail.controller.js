'use strict';

angular.module('kevoreeRegistryApp')
    .controller('TypeDefinitionDetailController', function ($scope, $state, $stateParams, TypeDefinitions) {
        $scope.tdef = null;
        TypeDefinitions.get(
            { id: $stateParams.id },
            function (du) {
                $scope.tdef = du;
            },
            function () {
                $state.go('tdefs');
            }
        );
    });
