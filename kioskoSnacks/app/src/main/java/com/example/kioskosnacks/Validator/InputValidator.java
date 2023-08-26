package com.example.kioskosnacks.Validator;

import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

public class InputValidator {
    public static void setNumericInputWithDecimalLimit(EditText editText, int decimalLimit) {
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        InputFilter decimalDigitsFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                String regex = "\\d+(\\.\\d{0,2})?"; // Patrón regex para aceptar números enteros y números decimales con hasta 2 decimales
                StringBuilder newTextBuilder = new StringBuilder(dest);
                newTextBuilder.replace(dstart, dend, source.subSequence(start, end).toString());
                String newText = newTextBuilder.toString();
                if (newText.matches(regex)) {
                    return null; // Acepta el texto ingresado
                } else {
                    return ""; // Ignora el texto ingresado
                }
            }
        };

        editText.setFilters(new InputFilter[]{decimalDigitsFilter});
    }

    public static boolean isEmpty(EditText editText) {
        String text = editText.getText().toString().trim();
        if (text.isEmpty())
            return true;

        return false;
    }

    public static void limitEditTextLength(final EditText editText, final int maxLength) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > maxLength) {
                    String originalText = editable.toString();
                    String trimmedText = originalText.substring(0, maxLength);
                    editText.setText(trimmedText);
                    editText.setSelection(trimmedText.length());
                }
            }
        });
    }
}
