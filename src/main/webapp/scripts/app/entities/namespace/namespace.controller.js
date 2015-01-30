'use strict';

angular.module('kevoreeRegistryApp')
    .controller('NamespaceController', function ($scope, Namespaces, Namespace) {
        $scope.namespaces = [];
        $scope.loadAll = function() {
            Namespaces.query(function(result) {
               $scope.namespaces = result;
            });
        };
        $scope.loadAll();

        $scope.create = function () {
            Namespaces.save($scope.namespace,
                function () {
                    $scope.loadAll();
                    $('#saveNamespaceModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (name) {
            $scope.namespace = Namespaces.get({ name: name });
            $('#saveNamespaceModal').modal('show');
        };

        $scope.delete = function (name) {
            $scope.namespace = Namespaces.get({ name: name });
            $('#deleteNamespaceConfirmation').modal('show');
        };

        $scope.addMember = function (name) {
            $scope.namespace = Namespaces.get({ name: name });
            $('#addMemberModal').modal('show');
        };

        $scope.confirmAddMember = function (nsName, memberName) {
            Namespace.addMember(nsName, memberName).then(function () {
                $scope.loadAll();
                $('#addMemberModal').modal('hide');
                $scope.clear();
            });
        };

        $scope.removeMember = function (name) {
            $scope.namespace = Namespaces.get({ name: name }, function () {
                $scope.namespace.members = $scope.namespace.members.filter(function (login) {
                    return (login !== $scope.user.login);
                });
            });
            $('#removeMemberModal').modal('show');
        };

        $scope.confirmRemoveMember = function (nsName, member) {
            Namespace.removeMember(nsName, member).then(function () {
                $scope.loadAll();
                $('#removeMemberModal').modal('hide');
                $scope.clear();
            });
        };

        $scope.confirmDelete = function (name) {
            Namespaces.delete({ name: name },
                function () {
                    $scope.loadAll();
                    $('#deleteNamespaceConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.namespace = { name: null };
        };
    });
