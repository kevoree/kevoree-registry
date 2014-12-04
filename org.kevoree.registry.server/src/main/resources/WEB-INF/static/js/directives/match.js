// Created by leiko on 03/12/14 11:07

angular.module('kevoreeRegistry')
    .directive("match", function() {
        return {
            require: '?ngModel',
            restrict: 'A',
            scope: {
                match: '='
            },
            link: function(scope, elem, attrs, ctrl) {
                if (!ctrl) { return; }

                scope.$watch(function() {
                    var modelValue = angular.isUndefined(ctrl.$modelValue)? ctrl.$$invalidModelValue : ctrl.$modelValue;
                    return (ctrl.$pristine && angular.isUndefined(modelValue)) || scope.match === modelValue;
                }, function(currentValue) {
                    ctrl.$setValidity('match', currentValue);
                });
            }
        };
    });