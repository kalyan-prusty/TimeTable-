package com.example.kalyan.timetable;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by KALYAN on 25-08-2017.
 */

public class Tuesday extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    Cursor mCursor;
    final int LOADER_CODE = 2;
    View view = null;
    ListView list1 = null;
    String sqlArray[];
    String tSQL[];
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tuesday_layout,container,false);
        list1 = (ListView) view.findViewById(R.id.list_view2);
        registerForContextMenu(list1);
        sqlArray = getResources().getStringArray(R.array.TimeSQL);
        getActivity().getSupportLoaderManager().initLoader(LOADER_CODE,null,this);

        setView(mCursor);
        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Select the Action");
        menu.add(0,getId(),0,"Edit");
        menu.add(0,getId(),0,"Delete");
    }
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int listPosition = info.position;
        String arg[] = {"tuesday"};
        if(item.getTitle() == "Delete"){
            ContentValues values = new ContentValues();
            values.put(sqlArray[listPosition], "null");
            getActivity().getContentResolver().update(Contract.Entry.CONTENT_URI, values, "day = ?", arg);
            // adapter.notifyDataSetChanged();
            getActivity().finish();
            Intent intent  = new Intent(getActivity(),MainActivity.class);
            startActivity(intent);
        }else if(item.getTitle() == "Edit"){
            Intent intent = new Intent(MainActivity.getContext(),EditorActivity.class);
            intent.putExtra("subject",((TextView)view.findViewById(R.id.subject)).getText().toString());
            intent.putExtra("room",((TextView)view.findViewById(R.id.room)).getText().toString());
            intent.putExtra("position",listPosition);
            startActivity(intent);
        }
        return super.onContextItemSelected(item);
    }

    public void setView(Cursor cursor){
        tSQL = MainActivity.getContext().getResources().getStringArray(R.array.TimeSQL);

        if( cursor !=  null  && cursor.moveToFirst() ) {
            MyAdapter adapter = new MyAdapter(MainActivity.getContext(),cursor,tSQL);
            list1.setAdapter(adapter);
        }

    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String selectQuery = "SELECT  * FROM " + Contract.Entry.TABLE_NAME + " WHERE "
                + Contract.Entry.COLUMN_DAY + " = " + "\"tuesday\"";

        Helper helper = (new Helper(MainActivity.getContext()));
        try {
            mCursor = helper.getReadableDatabase().rawQuery(selectQuery, null);
        }catch (SQLiteException e){

        }

        return new CursorLoader(getContext(),Contract.Entry.CONTENT_URI,
                null,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

}
