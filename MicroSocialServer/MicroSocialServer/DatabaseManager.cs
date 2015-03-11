using System;
using System.Collections.Generic;
using System.Data.SQLite;
using System.Runtime.InteropServices;
using MicroSocialServer.Schema;

namespace MicroSocialServer
{
    internal class DatabaseManager
    {
        private const string DatabaseFilename = "database.db";
        private SQLiteConnection _databaseConnection;

        public void Connect()
        {
            _databaseConnection = new SQLiteConnection("Data Source=" + DatabaseFilename + ";Version=3;");
            _databaseConnection.Open();
        }

        public void Close()
        {
            if (_databaseConnection != null)
            {
                _databaseConnection.Close();
            }
        }

        // USER MANAGEMENT

        public void AddUser(User user)
        {
            var sqlCommand = String.Format(
                "INSERT INTO Users (username, password_hash, full_name, email) VALUES ('{0}', '{1}', '{2}', '{3}');",
                user.username, user.passwordHash, user.fullName, user.email
                );

            var command = new SQLiteCommand(sqlCommand, _databaseConnection);
            command.ExecuteNonQuery();
        }

        public List<User> GetUsers()
        {
            var users = new List<User>();

            var sqlQuery =
                "SELECT * FROM Users ORDER BY username ASC";

            var command = new SQLiteCommand(sqlQuery, _databaseConnection);
            var reader = command.ExecuteReader();

            while (reader.Read())
            {
                var user = new User();
                user.username = (string) reader["username"];
                user.fullName = (string) reader["full_name"];
                user.email = (string)reader["email"];

                users.Add(user);
            }

            return users;
        }

        public bool CheckPassword(string username, string plaintextPassword)
        {
            var sqlQuery = String.Format(
                "SELECT * FROM Users WHERE username=\"{0}\"",
                username);

            var command = new SQLiteCommand(sqlQuery, _databaseConnection);
            var reader = command.ExecuteReader();

            if (reader.HasRows)
            {
                reader.Read();
                var hashedPassword = (string) reader["password_hash"];

                reader.Close();

                return BCrypt.Net.BCrypt.Verify(plaintextPassword, hashedPassword);
            }
            else
            {
                return false;
            }
        }

        // SESSION MANAGEMENT

        public bool CheckSession(int sessionId)
        {
            var sqlQuery = String.Format(
                "SELECT * FROM Sessions WHERE session_id={0}",
                sessionId);

            var command = new SQLiteCommand(sqlQuery, _databaseConnection);
            var reader = command.ExecuteReader();

            var hasRows = reader.HasRows;
            reader.Close();

            return hasRows;
        }

        public int AddSession(string username)
        {
            var findOldDataQuery = String.Format(
                "SELECT * FROM Sessions WHERE username=\"{0}\"",
                username
                );

            var findCommand = new SQLiteCommand(findOldDataQuery, _databaseConnection);
            var findReader = findCommand.ExecuteReader();

            if (findReader.HasRows)
            {
                var removeOldDataQuery = String.Format(
                    "DELETE FROM Sessions WHERE username=\"{0}\"",
                    username
                    );

                var deleteCommand = new SQLiteCommand(removeOldDataQuery, _databaseConnection);
                deleteCommand.ExecuteNonQuery();
            }

            findReader.Close();

            var sqlCommand = String.Format(
                "INSERT INTO Sessions (username) VALUES ('{0}');",
                username
                );

            var command = new SQLiteCommand(sqlCommand, _databaseConnection);
            var rowsAffected = command.ExecuteNonQuery();

            if (rowsAffected > 0)
            {
                var sqlQuery = String.Format(
                    "SELECT * FROM Sessions WHERE username=\"{0}\"",
                    username);

                var query = new SQLiteCommand(sqlQuery, _databaseConnection);
                var reader = query.ExecuteReader();

                reader.Read();
                var sessionId = reader.GetInt32(1);
                reader.Close();
                //return (int)reader["session_id"];
                return sessionId;
            }
            return 0;
        }

        public void DeleteSession(string username)
        {
            var removeOldDataQuery = String.Format(
                "DELETE FROM Sessions WHERE username=\"{0}\"",
                username
                );

            var deleteCommand = new SQLiteCommand(removeOldDataQuery, _databaseConnection);
            deleteCommand.ExecuteNonQuery();
        }

        public User GetUserFromSession(int session)
        {
            var sql = String.Format(
                "SELECT * FROM Sessions WHERE session_id={0}",
                session);

            var command = new SQLiteCommand(sql, _databaseConnection);
            var reader = command.ExecuteReader();

            if (reader.HasRows)
            {
                reader.Read();
                var user = new User();
                user.username = (string) reader["username"];

                reader.Close();
                return user;
            }
            else
            {
                reader.Close();
                return null;
            }
        }

        // STATUS MANAGEMENT

        public void AddStatus(Status status)
        {
            var sql = String.Format(
                "INSERT INTO Statuses (username, time, status_body) VALUES ('{0}', DATETIME('NOW'), '{1}')",
                status.poster, status.statusContent
            );

            var command = new SQLiteCommand(sql, _databaseConnection);
            command.ExecuteNonQuery();
        }

        public List<Status> GetStatuses(int first, int last)
        {
            var statuses = new List<Status>();

            var sql = String.Format(
                "SELECT * FROM Statuses ORDER BY ID DESC LIMIT {0}",
                first + last + 1
                );

            var command = new SQLiteCommand(sql, _databaseConnection);
            var reader = command.ExecuteReader();

            int i = 0;
            while (reader.Read())
            {
                var newStatus = new Status();
                newStatus.poster = (string) reader["username"];
                newStatus.statusContent = (string) reader["status_body"];
                // TODO - sort out a way to retrieve date time values from the db
                //newStatus.time = DateTime.Parse((string) reader["time"]);
                newStatus.time = (DateTime) reader["time"];

                if (i >= first)
                    statuses.Add(newStatus);

                i++;
            }

            reader.Close();
            return statuses;
        }

        // MESSAGE MANAGEMENT

        public void AddMessage(Message message)
        {
            var sql = String.Format(
                "INSERT INTO Messages (fromUser, toUser, time, message) VALUES ('{0}', '{1}', DATETIME('NOW'), '{2}')",
                message.senderName, message.recipientName, message.messageBody
                );

            var command = new SQLiteCommand(sql, _databaseConnection);
            command.ExecuteNonQuery();
        }

        public List<Message> GetMessages(string user1, string user2, int first, int last)
        {
            var messages = new List<Message>();

            var sql = String.Format(
                //"SELECT * FROM Messages WHERE (from='{0}' AND to='{1}') OR (from='{1}' AND to='{0}') ORDER BY ID DESC LIMIT {2}",
                  //WHERE (from = '{0}' AND to = '{1}')
                  //OR (from = '{1}' AND to = '{0}') 
                @"SELECT *
                  FROM Messages 
                  WHERE (fromUser = '{0}' AND toUser = '{1}')
                  OR (fromUser = '{1}' AND toUser = '{0}') 
                  ORDER BY ID DESC 
                  LIMIT {2}",
                user1, user2, first + last + 1
                );

            var command = new SQLiteCommand(sql, _databaseConnection);
            var reader = command.ExecuteReader();

            int i = 0;
            while (reader.Read())
            {
                var message = new Message();
                message.senderName = (string) reader["fromUser"];
                message.recipientName = (string) reader["toUser"];
                message.time = (DateTime) reader["time"];
                message.messageBody = (string) reader["message"];

                if (i >= first)
                    messages.Add(message);

                i++;
            }

            reader.Close();
            return messages;
        }
    }
}