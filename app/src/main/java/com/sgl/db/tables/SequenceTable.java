package com.sgl.db.tables;

import android.net.Uri;

import com.sgl.db.ContentDescriptor;

/**
 * Created by Admin on 08-07-2017.
 */

public class SequenceTable{

    public static final String TABLE_NAME = "SequenceTable";
    public static final String PATH = "SEQUENCE_TABLE";
    public static final int PATH_TOKEN = 25;
    public static final Uri CONTENT_URI = ContentDescriptor.BASE_URI.buildUpon().appendPath(PATH).build();
    /**
     * This class contains Constants to describe name of Columns of Consumer Table
     *
     * @author Bynry01
     */
    public static class Cols
    {
        public static final String ID = "_id";
        public static final String ZONE_CODE = "zone_code";
        public static final String CYCLE_CODE = "cycle_code";
        public static final String BINDER_CODE = "binder_code";
        public static final String METER_READER_ID = "meter_reader_id";
        public static final String SEQUENCE = "sequence";
    }
}
