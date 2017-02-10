package com.application.remindme.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.application.remindme.Constants;
import com.application.remindme.R;
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
 * Created by vv_voronov on 23.01.2017.
 */

public class RemindListAdapter extends RecyclerView.Adapter<RemindListAdapter.RemindViewHodler> {

    private List<RemindDTO> data;
    public DBHelper dbHelper;


       public RemindListAdapter(List<RemindDTO> data, Context context) {

           this.data = data;
           dbHelper = new DBHelper(context);
    }

    @Override
    public RemindViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.remind_item,parent,false);
        return new RemindViewHodler(view);
    }

    @Override
    public void onBindViewHolder(RemindViewHodler holder, final int position) {
        final RemindDTO item = data.get(position);

        holder.title.setText(item.getTitle());
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RemindTask().execute(item.getId());

            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }



    public static class RemindViewHodler extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView title;
        ImageButton button;

        public RemindViewHodler(View itemView) {
            super(itemView);

            cardView = (CardView)itemView.findViewById(R.id.cardView);
            title = (TextView)itemView.findViewById(R.id.title);
            button = (ImageButton)itemView.findViewById(R.id.delete);

        }
    }
    public void setData(List<RemindDTO> data) {
        this.data = data;

    }
    //connect to SQLite
    private class RemindTask extends AsyncTask<Long, Void, List<RemindDTO>> {

        @Override
        protected List<RemindDTO> doInBackground(Long... params) {

            Long id = params[0];
            SQLiteDatabase database = dbHelper.getReadableDatabase();
            database.delete(DBHelper.TABLE_CONTACTS, DBHelper.KEY_ID + "=" + id,null);

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
            setData(remindDTO);
            RemindListAdapter.this.notifyDataSetChanged();
        }
    }
//connect to rest service
   /* private class RemindTask extends AsyncTask<Long, Void, RemindDTO[]> {
        @Override
        protected RemindDTO[] doInBackground(Long... params) {
            RestTemplate template = new RestTemplate();
            template.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            template.delete(Constants.URL.GET_REMIND+"/"+params[0]);

            RestTemplate templateGet = new RestTemplate();
            templateGet.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            ResponseEntity<RemindDTO[]> responseEntity = templateGet.getForEntity(Constants.URL.GET_REMIND, RemindDTO[].class);

            return responseEntity.getBody();
        }
        @Override
        protected void onPostExecute(RemindDTO[] remindDTO) {
            setData(Arrays.asList(remindDTO));
            RemindListAdapter.this.notifyDataSetChanged();//.
        }
    }*/


}
