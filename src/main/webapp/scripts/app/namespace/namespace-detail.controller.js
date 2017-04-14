'use strict';

angular.module('kevoreeRegistryApp')
	.controller('NamespaceDetailController', function ($scope, $stateParams, $timeout, $resource, Namespaces, Namespace) {
		$scope.namespace = null;
		$scope.selectedMember = null;
		$scope.member = null;

		$scope.load = function (name) {
			Namespaces.get({ name: name }, function (result) {
				$scope.namespace = result;
				$scope.selectedMember = $scope.namespace.members[0];
			});

			$resource('/api/namespaces/:name/tdefs', {}, {}).query({ name: name, version: 'latest' })
				.$promise.then(function (result) {
					$scope.typeDefinitions = result;
				});
		};
		$scope.load($stateParams.name);

		$scope.addMember = function () {
			$('#addMemberModal').modal('show');
		};

		$scope.confirmAddMember = function (name, member) {
			Namespace.save({ name: name }, { name: member },
				function () {
					$scope.load($stateParams.name);
					$('#addMemberModal').modal('hide');
					$scope.clear();
				},
				function (resp) {
					$scope.addError = resp.data.message;
				});
		};

		$scope.removeMember = function () {
			$('#removeMemberModal').modal('show');
		};

		$scope.confirmRemoveMember = function (name, member) {
			Namespace.delete({ name: name, member: member.login },
				function () {
					$scope.load($stateParams.name);
					$('#removeMemberModal').modal('hide');
				},
				function () {
					$scope.removeError = 'unable to remove member, did you select one?';
				});
		};

		$scope.clear = function () {
			$scope.selectedMember = null;
			$scope.member = null;
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
