'use strict';

angular.module('kevoreeRegistryApp')
	.controller('MetricsController', function ($scope, MetricsService) {
		var vm = this;
		vm.metrics = {};
		vm.updatingMetrics = true;

		vm.refresh = function () {
			vm.updatingMetrics = true;
			MetricsService.getMetrics()
				.then(function (metrics) {
					vm.metrics = metrics;
					vm.updatingMetrics = false;
				})
				.catch(function (resp) {
					vm.metrics = resp.data;
					vm.updatingMetrics = false;
				});
		};

		$scope.$watch('vm.metrics', function (newValue) {
			vm.servicesStats = {};
			vm.cachesStats = {};
			angular.forEach(newValue.timers, function (value, key) {
				if (key.indexOf('web.rest') !== -1 || key.indexOf('service') !== -1) {
					vm.servicesStats[key] = value;
				}

				if (key.indexOf('net.sf.ehcache.Cache') !== -1) {
					// remove gets or puts
					var index = key.lastIndexOf('.');
					var newKey = key.substr(0, index);

					// Keep the name of the domain
					index = newKey.lastIndexOf('.');
					vm.cachesStats[newKey] = {
						'name': newKey.substr(index + 1),
						'value': value
					};
				}
			});
		});

		vm.refresh();

		vm.refreshThreadDumpData = function () {
			MetricsService.threadDump().then(function (data) {
				vm.threadDump = data;

				vm.threadDumpRunnable = 0;
				vm.threadDumpWaiting = 0;
				vm.threadDumpTimedWaiting = 0;
				vm.threadDumpBlocked = 0;

				angular.forEach(data, function (value) {
					if (value.threadState === 'RUNNABLE') {
						vm.threadDumpRunnable += 1;
					} else if (value.threadState === 'WAITING') {
						vm.threadDumpWaiting += 1;
					} else if (value.threadState === 'TIMED_WAITING') {
						vm.threadDumpTimedWaiting += 1;
					} else if (value.threadState === 'BLOCKED') {
						vm.threadDumpBlocked += 1;
					}
				});

				vm.threadDumpAll = vm.threadDumpRunnable + vm.threadDumpWaiting +
					vm.threadDumpTimedWaiting + vm.threadDumpBlocked;

			});
		};

		vm.getLabelClass = function (threadState) {
			if (threadState === 'RUNNABLE') {
				return 'label-success';
			} else if (threadState === 'WAITING') {
				return 'label-info';
			} else if (threadState === 'TIMED_WAITING') {
				return 'label-warning';
			} else if (threadState === 'BLOCKED') {
				return 'label-danger';
			}
		};
	});
