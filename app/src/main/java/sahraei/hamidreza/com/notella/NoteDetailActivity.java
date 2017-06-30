package sahraei.hamidreza.com.notella;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import sahraei.hamidreza.com.notella.Database.AppDatabase;
import sahraei.hamidreza.com.notella.Model.Note;

/**
 * An activity representing a single Note detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link NoteListActivity}.
 */
public class NoteDetailActivity extends AppCompatActivity {

    String noteItemId;
    String parentId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);

        noteItemId = getIntent().getStringExtra(NoteDetailFragment.ARG_ITEM_ID);
        parentId = getIntent().getStringExtra(NoteDetailFragment.ARG_ITEM_PARENT_ID);;

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
//            arguments.putString(NoteDetailFragment.ARG_ITEM_ID,
//                    getIntent().getStringExtra(NoteDetailFragment.ARG_ITEM_ID));
            arguments.putString(NoteDetailFragment.ARG_ITEM_ID,
                    noteItemId);
            arguments.putString(NoteDetailFragment.ARG_ITEM_PARENT_ID,
                    parentId);
            NoteDetailFragment fragment = new NoteDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.note_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note_detail_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, NoteListActivity.class).putExtra(NoteListActivity.ARG_NOTE_PARENT, parentId));
            return true;
        }
        if (id == R.id.action_delete) {
            showDialogOK(getString(R.string.sure_message), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            new DeleteNote().execute(noteItemId);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:

                            break;
                    }
                }
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class DeleteNote extends AsyncTask<String,Void,Void>{

        @Override
        protected Void doInBackground(String... strings) {
            String noteId = strings[0];
            AppDatabase.getInstance(getApplicationContext()).noteDAO().deleteById(noteId);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getApplicationContext(), R.string.successful_delete, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), NoteListActivity.class);
            intent.putExtra(NoteListActivity.ARG_NOTE_PARENT, parentId);
            startActivity(intent);
            finish();
        }
    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(getResources().getString(R.string.Submit), okListener)
                .setNegativeButton(getResources().getString(R.string.cancel), okListener)
                .create()
                .show();
    }
}
