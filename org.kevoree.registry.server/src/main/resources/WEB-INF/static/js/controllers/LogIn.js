// Created by leiko on 26/11/14 17:06

/**
 *
 */
angular.module('kevoreeRegistry')
    .controller('LogIn', ['$scope', '$http', function($scope, $http) {
        $scope.error = null;
        $scope.validate = function(user) {
            $http.post('/!/auth/login', { email: user.email, password: user.password })
                .success(function () {
                    window.location = '/';
                })
                .error(function (err, status) {
                    if (err.error) {
                        $scope.error = err.error;
                    } else {
                        $scope.error = 'Something went wrong (status code '+status+')';
                    }
                });
        };
    }]);