package com.sgl.activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Spinner;

/**
 * Created by Bynry on 11/22/15.
 */
public class ParentActivity extends AppCompatActivity
{
    private ProgressDialog mProgressDialog;

    // dismiss loading dialog
    protected void dismissLoadingDialog()
    {

        if (mProgressDialog != null && mProgressDialog.isShowing())
        {
            try
            {
                mProgressDialog.dismiss();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

    }

    // show loading dialog
    protected void showLoadingDialog()
    {

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

    }

    // show loading dialog
    protected void showLoadingDialog(String msg)
    {

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(msg);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

    }
    //private method of your class
    protected int getIndex(Spinner spinner, String myString)
    {
        int index = 0;

        for (int i=0;i<spinner.getCount();i++)
        {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString))
            {
                index = i;
                break;
            }
        }
        return index;
    }
}
