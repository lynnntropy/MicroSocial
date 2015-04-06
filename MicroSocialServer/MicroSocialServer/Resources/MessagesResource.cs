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
    public sealed class MessagesResource : RESTResource
    {
        [RESTRoute(Method = HttpMethod.POST, PathInfo = @"^/message$")]
        public void SendMessage(HttpListenerContext context)
        {
            context.Response.AddHeader("Access-Control-Allow-Headers", "Content-Type");
            context.Response.AddHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            context.Response.AddHeader("Access-Control-Allow-Origin", "*");

            var jsonPayload = GetJsonPayload(context.Request);
            var sessionId = int.Parse(jsonPayload.GetValue("session_id").ToString());

            var dbManager = new DatabaseManager();
            dbManager.Connect();

            if (dbManager.CheckSession(sessionId))
            {
                //var from = jsonPayload.GetValue("from").ToString();
                var from = dbManager.GetUserFromSession(sessionId).username;
                var to = jsonPayload.GetValue("to").ToString();
                var messageBody = jsonPayload.GetValue("message").ToString();

                var message = new Message(messageBody, DateTime.Now, from, to);
                dbManager.AddMessage(message);

                foreach (Socket.Chat session in Socket.Chat.Chats)
                {
                    // send the message to the other user directly
                    // if they are currently connected

                    if (session.user1 == message.recipientName)
                    {
                        if (session.user2 == message.senderName)
                        {
                            session.ReceiveMessage(message.messageBody);
                            break;
                        }
                    }
                }

                dbManager.Close();
                this.SendTextResponse(context, "OK");
            }
            else
            {
                dbManager.Close();
                context.Response.StatusCode = 401;
                this.SendTextResponse(context, "Nope.");
            }
        }

        [RESTRoute(Method = HttpMethod.GET, PathInfo = @"^/messages\?session=\S+&user=\S+&first=\d+&last=\d+$")]
        public void GetMessages(HttpListenerContext context)
        {
            context.Response.AddHeader("Access-Control-Allow-Headers", "Content-Type");
            context.Response.AddHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            context.Response.AddHeader("Access-Control-Allow-Origin", "*");

            // we know the GET parameters were passed because otherwise the regex wouldn't match

            //var jsonPayload = GetJsonPayload(context.Request);
            //var sessionId = int.Parse(jsonPayload.GetValue("session_id").ToString());

            var dbManager = new DatabaseManager();
            dbManager.Connect();

            
            //string regex = @"^/messages\?first=(?<first>\d+)&last=(?<last>\d+)$";
            var regex =
                @"^/messages\?session=(?<session>\S+)&user=(?<user>\S+)&first=(?<first>\d+)&last=(?<last>\d+)$";

            Match match = Regex.Match(context.Request.RawUrl, regex);
            if (match.Success)
            {
                var session = int.Parse(match.Groups["session"].Value);
                    
                if (dbManager.CheckSession(session))
                {
                    var user1 = dbManager.GetUserFromSession(session).username;
                    var user2 = match.Groups["user"].Value;
                    int first = int.Parse(match.Groups["first"].Value);
                    int last = int.Parse(match.Groups["last"].Value);

                    var messages = dbManager.GetMessages(user1, user2, first, last);

                    var json = new JObject();
                    json["user1"] = JToken.FromObject(user1);
                    json["user2"] = JToken.FromObject(user2);
                    json["first"] = JToken.FromObject(first);
                    json["last"] = JToken.FromObject(last);
                    json["messages"] = JToken.FromObject(messages);

                    dbManager.Close();

                    

                    //json.ToString()
                    this.SendTextResponse(context, json.ToString(), Encoding.UTF8);

                    //context.Response.ContentEncoding = Encoding.UTF8;
                    //this.SendJsonResponse(context, json);
                }
                else
                {
                    dbManager.Close();
                    context.Response.StatusCode = 401;
                    this.SendTextResponse(context, "Nope.");
                }
            }
            else
            {
                dbManager.Close();
                this.SendTextResponse(context,
                    "Grapevine dun goofed if you got this far..");
            }
        }

        [RESTRoute(Method = HttpMethod.GET, PathInfo = @"^/newestMessages\?session=\S+&first=\d+&last=\d+$")]
        public void GetNewestMessages(HttpListenerContext context)
        {
            context.Response.AddHeader("Access-Control-Allow-Headers", "Content-Type");
            context.Response.AddHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            context.Response.AddHeader("Access-Control-Allow-Origin", "*");

            var dbManager = new DatabaseManager();
            dbManager.Connect();

            var regex =
                @"^/newestMessages\?session=(?<session>\S+)&first=(?<first>\d+)&last=(?<last>\d+)$";

            Match match = Regex.Match(context.Request.RawUrl, regex);
            if (match.Success)
            {
                var session = int.Parse(match.Groups["session"].Value);

                if (dbManager.CheckSession(session))
                {
                    var requestingUser = dbManager.GetUserFromSession(session).username;
                    int first = int.Parse(match.Groups["first"].Value);
                    int last = int.Parse(match.Groups["last"].Value);

                    var messages = dbManager.GetLatestMessages(requestingUser, first, last);

                    var json = new JObject();
                    json["user"] = JToken.FromObject(requestingUser);
                    json["first"] = JToken.FromObject(first);
                    json["last"] = JToken.FromObject(last);
                    json["messages"] = JToken.FromObject(messages);

                    dbManager.Close();
                    this.SendTextResponse(context, json.ToString(), Encoding.UTF8);
                }
                else
                {
                    dbManager.Close();
                    context.Response.StatusCode = 401;
                    this.SendTextResponse(context, "Nope.");
                }
            }
            else
            {
                dbManager.Close();
                this.SendTextResponse(context,
                    "Newest messages: Grapevine dun goofed if you got this far..");
            }
        }
    }
}
