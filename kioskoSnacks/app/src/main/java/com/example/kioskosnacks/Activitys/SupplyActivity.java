package com.example.kioskosnacks.Activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.kioskosnacks.Alertas.AlertMessage;
import com.example.kioskosnacks.R;
import com.example.kioskosnacks.RecyclerViewUtils.Adapter.CardShopAdapter;
import com.example.kioskosnacks.RecyclerViewUtils.Extras.DeleteAnimation;
import com.example.kioskosnacks.RecyclerViewUtils.Extras.PaddingRecycleView;
import com.example.kioskosnacks.WebService.Models.Concept;
import com.example.kioskosnacks.WebService.Observers.ConceptObserver;
import com.example.kioskosnacks.WebService.Observers.SupplyObserver;
import com.example.kioskosnacks.WebService.Responses.ConceptResponse;
import com.example.kioskosnacks.WebService.Responses.SupplyResponse;
import com.example.kioskosnacks.WebService.ViewModels.ConceptViewModel;
import com.example.kioskosnacks.WebService.ViewModels.SupplyViewModel;

import java.util.ArrayList;

import timber.log.Timber;

public class SupplyActivity extends BaseActivity implements View.OnClickListener, ConceptObserver, SupplyObserver {
    private RecyclerView inventorySupply;
    private ArrayList<Concept> supplyArrayList;
    private CardShopAdapter adapter;
    private LinearLayoutManager manager;
    private LottieAnimationView loading;
    private View layoutLoading;
    private boolean onlyOne = false;
    private ConceptViewModel conceptViewModel;
    private SupplyViewModel supplyViewModel;
    private AlertMessage alert;
    private EditText scanCode;
    private String TAG = "SupplyActivity";
    private boolean save = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supply);

        FrameLayout back = findViewById(R.id.backActivity);
        back.setOnClickListener(this);

        TextView titulo = findViewById(R.id.titulo);
        titulo.setText("Supply product");

        scanCode = findViewById(R.id.codigo);
        layoutLoading = findViewById(R.id.layout_loading);
        loading = findViewById(R.id.loading);

        alert = new AlertMessage(this);
        alert.setListener(this);

        inventorySupply = findViewById(R.id.inventorySupply);
        manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        inventorySupply.setLayoutManager(manager);
        int leftSpacing = getResources().getDimensionPixelSize(R.dimen.recycleLeft);
        int rightSpacing = getResources().getDimensionPixelSize(R.dimen.recycleRight);
        int topSpacing = getResources().getDimensionPixelSize(R.dimen.recycleTop);
        int bottomSpacing = getResources().getDimensionPixelSize(R.dimen.recycleBottom);
        PaddingRecycleView itemDecoration = new PaddingRecycleView(leftSpacing, rightSpacing, topSpacing, bottomSpacing);
        inventorySupply.addItemDecoration(itemDecoration);
        supplyArrayList = new ArrayList<>();

        Button btnSupply = findViewById(R.id.btnSupply);
        btnSupply.setOnClickListener(this);

        conceptViewModel = new ConceptViewModel(this);
        conceptViewModel.addObserverGet(this);

        supplyViewModel = new SupplyViewModel(this);
        supplyViewModel.addObserver(this);

        Button btnBuscar = findViewById(R.id.btnBuscar);
        btnBuscar.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backActivity:
                this.finish();
                break;
            case R.id.alertConfirm:
                aceptAlert();
                break;
            case R.id.alertCancel:
                alert.alertDismiss();
                break;
            case R.id.btnSupply:
                supply();
                break;
            case R.id.btnBuscar:
                buscar();
                break;
        }
    }

    private String textSearch = "";

    private void buscar() {

        Dialog search = new Dialog(this);
        search.requestWindowFeature(Window.FEATURE_NO_TITLE);
        search.setCancelable(false);
        search.setContentView(R.layout.layout_search);

        search.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));

        EditText searchEditText = search.findViewById(R.id.productSearch);
        searchEditText.setText(textSearch);

        Button btnCancel = search.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textSearch = "";
                search.dismiss();
            }
        });

        Button btnSearch = search.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textSearch = searchEditText.getText().toString();
                startLoading();
                conceptViewModel.getConcept(textSearch);
                textSearch = "";
                search.dismiss();
            }
        });
        search.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        search.show();
    }

    private void supply() {
        if (supplyArrayList.isEmpty()){
            alert.setTitle("Scan product first");
            alert.setAnimation(R.raw.warning);
            alert.disableCancel(true);
            alert.showAlert();
            return;
        }
        supplyViewModel.addSuply(supplyArrayList);
        for (Concept item : supplyArrayList) {
            item.setQuantity(item.getQuantity() + item.getInCart());
            conceptViewModel.updateConcept(item);
        }
    }

    private void aceptAlert() {
        alert.alertDismiss();
        if (save)
            this.finish();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (onlyOne)
            return super.dispatchKeyEvent(event);

        StringBuilder sb = new StringBuilder();
        Handler myHandler = new Handler();
        int action = event.getAction();
        switch (action) {
            case KeyEvent.ACTION_DOWN:
                int unicodeChar = event.getUnicodeChar();
                sb.append((char) unicodeChar);
                if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
                    return super.dispatchKeyEvent(event);
                }
                if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
                    return super.dispatchKeyEvent(event);
                }
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    return super.dispatchKeyEvent(event);
                }
                if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
                    return super.dispatchKeyEvent(event);
                }
                if (event.getKeyCode() == KeyEvent.KEYCODE_HOME) {
                    return super.dispatchKeyEvent(event);
                }
                if (event.getKeyCode() == KeyEvent.KEYCODE_POWER) {
                    return super.dispatchKeyEvent(event);
                }
                final int len = sb.length();
                myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (len != sb.length()) return;
                        if (sb.length() > 0) {

                            //System.out.println("Buscando datos" + sb +", longitud" + sb.length());
                            String filteredText = sb.toString().replaceAll("[^a-zA-Z0-9]", "");
                            String scannedText = filteredText;

                            //System.out.println("Caracter pulsado: " + scannedText);
                            scanCode.setText(scanCode.getText() + scannedText);
                            if (scanCode.getText().length() == 13 && !onlyOne) {
                                String l = String.format("%s [%s]: %s", TAG, "Scanner", ("scan bar code: " + scanCode.getText().length()));
                                Timber.i(l.toString());
                                onlyOne = true;
                                startLoading();
                                conceptViewModel.getConcept(scanCode.getText().toString());
                            }
                            sb.setLength(0);
                        }
                    }
                }, 200);
                return true;
            default:
                break;
        }
        return super.dispatchKeyEvent(event);
    }

    private void startLoading() {
        layoutLoading.setVisibility(View.VISIBLE);
        loading.playAnimation();
    }

    private void stopLoading() {
        onlyOne = false;
        scanCode.setText("");
        loading.cancelAnimation();
        layoutLoading.setVisibility(View.GONE);
    }

    @Override
    public void onSuccessConcept(ConceptResponse succesResponse) {
        stopLoading();

        String name = succesResponse.getConcept().getName();
        if (name.equals("admin")) {
            alert.setTitle("This is not product, " + name);
            alert.setAnimation(R.raw.warning_red);
            alert.setLoop(true);
            alert.disableCancel(true);
            alert.showAlert();
            return;
        }

        boolean producInCart = false;
        for (Concept item : supplyArrayList) {
            if (succesResponse.getConcept().getId().equals(item.getId())) {
                producInCart = true;
                item.setInCart(item.getInCart() + 1);

                String internalStoragePath = getFilesDir().getPath();
                adapter = new CardShopAdapter(supplyArrayList, internalStoragePath,true);
                inventorySupply.setAdapter(adapter);
                //String l = String.format("%s [%s]: %s", TAG, "OnSuccessConcept", "Product in cart:" + item.getName());
                //Timber.i(l.toString());
                break;
            }
        }
        if (!producInCart) {
            succesResponse.getConcept().setInCart(1);
            //String l = String.format("%s [%s]: %s", TAG, "OnSuccessConcept", succesResponse.getConcept());
            //Timber.i(l.toString());

            supplyArrayList.add(succesResponse.getConcept());

            String internalStoragePath = getFilesDir().getPath();
            adapter = new CardShopAdapter(supplyArrayList, internalStoragePath,true);
            inventorySupply.setAdapter(adapter);
            inventorySupply.setItemAnimator(new DeleteAnimation());
        }
    }

    @Override
    public void onErrorConcept(ConceptResponse errorResponse) {
        stopLoading();
        Toast.makeText(this, errorResponse.getMessage(), Toast.LENGTH_LONG).show();
        switch (errorResponse.getType()) {
            case NOTFOUND:
                //String l = String.format("%s [%s]: %s", TAG, "OnErrorConcept", ("not found: " + errorResponse.getConcept().getId()));
                //Timber.i(l.toString());
                alert.setTitle("Product not found code: " + errorResponse.getConcept().getId());
                alert.setAnimation(R.raw.warning);
                alert.setLoop(false);
                break;
            case ERROR:
                alert.setAnimation(R.raw.warning_orange);
                alert.setTitle(getBaseContext().getString(R.string.error));
                alert.setLoop(true);
                break;
            case NULL_JSON:
                alert.setAnimation(R.raw.warning_red);
                alert.setTitle(getBaseContext().getString(R.string.unknown));
                alert.setLoop(true);
                break;
            case REQUEST_ERROR:
                alert.setAnimation(R.raw.no_internet);
                alert.setTitle(getBaseContext().getString(R.string.noConnexion));
                alert.setLoop(true);
                break;
        }
        alert.setAceptText("Accept");
        alert.disableCancel(true);
        alert.showAlert();
    }

    @Override
    public void onSuccessSuply(SupplyResponse succesResponse) {
        save = true;
        alert.setTitle("Supply successful");
        alert.setAnimation(R.raw.save);
        alert.disableCancel(true);
        alert.showAlert();
    }

    @Override
    public void onErrorSupply(SupplyResponse errorResponse) {
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