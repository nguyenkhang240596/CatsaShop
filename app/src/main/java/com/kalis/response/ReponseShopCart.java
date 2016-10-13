package com.kalis.response;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by Kalis on 3/16/2016.
 */
public class ReponseShopCart extends AsyncTask<String,String,Integer> {

        private String hoten,diachi,phone,province,quanhuyen,result,detail;

        public ReponseShopCart(String hoten, String diachi, String phone, String province, String quanhuyen,String detail) {
            this.hoten = hoten;
            this.diachi = diachi;
            this.phone = phone;
            this.province = province;
            this.quanhuyen = quanhuyen;
            this.detail = detail;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected Integer doInBackground(String ... params) {
            try{

                String data = URLEncoder.encode("name", "UTF-8")
                        + "=" + URLEncoder.encode(hoten, "UTF-8");

                data += "&" + URLEncoder.encode("address", "UTF-8") + "="
                        + URLEncoder.encode(diachi, "UTF-8");

                data += "&" + URLEncoder.encode("phone", "UTF-8") + "="
                        + URLEncoder.encode(phone, "UTF-8");

                data += "&" + URLEncoder.encode("province", "UTF-8") + "="
                        + URLEncoder.encode(province, "UTF-8");

                data += "&" + URLEncoder.encode("quanhuyen", "UTF-8") + "="
                        + URLEncoder.encode(quanhuyen, "UTF-8");

                data += "&" + URLEncoder.encode("details", "UTF-8") + "="
                        + URLEncoder.encode(detail, "UTF-8");

                BufferedReader reader=null;

                // Send data
                // Defined URL  where to send data
                URL url = new URL(params[0]);

                // Send POST data request

                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write( data );
                wr.flush();

                // Get the server response

                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder("");
                String line = null;

                // Read Server Response
                while((line = reader.readLine()) != null)
                {
                    // Append server response in string
                    sb.append(line + "\n");
                }
                result = sb.toString().replace('\t',' ').replace('\n',' ').trim();
                if (Integer.parseInt(result) == 1) return 1;

            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
            return 0;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }


}
