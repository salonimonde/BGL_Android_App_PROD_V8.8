package com.sgl.webservice;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.sgl.callers.ServiceCaller;
import com.sgl.configuration.AppConstants;
import com.sgl.models.JsonResponse;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bynry01 on 22-08-2016.
 */
public class WebRequests {

    private static final String TAG = "WebRequests";

    public static JsonObjectRequest loginRequest(int request_type, String url, final String label, final ServiceCaller caller, final String email_id, final String password, String imeiNumber) {



        int LOGIN_TIME_OUT = 5 * 50000;
        final Map<String, String> postParam = new HashMap<String, String>();
        postParam.put("username", email_id);
        postParam.put("imei_no", imeiNumber);
        postParam.put("password", password);
        postParam.put("city", "Gandhi Nagar");
        final JSONObject jsonObject = new JSONObject(postParam);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(request_type, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Gson gson = new Gson();
                JsonResponse jsonResponse = gson.fromJson(response.toString(), JsonResponse.class);
                caller.onAsyncSuccess(jsonResponse, label);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        Gson gson = new Gson();
                        JsonResponse jsonResponse = gson.fromJson(res, JsonResponse.class);
                        caller.onAsyncSuccess(jsonResponse, label);
                    } catch (UnsupportedEncodingException | JsonSyntaxException e1) {
                        // e1.printStackTrace();
                        caller.onAsyncFail(error.getMessage() != null && !error.getMessage().equals("") ? error.getMessage() : "Please Contact Server Admin", label, response);
                    }
                } else
                    caller.onAsyncFail(error.getMessage() != null && !error.getMessage().equals("") ? error.getMessage() : "Please Contact Server Admin", label, response);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=utf-8");
                return params;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(LOGIN_TIME_OUT, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return jsonObjReq;
    }

    public static JsonObjectRequest getJobCards(int request_type, String url, final String label, final ServiceCaller caller, int pageNumber, final String token) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(request_type, url + pageNumber, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Gson gson = new Gson();
                Log.i("Webrequestttttttttttttt", ": " + response.toString());

                JsonResponse jsonResponse = gson.fromJson(response.toString(), JsonResponse.class);
                caller.onAsyncSuccess(jsonResponse, label);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {

                        String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        Gson gson = new Gson();
                        JsonResponse jsonResponse = gson.fromJson(res, JsonResponse.class);
                        caller.onAsyncSuccess(jsonResponse, label);
                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                    } catch (JsonSyntaxException je) {
                        caller.onAsyncFail(error.getMessage() != null && !error.getMessage().equals("") ? error.getMessage() : "Please Contact Server Admin", label, response);
                    }
                } else
                    caller.onAsyncFail(error.getMessage() != null && !error.getMessage().equals("") ? error.getMessage() : "Please Contact Server Admin", label, response);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=utf-8");
                params.put("Accept", "application/json");
                params.put("Authorization", token);
                return params;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(300000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return jsonObjReq;
    }

