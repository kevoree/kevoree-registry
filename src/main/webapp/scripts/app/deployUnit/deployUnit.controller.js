'use strict';

angular.module('kevoreeRegistryApp')
    .controller('DeployUnitController', function ($rootScope, $scope, $stateParams, DeployUnits, User, Principal) {
        $scope.dus = [];
        $scope.namespaces = [];
        $scope.isInRole = Principal.isInRole;

        $scope.loadAll = function() {
            DeployUnits.query(function(result) {
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

        $scope.isMember = function (deployUnit) {
            return $rootScope.user.namespaces.some(function (ns) {
                return ns.typeDefinitions.some(function (tdef) {
                    return tdef.deployUnits.some(function (du) {
                        return deployUnit.id === du.id;
                    });
                });
            });
        };

        $scope.delete = function (namespace, tdefName, tdefVersion, name, version, platform, event) {
            event.stopPropagation();
            event.preventDefault();
            $scope.tdef = DeployUnits.get({
                namespace: namespace,
                tdefName: tdefName,
                tdefVersion: tdefVersion,
                name: name,
                version: version,
                platform: platform
            });
            $('#deleteConfirmation').modal('show');
        };

        $scope.confirmDelete = function (namespace, tdefName, tdefVersion, name, version, platform) {
            DeployUnits.delete({
                    namespace: namespace,
                    tdefName: tdefName,
                    tdefVersion: tdefVersion,
                    name: name,
                    version: version,
                    platform: platform
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
