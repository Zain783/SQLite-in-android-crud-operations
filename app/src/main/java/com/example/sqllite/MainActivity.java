package com.example.sqllite;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextSearch;
    private Button buttonAdd, buttonSearch;
    private ListView listView;

    private ArrayList<String> dataList;
    private ArrayAdapter<String> adapter;

    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextSearch = findViewById(R.id.editTextSearch);
        buttonAdd = findViewById(R.id.buttonAdd);
        buttonSearch = findViewById(R.id.buttonSearch);
        listView = findViewById(R.id.listView);

        // Initialize data list and adapter
        dataList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);

        // Open or create the database
        database = openOrCreateDatabase("MyDatabase", MODE_PRIVATE, null);
        createTable();

        // Add button click listener
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();

                if (name.isEmpty() || email.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter name and email", Toast.LENGTH_SHORT).show();
                } else {
                    insertData(name, email);
                    editTextName.setText("");
                    editTextEmail.setText("");
                    fetchData();
                }
            }
        });

        // Search button click listener
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchKeyword = editTextSearch.getText().toString().trim();
                searchContacts(searchKeyword);
            }
        });

        // List item click listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = adapter.getItem(position);
                Toast.makeText(MainActivity.this, "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();
            }
        });

        fetchData();
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS Contacts (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, email TEXT)";
        database.execSQL(sql);
    }

    private void insertData(String name, String email) {
        String sql = "INSERT INTO Contacts (name, email) VALUES ('" + name + "', '" + email + "')";
        database.execSQL(sql);
    }

    private void fetchData() {
        Cursor cursor = database.rawQuery("SELECT * FROM Contacts", null);

        dataList.clear();
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String email = cursor.getString(cursor.getColumnIndex("email"));
                dataList.add(name + " - " + email);
            } while (cursor.moveToNext());
        }

        adapter.notifyDataSetChanged();
        cursor.close();
    }

    private void searchContacts(String keyword) {
        String sql = "SELECT * FROM Contacts WHERE name LIKE '%" + keyword + "%'";
        Cursor cursor = database.rawQuery(sql, null);

        dataList.clear();
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String email = cursor.getString(cursor.getColumnIndex("email"));
                dataList.add(name + " - " + email);
            } while (cursor.moveToNext());
        }

        adapter.notifyDataSetChanged();
        cursor.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close the database connection
        if (database != null) {
            database.close();
        }
    }
}
