(function() {
    'use strict';

    angular
        .module('jHipsterPlaygroundApp')
        .factory('AuthServerProvider', AuthServerProvider);

    AuthServerProvider.$inject = ['$http', '$localStorage', '$sessionStorage', '$q'];

    function AuthServerProvider ($http, $localStorage, $sessionStorage, $q) {
        var service = {
            getToken: getToken,
            getRefreshToken: getRefreshToken,
            hasValidToken: hasValidToken,
            login: login,
            loginWithToken: loginWithToken,
            storeAuthenticationToken: storeAuthenticationToken,
            storeRefreshToken: storeRefreshToken,
            logout: logout
        };

        return service;

        function getToken () {
            return $localStorage.authenticationToken || $sessionStorage.authenticationToken;
        }

        function getRefreshToken () {
            return $localStorage.refreshToken || $sessionStorage.refreshToken;
        }

        function hasValidToken () {
            var token = this.getToken();
            return token && token.expires && token.expires > new Date().getTime();
        }

        function login (credentials) {
            var data = {
                username: credentials.username,
                password: credentials.password,
                rememberMe: credentials.rememberMe
            };
            return $http.post('api/authenticate', data).success(authenticateSuccess);

            function authenticateSuccess (data, status, headers) {
                var bearerToken = headers('Authorization');
                if (angular.isDefined(bearerToken) && bearerToken.slice(0, 7) === 'Bearer ') {
                    var jwt = bearerToken.slice(7, bearerToken.length);
                    service.storeAuthenticationToken(jwt, false);

                    var refreshToken = headers('refresh_token');
                    service.storeRefreshToken(refreshToken, credentials.rememberMe);
                    return jwt;
                }
            }
        }

        function loginWithToken(jwt, rememberMe) {
            var deferred = $q.defer();

            if (jwt !== undefined) {
                this.storeAuthenticationToken(jwt, rememberMe);
                deferred.resolve(jwt);
            }
            else {
                deferred.reject();
            }

            return deferred.promise;
        }

        function storeAuthenticationToken(jwt, rememberMe) {
            if(rememberMe){
                $localStorage.authenticationToken = jwt;
            } else {
                $sessionStorage.authenticationToken = jwt;
            }
        }

        function storeRefreshToken(jwt, rememberMe) {
            if(rememberMe){
                $localStorage.refreshToken = jwt;
            } else {
                $sessionStorage.refreshToken = jwt;
            }
        }

        function logout () {
            delete $localStorage.authenticationToken;
            delete $sessionStorage.authenticationToken;
            
            delete $localStorage.refreshToken;
            delete $sessionStorage.refreshToken;
        }
    }
})();
