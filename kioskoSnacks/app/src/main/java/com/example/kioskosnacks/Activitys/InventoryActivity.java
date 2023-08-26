package com.example.kioskosnacks.Activitys;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.kioskosnacks.Alertas.AlertMessage;
import com.example.kioskosnacks.DataBase.ImageDB;
import com.example.kioskosnacks.DataBase.TokenDB;
import com.example.kioskosnacks.RecyclerViewUtils.Adapter.InventoryAdapter;
import com.example.kioskosnacks.RecyclerViewUtils.Extras.PaddingRecycleView;
import com.example.kioskosnacks.RecyclerViewUtils.Models.ProducsInventory;
import com.example.kioskosnacks.R;
import com.example.kioskosnacks.WebService.Models.Concept;
import com.example.kioskosnacks.WebService.Observers.ConceptObserver;
import com.example.kioskosnacks.WebService.Responses.ConceptResponse;
import com.example.kioskosnacks.WebService.Observers.InventoryObserver;
import com.example.kioskosnacks.WebService.Responses.InventoryResponse;
import com.example.kioskosnacks.WebService.Observers.UpdateConceptObserver;
import com.example.kioskosnacks.WebService.Responses.UpdateConceptResponse;
import com.example.kioskosnacks.WebService.ViewModels.ConceptViewModel;
import com.example.kioskosnacks.WebService.ViewModels.InventoryViewModel;

import java.io.File;
import java.util.ArrayList;

import timber.log.Timber;

public class InventoryActivity extends BaseActivity implements View.OnClickListener, InventoryAdapter.OnItemClickListener, InventoryObserver, UpdateConceptObserver {
    private String TAG = this.getClass().getName();
    private ArrayList<Concept> inventoryArrayList;
    private RecyclerView inventoryList;
    private InventoryAdapter adapter;
    private String textSearch = "";
    private InventoryViewModel inventoryViewModel;
    private ConceptViewModel conceptViewModel;
    private LottieAnimationView loading;
    private View layoutLoading;
    private Dialog infoDialog;
    private AlertMessage alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        layoutLoading = findViewById(R.id.layout_loading);
        loading = findViewById(R.id.loading);

        FrameLayout backActivity = findViewById(R.id.backActivity);
        backActivity.setOnClickListener(this);

        TextView titulo = findViewById(R.id.titulo);
        titulo.setText("Inventory");

        Button btnAddProduct = findViewById(R.id.btnAddProduct);
        btnAddProduct.setOnClickListener(this);

        Button btnSupply = findViewById(R.id.btnSupplyInventory);
        btnSupply.setOnClickListener(this);

        FrameLayout dialogSearch = findViewById(R.id.search);
        dialogSearch.setOnClickListener(this);

        inventoryList = findViewById(R.id.cardInventory);
        inventoryList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        int leftSpacing = getResources().getDimensionPixelSize(R.dimen.recycleLeft);
        int rightSpacing = getResources().getDimensionPixelSize(R.dimen.recycleRight);
        int topSpacing = getResources().getDimensionPixelSize(R.dimen.recycleTop);
        int bottomSpacing = getResources().getDimensionPixelSize(R.dimen.recycleBottom);
        PaddingRecycleView itemDecoration = new PaddingRecycleView(leftSpacing, rightSpacing, topSpacing, bottomSpacing);
        inventoryList.addItemDecoration(itemDecoration);


        inventoryArrayList = new ArrayList<>();

        inventoryViewModel = new InventoryViewModel(this);
        inventoryViewModel.addObserver(this);

        conceptViewModel = new ConceptViewModel(this);
        conceptViewModel.addObserverUpdate(this);

