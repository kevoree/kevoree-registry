(function() {
    'use strict';

    angular
        .module('kevoreeRegistryApp')
        .controller('TypeDefinitionDetailController', TypeDefinitionDetailController);

    TypeDefinitionDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'TypeDefinition', 'Namespace'];

    function TypeDefinitionDetailController($scope, $rootScope, $stateParams, entity, TypeDefinition, Namespace) {
        var vm = this;
        vm.typeDefinition = entity;
        
        var unsubscribe = $rootScope.$on('kevoreeRegistryApp:typeDefinitionUpdate', function(event, result) {
            vm.typeDefinition = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
