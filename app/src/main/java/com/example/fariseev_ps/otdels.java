package com.example.fariseev_ps;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class otdels extends Activity {


    //Переменная для работы с БД
    ActionBar actionBar;
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    String list;
    MySimpleAdapter adapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
       // EditText editSearch = (EditText) findViewById(R.id.editSearch);
        //editSearch.setVisibility(View.VISIBLE);
        //editSearch.setOnClickListener(itemClickListenerText);
       // getActionBar().hide();
        String otdels = getIntent().getExtras().getString("otdel");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        list=prefs.getString(getString(R.string.list), "1");

//база
        mDBHelper = new DatabaseHelper(this);
        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }
        try {
            mDb = mDBHelper.getWritableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }
//Список клиентов
        ArrayList<HashMap<String, Object>> clients = new ArrayList<HashMap<String, Object>>();

//Список параметров конкретного клиента
        HashMap<String, Object> client;
//--------------------------------------------------------------
//Отправляем запрос в БД
        Cursor cursor = mDb.rawQuery("SELECT * FROM Лист"+list, null);
        actionBar = getActionBar();
        cursor.moveToPosition(2);
        while (!cursor.isAfterLast()) {
            client = new HashMap<String, Object>();
            if (cursor.getString(7).equals(otdels)) {
                client.put("clients", cursor.getString(0));
                if (cursor.getString(10)==null) client.put("otd", cursor.getString(7)+".");
                else client.put("otd", cursor.getString(7)+"."+"\n"+cursor.getString(10)+".");
                client.put("dole", cursor.getString(8));
                client.put("temp", cursor.getString(7));
               // actionBar.setTitle(cursor.getString(6));
               // client.put("gde", cursor.getString(6));

               // client.put("dole", cursor.getString(8));
                clients.add(client);
            }
            cursor.moveToNext();
        }
        cursor.moveToPrevious();
        actionBar.setTitle(cursor.getString(6));
        cursor.close();

//Создаем адаптер
        adapter = new MySimpleAdapter(this, clients, R.layout.adapter_item2, new String[]{"clients","otd", "dole"}, new int[]{R.id.textViewmain,R.id.textView2,R.id.textView3});
        //listView = (ListView) findViewById(R.id.listView);
        listView = findViewById(R.id.listView);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(itemClickListener);
   }

    //@Override
    //protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    //    super.onActivityResult(requestCode, resultCode, data);
//
 //       Log.d("--",String.valueOf(requestCode));
  //  }

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
            HashMap<String, Object> itemHashMap =
                    (HashMap<String, Object>) parent.getItemAtPosition(position);
            String itemclicked = itemHashMap.get("clients").toString();
            String name=itemclicked;
            itemclicked=itemHashMap.get("temp").toString();
            Intent sec_intent = new Intent(otdels.this, users.class);
            //     TextView textView = (TextView) titleItem;
            //   String strText = textView.getText().toString();
            sec_intent.putExtra("usermake", name);
            sec_intent.putExtra("userotd", itemclicked);
            startActivityForResult(sec_intent,1);

            //    String descriptionItem = itemHashMap.get("dole").toString();
            //              Toast.makeText(getApplicationContext(),"Вы выбрали " + titleItem, Toast.LENGTH_SHORT).show();
        }
    };
/*
    SearchView.OnClickListener itemClickListenerText = new SearchView.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
              //  EditText editSearch = (EditText) v.findViewById(R.id.editSearch);
                if (!editSearch.getText().toString().equals("")) {
                Intent sec_intent = new Intent(getApplicationContext(), search.class);
                sec_intent.putExtra("searc", editSearch.getText().toString());
                editSearch.setText(null);
                startActivity(sec_intent); }
        }
        ;
    };
*/
}
