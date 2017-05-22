(function () {
  'use strict';

  angular
    .module('kevoreeRegistryApp')
    .directive('pageRibbon', pageRibbon);

  pageRibbon.$inject = ['ProfileService'];

  function pageRibbon(ProfileService) {
    var directive = {
      replace: true,
      restrict: 'AE',
      template: '<div class="ribbon hidden"><a href="" data-translate="global.ribbon.{{ribbonEnv}}">{{ribbonEnv}}</a></div>',
      link: linkFunc
    };

    return directive;

    function linkFunc(scope, element, attrs) {
      ProfileService.getProfileInfo().then(function (response) {
        if (response.ribbonEnv) {
          scope.ribbonEnv = response.ribbonEnv;
          element.addClass(response.ribbonEnv);
          element.removeClass('hidden');
        }
      });
    }
  }
})();
