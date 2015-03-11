var microSocialApp = angular.module('microSocial', ['ngAnimate', 'angularSpinner', 'md5', 'ui.gravatar']);

microSocialApp.config(['usSpinnerConfigProvider', function (usSpinnerConfigProvider) {
    usSpinnerConfigProvider.setDefaults({
        lines: 11, // The number of lines to draw
        length: 4, // The length of each line
        width: 2, // The line thickness
        radius: 11, // The radius of the inner circle
        corners: 0, // Corner roundness (0..1)
        rotate: 0, // The rotation offset
        direction: 1, // 1: clockwise, -1: counterclockwise
        color: '#fff', // #rgb or #rrggbb or array of colors
        speed: 1, // Rounds per second
        trail: 60, // Afterglow percentage
        shadow: false, // Whether to render a shadow
        hwaccel: false, // Whether to use hardware acceleration
        className: 'spinner', // The CSS class to assign to the spinner
        zIndex: 2e9, // The z-index (defaults to 2000000000)
        top: '50%', // Top position relative to parent
        left: '50%' // Left position relative to parent
    });
}]);

microSocialApp.controller('LoginController', ['$scope', '$rootScope', '$http', '$log', '$location', 'usSpinnerService', function ($scope, $rootScope, $http, $log, $location, usSpinnerService)
{
    $rootScope.baseUrl = $location.absUrl();
    $rootScope.baseUrl = $rootScope.baseUrl.substring(0, $rootScope.baseUrl.length - 1);

    if ($.cookie('session'))
    {
        // session cookie defined, check if it's valid

        $http({
            method: "POST",
            url: $rootScope.baseUrl + ":9000" + "/session/check",
            data: {
                "session_id": $.cookie('session')
            }
        })
        .success(function (data, status, headers, config)
        {
            $log.info(data);
            $scope.successfulLogin($.cookie('session'));
            // instant login.. unsure if a good idea
//            $rootScope.validCookie = true;

        }).error(function (data, status, headers, config)
        {
            $log.info(data);
        });
    }

    $scope.user = {};
    $scope.registerForm = false;

    $scope.working = false;
    $scope.showSpinner = false;

    $scope.errorMessage = "error here";
    $scope.showError = false;

    $scope.loginEnded = false;



    $scope.submitForm = function()
    {
        $scope.startSpin();

        var loginSuccess = function(session)
        {
            $scope.successfulLogin(session);
        };

        var registerSuccess = function()
        {
            // login the user if successfully registered

            $http({
                method: "POST",
                url: $rootScope.baseUrl + ":9000" + "/session",
                data: {
                    "username": $scope.user.username,
                    "password": $scope.user.password
                }
            })
            .success(function (data, status, headers, config)
            {
                $log.info(data);
                loginSuccess();

            }).error(function (data, status, headers, config)
            {
                $log.info(data);
            });
        };

        if (!$scope.registerForm)
        {
            $http({
                method: "POST",
                url: $rootScope.baseUrl + ":9000" + "/session",
                data: {
                    "username": $scope.user.username,
                    "password": $scope.user.password
                }
            })
            .success(function (data, status, headers, config)
            {
                $log.info(data);
                loginSuccess(data.session_id);

            }).error(function (data, status, headers, config)
            {
                $log.info(data);
                $scope.stopSpin();

                $scope.errorMessage = "Invalid login details.";
                $scope.showError = true;
            });
        }
        else
        {
            $http({
                method: "POST",
                url: $rootScope.baseUrl + ":9000" + "/register",
                data: {
                    "username": $scope.user.username,
                    "password": $scope.user.password,
                    "fullName": $scope.user.fullName,
                    "email": $scope.user.email
                }
            })
            .success(function (data, status, headers, config)
            {

                $log.info(data);
                registerSuccess();

            }).error(function (data, status, headers, config)
            {
                $log.info(data);
            });
        }
    };

    $scope.showRegister = function()
    {
        if ($scope.registerForm) $scope.registerForm = false;
        else $scope.registerForm = true;
    };

    $scope.startSpin = function()
    {
        $scope.working = true;
        $('#login-form').addClass('loading');

        setTimeout(function ()
        {
            $scope.showSpinner = true;
            usSpinnerService.spin('login');
        }, 350);
    };

    $scope.stopSpin = function()
    {
        setTimeout(function ()
        {
//            $scope.showSpinner = true;
//            usSpinnerService.stop('login');
            usSpinnerService.stop('login');

            $scope.working = false;
            $('#login-form').removeClass('loading');

            $log.info("Stopping spinner. $scope.working is " + $scope.working);
        }, 350);
    };

    $scope.successfulLogin = function(session)
    {
//        $scope.loginEnded = true;
        $scope.session = session;
        $log.info("$scope.session is " + $scope.session);

//        $cookies.session_id = session;
        $.cookie('session', session, { path: '/', expires: 365 });

        $('#login').addClass("loginEnded");

        $rootScope.$broadcast('loginCompleted');

        setTimeout(function()
        {
            $('#login').css("display", "none");
        }, 1000)
    };
}]);

microSocialApp.controller('UserListController', ['$scope', '$rootScope', '$http', '$log', function($scope, $rootScope, $http, $log)
{
    $scope.users = [];

    $scope.$on('loginCompleted', function (event)
    {
        $log.info("User list received login completed event.");

        $scope.getUsers();
    });

    $scope.getUsers = function()
    {
        $http({
            method: "GET",
            url: $rootScope.baseUrl + ":9000" + "/getUsers"
        })
        .success(function (data, status, headers, config)
        {
            $scope.users = data.users;
            $log.info($scope.users);

        }).error(function (data, status, headers, config)
        {
            $log.info(data);
        });
    };
}]);

microSocialApp.controller('FeedController', ['$scope', '$rootScope', '$http', '$log', function($scope, $rootScope, $http, $log)
{
    $scope.posts = [];

    $scope.$on('loginCompleted', function (event)
    {
        $log.info("Feed controller received login completed event.");

        $scope.getFeed(0, 15);
    });

    $scope.getFeed = function (first, last)
    {
        $http({
            method: "GET",
            url: $rootScope.baseUrl + ":9000" + "/feed?first=" + first + "&last=" + last
        })
        .success(function (data, status, headers, config)
        {
//            $log.info(data);
            $scope.posts = $scope.posts.concat(data.feed);
            $log.info($scope.posts);

                setTimeout(function ()
                {
                    jQuery("time.timeago").timeago();
                }, 10);


        }).error(function (data, status, headers, config)
        {
            $log.info(data);
        });
    };
}]);