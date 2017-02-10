package com.application.remindme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

/**
 * Created by vv_voronov on 08.02.2017.
 */

public class AddItem extends Activity {

    private EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_item);
        editText = (EditText) findViewById(R.id.editText);

    }

    public void onOk(View view){
        Intent intent = new Intent();
        intent.putExtra("text",editText.getText().toString());
        setResult(RESULT_OK,intent);
        finish();
    }

    public void onCancel(View view){
        setResult(RESULT_CANCELED);
        finish();
    }
}
