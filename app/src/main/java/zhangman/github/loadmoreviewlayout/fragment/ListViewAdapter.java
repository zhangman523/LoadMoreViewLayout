package zhangman.github.loadmoreviewlayout.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import zhangman.github.loadmoreviewlayout.R;

/**
 * Created by zhangman on 2017/12/28 11:45.
 * Email: zhangman523@126.com
 */

public class ListViewAdapter extends BaseAdapter {
  private int mCount = 10;

  @Override
  public int getCount() {
    return mCount;
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
    ViewHolder viewHolder;
    if (convertView == null) {
      convertView =
          LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text, parent, false);
      viewHolder = new ViewHolder(convertView);
      convertView.setTag(viewHolder);
    } else {
      viewHolder = (ViewHolder) convertView.getTag();
    }
    viewHolder.mItemLabel.setBackgroundResource(
        position % 2 == 1 ? R.color.red : R.color.colorPrimary);
    return convertView;
  }

  static class ViewHolder {
    TextView mItemLabel;

    public ViewHolder(View view) {
      mItemLabel = view.findViewById(R.id.item_label);
    }
  }
}


