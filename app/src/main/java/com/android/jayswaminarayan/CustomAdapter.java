package com.android.jayswaminarayan;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CustomAdapter extends BaseAdapter {
    private Context mContext;
    private String[] titles;
    private AssetManager assets;

    public CustomAdapter(Context context, AssetManager ass, String[] t) {
        mContext = context;
        titles = t;
        assets = ass;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View row;
        row = inflater.inflate(R.layout.row, parent, false);
        TextView title;
        ImageView img;
        title = row.findViewById(R.id.pdfName);
        img = row.findViewById(R.id.pdfIcon);
        if(!isFullEnglish(titles[position])) {
            Typeface face = Typeface.createFromAsset(assets, "GujaratiRegular.ttf");
            title.setTypeface(face);
        }
        title.setText(titles[position]);
        img.setBackgroundResource(R.drawable.pdf__icon_1);
        return (row);
    }

    private boolean isFullEnglish(String t) {
        for(char c: t.toCharArray()) {
            int asc = (int)c;
            if(!(asc>=33 && asc<=126))
                return false;
        }
        return true;
    }
}