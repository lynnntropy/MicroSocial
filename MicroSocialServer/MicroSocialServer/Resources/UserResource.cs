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
            context.Response.AddHeader("Access-Control-Allow-Headers", "Content-Type");
            context.Response.AddHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            context.Response.AddHeader("Access-Control-Allow-Origin", "*");

            JObject jsonPayload = GetJsonPayload(context.Request);

            String username = jsonPayload.GetValue("username").ToString();
            String password = jsonPayload.GetValue("password").ToString();
            String fullName = jsonPayload.GetValue("fullName").ToString();
            String email = jsonPayload.GetValue("email").ToString();

            String passwordHash = BCrypt.Net.BCrypt.HashPassword(password);

            if (username != null && passwordHash != null && email != null)
            {
                DatabaseManager dbManager = new DatabaseManager();
                dbManager.Connect();

                if (!dbManager.GetUsers().Exists(x => x.username == username))
                {
                    var user = new User();
                    user.username = username;
                    user.passwordHash = passwordHash;
                    user.fullName = fullName;
                    user.email = email;

                    dbManager.AddUser(user);
                    dbManager.Close();

                    context.Response.StatusCode = 200;
                    this.SendTextResponse(context, String.Format("Successfully registered user {0}.", username));
                }
                else
                {
                    dbManager.Close();

                    context.Response.StatusCode = 403;
                    this.SendTextResponse(context, String.Format("User already exists."));
                }

                
            }
            else
            {
                context.Response.StatusCode = 400;
                this.SendTextResponse(context, "No valid user received.");
            }
        }

        [RESTRoute(Method = HttpMethod.GET, PathInfo = @"^/getUsers")]
        public void GetUsers(HttpListenerContext context)
        {
            context.Response.AddHeader("Access-Control-Allow-Headers", "Content-Type");
            context.Response.AddHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            context.Response.AddHeader("Access-Control-Allow-Origin", "*");

            DatabaseManager dbManager = new DatabaseManager();
            dbManager.Connect();
            List<User> users = dbManager.GetUsers();
            dbManager.Close();

            JObject response = new JObject();
            //response.Add("users", Newtonsoft.Json.JsonConvert.SerializeObject(users));
            response["users"] = JToken.FromObject(users);

            //this.SendJsonResponse(context, response);
            this.SendTextResponse(context, response.ToString(), Encoding.UTF8);
        }

        [RESTRoute(Method = HttpMethod.POST, PathInfo = @"^/session$")]
        public void CreateSession(HttpListenerContext context)
        {
            context.Response.AddHeader("Access-Control-Allow-Headers", "Content-Type");
            context.Response.AddHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            context.Response.AddHeader("Access-Control-Allow-Origin", "*");

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

                //this.SendJsonResponse(context, response);
                this.SendTextResponse(context, response.ToString(), Encoding.UTF8);
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

        [RESTRoute(Method = HttpMethod.POST, PathInfo = @"^/session/check$")]
        public void CheckSession(HttpListenerContext context)
        {
            context.Response.AddHeader("Access-Control-Allow-Headers", "Content-Type");
            context.Response.AddHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            context.Response.AddHeader("Access-Control-Allow-Origin", "*");

            JObject jsonPayload = GetJsonPayload(context.Request);

            int sessionId = int.Parse(jsonPayload.GetValue("session_id").ToString());

            if (sessionId != null)
            {
                DatabaseManager dbManager = new DatabaseManager();
                dbManager.Connect();

                if (dbManager.CheckSession(sessionId))
                {
                    context.Response.StatusCode = 200; // 200 OK
                    JObject response = new JObject();
                    //response.Add("users", Newtonsoft.Json.JsonConvert.SerializeObject(users));
                    response["username"] = JToken.FromObject(dbManager.GetUserFromSession(sessionId).username);

                    dbManager.Close();
                    //this.SendJsonResponse(context, response);
                    this.SendTextResponse(context, response.ToString(), Encoding.UTF8);
                }
                else
                {
                    dbManager.Close();
                    context.Response.StatusCode = 401;
                    this.SendTextResponse(context, "Invalid session.");
                }
            }
        }
    }
}
