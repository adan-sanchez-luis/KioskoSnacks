package com.example.kioskosnacks.Activitys;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
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

import com.airbnb.lottie.LottieAnimationView;
import com.example.kioskosnacks.Alertas.AlertMessage;
import com.example.kioskosnacks.R;
import com.example.kioskosnacks.Validator.InputValidator;
import com.example.kioskosnacks.WebService.Models.Concept;
import com.example.kioskosnacks.WebService.Observers.UpdateConceptObserver;
import com.example.kioskosnacks.WebService.Responses.UpdateConceptResponse;
import com.example.kioskosnacks.WebService.ViewModels.ConceptViewModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import timber.log.Timber;

public class EditProductActivity extends BaseActivity implements View.OnClickListener, UpdateConceptObserver {

    private LottieAnimationView loading;
    private View layoutLoading;
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
    private CircularProgressButton btnSave;
    private Concept concept;
    private String TAG = "EditProductActivity";
    private boolean updateSuccess = false;
    private ActivityResultLauncher<String> pickMediaLauncher;
    private Uri path = null;
    private boolean withIMG = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        try {
            layoutLoading = findViewById(R.id.layout_loading);
            loading = findViewById(R.id.loading);
            startLoading();

            FrameLayout back = findViewById(R.id.backActivity);
            back.setOnClickListener(this);

            TextView titulo = findViewById(R.id.titulo);
            titulo.setText("Edit product");

            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();

            concept = (Concept) bundle.get("Concept");

            tvCode = findViewById(R.id.productCode);
            tvName = findViewById(R.id.productName);
            tvDescription = findViewById(R.id.description);
            tvStock = findViewById(R.id.quantity);
            spinnerUnits = findViewById(R.id.units);
            tvPrice = findViewById(R.id.price);
            spinnerGroup = findViewById(R.id.group);
            tvNotes = findViewById(R.id.notes);
            img = findViewById(R.id.imgForm);

            tvCode.setFocusable(false);
            tvCode.setEnabled(false);
            tvCode.setCursorVisible(false);
            tvCode.setKeyListener(null);
            tvCode.setBackgroundColor(Color.TRANSPARENT);

            focus(tvName);
            focus(tvDescription);
            focus(tvStock);
            focus(tvPrice);
            focus(tvNotes);

            InputValidator.setNumericInputWithDecimalLimit(tvPrice, 2); // Establece la validación para números con 2 decimales
            InputValidator.limitEditTextLength(tvStock, 5);

            conceptViewModel = new ConceptViewModel(this);
            conceptViewModel.addObserverUpdate(this);

            alert = new AlertMessage(this);
            alert.setListener(this);

            btnSave = findViewById(R.id.sendDataProduc);
            btnSave.setOnClickListener(this);


            String[] dataGroup = getResources().getStringArray(R.array.groupConcept);
            configSpinner(spinnerGroup, dataGroup);
            String[] dataUnits = getResources().getStringArray(R.array.unitsConcept);
            configSpinner(spinnerUnits, dataUnits);

            loadConcetp();
            stopLoading();


            Button btnImage = findViewById(R.id.btnImage);
            btnImage.setOnClickListener(v -> pickImage());

            // Inicializar el lanzador para la selección de medios
            pickMediaLauncher = registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    result -> {
                        if (result != null) {
                            path = result;
                            withIMG = false;
                            // Hacer algo con la URI del medio seleccionado, como mostrarlo en un ImageView
                            img.setImageURI(path);
                        }
                    });

        } catch (Exception e) {
            String l = String.format("%s [%s]: %s", TAG, "onCreate", "Product in cart:" + concept + ", error: " + e.getMessage());
            Timber.i(l.toString());
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
                textView.setTextSize(40);
                return view;
            }
        };

        spinner.setAdapter(adapter);
    }

    private void loadConcetp() {
        tvCode.setText(concept.getId());
        tvName.setText(concept.getName());
        tvDescription.setText(concept.getDescription());
        tvStock.setText(concept.getQuantity() + "");
        int units = (concept.getUnit() - 6) < 1 ? 0 : concept.getUnit() - 6;
        spinnerUnits.setSelection(units);
        tvPrice.setText(concept.getPrice() + "");
        int group = concept.getGroup();
        group = group < 3 ? group : group - 1;
        spinnerGroup.setSelection(group);
        tvNotes.setText(concept.getNotes());

        File internalStorageDir = getFilesDir();
        File imageFile = new File(internalStorageDir, concept.getId() + ".jpg");

        if (imageFile.exists()) {
            // Mostrar la imagen en el ImageView
            img.setImageURI(Uri.fromFile(imageFile));
            withIMG = true;
        } else {
            img.setImageResource(R.drawable.not_found);
        }
    }

    public void pickImage() {
        pickMediaLauncher.launch("image/*"); // Lanzar el selector de medios para imágenes
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backActivity:
                this.finish();
                break;
            case R.id.sendDataProduc:
                updateProduct();
                break;
            case R.id.alertConfirm:
                closeAlert();
                break;
        }
    }

    private void closeAlert() {
        alert.alertDismiss();
        if (updateSuccess)
            this.finish();
    }

    private void updateProduct() {
        if (InputValidator.isEmpty(tvCode) || InputValidator.isEmpty(tvName) || InputValidator.isEmpty(tvDescription) || InputValidator.isEmpty(tvStock)
                || InputValidator.isEmpty(tvPrice) || spinnerGroup.getSelectedItemPosition() == 0 || spinnerUnits.getSelectedItemPosition() == 0) {
            alert.setTitle("Please fill in the empty fields");
            alert.setAnimation(R.raw.warning_orange);
            alert.setLoop(true);
            alert.disableCancel(true);
            alert.showAlert();
            return;
        }


        if (withIMG) {
            save();
        } else {
            if (path != null) {
                save();
                String code = concept.getId();
                saveIMG(code + ".jpg");
            } else {
                dialogConfirm();
            }
        }

        cancelBtn();
    }

    private void save() {
        String name = tvName.getText().toString();
        String description = tvDescription.getText().toString();
        String quantity = tvStock.getText().toString();
        int unit = spinnerUnits.getSelectedItemPosition() + 6;
        String price = tvPrice.getText().toString();
        int group = spinnerGroup.getSelectedItemPosition();
        group = group < 3 ? group : group + 1;
        String notes = tvNotes.getText().toString();

        concept.setName(name);
        concept.setDescription(description);
        concept.setQuantity(Integer.parseInt(quantity));
        concept.setUnit(unit);
        concept.setPrice(Double.parseDouble(price));
        concept.setGroup(group);
        concept.setNotes(notes);
        conceptViewModel.updateConcept(concept);
    }

    private void saveIMG(String name) {
        File internalStorageDir = getFilesDir(); // Obtener la ruta del almacenamiento interno de la aplicación
        File destinationFile = new File(internalStorageDir, name);

        // Eliminar la imagen anterior si existe
        System.out.println("nombre de img: " + name);
        if (destinationFile.exists()) {
            System.out.println("eliminando img: " + destinationFile.delete());
        }
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
                activeBtn();
            }
        });
        peticion.show();
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

    private void cancelBtn() {
        btnSave.startAnimation();
        btnSave.setEnabled(false);
    }

    private void activeBtn() {
        btnSave.revertAnimation();
        btnSave.setEnabled(true);
    }

    private void startLoading() {
        layoutLoading.setVisibility(View.VISIBLE);
        loading.playAnimation();
    }

    private void stopLoading() {
        loading.cancelAnimation();
        layoutLoading.setVisibility(View.GONE);
    }

    @Override
    public void onSuccessUpdate(UpdateConceptResponse succesResponse) {
        updateSuccess = true;
        alert.setTitle("Updated product");
        alert.setAnimation(R.raw.save);
        alert.setLoop(false);
        alert.disableCancel(true);
        alert.showAlert();
        activeBtn();
    }

    @Override
    public void onErrorUpdate(UpdateConceptResponse errorResponse) {
        activeBtn();
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