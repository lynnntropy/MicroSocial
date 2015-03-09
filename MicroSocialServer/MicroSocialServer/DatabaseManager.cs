using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using System.Data.SQLite;

using MicroSocialServer.Schema;

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

        public List<User> GetUsers()
        {
            List<User> users = new List<User>();

            string sqlQuery =
                "SELECT * FROM Users ORDER BY username ASC";

            SQLiteCommand command = new SQLiteCommand(sqlQuery, databaseConnection);
            SQLiteDataReader reader = command.ExecuteReader();

            while (reader.Read())
            {
                users.Add(new User((string)reader["username"]));
            }



            return users;
        }

        public bool CheckPassword(string username, string plaintextPassword)
        {
            string sqlQuery = String.Format(
                "SELECT * FROM Users WHERE username=\"{0}\"",
                username);

            SQLiteCommand command = new SQLiteCommand(sqlQuery, databaseConnection);
            SQLiteDataReader reader = command.ExecuteReader();

            reader.Read();
            string hashedPassword = (string)reader["password_hash"];

            return BCrypt.Net.BCrypt.Verify(plaintextPassword, hashedPassword);
        }

    }
}
