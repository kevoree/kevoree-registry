function JhiItemCountController() {
	var vm = this;
	vm.limits = [20, 50, 100, 250];
}

angular
	.module('kevoreeRegistryApp')
	.component('jhiItemCount', {
		templateUrl: 'app/components/jhi-item-count/jhi-item-count.html',
		bindings: {
			limit: '=',
			onChange: '&',
		},
		controller: JhiItemCountController,
		controllerAs: 'vm'
	});
