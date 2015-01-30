'use strict';

angular.module('kevoreeRegistryApp')
    .controller('TypeDefinitionController', function ($scope, TypeDefinition) {
        $scope.tdefs = [];
        $scope.loadAll = function() {
            TypeDefinition.query(function(result) {
               $scope.tdefs = result;
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

        $scope.update = function (name, version) {
            $scope.tdef = TypeDefinition.get({name: name, version: version});
            $('#saveTypeDefinitionModal').modal('show');
        };

        $scope.delete = function (name, version) {
            $scope.tdef = Namespace.get({name: name, version: version});
            $('#deleteTypeDefinitionConfirmation').modal('show');
        };

        $scope.confirmDelete = function (name, version) {
            TypeDefinition.delete({name: name, version: version},
                function () {
                    $scope.loadAll();
                    $('#deleteTypeDefinitionConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.tdef = {name: null, version: null, serializedTypeDefinition: null};
        };
    });
