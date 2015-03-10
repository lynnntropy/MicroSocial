using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using Grapevine;
using Grapevine.Server;
using System.Net;
using System.Runtime.CompilerServices;
using Newtonsoft.Json.Linq;
using MicroSocialServer.Schema;

using System.Text.RegularExpressions;

namespace MicroSocialServer.Resources
{
    public sealed class CORSResource : RESTResource
    {
        [RESTRoute(Method = HttpMethod.OPTIONS, PathInfo = @"^/\S+")]
        public void AnswerOptionsRequest(HttpListenerContext context)
        {
            context.Response.AddHeader("Access-Control-Allow-Headers", "Content-Type");
            context.Response.AddHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
            context.Response.AddHeader("Access-Control-Allow-Origin", "*");

            this.SendTextResponse(context, "");
        }
    }
}