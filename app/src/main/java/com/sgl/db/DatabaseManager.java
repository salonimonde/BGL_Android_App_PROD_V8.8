package com.sgl.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.sgl.R;
import com.sgl.activity.LoginActivity;
import com.sgl.configuration.AppConstants;
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
import com.sgl.models.BillCard;
import com.sgl.models.Consumer;
import com.sgl.models.Disconnection;
import com.sgl.models.DisconnectionHistory;
import com.sgl.models.HistoryCard;
import com.sgl.models.JobCard;
import com.sgl.models.MeterImage;
import com.sgl.models.MeterReading;
import com.sgl.models.NotificationCard;
import com.sgl.models.PendingCount;
import com.sgl.models.Sequence;
import com.sgl.models.SummaryCard;
import com.sgl.models.SummaryCount;
import com.sgl.models.UploadBillHistory;
import com.sgl.models.UploadDisconnectionNotices;
import com.sgl.models.UploadsHistory;
import com.sgl.models.User;
import com.sgl.models.UserProfile;
import com.sgl.utils.CommonUtils;
import com.sgl.utils.LocationManagerReceiver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * This class acts as an interface between database and UI. It contains all the
 * methods to interact with device database.
 *
 * @author Bynry01
 */
public class DatabaseManager {
    /**
     * Save User to UserLogin table
     *
     * @param context Context
     * @param user    User
     */
    public static void saveUser(Context context, User user) {
        if (user != null) {
            ContentValues values = getContentValuesUserLoginTable(user);
            String condition = LoginTable.Cols.USER_LOGIN_ID + "='" + user.userid + "'";
            saveValues(context, LoginTable.CONTENT_URI, values, condition);
        }
    }

    /**
     * Save User to UserProfileTable
     *
     * @param context
     * @param userProfiles
     */
    private static void saveUserProfile(Context context, String user_email, ArrayList<UserProfile> userProfiles) {
        if (userProfiles != null && userProfiles.size() > 0) {
            for (UserProfile userProfile : userProfiles) {
                userProfile.email_id = user_email;
                ContentValues values = getContentValuesUserProfileTable(userProfile);
                String condition = UserProfileTable.Cols.METER_READER_ID + "='" + userProfile.meter_reader_id + "'";
                saveValues(context, UserProfileTable.CONTENT_URI, values, condition);
                SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = df.format(new Date());
                User user = new User();
                user.userid = user_email;
                user.meter_reader_id = userProfile.meter_reader_id;

                user.login_date = date;
                LocationManagerReceiver receiver = new LocationManagerReceiver(context);
                receiver.saveLoginDetailsWithLocation(context, user);
            }
        }
    }

    /**
     * Save Consumer to ConsumerTable
     *
     * @param context  Context
     * @param consumer Consumer
     */
    public static void saveUnBillConsumer(Context context, Consumer consumer) {
        if (consumer != null) {
            ContentValues values = getContentValuesConsumerTable(consumer);
            String condition = ConsumerTable.Cols.CONSUMER_ID + "='" + consumer.consumer_no + "'";
            saveValues(context, ConsumerTable.CONTENT_URI, values, condition);
        }
    }

    /**
     * Save UploadsHistory to UploadsHistoryTable
     *
     * @param context        Context
     * @param uploadsHistory uploadsHistory
     */

    public static void saveUploadsHistory(Context context, UploadsHistory uploadsHistory) {
        if (uploadsHistory != null) {
            ContentValues values = getContentValuesUploadsHistoryTable(uploadsHistory);
            //Changes for count Error Starts Avinesh:02-03-17
            String condition = UploadsHistoryTable.Cols.CONSUMER_ID + "='" + uploadsHistory.consumer_no + "' AND "
                    + UploadsHistoryTable.Cols.UPLOAD_STATUS + "='" + uploadsHistory.upload_status + "' AND "
                    + UploadsHistoryTable.Cols.METER_READER_ID + "='" + uploadsHistory.meter_reader_id + "'";
            //Changes for count Error Ends Avinesh:02-03-17
            saveValues(context, UploadsHistoryTable.CONTENT_URI, values, condition);
        }
    }

    public static void saveUploadBillHistory(Context context, UploadBillHistory uploadsHistory) {
        if (uploadsHistory != null) {
            ContentValues values = getContentValuesUploadBillHistoryTable(uploadsHistory);
            //Changes for count Error Starts Avinesh:02-03-17
            String condition = UploadBillHistoryTable.Cols.JOBCARD_ID + "='" + uploadsHistory.jobcard_id + "' AND "
                    + UploadBillHistoryTable.Cols.METER_READER_ID + "='" + uploadsHistory.meter_reader_id + "'";
            //Changes for count Error Ends Avinesh:02-03-17
            saveValues(context, UploadBillHistoryTable.CONTENT_URI, values, condition);
        }
    }

    private static ContentValues getContentValuesUploadBillHistoryTable(UploadBillHistory uploadsHistory) {
        ContentValues values = new ContentValues();
        try {
            values.put(UploadBillHistoryTable.Cols.JOBCARD_ID, uploadsHistory.jobcard_id != null ? uploadsHistory.jobcard_id : "");
            values.put(UploadBillHistoryTable.Cols.CONSUMER_ASSIGNED, uploadsHistory.consumer_assigned != null ? uploadsHistory.consumer_assigned : "");
            values.put(UploadBillHistoryTable.Cols.CYCLE_CODE, uploadsHistory.cycle_code != null ? uploadsHistory.cycle_code : "");
            values.put(UploadBillHistoryTable.Cols.BILLMONTH, uploadsHistory.billmonth != null ? uploadsHistory.billmonth : "");
            values.put(UploadBillHistoryTable.Cols.BINDER_CODE, uploadsHistory.binder_code != null ? uploadsHistory.binder_code : "");
            values.put(UploadBillHistoryTable.Cols.READING_DATE, uploadsHistory.reading_date != null ? uploadsHistory.reading_date : "");
            values.put(UploadBillHistoryTable.Cols.METER_READER_ID, uploadsHistory.meter_reader_id != null ? uploadsHistory.meter_reader_id : "");
            values.put(UploadBillHistoryTable.Cols.DISTRIBUTED, uploadsHistory.distributed != null ? uploadsHistory.distributed : "");
            values.put(UploadBillHistoryTable.Cols.SUBDIVISION, uploadsHistory.subdivision_name != null ? uploadsHistory.subdivision_name : "");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }


    /**
     * Save User to JobCardTable
     *
     * @param context Context
     * @param jobCard JobCard
     */
    public static void saveJobCard(Context context, JobCard jobCard) {
        if (jobCard != null) {
            ContentValues values = getContentValuesJobCardTable(jobCard);
            String condition = JobCardTable.Cols.JOB_CARD_ID + "='" + jobCard.job_card_id + "'";
            saveValues(context, JobCardTable.CONTENT_URI, values, condition);
        }
    }

    /**
     * Save User to JobCardTable
     *
     * @param context Context
     * @param jobCard JobCard
     */
    public static void saveJobCardStatus(Context context, JobCard jobCard, String status) {
        if (jobCard != null) {
            ContentValues values = getContentValuesJobCardTable(jobCard);
            values.put(JobCardTable.Cols.JOB_CARD_STATUS, status);
            // values.put(JobCardTable.Cols.IS_REVISIT, "False");
            String condition = JobCardTable.Cols.JOB_CARD_ID + "='" + jobCard.job_card_id + "'";
            saveValues(context, JobCardTable.CONTENT_URI, values, condition);
        }
    }

    public static PendingCount getPendingCount(String meterReaderId) {
        PendingCount pendingCount = null;
        SQLiteDatabase db = DatabaseProvider.dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select\n" +
                "    count(*) total_jobs,\n" +
                "    sum(case when " + JobCardTable.Cols.JOB_CARD_STATUS + "= 'COMPLETED' AND " + JobCardTable.Cols.IS_REVISIT + "='False' then 1 else 0 end) normalPending,\n" +
                "    sum(case when " + JobCardTable.Cols.JOB_CARD_STATUS + "= 'COMPLETED' AND " + JobCardTable.Cols.IS_REVISIT + "= 'True' then 1 else 0 end) revisitPending,\n" +
                "    t1.unBillPending As unBillPending\n" +
                "    from " + JobCardTable.TABLE_NAME + " t,(select count(*) As unBillPending from " + ConsumerTable.TABLE_NAME + ") t1 where t." + JobCardTable.Cols.METER_READER_ID + "='" + meterReaderId + "'", null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            pendingCount = new PendingCount();
            pendingCount.normalPending = cursor.getInt(cursor.getColumnIndex("normalPending"));
            pendingCount.revisitPending = cursor.getInt(cursor.getColumnIndex("revisitPending"));
            pendingCount.unBillPending = cursor.getInt(cursor.getColumnIndex("unBillPending"));
        }
        if (cursor != null) {
            cursor.close();
        }
        if (db.isOpen()) {
            db.close();
        }
        return pendingCount;
    }

    public static void saveReadingAttempts(Context context, JobCard jobCard, String readingAttempts, String plReadingAttempts) {
        if (jobCard != null) {
            ContentValues values = getContentValuesJobCardTable(jobCard);
            values.put(JobCardTable.Cols.DOOR_LOCK_READING_ATTEMPT, readingAttempts);
            values.put(JobCardTable.Cols.PERMANENTLY_LOCK_READING_ATTEMPT, plReadingAttempts);
            String condition = JobCardTable.Cols.JOB_CARD_ID + "='" + jobCard.job_card_id + "'";
            saveValues(context, JobCardTable.CONTENT_URI, values, condition);
        }
    }

    public static void saveBillCardStatus(Context context, BillCard billCard, String status) {
        if (billCard != null) {
            ContentValues values = getContentValuesBillCardTable(billCard);
            values.put(BillTable.Cols.JOBCARD_STATUS, status);
            String condition = BillTable.Cols.JOB_CARD_ID + "='" + billCard.jobcard_id + "'";
            saveValues(context, BillTable.CONTENT_URI, values, condition);
        }
    }

    // Method for Save MeterReading Starts Avinesh:02-03-17
    public static void saveMeterReadingRNT(Context context, MeterReading meterReading) {
        if (meterReading != null) {
            ContentValues values = getContentValuesMeterReadingTable(meterReading);
            saveMeterReadingsRNT(context, MeterReadingTable.CONTENT_URI, values);
        }
    }

