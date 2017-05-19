(function () {
  'use strict';

  angular
		.module('kevoreeRegistryApp')
		.directive('activeMenu', activeMenu);

  activeMenu.$inject = ['$translate', 'tmhDynamicLocale'];

  function activeMenu($translate, tmhDynamicLocale) {
    return {
      restrict: 'A',
      link: function (scope, element, attrs) {
        var language = attrs.activeMenu;

        scope.$watch(function () {
          return $translate.use();
        }, function (selectedLanguage) {
          if (language === selectedLanguage) {
            tmhDynamicLocale.set(language);
            element.addClass('active');
          } else {
            element.removeClass('active');
          }
        });
      }
    };
  }
})();
