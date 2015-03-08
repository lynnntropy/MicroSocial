using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using Grapevine.Server;

namespace MicroSocialServer
{
    class Program
    {
        static void Main(string[] args)
        {
            var server = new RESTServer(port: "9000");
            server.Start();

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
