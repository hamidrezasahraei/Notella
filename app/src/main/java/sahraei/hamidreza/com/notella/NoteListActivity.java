package sahraei.hamidreza.com.notella;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;


import sahraei.hamidreza.com.notella.Adapter.NoteListAdapter;
import sahraei.hamidreza.com.notella.Adapter.OpenFolderCallBack;
import sahraei.hamidreza.com.notella.Database.AppDatabase;
import sahraei.hamidreza.com.notella.Model.Folder;
import sahraei.hamidreza.com.notella.Model.ListItem;
import sahraei.hamidreza.com.notella.Model.Note;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Note. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link NoteDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class NoteListActivity extends AppCompatActivity implements OpenFolderCallBack {

    private List<Object> items;

    SharedPreferences prefs;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    Folder rootFolder;

    String currentFolderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);


        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.note_list);
        assert recyclerView != null;

        if (findViewById(R.id.note_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: ADD Note
                if (mTwoPane){
                    Bundle arguments = new Bundle();
                    arguments.putString(NoteDetailFragment.ARG_ITEM_ID, NoteDetailFragment.NEW_NOTE_VALUE);
                    arguments.putString(NoteDetailFragment.ARG_ITEM_PARENT_ID, currentFolderId);
                    NoteDetailFragment fragment = new NoteDetailFragment();
                    fragment.setArguments(arguments);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.note_detail_container, fragment)
                            .commit();
                }else {
                    Intent intent = new Intent(getApplicationContext(), NoteDetailActivity.class);
                    intent.putExtra(NoteDetailFragment.ARG_ITEM_ID, NoteDetailFragment.NEW_NOTE_VALUE);
                    intent.putExtra(NoteDetailFragment.ARG_ITEM_PARENT_ID, currentFolderId);
                    startActivity(intent);
                }
            }
        });

        if (prefs.getBoolean("firstrun", true)) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    Folder folder = new Folder(getString(R.string.all));
                    AppDatabase.getInstance(getApplicationContext()).folderDAO().insertAll(folder);
                    setupRecyclerView(recyclerView);
                }
            });
            prefs.edit().putBoolean("firstrun", false).commit();
        }else {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    setupRecyclerView(recyclerView);
                }
            });
        }


    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        NoteListAdapter noteListAdapter = new NoteListAdapter(this, mTwoPane);
        recyclerView.setAdapter(noteListAdapter);
        Folder folder = AppDatabase.getInstance(this).folderDAO().findRootDirectory();
        currentFolderId = folder.getId();
        List<Folder> childFolders = AppDatabase.getInstance(this).folderDAO().findChildFolder(folder.getId());
        noteListAdapter.addAll(childFolders);
        List<Note> childNotes = AppDatabase.getInstance(this).noteDAO().loadAllByParentId(currentFolderId);
        noteListAdapter.addAll(childNotes);
    }

    @Override
    public void openFolder(String folderId) {

    }

//    public class FindChildFolders extends AsyncTask<Void, Integer, List<Folder>>{
//
//        @Override
//        protected List<Folder> doInBackground(String... folderId) {
//            String rootId = folderId[0];
//            return AppDatabase.getInstance(getApplicationContext()).folderDAO().findChildFolder(rootId);
//        }
//
//        @Override
//        protected void onPostExecute(List<Folder> folders) {
//            super.onPostExecute(folders);
//
//        }
//    }


}
