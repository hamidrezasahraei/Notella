package sahraei.hamidreza.com.notella.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import sahraei.hamidreza.com.notella.Model.Folder;
import sahraei.hamidreza.com.notella.Model.Note;
import sahraei.hamidreza.com.notella.NoteListActivity;
import sahraei.hamidreza.com.notella.R;

/**
 * Created by hamidrezasahraei on 23/6/2017 AD.
 */

public class NoteListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> items;
    private Context mContext;
    private boolean isTwoPane = false;

    private final int NOTE_ITEM_TYPE = 0;
    private final int FOLDER_ITEM_TYPE = 1;

    public NoteListAdapter(Context context, List<Object> items) {
        this.items = items;
        this.mContext = context;
    }

    public NoteListAdapter(Context context, List<Object> items, boolean isTwoPane) {
        this.items = items;
        this.mContext = context;
        this.isTwoPane = isTwoPane;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutRes = 0;
        View view;
        switch (viewType) {
            case NOTE_ITEM_TYPE:
                layoutRes = R.layout.note_item_recyclerview;
                view = LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
                return new NoteItemViewHolder(view);

            case FOLDER_ITEM_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
                return new FolderItemViewHolder(view);
        }
        view = LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
        return new NoteItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case NOTE_ITEM_TYPE:

                break;
            case FOLDER_ITEM_TYPE:

                break;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object current = items.get(position);
        if (current instanceof Note){
            return NOTE_ITEM_TYPE;
        }else if(current instanceof Folder){
            return FOLDER_ITEM_TYPE;
        }else {
            return -1;
        }
    }

    private class NoteItemViewHolder extends RecyclerView.ViewHolder{

        NoteItemViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class FolderItemViewHolder extends RecyclerView.ViewHolder{

        FolderItemViewHolder(View itemView) {
            super(itemView);
        }
    }
}
