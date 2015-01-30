'use strict';

angular.module('kevoreeRegistryApp')
    .controller('TypeDefinitionDetailController', function ($scope, $stateParams, TypeDefinition) {
        $scope.tDef = {};
        $scope.load = function (name, version) {
            TypeDefinition.get({name: name, version: version}, function (result) {
              $scope.tDef = result;
            });
        };
        $scope.load($stateParams.name, $stateParams.version);
    });