    private static void saveMeterReadingsRNT(Context context, Uri table, ContentValues values) {
        ContentResolver resolver = context.getContentResolver();
        try {
            resolver.insert(table, values);
        } catch (Exception e) {
            Toast.makeText(context, "Failed to save reading", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Save User to JobCardTable
     *
     * @param context      Context
     * @param meterReading MeterReading
     */
    public static void saveMeterReading(Context context, MeterReading meterReading) {
        if (meterReading != null) {
            ContentValues values = getContentValuesMeterReadingTable(meterReading);
            String condition = MeterReadingTable.Cols.JOB_CARD_ID + "='" + meterReading.job_card_id + "' AND "
                    + MeterReadingTable.Cols.METER_READER_ID + "='" + meterReading.meter_reader_id + "'";
            saveMeterReadings(context, MeterReadingTable.CONTENT_URI, values, condition);
        }
    }

    private static void saveMeterReadings(Context context, Uri table, ContentValues values, String condition) {
        ContentResolver resolver = context.getContentResolver();

        Cursor cursor = resolver.query(table, null,
                condition, null, null);
        try {
            if (cursor != null && cursor.getCount() > 0) {
                resolver.update(table, values, condition, null);
                Toast.makeText(context, "Reading punched successfully.", Toast.LENGTH_LONG).show();
            } else {
                resolver.insert(table, values);
                Toast.makeText(context, "Reading punched successfully.", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, "Failed to save reading", Toast.LENGTH_LONG).show();
        }
        if (cursor != null) {
            cursor.close();
        }


       /* try {
            resolver.insert(table, values);
            Toast.makeText(context, "Reading punched successfully.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(context, "Failed to save reading", Toast.LENGTH_LONG).show();
        }*/
    }

    private static void saveValues(Context context, Uri table, ContentValues values, String condition) {
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(table, null,
                condition, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            resolver.update(table, values, condition, null);
        } else {
            resolver.insert(table, values);
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    public static int deleteUploadsHistory(Context context, String mr) {
        String condition = CommonUtils.getPreviousDateCondition(mr);
        ContentResolver resolver = context.getContentResolver();
        int deleted = resolver.delete(UploadsHistoryTable.CONTENT_URI, condition, null);
        return deleted;
    }

    public static int deleteUploadsBillHistory(Context context, String mr) {
        String condition = CommonUtils.getPreviousDateConditionBill(mr);
        ContentResolver resolver = context.getContentResolver();
        int deleted = resolver.delete(UploadBillHistoryTable.CONTENT_URI, condition, null);
        return deleted;
    }

    public static ArrayList<String> getUploadsHistoryRoutes(Context context, String date) {
        ArrayList<String> routes = null;
        String condition = UploadsHistoryTable.Cols.READING_DATE + "='" + date + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(UploadsHistoryTable.CONTENT_URI, new String[]{"DISTINCT " + UploadsHistoryTable.Cols.ROUTE_ID},
                condition, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            routes = new ArrayList<>();
            while (!cursor.isAfterLast()) {
                String route_code = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.ROUTE_ID));
                routes.add(route_code);
                cursor.moveToNext();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return routes;
    }

    public static ArrayList<UploadsHistory> getUploadsHistory(Context context, String date, String routeId, String meterReaderId) {
        String condition = UploadsHistoryTable.Cols.READING_DATE + "='" + date + "' AND "
                + UploadsHistoryTable.Cols.ROUTE_ID + "='" + routeId + "' AND "
                + UploadsHistoryTable.Cols.METER_READER_ID + "='" + meterReaderId + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(UploadsHistoryTable.CONTENT_URI, null,
                condition, null, null);
        ArrayList<UploadsHistory> uploadsHistoryList = getUploadsHistoryFromCursor(cursor);
        if (cursor != null) {
            cursor.close();
        }
        return uploadsHistoryList;
    }

    public static HistoryCard getUploadsHistoryCounts(Context context, String meterReaderId) {
        HistoryCard lHistoryCard = new HistoryCard();
        ContentResolver resolver = context.getContentResolver();
        String condition = UploadsHistoryTable.Cols.UPLOAD_STATUS + "='" + context.getString(R.string.addnewconsumer) + "' AND "
                + UploadsHistoryTable.Cols.METER_READER_ID + "='" + meterReaderId + "'";
        Cursor cursor = resolver.query(UploadsHistoryTable.CONTENT_URI, null, condition, null, null);
        ArrayList<UploadsHistory> uploadsHistoryList = getUploadsHistoryFromCursor(cursor);
        if (uploadsHistoryList != null) {
            lHistoryCard.unbill = uploadsHistoryList.size();
        } else {
            lHistoryCard.unbill = 0;
        }
        condition = UploadsHistoryTable.Cols.UPLOAD_STATUS + "='" + context.getString(R.string.meter_status_normal) + "' AND "
                + UploadsHistoryTable.Cols.METER_READER_ID + "='" + meterReaderId + "'";
        cursor = resolver.query(UploadsHistoryTable.CONTENT_URI, null, condition, null, null);
        uploadsHistoryList = getUploadsHistoryFromCursor(cursor);
        if (uploadsHistoryList != null) {
            lHistoryCard.open = uploadsHistoryList.size();
        } else {
            lHistoryCard.open = 0;
        }
        condition = UploadsHistoryTable.Cols.UPLOAD_STATUS + "='" + context.getString(R.string.revisit) + "' AND "
                + UploadsHistoryTable.Cols.METER_READER_ID + "='" + meterReaderId + "'";
        cursor = resolver.query(UploadsHistoryTable.CONTENT_URI, null, condition, null, null);
        uploadsHistoryList = getUploadsHistoryFromCursor(cursor);
        if (uploadsHistoryList != null) {
            lHistoryCard.revisit = uploadsHistoryList.size();
        } else {
            lHistoryCard.revisit = 0;
        }

        if (cursor != null) {
            cursor.close();
        }
        return lHistoryCard;
    }

    public static ArrayList<SummaryCard> getSummaryCard(Context context, String readerId) {
        ArrayList<SummaryCard> summaryCardArrayList = new ArrayList<>();

        ArrayList<String> routes = getTotalRoutes(context, readerId);
        String route_id = "";
        String billCycle = "";
        if (routes != null)
            for (int i = 0; i < routes.size(); i++) {
                SummaryCard lSummaryCard = new SummaryCard();
                route_id = routes.get(i);
                ArrayList<String> bill = getTotalBillCycleCode(context, readerId, route_id);
                billCycle = bill.get(0);

                lSummaryCard.route_id = route_id;
                lSummaryCard.bill_cycle_code = billCycle;

                //calculate revisit job cards inside single route
                String conditionTotal = JobCardTable.Cols.METER_READER_ID + "='" + readerId + "' AND "
                        + JobCardTable.Cols.ROUTE_ID + "='" + route_id + "'";

                ContentResolver resolver = context.getContentResolver();
                Cursor cursorTotal = resolver.query(JobCardTable.CONTENT_URI, null,
                        conditionTotal, null, null);

                if (cursorTotal != null) {
                    lSummaryCard.total = cursorTotal.getCount();
                } else {
                    lSummaryCard.total = 0;
                }
                if (cursorTotal != null) {
                    cursorTotal.close();
                }

                //calculate Open job cards inside single route
                String conditionOpen = JobCardTable.Cols.METER_READER_ID + "='" + readerId + "' AND "
                        + JobCardTable.Cols.ROUTE_ID + "='" + route_id + "' AND "
                        + JobCardTable.Cols.JOB_CARD_STATUS + "='" + AppConstants.JOB_CARD_STATUS_ALLOCATED + "' AND "
                        + JobCardTable.Cols.IS_REVISIT + "='False'";

                Cursor cursorOpen = resolver.query(JobCardTable.CONTENT_URI, null,
                        conditionOpen, null, null);

                if (cursorOpen != null) {
                    lSummaryCard.open = cursorOpen.getCount();
                } else {
                    lSummaryCard.open = 0;
                }
                if (cursorOpen != null) {
                    cursorOpen.close();
                }

                //calculate Revisit job cards inside single route
                String conditionRevisit = JobCardTable.Cols.METER_READER_ID + "='" + readerId + "' AND "
                        + JobCardTable.Cols.ROUTE_ID + "='" + route_id + "' AND "
                        + JobCardTable.Cols.JOB_CARD_STATUS + "='" + AppConstants.JOB_CARD_STATUS_ALLOCATED + "' AND "
                        + JobCardTable.Cols.IS_REVISIT + "='True'";

                Cursor cursorRevisit = resolver.query(JobCardTable.CONTENT_URI, null,
                        conditionRevisit, null, null);

                if (conditionRevisit != null) {
                    lSummaryCard.revisit = cursorRevisit.getCount();
                } else {
                    lSummaryCard.revisit = 0;
                }
                if (conditionRevisit != null) {
                    cursorRevisit.close();
                }

                //calculate open job cards inside single route
                lSummaryCard.open = lSummaryCard.open + lSummaryCard.revisit;

                //calculate completed job cards inside single route
                lSummaryCard.completed = lSummaryCard.total - lSummaryCard.open;

                summaryCardArrayList.add(lSummaryCard);
            }

        return summaryCardArrayList;
    }

    /**
     * @param meter_reader_id
     */
    public static SummaryCount getSummary(String meter_reader_id) {
        SummaryCount summaryCount = null;
        SQLiteDatabase db = DatabaseProvider.dbHelper.getReadableDatabase();

        String sql = "select  count(*) total_jobs," +
                " sum(case when " + JobCardTable.Cols.JOB_CARD_STATUS + "= '" + AppConstants.JOB_CARD_STATUS_ALLOCATED + "' AND " + JobCardTable.Cols.IS_REVISIT + "='False' then 1 else 0 end) open," +
                " sum(case when " + JobCardTable.Cols.JOB_CARD_STATUS + "= '" + AppConstants.JOB_CARD_STATUS_ALLOCATED + "' AND " + JobCardTable.Cols.IS_REVISIT + "= 'True' then 1 else 0 end) revisit, " +
                "(select x.pendingUpload As pendingUpload " +
                "from (select count(*)+(select count(*)  " +
                "from " + ConsumerTable.TABLE_NAME + " where " + ConsumerTable.Cols.METER_READER_ID + "='" + meter_reader_id + "') As pendingUpload " +
                "from (select  distinct " + MeterReadingTable.Cols.JOB_CARD_ID + " " +
                "from " + MeterReadingTable.TABLE_NAME + " where " + ConsumerTable.Cols.METER_READER_ID + "='" + meter_reader_id + "' )) x) As pendingUpload " +
                "from " + JobCardTable.TABLE_NAME + " t  where t." + JobCardTable.Cols.METER_READER_ID + "='" + meter_reader_id + "'";

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            summaryCount = new SummaryCount();
            summaryCount.total_jobs = cursor.getInt(cursor.getColumnIndex("total_jobs"));
            summaryCount.open = cursor.getInt(cursor.getColumnIndex("open"));
            summaryCount.revisit = cursor.getInt(cursor.getColumnIndex("revisit"));
            summaryCount.pendingUpload = cursor.getInt(cursor.getColumnIndex("pendingUpload"));
        }
        if (cursor != null) {
            cursor.close();
        }
        if (db.isOpen()) {
            db.close();
        }
        return summaryCount;
    }

    private static ArrayList<JobCard> getJobCardListFromCursor(Cursor cursor) {

        ArrayList<JobCard> jobCards = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            JobCard user;
            jobCards = new ArrayList<JobCard>();
            while (!cursor.isAfterLast()) {
                user = getJobCardFromCursor(cursor);
                jobCards.add(user);
                cursor.moveToNext();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return jobCards;
    }

    private static JobCard getJobCardFromCursor(Cursor cursor) {
        JobCard jobCard = new JobCard();
        jobCard.consumer_name = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.CONSUMER_NAME)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.CONSUMER_NAME)) : "";
        jobCard.meter_reader_id = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.METER_READER_ID)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.METER_READER_ID)) : "";
        jobCard.consumer_no = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.CONSUMER_NO)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.CONSUMER_NO)) : "";
        jobCard.meter_no = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.METER_ID)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.METER_ID)) : "";
        jobCard.bill_cycle_code = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.BILL_CYCLE_CODE)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.BILL_CYCLE_CODE)) : "";
        jobCard.schedule_month = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.SCHEDULE_MONTH)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.SCHEDULE_MONTH)) : "";
        jobCard.schedule_end_date = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.SCHEDULE_END_DATE)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.SCHEDULE_END_DATE)) : "";
        jobCard.route_code = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.ROUTE_ID)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.ROUTE_ID)) : "";
        jobCard.phone_no = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.PHONE_NO)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.PHONE_NO)) : "";
        jobCard.address = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.ADDRESS)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.ADDRESS)) : "";
        jobCard.meter_reader_id = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.METER_READER_ID)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.METER_READER_ID)) : "";
        jobCard.prv_meter_reading = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.PRV_METER_READING)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.PRV_METER_READING)) : "";
        jobCard.lattitude = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.PRV_LAT)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.PRV_LAT)) : "";
        jobCard.longitude = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.PRV_LONG)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.PRV_LONG)) : "";
        jobCard.is_revisit = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.IS_REVISIT)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.IS_REVISIT)) : "";
        jobCard.assigned_date = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.ASSIGNED_DATE)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.ASSIGNED_DATE)) : "";
        jobCard.job_card_status = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.JOB_CARD_STATUS)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.JOB_CARD_STATUS)) : "";
        jobCard.job_card_id = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.JOB_CARD_ID)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.JOB_CARD_ID)) : "";
        jobCard.prv_sequence = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.PRV_SEQUENCE)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.PRV_SEQUENCE)) : "";
        jobCard.zone_code = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.ZONE_CODE)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.ZONE_CODE)) : "";
        jobCard.category_id = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.CATEGORY_ID)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.CATEGORY_ID)) : "";
        jobCard.avg_consumption = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.AVG_CONSUMPTION)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.AVG_CONSUMPTION)) : "";
        jobCard.account_no = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.ACCOUNT_NO)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.ACCOUNT_NO)) : "";
        jobCard.current_sequence = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.CURRENT_SEQUENCE)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.CURRENT_SEQUENCE)) : "";
        jobCard.location_guidance = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.LOCATION_GUIDANCE)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.LOCATION_GUIDANCE)) : "";
        jobCard.street = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.STREET)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.STREET)) : "";
        jobCard.building_no = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.BUILDING_NO)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.BUILDING_NO)) : "";
        jobCard.door_lock_reading_attempt = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.DOOR_LOCK_READING_ATTEMPT)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.DOOR_LOCK_READING_ATTEMPT)) : "";
        jobCard.permanently_locked_reading_attempt = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.PERMANENTLY_LOCK_READING_ATTEMPT)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.PERMANENTLY_LOCK_READING_ATTEMPT)) : "";
        jobCard.sd_amount = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.SD_AMOUNT)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.SD_AMOUNT)) : "";
        jobCard.excluding_sd_amount = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.EXCLUDING_SD_AMOUNT)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.EXCLUDING_SD_AMOUNT)) : "";
        jobCard.total_amount = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.TOTAL_AMOUNT)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.TOTAL_AMOUNT)) : "";


        jobCard.meter_digit = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.METER_DIGIT)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.METER_DIGIT)) : "";
        jobCard.prev_reading_date = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.PRV_READING_DATE)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.PRV_READING_DATE)) : "";
        jobCard.total_price = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.TOTAL_PRICE)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.TOTAL_PRICE)) : "";
        jobCard.pre_due = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.PREVIOUS_DUE)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.PREVIOUS_DUE)) : "";
        jobCard.payment = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.PAYMENTS)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.PAYMENTS)) : "";
        jobCard.emi_security_dep = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.EMI_SECURITY_DEPOSIT)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.EMI_SECURITY_DEPOSIT)) : "";
        jobCard.avg_unit = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.AVG_UNIT)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.AVG_UNIT)) : "";
        jobCard.due_amount = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.DUE_AMOUNT)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.DUE_AMOUNT)) : "";


        return jobCard;
    }

    /**
     * @param context
     * @param reader_id
     */

    /*Context mContext,
    String reader_id, int limit*/
    public static ArrayList<Consumer> getUnBillConsumers(Context context, String reader_id, int limit) {
        String condition = ConsumerTable.Cols.METER_READER_ID + "='" + reader_id + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(ConsumerTable.CONTENT_URI, null,
                condition, null, ConsumerTable.Cols.CONSUMER_ID + " ASC " + " LIMIT " + limit);
        ArrayList<Consumer> consumers = getConsumersFromCursor(cursor);
        if (cursor != null) {
            cursor.close();
        }
        return consumers;
    }

    public static ArrayList<Consumer> checkUnBillConsumers(Context context, String reader_id, String consumerNo) {
        String condition = ConsumerTable.Cols.METER_READER_ID + "='" + reader_id + "' AND " + ConsumerTable.Cols.CONSUMER_ID + "='" + consumerNo + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(ConsumerTable.CONTENT_URI, null,
                condition, null, null);
        ArrayList<Consumer> consumers = getConsumersFromCursor(cursor);
        if (cursor != null) {
            cursor.close();
        }
        return consumers;
    }

    public static void deleteUnBillConsumer(Context mContext, String consumers, String mr) {
        String condition = ConsumerTable.Cols.ID + "='" + consumers + "' and " + ConsumerTable.Cols.METER_READER_ID + "='" + mr + "'";
        mContext.getContentResolver().delete(ConsumerTable.CONTENT_URI, condition, null);
    }

    private static ArrayList<Consumer> getConsumersFromCursor(Cursor cursor) {
        ArrayList<Consumer> consumers = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            Consumer consumer;
            consumers = new ArrayList<>();
            while (!cursor.isAfterLast()) {
                consumer = getConsumerFromCursor(cursor);
                consumers.add(consumer);
                cursor.moveToNext();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return consumers;
    }

    private static Consumer getConsumerFromCursor(Cursor cursor) {
        Consumer consumer = new Consumer();
        consumer.consumer_name = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.CONSUMER_NAME)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.CONSUMER_NAME)) : "";
        consumer.meter_reader_id = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.METER_READER_ID)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.METER_READER_ID)) : "";
        consumer.consumer_no = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.CONSUMER_ID)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.CONSUMER_ID)) : "";
        consumer.meter_no = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.METER_NO)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.METER_NO)) : "";
        consumer.dtc = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.DT_CODE)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.DT_CODE)) : "";
        consumer.bill_cycle_code = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.BILL_CYCLE_CODE)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.BILL_CYCLE_CODE)) : "";
        consumer.route_code = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.ROUTE_ID)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.ROUTE_ID)) : "";
        consumer.contact_no = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.PHONE_NO)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.PHONE_NO)) : "";
        consumer.address = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.ADDRESS)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.ADDRESS)) : "";
        consumer.current_meter_reading = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.CURRENT_METER_READING)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.CURRENT_METER_READING)) : "";
        consumer.meter_status = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.METER_STATUS)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.METER_STATUS)) : "";
        consumer.reader_status = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.READER_STATUS)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.READER_STATUS)) : "";
        consumer.cur_lat = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.CUR_LAT)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.CUR_LAT)) : "";
        consumer.cur_lng = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.CUR_LNG)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.CUR_LNG)) : "";
        consumer.email_id = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.EMAIL_ID)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.EMAIL_ID)) : "";
        consumer.reader_remark_comment = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.COMMENTS)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.COMMENTS)) : "";
        consumer.suspicious_activity = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.IS_SUSPICIOUS)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.IS_SUSPICIOUS)) : "";
        consumer.suspicious_remark = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.SUSPICIOUS_REMARKS)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.SUSPICIOUS_REMARKS)) : "";
        consumer.connection_status = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.CONNECTION_STATUS)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.CONNECTION_STATUS)) : "";
        consumer.reading_month = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.MONTH)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.MONTH)) : "";
        consumer.meter_reader_id = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.METER_READER_ID)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.METER_READER_ID)) : "";

        MeterImage meterImage = new MeterImage();
        meterImage.name = "mi_" + consumer.reading_month + "_" + consumer.bill_cycle_code + "_newconsumer_" + consumer.meter_reader_id + "_" + consumer.consumer_no + ".JPEG";
        meterImage.image = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.READING_IMAGE)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.READING_IMAGE)) : "";
        meterImage.content_type = "image/jpeg";
        consumer.meter_image = meterImage;

        MeterImage suspicious_activity_image = new MeterImage();
        suspicious_activity_image.name = "sp_" + consumer.reading_month + "_" + consumer.bill_cycle_code + "_newconsumer_" + consumer.meter_reader_id + "_" + consumer.consumer_no + ".JPEG";
        suspicious_activity_image.image = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.SUSPICIOUS_READING_IMAGE)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.SUSPICIOUS_READING_IMAGE)) : "";
        suspicious_activity_image.content_type = "image/jpeg";
        consumer.suspicious_activity_image = suspicious_activity_image;

        consumer.reading_date = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.READING_DATE)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.READING_DATE)) : "";
        consumer.reading_taken_by = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.READING_TAKEN_BY)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.READING_TAKEN_BY)) : "";
        consumer.location_guidance = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.LOCATION_GUIDANCE)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.LOCATION_GUIDANCE)) : "";
        consumer.current_meter_reading = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.CURRENT_METER_READING)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.CURRENT_METER_READING)) : "";
        consumer.mobile_no = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.MOBILE_NO)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.MOBILE_NO)) : "";
        consumer.new_sequence = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.NEW_SEQUENCE)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.NEW_SEQUENCE)) : "";
        consumer.meter_type = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.METER_TYPE)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.METER_TYPE)) : "";
        consumer.id = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.ID)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.ID)) : "";
        consumer.zone_code = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.ZONE_CODE)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.ZONE_CODE)) : "";

        return consumer;
    }

    private static ArrayList<UploadsHistory> getUploadsHistoryFromCursor(Cursor cursor) {
        ArrayList<UploadsHistory> uploadsHistoryArray = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            UploadsHistory uploadsHistory;
            uploadsHistoryArray = new ArrayList<>();
            while (!cursor.isAfterLast()) {
                uploadsHistory = getUploadHistoryFromCursor(cursor);
                uploadsHistoryArray.add(uploadsHistory);
                cursor.moveToNext();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return uploadsHistoryArray;
    }

    private static UploadsHistory getUploadHistoryFromCursor(Cursor cursor) {
        UploadsHistory uploadsHistory = new UploadsHistory();
        uploadsHistory.consumer_no = cursor.getString(cursor.getColumnIndex(UploadsHistoryTable.Cols.CONSUMER_ID)) != null ? cursor.getString(cursor.getColumnIndex(UploadsHistoryTable.Cols.CONSUMER_ID)) : "";
        uploadsHistory.bill_cycle_code = cursor.getString(cursor.getColumnIndex(UploadsHistoryTable.Cols.BILL_CYCLE_CODE)) != null ? cursor.getString(cursor.getColumnIndex(UploadsHistoryTable.Cols.BILL_CYCLE_CODE)) : "";
        uploadsHistory.route_code = cursor.getString(cursor.getColumnIndex(UploadsHistoryTable.Cols.ROUTE_ID)) != null ? cursor.getString(cursor.getColumnIndex(UploadsHistoryTable.Cols.ROUTE_ID)) : "";
        uploadsHistory.month = cursor.getString(cursor.getColumnIndex(UploadsHistoryTable.Cols.MONTH)) != null ? cursor.getString(cursor.getColumnIndex(UploadsHistoryTable.Cols.MONTH)) : "";
        uploadsHistory.upload_status = cursor.getString(cursor.getColumnIndex(UploadsHistoryTable.Cols.UPLOAD_STATUS)) != null ? cursor.getString(cursor.getColumnIndex(UploadsHistoryTable.Cols.UPLOAD_STATUS)) : "";
        uploadsHistory.reading_date = cursor.getString(cursor.getColumnIndex(UploadsHistoryTable.Cols.READING_DATE)) != null ? cursor.getString(cursor.getColumnIndex(UploadsHistoryTable.Cols.READING_DATE)) : "";
        uploadsHistory.meter_reader_id = cursor.getString(cursor.getColumnIndex(UploadsHistoryTable.Cols.METER_READER_ID)) != null ? cursor.getString(cursor.getColumnIndex(UploadsHistoryTable.Cols.METER_READER_ID)) : "";

        return uploadsHistory;
    }

    /**
     * @param context
     * @param reader_id
     */
    public static ArrayList<MeterReading> getMeterReadings(Context context, String reader_id, int limit) {
        String condition = MeterReadingTable.Cols.METER_READER_ID + "='" + reader_id
                + "' and " + MeterReadingTable.Cols.IS_UPLOADED + "='False'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(MeterReadingTable.CONTENT_URI, null,
                condition, null, MeterReadingTable.Cols.METER_READER_ID + " ASC " + " LIMIT " + limit);
        ArrayList<MeterReading> meterReadings = getMeterReadingsFromCursor(context, cursor);
        if (cursor != null) {
            cursor.close();
        }
        return meterReadings;
    }

    public static ArrayList<MeterReading> getMeterReadingsSpotBill(Context context, String reader_id, int limit, String meterNo) {
        String condition = MeterReadingTable.Cols.METER_READER_ID + "='" + reader_id
                + "' and " + MeterReadingTable.Cols.IS_UPLOADED + "='False' and "
                + MeterReadingTable.Cols.METER_NO + "='" + meterNo +"'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(MeterReadingTable.CONTENT_URI, null,
                condition, null, MeterReadingTable.Cols.METER_READER_ID + " ASC " + " LIMIT " + limit);
        ArrayList<MeterReading> meterReadings = getMeterReadingsFromCursor(context, cursor);
        if (cursor != null) {
            cursor.close();
        }
        return meterReadings;
    }

    public static ArrayList<MeterReading> getMeterReading(Context context, String reader_id) {
        String condition = MeterReadingTable.Cols.METER_READER_ID + "='" + reader_id + "' and " + MeterReadingTable.Cols.IS_UPLOADED + "='False'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(MeterReadingTable.CONTENT_URI, null,
                condition, null, null);
        ArrayList<MeterReading> meterReadings = getMeterReadingsFromCursor(context, cursor);
        if (cursor != null) {
            cursor.close();
        }
        return meterReadings;
    }

    public static ArrayList<MeterReading> getMeterReading(Context context, String reader_id, String jobCardId) {
        SQLiteDatabase db = DatabaseProvider.dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from " + MeterReadingTable.TABLE_NAME + " where " + MeterReadingTable.Cols.JOB_CARD_ID + "='" + jobCardId + "' AND " + MeterReadingTable.Cols.METER_READER_ID + "= '" + reader_id + "'", null);
        ArrayList<MeterReading> meterReadings = getMeterReadingsFromCursor(context, cursor);
        if (cursor != null) {
            cursor.close();
        }
        if (db.isOpen()) {
            db.close();
        }
        return meterReadings;

    }
    public static ArrayList<MeterReading> getMeterReadingSpotBill(Context context, String reader_id, String jobCardId) {
        SQLiteDatabase db = DatabaseProvider.dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from " + MeterReadingTable.TABLE_NAME + " where " +
                MeterReadingTable.Cols.JOB_CARD_ID + "='" + jobCardId + "' AND "
                + MeterReadingTable.Cols.METER_READER_ID + "= '" + reader_id + "'", null);
        ArrayList<MeterReading> meterReadings = getMeterReadingsFromCursor(context, cursor);
        if (cursor != null) {
            cursor.close();
        }
        if (db.isOpen()) {
            db.close();
        }
        return meterReadings;

    }

    private static ArrayList<MeterReading> getMeterReadingsFromCursor(Context context, Cursor cursor) {
        ArrayList<MeterReading> meterReadings = null;
        if (cursor != null && cursor.getCount() > 0) {
            try {
                cursor.moveToFirst();
                MeterReading meterReading;
                meterReadings = new ArrayList<>();
                while (!cursor.isAfterLast()) {
                    meterReading = getMeterReadingFromCursor(context, cursor);
                    meterReadings.add(meterReading);
                    cursor.moveToNext();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (cursor != null) {
            cursor.close();
        }

        return meterReadings;
    }

    private static MeterReading getMeterReadingFromCursor(Context context, Cursor cursor) {
        MeterReading meterReading = new MeterReading();
        meterReading.meter_no = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.METER_NO)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.METER_NO)) : "";
        meterReading.meter_reader_id = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.METER_READER_ID)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.METER_READER_ID)) : "";
        meterReading.job_card_id = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.JOB_CARD_ID)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.JOB_CARD_ID)) : "";
        meterReading.current_meter_reading = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.CURRENT_METER_READING)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.CURRENT_METER_READING)) : "";
        meterReading.meter_status = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.METER_STATUS)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.METER_STATUS)) : "";
        meterReading.reader_status = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.READER_STATUS)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.READER_STATUS)) : "";
        meterReading.reading_month = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.READING_MONTH)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.READING_MONTH)) : "";
        meterReading.reader_remark_comment = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.READER_REMARK_COMMENT)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.READER_REMARK_COMMENT)) : "";
        meterReading.suspicious_activity = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.IS_SUSPICIOUS)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.IS_SUSPICIOUS)) : "";
        meterReading.suspicious_remark = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.SUSPICIOUS_REMARKS)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.SUSPICIOUS_REMARKS)) : "";

        MeterImage meterImage = new MeterImage();

        String meterReading_bill_cycle_code = "";
        String meterReading_consumer_no = "";
        ArrayList<JobCard> mJobCards = DatabaseManager.getJobCard(meterReading.meter_reader_id, AppConstants.JOB_CARD_STATUS_COMPLETED, meterReading.job_card_id);
        if (mJobCards != null && mJobCards.size() > 0) {
            meterReading_bill_cycle_code = mJobCards.get(0).bill_cycle_code;
            meterReading_consumer_no = mJobCards.get(0).consumer_no;
        }

        meterImage.name = "mi_" + meterReading.reading_month + "_" + meterReading_bill_cycle_code + "_" + meterReading.job_card_id + "_" + meterReading_consumer_no + ".JPEG ";
        meterImage.image = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.READING_IMAGE)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.READING_IMAGE)) : "";
        meterImage.content_type = "image/jpeg";
        meterReading.meter_image = meterImage;

        MeterImage suspicious_activity_image = new MeterImage();
        suspicious_activity_image.name = "sp_" + meterReading.reading_month + "_" + meterReading_bill_cycle_code + "_" + meterReading.job_card_id + "_" + meterReading_consumer_no + ".JPEG";
        suspicious_activity_image.image = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.SUSPICIOUS_READING_IMAGE)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.SUSPICIOUS_READING_IMAGE)) : "";
        suspicious_activity_image.content_type = "image/jpeg";
        meterReading.suspicious_activity_image = suspicious_activity_image;

        meterReading.cur_lat = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.CUR_LAT)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.CUR_LAT)) : "";
        meterReading.cur_lng = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.CUR_LNG)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.CUR_LNG)) : "";
        meterReading.isUploaded = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.IS_UPLOADED)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.IS_UPLOADED)) : "";
        meterReading.isRevisit = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.IS_REVISIT)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.IS_REVISIT)) : "";
        meterReading.reading_date = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.READING_DATE)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.READING_DATE)) : "";
        meterReading.reading_taken_by = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.READING_TAKEN_BY)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.READING_TAKEN_BY)) : "";
        meterReading.location_guidance = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.LOCATION_GUIDANCE)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.LOCATION_GUIDANCE)) : "";
        meterReading.mobile_no = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.MOBILE_NO)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.MOBILE_NO)) : "";
        meterReading.prv_sequence = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.PRV_SEQUENCE)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.PRV_SEQUENCE)) : "";
        meterReading.new_sequence = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.NEW_SEQUENCE)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.NEW_SEQUENCE)) : "";
        meterReading.zone_code = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.ZONE_CODE)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.ZONE_CODE)) : "";
        meterReading.meter_type = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.METER_TYPE)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.METER_TYPE)) : "";
        meterReading.door_lock_reading_attempt = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.DOOR_LOCK_READING_ATTEMPT)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.DOOR_LOCK_READING_ATTEMPT)) : "";
        meterReading.permanently_locked_reading_attempt = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.PERMANENTLY_LOCK_READING_ATTEMPT)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.PERMANENTLY_LOCK_READING_ATTEMPT)) : "";

        MeterImage chequeImage = new MeterImage();
        chequeImage.name = "ci_" + meterReading.reading_month + "_" + meterReading_bill_cycle_code + "_" + meterReading.job_card_id + "_" + meterReading_consumer_no + ".JPEG";
        chequeImage.image = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.CHEQUE_IMAGE)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.CHEQUE_IMAGE)) : "";
        chequeImage.content_type = "image/jpeg";
        meterReading.cheque_image = chequeImage;

        meterReading.cheque_number = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.CHEQUE_NUMBER)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.CHEQUE_NUMBER)) : "";
        meterReading.cheque_amount = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.CHEQUE_AMOUNT)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.CHEQUE_AMOUNT)) : "";



        meterReading.time_taken = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.TIME_TAKEN)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.TIME_TAKEN)) : "";
        meterReading.spot_billing = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.IS_SPOT_BILL)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.IS_SPOT_BILL)) : "";


        meterReading.consumptionCharges = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.CONSUMPTION_CHARGES)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.CONSUMPTION_CHARGES)) : "";
        meterReading.current_charges = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.CURRENT_CHARGES)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.CURRENT_CHARGES)) : "";
        meterReading.amt_before_due = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.AMOUNT_BEFORE_DUE)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.AMOUNT_BEFORE_DUE)) : "";
        meterReading.amt_after_due = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.AMOUNT_AFTER_DUE)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.AMOUNT_AFTER_DUE)) : "";
        meterReading.bill_type = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.BILL_TYPE)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.BILL_TYPE)) : "";
        meterReading.due_date = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.DUE_DATE)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.DUE_DATE)) : "";
        meterReading.emi_security_dep = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.EMI_SECURITY_DEPOSIT)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.EMI_SECURITY_DEPOSIT)) : "";
        meterReading.payment = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.PAYMENTS)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.PAYMENTS)) : "";
        meterReading.pre_due = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.PREVIOUS_DUE)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.PREVIOUS_DUE)) : "";
        meterReading.total_price = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.TOTAL_PRICE)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.TOTAL_PRICE)) : "";
        meterReading.consump_days = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.CONSUMP_DAYS)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.CONSUMP_DAYS)) : "";

        meterReading.name_change = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.NAME_CHANGE)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.NAME_CHANGE)) : "";
        meterReading.address_change = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.ADD_CHANGE)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.ADD_CHANGE)) : "";
        meterReading.meter_change = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.METER_CHANGE)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.METER_CHANGE)) : "";
        meterReading.mobile_change = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.MOB_CHANGE)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.MOB_CHANGE)) : "";
        meterReading.changed_name = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.CHANGED_NAME)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.CHANGED_NAME)) : "";
        meterReading.changed_address = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.CHANGED_ADD)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.CHANGED_ADD)) : "";
        meterReading.changed_meter = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.CHANGED_METER)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.CHANGED_METER)) : "";
        meterReading.changed_mobile = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.CHANGED_MOB)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.CHANGED_MOB)) : "";
        return meterReading;
    }

    /**
     * @param context
     * @param reader_id
     */

    public static ArrayList<String> getRoutes(Context context, String reader_id) {
        String condition = JobCardTable.Cols.METER_READER_ID + "='" + reader_id + "' AND " +
                JobCardTable.Cols.JOB_CARD_STATUS + "='" + AppConstants.JOB_CARD_STATUS_ALLOCATED + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(JobCardTable.CONTENT_URI, new String[]{"DISTINCT " + JobCardTable.Cols.ROUTE_ID},
                condition, null, null);
        ArrayList<String> routes = null;

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            routes = new ArrayList<>();
            while (!cursor.isAfterLast()) {
                String route_code = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.ROUTE_ID));
                routes.add(route_code);
                cursor.moveToNext();
            }
        }

        if (cursor != null) {
            cursor.close();
        }
        return routes;
    }

    public static ArrayList<String> getZone(Context context, String reader_id) {
        String condition = JobCardTable.Cols.METER_READER_ID + "='" + reader_id + "' AND " +
                JobCardTable.Cols.JOB_CARD_STATUS + "='" + AppConstants.JOB_CARD_STATUS_ALLOCATED + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(JobCardTable.CONTENT_URI, new String[]{"DISTINCT " + JobCardTable.Cols.ZONE_CODE},
                condition, null, null);
        ArrayList<String> routes = null;

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            routes = new ArrayList<>();
            while (!cursor.isAfterLast()) {
                String route_code = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.ZONE_CODE));
                routes.add(route_code);
                cursor.moveToNext();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return routes;
    }

    /**
     * @param context
     * @param reader_id
     */

    public static ArrayList<String> getBillCycleCode(Context context, String reader_id) {
        String condition = JobCardTable.Cols.METER_READER_ID + "='" + reader_id + "' AND " +
                JobCardTable.Cols.JOB_CARD_STATUS + "='" + AppConstants.JOB_CARD_STATUS_ALLOCATED + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(JobCardTable.CONTENT_URI, new String[]{"DISTINCT " + JobCardTable.Cols.BILL_CYCLE_CODE},
                condition, null, null);
        ArrayList<String> billcyclecode = null;

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            billcyclecode = new ArrayList<String>();
            while (!cursor.isAfterLast()) {
                String bill_cycle_code = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.BILL_CYCLE_CODE));
                billcyclecode.add(bill_cycle_code);
                cursor.moveToNext();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return billcyclecode;
    }


    /**
     * @param context
     * @param reader_id
     */

    public static ArrayList<String> getBillMonth(Context context, String reader_id) {
        String condition = JobCardTable.Cols.METER_READER_ID + "='" + reader_id + "' AND " +
                JobCardTable.Cols.JOB_CARD_STATUS + "='" + AppConstants.JOB_CARD_STATUS_ALLOCATED + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(JobCardTable.CONTENT_URI, new String[]{"DISTINCT " + JobCardTable.Cols.SCHEDULE_MONTH},
                condition, null, null);
        ArrayList<String> billmonth = null;

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            billmonth = new ArrayList<String>();
            while (!cursor.isAfterLast()) {
                String bill_month = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.SCHEDULE_MONTH));
                billmonth.add(bill_month);
                cursor.moveToNext();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return billmonth;
    }

    // Method to save image Starts Avinesh:04-03-17
    public static void saveImage(Context context, UserProfile con) {
        if (con != null) {
            ContentValues values = new ContentValues();
            try {
                values.put(UserProfileTable.Cols.IMAGE, con.profile_image);

            } catch (Exception e) {
                e.printStackTrace();
            }
            saveImage(context, values, con);
        }
    }

    private static void saveImage(Context context, ContentValues values, UserProfile con) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.update(UserProfileTable.TABLE_NAME, values, UserProfileTable.Cols.METER_READER_ID + " = ?",
                new String[]{String.valueOf(con.meter_reader_id)});
        if (db.isOpen()) {
            db.close();
        }
    }

    /**
     * @param route
     * @param reader_id
     */
    public static ArrayList<String> getBillCycleCode(Context context, String reader_id, String route) {
        String condition = JobCardTable.Cols.METER_READER_ID + "='" + reader_id + "' AND " + JobCardTable.Cols.JOB_CARD_STATUS + "='" + AppConstants.JOB_CARD_STATUS_ALLOCATED + "' AND " + JobCardTable.Cols.ROUTE_ID + "='" + route + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(JobCardTable.CONTENT_URI, new String[]{"DISTINCT " + JobCardTable.Cols.BILL_CYCLE_CODE},
                condition, null, null);
        ArrayList<String> billcyclecode = null;

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            billcyclecode = new ArrayList<String>();
            while (!cursor.isAfterLast()) {
                String bill_cycle_code = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.BILL_CYCLE_CODE));
                billcyclecode.add(bill_cycle_code);
                cursor.moveToNext();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return billcyclecode;
    }

    /**
     * @param context
     * @param reader_id
     */
    public static UserProfile getUserProfile(Context context, String reader_id) {
        UserProfile userProfile = null;
        String condition = UserProfileTable.Cols.METER_READER_ID + "='" + reader_id + "' OR " +
                UserProfileTable.Cols.EMAIL_ID + "='" + reader_id + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(UserProfileTable.CONTENT_URI, null, condition, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                userProfile = getUserProfileFromCursor(cursor);
                cursor.moveToNext();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return userProfile;
    }


    private static UserProfile getUserProfileFromCursor(Cursor cursor) {
        UserProfile userProfile = new UserProfile();
        userProfile.meter_reader_name = cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.METER_READER_NAME)) != null ? cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.METER_READER_NAME)) : "";
        userProfile.meter_reader_id = cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.METER_READER_ID)) != null ? cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.METER_READER_ID)) : "";
