package se.nackademin.bitcoinapi;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final String BPI_ENDPOINT = "https://api.coindesk.com/v1/bpi/currentprice.json";
    // We declare Bitcoin Price Index / BPI as a constant
    private OkHttpClient okHttpClient = new OkHttpClient();
    // We imported OkHttpClient and defined a property

    private ProgressDialog progressDialog;
    private TextView price;
    //imported both and defined a property


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        price = (TextView) findViewById(R.id.price);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("BPI Loading");
        progressDialog.setMessage("Wait ...");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // oncreateoptionsmenu. We imported view.menu
        getMenuInflater().inflate(R.menu.main, menu);
        //Android OS inflate XML code
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_load) {
            load();
        }

        return super.onOptionsItemSelected(item);
    }

    private void load() {
        Request request = new Request.Builder()
                // import okhttp request
                .url(BPI_ENDPOINT)
                // bpi_endpoint is declared constant adress to json
                .build();

        progressDialog.show();

        okHttpClient.newCall(request).enqueue(new Callback() {
            //import okhttp callback
            @Override
            public void onFailure(Call call, IOException e) {
                // import okhttp call
                // import ioexception?
                Toast.makeText(MainActivity.this, "Error during BPI loading : "
                        // import okhttp toast
                        + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response)
                    throws IOException {
                final String body = response.body().string();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        parseBpiResponse(body);
                    }
                });
            }
        });

    }

    private void parseBpiResponse(String body) {
        try {
            StringBuilder builder = new StringBuilder();

            JSONObject jsonObject = new JSONObject(body);
            // import jsonobject?
            JSONObject timeObject = jsonObject.getJSONObject("time");
            builder.append(timeObject.getString("updated")).append("\n\n");

            JSONObject bpiObject = jsonObject.getJSONObject("bpi");
            JSONObject usdObject = bpiObject.getJSONObject("USD");
            builder.append(usdObject.getString("rate")).append("$").append("\n");

            JSONObject gbpObject = bpiObject.getJSONObject("GBP");
            builder.append(gbpObject.getString("rate")).append("£").append("\n");

            JSONObject euroObject = bpiObject.getJSONObject("EUR");
            builder.append(euroObject.getString("rate")).append("€").append("\n");

            price.setText(builder.toString());

        } catch (Exception e) {

        }
    }

}
