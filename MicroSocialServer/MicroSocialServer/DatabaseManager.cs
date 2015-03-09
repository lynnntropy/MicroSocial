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

            reader.Close();

            return BCrypt.Net.BCrypt.Verify(plaintextPassword, hashedPassword);
        }

        public bool CheckSession(string username, int sessionId)
        {
            string sqlQuery = String.Format(
                "SELECT * FROM Sessions WHERE username=\"{0}\" AND session_id={1}",
                username, sessionId);

            SQLiteCommand command = new SQLiteCommand(sqlQuery, databaseConnection);
            SQLiteDataReader reader = command.ExecuteReader();

            bool hasRows = reader.HasRows;
            reader.Close();

            return hasRows;
        }

        public int AddSession(string username)
        {
            string findOldDataQuery = String.Format(
                "SELECT * FROM Sessions WHERE username=\"{0}\"",
                username
                );

            SQLiteCommand findCommand = new SQLiteCommand(findOldDataQuery, databaseConnection);
            var findReader = findCommand.ExecuteReader();

            if (findReader.HasRows)
            {
                string removeOldDataQuery = String.Format(
                    "DELETE FROM Sessions WHERE username=\"{0}\"",
                    username
                    );

                SQLiteCommand deleteCommand = new SQLiteCommand(removeOldDataQuery, databaseConnection);
                deleteCommand.ExecuteNonQuery();
            }

            findReader.Close();

            string sqlCommand = String.Format(
                "INSERT INTO Sessions (username) VALUES ('{0}');",
                username
                );

            SQLiteCommand command = new SQLiteCommand(sqlCommand, databaseConnection);
            int rowsAffected = command.ExecuteNonQuery();

            if (rowsAffected > 0)
            {
                string sqlQuery = String.Format(
                    "SELECT * FROM Sessions WHERE username=\"{0}\"",
                    username);

                SQLiteCommand query = new SQLiteCommand(sqlQuery, databaseConnection);
                SQLiteDataReader reader = query.ExecuteReader();

                reader.Read();
                int sessionId = reader.GetInt32(1);
                reader.Close();
                //return (int)reader["session_id"];
                return sessionId;
            }
            else
            {
                return 0;
            }
        }

        public void DeleteSession(string username)
        {
            string removeOldDataQuery = String.Format(
                    "DELETE FROM Sessions WHERE username=\"{0}\"",
                    username
                    );

            SQLiteCommand deleteCommand = new SQLiteCommand(removeOldDataQuery, databaseConnection);
            deleteCommand.ExecuteNonQuery();
        }
    }
}
