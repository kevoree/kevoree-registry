// Created by leiko on 26/11/14 17:06

/**
 *
 */
angular.module('kevoreeRegistry')
    .controller('SignInCtrl', ['$scope', '$http', function($scope, $http) {
        $scope.error = null;

        $scope.validate = function(user) {
            var formData = angular.copy(user);
            delete formData['password1'];

            $http.post('/!/auth/signin', formData)
                .success(function () {
                    window.location = '/';
                })
                .error(function (err) {
                    $scope.error = err.message;
                });
        };

    }]);