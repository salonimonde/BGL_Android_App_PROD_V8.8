package com.sgl.db.tables;

import android.net.Uri;

import com.sgl.db.ContentDescriptor;

/**
 * Created by User on 25-02-2017.
 */

public class NotificationTable
{
    public static final String TABLE_NAME = "Notification";
    public static final String PATH = "Notification_TABLE";
    public static final int PATH_TOKEN = 1;
    public static final Uri CONTENT_URI = ContentDescriptor.BASE_URI.buildUpon().appendPath(PATH).build();

    public static class Cols
    {
        public static final String ID = "_id";
        public static final String MSG = "msg";
        public static final String TITLE = "title";
        public static final String DATE = "date";
        public static final String IS_READ = "is_read";
        public static final String METER_READER_ID = "meter_reader_id";
    }
}
