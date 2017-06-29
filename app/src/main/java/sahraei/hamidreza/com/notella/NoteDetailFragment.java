package sahraei.hamidreza.com.notella;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.ViewStubCompat;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import sahraei.hamidreza.com.notella.Adapter.ColorPickerGridRecyclerAdapter;
import sahraei.hamidreza.com.notella.CustomView.DrawingView;
import sahraei.hamidreza.com.notella.Database.AppDatabase;
import sahraei.hamidreza.com.notella.Model.Note;

import static android.content.Context.INPUT_METHOD_SERVICE;

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
    private Note mNote;

    /**
     * The edittext which user writes his notes.
     */
    private EditText editText;

    /**
     * Title of note
     */
    private EditText titleEditText;

    /**
     * Used for keeping user text and store it as HTML in DB
     * for formatting and styling
     */
    private Spannable noteTextSpannable;

    View textModeMarkupContainer;
    View drawModeMarkupContainer;
    ViewStubCompat drawModeMarkupContainerViewStub;

    ImageButton boldTextButton;
    ImageButton italicTextButton;
    ImageButton strikeTextButton;
    ImageButton colorPickerButton;
    ImageButton drawButton;

    ImageButton clearDrawButton;
    ImageButton eraserButton;
    ImageButton changeBrushColorButton;
    View changeBrushSizeButton;
    TextView brushSizeTextView;
    ImageButton textModeButton;

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

    private boolean isEraseMode;
    private boolean isPaintMode;

    // Drawing canvas
    private DrawingView drawingView;

    // Brush sizes
    private static final float
            SMALL_BRUSH = 5,
            MEDIUM_BRUSH = 10,
            LARGE_BRUSH = 20;
    private AlertDialog brushSizeDialog;


    Handler saveIntervalHandler = new Handler();
    int delay = 5000; //15 seconds
    Runnable saveRunnable;
    boolean hasUnsavedChanges = false;
    boolean editTextListenForChanges = true;


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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.note_detail, container, false);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.detail_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        editText = (EditText) rootView.findViewById(R.id.note_edittext);
        titleEditText = (EditText) toolbar.findViewById(R.id.note_title_edittext);

        drawModeMarkupContainerViewStub = (ViewStubCompat) rootView.findViewById(R.id.draw_markup_container);
        textModeMarkupContainer = rootView.findViewById(R.id.text_markup_container);

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

        isEraseMode = false;
        isPaintMode = true;

        progressBar = (ProgressBar) rootView.findViewById(R.id.progressbar);

        noteTextSpannable = editText.getText();

        // Handling drawingView's onTouchListener via EditText onTouchListener
        editText.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isDrawModeOn) {
                    drawingView.onTouchEvent(event);
                    hasUnsavedChanges = true;
                    return true;
                } else {
                    return false;
                }
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        if (getArguments().containsKey(ARG_ITEM_PARENT_ID)) {
            parentId = getArguments().getString(ARG_ITEM_PARENT_ID);
        }

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            noteId = getArguments().getString(ARG_ITEM_ID);
            if (noteId.equals(NEW_NOTE_VALUE)) {
                //Make a new note
                mNote = new Note("", "", parentId);
            } else {
                //Load existing note from database and show it for editing
                new GetNoteFromDB().execute(noteId);
            }
        }

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editTextListenForChanges) {
                    if (!hasUnsavedChanges)
                        hasUnsavedChanges = true;
                }
            }
        });

        titleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                System.out.println(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editTextListenForChanges) {
                    if (!hasUnsavedChanges)
                        hasUnsavedChanges = true;
                }
            }
        });

        /**
         * This handler is for auto saving, every 'delay' seconds will save the note if a it has a change
         */
        saveIntervalHandler.postDelayed(new Runnable() {
            public void run() {
                //do something
                if (hasUnsavedChanges) {
                    saveNote();
                    Toast.makeText(getContext(), R.string.saved_impression, Toast.LENGTH_SHORT).show();
                    hasUnsavedChanges = false;
                }

                saveRunnable = this;
                saveIntervalHandler.postDelayed(saveRunnable, delay);
            }
        }, delay);

        super.onStart();
    }

    @Override
    public void onPause() {
        saveIntervalHandler.removeCallbacks(saveRunnable); //stop handler when activity not visible
        super.onPause();
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
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
                if (drawModeMarkupContainer == null) {
                    drawModeMarkupContainer = drawModeMarkupContainerViewStub.inflate();
                }
                setDrawPanelButtonsOnClick();
                toggleTextDrawMode();
                break;
            case R.id.draw_format_text:
                toggleTextDrawMode();
                break;

            case R.id.draw_eraser:
                toggleEraserBrush();
                break;

            case R.id.draw_refresh:
                eraseAllDraws();
                hasUnsavedChanges = true;
                break;
            case R.id.draw_brush_size:
                showBrushSizeSelectorDialog();
                break;

            case R.id.brush_small:
                changeBrushSize(SMALL_BRUSH);
                break;

            case R.id.brush_medium:
                changeBrushSize(MEDIUM_BRUSH);
                break;

            case R.id.brush_large:
                changeBrushSize(LARGE_BRUSH);
                break;
        }
    }

    /**
     * This method will give style to a text such as changing color, making bold, italic and ...
     * It works with 'edittext' defined in this activity.
     * @param styleSpan
     */
    private void formatText(ParcelableSpan styleSpan) {
        noteTextSpannable = editText.getText();
        int posStart = editText.getSelectionStart();
        int posEnd = editText.getSelectionEnd();
        noteTextSpannable.setSpan(styleSpan, posStart, posEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        editText.setText(noteTextSpannable);
        editText.setSelection(editText.getText().length());
    }

    /**
     * This method is show a a dialog with a list of colors in it and user can
     * select one of them and with the help of a callback the fragment will find out and use
     * formattext method or in drawing mode: drawingView.setColor() in order to change color
     */

    private void showColorPickerDialog() {
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

    /**
     * This method shows a dialog with three brush size in it and user
     * can select a size for brush in drawing mode
     */
    private void showBrushSizeSelectorDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        View convertView = LayoutInflater.from(getActivity()).inflate(R.layout.brush_size_selector_dialog, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle(R.string.choose_brush_size);
        brushSizeDialog = alertDialog.create();
        ImageButton mediumBrushButton = (ImageButton) convertView.findViewById(R.id.brush_medium);
        ImageButton smallBrushButton = (ImageButton) convertView.findViewById(R.id.brush_small);
        ImageButton largeBrushButton = (ImageButton) convertView.findViewById(R.id.brush_large);

        mediumBrushButton.setOnClickListener(this);
        smallBrushButton.setOnClickListener(this);
        largeBrushButton.setOnClickListener(this);

        brushSizeDialog.show();
    }

    /**
     * This is a callback method for color picker recyclerview which hold colors
     * We find the selected color with position parameter and select the color from the array.
     * @param position
     */
    @Override
    public void onColorClick(int position) {
        int colorCode = Color.parseColor(colors[position]);

        if (isTextModeOn) {
            noteTextSpannable = editText.getText();
            noteTextSpannable.setSpan(new ForegroundColorSpan(colorCode), selectedTextStartPos, selectedTextEndPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            editText.setText(noteTextSpannable);
            editText.setSelection(editText.getText().length());
        } else if (isDrawModeOn) {
            drawingView.setPaintColor(colorCode);
            changeBrushColorButton.setColorFilter(colorCode);
        }
        colorPickerDialog.dismiss();
    }

    @SuppressWarnings("deprecation")
    public void saveNote() {
        noteTitle = titleEditText.getText().toString();
        noteTextSpannable = editText.getText();
        if (noteTitle == null || noteTitle.equals("")) {
            noteTitle = "New Note";
        }
        String htmlText;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            htmlText = Html.toHtml(noteTextSpannable, Html.FROM_HTML_MODE_LEGACY);
        } else {
            htmlText = Html.toHtml(noteTextSpannable);
        }
        mNote.setTitle(noteTitle);
        mNote.setText(htmlText);
        mNote.setCreationDate(new Date());
        mNote.setDraw(drawingView.getCanvasBitmap());
        new SaveNoteToDB().execute(mNote);
    }


    public class SaveNoteToDB extends AsyncTask<Note, Integer, Void> {

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

    public class GetNoteFromDB extends AsyncTask<String, Integer, Note> {

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
            mNote = note;
            setNoteToView(note);
        }
    }

    @SuppressWarnings("deprecation")
    private void setNoteToView(Note note) {
        Spanned text;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            text = Html.fromHtml(note.getText(), Html.FROM_HTML_MODE_LEGACY);
        } else {
            text = Html.fromHtml(note.getText());
        }
        editTextListenForChanges = false;
        titleEditText.setText(note.getTitle());
        titleEditText.setSelection(note.getTitle().length());
        editText.setText(text);
        editTextListenForChanges = true;
        drawingView.setBitmap(note.getDraw());


    }

    private void checkViewStubInflated(ViewStubCompat viewStubCompat, View inflatedView) {
        if (viewStubCompat.getParent() != null) {
            inflatedView = viewStubCompat.inflate();
        }

    }

    private void setDrawPanelButtonsOnClick() {
        clearDrawButton = (ImageButton) drawModeMarkupContainer.findViewById(R.id.draw_refresh);
        eraserButton = (ImageButton) drawModeMarkupContainer.findViewById(R.id.draw_eraser);
        changeBrushSizeButton = drawModeMarkupContainer.findViewById(R.id.draw_brush_size);
        textModeButton = (ImageButton) drawModeMarkupContainer.findViewById(R.id.draw_format_text);
        changeBrushColorButton = (ImageButton) drawModeMarkupContainer.findViewById(R.id.text_format_color);
        brushSizeTextView = (TextView) drawModeMarkupContainer.findViewById(R.id.draw_brush_size_text);

        textModeButton.setOnClickListener(this);
        clearDrawButton.setOnClickListener(this);
        changeBrushSizeButton.setOnClickListener(this);
        eraserButton.setOnClickListener(this);
        changeBrushColorButton.setOnClickListener(this);
    }

    private void toggleTextDrawMode() {
        isTextModeOn = !isTextModeOn;
        isDrawModeOn = !isDrawModeOn;
        if (isTextModeOn) {
            if (drawModeMarkupContainer.getVisibility() == View.VISIBLE) {
                drawModeMarkupContainer.setVisibility(View.INVISIBLE);
                textModeMarkupContainer.setVisibility(View.VISIBLE);
            }
        } else {
            if (drawModeMarkupContainer.getVisibility() != View.VISIBLE) {
                drawModeMarkupContainer.setVisibility(View.VISIBLE);
                textModeMarkupContainer.setVisibility(View.INVISIBLE);
            }
            try {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            } catch (Exception e) {

            }
        }
    }

    private void toggleEraserBrush() {
        isPaintMode = !isPaintMode;
        isEraseMode = !isEraseMode;
        if (isEraseMode) {
            eraserButton.setImageResource(R.drawable.ic_brush_white_24dp);
        } else {
            eraserButton.setImageResource(R.drawable.ic_eraser_white_24dp);
        }
        drawingView.setErase(isEraseMode);
    }

    public void changeBrushSize(float size) {
        drawingView.setBrushSize(size);
        if (brushSizeDialog.isShowing()) {
            brushSizeDialog.dismiss();
        }
        if (size <= SMALL_BRUSH) {
            brushSizeTextView.setText(getResources().getString(R.string.small));
        } else if (size > SMALL_BRUSH && size < LARGE_BRUSH) {
            brushSizeTextView.setText(getResources().getString(R.string.medium));
        } else if (size >= LARGE_BRUSH) {
            brushSizeTextView.setText(getResources().getString(R.string.large));
        }
    }

    private void eraseAllDraws() {
        drawingView.startNew();
    }

    private void showProgressbar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressbar() {
        progressBar.setVisibility(View.GONE);
    }

}
