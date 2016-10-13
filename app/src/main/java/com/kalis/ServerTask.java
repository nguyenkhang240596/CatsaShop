package com.kalis;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.kalis.R;
import com.kalis.log.LogSystem;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.Random;

/**
 * Created by PC14-02 on 11/28/2015.
 */
public final class ServerTask {
    //Biến lưu namespace do bạn cấu hình trong Webservice
    public static final String NAMESPACE="http://tempuri.org/";
    //Biến lưu WSDL của server URL
    public static final String SERVER_URL ="http://192.168.56.2/poolserver/wsgcm.asmx?WSDL";
    //biến lưu Sender ID
    public static final String SENDER_ID = "637962849739";
    //biến lưu log lại khi có sự kiện sảy ra
    public static final String TAG = "DrthanhGCM_LOG";
    /**
     * Intent sử dụng để hiển thị lên màn hình
     */
    public static final String DISPLAY_MESSAGE_ACTION = "com.kalis.hocgooglecloud.DISPLAY_MESSAGE";

    /**
     * Thông tin (message) để intent hiển thị
     */
    public static final String EXTRA_MESSAGE = "message";
    /**
     * Hàm dùng để hiển thị Message lên màn hình (chú ý là nó chạy background)
     *
     * @param context
     *            application's context.
     * @param message
     *            thông báo hiển thị ở màn hình.
     */
    public static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }

    public  static final int MAX_ATTEMPTS = 5;
    public  static final int BACKOFF_MILLI_SECONDS = 2000;
    public  static final Random random = new Random();

    public static boolean post(final Context context, final String regId)
    {
        try{
            final String METHOD_NAME="luuGCMID"; // doi
            final String SOAP_ACTION=NAMESPACE+METHOD_NAME;
            SoapObject request=new SoapObject(NAMESPACE, METHOD_NAME);

            request.addProperty("gcmId", regId); // doi
            SoapSerializationEnvelope envelope=
                    new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet=true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport=
                    new HttpTransportSE(SERVER_URL);
            androidHttpTransport.call(SOAP_ACTION, envelope);
            SoapPrimitive response=(SoapPrimitive) envelope.getResponse();

            boolean res = Boolean.parseBoolean(response.toString());
            if (res)
            {
                LogSystem.E("Đã lưu GCM ID device về server thành công");
            }
            else
            {
                LogSystem.E("Đã lưu GCM ID device về server thất bại");
            }

            return true;
        }
        catch(Exception e)
        {
            Toast.makeText(context, "kết quả =\n"+e.toString(), Toast.LENGTH_LONG).show();
            Log.i(TAG, "Lỗi post:\n"+e.toString());
        }
        return false;
    }

    public static boolean register(final Context context, final String regId) {
        Log.i(TAG, "registering device (regId = " + regId + ")");

        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
        // Once GCM returns a registration id, we need to register it in the
        // demo server. As the server might be down, we will retry it a couple
        // times.
        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
            Log.d(TAG, "Attempt #" + i + " to register");
            try {
                displayMessage(context, context.getString(
                        R.string.server_registering, i, MAX_ATTEMPTS));
                boolean b=post(context,regId);

                GCMRegistrar.setRegisteredOnServer(context, true);

                String message = context.getString(R.string.server_registered);

                displayMessage(context, message);
                if(b)
                    return true;
            } catch (Exception e) {
                // Here we are simplifying and retrying on any error; in a real
                // application, it should retry only on unrecoverable errors
                // (like HTTP error code 503).
                Log.e(TAG, "Failed to register on attempt " + i, e);
                if (i == MAX_ATTEMPTS) {
                    break;
                }
                try {
                    Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                    // Activity finished before we complete - exit.
                    Log.d(TAG, "Thread interrupted: abort remaining retries!");
                    Thread.currentThread().interrupt();
                    return false;
                }
                // increase backoff exponentially
                backoff *= 2;
            }
        }
        String message = context.getString(R.string.server_register_error,
                MAX_ATTEMPTS);
        displayMessage(context, message);
        return false;
    }
}