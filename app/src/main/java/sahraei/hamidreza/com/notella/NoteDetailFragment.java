package sahraei.hamidreza.com.notella;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.ParcelableSpan;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import sahraei.hamidreza.com.notella.Adapter.ColorPickerGridRecyclerAdapter;
import sahraei.hamidreza.com.notella.CustomView.DrawingView;
import sahraei.hamidreza.com.notella.Database.AppDatabase;
import sahraei.hamidreza.com.notella.Model.Note;

/**
 * A fragment representing a single Note detail screen.
 * This fragment is either contained in a {@link NoteListActivity}
 * in two-pane mode (on tablets) or a {@link NoteDetailActivity}
 * on handsets.
 */
public class NoteDetailFragment extends Fragment implements View.OnClickListener, ColorPickerGridRecyclerAdapter.ColorClickListener {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     * if item_id equals to "NEW" means that want to create a new note
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static final String NEW_NOTE_VALUE = "NEW";

    public static final String ARG_ITEM_PARENT_ID = "parent_id";

    /**
     * The note content this fragment is presenting.
     */
    private Note mItem;

    /**
     * The edittext which user writes his notes.
     */
    private EditText editText;

    /**
     * Used for keeping user text and store it as HTML in DB
     * for formatting and styling
     */
    private Spannable noteTextSpannable;

    ImageButton boldTextButton;
    ImageButton italicTextButton;
    ImageButton strikeTextButton;
    ImageButton colorPickerButton;
    ImageButton drawButton;

    /**
     * indeterminate progressbar for saving and fetching in android
     */
    ProgressBar progressBar;

    String[] colors = {"#000000", "#FFF44336", "#E91E63", "#9C27B0", "#673AB7", "#3F51B5", "#2196F3",
            "#03A9F4", "#00BCD4", "#009688", "#4CAF50", "#8BC34A", "#CDDC39", "#FFEB3B", "#FFC107",
            "#FF9800", "#FF5722", "#795548", "#9E9E9E", "#607D8B"};
    Dialog colorPickerDialog;

    int selectedTextStartPos;
    int selectedTextEndPos;

    /**
     * ID of note
     * if eauals to "NEW" means new note, otherwise used for loading a note
     */
    String noteId;

    /**
     * Title of note
     */
    String noteTitle;

    /**
     * Parent of this note which come from intent
     */
    String parentId;


    // Draw mode booleans
    private boolean isDrawModeOn;
    private boolean isTextModeOn;

    // Drawing canvas
    private DrawingView drawingView;

    // Brush sizes
    private static final float
            SMALL_BRUSH = 5,
            MEDIUM_BRUSH = 10,
            LARGE_BRUSH = 20;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NoteDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


        if (getArguments().containsKey(ARG_ITEM_ID)) {
            noteId = getArguments().getString(ARG_ITEM_ID);
            if (noteId.equals(NEW_NOTE_VALUE)){
                //TODO:new note
            }else {
                //TODO:fetch from db
            }
        }

