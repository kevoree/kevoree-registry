'use strict';

angular.module('kevoreeRegistryApp')
    .controller('DeployUnitController', function ($scope, $stateParams, DeployUnits, User, Principal) {
        $scope.dus = [];
        $scope.namespaces = [];
        $scope.isInRole = Principal.isInRole;

        $scope.loadAll = function() {
            DeployUnits.query({
                namespace: $stateParams.namespace,
                tdef: $stateParams.tdef,
                tdefVersion: $stateParams.tdefVersion,
                name: $stateParams.name,
                version: $stateParams.version
            }, function(result) {
                // map dus so that they also get a fqn for filtering purposes
                $scope.dus = result;
            });
        };
        $scope.loadAll();

        $scope.create = function () {
            User.getNamespaces().then(function (resp) {
                $scope.namespaces = resp.data;
            });
            $('#createModal').modal('show');
        };

        $scope.confirmCreate = function () {
            DeployUnits.save($scope.tdef,
                function () {
                    $scope.loadAll();
                    $('#createModal').modal('hide');
                    $scope.clear();
                }, function (resp) {
                    $scope.createError = resp.data.message;
                });
        };

        $scope.delete = function (namespace, tdef, tdefVersion, name, version, event) {
            event.stopPropagation();
            event.preventDefault();
            $scope.tdef = DeployUnits.get({
                namespace: namespace,
                tdef: tdef,
                tdefVersion: tdefVersion,
                name: name,
                version: version
            });
            $('#deleteConfirmation').modal('show');
        };

        $scope.confirmDelete = function (namespace, name, version) {
            DeployUnits.delete({
                    namespace: namespace,
                    tdef: tdef,
                    tdefVersion: tdefVersion,
                    name: name,
                    version: version
                }, function () {
                    $scope.loadAll();
                    $('#deleteConfirmation').modal('hide');
                    $scope.clear();
                },
                function (resp) {
                    $scope.deleteError = resp.data.message;
                });
        };

        $scope.clear = function () {
            $scope.filterText = null;
        };

        $scope.clearDeleteError = function () {
            $scope.deleteError = null;
        };

        $scope.clearCreateError = function () {
            $scope.createError = null;
        };
    });
