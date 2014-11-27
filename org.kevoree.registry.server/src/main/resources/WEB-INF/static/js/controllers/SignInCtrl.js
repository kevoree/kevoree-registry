// Created by leiko on 26/11/14 17:06

/**
 *
 */
angular.module('kevoreeRegistry')
    .controller('SignInCtrl', ['$scope', '$http', function($scope, $http) {
        $scope.error = null;
        $scope.validate = function(user) {
            var data = {
                name: user.name,
                email: user.email,
                password: CryptoJS.SHA512(user.password).toString()
            };

            console.log('Sign in clicked:', data);
            $http.post('/!/auth/signin', data)
                .success(function () {
                    window.location = '/';
                })
                .error(function (err) {
                    $scope.error = err.message;
                });
        };

    }]).directive("match", function() {
        return {
            require: '?ngModel',
            restrict: 'A',
            scope: {
                match: '='
            },
            link: function(scope, elem, attrs, ctrl) {
                if (!ctrl) { return; }

                scope.$watch(function() {
                    var modelValue = angular.isUndefined(ctrl.$modelValue)? ctrl.$$invalidModelValue : ctrl.$modelValue;
                    return (ctrl.$pristine && angular.isUndefined(modelValue)) || scope.match === modelValue;
                }, function(currentValue) {
                    ctrl.$setValidity('match', currentValue);
                });
            }
        };
    });