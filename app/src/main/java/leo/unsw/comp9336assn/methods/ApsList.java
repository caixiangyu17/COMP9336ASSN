package leo.unsw.comp9336assn.methods;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import leo.unsw.comp9336assn.R;


/**
 * 生成一个adapter的类
 * 这里实例化的时候需要context参数
 * list的样式有xml的item来决定，这里需要修改内部的ViewHolder与之对应
 * 对于一些复杂的变化，在getView之中修改
 * 调用方法为
 * SampleList listapi=new SampleList(context);
 * normalModeList.setAdapter(listapi.normalModeAdapter);
 * 透明列表注意：android:cacheColorHint="#00000000" 在list中加此句，但是会大大影响效率，
 * 会使滚动变卡，所以如果长列表背景最好不要用图片，用单一颜色来使用，将这个也设置成该颜色
 * 经测试发现变卡的原因可能是list过长，如果小一点就会变得顺畅
 *
 * @author cxy
 *         Feb. 2 2012
 */
public class ApsList {


    static Context context = null;
    public NormalModeAdapter normalModeAdapter = null;

    public ApsList(Context context, HashMap<String, ShowAp> map) {
        this.context = context;
        this.normalModeAdapter = new NormalModeAdapter(map);
    }


    static class ViewHolder {
        TextView tv_ssid;
        TextView tv_count;
        TextView tv_power;
    }

    static class NormalModeAdapter extends BaseAdapter {
        LayoutInflater inflater = null;
        private HashMap<String, ShowAp> map;
        private ArrayList<ShowAp> list = new ArrayList<>();


        public NormalModeAdapter(HashMap<String, ShowAp> map) {
            this.map = map;
            Iterator itr = map.entrySet().iterator();
            while (itr.hasNext()) {
                HashMap.Entry entry = (HashMap.Entry) itr.next();
                ShowAp value = (ShowAp) entry.getValue();
                list.add(value);
            }
            Comparator<ShowAp> comparator = new Comparator<ShowAp>(){

                @Override
                public int compare(ShowAp showAp, ShowAp t1) {
                    return (int) (t1.power - showAp.power);
                }
            };
            Collections.sort(list, comparator);
        }



        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            if (map == null) {
                return 0;
            }
            return map.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        /**
         * 此方法根据各种不同list样式进行修改
         */
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder viewHolder;
            if (convertView == null) {
                if (inflater == null) {
                    inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
                }
                convertView = inflater.inflate(R.layout.item_wifi, null);
                viewHolder = new ViewHolder();
                viewHolder.tv_ssid = (TextView) convertView.findViewById(R.id.tv_ssid);
                viewHolder.tv_count = (TextView) convertView.findViewById(R.id.tv_count);
                viewHolder.tv_power = (TextView) convertView.findViewById(R.id.tv_power);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.tv_ssid.setText(list.get(position).ssid);
            viewHolder.tv_count.setText(list.get(position).count + "");
            DecimalFormat df = new DecimalFormat("0.000");
            viewHolder.tv_power.setText(df.format(list.get(position).power) + "nW");
            viewHolder.tv_ssid.setTextColor(list.get(position).color);
            viewHolder.tv_count.setTextColor(list.get(position).color);
            viewHolder.tv_power.setTextColor(list.get(position).color);


            return convertView;
        }

    }


}
