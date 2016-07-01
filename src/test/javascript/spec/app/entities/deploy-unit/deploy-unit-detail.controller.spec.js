'use strict';

describe('Controller Tests', function() {

    describe('DeployUnit Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockDeployUnit;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockDeployUnit = jasmine.createSpy('MockDeployUnit');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'DeployUnit': MockDeployUnit
            };
            createController = function() {
                $injector.get('$controller')("DeployUnitDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'kevoreeRegistryApp:deployUnitUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
