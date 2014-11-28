// Created by leiko on 26/11/14 17:06

var FQN_REGEX = /^([a-z_]+(\.[a-z_]+)*)$/;

/**
 *
 */
angular.module('kevoreeRegistry')
    .controller('ProfileCtrl', ['$scope', '$http', function ($scope, $http) {
        $scope.user = {};
        $scope.namespace = null;
        $scope.password = {};
        $scope.fqnRegex = FQN_REGEX.toString();

        $http({method: 'GET', url: '/!/user', headers: { Accept: 'application/json' }}).
            success(function(data, status, headers, config) {
                console.log(data);
                $scope.user = data;
            }).
            error(function(data, status, headers, config) {
                console.log('ERROR', data, status);
            }
        );

        $scope.updatePassword = function (data, csrfToken) {
            // TODO
            var formData = angular.copy(data);
            formData['csrfmiddlewaretoken'] = csrfToken;
            delete formData['new_pass1'];
            console.log('TODO update password using', formData);
        };

        $scope.updateGravatar = function (data, csrfToken) {
            // TODO
            console.log('TODO update gravatar email using', data, csrfToken);
        };

        $scope.registerNamespace = function (data, csrfToken) {
            console.log('TODO register namespace:', data, csrfToken);
        };

        $scope.deleteNs = function (data) {
            console.log('Should delete ns', data);
        };

        $scope.leaveNs = function (data) {
            console.log('Should leave ns', data);
        };

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