    public static JsonObjectRequest getdeassignedreassignedjobcards(int request_type, String url, final String label, final ServiceCaller caller, int pageNumber, final String token) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(request_type, url + pageNumber, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Gson gson = new Gson();
                JsonResponse jsonResponse = gson.fromJson(response.toString(), JsonResponse.class);
                caller.onAsyncSuccess(jsonResponse, label);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        Gson gson = new Gson();
                        JsonResponse jsonResponse = gson.fromJson(res, JsonResponse.class);
                        caller.onAsyncSuccess(jsonResponse, label);
                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                    } catch (JsonSyntaxException je) {
                        caller.onAsyncFail(error.getMessage() != null && !error.getMessage().equals("") ? error.getMessage() : "Please Contact Server Admin", label, response);
                    }
                } else
                    caller.onAsyncFail(error.getMessage() != null && !error.getMessage().equals("") ? error.getMessage() : "Please Contact Server Admin", label, response);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=utf-8");
                params.put("Accept", "application/json");
                params.put("Authorization", token);
                return params;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return jsonObjReq;
    }

    public static JsonObjectRequest uploadMeterReading(final JSONObject jsonObject, int request_type,final String url, final String label, final ServiceCaller caller, final String token, final String city) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(request_type, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("Api_Calling", "" + url);
                Log.i("JSONObject", "" + jsonObject);
                Log.i("TOKENN", "" + token);
                Log.i("Webrequestttttttttttttt", ": " + response.toString());
                Gson gson = new Gson();
                JsonResponse jsonResponse = gson.fromJson(response.toString(), JsonResponse.class);
                caller.onAsyncSuccess(jsonResponse, label);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        Gson gson = new Gson();
                        JsonResponse jsonResponse = gson.fromJson(res, JsonResponse.class);
                        caller.onAsyncSuccess(jsonResponse, label);
                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                    } catch (JsonSyntaxException je) {
                        caller.onAsyncFail(error.getMessage() != null && !error.getMessage().equals("") ? error.getMessage() : "Please Contact Server Admin", label, response);
                    }
                } else
                    caller.onAsyncFail(error.getMessage() != null && !error.getMessage().equals("") ? error.getMessage() : "Please Contact Server Admin", label, response);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=utf-8");
                params.put("Accept", "application/json");
                params.put("Authorization", token);
                return params;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return jsonObjReq;
    }

    public static JsonObjectRequest uploadConsumerMeterReading(final JSONObject jsonObject, int request_type, final String url, final String label, final ServiceCaller caller, final String token) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(request_type, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                Log.i("Webrequestttttttttttttt: "+url, ": " + token);
                Gson gson = new Gson();
                JsonResponse jsonResponse = gson.fromJson(response.toString(), JsonResponse.class);
                caller.onAsyncSuccess(jsonResponse, label);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        Gson gson = new Gson();
                        JsonResponse jsonResponse = gson.fromJson(res, JsonResponse.class);
                        caller.onAsyncSuccess(jsonResponse, label);
                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                    } catch (JsonSyntaxException je) {
                        caller.onAsyncFail(error.getMessage() != null && !error.getMessage().equals("") ? error.getMessage() : "Please Contact Server Admin", label, response);
                    }
                } else
                    caller.onAsyncFail(error.getMessage() != null && !error.getMessage().equals("") ? error.getMessage() : "Please Contact Server Admin", label, response);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=utf-8");
                params.put("Accept", "application/json");
                params.put("Authorization", token);
                return params;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return jsonObjReq;
    }

    public static JsonObjectRequest profileImageChange(int request_type, String url, final String label, final ServiceCaller caller, final JSONObject jsonObj, final String token) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(request_type, url, jsonObj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Gson gson = new Gson();
                JsonResponse jsonResponse = gson.fromJson(response.toString(), JsonResponse.class);
                caller.onAsyncSuccess(jsonResponse, label);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        Gson gson = new Gson();
                        JsonResponse jsonResponse = gson.fromJson(res, JsonResponse.class);
                        caller.onAsyncSuccess(jsonResponse, label);
                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                    } catch (JsonSyntaxException je) {
                        caller.onAsyncFail(error.getMessage() != null && !error.getMessage().equals("") ? error.getMessage() : "Please Contact Server Admin", label, response);
                    }
                } else
                    caller.onAsyncFail(error.getMessage() != null && !error.getMessage().equals("") ? error.getMessage() : "Please Contact Server Admin", label, response);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=utf-8");
                params.put("Accept", "application/json");
                params.put("Authorization", token);
                return params;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(300000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return jsonObjReq;

    }

    public static JsonObjectRequest updateDeviceToken(int request_type, String url, final String label, final ServiceCaller caller, final String mr_id, String deviceToken) {
        int update_device_token = 5 * 1000;
        final Map<String, String> postParam = new HashMap<String, String>();
        postParam.put("user_id", mr_id);
        postParam.put("fcm_token", deviceToken);
        final JSONObject jsonObject = new JSONObject(postParam);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(request_type, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Gson gson = new Gson();
                JsonResponse jsonResponse = gson.fromJson(response.toString(), JsonResponse.class);
                caller.onAsyncSuccess(jsonResponse, label);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        Gson gson = new Gson();
                        JsonResponse jsonResponse = gson.fromJson(res, JsonResponse.class);
                        caller.onAsyncSuccess(jsonResponse, label);
                    } catch (UnsupportedEncodingException | JsonSyntaxException e1) {
                        caller.onAsyncFail(error.getMessage() != null && !error.getMessage().equals("") ? error.getMessage() : "Please Contact Server Admin", label, response);
                    }
                } else
                    caller.onAsyncFail(error.getMessage() != null && !error.getMessage().equals("") ? error.getMessage() : "Please Contact Server Admin", label, response);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=utf-8");
                return params;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(update_device_token, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return jsonObjReq;
    }

    public static JsonObjectRequest getBillCards(int request_type, final String url, final String label, final ServiceCaller caller, final String token) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(request_type, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Gson gson = new Gson();
//                Log.i("Webrequestttttttttttttt: "+url, ": " + response.toString());
                JsonResponse jsonResponse = gson.fromJson(response.toString(), JsonResponse.class);
                caller.onAsyncSuccess(jsonResponse, label);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {

                        String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        Gson gson = new Gson();
                        JsonResponse jsonResponse = gson.fromJson(res, JsonResponse.class);
                        caller.onAsyncSuccess(jsonResponse, label);
                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                    } catch (JsonSyntaxException je) {
                        caller.onAsyncFail(error.getMessage() != null && !error.getMessage().equals("") ? error.getMessage() : "Please Contact Server Admin", label, response);
                    }
                } else
                    caller.onAsyncFail(error.getMessage() != null && !error.getMessage().equals("") ? error.getMessage() : "Please Contact Server Admin", label, response);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=utf-8");
                params.put("Accept", "application/json");
                params.put("Authorization", token);
                return params;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return jsonObjReq;
    }

    public static JsonObjectRequest getDeassignedBillCards(int request_type, String url, final String label, final ServiceCaller caller, final String token) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(request_type, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Gson gson = new Gson();
//                Log.i("Webrequestttttttttttttt", ": " + response.toString());
                JsonResponse jsonResponse = gson.fromJson(response.toString(), JsonResponse.class);
                caller.onAsyncSuccess(jsonResponse, label);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {

                        String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        Gson gson = new Gson();
                        JsonResponse jsonResponse = gson.fromJson(res, JsonResponse.class);
                        caller.onAsyncSuccess(jsonResponse, label);
                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                    } catch (JsonSyntaxException je) {
                        caller.onAsyncFail(error.getMessage() != null && !error.getMessage().equals("") ? error.getMessage() : "Please Contact Server Admin", label, response);
                    }
                } else
                    caller.onAsyncFail(error.getMessage() != null && !error.getMessage().equals("") ? error.getMessage() : "Please Contact Server Admin", label, response);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=utf-8");
                params.put("Accept", "application/json");
                params.put("Authorization", token);
                return params;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return jsonObjReq;
    }

    public static JsonObjectRequest getPaymentData(int request_type, String url, final String label, final ServiceCaller caller, final String token) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(request_type, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Gson gson = new Gson();
//                Log.i("Webrequestttttttttttttt", ": " + response.toString());
                JsonResponse jsonResponse = gson.fromJson(response.toString(), JsonResponse.class);
                caller.onAsyncSuccess(jsonResponse, label);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {

                        String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        Gson gson = new Gson();
                        JsonResponse jsonResponse = gson.fromJson(res, JsonResponse.class);
                        caller.onAsyncSuccess(jsonResponse, label);
                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                    } catch (JsonSyntaxException je) {
                        caller.onAsyncFail(error.getMessage() != null && !error.getMessage().equals("") ? error.getMessage() : "Please Contact Server Admin", label, response);
                    }
                } else
                    caller.onAsyncFail(error.getMessage() != null && !error.getMessage().equals("") ? error.getMessage() : "Please Contact Server Admin", label, response);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=utf-8");
                params.put("Accept", "application/json");
                params.put("Authorization", token);
                return params;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return jsonObjReq;
    }


    public static JsonObjectRequest callPostMethod(final JSONObject jsonObject, int request_type, final String url, final String label, final ServiceCaller caller, final String token) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(request_type, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("Api_Calling", " " + url);
                Log.i("JSONObject", " " + jsonObject);
                Log.i("TOKENN", " " + token);
                Log.i("Webrequestttttttttttttt", ": " + response.toString());
                Gson gson = new Gson();
                JsonResponse jsonResponse = gson.fromJson(response.toString(), JsonResponse.class);
                caller.onAsyncSuccess(jsonResponse, label);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        Gson gson = new Gson();
                        JsonResponse jsonResponse = gson.fromJson(res, JsonResponse.class);
                        caller.onAsyncSuccess(jsonResponse, label);
                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                    } catch (JsonSyntaxException je) {
                        caller.onAsyncFail(error.getMessage() != null && !error.getMessage().equals("") ? error.getMessage() : "Please Contact Server Admin", label, response);
                    }
                } else
                    caller.onAsyncFail(error.getMessage() != null && !error.getMessage().equals("") ? error.getMessage() : "Please Contact Server Admin", label, response);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=utf-8");
                params.put("Accept", "application/json");
                params.put("Authorization", token);
                return params;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return jsonObjReq;
    }

}
