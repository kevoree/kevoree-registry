// Created by leiko on 26/11/14 17:06

/**
 *
 */
angular.module('kevoreeRegistry')
    .controller('LogInCtrl', ['$scope', '$http', function($scope, $http) {
        $scope.error = null;
        $scope.validate = function(user) {
            $http.post('/!/auth/login', { email: user.email, password: user.password })
                .success(function () {
                    window.location = '/';
                })
                .error(function (err) {
                    $scope.error = err.message;
                });
        };
    }]);