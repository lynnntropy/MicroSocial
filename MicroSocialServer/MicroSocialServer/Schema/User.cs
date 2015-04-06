using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using Newtonsoft.Json;

namespace MicroSocialServer.Schema
{
    class User
    {
        public string username;

        public string fullName;
        public string email;

        [JsonIgnore]
        public string passwordHash;

        public User()
        {
            
        }

        public User(string username)
        {
            this.username = username;
        }
    }
}
