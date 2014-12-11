// Created by leiko on 26/11/14 17:06

/**
 *
 */
angular.module('kevoreeRegistry')
    .controller('SignIn', ['$scope', '$http', function($scope, $http) {
        $scope.error = null;

        $scope.validate = function(user) {
            var formData = angular.copy(user);
            delete formData['password1'];

            $http.post('/!/auth/signin', formData)
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