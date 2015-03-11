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
    public sealed class StatusResource : RESTResource 
    {
        [RESTRoute(Method = HttpMethod.GET, PathInfo = @"^/feed\?first=\d+&last=\d+$")]
        public void GetFeed(HttpListenerContext context)
        {
            context.Response.AddHeader("Access-Control-Allow-Headers", "Content-Type");
            context.Response.AddHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            context.Response.AddHeader("Access-Control-Allow-Origin", "*");

            // we know the GET parameters were passed because otherwise the regex wouldn't match

            string regex = @"^/feed\?first=(?<first>\d+)&last=(?<last>\d+)$";

            Match match = Regex.Match(context.Request.RawUrl, regex);
            if (match.Success)
            {
                //this.SendTextResponse(context,
                //    string.Format("User requested statuses {0} ~ {1}.", match.Groups["first"].Value, match.Groups["last"].Value));

                int first = int.Parse(match.Groups["first"].Value);
                int last = int.Parse(match.Groups["last"].Value);

                var dbManager = new DatabaseManager();
                dbManager.Connect();
                var statuses = dbManager.GetStatuses(first, last);

                var json = new JObject();
                json["feed"] = JToken.FromObject(statuses);

                this.SendJsonResponse(context, json);
            }
            else
            {
                context.Response.StatusCode = 500;
                this.SendTextResponse(context,
                    "Grapevine dun goofed if you got this far..");
            }
        }

        [RESTRoute(Method = HttpMethod.POST, PathInfo = @"^/status$")]
        public void PostStatus(HttpListenerContext context)
        {
            var jsonPayload = GetJsonPayload(context.Request);

            var sessionId = int.Parse(jsonPayload.GetValue("session_id").ToString());
            var statusBody = jsonPayload.GetValue("status_body").ToString();

            var dbManager = new DatabaseManager();
            dbManager.Connect();

            if (dbManager.CheckSession(sessionId))
            {
                // session is valid, go on

                var newStatus = new Status();
                newStatus.poster = dbManager.GetUserFromSession(sessionId);
                newStatus.statusContent = statusBody;
                dbManager.AddStatus(newStatus);

                this.SendTextResponse(context, "");
            }
        }
    }
}
