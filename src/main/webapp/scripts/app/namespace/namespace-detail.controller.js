'use strict';

angular.module('kevoreeRegistryApp')
    .controller('NamespaceDetailController', function ($scope, $stateParams, $timeout, Namespaces, Namespace) {
        $scope.namespace = null;
        $scope.load = function (name) {
            Namespaces.get({ name: name }, function (result) {
                $scope.namespace = result;
            });
        };
        $scope.load($stateParams.name);

        $scope.addMember = function () {
            $('#addMemberModal').modal('show');
        };

        $scope.confirmAddMember = function (nsName, memberName) {
            Namespace.addMember(nsName, memberName).then(
                function () {
                    $scope.load($stateParams.name);
                    $('#addMemberModal').modal('hide');
                    $scope.member = {}; // clear
                },
                function (resp) {
                    $scope.addError = resp.data.message;
                });
        };

        $scope.removeMember = function () {
            $scope.filteredMembers = $scope.namespace.members.filter(function (login) {
                return (login !== $scope.user.login);
            });
            $('#removeMemberModal').modal('show');
        };

        $scope.confirmRemoveMember = function (nsName, member) {
            Namespace.removeMember(nsName, member).then(
                function () {
                    $scope.load($stateParams.name);
                    $('#removeMemberModal').modal('hide');
                },
                function () {
                    $scope.removeError = 'unable to remove member, did you select one?';
                });
        };

        $scope.clearAddError = function () {
            $scope.addError = null;
        };

        $scope.clearDeleteError = function () {
            $scope.removeError = null;
        };

        angular.element('#addMemberModal').on('shown.bs.modal', function () {
            angular.element('[ng-model="member"]').focus();
        });

        angular.element('#removeMemberModal').on('shown.bs.modal', function () {
            angular.element('[ng-model="member"]').focus();
        });
    });
