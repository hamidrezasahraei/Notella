package sahraei.hamidreza.com.notella.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import sahraei.hamidreza.com.notella.Model.Folder;
import sahraei.hamidreza.com.notella.Model.ListItem;
import sahraei.hamidreza.com.notella.Model.Note;
import sahraei.hamidreza.com.notella.NoteDetailActivity;
import sahraei.hamidreza.com.notella.NoteDetailFragment;
import sahraei.hamidreza.com.notella.R;

/**
 * Created by hamidrezasahraei on 23/6/2017 AD.
 * Adapter for main list which contains notes and folders
 * and user can select folders or notes
 * this adapter support two pane layout for tablets and wider screens
 */

public class NoteListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private SortedList<ListItem> items;
    private Context mContext;
    private boolean isTwoPane = false;

    public static final int NOTE_ITEM_TYPE = 0;
    public static final int FOLDER_ITEM_TYPE = 1;

    public NoteListAdapter(Context context) {
        this(context, false);
    }

    public NoteListAdapter(Context context, boolean isTwoPane) {
        this.mContext = context;
        this.isTwoPane = isTwoPane;
        this.items = new SortedList<ListItem>(ListItem.class, new SortedList.Callback<ListItem>() {
            @Override
            public int compare(ListItem o1, ListItem o2) {
                if (o2 instanceof Folder && o1 instanceof Note)
                    return 1;
                if (o1 instanceof Note && o2 instanceof Note){
                    return ((Note) o2).getCreationDate().compareTo(((Note) o1).getCreationDate());
                }

                return 0;
            }

            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(ListItem oldItem, ListItem newItem) {
//                // return whether the items' visual representations are the same or not.
//                if (oldItem instanceof Note){
//
//                }
                return false;
            }

            @Override
            public boolean areItemsTheSame(ListItem item1, ListItem item2) {
                return false;
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutRes = 0;
        View view;
        switch (viewType) {
            case NOTE_ITEM_TYPE:
                layoutRes = R.layout.note_recyclerview_item;
                view = LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
                return new NoteItemViewHolder(view);

            case FOLDER_ITEM_TYPE:
                layoutRes = R.layout.folder_recyclerview_item;
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
                final NoteItemViewHolder noteItemViewHolder = (NoteItemViewHolder) holder;
                noteItemViewHolder.note = (Note)items.get(position);
                noteItemViewHolder.titleTextView.setText(noteItemViewHolder.note.getTitle());
                noteItemViewHolder.descriptionTextView.setText(convertNoteTextToText(noteItemViewHolder.note.getText()).replace("\n"," "));
                noteItemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isTwoPane){
                            Bundle arguments = new Bundle();
                            arguments.putString(NoteDetailFragment.ARG_ITEM_ID, noteItemViewHolder.note.getId());
                            arguments.putString(NoteDetailFragment.ARG_ITEM_PARENT_ID, noteItemViewHolder.note.getParentId());
                            NoteDetailFragment fragment = new NoteDetailFragment();
                            fragment.setArguments(arguments);
                            ((AppCompatActivity)mContext).getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.note_detail_container, fragment)
                                    .commit();
                        }else {
                            Intent intent = new Intent(mContext, NoteDetailActivity.class);
                            intent.putExtra(NoteDetailFragment.ARG_ITEM_ID, noteItemViewHolder.note.getId());
                            intent.putExtra(NoteDetailFragment.ARG_ITEM_PARENT_ID, noteItemViewHolder.note.getParentId());

                            mContext.startActivity(intent);
                        }
                    }
                });

                break;
            case FOLDER_ITEM_TYPE:
                final FolderItemViewHolder folderItemViewHolder = (FolderItemViewHolder) holder;
                folderItemViewHolder.folder = (Folder)items.get(position);
                folderItemViewHolder.titleTextView.setText(folderItemViewHolder.folder.getTitle());
                folderItemViewHolder.descriptionTextView.setText(folderItemViewHolder.folder.getDescription());

                if (mContext instanceof OpenFolderCallBack){
                    folderItemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ((OpenFolderCallBack) mContext).openFolder(folderItemViewHolder.folder.getId());
                        }
                    });
                }

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

    // region PageList Helpers
    public ListItem get(int position) {
        return items.get(position);
    }

    public int add(ListItem item) {
        return items.add(item);
    }

    public int indexOf(ListItem item) {
        return items.indexOf(item);
    }

    public void updateItemAt(int index, ListItem item) {
        items.updateItemAt(index, item);
    }

    public void addAll(List<? extends ListItem> items) {
        this.items.beginBatchedUpdates();
        for (ListItem item : items) {
            this.items.add(item);
        }
        this.items.endBatchedUpdates();
    }

    public void addAll(ListItem[] items) {
        addAll(Arrays.asList(items));
    }

    public boolean remove(ListItem item) {
        return items.remove(item);
    }

    public ListItem removeItemAt(int index) {
        return items.removeItemAt(index);
    }

    public void clear() {
        items.beginBatchedUpdates();
        //remove items at end, to avoid unnecessary array shifting
        while (items.size() > 0) {
            items.removeItemAt(items.size() - 1);
        }
        items.endBatchedUpdates();
    }

    private class NoteItemViewHolder extends RecyclerView.ViewHolder{
        View container;
        TextView titleTextView;
        TextView descriptionTextView;
        Note note;

        NoteItemViewHolder(View itemView) {
            super(itemView);
            container = itemView;
            titleTextView = (TextView) itemView.findViewById(R.id.note_card_title);
            descriptionTextView = (TextView) itemView.findViewById(R.id.note_card_desc);
        }
    }

    private class FolderItemViewHolder extends RecyclerView.ViewHolder{
        View container;
        TextView titleTextView;
        TextView descriptionTextView;
        Folder folder;

        FolderItemViewHolder(View itemView) {
            super(itemView);
            container = itemView;
            titleTextView = (TextView) itemView.findViewById(R.id.folder_card_title);
            descriptionTextView = (TextView) itemView.findViewById(R.id.folder_card_desc);
        }
    }

    @SuppressWarnings("deprecation")
    private String convertNoteTextToText(String htmlText) {
        String text;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            text = Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY).toString();
        } else {
            text = Html.fromHtml(htmlText).toString();
        }
        return text;
    }
}
