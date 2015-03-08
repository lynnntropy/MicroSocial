using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using System.Data.SQLite;

namespace MicroSocialServer
{
    
    class DatabaseManager
    {
        static string databaseFilename = "database.db";
        SQLiteConnection databaseConnection;

        public DatabaseManager()
        {

        }

        public void Connect()
        {
            this.databaseConnection = new SQLiteConnection("Data Source=" + databaseFilename + ";Version=3;");
            this.databaseConnection.Open();
        }

        public void Close()
        {
            if (this.databaseConnection != null)
            {
                this.databaseConnection.Close();
            }
        }

        public void AddUser(string username, string passwordHash)
        {
            string sqlCommand = String.Format(
                "INSERT INTO Users VALUES ('{0}', '{1}');",
                username, passwordHash
                );

            SQLiteCommand command = new SQLiteCommand(sqlCommand, databaseConnection);
            int rowsAffected = command.ExecuteNonQuery();
        }
    }
}
