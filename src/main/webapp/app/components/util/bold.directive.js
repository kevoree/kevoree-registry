(function () {
  'use strict';

  angular.module('kevoreeRegistryApp')
		.directive('bold', bopldDirective);

  bopldDirective.$inject = ['$timeout'];

  function bopldDirective($timeout) {
    return {
      restrict: 'A',
      scope: {
        bold: '=bold'
      },
      link: function (scope, elem) {
				// execute after DOM is rendered
        $timeout(function () {
          var text = elem.text().trim();
          elem.html(
						text.replace(
							new RegExp(scope.bold, 'g'),
							'<strong>'+scope.bold+'</strong>'
						)
					);
        });
      }
    };
  }
})();
