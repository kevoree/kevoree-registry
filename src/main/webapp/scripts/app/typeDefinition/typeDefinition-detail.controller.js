'use strict';

angular.module('kevoreeRegistryApp')
    .controller('TypeDefinitionDetailController', function ($scope, $state, $stateParams, TypeDefinitions) {
        $scope.tdef = {};
        $scope.load = function (namespace, name, version) {
            TypeDefinitions.query({
                namespace: namespace,
                name: name,
                version: version
            }, function (result) {
                $scope.tdef = result;
            });
        };

        $scope.load($stateParams.namespace, $stateParams.name, $stateParams.version);
    });
