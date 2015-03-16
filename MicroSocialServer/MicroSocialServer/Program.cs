using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;

using Grapevine.Server;
using MicroSocialServer.Socket;
using WebSocketSharp.Server;

namespace MicroSocialServer
{
    class Program
    {
        static void Main(string[] args)
        {
            var server = new RESTServer();
            server.Port = "9000";
            server.Host = "*";
            server.Start();

            var socketServer = new WebSocketServer(9001);
            socketServer.AddWebSocketService<Socket.Chat>("/chat");
            socketServer.Start();

            while (server.IsListening)
            {
                System.Threading.Thread.Sleep(300);
            }

            Console.WriteLine("Server stopped.");
            Console.WriteLine("Press Enter to Continue...");
            Console.ReadLine();
        }
    }
}