        alert = new AlertMessage(this);
        alert.setListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.backActivity:
                backActivity();
                break;
            case R.id.search:
                dialogSearch();
                break;
            case R.id.btnAddProduct:
                addProductActivity();
                break;
            case R.id.alertConfirm:
                alert.alertDismiss();
                break;
            case R.id.btnSupplyInventory:
                supplyActivity();
        }
    }

    private void supplyActivity() {
        Intent intent = new Intent(this, SupplyActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void addProductActivity() {
        Intent intent = new Intent(this, AddProductActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void dialogSearch() {
        Dialog search = new Dialog(this);
        search.requestWindowFeature(Window.FEATURE_NO_TITLE);
        search.setCancelable(false);
        search.setContentView(R.layout.layout_search);

        search.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));

        EditText searchEditText = search.findViewById(R.id.productSearch);
        searchEditText.setText(textSearch);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String query = editable.toString().trim();
                adapter.filter(query);
            }
        });

        Button btnCancel = search.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textSearch = "";
                adapter.filter("");
                search.dismiss();
            }
        });

        Button btnSearch = search.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textSearch = searchEditText.getText().toString();
                search.dismiss();
            }
        });
        search.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        search.show();
    }

    private void backActivity() {
        this.finish();
    }

    @Override
    public void onClickListener(Concept producsInventory) {
        infoDialog = new Dialog(this);
        infoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        infoDialog.setContentView(R.layout.layout_scan_product);

        infoDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        ImageView close = infoDialog.findViewById(R.id.clouse);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infoDialog.dismiss();
            }
        });
        ImageView imageProduct = infoDialog.findViewById(R.id.imageInventory);
        File internalStorageDir = getFilesDir();
        File imageFile = new File(internalStorageDir, producsInventory.getId() + ".jpg");

        if (imageFile.exists()) {
            // Mostrar la imagen en el ImageView
            imageProduct.setImageURI(Uri.fromFile(imageFile));
        } else {
            imageProduct.setImageResource(R.drawable.not_found);
        }
        TextView name = infoDialog.findViewById(R.id.nombreSelect);
        name.setText(producsInventory.getName());

        Button btnDelete = infoDialog.findViewById(R.id.deleteInventory);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDelete(producsInventory);
            }
        });

        Button btnEdit = infoDialog.findViewById(R.id.cancelInventory);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), EditProductActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("Concept", producsInventory);
                startActivity(intent);
                infoDialog.dismiss();
            }
        });
        infoDialog.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        infoDialog.show();
    }

    private void confirmDelete(Concept concept) {
        Dialog confirm = new Dialog(this);
        confirm.requestWindowFeature(Window.FEATURE_NO_TITLE);
        confirm.setCancelable(false);
        confirm.setContentView(R.layout.alert_message);

        confirm.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        LottieAnimationView icon = confirm.findViewById(R.id.alertWarning);
        icon.setAnimation(R.raw.warning_red);
        icon.loop(true);
        icon.playAnimation();
        TextView title = confirm.findViewById(R.id.alertTitle);
        title.setText("Are you sure to delete the product?");

        Button btnCancelDelete = confirm.findViewById(R.id.alertCancel);
        btnCancelDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirm.dismiss();
            }
        });

        Button btnDelete = confirm.findViewById(R.id.alertConfirm);
        btnDelete.setText("Delete");
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infoDialog.dismiss();
                confirm.dismiss();
                startLoading();
                concept.setStatus(0);
                conceptViewModel.updateConcept(concept);
            }
        });
        confirm.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        confirm.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLoading();
        reloadInventory();
    }

    private void reloadInventory() {
        inventoryArrayList.clear();
        String internalStoragePath = getFilesDir().getPath();
        adapter = new InventoryAdapter(inventoryArrayList, internalStoragePath);
        adapter.setOnItemClickListener(this);
        inventoryList.setAdapter(adapter);

        inventoryViewModel.getInventory();
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
    public void onSuccessInventory(InventoryResponse succesResponse) {
        stopLoading();
        try {
            for (Concept item : succesResponse.getConcepts()) {
                //String log = String.format("%s [%s]: %s", TAG, "array de concepts", item.toString());
                //Timber.i(log.toString());
                if (!item.getName().equals("admin")) {
                    inventoryArrayList.add(item);
                }
            }
            String internalStoragePath = getFilesDir().getPath();
            adapter = new InventoryAdapter(inventoryArrayList, internalStoragePath);
            adapter.setOnItemClickListener(this);
            inventoryList.setAdapter(adapter);
        } catch (Exception e) {
            String log = String.format("%s [%s]: %s", TAG, "onSuccessInventory", succesResponse.getConcepts().toString());
            Timber.i(log.toString());
        }
    }

    @Override
    public void onErrorInventory(InventoryResponse errorResponse) {
        stopLoading();
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

    @Override
    public void onSuccessUpdate(UpdateConceptResponse succesResponse) {
        stopLoading();
        alert.setTitle("Product delete successful");
        alert.disableCancel(true);
        alert.setAnimation(R.raw.save);
        alert.showAlert();

        reloadInventory();
    }

    @Override
    public void onErrorUpdate(UpdateConceptResponse errorResponse) {
        stopLoading();
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