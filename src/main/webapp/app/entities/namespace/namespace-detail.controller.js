(function() {
    'use strict';

    angular
        .module('kevoreeRegistryApp')
        .controller('NamespaceDetailController', NamespaceDetailController);

    NamespaceDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Namespace', 'User'];

    function NamespaceDetailController($scope, $rootScope, $stateParams, entity, Namespace, User) {
        var vm = this;
        vm.namespace = entity;
        
        var unsubscribe = $rootScope.$on('kevoreeRegistryApp:namespaceUpdate', function(event, result) {
            vm.namespace = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
