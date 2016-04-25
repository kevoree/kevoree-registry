(function() {
    'use strict';

    angular
        .module('kevoreeRegistryApp')
        .controller('NamespaceDialogController', NamespaceDialogController);

    NamespaceDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Namespace', 'User'];

    function NamespaceDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Namespace, User) {
        var vm = this;
        vm.namespace = entity;
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('kevoreeRegistryApp:namespaceUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.namespace.id !== null) {
                Namespace.update(vm.namespace, onSaveSuccess, onSaveError);
            } else {
                Namespace.save(vm.namespace, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();
