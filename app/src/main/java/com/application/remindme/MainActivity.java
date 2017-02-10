package com.application.remindme;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.application.remindme.adapter.TabsFragmentAdapter;
import com.application.remindme.dto.DBHelper;
import com.application.remindme.dto.RemindDTO;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * Created by vv_voronov on 13.01.2017.
 */

public class MainActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_main;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ViewPager viewPager;

    private TabsFragmentAdapter adapter;
    DBHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        dbHelper = new DBHelper(this);
        initToolbar();
        initNavigationView();
        initTabs();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return false;
            }
        });

      //  toolbar.inflateMenu(R.menu.menu);
    }

    protected void initTabs() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        adapter = new TabsFragmentAdapter(getApplicationContext(), getSupportFragmentManager(), new ArrayList<RemindDTO>());
        viewPager.setAdapter(adapter);

        //      new RemindMeTask().execute();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);


    }

    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.view_navigation_open, R.string.view_navigation_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                drawerLayout.closeDrawers();
                switch (menuItem.getItemId()) {
                    case R.id.actionNotfication:
                        showNotificationTabTwo();
                        break;
                    case R.id.actionNotfication2:{
                        showNotificationTabOne();
                        break;
                    }
                }
                return true;
            }
        });
    }

    private void showNotificationTabTwo() {
        viewPager.setCurrentItem(Constants.TAB_TWO);
    }
    private void showNotificationTabOne() {
        viewPager.setCurrentItem(Constants.TAB_ONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            RemindDTO remindDTO = new RemindDTO(data.getStringExtra("text"));
            remindDTO.setRemindDate(new Date());
            remindDTO.setId(1);
            new RemindTask().execute(remindDTO);
        }
    }

    public void onAddButton(View view) {
        Intent intent = new Intent(this,AddItem.class);
        startActivityForResult(intent,1);
      }
    //connect to SQLite
    private class RemindTask extends AsyncTask<RemindDTO, Void, List<RemindDTO>> {

        @Override
        protected List<RemindDTO> doInBackground(RemindDTO... params) {

            RemindDTO remindDTO = params[0];
            SQLiteDatabase database = dbHelper.getReadableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBHelper.KEY_TITLE, remindDTO.getTitle());
            contentValues.put(DBHelper.KEY_DATE, remindDTO.getRemindDate().getTime());
            database.insert(DBHelper.TABLE_CONTACTS, null, contentValues);

            //refresh
            List<RemindDTO> remindDTOList = new ArrayList<>();
            Cursor cursor = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(DBHelper.KEY_ID);
                int titleIndex = cursor.getColumnIndex(DBHelper.KEY_TITLE);
                int dataIndex = cursor.getColumnIndex(DBHelper.KEY_DATE);
                do {
                    RemindDTO remindDTOupd = new RemindDTO(cursor.getString(titleIndex));
                    remindDTOupd.setId(cursor.getInt(index));
                    remindDTOupd.setRemindDate(new Date(cursor.getInt(dataIndex)));
                    remindDTOList.add(remindDTOupd);
                } while (cursor.moveToNext());
                cursor.close();

            }

            return remindDTOList;
        }

        @Override
        protected void onPostExecute(List<RemindDTO> remindDTO) {
            adapter.setData(remindDTO);
        }
    }

//connect with rest service
    /*
    private class RemindTask extends AsyncTask<RemindDTO, Void, RemindDTO[]> {
        @Override
        protected RemindDTO[] doInBackground(RemindDTO... params) {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity entity = new HttpEntity(params[0],httpHeaders);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> out = restTemplate.exchange(Constants.URL.GET_REMIND, HttpMethod.POST,entity, String.class);

            RestTemplate template = new RestTemplate();
            template.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            ResponseEntity<RemindDTO[]> responseEntity = template.getForEntity(Constants.URL.GET_REMIND, RemindDTO[].class);

            return responseEntity.getBody();
        }
        @Override
        protected void onPostExecute(RemindDTO[] remindDTO) {
            adapter.setData(Arrays.asList(remindDTO));
        }
    }*/
}



