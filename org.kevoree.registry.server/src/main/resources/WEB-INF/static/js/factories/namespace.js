// Created by leiko on 03/12/14 11:09
angular.module('kevoreeRegistry')
    .factory('namespaceFactory', ['$http', function ($http) {
        var baseURL = '/!/ns';
        var factory = {};

        /**
         *
         * @returns {*}
         */
        factory.getNs = function () {
            return $http.get(baseURL);
        };

        /**
         *
         * @param {String} fqn
         * @returns {*}
         */
        factory.addNs = function (fqn) {
            return $http.post(baseURL + '/add', { fqn : fqn });
        };

        /**
         *
         * @param {String} fqn
         * @returns {*}
         */
        factory.leaveNs = function (fqn) {
            return $http.post(baseURL + '/leave', { fqn : fqn });
        };

        /**
         *
         * @param {String} fqn
         * @returns {*}
         */
        factory.deleteNs = function (fqn) {
            return $http.post(baseURL + '/delete', { fqn : fqn });
        };

        return factory;
    }]);