package com.sgl.db;

import android.content.UriMatcher;
import android.net.Uri;

import com.sgl.db.tables.BillTable;
import com.sgl.db.tables.ConsumerTable;
import com.sgl.db.tables.DisconnectionHistoryTable;
import com.sgl.db.tables.DisconnectionTable;
import com.sgl.db.tables.JobCardTable;
import com.sgl.db.tables.LoginTable;
import com.sgl.db.tables.MeterReadingTable;
import com.sgl.db.tables.NotificationTable;
import com.sgl.db.tables.SequenceTable;
import com.sgl.db.tables.UploadBillHistoryTable;
import com.sgl.db.tables.UploadDisconnectionTable;
import com.sgl.db.tables.UploadsHistoryTable;
import com.sgl.db.tables.UserProfileTable;

public class ContentDescriptor
{

    public static final String AUTHORITY = "com.sgl";
    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);
    public static final UriMatcher URI_MATCHER = buildUriMatcher();

    private static UriMatcher buildUriMatcher()
    {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(AUTHORITY, LoginTable.PATH, LoginTable.PATH_TOKEN);
        matcher.addURI(AUTHORITY, ConsumerTable.PATH, ConsumerTable.PATH_TOKEN);
        matcher.addURI(AUTHORITY, JobCardTable.PATH, JobCardTable.PATH_TOKEN);
        matcher.addURI(AUTHORITY, MeterReadingTable.PATH, MeterReadingTable.PATH_TOKEN);
        matcher.addURI(AUTHORITY, UserProfileTable.PATH, UserProfileTable.PATH_TOKEN);
        matcher.addURI(AUTHORITY, UploadsHistoryTable.PATH, UploadsHistoryTable.PATH_TOKEN);
        matcher.addURI(AUTHORITY, NotificationTable.PATH, NotificationTable.PATH_TOKEN);
        matcher.addURI(AUTHORITY, BillTable.PATH, BillTable.PATH_TOKEN);
        matcher.addURI(AUTHORITY, UploadBillHistoryTable.PATH, UploadBillHistoryTable.PATH_TOKEN);
        matcher.addURI(AUTHORITY, SequenceTable.PATH, SequenceTable.PATH_TOKEN);
        matcher.addURI(AUTHORITY, DisconnectionTable.PATH, DisconnectionTable.PATH_TOKEN);
        matcher.addURI(AUTHORITY, UploadDisconnectionTable.PATH, UploadDisconnectionTable.PATH_TOKEN);
        matcher.addURI(AUTHORITY, DisconnectionHistoryTable.PATH, DisconnectionHistoryTable.PATH_TOKEN);
        matcher.addURI(AUTHORITY, DisconnectionTable.PATH, DisconnectionTable.PATH_TOKEN);
        matcher.addURI(AUTHORITY, UploadDisconnectionTable.PATH, UploadDisconnectionTable.PATH_TOKEN);
        matcher.addURI(AUTHORITY, DisconnectionHistoryTable.PATH, DisconnectionHistoryTable.PATH_TOKEN);

        return matcher;
    }
}