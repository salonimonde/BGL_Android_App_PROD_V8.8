package com.sgl.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

import com.sgl.R;
import com.sgl.configuration.AppConstants;
import com.sgl.db.tables.DisconnectionHistoryTable;
import com.sgl.db.tables.UploadBillHistoryTable;
import com.sgl.db.tables.UploadsHistoryTable;
import com.sgl.preferences.SharedPrefManager;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 * Created by Bynry01 on 22-08-2016.
 */
public class CommonUtils
{

    public static void hideKeyBoard(Context context)
    {
        View view = ((Activity)context).getCurrentFocus();
        if(view != null)
        {
          InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
          imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static boolean isNetworkAvailable(Context context)
    {
        final ConnectivityManager conn_manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo network_info = conn_manager.getActiveNetworkInfo();

        if (network_info != null && network_info.isConnected())
        {
            if (network_info.getType() == ConnectivityManager.TYPE_WIFI)
                return true;
            else if (network_info.getType() == ConnectivityManager.TYPE_MOBILE)
                return true;
        }

        return false;
    }

    public static void saveCredentials(Context context, String email, String authToken)
    {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        SharedPrefManager.saveValue(context, SharedPrefManager.USER_ID,email);
        SharedPrefManager.saveValue(context, SharedPrefManager.AUTH_TOKEN,authToken);
        SharedPrefManager.saveValue(context, SharedPrefManager.USER_LOGGED_IN_DATE,date);
    }

    public static void saveAuthToken(Context context, String authToken)
    {
        SharedPrefManager.saveValue(context, SharedPrefManager.AUTH_TOKEN,authToken);
    }

    public static String getBitmapEncodedString(Bitmap pBitmap)
    {
        if(pBitmap!=null)
        {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            pBitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream);
            byte[] byteArray = stream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        }
        return "";
    }

    public static boolean isLoggedIn(Context context)
    {
        String logged_in_date = SharedPrefManager.getStringValue(context, SharedPrefManager.USER_LOGGED_IN_DATE);
        if (!logged_in_date.equals(""))
        {
            String current_date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            if (logged_in_date.equals(current_date))
            {
                String uname = SharedPrefManager.getStringValue(context, SharedPrefManager.USER_ID);
                return !(uname.equals("")/* && password.equals("")*/);
            }
        }
        return false;
    }

    public static int getColor(Context context, int id)
    {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23)
        {
            return ContextCompat.getColor(context, id);
        } else
        {
            return context.getResources().getColor(id);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static void askForPermissions(final Context context, RelativeLayout ll_main_view, ArrayList<String> permissions)
    {
        ArrayList<String> permissionsToRequest = findUnAskedPermissions(context, App.getInstance().permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.
        final ArrayList<String> permissionsRejected = findRejectedPermissions(context, App.getInstance().permissions);

        if(permissionsToRequest.size()>0)
        {
            //we need to ask for permissions
            //but have we already asked for them?

            ((Activity)context).requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), AppConstants.ALL_PERMISSIONS_RESULT);

            //mark all these as asked..
            for(String perm : permissionsToRequest)
            {
                markAsAsked(context,perm);
            }
        }
        else
        {
            if(permissionsRejected.size()>0)
            {
                //we have none to request but some previously rejected..tell the user.
                //It may be better to show a dialog here in a prod application
                showPostPermissionsShackBar(context, ll_main_view, permissionsRejected);
            }
        }
    }

    public static void showPostPermissionsShackBar(final Context context, RelativeLayout ll_mail_view, final ArrayList<String> permissionsRejected)
    {
        Snackbar snackBarView = Snackbar
                .make(ll_mail_view, String.valueOf(permissionsRejected.size()) + context.getString(R.string.permission_rejected_already), Snackbar.LENGTH_LONG)
                .setAction(R.string.allow_to_ask_permission_again, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        for (String perm : permissionsRejected)
                        {
                            clearMarkAsAsked(context, perm);
                        }
                    }
                });
        ViewGroup group = (ViewGroup) snackBarView.getView();
        group.setBackgroundColor(getColor(context, R.color.colorPrimary));
        snackBarView.show();
    }

    public static boolean hasPermission(Context context, String permission)
    {
        if (canMakeSmores())
        {
            return(ContextCompat.checkSelfPermission(context,permission)== PackageManager.PERMISSION_GRANTED);
        }
        return true;
    }

    public static void markAsAsked(Context context,String permission)
    {
        SharedPrefManager.saveValue(context,permission, false);
    }

    public static void clearMarkAsAsked(Context context,String permission)
    {
        SharedPrefManager.saveValue(context,permission, true);
    }

    public static ArrayList<String> findUnAskedPermissions(Context context,ArrayList<String> wanted)
    {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted)
        {
            if (!hasPermission(context,perm) && SharedPrefManager.shouldWeAskPermission(context,perm))
            {
                result.add(perm);
            }
        }

        return result;
    }

    public static ArrayList<String> findRejectedPermissions(Context context,ArrayList<String> wanted)
    {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted)
        {
            if (!hasPermission(context,perm) && !SharedPrefManager.shouldWeAskPermission(context,perm))
            {
                result.add(perm);
            }
        }

        return result;
    }

    private static boolean canMakeSmores()
    {
        return(Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    public static void logout(Context context)
    {
        SharedPrefManager.saveValue( context, SharedPrefManager.USER_NAME,"");
        SharedPrefManager.saveValue(context, SharedPrefManager.PASSWORD,"");
        SharedPrefManager.saveValue(context, SharedPrefManager.USER_LOGGED_IN_DATE,"");
    }

    public static String getCurrentDateTime()
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getCurrentDateTime2()
    {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getTime()
    {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getCurrentDate()
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getDate()
    {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        return dateFormat.format(date);
    }
    public static String getPreviousDate(int prev)
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, -prev);
        Date newDate = calendar.getTime();
        return dateFormat.format(newDate);
    }

    public static String getPreviousDateCondition(String mr)
    {

        StringBuilder condition = new StringBuilder( UploadsHistoryTable.Cols.METER_READER_ID + "='" + mr +"' AND " +UploadsHistoryTable.Cols.READING_DATE + " NOT IN (");

        for (int i = 0; i < AppConstants.UPLOAD_HISTORY_DATE_COUNT; i++)
        {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_YEAR, -i);
            Date newDate = calendar.getTime();
            if(i+1==AppConstants.UPLOAD_HISTORY_DATE_COUNT)
            {
                condition.append("'" + dateFormat.format(newDate) + "')");
            }
            else
            {
                condition.append("'" + dateFormat.format(newDate) + "',");
            }
        }

        return String.valueOf(condition);
    }

    public static String getPreviousDateConditionBill(String mr)
    {

        StringBuilder condition = new StringBuilder( UploadBillHistoryTable.Cols.METER_READER_ID + "='" + mr +"' AND " +UploadBillHistoryTable.Cols.READING_DATE + " NOT IN (");

        for (int i = 0; i < AppConstants.UPLOAD_BILL_HISTORY_DATE_COUNT; i++)
        {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_YEAR, -i);
            Date newDate = calendar.getTime();
            if(i+1==AppConstants.UPLOAD_BILL_HISTORY_DATE_COUNT)
            {
                condition.append("'" + dateFormat.format(newDate) + "')");
            }
            else
            {
                condition.append("'" + dateFormat.format(newDate) + "',");
            }
        }

        return String.valueOf(condition);
    }

    public static String getPreviousDateConditionDCNotice(String mr)
    {

        StringBuilder condition = new StringBuilder( DisconnectionHistoryTable.Cols.METER_READER_ID + "='" + mr +"' AND " + DisconnectionHistoryTable.Cols.DATE + " NOT IN (");

        for (int i = 0; i < AppConstants.UPLOAD_BILL_HISTORY_DATE_COUNT; i++)
        {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_YEAR, -i);
            Date newDate = calendar.getTime();
            if(i+1 == AppConstants.UPLOAD_BILL_HISTORY_DATE_COUNT)
            {
                condition.append("'" + dateFormat.format(newDate) + "')");
            }
            else
            {
                condition.append("'" + dateFormat.format(newDate) + "',");
            }
        }

        return String.valueOf(condition);
    }

    public static Bitmap  addWaterMarkDate(Bitmap src, String watermark)
    {
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);

        Paint paint = new Paint();
        paint.setColor(Color.YELLOW);
        //paint.setAlpha(alpha);
        paint.setTextSize(20);
        paint.setAntiAlias(true);
        paint.setUnderlineText(false);
        canvas.drawText(watermark, 5, h-5, paint);

        return result;
    }

    public static int sizeOfBitmap(Bitmap pBitmap)
    {
        if(pBitmap!=null)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1)
        {
            return pBitmap.getRowBytes() * pBitmap.getHeight();
        }
        else
        {
            return pBitmap.getByteCount();
        }
        return 0;
    }

    public static boolean checkAndRequestPermissions(Context mContext, Activity activity)
    {
        int permissionSendMessage = ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.SEND_SMS);
        int locationPermission = ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.SEND_SMS);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (locationPermission != PackageManager.PERMISSION_GRANTED)
        {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED)
        {
            listPermissionsNeeded.add(android.Manifest.permission.SEND_SMS);
        }

        if (!listPermissionsNeeded.isEmpty())
        {
            ActivityCompat.requestPermissions(activity,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), AppConstants.REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    public static void sendSMS(String phoneNo, String sendMessage)
    {
        //Your authentication key
        String authkey = "100303Ag1chIxFw5673aa1d";
        //Multiple mobiles numbers separated by comma
        //String mobiles = "9595092582";
        //Sender ID,While using route4 sender id should be 6 characters long.
        String senderId = "Muhurt";
        //define txtBinderCode
        String route = "4";

        URLConnection myURLConnection = null;
        URL myURL = null;
        BufferedReader reader = null;

        //encoding message
        //String encoded_message = URLEncoder.encode(message);

        //Send SMS API   https://control.msg91.com/api/sendhttp.php
        String mainUrl = "https://control.msg91.com/api/sendhttp.php?";

        //Prepare parameter string
        StringBuilder sbPostData = new StringBuilder(mainUrl);
        sbPostData.append("authkey=" + authkey);
        sbPostData.append("&mobiles=" + phoneNo);
        sbPostData.append("&message=" + sendMessage);
        sbPostData.append("&txtBinderCode=" + route);
        sbPostData.append("&sender=" + senderId);

        //final string
        mainUrl = sbPostData.toString();
        try
        {
            //prepare connection
            myURL = new URL(mainUrl);
            myURLConnection = myURL.openConnection();
            myURLConnection.connect();
            reader = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));

            //reading response
            String response;
            while ((response = reader.readLine()) != null)
                //print response
//                Log.i("Response", "Response of msg91" + response);

            //finally close connection
            reader.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();

        }
    }

    public static void setAnimation(View viewToAnimate, int position, int lastPosition, Context mContext)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.push_up);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    /**
     * Return a localized string from the application's package's
     * default string table.
     *
     * @param resId Resource id for the string
     */
    public static final String getString(Context context, @StringRes int resId) {
        return context.getResources().getString(resId);
    }

    public static final void alertTone(Context mContext, int tone)
    {
        int warnTime = 800;
        final MediaPlayer mediaPlayer;

        ((Vibrator)mContext.getSystemService(VIBRATOR_SERVICE)).vibrate(warnTime);
        mediaPlayer = MediaPlayer.create(mContext, tone);
        mediaPlayer.start();

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mediaPlayer.release();
            }
        };
        handler.postDelayed(runnable, warnTime);
    }

    public static final byte[] UNICODE_TEXT = new byte[] {0x23, 0x23, 0x23,
            0x23, 0x23, 0x23,0x23, 0x23, 0x23,0x23, 0x23, 0x23,0x23, 0x23, 0x23,
            0x23, 0x23, 0x23,0x23, 0x23, 0x23,0x23, 0x23, 0x23,0x23, 0x23, 0x23,
            0x23, 0x23, 0x23};

    private static String hexStr = "0123456789ABCDEF";
    private static String[] binaryArray = { "0000", "0001", "0010", "0011",
            "0100", "0101", "0110", "0111", "1000", "1001", "1010", "1011",
            "1100", "1101", "1110", "1111" };

    public static byte[] decodeBitmap(Bitmap bmp){
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();

        List<String> list = new ArrayList<String>(); //binaryString list
        StringBuffer sb;


        int bitLen = bmpWidth / 8;
        int zeroCount = bmpWidth % 8;

        String zeroStr = "";
        if (zeroCount > 0) {
            bitLen = bmpWidth / 8 + 1;
            for (int i = 0; i < (8 - zeroCount); i++) {
                zeroStr = zeroStr + "0";
            }
        }

        for (int i = 0; i < bmpHeight; i++) {
            sb = new StringBuffer();
            for (int j = 0; j < bmpWidth; j++) {
                int color = bmp.getPixel(j, i);

                int r = (color >> 16) & 0xff;
                int g = (color >> 8) & 0xff;
                int b = color & 0xff;

                // if color close to white，bit='0', else bit='1'
                if (r > 160 && g > 160 && b > 160)
                    sb.append("0");
                else
                    sb.append("1");
            }
            if (zeroCount > 0) {
                sb.append(zeroStr);
            }
            list.add(sb.toString());
        }

        List<String> bmpHexList = binaryListToHexStringList(list);
        String commandHexString = "1D763000";
        String widthHexString = Integer
                .toHexString(bmpWidth % 8 == 0 ? bmpWidth / 8
                        : (bmpWidth / 8 + 1));
        if (widthHexString.length() > 2) {
            Log.e("decodeBitmap error", " width is too large");
            return null;
        } else if (widthHexString.length() == 1) {
            widthHexString = "0" + widthHexString;
        }
        widthHexString = widthHexString + "00";

        String heightHexString = Integer.toHexString(bmpHeight);
        if (heightHexString.length() > 2) {
            Log.e("decodeBitmap error", " height is too large");
            return null;
        } else if (heightHexString.length() == 1) {
            heightHexString = "0" + heightHexString;
        }
        heightHexString = heightHexString + "00";

        List<String> commandList = new ArrayList<String>();
        commandList.add(commandHexString+widthHexString+heightHexString);
        commandList.addAll(bmpHexList);

        return hexList2Byte(commandList);
    }

    public static List<String> binaryListToHexStringList(List<String> list) {
        List<String> hexList = new ArrayList<String>();
        for (String binaryStr : list) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < binaryStr.length(); i += 8) {
                String str = binaryStr.substring(i, i + 8);

                String hexString = myBinaryStrToHexString(str);
                sb.append(hexString);
            }
            hexList.add(sb.toString());
        }
        return hexList;

    }

    public static String myBinaryStrToHexString(String binaryStr) {
        String hex = "";
        String f4 = binaryStr.substring(0, 4);
        String b4 = binaryStr.substring(4, 8);
        for (int i = 0; i < binaryArray.length; i++) {
            if (f4.equals(binaryArray[i]))
                hex += hexStr.substring(i, i + 1);
        }
        for (int i = 0; i < binaryArray.length; i++) {
            if (b4.equals(binaryArray[i]))
                hex += hexStr.substring(i, i + 1);
        }

        return hex;
    }

    public static byte[] hexList2Byte(List<String> list) {
        List<byte[]> commandList = new ArrayList<byte[]>();

        for (String hexStr : list) {
            commandList.add(hexStringToBytes(hexStr));
        }
        byte[] bytes = sysCopy(commandList);
        return bytes;
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    public static byte[] sysCopy(List<byte[]> srcArrays) {
        int len = 0;
        for (byte[] srcArray : srcArrays) {
            len += srcArray.length;
        }
        byte[] destArray = new byte[len];
        int destLen = 0;
        for (byte[] srcArray : srcArrays) {
            System.arraycopy(srcArray, 0, destArray, destLen, srcArray.length);
            destLen += srcArray.length;
        }
        return destArray;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
}
