(function() {
    'use strict';

    angular
        .module('jHipsterPlaygroundApp')
        .config(httpConfig);

    httpConfig.$inject = ['$urlRouterProvider', '$httpProvider', 'httpRequestInterceptorCacheBusterProvider', '$urlMatcherFactoryProvider', 'jwtInterceptorProvider'];

    function httpConfig($urlRouterProvider, $httpProvider, httpRequestInterceptorCacheBusterProvider, $urlMatcherFactoryProvider, jwtInterceptorProvider) {

        //Cache everything except rest api requests
        httpRequestInterceptorCacheBusterProvider.setMatchlist([/.*api.*/, /.*protected.*/], true);

        $urlRouterProvider.otherwise('/');

        jwtInterceptorProvider.tokenGetter = ['AuthServerProvider', 'jwtHelper', '$http', function(AuthServerProvider, jwtHelper, $http) {
            var idToken = AuthServerProvider.getToken();
            var refreshToken = AuthServerProvider.getRefreshToken();

            if (angular.isDefined(idToken)) {
                if (jwtHelper.isTokenExpired(idToken)) {
                    return $http({
                        url: '/api/refresh',
                        skipAuthorization: true, // No need to send the JWT in the headers
                        method: 'POST',
                        data: {
                            refreshToken: refreshToken
                        }
                    }).then(function(response) {
                        var id_token = response.data.id_token;
                        AuthServerProvider.storeAuthenticationToken(id_token);
                        return id_token;
                    });
                } else {
                    return idToken;
                }
            }
        }];

        $httpProvider.interceptors.push('errorHandlerInterceptor');
        $httpProvider.interceptors.push('authExpiredInterceptor');
        $httpProvider.interceptors.push('jwtInterceptor');
        $httpProvider.interceptors.push('notificationInterceptor');
        // jhipster-needle-angularjs-add-interceptor JHipster will add new application http interceptor here

        $urlMatcherFactoryProvider.type('boolean', {
            name : 'boolean',
            decode: function(val) { return val === true || val === 'true'; },
            encode: function(val) { return val ? 1 : 0; },
            equals: function(a, b) { return this.is(a) && a === b; },
            is: function(val) { return [true,false,0,1].indexOf(val) >= 0; },
            pattern: /bool|true|0|1/
        });
    }
})();
