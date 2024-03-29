package sahraei.hamidreza.com.notella.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sahraei.hamidreza.com.notella.R;

/**
 * Created by hamidrezasahraei on 24/6/2017 AD.
 */

public class ColorPickerGridRecyclerAdapter extends RecyclerView.Adapter<ColorPickerGridRecyclerAdapter.ViewHolder> {

    private String[] mData;
    private LayoutInflater mInflater;
    private ColorClickListener mColorClickListener;

    // data is passed into the constructor
    public ColorPickerGridRecyclerAdapter(Context context, String[] data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the cell layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.color_picker_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the textview in each cell
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String animal = mData[position];
        GradientDrawable gd = (GradientDrawable) holder.colorCircle.getBackground().getCurrent();
        gd.setColor(Color.parseColor(mData[position]));
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return mData.length;
    }


    // stores and recycles views as they are scrolled off screen
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View colorCircle;

        ViewHolder(View itemView) {
            super(itemView);
            colorCircle = itemView.findViewById(R.id.color_picker_circle);
            colorCircle.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mColorClickListener != null) mColorClickListener.onColorClick(getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public String getItem(int id) {
        return mData[id];
    }

    // allows clicks events to be caught
    public void setClickListener(ColorClickListener itemClickListener) {
        this.mColorClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ColorClickListener {
        void onColorClick(int position);
    }
}
