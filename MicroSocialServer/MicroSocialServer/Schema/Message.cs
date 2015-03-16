using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MicroSocialServer.Schema
{
    class Message
    {
        public string messageBody;
        public DateTime time;
        public string senderName;
        public string recipientName;

        public string senderEmail;

        public Message(string messageBody, DateTime time, string senderName, string recipientName)
        {
            this.messageBody = messageBody;
            this.time = time;
            this.senderName = senderName;
            this.recipientName = recipientName;
        }

        public Message()
        {
        }
    }
}
