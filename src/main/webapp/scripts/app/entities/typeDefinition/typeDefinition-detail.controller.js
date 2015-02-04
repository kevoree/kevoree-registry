'use strict';

angular.module('kevoreeRegistryApp')
    .controller('TypeDefinitionDetailController', function ($scope, $stateParams, TypeDefinition) {
        $scope.tdef = {};
        $scope.load = function (namespace, name, version) {
            TypeDefinition.get({namespace: namespace, name: name, version: version}, function (result) {
              $scope.tdef = result;
            });
        };
        $scope.load($stateParams.namespace, $stateParams.name, $stateParams.version);
    });
