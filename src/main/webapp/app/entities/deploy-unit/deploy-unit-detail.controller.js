(function() {
    'use strict';

    angular
        .module('kevoreeRegistryApp')
        .controller('DeployUnitDetailController', DeployUnitDetailController);

    DeployUnitDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'DeployUnit', 'TypeDefinition'];

    function DeployUnitDetailController($scope, $rootScope, $stateParams, entity, DeployUnit, TypeDefinition) {
        var vm = this;
        vm.deployUnit = entity;
        
        var unsubscribe = $rootScope.$on('kevoreeRegistryApp:deployUnitUpdate', function(event, result) {
            vm.deployUnit = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
