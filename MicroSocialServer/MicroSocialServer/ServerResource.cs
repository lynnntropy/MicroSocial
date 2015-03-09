using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using Grapevine;
using Grapevine.Server;
using System.Net;

using Newtonsoft.Json.Linq;

using MicroSocialServer.Schema;


namespace MicroSocialServer
{
    public sealed class ServerResource : RESTResource
    {
        [RESTRoute(Method = HttpMethod.GET, PathInfo = @"^/hello")]
        public void SayHello(HttpListenerContext context)
        {
            //this.SendTextResponse(context, String.Format("Hello World! It's {0} here!", DateTime.Now.TimeOfDay));

            Console.WriteLine(context.Request.ToString());

            //this.SendTextResponse(context, String.Format("Hello World! It's {0} here!", DateTime.Now.TimeOfDay));
            this.SendTextResponse(context, context.Request.QueryString.ToString());
        }

        [RESTRoute(Method = HttpMethod.POST, PathInfo = @"^/helloPost")]
        public void SayHelloPost(HttpListenerContext context)
        {
            JObject jsonPayload = GetJsonPayload(context.Request);

            if (jsonPayload != null) this.SendTextResponse(context, jsonPayload.ToString());
            else this.SendTextResponse(context, "Payload is null.");
        }

        [RESTRoute(Method = HttpMethod.POST, PathInfo = @"^/register")]
        public void RegisterUser(HttpListenerContext context)
        {
            JObject jsonPayload = GetJsonPayload(context.Request);

            String username = jsonPayload.GetValue("username").ToString();
            String password = jsonPayload.GetValue("password").ToString();

            String passwordHash = BCrypt.Net.BCrypt.HashPassword(password);

            if (username != null && passwordHash != null)
            {
                DatabaseManager dbManager = new DatabaseManager();
                dbManager.Connect();
                dbManager.AddUser(username, passwordHash);
                dbManager.Close();

                this.SendTextResponse(context, String.Format("Successfully registered user {0}.", username));
            }
            else
            { 
                this.SendTextResponse(context, "No valid user received.");
            }
        }

        [RESTRoute(Method = HttpMethod.GET, PathInfo = @"^/getUsers")]
        public void GetUsers(HttpListenerContext context)
        {
            DatabaseManager dbManager = new DatabaseManager();
            dbManager.Connect();
            List<User> users = dbManager.GetUsers();
            dbManager.Close();

            JObject response = new JObject();
            //response.Add("users", Newtonsoft.Json.JsonConvert.SerializeObject(users));
            response["users"] = JToken.FromObject(users);

            this.SendJsonResponse(context, response);
        }
    }
}
