angular.module('kevoreeRegistryApp')
	.controller('TrackerController', TrackerController);

TrackerController.$inject = ['Tracker'];

function TrackerController(Tracker) {
	var vm = this;
	// This controller uses a Websocket connection to receive user activities in real-time.
	vm.activities = [];

	Tracker.receive().then(null, null, function (activity) {
		showActivity(activity);
	});

	function showActivity(activity) {
		var existingActivity = false;
		for (var index = 0; index < vm.activities.length; index++) {
			if (vm.activities[index].sessionId === activity.sessionId) {
				existingActivity = true;
				if (activity.page === 'logout') {
					vm.activities.splice(index, 1);
				} else {
					vm.activities[index] = activity;
				}
			}
		}
		if (!existingActivity && (activity.page !== 'logout')) {
			vm.activities.push(activity);
		}
	}
}
