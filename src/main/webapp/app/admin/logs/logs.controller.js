(function () {
  'use strict';

  angular
    .module('kevoreeRegistryApp')
    .controller('LogsController', LogsController);

  LogsController.$inject = ['Logs'];

  function LogsController(Logs) {
    var vm = this;

    vm.changeLevel = changeLevel;
    vm.loggers = Logs.findAll();

    function changeLevel(name, level) {
      Logs.changeLevel({ name: name, level: level }, function () {
        vm.loggers = Logs.findAll();
      });
    }
  }
})();
