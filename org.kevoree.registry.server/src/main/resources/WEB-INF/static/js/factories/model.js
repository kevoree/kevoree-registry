// Created by leiko on 03/12/14 11:09
angular.module('kevoreeRegistry')
    .factory('modelFactory', ['$http', function ($http) {
        var factory = {};

        /**
         *
         * @param {String} [path]
         * @returns {*}
         */
        factory.getModel = function (path) {
            path = path || '';
            if (path.length > 0) {
                if (path[0] === '/') {
                    path = path.substr(1, path.length-1);
                }
            }
            return $http.get("/"+path);
        };

        return factory;
    }]);