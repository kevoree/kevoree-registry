(function() {
    'use strict';

    angular
        .module('kevoreeRegistryApp')
        .controller('TypeDefinitionDialogController', TypeDefinitionDialogController);

    TypeDefinitionDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'TypeDefinition', 'Namespace'];

    function TypeDefinitionDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, TypeDefinition, Namespace) {
        var vm = this;
        vm.typeDefinition = entity;
        vm.namespaces = Namespace.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('kevoreeRegistryApp:typeDefinitionUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.typeDefinition.id !== null) {
                TypeDefinition.update(vm.typeDefinition, onSaveSuccess, onSaveError);
            } else {
                TypeDefinition.save(vm.typeDefinition, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();
