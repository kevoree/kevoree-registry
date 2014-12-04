// Created by leiko on 03/12/14 11:09
angular.module('kevoreeRegistry')
    .factory('userFactory', ['$http', function ($http) {
        var baseURL = '/!/user';
        var factory = {};

        /**
         *
         * @returns {*}
         */
        factory.getUser = function () {
            return $http.get(baseURL);
        };

        /**
         *
         * @param {Object} data
         * @returns {*}
         */
        factory.editUser = function (data) {
            return $http.post(baseURL + '/edit', data);
        };

        return factory;
    }]);