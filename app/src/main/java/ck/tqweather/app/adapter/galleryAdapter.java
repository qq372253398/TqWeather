package ck.tqweather.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import ck.tqweather.app.R;

/**
 * Created by ck on 2016/6/1.
 */
public class galleryAdapter extends BaseAdapter {
    public Context mContext;
    private String[] mdate;
    private String[] mloworhigh;
    private String[] mtype;
    private String[] mfeng;

    public galleryAdapter(Context context, String[] date, String[] loworhigh, String[] type, String[] feng) {
        mContext = context;
        mdate = date;
        mloworhigh = loworhigh;
        mtype = type;
        mfeng = feng;
    }

    @Override
    public int getCount() {
        return mdate.length;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = View.inflate(mContext, R.layout.pic_text, null);
            viewHolder.tv_date = (TextView) view.findViewById(R.id.tv_date);
            viewHolder.tv_loworhigh = (TextView) view.findViewById(R.id.tv_loworhigh);
            viewHolder.tv_type = (TextView) view.findViewById(R.id.tv_type);
            viewHolder.tv_feng = (TextView) view.findViewById(R.id.tv_feng);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.tv_date.setText(mdate[i]);
        viewHolder.tv_loworhigh.setText(mloworhigh[i]);
        viewHolder.tv_type.setText(mtype[i]);
        viewHolder.tv_feng.setText(mfeng[i]);
        return view;
    }

    class ViewHolder {
        private TextView tv_date;
        private TextView tv_loworhigh;
        private TextView tv_type;
        private TextView tv_feng;
    }
}
