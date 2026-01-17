package com.example.musicplayer.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface UserDao {
    @Insert
    void insert(User user);

    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    User login(String username, String password);

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    User checkUsername(String username);

    @Update
    void update(User user);

    @Query("SELECT * FROM users WHERE uid = :uid LIMIT 1")
    User getUserById(int uid);
}
