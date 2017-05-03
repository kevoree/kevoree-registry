'use strict';

angular.module('kevoreeRegistryApp')
	.controller('LogsController', function (LogsService) {
		var vm = this;
		vm.loggers = LogsService.findAll();

		vm.changeLevel = function (name, level) {
			LogsService.changeLevel({
				name: name,
				level: level
			}, function () {
				vm.loggers = LogsService.findAll();
			});
		};
	});
