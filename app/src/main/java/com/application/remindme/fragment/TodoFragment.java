package com.application.remindme.fragment;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.remindme.R;
import com.application.remindme.adapter.RemindListAdapter;
import com.application.remindme.dto.DBHelper;
import com.application.remindme.dto.RemindDTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class TodoFragment extends AbstractTabFragment {
    private static final int LAYOUT = R.layout.fragment_history;

    private List<RemindDTO> data;

    private RemindListAdapter adapter;

    public DBHelper dbHelper;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dbHelper=new DBHelper(context);
    }

    public static TodoFragment getInstance(Context context, List<RemindDTO> data) {
        Bundle args = new Bundle();
        TodoFragment fragment = new TodoFragment();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setData(data);
        fragment.setTitle(context.getString(R.string.tab_item_todo));
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            new RemindMeTask().execute();
        }catch (RuntimeException e){}

        view = inflater.inflate(LAYOUT, container, false);

        RecyclerView rv = (RecyclerView) view.findViewById(R.id.recycleView);
        rv.setLayoutManager(new LinearLayoutManager(context));

        adapter = new RemindListAdapter(data,context);
        rv.setAdapter(adapter);

        return view;
    }

    public void refreshList(List<RemindDTO> data) {
        adapter.setData(data);
        adapter.notifyDataSetChanged();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setData(List<RemindDTO> data) {
        this.data = data;
    }

/*to connect with rest server
    private class RemindMeTask extends AsyncTask<Void, Void, RemindDTO[]> {

        @Override
        protected RemindDTO[] doInBackground(Void... params)  {
            RemindDTO[] remindDTOs = new RemindDTO[0];
            try {
                RestTemplate template = new RestTemplate();
                template.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ResponseEntity<RemindDTO[]> responseEntity = template.getForEntity(Constants.URL.GET_REMIND, RemindDTO[].class);
                remindDTOs=responseEntity.getBody();
            }catch (RuntimeException e){return new RemindDTO[0];}
            return  remindDTOs;
        }

        @Override
        protected void onPostExecute(RemindDTO[] remindDTO) {
           refreshList(Arrays.asList(remindDTO));
        }
    }*/

    //connect with SQLite
    private class RemindMeTask extends AsyncTask<Void, Void, List<RemindDTO>> {

        @Override
        protected List<RemindDTO> doInBackground(Void... params)  {
            SQLiteDatabase database = dbHelper.getWritableDatabase();
             List<RemindDTO> remindDTOList  = new ArrayList<>();
            Cursor cursor = database.query(DBHelper.TABLE_CONTACTS,null,null,null,null,null,null);
            if (cursor.moveToFirst()){
                int index = cursor.getColumnIndex(DBHelper.KEY_ID);
                int titleIndex = cursor.getColumnIndex(DBHelper.KEY_TITLE);
                int dataIndex = cursor.getColumnIndex(DBHelper.KEY_DATE);
                do{
                    RemindDTO remindDTO = new RemindDTO(cursor.getString(titleIndex));
                    remindDTO.setId(cursor.getInt(index));
                    remindDTO.setRemindDate(new Date(cursor.getInt(dataIndex)));
                    remindDTOList.add(remindDTO);
                }while (cursor.moveToNext());
                cursor.close();

            }

           return remindDTOList;
        }

        @Override
        protected void onPostExecute(List<RemindDTO> remindDTO) {
            refreshList(remindDTO);
        }
    }

}