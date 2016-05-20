(function() {
    'use strict';

    angular
        .module('jHipsterPlaygroundApp')
        .factory('notificationInterceptor', notificationInterceptor);

    notificationInterceptor.$inject = ['$q', 'AlertService'];

    function notificationInterceptor ($q, AlertService) {
        var service = {
            response: response
        };

        return service;

        function response (response) {
            var alertKey = response.headers('X-jHipsterPlaygroundApp-alert');
            if (angular.isString(alertKey)) {
                AlertService.success(alertKey, { param : response.headers('X-jHipsterPlaygroundApp-params')});
            }
            return response;
        }
    }
})();
