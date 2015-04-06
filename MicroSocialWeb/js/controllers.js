var microSocialApp = angular.module('microSocial', ['ngAnimate', 'angularSpinner', 'md5', 'ui.gravatar', 'luegg.directives', 'infinite-scroll']);

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

angular.module('ui.gravatar').config([
    'gravatarServiceProvider', function(gravatarServiceProvider) {
        gravatarServiceProvider.defaults = {
            size: 100,
            "default": 'retro'  // default for missing avatars
        };
    }
]);

microSocialApp.config(function($httpProvider) {
    $httpProvider.defaults.headers.post['Content-Type'] = 'application/json; charset=UTF-8';
});


microSocialApp.controller('LoginController', ['$scope', '$rootScope', '$http', '$log', '$location', 'usSpinnerService', function ($scope, $rootScope, $http, $log, $location, usSpinnerService)
{
    $rootScope.baseUrl = $location.absUrl();
    $rootScope.baseUrl = $rootScope.baseUrl.substring(0, $rootScope.baseUrl.length - 1);

    $scope.$on('logoutSuccessful', function (event)
    {
        $scope.stopSpin();
    });

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

    if ($.cookie('session'))
    {
        $scope.startSpin();

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
            $scope.user.username = $.cookie('username');

            $scope.successfulLogin($.cookie('session'));
            // instant login.. unsure if a good idea
//            $rootScope.validCookie = true;

        }).error(function (data, status, headers, config)
        {
            $log.info(data);
            $scope.stopSpin();
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
                loginSuccess(data.session_id);

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
        $rootScope.session = session;
        $rootScope.username = $scope.user.username;
        $log.info("$rootScope.session is " + $rootScope.session);

//        $cookies.session_id = session;
        $.cookie('session', session, { path: '/', expires: 365 });
        $.cookie('username', $scope.user.username, { path: '/', expires: 365 });

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

    $scope.$on('logoutSuccessful', function (event)
    {
        $scope.users = [];
    });

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

    $scope.openMessages = function(username, fullName)
    {
        $rootScope.messagesOtherUserName = fullName;
        $rootScope.$broadcast('openMessages', { username: username });
    }
}]);

microSocialApp.controller('FeedController', ['$scope', '$rootScope', '$http', '$log', function($scope, $rootScope, $http, $log)
{
    $scope.posts = [];
    $scope.fullWidth = true;

    $scope.$on('logoutSuccessful', function (event)
    {
        $scope.posts = [];
    });

    $scope.$on('loginCompleted', function (event)
    {
        $log.info("Feed controller received login completed event.");

        $scope.getFeed(0, 15);
    });

    $scope.$on('refreshFeed', function (event)
    {
        $scope.getFeed(0, 0);
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

    $scope.loadMore = function ()
    {
        $log.info('Loading more posts..');
        $scope.getFeed($scope.posts.length, $scope.posts.length + 20);
    }
}]);

microSocialApp.controller('StatusFormController', ['$scope', '$rootScope', '$http', '$log', function($scope, $rootScope, $http, $log)
{
    $scope.status = {};

    $scope.submitStatus = function()
    {
//        $scope.status.body = nl2br($scope.status.body, false);

        $("#status-field").val('');
        $scope.statusForm.$setPristine();

        $http({
            method: "POST",
            url: $rootScope.baseUrl + ":9000" + "/status",
            data:
            {
                "session_id": $rootScope.session,
                "status_body": $scope.status.body
            }
        })
        .success(function (data, status, headers, config)
        {
            $log.info("Posted status successfully.");
            $rootScope.$broadcast('refreshFeed');

        }).error(function (data, status, headers, config)
        {
            $log.info(data);
        });
    }
}]);

microSocialApp.controller('MessagesController', ['$scope', '$rootScope', '$http', '$log', function($scope, $rootScope, $http, $log)
{
    $scope.username = "";
    $rootScope.messagesOpen = false;

    $scope.messages = [];
    $scope.messageToSend = "";

    $scope.$on('logoutSuccessful', function (event)
    {
        $scope.messages = [];
    });

    $scope.$on('openMessages', function(event, args)
    {
        $scope.openMessages(args.username);
    });

    $scope.openMessages = function(username)
    {
        if ($rootScope.messagesOpen === false)
        {
            $rootScope.messagesOpen = true;

            setTimeout(function ()
            {
                $scope.username = username;
                $scope.getMessages(0, 50, true);

            }, 600);

        }
        else
        {
            $scope.crossFadeMessages(username);
        }
    };

    $scope.closeMessages = function()
    {
        $rootScope.messagesOpen = false;
        setTimeout(function ()
        {
            $scope.messages = [];
        }, 600);

    };

    $scope.getMessages = function (first, last, clear, addToBack)
    {
        $http({
            method: "GET",
            url: $rootScope.baseUrl + ":9000" + "/messages?session=" + $rootScope.session + "&user=" + $scope.username + "&first=" + first +"&last=" + last
        })
        .success(function (data, status, headers, config)
        {
            if ($('.messages-container').hasClass('fade-out'))
            {
                $('.messages-container').removeClass('fade-out');
            }

            if (!clear)
            {
                if (!addToBack)
                {
                    $scope.messages = data.messages.concat($scope.messages);
                }
                else
                {
                    $scope.messages = $scope.messages.concat(data.messages);
                }
            }
            else
            {
                $scope.messages = data.messages;
            }

            $log.info($scope.messages);

            for (var i = $scope.messages.length - 2; i >= 0; i--)
            {
                if ($scope.messages[i + 1].senderName == $scope.messages[i].senderName)
                {
                    $scope.messages[i].appendMessage = true;
                }
            }

            $log.info($scope.messages);

        }).error(function (data, status, headers, config)
        {
            $log.info(data);
        });
    };

    $scope.submitMessage = function ()
    {
        if ($scope.messageToSend.trim().length > 0)
        {
            $('#message-input-box').val('');

            $http({
                method: "POST",
                url: $rootScope.baseUrl + ":9000" + "/message",
                data: {
                    "session_id": $rootScope.session,

                    "from": $rootScope.username,
                    "to": $scope.username,
                    "message": $scope.messageToSend
                }
            })
            .success(function (data, status, headers, config)
            {
                $scope.getMessages(0, 0, false);
                $scope.messageToSend = "";

            }).error(function (data, status, headers, config)
            {
                $log.info(data);
            });
        }
    };

    $scope.crossFadeMessages = function (username)
    {
        $('.messages-container').addClass('fade-out');

        $('.messages-container').addClass('no-animation');
        setTimeout(function()
        {
            $('.messages-container').removeClass('no-animation');
        }, 500);

        setTimeout(function ()
        {
            $scope.username = username;
            $scope.getMessages(0, 50, true);

        }, 250);
    };

    $scope.socketIsOpen = false;
    $scope.openSocket = function()
    {
        var domain = $rootScope.baseUrl.substring(7, $rootScope.baseUrl.length);
        $scope.socket = new WebSocket("ws://" + domain  +":9001/chat");

        var conversationStarted = false;

        $scope.socket.onopen = function (event)
        {
            $scope.socketIsOpen = true;
            $scope.socket.send($scope.username);
            setTimeout(function ()
            {
                $scope.socket.send($rootScope.session);

            }, 1000);
        };

        $scope.socket.onmessage = function (event)
        {
            $log.info("Socket message: " + event.data);

            if (conversationStarted)
            {
                $scope.getMessages(0, 0, false);
            }
            else
            {
                var authenticatedMessage = "Authenticated";
                if (event.data.substring(0, authenticatedMessage.length) === authenticatedMessage)
                {
                    conversationStarted = true;
                }
            }
        }
    };

    $scope.closeSocket = function()
    {
        $scope.socket.close();
        $scope.socketIsOpen = false;
    };

    $scope.$watch('username', function(newVal, oldVal)
    {
        if ($scope.socketIsOpen)
        {
            $scope.closeSocket();
        }

        $scope.openSocket();
    });

    $scope.loadMore = function ()
    {
        $scope.getMessages($scope.messages.length, $scope.messages.length + 50, false, true);
    }

}]);

microSocialApp.controller('NavbarController', ['$scope', '$rootScope', '$http', '$log', function($scope, $rootScope, $http, $log)
{
    $scope.logoutHovered = false;
    $scope.settingsHovered = false;

    $scope.$on('loginCompleted', function (event)
    {
        $http({
            method: "POST",
            url: $rootScope.baseUrl + ":9000" + "/session/getUser",
            data:
            {
                "session_id": $rootScope.session
            }
        })
        .success(function (data, status, headers, config)
        {
            $log.info(data);
            $rootScope.userProfile = data;

        }).error(function (data, status, headers, config)
        {
            $log.info(data);
        });
    });

    $scope.logoutHover = function ()
    {
        $scope.logoutHovered = true;
    };

    $scope.logoutLeave = function ()
    {
        $scope.logoutHovered = false;
    };

    $scope.settingsHover = function ()
    {
        $scope.settingsHovered = true;
    };

    $scope.settingsLeave = function ()
    {
        $scope.settingsHovered = false;
    };

    $scope.logoutClick = function ()
    {
        $http({
            method: "DELETE",
            url: $rootScope.baseUrl + ":9000" + "/session",
            data:
            {
                "username": $rootScope.username,
                "session_id": $rootScope.session
            }
        })
        .success(function (data, status, headers, config)
        {
            $log.info(data);

            setTimeout(function ()
            {
                $('#login').css("display", "block");
            }, 0);

            $('#login').removeClass("loginEnded");

            $rootScope.$broadcast('logoutSuccessful');

        }).error(function (data, status, headers, config)
        {
            $log.info(data);
        });
    };

    $scope.settingsClick = function ()
    {
        $rootScope.settingsOpened = !$rootScope.settingsOpened;
    }
}]);