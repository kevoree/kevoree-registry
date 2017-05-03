var jhiAlert = {
	templateUrl: 'scripts/components/alert/alert.html',
	controller: jhiAlertController
};

angular
	.module('kevoreeRegistryApp')
	.component('jhiAlert', jhiAlert);

jhiAlertController.$inject = ['$rootScope', '$scope', '$sce', 'AlertService'];

function jhiAlertController($rootScope, $scope, $sce, AlertService) {
	var vm = this;

	vm.alerts = AlertService.get();

	$scope.$on('$destroy', function () {
		vm.alerts = null;
	});
}
