using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using MicroSocialServer.Schema;
using WebSocketSharp;
using WebSocketSharp.Server;

namespace MicroSocialServer.Socket
{
    class Chat : WebSocketBehavior
    {
        private static readonly List<Chat> chats = new List<Chat>();

        private bool authenticated = false;
        public string user1;
        public string user2;

        public static List<Chat> Chats
        {
            get { return chats; }
        }

        public Chat()
        {
            chats.Add(this);
        }

        public Chat(string user1, string user2)
        {
            chats.Add(this);

            this.user1 = user1;
            this.user2 = user2;
        }

        protected override void OnMessage(MessageEventArgs e)
        {
            var message = e.Data;

            if (authenticated)
            {
                // send the message to the other user directly
                // if they are currently connected

                foreach (Chat session in Chats)
                {
                    if (session.user1 == this.user2)
                    {
                        session.ReceiveMessage(message);
                    }
                }

                // add the message to the database

                var databaseManager = new DatabaseManager();
                databaseManager.Connect();
                databaseManager.AddMessage(new Message(message, DateTime.Now, this.user1, this.user2));
                databaseManager.Close();
            }
            else if (user2 == null)
            {
                this.user2 = message;
                this.Send(string.Format("Opened chat session with {0}.", user2));
            }
            else
            {
                var sessionId = int.Parse(message);

                var databaseManager = new DatabaseManager();
                databaseManager.Connect();

                if (databaseManager.CheckSession(sessionId))
                {
                    this.user1 = databaseManager.GetUserFromSession(sessionId).username;
                    this.Send(string.Format("Authenticated successfully as {0}.", user1));
                    this.authenticated = true;
                }
                else
                {
                    this.Send("Access denied.");
                }

                databaseManager.Close();
            }
        }

        public void ReceiveMessage(string message)
        {
            if (authenticated)
            {
                this.Send(message);
            }
            
        }

        protected override void OnClose(CloseEventArgs e)
        {
            Chats.Remove(this);
            base.OnClose(e);
        }
    }
}
