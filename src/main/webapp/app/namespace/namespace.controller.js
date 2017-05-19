'use strict';

angular.module('kevoreeRegistryApp')
	.controller('NamespaceController', NamespaceController);

NamespaceController.$inject = ['$q', 'Namespaces', 'Principal'];

function NamespaceController($q, Namespaces, Principal) {
  var vm = this;
  vm.namespaces = [];
  vm.search = {
    name: '',
    owner: '',
    members: ''
  };
  vm.orderColumn = 'name';
  vm.reverse = false;
  vm.orderClasses = {
    'glyphicon-chevron-up': vm.reverse,
    'glyphicon-chevron-down': !vm.reverse
  };
  vm.canDelete = false;

  vm.loadAll = function () {
    $q.all([
      Principal.identity(),
      Namespaces.query().$promise
    ]).then(function (results) {
      var user = results[0];
      vm.namespaces = results[1].map(function (ns) {
        ns.members = ns.members.filter(function (member) {
          return member !== ns.owner;
        });
        ns.canDelete = user &&
					(user.authorities.indexOf('ROLE_ADMIN') !== -1 || user.login === ns.owner);
        return ns;
      });
    }).catch(function () {
			// TODO handle error
    });
  };

  vm.loadAll();

  vm.create = function () {
    Namespaces.save(vm.namespace,
			function () {
  vm.loadAll();
  vm.clear();
},
			function (resp) {
  vm.saveError = resp.data.message;
}
		);
  };

  vm.update = function (name) {
    vm.namespace = Namespaces.get({ name: name });
  };

  vm.delete = function (name, event) {
    event.stopPropagation();
    event.preventDefault();
    vm.namespace = Namespaces.get({ name: name });
  };

  vm.confirmDelete = function (name) {
    Namespaces.delete({ name: name })
			.$promise
			.then(function () {
  vm.loadAll();
  vm.clear();
})
			.catch(function (resp) {
  if (resp.status === 401) {
    vm.deleteError = resp.statusText;
  } else {
    vm.deleteError = resp.statusText;
  }
});
  };

  vm.clear = function () {
    vm.namespace = null;
    vm.search = {
      name: '',
      owner: '',
      members: ''
    };
  };

  vm.clearDeleteError = function () {
    vm.deleteError = null;
  };

  vm.clearSaveError = function () {
    vm.saveError = null;
  };

  vm.changeOrderBy = function (prop) {
    if (prop === vm.orderColumn) {
      vm.reverse = !vm.reverse;
      vm.orderClasses = {
        'glyphicon-chevron-up': vm.reverse,
        'glyphicon-chevron-down': !vm.reverse
      };
    }
    vm.orderColumn = prop;
  };

  angular.element('#saveNamespaceModal').on('shown.bs.modal', function () {
    angular.element('[ng-model="namespace.name"]').focus();
  });
}
