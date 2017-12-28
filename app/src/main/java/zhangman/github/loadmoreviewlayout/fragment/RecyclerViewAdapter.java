package zhangman.github.loadmoreviewlayout.fragment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import zhangman.github.loadmoreviewlayout.R;

/**
 * Created by zhangman on 2017/12/28 13:23.
 * Email: zhangman523@126.com
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

  @Override
  public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, int position) {
    holder.mItemLabel.setBackgroundResource(position % 2 == 1 ? R.color.red : R.color.colorPrimary);
  }

  @Override
  public int getItemCount() {
    return 10;
  }

  static class ViewHolder extends RecyclerView.ViewHolder {
    TextView mItemLabel;

    public ViewHolder(View view) {
      super(view);
      mItemLabel = view.findViewById(R.id.item_label);
    }
  }
}
