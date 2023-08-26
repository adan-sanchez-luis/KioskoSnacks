package com.example.kioskosnacks.Activitys;

import static androidx.activity.result.contract.ActivityResultContracts.*;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.example.kioskosnacks.Alertas.AlertMessage;
import com.example.kioskosnacks.R;
import com.example.kioskosnacks.Validator.InputValidator;
import com.example.kioskosnacks.WebService.Models.Concept;
import com.example.kioskosnacks.WebService.Observers.SaveConceptObserver;
import com.example.kioskosnacks.WebService.Responses.SaveConceptResponse;
import com.example.kioskosnacks.WebService.ViewModels.ConceptViewModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class AddProductActivity extends BaseActivity implements View.OnClickListener, SaveConceptObserver {

    private ConceptViewModel conceptViewModel;
    private EditText tvCode;
    private EditText tvName;
    private EditText tvDescription;
    private EditText tvStock;
    private Spinner spinnerUnits;
    private EditText tvPrice;
    private Spinner spinnerGroup;
    private EditText tvNotes;
    private ImageView img;
    private AlertMessage alert;
    private CircularProgressButton send;
    private boolean save = false;
    private Uri path = null;
    private ActivityResultLauncher<String> pickMediaLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        FrameLayout back = findViewById(R.id.backActivity);
        back.setOnClickListener(this);

        TextView titulo = findViewById(R.id.titulo);
        titulo.setText("Add product");

        tvCode = findViewById(R.id.productCode);
        tvName = findViewById(R.id.productName);
        tvDescription = findViewById(R.id.description);
        tvStock = findViewById(R.id.quantity);
        spinnerUnits = findViewById(R.id.units);
        tvPrice = findViewById(R.id.price);
        spinnerGroup = findViewById(R.id.group);
        tvNotes = findViewById(R.id.notes);
        img = findViewById(R.id.imgForm);

        focus(tvCode);
        focus(tvName);
        focus(tvDescription);
        focus(tvStock);
        focus(tvPrice);
        focus(tvNotes);

        InputValidator.setNumericInputWithDecimalLimit(tvPrice, 2); // Establece la validación para números con 2 decimales
        InputValidator.limitEditTextLength(tvStock,5);
        conceptViewModel = new ConceptViewModel(this);
        conceptViewModel.addObserverSave(this);

        alert = new AlertMessage(this);
        alert.setListener(this);

        send = findViewById(R.id.sendDataProduc);
        send.setOnClickListener(this);

        String[] dataGroup = getResources().getStringArray(R.array.groupConcept);
        configSpinner(spinnerGroup, dataGroup);
        String[] dataUnits = getResources().getStringArray(R.array.unitsConcept);
        configSpinner(spinnerUnits, dataUnits);

        img = findViewById(R.id.imgForm);
        // Inicializar el lanzador para la selección de medios
        pickMediaLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                result -> {
                    if (result != null) {
                        path = result;
                        // Hacer algo con la URI del medio seleccionado, como mostrarlo en un ImageView
                        img.setImageURI(path);
                    }
                });

        Button btnImage = findViewById(R.id.btnImage);
        btnImage.setOnClickListener(v -> pickImage());
    }

    private void focus(EditText editText) {
        editText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //do here your stuff f
                    ocultarTeclado();
                    return true;
                }
                return false;
            }
        });
    }

    private void ocultarTeclado() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backActivity:
                this.finish();
                break;
            case R.id.sendDataProduc:
                saveProduct();
                break;
            case R.id.alertConfirm:
                closeAlert();
                break;
        }
    }

    private void configSpinner(final Spinner spinner, String[] data) {
        // Crea un ArrayAdapter personalizado
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                R.layout.spinner_form, // Utiliza el diseño personalizado
                data
        ) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextSize(40);
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextSize(40); // Tamaño de letra en 50sp
                return view;
            }
        };

        spinner.setAdapter(adapter);
    }


    public void pickImage() {
        pickMediaLauncher.launch("image/*"); // Lanzar el selector de medios para imágenes
    }

    private void closeAlert() {
        alert.alertDismiss();
        if (save) {
            this.finish();
        }
    }

    private void saveProduct() {
        if (InputValidator.isEmpty(tvName) || InputValidator.isEmpty(tvDescription) || spinnerUnits.getSelectedItemPosition() == 0
                || InputValidator.isEmpty(tvStock) || InputValidator.isEmpty(tvPrice) || spinnerGroup.getSelectedItemPosition() == 0
                || tvCode.getText().toString().length() != 13) {
            alert.setTitle("Please fill in the empty fields");
            alert.setAnimation(R.raw.warning_orange);
            alert.setLoop(true);
            alert.disableCancel(true);
            alert.showAlert();
            return;
        }

        if (path != null) {
            save();
            String code = tvCode.getText().toString();
            saveIMG(code + ".jpg");
        } else {
            dialogConfirm();
        }
    }

    private void dialogConfirm() {
        Dialog peticion = new Dialog(this);
        peticion.requestWindowFeature(Window.FEATURE_NO_TITLE);
        peticion.setCancelable(false);
        peticion.setContentView(R.layout.alert_message);
        LottieAnimationView icon = peticion.findViewById(R.id.alertWarning);
        icon.setAnimation(R.raw.warning_orange);

        TextView title = peticion.findViewById(R.id.alertTitle);
        title.setText("Product has no image, continue anyway?");

        Button btnAccept = peticion.findViewById(R.id.alertConfirm);
        Button btnCancel = peticion.findViewById(R.id.alertCancel);

        peticion.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                peticion.dismiss();
                save();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                peticion.dismiss();
            }
        });
        peticion.show();
    }

    private void save() {
        String code = tvCode.getText().toString();
        String name = tvName.getText().toString();
        String description = tvDescription.getText().toString();
        String stock = tvStock.getText().toString();
        int unit = spinnerUnits.getSelectedItemPosition() + 6;
        String price = tvPrice.getText().toString();
        int group = spinnerGroup.getSelectedItemPosition();
        String notes = tvNotes.getText().toString();

        group = group < 3 ? group : group + 1;
        Concept concept = new Concept();
        concept.setId(code);
        concept.setName(name);
        concept.setDescription(description);
        concept.setQuantity(Integer.parseInt(stock));
        concept.setUnit(unit);
        concept.setPrice(Double.parseDouble(price));
        concept.setTag(0);
        concept.setGroup(group);
        concept.setNotes(notes);
        concept.setAcountType(2);
        concept.setStatus(1);
        conceptViewModel.saveConcept(concept);
    }


    private void saveIMG(String name) {
        File internalStorageDir = getFilesDir(); // Obtener la ruta del almacenamiento interno de la aplicación
        File destinationFile = new File(internalStorageDir, name);

        try {
            FileInputStream inputStream = (FileInputStream) getContentResolver().openInputStream(path);
            FileOutputStream outputStream = new FileOutputStream(destinationFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSuccessSaveConcept(SaveConceptResponse succesResponse) {
        //crear alerta y cerrar
        save = true;
        alert.setTitle("Product save");
        alert.setAnimation(R.raw.save);
        alert.setLoop(false);
        alert.disableCancel(true);
        alert.showAlert();
    }

    @Override
    public void onErrorSaveConcept(SaveConceptResponse errorResponse) {
        save = false;
        switch (errorResponse.getType()) {
            case ERROR:
                alert.setAnimation(R.raw.warning_orange);
                alert.setTitle(getBaseContext().getString(R.string.error));
                break;
            case NULL_JSON:
                alert.setAnimation(R.raw.warning_red);
                alert.setTitle(getBaseContext().getString(R.string.unknown));
                break;
            case REQUEST_ERROR:
                alert.setAnimation(R.raw.no_internet);
                alert.setTitle(getBaseContext().getString(R.string.noConnexion));
                break;
        }
        alert.setLoop(true);
        alert.disableCancel(true);
        alert.showAlert();
    }
}