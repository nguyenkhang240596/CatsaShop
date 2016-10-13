package com.kalis.fragment;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.kalis.R;
import com.kalis.dialog.MyToast;
import com.kalis.keys.KeySource;
import com.kalis.model.Product;
import com.kalis.response.ReponseShopCart;

import java.util.ArrayList;

public class OrderMenuFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Spinner spinnerTT, spinnerQH;
    private EditText txtAddress, txtPhoneNumber, txtName;
    private Button btnSend;
    private ArrayAdapter<String> adapterTT, adapterQH;
    private ArrayList<String> arrayListTT, arrayListQH;
    private SQLiteDatabase sqLiteDatabase;
    private String details = "";

    private OnFragmentInteractionListener mListener;

    public static OrderMenuFragment newInstance(String param1, String param2) {
        OrderMenuFragment fragment = new OrderMenuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public OrderMenuFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            Bundle b = getArguments();
            int size = b.getInt(KeySource.DATA_SIZE);
            for ( int i=0;i < size ; i++)
            {
                details += b.getString(i + "") +"\n";

            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_order_menu, container, false);

        addControls(rootView);
        addEvents();
        connectDatabase();
        loadProvinces();
        return rootView;
    }

    private void addEvents() {

        spinnerTT.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadQuanHuyen(arrayListTT.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReponseShopCart reponse = new ReponseShopCart(
                        txtName.getText().toString(),
                        txtAddress.getText().toString(),
                        txtPhoneNumber.getText().toString(),
                        spinnerTT.getSelectedItem().toString(),
                        spinnerQH.getSelectedItem().toString(),
                        details
                );
                try{
                    if (reponse.execute(KeySource.REPONSE).get() == 1)
                    {
                        MyToast.toastLong(getActivity(),getString(R.string.send_response_success));
                        getActivity().onBackPressed();
                    }
                    else
                    {
                        MyToast.toastLong(getActivity(),getString(R.string.send_response_error));
                    }
                }
                catch (Exception e)
                {
                    Log.e("RESPONSE : ", e.toString());
                }

            }
        });

    }

    private void addControls(View rootView) {

        txtName = (EditText) rootView.findViewById(R.id.txtName);
        txtAddress = (EditText) rootView.findViewById(R.id.txtAddress);
        txtPhoneNumber = (EditText) rootView.findViewById(R.id.txtPhone);
        btnSend = (Button) rootView.findViewById(R.id.btnSendInfomationToServer);
        spinnerTT = (Spinner) rootView.findViewById(R.id.spinnerTinhTp);
        spinnerQH = (Spinner) rootView.findViewById(R.id.spinnerQuanHuyen);
        arrayListTT = new ArrayList<>();
        arrayListQH = new ArrayList<>();
        adapterTT = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, arrayListTT);
        adapterQH = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, arrayListQH);
        adapterTT.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterQH.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTT.setAdapter(adapterTT);
        spinnerQH.setAdapter(adapterQH);

    }

    private void connectDatabase() {
        sqLiteDatabase = getActivity().openOrCreateDatabase(KeySource.DATABASE_NAME, Context.MODE_PRIVATE, null);
    }

    private void loadQuanHuyen(String key) {
        try {
            arrayListQH.clear();
            Cursor c = sqLiteDatabase.query(KeySource.TABLES[2], null, "name=?", new String[]{key}
                    , null, null, null);

            c.moveToFirst();
            String id = c.getString(0);
            c.close();

            Cursor c2 = sqLiteDatabase.query(KeySource.TABLES[2], null, "pid=?", new String[]{id}
                    , null, null, null);

            while (c2.moveToNext()) {

                String quanHuyen = c2.getString(2);
                arrayListQH.add(quanHuyen);
            }
            c2.close();
            adapterQH.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadProvinces() {
        Cursor c = sqLiteDatabase.query(KeySource.TABLES[2], null, null, null
                , null, null, null, "64");

        while (c.moveToNext()) {

            String province = c.getString(2);
            arrayListTT.add(province);
        }
        c.close();
        adapterTT.notifyDataSetChanged();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
