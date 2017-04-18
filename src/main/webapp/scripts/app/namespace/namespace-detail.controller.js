'use strict';

angular.module('kevoreeRegistryApp')
	.controller('NamespaceDetailController', function ($q, $scope, $stateParams, $timeout, $resource, Principal, Namespaces, Namespace, Users) {
		$scope.user = null;
		$scope.namespace = null;
		$scope.selectedUser = null;
		$scope.canModify = false;

		$scope.load = function (name) {
			$q.all([
				Principal.identity(),
				Namespaces.get({ name: name }).$promise,
				$resource('/api/namespaces/:name/tdefs', {}, {})
					.query({ name: name, version: 'latest' }).$promise
			]).then(function (results) {
				$scope.user = results[0];
				$scope.namespace = results[1];
				$scope.typeDefinitions = results[2];

				$scope.canModify = $scope.user && ($scope.user.authorities.indexOf('ROLE_ADMIN') !== -1
					|| $scope.user.login === $scope.namespace.owner);

			});
		};
		$scope.load($stateParams.name);

		$scope.addMember = function () {
			Users.query().$promise.then(function (users) {
				$scope.users = users.filter(function (user) {
					return user.authorities.indexOf('ROLE_ADMIN') === -1
						&& user.authorities.indexOf('ROLE_ANONYMOUS') === -1
						&& $scope.namespace.members.indexOf(user.login) === -1;
				});
				$scope.selectedUser = $scope.users[0];
			});
			$('#addMemberModal').modal('show');
		};

		$scope.confirmAddMember = function (name, user) {
			Namespace.addMember(name, user)
				.then(function () {
					$scope.load($stateParams.name);
					$('#addMemberModal').modal('hide');
					$scope.clear();
				})
				.catch(function (resp) {
					$scope.addError = resp.data.message;
					switch (resp.code) {
						case 401:
							// unauthorized
							$scope.removeError = 'you must be logged-in to add "' + user.login + '" to "' + name + '"';
							break;

						case 403:
							// forbidden
							$scope.removeError = 'you are not authorized to add users to "' + name + '"';
							break;

						case 404:
							// namespace not found
							$scope.removeError = 'unable to find namespace "' + name + '"';
							break;

						default:
							console.log('unhandled error on POST /api/namespaces/' + name + '/members', user, resp);
							break;
					}
				});
		};

		$scope.removeMember = function () {
			$('#removeMemberModal').modal('show');
		};

		$scope.confirmRemoveMember = function (name, member) {
			Namespace.deleteMember(name, member)
				.then(function () {
					$scope.load($stateParams.name);
					$('#removeMemberModal').modal('hide');
				})
				.catch(function (resp) {
					switch (resp.code) {
						case 401:
							// unauthorized
							$scope.removeError = 'you must be logged-in to delete "' + member.login + '" from "' + name + '"';
							break;

						case 403:
							// forbidden
							$scope.removeError = 'you are not authorized to delete "' + member.login + '" from "' + name + '"';
							break;

						case 404:
							// namespace not found
							$scope.removeError = 'unable to find namespace "' + name + '"';
							break;

						default:
							console.log('unhandled error on DELETE /api/namespaces/' + name + '/members/' + member.login, resp);
							break;
					}
				});
		};

		$scope.clear = function () {
			$scope.users = [];
			$scope.selectedUser = null;
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
