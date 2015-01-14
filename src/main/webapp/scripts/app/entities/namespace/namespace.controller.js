'use strict';

angular.module('kevoreeRegistryApp')
    .controller('NamespaceController', function ($scope, Namespace) {
        $scope.namespaces = [];
        $scope.loadAll = function() {
            Namespace.query(function(result) {
               $scope.namespaces = result;
            });
        };
        $scope.loadAll();

        $scope.create = function () {
            Namespace.save($scope.namespace,
                function () {
                    $scope.loadAll();
                    $('#saveNamespaceModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (fqn) {
            $scope.namespace = Namespace.get({fqn: fqn});
            $('#saveNamespaceModal').modal('show');
        };

        $scope.delete = function (fqn) {
            $scope.namespace = Namespace.get({fqn: fqn});
            $('#deleteNamespaceConfirmation').modal('show');
        };

        $scope.confirmDelete = function (fqn) {
            Namespace.delete({fqn: fqn},
                function () {
                    $scope.loadAll();
                    $('#deleteNamespaceConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.namespace = {fqn: null};
        };
    });
