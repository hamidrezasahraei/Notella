package sahraei.hamidreza.com.notella;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.ParcelableSpan;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import sahraei.hamidreza.com.notella.Adapter.ColorPickerGridRecyclerAdapter;
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
     */
    public static final String ARG_ITEM_ID = "item_id";

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
    private Spannable spannable;

    ImageButton boldTextButton;
    ImageButton italicTextButton;
    ImageButton strikeTextButton;
    ImageButton colorPickerButton;
    ImageButton drawButton;

    String[] colors = {"#000000", "#FFF44336", "#E91E63", "#9C27B0", "#673AB7", "#3F51B5", "#2196F3",
            "#03A9F4", "#00BCD4", "#009688", "#4CAF50", "#8BC34A", "#CDDC39", "#FFEB3B", "#FFC107",
            "#FF9800", "#FF5722", "#795548", "#9E9E9E", "#607D8B"};
    Dialog colorPickerDialog;

    int selectedTextStartPos;
    int selectedTextEndPos;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NoteDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
//            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
            //TODO: Load note into this page

            Activity activity = this.getActivity();
//            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
//            if (appBarLayout != null) {
//                appBarLayout.setTitle(mItem.getTitle());
//            }
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

        boldTextButton.setOnClickListener(this);
        italicTextButton.setOnClickListener(this);
        strikeTextButton.setOnClickListener(this);
        colorPickerButton.setOnClickListener(this);
        drawButton.setOnClickListener(this);

        spannable = editText.getText();

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

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus){
//                    selectedTextStartPos = editText.getSelectionStart();
//                    selectedTextEndPos = editText.getSelectionEnd();
                    System.out.println("hasnotfocued");
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

                break;
        }
    }

    private void formatText(ParcelableSpan styleSpan){
        spannable = editText.getText();
        int posStart = editText.getSelectionStart();
        int posEnd = editText.getSelectionEnd();
        spannable.setSpan(styleSpan, posStart, posEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        editText.setText(spannable);
        editText.setSelection(editText.getText().length());
    }

    private void showColorPickerDialog(){
        int colorsColumnNumber = 4;

        selectedTextStartPos = editText.getSelectionStart();
        selectedTextEndPos = editText.getSelectionEnd();

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        View convertView = LayoutInflater.from(getActivity()).inflate(R.layout.color_picker_dialog, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle("Choose a color:");
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
        spannable = editText.getText();
        spannable.setSpan(new ForegroundColorSpan(colorCode), selectedTextStartPos, selectedTextEndPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        editText.setText(spannable);
        editText.setSelection(editText.getText().length());
        colorPickerDialog.dismiss();
    }
}
