package team20.flashbackmusic;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by lenovo on 2018/2/13.
 */

public class MyAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list = new ArrayList<String>();
    private Context context;
    int buttonimage = 1;


    public MyAdapter(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    //@Override
    public long getItemId(int pos) {
        //return list.get(pos).getiD();
        //just return 0 if your list items do not have an Id variable.
        return 0;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview, null);
        }

        //Handle TextView and display string from your list
        final TextView listItemText = (TextView)view.findViewById(R.id.list_item_string);
        final Button addBtn = (Button)view.findViewById(R.id.add_btn);
        listItemText.setText(list.get(position));
        addBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //do something
                if(buttonimage==1) {
                    MainActivity.playList_flashback.changeToFavorite(list.get(position));
                    addBtn.setBackgroundResource(R.drawable.check);
                    buttonimage =2;
                }
                else if(buttonimage==2){
                    MainActivity.playList_flashback.changeToDislike(list.get(position));
                    addBtn.setBackgroundResource(R.drawable.cross);
                    buttonimage = 3;
                }
                else{
    //                Location location;
      //              Calendar time;
        //            MainActivity.playList_flashback.changeToNeutral(list.get(position),location, time);
                    addBtn.setBackgroundResource(R.drawable.add);
                    buttonimage = 1;
                }
            }
        });
        //Handle buttons and add onClickListeners
        //
        //addBtn.setOnClickListener(new View.OnClickListener(){
          //  @Override
            //public void onClick(View v) {
                //do something

            //}
        //});

        return view;
    }
}