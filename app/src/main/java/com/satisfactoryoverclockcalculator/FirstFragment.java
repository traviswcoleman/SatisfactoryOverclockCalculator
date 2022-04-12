package com.satisfactoryoverclockcalculator;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.satisfactoryoverclockcalculator.databinding.FragmentFirstBinding;

import java.util.Locale;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private TextWatcher calculateWatcher;
    private boolean calculating;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        calculateWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!calculating) {
                    calculating = true;
                    Calculate();
                    calculating = false;
                }
            }
        };

        binding.txtNumItems.addTextChangedListener(calculateWatcher);
        binding.txtNumPerMinute.addTextChangedListener(calculateWatcher);
        binding.txtNumBuildings.addTextChangedListener(calculateWatcher);
        binding.txtItemNeeded.addTextChangedListener(calculateWatcher);

        binding.btnAddRow.setOnClickListener(v -> addRow());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void Calculate()
    {
        double numItems = 0;
        double numPerMinute = 0;
        double minBuildings = 0;

        try{
            numItems = Double.parseDouble(binding.txtNumItems.getText().toString());
            numPerMinute = Double.parseDouble(binding.txtNumPerMinute.getText().toString());
        } finally
        {
            if(numItems == 0 || numPerMinute == 0) {
                binding.txtMinBuildings.setText(R.string.minBuildings_default);
                binding.txtOverclockPerc.setText(R.string.overclockPerc_default);
                binding.txtNumBuildings.setText("");
                clearInputsTable();
                return;
            }
        }

        try {
            minBuildings = Math.ceil(numItems / (numPerMinute * 2.5));
        } finally {
            if(minBuildings == 0) {
                clearInputsTable();
                return;
            }
        }

        binding.txtMinBuildings.setText(String.format(Locale.getDefault(), "%.0f", minBuildings));

        double numBuildings = 0;
        try {
            numBuildings = Double.parseDouble(binding.txtNumBuildings.getText().toString());
        } finally
        {
            if(numBuildings <= 0) {
                clearInputsTable();
                return;
            }
        }

        double overclockPerc = numItems / numBuildings / numPerMinute*100;

        binding.txtOverclockPerc.setText(String.format(Locale.getDefault(), "%.2f%%", overclockPerc));

        int i = 1;
        while(i < binding.tblInputs.getChildCount())
        {
            calculateTableRow((TableRow)binding.tblInputs.getChildAt(i), overclockPerc);
            i++;
        }
    }

    private void calculateTableRow(TableRow row, double overclockPerc)
    {
        EditText txtItemNeeded = row.findViewWithTag("txtItemNeeded");
        TextView txtItemTotalNeeded = row.findViewWithTag("txtItemTotalNeeded");
        if(txtItemNeeded == null || txtItemTotalNeeded == null ||
                txtItemNeeded.getText().toString().equals("")) return;

        txtItemTotalNeeded.setText(R.string.itemTotalNeeded_default);
        double itemNeeded = 0;
        try{
            itemNeeded = Double.parseDouble(txtItemNeeded.getText().toString());
        }
        finally {
            if(itemNeeded > 0)
                txtItemTotalNeeded.setText(String.format(Locale.getDefault(), "%.2f", itemNeeded * overclockPerc/100));
        }
    }

    private void clearInputsTable() {
        while(binding.tblInputs.getChildCount() > 2)
            binding.tblInputs.removeView(binding.tblInputs.getChildAt(2));
        TableRow row = (TableRow)binding.tblInputs.getChildAt(1);
        EditText txtItemNeeded = row.findViewWithTag("txtItemNeeded");
        TextView txtItemTotalNeeded = row.findViewWithTag("txtItemTotalNeeded");
        if(txtItemNeeded != null)
            txtItemNeeded.setText("");
        if(txtItemTotalNeeded != null)
            txtItemTotalNeeded.setText(R.string.itemTotalNeeded_default);
    }

    private void addRow()
    {
        TableRow newRow = new TableRow(binding.tblInputs.getContext());
        TableRow.LayoutParams params = new TableRow.LayoutParams();
        params.width = TableRow.LayoutParams.MATCH_PARENT;
        params.height = TableRow.LayoutParams.WRAP_CONTENT;

        newRow.setLayoutParams(params);

        Context rowContext = newRow.getContext();
        EditText txtItem = createTxtItem(rowContext);
        EditText txtItemNeeded = createTxtItemNeeded(rowContext);
        TextView txtItemTotalNeeded = createTxtItemTotalNeeded(rowContext);
        ImageButton btnRemoveRow = createBtnRemoveRow(rowContext);

        newRow.addView(txtItem);
        newRow.addView(txtItemNeeded);
        newRow.addView(txtItemTotalNeeded);
        newRow.addView(btnRemoveRow);

        binding.tblInputs.addView(newRow);
    }

    private EditText createTxtItem(Context context)
    {
        EditText txtItem = new EditText(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            txtItem.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO);
            txtItem.setInputType(View.AUTOFILL_TYPE_TEXT);
        }
        txtItem.setTag("txtItem");
        TableRow.LayoutParams params = new TableRow.LayoutParams();
        params.setLayoutDirection(View.LAYOUT_DIRECTION_INHERIT);
        params.leftMargin = (int)getResources().getDimension(R.dimen.cellPadding);
        params.rightMargin = (int)getResources().getDimension(R.dimen.rowSideMargin);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            txtItem.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO);
            txtItem.setInputType(View.AUTOFILL_TYPE_TEXT);
        }

        txtItem.setLayoutParams(params);
        txtItem.setSelectAllOnFocus(true);
        return txtItem;
    }

    private EditText createTxtItemNeeded(Context context)
    {
        EditText txtItemNeeded = new EditText(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            txtItemNeeded.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO);
            txtItemNeeded.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        }

        TableRow.LayoutParams params = new TableRow.LayoutParams();
        params.setLayoutDirection(View.LAYOUT_DIRECTION_INHERIT);
        params.rightMargin = (int)getResources().getDimension(R.dimen.rowSideMargin);
        params.leftMargin = (int)getResources().getDimension(R.dimen.cellPadding);
        txtItemNeeded.setLayoutParams(params);

        txtItemNeeded.addTextChangedListener(calculateWatcher);
        txtItemNeeded.setTag("txtItemNeeded");

        txtItemNeeded.setSelectAllOnFocus(true);
        return txtItemNeeded;
    }

    private TextView createTxtItemTotalNeeded(Context context)
    {
        TextView txtItemTotalNeeded = new TextView(context);
        txtItemTotalNeeded.setText(R.string.itemTotalNeeded_default);

        TableRow.LayoutParams params = new TableRow.LayoutParams();
        params.setLayoutDirection(View.LAYOUT_DIRECTION_INHERIT);
        params.rightMargin = (int)getResources().getDimension(R.dimen.cellPadding);
        txtItemTotalNeeded.setLayoutParams(params);
        txtItemTotalNeeded.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.inputTextSize));
        txtItemTotalNeeded.setTag("txtItemTotalNeeded");
        return txtItemTotalNeeded;
    }

    private ImageButton createBtnRemoveRow(Context context)
    {
        ImageButton btnRemoveRow = new ImageButton(context);
        TableRow.LayoutParams params = new TableRow.LayoutParams();
        params.setLayoutDirection(View.LAYOUT_DIRECTION_INHERIT);
        btnRemoveRow.setLayoutParams(params);
        btnRemoveRow.setImageResource(android.R.drawable.ic_delete);
        btnRemoveRow.setTag("btnRemoveRow");
        btnRemoveRow.setOnClickListener(this::btnRemove_Click);
        return btnRemoveRow;
    }

    private void btnRemove_Click(View view)
    {
        TableRow row = ((TableRow)view.getParent());
        TableLayout table = (TableLayout) row.getParent();
        table.removeView(row);
    }
}