//        userProfile.address = cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.ADDRESS)) != null ? cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.ADDRESS)) : "";
        userProfile.city = cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.CITY)) != null ? cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.CITY)) : "";
//        userProfile.state = cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.STATE)) != null ? cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.STATE)) : "";
//        userProfile.emp_id = cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.EMP_ID)) != null ? cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.EMP_ID)) : "";
        userProfile.email_id = cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.EMAIL_ID)) != null ? cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.EMAIL_ID)) : "";
//        userProfile.emp_type = cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.EMP_TYPE)) != null ? cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.EMP_TYPE)) : "";
        userProfile.device_imei_id = cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.DEVICE_IMEI_ID)) != null ? cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.DEVICE_IMEI_ID)) : "";
        userProfile.contact_no = cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.CONTACT_NO)) != null ? cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.CONTACT_NO)) : "";
//        userProfile.fcm_token = cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.FCM_TOKEN)) != null ? cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.FCM_TOKEN)) : "";
        userProfile.profile_image = cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.IMAGE)) != null ? cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.IMAGE)) : "";
        userProfile.app_link = cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.APP_LINK)) != null ? cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.APP_LINK)) : "";
        userProfile.app_version = cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.APP_VERSION)) != null ? cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.APP_VERSION)) : "";




        userProfile.current_date = cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.CURRENT_DATE)) != null ? cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.CURRENT_DATE)) : "";

        return userProfile;
    }

    /**
     * Get ContentValues from the Contact to insert it into UserLogin Table
     *
     * @param user User
     */
    private static ContentValues getContentValuesUserLoginTable(User user) {
        ContentValues values = new ContentValues();
        try {
            values.put(LoginTable.Cols.USER_LOGIN_ID, user.userid != null ? user.userid : "");
            values.put(LoginTable.Cols.METER_READER_ID, user.meter_reader_id != null ? user.meter_reader_id : "");
            values.put(LoginTable.Cols.LOGIN_DATE, user.login_date != null ? user.login_date : "");
            values.put(LoginTable.Cols.LOGIN_LAT, user.login_lat != null ? user.login_lat : "");
            values.put(LoginTable.Cols.LOGIN_LNG, user.login_lng != null ? user.login_lng : "");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    private static ContentValues getContentValuesMeterReadingTable(MeterReading meterReading) {
        ContentValues values = new ContentValues();
        try {
            values.put(MeterReadingTable.Cols.METER_NO, meterReading.meter_no != null ? meterReading.meter_no : "");
            values.put(MeterReadingTable.Cols.METER_READER_ID, meterReading.meter_reader_id != null ? meterReading.meter_reader_id : "");
            values.put(MeterReadingTable.Cols.JOB_CARD_ID, meterReading.job_card_id != null ? meterReading.job_card_id : "");
            values.put(MeterReadingTable.Cols.CURRENT_METER_READING, meterReading.current_meter_reading != null ? meterReading.current_meter_reading : "");
            values.put(MeterReadingTable.Cols.METER_STATUS, meterReading.meter_status != null ? meterReading.meter_status : "");
            values.put(MeterReadingTable.Cols.READER_STATUS, meterReading.reader_status != null ? meterReading.reader_status : "");
            values.put(MeterReadingTable.Cols.READING_MONTH, meterReading.reading_month != null ? meterReading.reading_month : "");
            values.put(MeterReadingTable.Cols.READING_IMAGE, meterReading.meter_image != null ? meterReading.meter_image.image != null ? meterReading.meter_image.image : "" : "");
            values.put(MeterReadingTable.Cols.SUSPICIOUS_READING_IMAGE, meterReading.suspicious_activity_image != null ? meterReading.suspicious_activity_image.image != null ? meterReading.suspicious_activity_image.image : "" : "");
            values.put(MeterReadingTable.Cols.READER_REMARK_COMMENT, meterReading.reader_remark_comment != null ? meterReading.reader_remark_comment : "");
            values.put(MeterReadingTable.Cols.IS_SUSPICIOUS, meterReading.suspicious_activity != null ? meterReading.suspicious_activity : "");
            values.put(MeterReadingTable.Cols.SUSPICIOUS_REMARKS, meterReading.suspicious_remark != null ? meterReading.suspicious_remark : "");
            values.put(MeterReadingTable.Cols.CUR_LAT, meterReading.cur_lat != null ? meterReading.cur_lat : "");
            values.put(MeterReadingTable.Cols.CUR_LNG, meterReading.cur_lng != null ? meterReading.cur_lng : "");
            values.put(MeterReadingTable.Cols.IS_UPLOADED, meterReading.isUploaded != null ? meterReading.isUploaded : "");
            values.put(MeterReadingTable.Cols.IS_REVISIT, meterReading.isRevisit != null ? meterReading.isRevisit : "");
            values.put(MeterReadingTable.Cols.READING_DATE, meterReading.reading_date != null ? meterReading.reading_date : "");
            values.put(MeterReadingTable.Cols.PRV_SEQUENCE, meterReading.prv_sequence != null ? meterReading.prv_sequence : "");
            values.put(MeterReadingTable.Cols.NEW_SEQUENCE, meterReading.new_sequence != null ? meterReading.new_sequence : "");
            values.put(MeterReadingTable.Cols.LOCATION_GUIDANCE, meterReading.location_guidance != null ? meterReading.location_guidance : "");
            values.put(MeterReadingTable.Cols.MOBILE_NO, meterReading.mobile_no != null ? meterReading.mobile_no : "");
            values.put(MeterReadingTable.Cols.READING_TAKEN_BY, meterReading.reading_taken_by != null ? meterReading.reading_taken_by : "");
            values.put(MeterReadingTable.Cols.ZONE_CODE, meterReading.zone_code != null ? meterReading.zone_code : "");
            values.put(MeterReadingTable.Cols.METER_TYPE, meterReading.meter_type != null ? meterReading.meter_type : "");
            values.put(MeterReadingTable.Cols.DOOR_LOCK_READING_ATTEMPT, meterReading.door_lock_reading_attempt != null ? meterReading.door_lock_reading_attempt : "");
            values.put(MeterReadingTable.Cols.PERMANENTLY_LOCK_READING_ATTEMPT, meterReading.permanently_locked_reading_attempt != null ? meterReading.permanently_locked_reading_attempt : "");
            values.put(MeterReadingTable.Cols.CHEQUE_IMAGE, meterReading.cheque_image != null ? meterReading.cheque_image.image != null ? meterReading.cheque_image.image : "" : "");
            values.put(MeterReadingTable.Cols.CHEQUE_NUMBER, meterReading.cheque_number != null ? meterReading.cheque_number : "");
            values.put(MeterReadingTable.Cols.CHEQUE_AMOUNT, meterReading.cheque_amount != null ? meterReading.cheque_amount : "");
            values.put(MeterReadingTable.Cols.TIME_TAKEN, meterReading.time_taken != null ? meterReading.time_taken : "");



            values.put(MeterReadingTable.Cols.IS_SPOT_BILL, meterReading.spot_billing != null ? meterReading.spot_billing : "");





            values.put(MeterReadingTable.Cols.CONSUMPTION_CHARGES, meterReading.consumptionCharges != null ? meterReading.consumptionCharges : "");
            values.put(MeterReadingTable.Cols.CURRENT_CHARGES, meterReading.current_charges != null ? meterReading.current_charges : "");
            values.put(MeterReadingTable.Cols.AMOUNT_BEFORE_DUE, meterReading.amt_before_due != null ? meterReading.amt_before_due : "");
            values.put(MeterReadingTable.Cols.AMOUNT_AFTER_DUE, meterReading.amt_after_due != null ? meterReading.amt_after_due : "");
            values.put(MeterReadingTable.Cols.BILL_TYPE, meterReading.bill_type != null ? meterReading.bill_type : "");
            values.put(MeterReadingTable.Cols.DUE_DATE, meterReading.due_date != null ? meterReading.due_date : "");
            values.put(MeterReadingTable.Cols.EMI_SECURITY_DEPOSIT, meterReading.emi_security_dep != null ? meterReading.emi_security_dep : "");
            values.put(MeterReadingTable.Cols.PREVIOUS_DUE, meterReading.pre_due != null ? meterReading.pre_due : "");
            values.put(MeterReadingTable.Cols.PAYMENTS, meterReading.payment != null ? meterReading.payment : "");
            values.put(MeterReadingTable.Cols.TOTAL_PRICE, meterReading.total_price != null ? meterReading.total_price : "");
            values.put(MeterReadingTable.Cols.CONSUMP_DAYS, meterReading.consump_days != null ? meterReading.consump_days : "");


            values.put(MeterReadingTable.Cols.NAME_CHANGE, meterReading.name_change != null ? meterReading.name_change : "");
            values.put(MeterReadingTable.Cols.ADD_CHANGE, meterReading.address_change != null ? meterReading.address_change : "");
            values.put(MeterReadingTable.Cols.METER_CHANGE, meterReading.meter_change != null ? meterReading.meter_change : "");
            values.put(MeterReadingTable.Cols.MOB_CHANGE, meterReading.mobile_change != null ? meterReading.mobile_change : "");
            values.put(MeterReadingTable.Cols.CHANGED_NAME, meterReading.changed_name != null ? meterReading.changed_name : "");
            values.put(MeterReadingTable.Cols.CHANGED_ADD, meterReading.changed_address != null ? meterReading.changed_address : "");
            values.put(MeterReadingTable.Cols.CHANGED_METER, meterReading.changed_meter != null ? meterReading.changed_meter : "");
            values.put(MeterReadingTable.Cols.CHANGED_MOB, meterReading.changed_mobile != null ? meterReading.changed_mobile : "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    private static ContentValues getContentValuesUserProfileTable(UserProfile userProfile) {
        ContentValues values = new ContentValues();
        try {
            values.put(UserProfileTable.Cols.METER_READER_NAME, userProfile.meter_reader_name != null ? userProfile.meter_reader_name : "");
            values.put(UserProfileTable.Cols.METER_READER_ID, userProfile.meter_reader_id != null ? userProfile.meter_reader_id : "");
//            values.put(UserProfileTable.Cols.ADDRESS, userProfile.address != null ? userProfile.address : "");
            values.put(UserProfileTable.Cols.CITY, userProfile.city != null ? userProfile.city : "");
//            values.put(UserProfileTable.Cols.STATE, userProfile.state != null ? userProfile.state : "");
//            values.put(UserProfileTable.Cols.EMP_ID, userProfile.emp_id != null ? userProfile.emp_id : "");
            values.put(UserProfileTable.Cols.EMAIL_ID, userProfile.email_id != null ? userProfile.email_id : "");
//            values.put(UserProfileTable.Cols.EMP_TYPE, userProfile.emp_type != null ? userProfile.emp_type : "");
            values.put(UserProfileTable.Cols.DEVICE_IMEI_ID, userProfile.device_imei_id != null ? userProfile.device_imei_id : "");
            values.put(UserProfileTable.Cols.CONTACT_NO, userProfile.contact_no != null ? userProfile.contact_no : "");
//            values.put(UserProfileTable.Cols.FCM_TOKEN, FcmToken != null ? FcmToken : "");
            values.put(UserProfileTable.Cols.APP_LINK, userProfile.app_link != null ? userProfile.app_link : "");
            values.put(UserProfileTable.Cols.APP_VERSION, userProfile.app_version != null ? userProfile.app_version : "");
            values.put(UserProfileTable.Cols.CURRENT_DATE, userProfile.current_date != null ? userProfile.current_date : "");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    private static ContentValues getContentValuesJobCardTable(JobCard jobCard) {
        ContentValues values = new ContentValues();
        try {
            values.put(JobCardTable.Cols.JOB_CARD_STATUS, jobCard.job_card_status != null ? jobCard.job_card_status : "");
            values.put(JobCardTable.Cols.JOB_CARD_ID, jobCard.job_card_id != null ? jobCard.job_card_id : "");
            values.put(JobCardTable.Cols.IS_REVISIT, jobCard.is_revisit != null ? jobCard.is_revisit : "");
            values.put(JobCardTable.Cols.METER_ID, jobCard.meter_no != null ? jobCard.meter_no : "");
            values.put(JobCardTable.Cols.BILL_CYCLE_CODE, jobCard.bill_cycle_code != null ? jobCard.bill_cycle_code : "");
            values.put(JobCardTable.Cols.METER_READER_ID, jobCard.meter_reader_id != null ? jobCard.meter_reader_id : "");
            values.put(JobCardTable.Cols.SCHEDULE_MONTH, jobCard.schedule_month != null ? jobCard.schedule_month : "");
            values.put(JobCardTable.Cols.SCHEDULE_END_DATE, jobCard.schedule_end_date != null ? jobCard.schedule_end_date : "");
            values.put(JobCardTable.Cols.ROUTE_ID, jobCard.route_code != null ? jobCard.route_code : "");
            values.put(JobCardTable.Cols.CONSUMER_NO, jobCard.consumer_no != null ? jobCard.consumer_no : "");
            values.put(JobCardTable.Cols.CONSUMER_NAME, jobCard.consumer_name != null ? jobCard.consumer_name : "");
            values.put(JobCardTable.Cols.PHONE_NO, jobCard.phone_no != null ? jobCard.phone_no : "");
            values.put(JobCardTable.Cols.ADDRESS, jobCard.address != null ? jobCard.address : "");
            values.put(JobCardTable.Cols.PRV_METER_READING, jobCard.prv_meter_reading != null ? jobCard.prv_meter_reading : "");
            values.put(JobCardTable.Cols.PRV_LAT, jobCard.lattitude != null ? jobCard.lattitude : "");
            values.put(JobCardTable.Cols.PRV_LONG, jobCard.longitude != null ? jobCard.longitude : "");
            values.put(JobCardTable.Cols.ASSIGNED_DATE, jobCard.assigned_date != null ? jobCard.assigned_date : "");
            values.put(JobCardTable.Cols.PRV_SEQUENCE, jobCard.prv_sequence != null ? jobCard.prv_sequence : "");
            values.put(JobCardTable.Cols.ZONE_CODE, jobCard.zone_code != null ? jobCard.zone_code : "");
            values.put(JobCardTable.Cols.CATEGORY_ID, jobCard.category_id != null ? jobCard.category_id : "");
            values.put(JobCardTable.Cols.AVG_CONSUMPTION, jobCard.avg_consumption != null ? jobCard.avg_consumption : "");
            values.put(JobCardTable.Cols.ACCOUNT_NO, jobCard.account_no != null ? jobCard.account_no : "");
            values.put(JobCardTable.Cols.CURRENT_SEQUENCE, jobCard.current_sequence != null ? jobCard.current_sequence : "");
            values.put(JobCardTable.Cols.LOCATION_GUIDANCE, jobCard.location_guidance != null ? jobCard.location_guidance : "");
            values.put(JobCardTable.Cols.STREET, jobCard.street != null ? jobCard.street : "");
            values.put(JobCardTable.Cols.BUILDING_NO, jobCard.building_no != null ? jobCard.building_no : "");
            values.put(JobCardTable.Cols.DOOR_LOCK_READING_ATTEMPT, jobCard.door_lock_reading_attempt != null ? jobCard.door_lock_reading_attempt : "");
            values.put(JobCardTable.Cols.PERMANENTLY_LOCK_READING_ATTEMPT, jobCard.permanently_locked_reading_attempt != null ? jobCard.permanently_locked_reading_attempt : "");
            values.put(JobCardTable.Cols.SD_AMOUNT, jobCard.sd_amount != null ? jobCard.sd_amount : "");
            values.put(JobCardTable.Cols.EXCLUDING_SD_AMOUNT, jobCard.excluding_sd_amount != null ? jobCard.excluding_sd_amount : "");
            values.put(JobCardTable.Cols.TOTAL_AMOUNT, jobCard.total_amount != null ? jobCard.total_amount : "");

            values.put(JobCardTable.Cols.METER_DIGIT, jobCard.meter_digit != null ? jobCard.meter_digit : "");
            values.put(JobCardTable.Cols.PRV_READING_DATE, jobCard.prev_reading_date != null ? jobCard.prev_reading_date : "");
            values.put(JobCardTable.Cols.TOTAL_PRICE, jobCard.total_price != null ? jobCard.total_price : "");
            values.put(JobCardTable.Cols.PREVIOUS_DUE, jobCard.pre_due != null ? jobCard.pre_due : "");
            values.put(JobCardTable.Cols.PAYMENTS, jobCard.payment != null ? jobCard.payment : "");
            values.put(JobCardTable.Cols.EMI_SECURITY_DEPOSIT, jobCard.emi_security_dep != null ? jobCard.emi_security_dep : "");
            values.put(JobCardTable.Cols.AVG_UNIT, jobCard.avg_unit != null ? jobCard.avg_unit : "");
            values.put(JobCardTable.Cols.DUE_AMOUNT, jobCard.due_amount != null ? jobCard.due_amount : "");


        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    private static ContentValues getContentValuesConsumerTable(Consumer consumer) {
        ContentValues values = new ContentValues();
        try {
            values.put(ConsumerTable.Cols.CONSUMER_ID, consumer.consumer_no != null ? consumer.consumer_no : "");
            values.put(ConsumerTable.Cols.METER_READER_ID, consumer.meter_reader_id != null ? consumer.meter_reader_id : "");
            values.put(ConsumerTable.Cols.CONSUMER_NAME, consumer.consumer_name != null ? consumer.consumer_name : "");
            values.put(ConsumerTable.Cols.PHONE_NO, consumer.contact_no != null ? consumer.contact_no : "");
            values.put(ConsumerTable.Cols.ADDRESS, consumer.address != null ? consumer.address : "");
            values.put(ConsumerTable.Cols.ROUTE_ID, consumer.route_code != null ? consumer.route_code : "");
            values.put(ConsumerTable.Cols.BILL_CYCLE_CODE, consumer.bill_cycle_code != null ? consumer.bill_cycle_code : "");
            values.put(ConsumerTable.Cols.METER_NO, consumer.meter_no != null ? consumer.meter_no : "");
            values.put(ConsumerTable.Cols.DT_CODE, consumer.dtc != null ? consumer.dtc : "");
            values.put(ConsumerTable.Cols.MONTH, consumer.reading_month != null ? consumer.reading_month : "");
            values.put(ConsumerTable.Cols.CONNECTION_STATUS, consumer.connection_status != null ? consumer.connection_status : "");
            values.put(ConsumerTable.Cols.EMAIL_ID, consumer.email_id != null ? consumer.email_id : "");
            values.put(ConsumerTable.Cols.CURRENT_METER_READING, consumer.current_meter_reading != null ? consumer.current_meter_reading : "");
            values.put(ConsumerTable.Cols.METER_STATUS, consumer.meter_status != null ? consumer.meter_status : "");
            values.put(ConsumerTable.Cols.READER_STATUS, consumer.reader_status != null ? consumer.reader_status : "");
            values.put(ConsumerTable.Cols.READING_IMAGE, consumer.meter_image != null ? consumer.meter_image.image != null ? consumer.meter_image.image : "" : "");
            values.put(ConsumerTable.Cols.COMMENTS, consumer.reader_remark_comment != null ? consumer.reader_remark_comment : "");
            values.put(ConsumerTable.Cols.IS_SUSPICIOUS, consumer.suspicious_activity != null ? consumer.suspicious_activity : "");
            values.put(ConsumerTable.Cols.SUSPICIOUS_REMARKS, consumer.suspicious_remark != null ? consumer.suspicious_remark : "");
            values.put(ConsumerTable.Cols.SUSPICIOUS_READING_IMAGE, consumer.suspicious_activity_image != null ? consumer.suspicious_activity_image.image != null ? consumer.suspicious_activity_image.image : "" : "");
            values.put(ConsumerTable.Cols.CUR_LAT, consumer.cur_lat != null ? consumer.cur_lat : "");
            values.put(ConsumerTable.Cols.CUR_LNG, consumer.cur_lng != null ? consumer.cur_lng : "");
            values.put(ConsumerTable.Cols.READING_DATE, consumer.reading_date != null ? consumer.reading_date : "");
            values.put(ConsumerTable.Cols.READING_TAKEN_BY, consumer.reading_taken_by != null ? consumer.reading_taken_by : "");
            values.put(ConsumerTable.Cols.LOCATION_GUIDANCE, consumer.location_guidance != null ? consumer.location_guidance : "");
            values.put(ConsumerTable.Cols.CURRENT_METER_READING, consumer.current_meter_reading != null ? consumer.current_meter_reading : "");
            values.put(ConsumerTable.Cols.MOBILE_NO, consumer.mobile_no != null ? consumer.mobile_no : "");
            values.put(ConsumerTable.Cols.METER_TYPE, consumer.meter_type != null ? consumer.meter_type : "");
            values.put(ConsumerTable.Cols.NEW_SEQUENCE, consumer.new_sequence != null ? consumer.new_sequence : "");
            values.put(ConsumerTable.Cols.ZONE_CODE, consumer.zone_code != null ? consumer.zone_code : "");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    private static ContentValues getContentValuesUploadsHistoryTable(UploadsHistory uploadsHistory) {
        ContentValues values = new ContentValues();
        try {
            values.put(UploadsHistoryTable.Cols.CONSUMER_ID, uploadsHistory.consumer_no != null ? uploadsHistory.consumer_no : "");
            values.put(UploadsHistoryTable.Cols.ROUTE_ID, uploadsHistory.route_code != null ? uploadsHistory.route_code : "");
            values.put(UploadsHistoryTable.Cols.BILL_CYCLE_CODE, uploadsHistory.bill_cycle_code != null ? uploadsHistory.bill_cycle_code : "");
            values.put(UploadsHistoryTable.Cols.MONTH, uploadsHistory.month != null ? uploadsHistory.month : "");
            values.put(UploadsHistoryTable.Cols.UPLOAD_STATUS, uploadsHistory.upload_status != null ? uploadsHistory.upload_status : "");
            values.put(UploadsHistoryTable.Cols.READING_DATE, uploadsHistory.reading_date != null ? uploadsHistory.reading_date : "");
            values.put(UploadsHistoryTable.Cols.METER_READER_ID, uploadsHistory.meter_reader_id != null ? uploadsHistory.meter_reader_id : "");


        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    public static ArrayList<JobCard> getJobCards(Context context, String reader_id, String jobCardStatus, String revisit) {

        String condition = JobCardTable.Cols.METER_READER_ID + "='" + reader_id + "' and " + JobCardTable.Cols.JOB_CARD_STATUS + "='" + jobCardStatus + "' and " + JobCardTable.Cols.IS_REVISIT + "='" + revisit + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(JobCardTable.CONTENT_URI, null,
                condition, null, JobCardTable.Cols.ACCOUNT_NO + " ASC");
        ArrayList<JobCard> jobCards = getJobCardListFromCursor(cursor);
        if (cursor != null) {
            cursor.close();
        }
        return jobCards;
    }

    public static ArrayList<JobCard> getJobCardByJobcardId(Context context, String reader_id, String jobCardStatus, String revisit, String jobcardid) {
        String condition = JobCardTable.Cols.METER_READER_ID + "='" + reader_id + "' and " + JobCardTable.Cols.JOB_CARD_STATUS + "='" + jobCardStatus + "' and " + JobCardTable.Cols.JOB_CARD_ID + "='" + jobcardid+ "' and " + JobCardTable.Cols.IS_REVISIT + "='" + revisit + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(JobCardTable.CONTENT_URI, null,
                condition, null, JobCardTable.Cols.ACCOUNT_NO + " ASC");
        ArrayList<JobCard> jobCards = getJobCardListFromCursor(cursor);
        if (cursor != null) {
            cursor.close();
        }
        return jobCards;
    }

    public static ArrayList<JobCard> getJobCards(String reader_id, String jobCardStatus) {
        SQLiteDatabase db = DatabaseProvider.dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from " + JobCardTable.TABLE_NAME + " where " + JobCardTable.Cols.JOB_CARD_STATUS + "='" + jobCardStatus + "' AND " + JobCardTable.Cols.JOB_CARD_ID + " IN(Select DISTINCT " + MeterReadingTable.Cols.JOB_CARD_ID + " from " + MeterReadingTable.TABLE_NAME + " where " + MeterReadingTable.Cols.METER_READER_ID + "='" + reader_id + "')", null);
        ArrayList<JobCard> jobCards = getJobCardListFromCursor(cursor);
        if (cursor != null) {
            cursor.close();
        }
        if (db.isOpen())
            db.close();
        return jobCards;
    }

    public static ArrayList<JobCard> getJobCard(String reader_id, String jobCardStatus, String jobCardId) {
        SQLiteDatabase db = DatabaseProvider.dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from " + JobCardTable.TABLE_NAME + " where " + JobCardTable.Cols.JOB_CARD_STATUS + "='" + jobCardStatus + "' AND " + JobCardTable.Cols.JOB_CARD_ID + " IN(Select DISTINCT " + MeterReadingTable.Cols.JOB_CARD_ID + " from " + MeterReadingTable.TABLE_NAME + " where " + MeterReadingTable.Cols.METER_READER_ID + "='" + reader_id + "' AND " + MeterReadingTable.Cols.JOB_CARD_ID + " = '" + jobCardId + "')", null);
        ArrayList<JobCard> jobCards = getJobCardListFromCursor(cursor);
        if (cursor != null) {
            cursor.close();
        }
        if (db.isOpen())
            db.close();
        return jobCards;
    }

    public static ArrayList<JobCard> getJobCardsFilter(String reader_id, String jobCardStatus, String revist, String binder) {
        SQLiteDatabase db = DatabaseProvider.dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + JobCardTable.TABLE_NAME + " where " + JobCardTable.Cols.METER_READER_ID + "='" + reader_id + "' and " + JobCardTable.Cols.JOB_CARD_STATUS + "='" + jobCardStatus + "' and " + JobCardTable.Cols.IS_REVISIT + "='" + revist + "' and " + JobCardTable.Cols.ROUTE_ID + "='" + binder + "' order by cast(" + JobCardTable.Cols.ACCOUNT_NO + " as signed)", null);

        ArrayList<JobCard> jobCards = getJobCardListFromCursor(cursor);
        if (cursor != null) {
            cursor.close();
        }
        if (db.isOpen())
            db.close();
        return jobCards;
    }

    public static ArrayList<JobCard> getJobCardBySequence(String reader_id, String jobCardStatus, String prv, String routId) {
        SQLiteDatabase db = DatabaseProvider.dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from " + JobCardTable.TABLE_NAME + " where " + JobCardTable.Cols.JOB_CARD_STATUS + "='" + jobCardStatus + "' AND " + JobCardTable.Cols.PRV_SEQUENCE + " = '" + prv + "' AND " + JobCardTable.Cols.METER_READER_ID + " = '" + reader_id + "' AND " + JobCardTable.Cols.ROUTE_ID + " = '" + routId + "'", null);
        ArrayList<JobCard> jobCards = getJobCardListFromCursor(cursor);
        if (cursor != null) {
            cursor.close();
        }
        if (db.isOpen())
            db.close();
        return jobCards;
    }

    public static ArrayList<JobCard> getJobCardsCount(Context context, String reader_id, String rout) {
        String condition = JobCardTable.Cols.METER_READER_ID + "='" + reader_id + "' and " +
                JobCardTable.Cols.ROUTE_ID + "='" + rout + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(JobCardTable.CONTENT_URI, null, condition, null, null);
        ArrayList<JobCard> jobCards = getJobCardListFromCursor(cursor);
        if (cursor != null) {
            cursor.close();
        }
        return jobCards;
    }

    public static void saveJobCards(Context mContext, ArrayList<JobCard> jobCards) {
        for (JobCard jobcard : jobCards) {
            saveJobCard(mContext, jobcard);
            DatabaseManager.createNewSequence(mContext, jobcard);
        }
    }


    public static void saveBillCards(Context mContext, ArrayList<BillCard> billCards) {
        for (BillCard billcard : billCards) {
            savebillCard(mContext, billcard);
        }
    }

    public static void savebillCard(Context context, BillCard billCard) {
        if (billCard != null) {
            ContentValues values = getContentValuesBillCardTable(billCard);
            String condition = BillTable.Cols.JOB_CARD_ID + "='" + billCard.jobcard_id + "'";
            saveValues(context, BillTable.CONTENT_URI, values, condition);
        }
    }

    private static ContentValues getContentValuesBillCardTable(BillCard billCard) {
        ContentValues values = new ContentValues();
        try {
            values.put(BillTable.Cols.JOB_CARD_ID, billCard.jobcard_id != null ? billCard.jobcard_id : "");
            values.put(BillTable.Cols.BINDER_CODE, billCard.binder_code != null ? billCard.binder_code : "");
            values.put(BillTable.Cols.CYCLE_CODE, billCard.cycle_code != null ? billCard.cycle_code : "");
            values.put(BillTable.Cols.START_DATE, billCard.start_date != null ? billCard.start_date : "");
            values.put(BillTable.Cols.SUBDIVISION, billCard.subdivision_name != null ? billCard.subdivision_name : "");
            values.put(BillTable.Cols.CONSUMER_ASSIGNED, billCard.consumer_assigned != null ? billCard.consumer_assigned : "");
            values.put(BillTable.Cols.END_DATE, billCard.end_date != null ? billCard.end_date : "");
            values.put(BillTable.Cols.JOBCARD_STATUS, billCard.jobcard_status != null ? billCard.jobcard_status : "");
            values.put(BillTable.Cols.END_DATE, billCard.end_date != null ? billCard.end_date : "");
            values.put(BillTable.Cols.METER_READER_ID, billCard.meter_reader_id != null ? billCard.meter_reader_id : "");
            values.put(BillTable.Cols.REMARK, billCard.remark != null ? billCard.remark : "");
            values.put(BillTable.Cols.DISTRIBUTED, billCard.distributed != null ? billCard.distributed : "");
            values.put(BillTable.Cols.BILLMONTH, billCard.billmonth != null ? billCard.billmonth : "");
            values.put(BillTable.Cols.READING_DATE, billCard.reading_date != null ? billCard.reading_date : "");
            values.put(BillTable.Cols.BILL_RECEIVED_COUNT, billCard.bill_received_count != null ? billCard.bill_received_count : "");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    public static ArrayList<BillCard> getBillCards(Context context, String reader_id, String jobCardStatus) {
        String condition = BillTable.Cols.METER_READER_ID + "='" + reader_id + "' and " + BillTable.Cols.JOBCARD_STATUS + "='" + jobCardStatus + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(BillTable.CONTENT_URI, null,
                condition, null, null);
        ArrayList<BillCard> jobCards = getbillCardListFromCursor(cursor);
        if (cursor != null) {
            cursor.close();
        }
        return jobCards;
    }

    private static ArrayList<BillCard> getbillCardListFromCursor(Cursor cursor) {
        ArrayList<BillCard> billCards = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            BillCard user;
            billCards = new ArrayList<>();
            while (!cursor.isAfterLast()) {
                user = getBillCardFromCursor(cursor);
                billCards.add(user);
                cursor.moveToNext();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return billCards;
    }

    private static BillCard getBillCardFromCursor(Cursor cursor) {
        BillCard billCard = new BillCard();

        billCard.meter_reader_id = cursor.getString(cursor.getColumnIndex(BillTable.Cols.METER_READER_ID)) != null ? cursor.getString(cursor.getColumnIndex(BillTable.Cols.METER_READER_ID)) : "";
        billCard.binder_code = cursor.getString(cursor.getColumnIndex(BillTable.Cols.BINDER_CODE)) != null ? cursor.getString(cursor.getColumnIndex(BillTable.Cols.BINDER_CODE)) : "";
        billCard.jobcard_id = cursor.getString(cursor.getColumnIndex(BillTable.Cols.JOB_CARD_ID)) != null ? cursor.getString(cursor.getColumnIndex(BillTable.Cols.JOB_CARD_ID)) : "";
        billCard.consumer_assigned = cursor.getString(cursor.getColumnIndex(BillTable.Cols.CONSUMER_ASSIGNED)) != null ? cursor.getString(cursor.getColumnIndex(BillTable.Cols.CONSUMER_ASSIGNED)) : "";
        billCard.cycle_code = cursor.getString(cursor.getColumnIndex(BillTable.Cols.CYCLE_CODE)) != null ? cursor.getString(cursor.getColumnIndex(BillTable.Cols.CYCLE_CODE)) : "";
        billCard.start_date = cursor.getString(cursor.getColumnIndex(BillTable.Cols.START_DATE)) != null ? cursor.getString(cursor.getColumnIndex(BillTable.Cols.START_DATE)) : "";
        billCard.end_date = cursor.getString(cursor.getColumnIndex(BillTable.Cols.END_DATE)) != null ? cursor.getString(cursor.getColumnIndex(BillTable.Cols.END_DATE)) : "";
        billCard.jobcard_status = cursor.getString(cursor.getColumnIndex(BillTable.Cols.JOBCARD_STATUS)) != null ? cursor.getString(cursor.getColumnIndex(BillTable.Cols.JOBCARD_STATUS)) : "";
        billCard.subdivision_name = cursor.getString(cursor.getColumnIndex(BillTable.Cols.SUBDIVISION)) != null ? cursor.getString(cursor.getColumnIndex(BillTable.Cols.SUBDIVISION)) : "";
        billCard.remark = cursor.getString(cursor.getColumnIndex(BillTable.Cols.REMARK)) != null ? cursor.getString(cursor.getColumnIndex(BillTable.Cols.REMARK)) : "";
        billCard.distributed = cursor.getString(cursor.getColumnIndex(BillTable.Cols.DISTRIBUTED)) != null ? cursor.getString(cursor.getColumnIndex(BillTable.Cols.DISTRIBUTED)) : "";
        billCard.billmonth = cursor.getString(cursor.getColumnIndex(BillTable.Cols.BILLMONTH)) != null ? cursor.getString(cursor.getColumnIndex(BillTable.Cols.BILLMONTH)) : "";
        billCard.reading_date = cursor.getString(cursor.getColumnIndex(BillTable.Cols.READING_DATE)) != null ? cursor.getString(cursor.getColumnIndex(BillTable.Cols.READING_DATE)) : "";
        billCard.bill_received_count = cursor.getString(cursor.getColumnIndex(BillTable.Cols.BILL_RECEIVED_COUNT)) != null ? cursor.getString(cursor.getColumnIndex(BillTable.Cols.BILL_RECEIVED_COUNT)) : "";

        return billCard;
    }

    public static void handleAssignedDeassignedBillJobs(Context mContext, ArrayList<String> re_de_assigned_jobcards, String meter_reader_id) {
        for (String card_id : re_de_assigned_jobcards) {
            deletebillJobCard(mContext, card_id, meter_reader_id);
        }
    }

    public static void deleteBillJobs(Context mContext, ArrayList<BillCard> jobcards, String meter_reader_id) {
        for (BillCard card_id : jobcards) {
            deletebillJobCard(mContext, card_id.jobcard_id, meter_reader_id);
        }
    }

    public static void deletebillJobCard(Context context, String card_id, String meter_reader_id) {
        try {
            String condition = BillTable.Cols.METER_READER_ID + "='" + meter_reader_id + "' AND " + BillTable.Cols.JOB_CARD_ID + "='" + card_id + "'";
            context.getContentResolver().delete(BillTable.CONTENT_URI, condition, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteJobCard(Context context, String card_id, String meter_reader_id) {
        try {
            String condition = JobCardTable.Cols.METER_READER_ID + "='" + meter_reader_id + "' AND " + JobCardTable.Cols.JOB_CARD_ID + "='" + card_id + "'";
            context.getContentResolver().delete(JobCardTable.CONTENT_URI, condition, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteMeterReading(Context context, String card_id, String meter_reader_id) {
        try {
            String condition = MeterReadingTable.Cols.METER_READER_ID + "='" + meter_reader_id + "' AND " + MeterReadingTable.Cols.JOB_CARD_ID + "='" + card_id + "'";
            context.getContentResolver().delete(MeterReadingTable.CONTENT_URI, condition, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void saveLoginDetails(LoginActivity loginActivity, String user_email, ArrayList<UserProfile> user_info) {
        DatabaseManager.saveUserProfile(loginActivity, user_email, user_info);
    }

    public static void handleAssignedDeassignedJobs(Context mContext, ArrayList<String> re_de_assigned_jobcards, String meter_reader_id) {
        for (String card_id : re_de_assigned_jobcards) {
            deleteJobCard(mContext, card_id, meter_reader_id);
        }
    }

    public static void deleteMeterReadings(Context mContext, ArrayList<MeterReading> readingToUpload) {
        for (MeterReading meter_reading : readingToUpload) {
            deleteMeterReading(mContext, meter_reading.job_card_id, meter_reading.meter_reader_id);
            deleteJobCard(mContext, meter_reading.job_card_id, meter_reading.meter_reader_id);
        }
    }

    public static ArrayList<Consumer> getUnBilledConsumerRecords(String meterReaderId) {
        SQLiteDatabase db = DatabaseProvider.dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from " + ConsumerTable.TABLE_NAME + " where " + ConsumerTable.Cols.METER_READER_ID + "='" + meterReaderId.trim() + "'", null);
        ArrayList<Consumer> consumerList = getConsumersFromCursor(cursor);
        Consumer user = null;
        if (cursor != null) {
            cursor.close();
        }
        if (db.isOpen()) {
            db.close();
        }
        return consumerList;
    }

    public static void saveNotification(Context context, NotificationCard notificationCard) {
        if (notificationCard != null) {
            ContentValues values = new ContentValues();
            try {
                values.put(NotificationTable.Cols.TITLE, notificationCard.title);
                values.put(NotificationTable.Cols.MSG, notificationCard.message);
                values.put(NotificationTable.Cols.DATE, notificationCard.date);
                values.put(NotificationTable.Cols.IS_READ, "false");
                values.put(NotificationTable.Cols.METER_READER_ID, notificationCard.meter_reader_id);
            } catch (Exception e) {
                e.printStackTrace();
            }
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            long newRowId = db.insert(NotificationTable.TABLE_NAME, null, values);
            if (db.isOpen()) {
                db.close();
            }
        }
    }

    public static void setReadNotification(Context context, String title) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NotificationTable.Cols.IS_READ, "true");
        String[] args = new String[]{title};
        db.update(NotificationTable.TABLE_NAME, values, "title=?", args);
        if (db.isOpen()) {
            db.close();
        }
    }

    public static int getCount(Context context, String flag, String meterReaderId) {
        int i = 0;
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + NotificationTable.TABLE_NAME + " where  " + NotificationTable.Cols.IS_READ + " = '" + flag + "' AND " + NotificationTable.Cols.METER_READER_ID + " = '" + meterReaderId + "'";
        Cursor c = db.rawQuery(selectQuery, null);
        ArrayList<NotificationCard> notificationCards = new ArrayList<NotificationCard>();

        while (c.moveToNext()) {
            i++;
            NotificationCard notiCard = new NotificationCard();
            notiCard.title = c.getString(c.getColumnIndex("title"));
            notiCard.message = c.getString(c.getColumnIndex("msg"));
            notiCard.date = c.getString(c.getColumnIndex("date"));
            notiCard.is_read = c.getString(c.getColumnIndex("is_read"));

            notificationCards.add(notiCard);
        }
        db.close();
        return i;
    }

    public static ArrayList<NotificationCard> getAllNotification(Context context, String meterReaderId) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + NotificationTable.TABLE_NAME + " where  " + NotificationTable.Cols.METER_READER_ID + " = '" + meterReaderId + "'";
        Cursor c = db.rawQuery(selectQuery, null);
        ArrayList<NotificationCard> noti = new ArrayList<NotificationCard>();

        while (c.moveToNext()) {
            NotificationCard notiCard = new NotificationCard();
            notiCard.title = c.getString(c.getColumnIndex("title"));
            notiCard.message = c.getString(c.getColumnIndex("msg"));
            notiCard.date = c.getString(c.getColumnIndex("date"));
            notiCard.is_read = c.getString(c.getColumnIndex("is_read"));
            noti.add(notiCard);
        }
        db.close();
        return noti;
    }

    public static void deleteAccount(Context context, String messageBody) {
        try {
            String condition = NotificationTable.Cols.MSG + "='" + messageBody + "'";
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.delete("Notification", condition, null);
            if (db.isOpen()) {
                db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<BillCard> getBillMeterReadings(Context context, String reader_id, int limit) {
        String condition = BillTable.Cols.METER_READER_ID + "='" + reader_id + "' and " + BillTable.Cols.JOBCARD_STATUS + "='" + AppConstants.JOB_CARD_STATUS_COMPLETED + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(BillTable.CONTENT_URI, null,
                condition, null, BillTable.Cols.METER_READER_ID + " ASC " + " LIMIT " + limit);
        ArrayList<BillCard> meterReadings = getBillMeterReadingsFromCursor(cursor);
        if (cursor != null) {
            cursor.close();
        }
        return meterReadings;
    }

    private static ArrayList<BillCard> getBillMeterReadingsFromCursor(Cursor cursor) {
        ArrayList<BillCard> meterReadings = null;
        if (cursor != null && cursor.getCount() > 0) {
            try {
                cursor.moveToFirst();
                BillCard meterReading;
                meterReadings = new ArrayList<>();
                while (!cursor.isAfterLast()) {
                    meterReading = getBillCardFromCursor(cursor);
                    meterReadings.add(meterReading);
                    cursor.moveToNext();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return meterReadings;
    }

    public static ArrayList<UploadBillHistory> getHistoryBillCards(Context context, String reader_id) {
        String condition = UploadBillHistoryTable.Cols.METER_READER_ID + "='" + reader_id + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(UploadBillHistoryTable.CONTENT_URI, null,
                condition, null, null);
        ArrayList<UploadBillHistory> jobCards = getBillHistoryCardListFromCursor(cursor);
        if (cursor != null) {
            cursor.close();
        }
        return jobCards;
    }

    private static ArrayList<UploadBillHistory> getBillHistoryCardListFromCursor(Cursor cursor) {
        ArrayList<UploadBillHistory> billCards = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            UploadBillHistory user;
            billCards = new ArrayList<UploadBillHistory>();
            while (!cursor.isAfterLast()) {
                user = getBillHistoryCardFromCursor(cursor);
                billCards.add(user);
                cursor.moveToNext();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return billCards;
    }

    private static UploadBillHistory getBillHistoryCardFromCursor(Cursor cursor) {
        UploadBillHistory billCard = new UploadBillHistory();

        billCard.meter_reader_id = cursor.getString(cursor.getColumnIndex(UploadBillHistoryTable.Cols.METER_READER_ID)) != null ? cursor.getString(cursor.getColumnIndex(UploadBillHistoryTable.Cols.METER_READER_ID)) : "";
        billCard.binder_code = cursor.getString(cursor.getColumnIndex(UploadBillHistoryTable.Cols.BINDER_CODE)) != null ? cursor.getString(cursor.getColumnIndex(UploadBillHistoryTable.Cols.BINDER_CODE)) : "";
        billCard.jobcard_id = cursor.getString(cursor.getColumnIndex(UploadBillHistoryTable.Cols.JOBCARD_ID)) != null ? cursor.getString(cursor.getColumnIndex(UploadBillHistoryTable.Cols.JOBCARD_ID)) : "";
        billCard.consumer_assigned = cursor.getString(cursor.getColumnIndex(UploadBillHistoryTable.Cols.CONSUMER_ASSIGNED)) != null ? cursor.getString(cursor.getColumnIndex(UploadBillHistoryTable.Cols.CONSUMER_ASSIGNED)) : "";
        billCard.cycle_code = cursor.getString(cursor.getColumnIndex(UploadBillHistoryTable.Cols.CYCLE_CODE)) != null ? cursor.getString(cursor.getColumnIndex(UploadBillHistoryTable.Cols.CYCLE_CODE)) : "";
        billCard.subdivision_name = cursor.getString(cursor.getColumnIndex(UploadBillHistoryTable.Cols.SUBDIVISION)) != null ? cursor.getString(cursor.getColumnIndex(UploadBillHistoryTable.Cols.SUBDIVISION)) : "";
        billCard.distributed = cursor.getString(cursor.getColumnIndex(UploadBillHistoryTable.Cols.DISTRIBUTED)) != null ? cursor.getString(cursor.getColumnIndex(BillTable.Cols.DISTRIBUTED)) : "";
        billCard.billmonth = cursor.getString(cursor.getColumnIndex(UploadBillHistoryTable.Cols.BILLMONTH)) != null ? cursor.getString(cursor.getColumnIndex(UploadBillHistoryTable.Cols.BILLMONTH)) : "";
        billCard.reading_date = cursor.getString(cursor.getColumnIndex(UploadBillHistoryTable.Cols.READING_DATE)) != null ? cursor.getString(cursor.getColumnIndex(UploadBillHistoryTable.Cols.READING_DATE)) : "";
        return billCard;
    }

    public static ArrayList<SummaryCard> getSummaryCard(Context context, String reader_id, String binder) {

        ArrayList<SummaryCard> summaryCardArrayList = new ArrayList<>();

        ArrayList<String> routes = getRoutes(context, reader_id);
        String route_id = "";
        String billcycle = "";
        if (routes != null)
            for (int i = 0; i < routes.size(); i++) {
                SummaryCard lSummaryCard = new SummaryCard();
                route_id = routes.get(i);
                ArrayList<String> bill = getBillCycleCode(context, reader_id, route_id);
                billcycle = bill.get(0);

                lSummaryCard.route_id = route_id;
                lSummaryCard.bill_cycle_code = billcycle;

                //calculate revisit job cards inside single route
                String conditionTotal = JobCardTable.Cols.METER_READER_ID + "='" + reader_id + "' AND "
                        + JobCardTable.Cols.ROUTE_ID + "='" + route_id + "'";

                ContentResolver resolver = context.getContentResolver();
                Cursor cursorTotal = resolver.query(JobCardTable.CONTENT_URI, null,
                        conditionTotal, null, null);

                if (cursorTotal != null) {
                    lSummaryCard.total = cursorTotal.getCount();
                } else {
                    lSummaryCard.total = 0;
                }
                if (cursorTotal != null) {
                    cursorTotal.close();
                }

                //calculate Open job cards inside single route
                String conditionOpen = JobCardTable.Cols.METER_READER_ID + "='" + reader_id + "' AND "
                        + JobCardTable.Cols.ROUTE_ID + "='" + route_id + "' AND "
                        + JobCardTable.Cols.JOB_CARD_STATUS + "='" + AppConstants.JOB_CARD_STATUS_ALLOCATED + "' AND "
                        + JobCardTable.Cols.IS_REVISIT + "='False'";

                Cursor cursorOpen = resolver.query(JobCardTable.CONTENT_URI, null,
                        conditionOpen, null, null);

                if (cursorOpen != null) {
                    lSummaryCard.open = cursorOpen.getCount();
                } else {
                    lSummaryCard.open = 0;
                }
                if (cursorOpen != null) {
                    cursorOpen.close();
                }

                //calculate Open job cards inside single route
                String conditionRevisit = JobCardTable.Cols.METER_READER_ID + "='" + reader_id + "' AND "
                        + JobCardTable.Cols.ROUTE_ID + "='" + route_id + "' AND "
                        + JobCardTable.Cols.JOB_CARD_STATUS + "='" + AppConstants.JOB_CARD_STATUS_ALLOCATED + "' AND "
                        + JobCardTable.Cols.IS_REVISIT + "='True'";

                Cursor cursorRevisit = resolver.query(JobCardTable.CONTENT_URI, null,
                        conditionRevisit, null, null);

                if (conditionRevisit != null) {
                    lSummaryCard.revisit = cursorRevisit.getCount();
                } else {
                    lSummaryCard.revisit = 0;
                }
                if (conditionRevisit != null) {
                    cursorRevisit.close();
                }

                //calculate completed job cards inside single route
                if (conditionRevisit == null || lSummaryCard.revisit == 0) {
                    lSummaryCard.completed = lSummaryCard.total - lSummaryCard.open;
                } else {
                    lSummaryCard.completed = lSummaryCard.total - lSummaryCard.open - lSummaryCard.revisit;
                }


                summaryCardArrayList.add(lSummaryCard);
            }

        return summaryCardArrayList;
    }

    public static ArrayList<String> getTotalRoutes(Context context, String reader_id) {
        String condition = JobCardTable.Cols.METER_READER_ID + "='" + reader_id + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(JobCardTable.CONTENT_URI, new String[]{"DISTINCT " + JobCardTable.Cols.ROUTE_ID},
                condition, null, null);
        ArrayList<String> routes = null;

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            routes = new ArrayList<String>();
            while (!cursor.isAfterLast()) {
                String route_code = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.ROUTE_ID));
                routes.add(route_code);
                cursor.moveToNext();
            }
        }

        if (cursor != null) {
            cursor.close();
        }

        return routes;
    }

    public static ArrayList<String> getTotalBillCycleCode(Context context, String reader_id, String route) {
        String condition = JobCardTable.Cols.METER_READER_ID + "='" + reader_id + "' AND " +
                JobCardTable.Cols.ROUTE_ID + "='" + route + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(JobCardTable.CONTENT_URI, new String[]{"DISTINCT " + JobCardTable.Cols.BILL_CYCLE_CODE},
                condition, null, null);
        ArrayList<String> billcyclecode = null;

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            billcyclecode = new ArrayList<String>();
            while (!cursor.isAfterLast()) {
                String bill_cycle_code = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.BILL_CYCLE_CODE));
                billcyclecode.add(bill_cycle_code);
                cursor.moveToNext();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return billcyclecode;
    }


    public static void createNewSequence(Context context, JobCard seq) {
        if (seq != null) {
            Sequence sequence = new Sequence();
            sequence.cycle_code = seq.bill_cycle_code;
            sequence.route_code = seq.route_code;
            sequence.sequence = seq.current_sequence;
            sequence.meter_reader_id = seq.meter_reader_id;
            sequence.zone_code = seq.zone_code;
            String condition = SequenceTable.Cols.METER_READER_ID + "='" + sequence.meter_reader_id + "' And " +
                    SequenceTable.Cols.ZONE_CODE + "='" + sequence.zone_code + "' AND " +
                    SequenceTable.Cols.BINDER_CODE + "='" + sequence.route_code + "' AND " +
                    SequenceTable.Cols.CYCLE_CODE + "='" + sequence.cycle_code + "'";
            ContentValues values = getContentValuesSequenceTable(sequence);
            saveValues(context, SequenceTable.CONTENT_URI, values, condition);
        }
    }

    private static ContentValues getContentValuesSequenceTable(Sequence sequence) {
        ContentValues values = new ContentValues();
        try {

            values.put(SequenceTable.Cols.ZONE_CODE, sequence.zone_code != null ? sequence.zone_code : "");
            values.put(SequenceTable.Cols.CYCLE_CODE, sequence.cycle_code != null ? sequence.cycle_code : "");
            values.put(SequenceTable.Cols.BINDER_CODE, sequence.route_code != null ? sequence.route_code : "");
            values.put(SequenceTable.Cols.METER_READER_ID, sequence.meter_reader_id != null ? sequence.meter_reader_id : "");
            values.put(SequenceTable.Cols.SEQUENCE, sequence.sequence != null ? sequence.sequence : "");


        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    public static void UpdateSequence(Context context, Sequence sequence) {
        if (sequence != null) {
            ContentValues values = getContentValuesSequenceTable(sequence);
            String condition = SequenceTable.Cols.METER_READER_ID + "='" + sequence.meter_reader_id + "' And " +
                    SequenceTable.Cols.ZONE_CODE + "='" + sequence.zone_code + "' AND " +
                    SequenceTable.Cols.BINDER_CODE + "='" + sequence.route_code + "' AND " +
                    SequenceTable.Cols.CYCLE_CODE + "='" + sequence.cycle_code + "'";
            saveValues(context, SequenceTable.CONTENT_URI, values, condition);
        }
    }

    public static void deleteSequence(Context context, Sequence sequence) {
        String condition = SequenceTable.Cols.METER_READER_ID + "='" + sequence.meter_reader_id + "' And " +
                SequenceTable.Cols.ZONE_CODE + "='" + sequence.zone_code + "' AND " +
                SequenceTable.Cols.BINDER_CODE + "='" + sequence.route_code + "' AND " +
                SequenceTable.Cols.CYCLE_CODE + "='" + sequence.cycle_code + "'";
        ContentResolver resolver = context.getContentResolver();
        resolver.delete(SequenceTable.CONTENT_URI, condition, null);
    }

    public static ArrayList<Sequence> getSequence(Context context, Sequence sequence) {
        String condition = SequenceTable.Cols.METER_READER_ID + "='" + sequence.meter_reader_id + "' And " +
                SequenceTable.Cols.ZONE_CODE + "='" + sequence.zone_code + "' AND " +
                SequenceTable.Cols.BINDER_CODE + "='" + sequence.route_code + "' AND " +
                SequenceTable.Cols.CYCLE_CODE + "='" + sequence.cycle_code + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(SequenceTable.CONTENT_URI, null,
                condition, null, null);
        ArrayList<Sequence> seq = getSequence(cursor);
        if (cursor != null) {
            cursor.close();
        }
        return seq;
    }

    public static ArrayList<Sequence> getAllSequence(Context context, String mr) {
        String condition = SequenceTable.Cols.METER_READER_ID + "='" + mr + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(SequenceTable.CONTENT_URI, null,
                condition, null, null);
        ArrayList<Sequence> seq = getSequence(cursor);
        if (cursor != null) {
            cursor.close();
        }
        return seq;
    }

    private static ArrayList<Sequence> getSequence(Cursor cursor) {
        ArrayList<Sequence> jobCards = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            Sequence user;
            jobCards = new ArrayList<Sequence>();
            while (!cursor.isAfterLast()) {
                user = getSequenceFromCursor(cursor);
                jobCards.add(user);
                cursor.moveToNext();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return jobCards;
    }

    private static Sequence getSequenceFromCursor(Cursor cursor) {
        Sequence jobCard = new Sequence();
        jobCard.meter_reader_id = cursor.getString(cursor.getColumnIndex(SequenceTable.Cols.METER_READER_ID)) != null ? cursor.getString(cursor.getColumnIndex(SequenceTable.Cols.METER_READER_ID)) : "";
        jobCard.zone_code = cursor.getString(cursor.getColumnIndex(SequenceTable.Cols.ZONE_CODE)) != null ? cursor.getString(cursor.getColumnIndex(SequenceTable.Cols.ZONE_CODE)) : "";
        jobCard.route_code = cursor.getString(cursor.getColumnIndex(SequenceTable.Cols.BINDER_CODE)) != null ? cursor.getString(cursor.getColumnIndex(SequenceTable.Cols.BINDER_CODE)) : "";
        jobCard.sequence = cursor.getString(cursor.getColumnIndex(SequenceTable.Cols.SEQUENCE)) != null ? cursor.getString(cursor.getColumnIndex(SequenceTable.Cols.SEQUENCE)) : "";
        jobCard.cycle_code = cursor.getString(cursor.getColumnIndex(SequenceTable.Cols.CYCLE_CODE)) != null ? cursor.getString(cursor.getColumnIndex(SequenceTable.Cols.CYCLE_CODE)) : "";

        return jobCard;
    }

    public static ArrayList<String> getJobCardsSearchByAddress(Context context, String query, String reader_id, String jobCardStatus) {
        String condition = JobCardTable.Cols.METER_READER_ID + "='" + reader_id + "' and " +
                JobCardTable.Cols.JOB_CARD_STATUS + "='" + jobCardStatus + "' AND ( " +
                JobCardTable.Cols.STREET + " LIKE '%" + query + "%' OR " +
                JobCardTable.Cols.CONSUMER_NO + " LIKE '%" + query + "%' OR " +
                JobCardTable.Cols.ACCOUNT_NO + " LIKE '%" + query + "%' OR " +
                JobCardTable.Cols.METER_ID + " LIKE '%" + query + "%' OR " +
                JobCardTable.Cols.CONSUMER_NAME + " LIKE '%" + query + "%' ) ";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(JobCardTable.CONTENT_URI, new String[]{"DISTINCT " + JobCardTable.Cols.BUILDING_NO, JobCardTable.Cols.STREET},
                condition, null, null);
        ArrayList<String> routes = null;

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            routes = new ArrayList<>();
            while (!cursor.isAfterLast()) {
                String buildingNo = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.BUILDING_NO));
                String street = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.STREET));
                routes.add(street + "(" + buildingNo + ")");
                cursor.moveToNext();
            }
        }

        if (cursor != null) {
            cursor.close();
        }
        return routes;
    }

    public static ArrayList<String> getJobCardsSearchByAddressFilter(Context context, String query, String reader_id, String jobCardStatus, String binder) {
        String condition = JobCardTable.Cols.METER_READER_ID + "='" + reader_id + "' AND " +
                JobCardTable.Cols.ROUTE_ID + "='" + binder + "' AND " +
                JobCardTable.Cols.JOB_CARD_STATUS + "='" + jobCardStatus + "' AND ( " +
                JobCardTable.Cols.STREET + " LIKE '%" + query + "%' OR " +
                JobCardTable.Cols.ACCOUNT_NO + " LIKE '%" + query + "%' OR " +
                JobCardTable.Cols.CONSUMER_NO + " LIKE '%" + query + "%' OR " +
                JobCardTable.Cols.METER_ID + " LIKE '%" + query + "%' OR " +
                JobCardTable.Cols.CONSUMER_NAME + " LIKE '%" + query + "%' ) ";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(JobCardTable.CONTENT_URI, new String[]{"DISTINCT " + JobCardTable.Cols.BUILDING_NO, JobCardTable.Cols.STREET},
                condition, null, null);
        ArrayList<String> routes = null;

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            routes = new ArrayList<>();
            while (!cursor.isAfterLast()) {
                String buildingNo = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.BUILDING_NO));
                String street = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.STREET));
                routes.add(street + "(" + buildingNo + ")");
                cursor.moveToNext();
            }
        }

        if (cursor != null) {
            cursor.close();
        }
        return routes;
    }

    public static ArrayList<JobCard> getJobCardsSearchByAddress(Context context, String query, String reader_id, String jobCardStatus, String buildingNo, String street) {

        String condition = JobCardTable.Cols.METER_READER_ID + "='" + reader_id + "' and " +
                JobCardTable.Cols.STREET + "='" + street + "' AND " +
                JobCardTable.Cols.BUILDING_NO + "='" + buildingNo + "' AND " +
                JobCardTable.Cols.JOB_CARD_STATUS + "='" + jobCardStatus + "' AND (" +
                JobCardTable.Cols.STREET + " LIKE '%" + query + "%' OR " +
                JobCardTable.Cols.ACCOUNT_NO + " LIKE '%" + query + "%' OR " +
                JobCardTable.Cols.CONSUMER_NO + " LIKE '%" + query + "%' OR " +
                JobCardTable.Cols.METER_ID + " LIKE '%" + query + "%' OR " +
                JobCardTable.Cols.CONSUMER_NAME + " LIKE '%" + query + "%' ) ";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(JobCardTable.CONTENT_URI, null, condition, null, null);
        ArrayList<JobCard> jobCards = getJobCardListFromCursor(cursor);
        if (cursor != null) {
            cursor.close();
        }
        return jobCards;
    }

    public static void saveUploadDisconnectionNotices(Context context, UploadDisconnectionNotices uploadDisconnectionNotices) {
        if (uploadDisconnectionNotices != null) {
            ContentValues values = getContentValuesUploadDisconnectionTable(context, uploadDisconnectionNotices);
            saveUploadDisconnectionNotices(context, UploadDisconnectionTable.CONTENT_URI, values);
        }
    }

    private static ContentValues getContentValuesDisconnectionTable(Context context, Disconnection disconnection) {
        ContentValues values = new ContentValues();
        try {
            values.put(DisconnectionTable.Cols.METER_READER_ID, disconnection.meter_reader_id != null ? disconnection.meter_reader_id : "");
            values.put(DisconnectionTable.Cols.BINDER_CODE, disconnection.binder_code != null ? disconnection.binder_code : "");
            values.put(DisconnectionTable.Cols.CONSUMER_NO, disconnection.consumer_no != null ? disconnection.consumer_no : "");
            values.put(DisconnectionTable.Cols.NAME, disconnection.consumer_name != null ? disconnection.consumer_name : "");
            values.put(DisconnectionTable.Cols.ADDRESS, disconnection.address != null ? disconnection.address : "");
            values.put(DisconnectionTable.Cols.CONTACT_NO, disconnection.contact_no != null ? disconnection.contact_no : "");
            values.put(DisconnectionTable.Cols.LATITUDE, disconnection.latitude != null ? disconnection.latitude : "");
            values.put(DisconnectionTable.Cols.LONGITUDE, disconnection.longitude != null ? disconnection.longitude : "");
            values.put(DisconnectionTable.Cols.JOB_CARD_STATUS, disconnection.job_card_status != null ? disconnection.job_card_status : "");
            values.put(DisconnectionTable.Cols.JOB_CARD_ID, disconnection.job_card_id != null ? disconnection.job_card_id : "");
            values.put(DisconnectionTable.Cols.ZONE_CODE, disconnection.zone_code != null ? disconnection.zone_code : "");
            values.put(DisconnectionTable.Cols.BILL_MONTH, disconnection.bill_month != null ? disconnection.bill_month : "");
            values.put(DisconnectionTable.Cols.DUE_DATE, disconnection.due_date != null ? disconnection.due_date : "");
            values.put(DisconnectionTable.Cols.NOTICE_DATE, disconnection.notice_date != null ? disconnection.notice_date : "");
            values.put(DisconnectionTable.Cols.DISCONNECTION_NOTICE_NO, disconnection.disconnection_notice_no != null ? disconnection.disconnection_notice_no : "");
            values.put(DisconnectionTable.Cols.TOTOS, disconnection.totos != null ? disconnection.totos : "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    private static void saveUploadDisconnectionNotices(Context context, Uri table, ContentValues values) {
        ContentResolver resolver = context.getContentResolver();
        try {
            resolver.insert(table, values);
            Toast.makeText(context, CommonUtils.getString(context, R.string.disconnection_notice_data_saved_successfully), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(context, CommonUtils.getString(context, R.string.failed_to_save_data), Toast.LENGTH_LONG).show();
        }
    }

    private static ContentValues getContentValuesUploadDisconnectionTable(Context context, UploadDisconnectionNotices disconnection) {
        ContentValues values = new ContentValues();
        try {
            values.put(UploadDisconnectionTable.Cols.METER_READER_ID, disconnection.meter_reader_id != null ? disconnection.meter_reader_id : "");
            values.put(UploadDisconnectionTable.Cols.BINDER_CODE, disconnection.binder_code != null ? disconnection.binder_code : "");
            values.put(UploadDisconnectionTable.Cols.CONSUMER_NO, disconnection.consumer_no != null ? disconnection.consumer_no : "");
            values.put(UploadDisconnectionTable.Cols.NAME, disconnection.consumer_name != null ? disconnection.consumer_name : "");
            values.put(UploadDisconnectionTable.Cols.CURRENT_LATITUDE, disconnection.current_latitude != null ? disconnection.current_latitude : "");
            values.put(UploadDisconnectionTable.Cols.CURRENT_LONGITUDE, disconnection.current_longitude != null ? disconnection.current_longitude : "");
            values.put(UploadDisconnectionTable.Cols.JOB_CARD_ID, disconnection.job_card_id != null ? disconnection.job_card_id : "");
            values.put(UploadDisconnectionTable.Cols.ZONE_CODE, disconnection.zone_code != null ? disconnection.zone_code : "");
            values.put(UploadDisconnectionTable.Cols.BILL_MONTH, disconnection.bill_month != null ? disconnection.bill_month : "");
            values.put(UploadDisconnectionTable.Cols.CURRENT_DATE, disconnection.current_date != null ? disconnection.current_date : "");
            values.put(UploadDisconnectionTable.Cols.DELIVERY_STATUS, disconnection.delivery_status != null ? disconnection.delivery_status : "");
            values.put(UploadDisconnectionTable.Cols.DELIVERY_REMARK, disconnection.delivery_remark != null ? disconnection.delivery_remark : "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    public static void updateDCJobCardStatus(Context context, Disconnection disconnection, String status) {
        if (disconnection != null)

        {
            ContentValues values = getContentValuesDisconnectionTable(context, disconnection);
            values.put(DisconnectionTable.Cols.JOB_CARD_STATUS, status);
            // values.put(JobCardTable.Cols.IS_REVISIT, "False");
            String condition = DisconnectionTable.Cols.JOB_CARD_ID + "='" + disconnection.job_card_id + "'";
            saveValues(context, DisconnectionTable.CONTENT_URI, values, condition);
        }
    }

    public static ArrayList<UploadDisconnectionNotices> getCompletedDCNoticesCards(Context context, String reader_id) {
        String condition = UploadDisconnectionTable.Cols.METER_READER_ID + "='" + reader_id + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(UploadDisconnectionTable.CONTENT_URI, null,
                condition, null, null);
        ArrayList<UploadDisconnectionNotices> uploadDisconnectionNotices = getCompletedDCNoticesCardsFromCursor(cursor);
        if (cursor != null) {
            cursor.close();
        }
        return uploadDisconnectionNotices;
    }


    private static ArrayList<UploadDisconnectionNotices> getCompletedDCNoticesCardsFromCursor(Cursor cursor) {
        ArrayList<UploadDisconnectionNotices> uploadDisconnectionNotices = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            UploadDisconnectionNotices user;
            uploadDisconnectionNotices = new ArrayList<>();
            while (!cursor.isAfterLast()) {
                user = getUploadDCNoticesFromCursor(cursor);
                uploadDisconnectionNotices.add(user);
                cursor.moveToNext();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return uploadDisconnectionNotices;
    }

    private static UploadDisconnectionNotices getUploadDCNoticesFromCursor(Cursor cursor) {
        UploadDisconnectionNotices uploadDisconnectionNotices = new UploadDisconnectionNotices();
        uploadDisconnectionNotices.meter_reader_id = cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.METER_READER_ID)) != null ? cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.METER_READER_ID)) : "";
        uploadDisconnectionNotices.binder_code = cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.BINDER_CODE)) != null ? cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.BINDER_CODE)) : "";
        uploadDisconnectionNotices.consumer_no = cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.CONSUMER_NO)) != null ? cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.CONSUMER_NO)) : "";
        uploadDisconnectionNotices.consumer_name = cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.NAME)) != null ? cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.NAME)) : "";
        uploadDisconnectionNotices.current_latitude = cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.CURRENT_LATITUDE)) != null ? cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.CURRENT_LATITUDE)) : "";
        uploadDisconnectionNotices.current_longitude = cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.CURRENT_LONGITUDE)) != null ? cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.CURRENT_LONGITUDE)) : "";
        uploadDisconnectionNotices.job_card_id = cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.JOB_CARD_ID)) != null ? cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.JOB_CARD_ID)) : "";
        uploadDisconnectionNotices.zone_code = cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.ZONE_CODE)) != null ? cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.ZONE_CODE)) : "";
        uploadDisconnectionNotices.bill_month = cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.BILL_MONTH)) != null ? cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.BILL_MONTH)) : "";
        uploadDisconnectionNotices.current_date = cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.CURRENT_DATE)) != null ? cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.CURRENT_DATE)) : "";
        uploadDisconnectionNotices.delivery_status = cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.DELIVERY_STATUS)) != null ? cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.DELIVERY_STATUS)) : "";
        uploadDisconnectionNotices.delivery_remark = cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.DELIVERY_REMARK)) != null ? cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.DELIVERY_REMARK)) : "";

        return uploadDisconnectionNotices;
    }

    public static ArrayList<UploadDisconnectionNotices> getDisconnectionNotices(Context context, String reader_id, int limit) {
//        String condition = DisconnectionTable.Cols.METER_READER_ID + "='" + reader_id + "' and " + DisconnectionTable.Cols.IS_UPLOADED + "='False'";
        String condition = UploadDisconnectionTable.Cols.METER_READER_ID + "='" + reader_id + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(UploadDisconnectionTable.CONTENT_URI, null,
                condition, null, UploadDisconnectionTable.Cols.METER_READER_ID + " ASC " + " LIMIT " + limit);
        ArrayList<UploadDisconnectionNotices> disconnectionNotices = getDCNoticesFromCursor(context, cursor);
        if (cursor != null) {
            cursor.close();
        }
        return disconnectionNotices;
    }

    private static ArrayList<UploadDisconnectionNotices> getDCNoticesFromCursor(Context context, Cursor cursor) {
        ArrayList<UploadDisconnectionNotices> uploadDisconnectionNotices = null;
        if (cursor != null && cursor.getCount() > 0) {
            try {
                cursor.moveToFirst();
                UploadDisconnectionNotices uploadDisconnectionNotices1;
                uploadDisconnectionNotices = new ArrayList<UploadDisconnectionNotices>();
                while (!cursor.isAfterLast()) {
                    uploadDisconnectionNotices1 = getUploadDCNoticesFromCursor(cursor);
                    uploadDisconnectionNotices.add(uploadDisconnectionNotices1);
                    cursor.moveToNext();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (cursor != null) {
            cursor.close();
        }

        return uploadDisconnectionNotices;
    }

    public static void handleDeassignedDCNotices(Context mContext, ArrayList<String> re_de_assigned_jobcards, String meter_reader_id) {
        for (String card_id : re_de_assigned_jobcards) {
            deleteDCNoticeCard(mContext, card_id, meter_reader_id, 0);
        }
    }

    public static void deleteDCNoticeCard(Context context, String card_id, String meter_reader_id, int fromTable) {
        try {
            if (fromTable == 0) {
                String condition = DisconnectionTable.Cols.METER_READER_ID + "='" + meter_reader_id + "' AND " + DisconnectionTable.Cols.JOB_CARD_ID + "='" + card_id + "'";
                context.getContentResolver().delete(DisconnectionTable.CONTENT_URI, condition, null);
            } else if (fromTable == 1) {
                String condition = UploadDisconnectionTable.Cols.METER_READER_ID + "='" + meter_reader_id + "' AND " + UploadDisconnectionTable.Cols.JOB_CARD_ID + "='" + card_id + "'";
                context.getContentResolver().delete(UploadDisconnectionTable.CONTENT_URI, condition, null);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteDCNotices(Context mContext, ArrayList<UploadDisconnectionNotices> uploadDisconnectionNotices, String meter_reader_id) {

        for (UploadDisconnectionNotices disconnectionNotices : uploadDisconnectionNotices) {
            deleteDCNoticeCard(mContext, disconnectionNotices.job_card_id, meter_reader_id, 1);
        }
    }

    public static void saveDCNoticesHistory(Context context, DisconnectionHistory disconnectionHistory) {
        if (disconnectionHistory != null) {
            ContentValues values = getContentValuesDisconnectionHistoryTable(disconnectionHistory);
            String condition = DisconnectionHistoryTable.Cols.METER_READER_ID + "='" + disconnectionHistory.meter_reader_id + "' AND "
                    + DisconnectionHistoryTable.Cols.JOB_CARD_ID + "='" + disconnectionHistory.job_card_id + "'";
            saveValues(context, DisconnectionHistoryTable.CONTENT_URI, values, condition);
        }
    }

    private static ContentValues getContentValuesDisconnectionHistoryTable(DisconnectionHistory disconnectionHistory) {
        ContentValues values = new ContentValues();
        try {
            values.put(DisconnectionHistoryTable.Cols.METER_READER_ID, disconnectionHistory.meter_reader_id != null ? disconnectionHistory.meter_reader_id : "");
            values.put(DisconnectionHistoryTable.Cols.BINDER_CODE, disconnectionHistory.binder_code != null ? disconnectionHistory.binder_code : "");
            values.put(DisconnectionHistoryTable.Cols.BILL_MONTH, disconnectionHistory.bill_month != null ? disconnectionHistory.bill_month : "");
            values.put(DisconnectionHistoryTable.Cols.JOB_CARD_ID, disconnectionHistory.job_card_id != null ? disconnectionHistory.job_card_id : "");
            values.put(DisconnectionHistoryTable.Cols.DATE, disconnectionHistory.date != null ? disconnectionHistory.date : "");
            values.put(DisconnectionHistoryTable.Cols.DELIVERY_STATUS, disconnectionHistory.delivery_status != null ? disconnectionHistory.delivery_status : "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    public static int deleteDCNoticesHistory(Context context, String mr) {
        String condition = CommonUtils.getPreviousDateCondition(mr);
        ContentResolver resolver = context.getContentResolver();
        int deleted = resolver.delete(UploadsHistoryTable.CONTENT_URI, condition, null);
        return deleted;
    }

    public static ArrayList<String> getDCNoticeHistoryBinders(Context context, String date) {
        ArrayList<String> routes = null;
        String condition = DisconnectionHistoryTable.Cols.DATE + "='" + date + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(DisconnectionHistoryTable.CONTENT_URI, new String[]{"DISTINCT " + DisconnectionHistoryTable.Cols.BINDER_CODE},
                condition, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            routes = new ArrayList<String>();
            while (!cursor.isAfterLast()) {
                String route_code = cursor.getString(cursor.getColumnIndex(DisconnectionHistoryTable.Cols.BINDER_CODE));
                routes.add(route_code);
                cursor.moveToNext();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return routes;
    }

    public static ArrayList<DisconnectionHistory> getDCNoticesHistory(Context context, String date, String routeId, String meterReaderId) {
        String condition = DisconnectionHistoryTable.Cols.DATE + "='" + date + "' AND "
                + DisconnectionHistoryTable.Cols.BINDER_CODE + "='" + routeId + "' AND "
                + DisconnectionHistoryTable.Cols.METER_READER_ID + "='" + meterReaderId + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(DisconnectionHistoryTable.CONTENT_URI, null,
                condition, null, null);
        ArrayList<DisconnectionHistory> uploadsHistoryFromCursor = getDCNOticeHistoryFromCursor(cursor);
        if (cursor != null) {
            cursor.close();
        }
        return uploadsHistoryFromCursor;
    }

    private static ArrayList<DisconnectionHistory> getDCNOticeHistoryFromCursor(Cursor cursor) {
        ArrayList<DisconnectionHistory> disconnectionHistories = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            DisconnectionHistory uploadsHistory;
            disconnectionHistories = new ArrayList<>();
            while (!cursor.isAfterLast()) {
                uploadsHistory = getDCNoticesHistoryFromCursor(cursor);
                disconnectionHistories.add(uploadsHistory);
                cursor.moveToNext();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return disconnectionHistories;
    }

    private static DisconnectionHistory getDCNoticesHistoryFromCursor(Cursor cursor) {
        DisconnectionHistory disconnectionHistory = new DisconnectionHistory();
        disconnectionHistory.meter_reader_id = cursor.getString(cursor.getColumnIndex(DisconnectionHistoryTable.Cols.METER_READER_ID)) != null ? cursor.getString(cursor.getColumnIndex(DisconnectionHistoryTable.Cols.METER_READER_ID)) : "";
        disconnectionHistory.binder_code = cursor.getString(cursor.getColumnIndex(DisconnectionHistoryTable.Cols.BINDER_CODE)) != null ? cursor.getString(cursor.getColumnIndex(DisconnectionHistoryTable.Cols.BINDER_CODE)) : "";
        disconnectionHistory.job_card_id = cursor.getString(cursor.getColumnIndex(DisconnectionHistoryTable.Cols.JOB_CARD_ID)) != null ? cursor.getString(cursor.getColumnIndex(DisconnectionHistoryTable.Cols.JOB_CARD_ID)) : "";
        disconnectionHistory.date = cursor.getString(cursor.getColumnIndex(DisconnectionHistoryTable.Cols.DATE)) != null ? cursor.getString(cursor.getColumnIndex(DisconnectionHistoryTable.Cols.DATE)) : "";
        disconnectionHistory.bill_month = cursor.getString(cursor.getColumnIndex(DisconnectionHistoryTable.Cols.BILL_MONTH)) != null ? cursor.getString(cursor.getColumnIndex(DisconnectionHistoryTable.Cols.BILL_MONTH)) : "";
        disconnectionHistory.delivery_status = cursor.getString(cursor.getColumnIndex(DisconnectionHistoryTable.Cols.DELIVERY_STATUS)) != null ? cursor.getString(cursor.getColumnIndex(DisconnectionHistoryTable.Cols.DELIVERY_STATUS)) : "";

        return disconnectionHistory;
    }

    public static void saveDisconnectionCards(Context mContext, ArrayList<Disconnection> disconnections) {
        for (Disconnection disconnection : disconnections) {
            saveDisconnectionCard(mContext, disconnection);
        }
    }

    public static void saveDisconnectionCard(Context context, Disconnection disconnection) {
        if (disconnection != null) {
            ContentValues values = getContentValuesDisconnectionTable(context, disconnection);
            String condition = DisconnectionTable.Cols.JOB_CARD_ID + "='" + disconnection.job_card_id + "'";
            saveDisconnectionValues(context, DisconnectionTable.CONTENT_URI, values, condition);
        }
    }

    private static void saveDisconnectionValues(Context context, Uri table, ContentValues values, String condition) {
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(table, null,
                condition, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            resolver.update(table, values, condition, null);
        } else {
            resolver.insert(table, values);
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    public static ArrayList<Disconnection> getDisconnectionOpenCards(Context context, String reader_id, String jobCardStatus) {
        String condition = DisconnectionTable.Cols.METER_READER_ID + "='" + reader_id + "' and " + DisconnectionTable.Cols.JOB_CARD_STATUS + "='" + jobCardStatus + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(DisconnectionTable.CONTENT_URI, null,
                condition, null, null);
        ArrayList<Disconnection> jobCards = getDisconnectionCardListFromCursor(cursor);
        if (cursor != null) {
            cursor.close();
        }
        return jobCards;
    }

    private static ArrayList<Disconnection> getDisconnectionCardListFromCursor(Cursor cursor) {
        ArrayList<Disconnection> disconnections = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            Disconnection user;
            disconnections = new ArrayList<Disconnection>();
            while (!cursor.isAfterLast()) {
                user = getDisconnectionNoticesFromCursor(cursor);
                disconnections.add(user);
                cursor.moveToNext();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return disconnections;
    }

    private static Disconnection getDisconnectionNoticesFromCursor(Cursor cursor) {
        Disconnection disconnection = new Disconnection();
        disconnection.meter_reader_id = cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.METER_READER_ID)) != null ? cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.METER_READER_ID)) : "";
        disconnection.binder_code = cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.BINDER_CODE)) != null ? cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.BINDER_CODE)) : "";
        disconnection.consumer_no = cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.CONSUMER_NO)) != null ? cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.CONSUMER_NO)) : "";
        disconnection.consumer_name = cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.NAME)) != null ? cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.NAME)) : "";
        disconnection.address = cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.ADDRESS)) != null ? cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.ADDRESS)) : "";
        disconnection.contact_no = cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.CONTACT_NO)) != null ? cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.CONTACT_NO)) : "";
        disconnection.latitude = cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.LATITUDE)) != null ? cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.LATITUDE)) : "";
        disconnection.longitude = cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.LONGITUDE)) != null ? cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.LONGITUDE)) : "";
        disconnection.job_card_status = cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.JOB_CARD_STATUS)) != null ? cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.JOB_CARD_STATUS)) : "";
        disconnection.job_card_id = cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.JOB_CARD_ID)) != null ? cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.JOB_CARD_ID)) : "";
        disconnection.zone_code = cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.ZONE_CODE)) != null ? cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.ZONE_CODE)) : "";
        disconnection.bill_month = cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.BILL_MONTH)) != null ? cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.BILL_MONTH)) : "";
        disconnection.due_date = cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.DUE_DATE)) != null ? cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.DUE_DATE)) : "";
        disconnection.notice_date = cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.NOTICE_DATE)) != null ? cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.NOTICE_DATE)) : "";
        disconnection.disconnection_notice_no = cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.DISCONNECTION_NOTICE_NO)) != null ? cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.DISCONNECTION_NOTICE_NO)) : "";
        disconnection.totos = cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.TOTOS)) != null ? cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.TOTOS)) : "";

        return disconnection;
    }
}