package com.example.imageencriptionanddecription;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.imageencriptionanddecription.Model.Image;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.ArrayList;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "EncryptedImages.db";
    private static final int DATABASE_VERSION = 1;
    private Dao<Image, String> imageDao;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Image.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Image.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Dao<Image, String> getImageDao() throws SQLException {
        if (imageDao == null) {
            imageDao = DaoManager.createDao(getConnectionSource(), Image.class);
        }
        return imageDao;
    }

    public ArrayList<Image> getImages() {
        try {
            return (ArrayList<Image>) getImageDao().queryForAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
