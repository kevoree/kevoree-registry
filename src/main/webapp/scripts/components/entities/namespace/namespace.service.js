'use strict';

angular.module('kevoreeRegistryApp')
    .factory('Namespace', function Namespace($http) {
        return {
            addMember: function (nsName, memberName) {
                return $http.post('api/namespace/'+nsName+'/addMember', { name: memberName });
            },

            removeMember: function (nsName, memberName) {
                return $http.post('api/namespace/'+nsName+'/removeMember', { name: memberName });
            }
        };
    });
