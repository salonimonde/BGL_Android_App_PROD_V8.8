package com.sgl.callers;

import com.android.volley.NetworkResponse;
import com.sgl.models.JsonResponse;

/**
 * Created by Bynry01 on 22-08-2016.
 */
public interface ServiceCaller
{
    void onAsyncSuccess(JsonResponse jsonResponse, String label);
    void onAsyncFail(String message, String label, NetworkResponse response);
}
