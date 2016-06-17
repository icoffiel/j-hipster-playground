(function() {
    'use strict';

    angular
        .module('jHipsterPlaygroundApp')
        .factory('authExpiredInterceptor', authExpiredInterceptor);


    authExpiredInterceptor.$inject = ['$rootScope', '$q', '$injector', '$localStorage', '$sessionStorage'];

    function authExpiredInterceptor($rootScope, $q, $injector, $localStorage, $sessionStorage) {
        var service = {
            responseError: responseError
        };

        return service;

        function responseError(response) {
            if (response.status === 401) {
                delete $localStorage.authenticationToken;
                delete $sessionStorage.authenticationToken;
                delete $localStorage.refreshToken;
                delete $sessionStorage.refreshToken;
                var Principal = $injector.get('Principal');
                if (Principal.isAuthenticated()) {
                    var Auth = $injector.get('Auth');
                    Auth.authorize(true);
                }
                $injector.get('$state').go('home');
            }
            return $q.reject(response);
        }
    }
})();
