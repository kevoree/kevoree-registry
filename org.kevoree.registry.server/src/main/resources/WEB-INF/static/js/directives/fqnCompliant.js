// Created by leiko on 03/12/14 11:25
angular.module('kevoreeRegistry')
    .directive('fqnCompliant', function() {
        return {
            require: 'ngModel',
            link: function(scope, elm, attrs, ctrl) {
                if (!ctrl) { return; }

                ctrl.$parsers.unshift(function(viewValue) {
                    if (viewValue && FQN_REGEX.test(viewValue)) {
                        // it is valid
                        ctrl.$setValidity('fqnCompliant', true);
                        return viewValue;
                    } else {
                        // it is invalid, return undefined (no model update)
                        ctrl.$setValidity('fqnCompliant', false);
                        return undefined;
                    }
                });
            }
        };
    });