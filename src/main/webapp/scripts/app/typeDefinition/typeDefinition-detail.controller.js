'use strict';

angular.module('kevoreeRegistryApp')
    .controller('TypeDefinitionDetailController', function ($scope, $state, $stateParams, TypeDefinitions) {
        $scope.tdef = {};
        $scope.load = function (namespace, name, version) {
            TypeDefinitions.get({ namespace: namespace, name: name, version: version },
                function (result) {
                    console.log('RESULT', result);
                    $scope.tdef = result;
                });
        };

        if ($stateParams.version) {
            $scope.load($stateParams.namespace, $stateParams.name, $stateParams.version);
        } else {
            $state.go('tdefs', { namespace: $stateParams.namespace, name: $stateParams.name });
        }
    });
