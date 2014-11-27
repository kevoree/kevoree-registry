// Created by leiko on 26/11/14 17:06

/**
 *
 */
angular.module('kevoreeRegistry')
    .controller('LogInCtrl', ['$scope', '$http', function($scope, $http) {
        $scope.error = null;
        $scope.validate = function(user) {
            var data = {
                email: user.email,
                password: CryptoJS.SHA512(user.password).toString()
            };

            console.log('Login in clicked:', data);
            $http.post('/!/auth/login', data)
                .success(function () {
                    window.location = '/';
                })
                .error(function (err) {
                    $scope.error = err.message;
                });
        };
    }]);