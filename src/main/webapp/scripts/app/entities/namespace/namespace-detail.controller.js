'use strict';

angular.module('kevoreeRegistryApp')
    .controller('NamespaceDetailController', function ($scope, $stateParams, Namespaces) {
        $scope.namespace = {};
        $scope.load = function (name) {
            Namespaces.get({ name: name }, function (result) {
              $scope.namespace = result;
            });
        };
        $scope.load($stateParams.name);
    });