        if (getArguments().containsKey(ARG_ITEM_PARENT_ID)) {
            parentId = getArguments().getString(ARG_ITEM_PARENT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.note_detail, container, false);

        editText = (EditText) rootView.findViewById(R.id.note_edittext);
        boldTextButton = (ImageButton) rootView.findViewById(R.id.text_format_bold);
        italicTextButton = (ImageButton) rootView.findViewById(R.id.text_format_italic);
        strikeTextButton = (ImageButton) rootView.findViewById(R.id.text_format_strikethrough);
        colorPickerButton = (ImageButton) rootView.findViewById(R.id.text_format_color);
        drawButton = (ImageButton) rootView.findViewById(R.id.text_format_brush);
        drawingView = (DrawingView) rootView.findViewById(R.id.drawing_view);

        boldTextButton.setOnClickListener(this);
        italicTextButton.setOnClickListener(this);
        strikeTextButton.setOnClickListener(this);
        colorPickerButton.setOnClickListener(this);
        drawButton.setOnClickListener(this);

        // set boolean values
        isDrawModeOn = false;
        isTextModeOn = true;



        progressBar = (ProgressBar) rootView.findViewById(R.id.progressbar);

        noteTextSpannable = editText.getText();

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                System.out.println(editable.toString());
            }
        });

        // Handling drawingView's onTouchListener via EditText onTouchListener
        editText.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isDrawModeOn) {
                    drawingView.onTouchEvent(event);
                    return true;
                } else {
                    return false;
                }
            }
        });


        // Show the dummy content as text in a TextView.
        if (mItem != null) {
//            ((TextView) rootView.findViewById(R.id.note_detail)).setText(mItem.details);
        }

        return rootView;
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.text_format_bold:
                formatText(new StyleSpan(Typeface.BOLD));
                break;
            case R.id.text_format_italic:
                formatText(new StyleSpan(Typeface.ITALIC));
                break;
            case R.id.text_format_strikethrough:
                formatText(new StrikethroughSpan());
                break;
            case R.id.text_format_color:
                showColorPickerDialog();
                break;

            case R.id.text_format_brush:
                isDrawModeOn = true;
                isTextModeOn = false;
                break;
        }
    }

    private void formatText(ParcelableSpan styleSpan){
        noteTextSpannable = editText.getText();
        int posStart = editText.getSelectionStart();
        int posEnd = editText.getSelectionEnd();
        noteTextSpannable.setSpan(styleSpan, posStart, posEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        editText.setText(noteTextSpannable);
        editText.setSelection(editText.getText().length());
    }

    private void showColorPickerDialog(){
        int colorsColumnNumber = 4;

        selectedTextStartPos = editText.getSelectionStart();
        selectedTextEndPos = editText.getSelectionEnd();

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        View convertView = LayoutInflater.from(getActivity()).inflate(R.layout.color_picker_dialog, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle(R.string.choose_color);
        colorPickerDialog = alertDialog.create();
        RecyclerView rv = (RecyclerView) convertView.findViewById(R.id.color_picker_list);
        rv.setLayoutManager(new GridLayoutManager(getActivity(), colorsColumnNumber));
        rv.setHasFixedSize(true);

        ColorPickerGridRecyclerAdapter adapter = new ColorPickerGridRecyclerAdapter(getActivity(), colors);
        adapter.setClickListener(this);
        rv.setAdapter(adapter);

        colorPickerDialog.show();
    }

    @Override
    public void onColorClick(View view, int position) {
        int colorCode = Color.parseColor(colors[position]);
        /**
         * use for changing color of color picker button while user typing.
         */
//        colorPickerButton.setColorFilter(colorCode);
        noteTextSpannable = editText.getText();
        noteTextSpannable.setSpan(new ForegroundColorSpan(colorCode), selectedTextStartPos, selectedTextEndPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        editText.setText(noteTextSpannable);
        editText.setSelection(editText.getText().length());
        colorPickerDialog.dismiss();
    }

    @SuppressWarnings("deprecation")
    public void saveNote(){
        String htmlText;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            htmlText = Html.toHtml(noteTextSpannable, Html.FROM_HTML_MODE_LEGACY);
        } else {
            htmlText = Html.toHtml(noteTextSpannable);
        }
        Note note = new Note(noteTitle, htmlText);
        AppDatabase.getInstance(getContext()).noteDAO().insertAll(note);
    }


    public class SaveNoteToDB extends AsyncTask<Note, Integer, Void>{

        @Override
        protected Void doInBackground(Note... notes) {
            AppDatabase.getInstance(getContext()).noteDAO().insertAll(notes[0]);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressbar();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            hideProgressbar();
        }
    }

    public class GetNoteFromDB extends AsyncTask<String,Integer,Note>{

        @Override
        protected Note doInBackground(String... strings) {
            String noteID = strings[0];
            Note note = AppDatabase.getInstance(getContext()).noteDAO().loadNoteById(noteID);
            return note;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressbar();
        }

        @Override
        protected void onPostExecute(Note note) {
            super.onPostExecute(note);
            hideProgressbar();
            setNoteToView(note);
        }
    }

    @SuppressWarnings("deprecation")
    private void setNoteToView(Note note){
        Spanned text;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            text = Html.fromHtml(note.getText(), Html.FROM_HTML_MODE_LEGACY);
        } else {
            text = Html.fromHtml(note.getText());
        }
        editText.setText(text);

    }

    private void showProgressbar(){
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressbar(){
        progressBar.setVisibility(View.GONE);
    }
}
