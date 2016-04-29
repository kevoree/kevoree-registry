(function() {
    'use strict';

    angular
        .module('kevoreeRegistryApp')
        .controller('DeployUnitController', DeployUnitController);

    DeployUnitController.$inject = ['$scope', '$state', 'DeployUnit'];

    function DeployUnitController ($scope, $state, DeployUnit) {
        var vm = this;
        vm.deployUnits = [];
        vm.loadAll = function() {
            DeployUnit.query(function(result) {
                vm.deployUnits = result;
            });
        };

        vm.loadAll();
        
    }
})();
