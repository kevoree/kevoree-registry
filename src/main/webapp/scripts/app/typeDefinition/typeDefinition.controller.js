'use strict';

angular.module('kevoreeRegistryApp')
    .controller('TypeDefinitionController', function ($scope, $stateParams, TypeDefinitions, Namespaces, User, Principal) {
        $scope.tdefs = [];
        $scope.namespaces = [];
        $scope.isInRole = Principal.isInRole;

        $scope.loadAll = function() {
            TypeDefinitions.query({
                namespace: $stateParams.namespace,
                name: $stateParams.name,
                version: $stateParams.version
            }, function(result) {
                // map tdefs so that they also get a fqn for filtering purposes
                $scope.tdefs = result.map(function (tdef) {
                    tdef.fqn = tdef.namespace.name + '.' + tdef.name + '/' + tdef.version;
                    return tdef;
                });
            });
        };
        $scope.loadAll();

        $scope.create = function () {
            User.getNamespaces().then(function (resp) {
                $scope.namespaces = resp.data;
            });
            $('#createTypeDefinitionModal').modal('show');
        };

        $scope.confirmCreate = function () {
            TypeDefinitions.save($scope.tdef,
                function () {
                    $scope.loadAll();
                    $('#createTypeDefinitionModal').modal('hide');
                    $scope.clear();
                }, function (resp) {
                    $scope.createError = resp.data.message;
                });
        };

        $scope.delete = function (namespace, name, version, event) {
            event.stopPropagation();
            event.preventDefault();
            $scope.tdef = TypeDefinitions.get({ namespace: namespace, name: name, version: version });
            $('#deleteTypeDefinitionConfirmation').modal('show');
        };

        $scope.confirmDelete = function (namespace, name, version) {
            TypeDefinitions.delete({ namespace: namespace, name: name, version: version },
                function () {
                    $scope.loadAll();
                    $('#deleteTypeDefinitionConfirmation').modal('hide');
                    $scope.clear();
                },
                function (resp) {
                    $scope.deleteError = resp.data.message;
                });
        };

        $scope.clear = function () {
            $scope.tdef = { namespace: null, name: null, version: null };
            $scope.filterText = null;
            Namespaces.query(function (namespaces) {
                $scope.namespaces = namespaces;
            });
        };

        $scope.clearDeleteError = function () {
            $scope.deleteError = null;
        };

        $scope.clearCreateError = function () {
            $scope.createError = null;
        };
    });
