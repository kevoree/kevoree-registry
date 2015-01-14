'use strict';

angular.module('kevoreeRegistryApp')
    .controller('NamespaceDetailController', function ($scope, $stateParams, Namespace) {
        $scope.namespace = {};
        $scope.load = function (fqn) {
            Namespace.get({fqn: fqn}, function(result) {
              $scope.namespace = result;
            });
        };
        $scope.load($stateParams.fqn);
    });
