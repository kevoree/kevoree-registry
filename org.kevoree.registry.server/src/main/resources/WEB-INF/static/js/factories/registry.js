// Created by leiko on 03/12/14 11:09
angular.module('kevoreeRegistry')
    .factory('registryFactory', ['$http', function ($http) {
        var baseURL = '/!/registry';
        var factory = {};

        /**
         *
         * @returns {*}
         */
        factory.getVersion = function () {
            return $http.get(baseURL + '/version');
        };

        return factory;
    }]);