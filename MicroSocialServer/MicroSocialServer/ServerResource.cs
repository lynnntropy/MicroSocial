using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using Grapevine;
using Grapevine.Server;
using System.Net;


namespace MicroSocialServer
{
    public sealed class ServerResource : RESTResource
    {
        [RESTRoute(Method = HttpMethod.GET, PathInfo = @"^/hello")]
        public void HandleFooBarRequests(HttpListenerContext context)
        {            
            this.SendTextResponse(context, String.Format("Hello World! It's {0}!", DateTime.Now));
        }
    }
}
