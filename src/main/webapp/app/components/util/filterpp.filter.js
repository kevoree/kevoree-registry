(function () {
  'use strict';

  angular.module('kevoreeRegistryApp')
    .filter('filterpp', filterppFilter);

  filterppFilter.$inject = [];

  /**
   * Filter that is able to handle sub array of strings
   * @param  {[type]} elems   [description]
   * @param  {[type]} filters [description]
   * @return {[type]}         [description]
   */
  function filterppFilter() {
    return function (elems, filters) {
      return elems.filter(function (elem) {
        var show = true;
        for (var prop in filters) {
          if (angular.isDefined(elem[prop])) {
            if (angular.isArray(elem[prop])) {
              if (elem[prop].length === 0 && filters[prop].length === 0) {
                show = true;
              } else {
                show = Boolean(elem[prop].find(function (subElem) {
                  return subElem.indexOf(filters[prop]) !== -1;
                }));
              }
            } else if (angular.isString(elem[prop])) {
              show = elem[prop].indexOf(filters[prop]) !== -1;
            } else {
              show = (elem[prop] + '').indexOf(filters[prop]) !== -1;
            }
          }
          if (!show) {
            return false;
          }
        }
        return show;
      });
    };
  }
})();
