// Created by leiko on 26/11/14 17:06

var FQN_REGEX = /^([a-z_]+(\.[a-z_]+)*)$/,
    INFO_TIMEOUT = 4000;

/**
 *
 */
angular.module('kevoreeRegistry')
    .controller('Profile', [
        '$scope', '$timeout', 'userFactory', 'namespaceFactory',
        function ($scope, $timeout, userFactory, namespaceFactory) {
            var gravatarPromise, passwordPromise;

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
                $timeout.cancel(passwordPromise);
                var formData = angular.copy(data);
                delete formData['new_pass1'];
                userFactory.editUser(formData)
                    .success(function () {
                        $scope.userSuccess = "Password updated successfully";
                        passwordPromise = $timeout(function () {
                            $scope.userSuccess = null;
                        }, INFO_TIMEOUT);
                    })
                    .error(function (res, status) {
                        if (res.error == null) {
                            $scope.userError = "Something went wrong (status code "+status+")";
                        } else {
                            $scope.userError = res.error;
                        }
                        passwordPromise = $timeout(function () {
                            $scope.userError = null;
                        }, INFO_TIMEOUT);
                    });
            };

            $scope.updateGravatar = function (email) {
                $timeout.cancel(gravatarPromise);
                userFactory.editUser({ gravatar_email: email })
                    .success(function () {
                        $scope.gravatarSuccess = "Gravatar email updated successfully";
                        gravatarPromise = $timeout(function () {
                            $scope.gravatarSuccess = null;
                        }, INFO_TIMEOUT);
                    })
                    .error(function (res, status) {
                        if (res.error == null) {
                            $scope.gravatarError = "Something went wrong (status code "+status+")";
                        } else {
                            $scope.gravatarError = res.error;
                        }
                        gravatarPromise = $timeout(function () {
                            $scope.gravatarError = null;
                        }, INFO_TIMEOUT);
                    });
            };

            $scope.registerNamespace = function (ns) {
                namespaceFactory.addNs(ns)
                    .success(function () {
                        $scope.updateUser();
                        $scope.nsError = null;
                    })
                    .error(nsErrorHandler);
            };

            $scope.deleteNs = function (ns) {
                namespaceFactory.deleteNs(ns)
                    .success(function () {
                        $scope.updateUser();
                        $scope.nsError = null;
                    })
                    .error(nsErrorHandler);
            };

            $scope.leaveNs = function (ns) {
                namespaceFactory.leaveNs(ns)
                    .success(function () {
                        $scope.updateUser();
                        $scope.nsError = null;
                    })
                    .error(nsErrorHandler);
            };
        }]);