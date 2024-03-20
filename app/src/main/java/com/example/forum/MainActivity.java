package com.example.forum;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private GridView gridView;
    private List<String> categories;
    private CategoryAdapter adapter;
    RequestQueue requestQueue;

    Button logOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String studentId = sharedPreferences.getString("userId", "defaultStudentId");
        String username = sharedPreferences.getString("username", "defaultUserName");
        TextView welcomeText = findViewById(R.id.userInfoTextView);
        welcomeText.setText("Welcome " + username);

        gridView = findViewById(R.id.categoryGridView);
        categories = new ArrayList<>(); // Initialize your categories list here
        adapter = new CategoryAdapter(this, categories);
        gridView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this);

        logOut = findViewById(R.id.signOutButton);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_login = new Intent(MainActivity.this, WelcomeActivity.class);
                startActivity(intent_login);
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ThreadActivity.class);
                String categoryName = categories.get(position);
                intent.putExtra("category_name", categoryName);
                startActivity(intent);
            }
        });

        fetchData();
    }

    private void fetchData() {
        String url = "https://forum-node.vercel.app/category";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    categories.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);
                            String categoryName = jsonObject.getString("category_name");
                            categories.add(categoryName);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Error parsing JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                    adapter.notifyDataSetChanged();
                },
                error -> Toast.makeText(MainActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show());

        requestQueue.add(jsonArrayRequest);
    }
}
