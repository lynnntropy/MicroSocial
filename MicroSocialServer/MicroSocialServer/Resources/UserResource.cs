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
    public sealed class UserResource : RESTResource
    {
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

        [RESTRoute(Method = HttpMethod.POST, PathInfo = @"^/session")]
        public void CreateSession(HttpListenerContext context)
        {
            JObject jsonPayload = GetJsonPayload(context.Request);

            String username = jsonPayload.GetValue("username").ToString();
            String password = jsonPayload.GetValue("password").ToString();

            DatabaseManager dbManager = new DatabaseManager();
            dbManager.Connect();

            if (dbManager.CheckPassword(username, password))
            {
                int sessionId = dbManager.AddSession(username);
                dbManager.Close();

                JObject response = new JObject();
                response["session_id"] = JToken.FromObject(sessionId);

                
                this.SendJsonResponse(context, response);
            }
            else
            {
                dbManager.Close();

                context.Response.StatusCode = 401; // 401 Unauthorized
                this.SendTextResponse(context, "Unauthorized");
            }
        }

        [RESTRoute(Method = HttpMethod.DELETE, PathInfo = @"^/session")]
        public void EndSession(HttpListenerContext context)
        {
            JObject jsonPayload = GetJsonPayload(context.Request);

            String username = jsonPayload.GetValue("username").ToString();
            int sessionId = int.Parse(jsonPayload.GetValue("session_id").ToString());

            DatabaseManager dbManager = new DatabaseManager();
            dbManager.Connect();

            if (dbManager.CheckSession(sessionId))
            {
                // TODO!
                dbManager.DeleteSession(username);

                dbManager.Close();

                context.Response.StatusCode = 200; // 200 OK
                this.SendTextResponse(context, String.Format("Session ended for user {0}", username));

            }
            else
            {
                dbManager.Close();

                context.Response.StatusCode = 401; // 401 Unauthorized
                this.SendTextResponse(context, "Unauthorized");
            }
        }
    }
}
