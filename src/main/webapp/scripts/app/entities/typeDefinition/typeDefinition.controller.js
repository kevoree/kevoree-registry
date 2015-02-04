'use strict';

angular.module('kevoreeRegistryApp')
    .controller('TypeDefinitionController', function ($scope, TypeDefinition, Namespaces) {
        $scope.tdefs = [];
        $scope.namespaces = [];

        $scope.loadAll = function() {
            Namespaces.query(function (namespaces) {
                $scope.namespaces = namespaces;
                TypeDefinition.query(function(result) {
                    $scope.tdefs = result;
                });
            });
        };
        $scope.loadAll();

        $scope.create = function () {
            TypeDefinition.save($scope.tdef,
                function () {
                    $scope.loadAll();
                    $('#saveTypeDefinitionModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (namespace, name, version) {
            $scope.tdef = TypeDefinition.get({namespace: namespace, name: name, version: version});
            $('#saveTypeDefinitionModal').modal('show');
        };

        $scope.delete = function (namespace, name, version) {
            $scope.tdef = Namespace.get({namespace: namespace, name: name, version: version});
            $('#deleteTypeDefinitionConfirmation').modal('show');
        };

        $scope.confirmDelete = function (namespace, name, version) {
            TypeDefinition.delete({namespace: namespace, name: name, version: version},
                function () {
                    $scope.loadAll();
                    $('#deleteTypeDefinitionConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.tdef = {namespace: null, name: null, version: null};
        };
    });
