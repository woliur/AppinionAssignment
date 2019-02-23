package com.woliur.appinionassignment;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import fr.ganfra.materialspinner.MaterialSpinner;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final String url = "https://raw.githubusercontent.com/appinion-dev/intern-dcr-data/master/data.json";

    Spinner spinnerProduct;
    Spinner spinnerLiterature;
    Spinner spinnerPhysician;
    Spinner spinnerGift;

    TextView literature;
    TextView physician;
    TextView gift;
    TextView accompanied;
    TextView remarks;

    Button submit;

    ArrayList<String> productGroupList = new ArrayList<>();
    ArrayList<String> literatureLists = new ArrayList<>();
    ArrayList<String> physicianLists = new ArrayList<>();
    ArrayList<String> giftLists = new ArrayList<>();

    OkHttpClient client = new OkHttpClient();

    ArrayAdapter adapterProduct;
    ArrayAdapter adapterLiterature;
    ArrayAdapter adapterPhysician;
    ArrayAdapter adapterGift;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerProduct =(MaterialSpinner)findViewById(R.id.product_spinner);
        adapterProduct = new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,productGroupList);
        spinnerProduct.setAdapter(adapterProduct);

        spinnerLiterature =(MaterialSpinner)findViewById(R.id.literature_spinner);
        adapterLiterature = new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,literatureLists);
        spinnerLiterature.setAdapter(adapterLiterature);

        spinnerPhysician =(MaterialSpinner)findViewById(R.id.physician_spinner);
        adapterPhysician = new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,physicianLists);
        spinnerPhysician.setAdapter(adapterPhysician);

        spinnerGift =(MaterialSpinner)findViewById(R.id.gift_spinner);
        adapterGift = new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,giftLists);
        spinnerGift.setAdapter(adapterGift);

        submit = findViewById(R.id.btn_submit);

        fetchData();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Done",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchData() {
        final Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showResponseDialog("Failed to connect to the server");
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(response.body() != null){
                            String result = null;
                            try {
                                result = response.body().string();
                                JSONObject object = new JSONObject(result);
                                JSONArray product = object.getJSONArray("product_group_list");
                                JSONArray jLiterature = object.getJSONArray("literature_list");
                                JSONArray jPhysician = object.getJSONArray("physician_sample_list");
                                JSONArray jGift = object.getJSONArray("gift_list");

                                for(int i = 0; i<product.length(); i++){
                                    JSONObject productObject = product.getJSONObject(i);
                                    String id = productObject.getString("id");
                                    String productGroup = productObject.getString("product_group");
                                    productGroupList.add(productGroup);
                                }
                                adapterProduct.notifyDataSetChanged();

                                for(int i = 0; i<jLiterature.length(); i++){
                                    JSONObject literatureObject = jLiterature.getJSONObject(i);
                                    String id = literatureObject.getString("id");
                                    String literature = literatureObject.getString("literature");
                                    literatureLists.add(literature);
                                }
                                adapterLiterature.notifyDataSetChanged();

                                for(int i = 0; i<jPhysician.length(); i++){
                                    JSONObject physicianObject = jPhysician.getJSONObject(i);
                                    String id = physicianObject.getString("id");
                                    String sample = physicianObject.getString("sample");
                                    physicianLists.add(sample);
                                }
                                adapterPhysician.notifyDataSetChanged();

                                for(int i = 0; i<jGift.length(); i++){
                                    JSONObject giftObject = jGift.getJSONObject(i);
                                    String id = giftObject.getString("id");
                                    String gift = giftObject.getString("gift");
                                    giftLists.add(gift);
                                }
                                adapterGift.notifyDataSetChanged();

                            }catch (IOException e){
                                showResponseDialog("Exception occurred");
                                e.printStackTrace();
                            }catch (JSONException e) {
                                showResponseDialog("Json Error");
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
    }

    private void showResponseDialog(String result) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle("Response");
        builder.setMessage(result);

        builder.setPositiveButton("OK", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {

        showExitDialog();
    }

    private void showExitDialog() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Do you want to exit?").setTitle("EXIT");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
