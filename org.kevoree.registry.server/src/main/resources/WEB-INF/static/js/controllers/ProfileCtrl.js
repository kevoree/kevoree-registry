// Created by leiko on 26/11/14 17:06

var FQN_REGEX = /^([a-z_]+(\.[a-z_]+)*)$/;

/**
 *
 */
angular.module('kevoreeRegistry')
    .controller('ProfileCtrl', ['$scope', '$http', '$filter', function ($scope, $http, $filter) {
        $scope.namespace = null;
        $scope.password = {};
        $scope.fqnRegex = FQN_REGEX.toString();

        function nsErrorHandler(res, status) {
            if (res.error) {
                $scope.nsError = res.error;
            } else {
                $scope.nsError = 'Something went wrong (status code '+status+')';
            }
        }

        // listeners
        $scope.updatePassword = function (data) {
            var formData = angular.copy(data);
            delete formData['new_pass1'];
            $http.post('/!/user/edit', formData)
                .success(function () {
                    $scope.userSuccess = "Password updated successfully";
                })
                .error(function (res, status) {
                    if (res.error == null) {
                        $scope.userError = "Something went wrong (status code "+status+")";
                    } else {
                        $scope.userError = res.error;
                    }
                });
        };

        $scope.updateGravatar = function (email) {
            $http.post('/!/user/edit', { gravatar_email: email })
                .success(function () {
                    $scope.gravatarSuccess = "Gravatar email updated successfully";
                })
                .error(function (res, status) {
                    if (res.error == null) {
                        $scope.gravatarError = "Something went wrong (status code "+status+")";
                    } else {
                        $scope.gravatarError = res.error;
                    }
                });
        };

        $scope.registerNamespace = function (ns) {
            $http.post('/!/ns/add', { fqn: ns })
                .success(function () {
                    $scope.getUser();
                    $scope.order(false);
                })
                .error(nsErrorHandler);
        };

        $scope.deleteNs = function (ns) {
            $http.post('/!/ns/delete', { fqn: ns })
                .success(function () {
                    $scope.getUser();
                    $scope.order(false);
                })
                .error(nsErrorHandler);
        };

        $scope.leaveNs = function (ns) {
            $http.post('/!/ns/leave', { fqn: ns })
                .success(function () {
                    $scope.getUser();
                    $scope.order(false);
                })
                .error(nsErrorHandler);
        };

        $scope.order = function (reverse) {
            $scope.user.namespaces = $filter('orderBy')($scope.user.namespaces, 'fqn', reverse);
        };

        $scope.getUser(function (err) {
            if (err) {
                // humm, that's embarassing
                console.error(err);
            } else {
                $scope.order(false);
            }
        });
    }]).directive('fqnCompliant', function() {
        return {
            require: 'ngModel',
            link: function(scope, elm, attrs, ctrl) {
                if (!ctrl) { return; }

                ctrl.$parsers.unshift(function(viewValue) {
                    if (viewValue && FQN_REGEX.test(viewValue)) {
                        // it is valid
                        ctrl.$setValidity('fqnCompliant', true);
                        return viewValue;
                    } else {
                        // it is invalid, return undefined (no model update)
                        ctrl.$setValidity('fqnCompliant', false);
                        return undefined;
                    }
                });
            }
        };
    });