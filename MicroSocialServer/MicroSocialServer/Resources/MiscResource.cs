using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using Grapevine;
using Grapevine.Server;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;

namespace MicroSocialServer.Resources
{
    public sealed class MiscResource : RESTResource
    {
        [RESTRoute(Method = HttpMethod.GET, PathInfo = @"^/serverInfo")]
        public void GetServerInfo(HttpListenerContext context)
        {
            JObject returnObject = new JObject();

            string confFileLocation =
                System.IO.Path.GetDirectoryName(System.Reflection.Assembly.GetExecutingAssembly().Location) +
                @"\config\serverconfig.json";

            using (StreamReader r = new StreamReader(confFileLocation))
            {
                string json = r.ReadToEnd();
                dynamic confValues = JsonConvert.DeserializeObject(json);

                string serverName = confValues.serverName;
                returnObject["serverName"] = serverName;
            }

            this.SendTextResponse(context, returnObject.ToString(), Encoding.UTF8);
        }
    }
}
