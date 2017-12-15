package com.example.kalyan.timetable;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;

public class EditorActivity extends AppCompatActivity {
    Spinner spinner;
    int page,selectedPos;
    String selection;
    String sqlArray[];
    EditText subjet,room;
    private String name[];
    Cursor cursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        name = getResources().getStringArray(R.array.Days);
        page = MainActivity.mainActivity.currentPage;
        setTitle(name[page].toUpperCase());

        spinner = (Spinner) findViewById(R.id.spinner);
        subjet = (EditText) findViewById(R.id.subject);
        room = (EditText) findViewById(R.id.room);
        sqlArray = getResources().getStringArray(R.array.TimeSQL);

        setupspinner();

        if(getIntent() != null){
            String subjectSt= getIntent().getStringExtra("subject")+"";
            String roomSt = getIntent().getStringExtra("room")+"";
            if(subjectSt !=null && roomSt != null && !subjectSt.equals("====")&&!roomSt.equals("====")&&
                    !subjectSt.equals("null")&&!roomSt.equals("null") ) {
                subjet.setText(subjectSt);
                room.setText(roomSt);
            }
            //Toast.makeText(getApplicationContext(),getIntent().getIntExtra("position",0)+"",Toast.LENGTH_SHORT).show();
            spinner.setSelection(getIntent().getIntExtra("position",0),true);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                boolean done = save();
                if(done) {
                    item.setIcon(R.drawable.ic_action_name);
                    Intent intent = new Intent(this,MainActivity.class);
                    intent.putExtra("page",page);
                    startActivity(intent);
                }
                else
                    item.setIcon(R.drawable.ic_notdone);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean save(){
        String selectQuery = "SELECT  * FROM " + Contract.Entry.TABLE_NAME + " WHERE "
                + Contract.Entry.COLUMN_DAY + " = " + "\""+name[page].toLowerCase()+"\"";
        Helper helper = (new Helper(MainActivity.getContext()));
        Cursor cursor = helper.getReadableDatabase().rawQuery(selectQuery, null);
        ContentValues values = new ContentValues();
        boolean done = false;
        if(cursor != null  && cursor.moveToFirst()){
            int tempColumnIndex = cursor.getColumnIndex(sqlArray[selectedPos]);
            String tempString = cursor.getString(tempColumnIndex);

            String arg[] = {name[page]};
            String subjectSt = subjet.getText().toString().trim();
            String roomSt = room.getText().toString().trim();
            if(subjectSt !=null && roomSt != null && !subjectSt.equals("")&&!roomSt.equals("")&&
                    !subjectSt.contains("null")&&!roomSt.contains("null") ) {
                values.put(sqlArray[selectedPos], subjectSt+ "-" + roomSt);
                getContentResolver().update(Contract.Entry.CONTENT_URI, values, "day = ?", arg);

                done = true;
               // Toast.makeText(getApplicationContext(), "1st", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
            else{
                SuperActivityToast.create(this, new Style(), Style.TYPE_STANDARD)
                        .setText("Enter Valid Data")
                        .setDuration(Style.DURATION_LONG)
                        .setFrame(Style.ANIMATIONS_POP)
                        .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_INDIGO))
                        .setAnimations(Style.ANIMATIONS_POP).show();
            }
        }
        else {
            values.put(Contract.Entry.COLUMN_DAY, name[page]);
            String subjectSt = subjet.getText().toString().trim();
            String roomSt = room.getText().toString().trim();
            if(subjectSt !=null && roomSt != null && !subjectSt.equals("")&&!roomSt.equals("")&&
                    !subjectSt.contains("null")&&!roomSt.contains("null") ) {
                values.put(sqlArray[selectedPos], subjet.getText().toString() + "-" + room.getText().toString());
                getContentResolver().insert(Contract.Entry.CONTENT_URI, values);
                done = true;
               // Toast.makeText(getApplicationContext(), "insert", Toast.LENGTH_SHORT).show();
            }else{
                SuperActivityToast.create(this, new Style(), Style.TYPE_STANDARD)
                        .setText("Enter Valid Data")
                        .setDuration(Style.DURATION_LONG)
                        .setFrame(Style.ANIMATIONS_POP)
                        .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_INDIGO))
                        .setAnimations(Style.ANIMATIONS_POP).show();
            }

        }
        return done;
    }

    private void setupspinner() {
        ArrayAdapter SpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.Time, android.R.layout.simple_spinner_item);

        SpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        spinner.setAdapter(SpinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selection = (String) parent.getItemAtPosition(position);
                selectedPos = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selection = "8AM TO 9AM";
            }
        });

    }
}
