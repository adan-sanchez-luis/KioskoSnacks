package com.example.kioskosnacks;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.kioskosnacks.Activitys.BaseActivity;
import com.example.kioskosnacks.Activitys.InactiveActivity;
import com.example.kioskosnacks.Activitys.LoginActivity;
import com.example.kioskosnacks.Activitys.PayActivity;
import com.example.kioskosnacks.Alertas.AlertInactive;
import com.example.kioskosnacks.Alertas.AlertMessage;
import com.example.kioskosnacks.Log.FileLogTree;
import com.example.kioskosnacks.Marshall.Marshall2;
import com.example.kioskosnacks.RecyclerViewUtils.Adapter.CardShopAdapter;
import com.example.kioskosnacks.RecyclerViewUtils.Extras.DeleteAnimation;
import com.example.kioskosnacks.RecyclerViewUtils.Extras.PaddingRecycleView;
import com.example.kioskosnacks.RecyclerViewUtils.Models.ProducsCardShop;
import com.example.kioskosnacks.WebService.Models.Concept;
import com.example.kioskosnacks.WebService.Observers.ConceptObserver;
import com.example.kioskosnacks.WebService.Responses.ConceptResponse;
import com.example.kioskosnacks.WebService.ViewModels.ConceptViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import timber.log.Timber;

public class MainActivity extends BaseActivity implements View.OnClickListener, ConceptObserver {
    private RecyclerView cardShop;
    private ArrayList<Concept> cardShopArrayList;
    private CardShopAdapter adapter;
    int i = 1;
    private Handler handler;
    private LinearLayoutManager manager;
    private static TextView tvTotal;
    private AlertMessage alert;

    private Marshall2 cashless;
    private Thread threadReconnection;
    private Thread infinito;
    private boolean startThread = false;
    private ConceptViewModel conceptViewModel;
    private LottieAnimationView loading;
    private View layoutLoading;
    private boolean onlyOne = false;
    private boolean delete = false;
    private EditText scanCode;
    private String TAG = "MainActivity";
    private static final int PERMISSION_REQUEST_CODE = 123;
    private AlertInactive alertInactive;
    private Handler inactivityHandler = new Handler();
    private Runnable inactivityRunnable = new Runnable() {
        @Override
        public void run() {
            // Mostrar el cuadro de diálogo de confirmación
            showConfirmationDialog();
        }
    };
    private Handler autoShowInactivityHandler = new Handler();
    private Runnable autoShowInactivityRunnable = new Runnable() {
        @Override
        public void run() {
            activityInactive();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alert = new AlertMessage(this);
        alert.setListener(this);
        tvTotal = findViewById(R.id.totalCarshop);
        scanCode = findViewById(R.id.codigo);

        permisos();
        configLog();
        FrameLayout clearAll = findViewById(R.id.clearAll);
        clearAll.setOnClickListener(this);

        cardShop = findViewById(R.id.cardShop);
        manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        cardShop.setLayoutManager(manager);

        int leftSpacing = getResources().getDimensionPixelSize(R.dimen.recycleLeft);
        int rightSpacing = getResources().getDimensionPixelSize(R.dimen.recycleRight);
        int topSpacing = getResources().getDimensionPixelSize(R.dimen.recycleTop);
        int bottomSpacing = getResources().getDimensionPixelSize(R.dimen.recycleBottom);
        PaddingRecycleView itemDecoration = new PaddingRecycleView(leftSpacing, rightSpacing, topSpacing, bottomSpacing);
        cardShop.addItemDecoration(itemDecoration);
        cardShopArrayList = new ArrayList<>();

        Button btnPay = findViewById(R.id.btnPay);
        btnPay.setOnClickListener(this);

        Button btnScanner = findViewById(R.id.btnScanner);
        btnScanner.setOnClickListener(this);

        Button btnInventory = findViewById(R.id.btnLogin);
        btnInventory.setOnClickListener(this);

        cashless = new Marshall2();

        cashless.startObserver();/*
        if (!startThread) {
            Reconnection reconnection = new Reconnection();
            infinito = new Thread(reconnection);
            infinito.start();
            startThread = true;
        }*/
        cashless.getObserver().observe(this, response -> {
            if (response == null)
                return;
            switch (response) {
                case VendApproved:
                    activityInactive();
                    break;
                case VendDenied:
                    break;
            }
        });

        conceptViewModel = new ConceptViewModel(this);
        conceptViewModel.addObserverGet(this);

        layoutLoading = findViewById(R.id.layout_loading);
        loading = findViewById(R.id.loading);

        startInactivityTimer();
        alertInactive = new AlertInactive(this);
        alertInactive.setListener(this);
    }

    private int timeInactive = 45000;
    private int timeAutoInactive = 60000;

    private void startInactivityTimer() {
        inactivityHandler.postDelayed(inactivityRunnable, timeInactive); // 45 segundos
        autoShowInactivityHandler.postDelayed(autoShowInactivityRunnable, timeAutoInactive); // 60 segundos
    }

    private void resetInactivityTimer() {
        inactivityHandler.removeCallbacks(inactivityRunnable);
        inactivityHandler.postDelayed(inactivityRunnable, timeInactive);
        autoShowInactivityHandler.removeCallbacks(autoShowInactivityRunnable);
        autoShowInactivityHandler.postDelayed(autoShowInactivityRunnable, timeAutoInactive); // 15 segundos
    }

    private void showConfirmationDialog() {
        alertInactive.showAlert();
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetInactivityTimer(); // Reiniciar el temporizador cuando la actividad se retome
    }

    @Override
    protected void onPause() {
        super.onPause();
        inactivityHandler.removeCallbacksAndMessages(null); // Detener el temporizador al cambiar de actividad
        autoShowInactivityHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        // Resetea el temporizador de inactividad cuando el usuario interactúa con la pantalla
        resetInactivityTimer();
        return super.dispatchTouchEvent(event);
    }

    private void permisos() {
        // se solicita los permisos para escribir los Logs
        if (Build.VERSION.SDK_INT >= 30) {
            if (!Environment.isExternalStorageManager()) {
                Intent getpermission = new Intent();
                getpermission.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(getpermission);
            }
        }
        // Comprobar y solicitar permiso para acceder al almacenamiento externo si es necesario
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        }
    }

    private void configLog() {
        Calendar calendar = Calendar.getInstance();
        int yearNow = calendar.get(Calendar.YEAR);
        int monthNow = calendar.get(Calendar.MONTH) + 1;
        int dayNow = calendar.get(Calendar.DAY_OF_MONTH);

        File logDirectory = new File(Environment.getExternalStorageDirectory(), "Logs");
        if (!logDirectory.exists()) {
            boolean directoryCreated = logDirectory.mkdirs();
            if (!directoryCreated) {
                System.out.println("no se creo el dir");
                // No se pudo crear el directorio, manejar el error aquí
            }
        }

        File logFile = new File(logDirectory, dayNow + "-" + monthNow + "-" + yearNow + ".txt");
        // Configura Timber para enviar los registros a un archivo
        Timber.plant(new Timber.DebugTree()); // Para mostrar registros en Logcat durante el desarrollo
        Timber.plant(new FileLogTree(logFile)); // Para enviar registros a un archivo
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        String l = "";
        switch (id) {
            case R.id.clearAll:
                l = String.format("%s [%s]: %s", TAG, "button clear all", "press");
                Timber.i(l.toString());
                clearAll();
                break;
            case R.id.btnPay:
                l = String.format("%s [%s]: %s", TAG, "button pay", "press");
                Timber.i(l.toString());
                pay();
                break;
            case R.id.btnScanner:
                l = String.format("%s [%s]: %s", TAG, "button scanner", "press");
                Timber.i(l.toString());
                addProduct();
                break;
            case R.id.btnLogin:
                l = String.format("%s [%s]: %s", TAG, "button login", "press");
                Timber.i(l.toString());
                //activityLogin();
                buscar();
                break;
            case R.id.alertConfirm:
                l = String.format("%s [%s]: %s", TAG, "button alert Acept", "press");
                Timber.i(l.toString());
                aceptAlert();
                break;
            case R.id.alertCancel:
                l = String.format("%s [%s]: %s", TAG, "button alert cancel", "press");
                Timber.i(l.toString());
                delete = false;
                alert.alertDismiss();
                break;
            case R.id.inactiveYes:
                resetInactivityTimer();
                alertInactive.alertDismiss();
                break;
            case R.id.inactiveNo:
                activityInactive();
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
                search.dismiss();
                if (textSearch.equals("6782349103592")) {
                    activityLogin();
                } else {
                    startLoading();
                    conceptViewModel.getConcept(textSearch);
                }
                textSearch = "";
            }
        });
        search.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        search.show();
    }

    private void activityInactive() {
        alertInactive.alertDismiss();
        startDelete();
        Intent intent = new Intent(this, InactiveActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void aceptAlert() {
        alert.alertDismiss();

        if (delete) {
            startDelete();
            delete = false;
        }
    }

    private void startDelete() {
        handler = new Handler();
        startProductDeletionAnimation();
        i = 1;
        TextView tvTotal = findViewById(R.id.totalCarshop);
        tvTotal.setText("$0.00");
    }

    private void activityLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void addProduct() {
        String name = "nameeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee";
        String description = "description aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        String img = "image";
        int count = 1;
        int amount = i;
        Concept producsCardShop = new Concept();
        producsCardShop.setId("7501003339522");
        producsCardShop.setName(name);
        producsCardShop.setDescription(description);
        producsCardShop.setInCart(count);
        producsCardShop.setPrice(amount);
        producsCardShop.setImg(img);
        cardShopArrayList.add(producsCardShop);
        String internalStoragePath = getFilesDir().getPath();
        adapter = new CardShopAdapter(cardShopArrayList, internalStoragePath, false);
        cardShop.setAdapter(adapter);
        cardShop.setItemAnimator(new DeleteAnimation());
        i++;
        updateTotal(cardShopArrayList);
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
                                String code = scanCode.getText().toString();
                                if (code.equals("6782349103592")) {
                                    activityLogin();
                                } else {
                                    startLoading();
                                    conceptViewModel.getConcept(scanCode.getText().toString());
                                }
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

    private void pay() {
        if (cardShopArrayList.isEmpty()) {
            alert.setTitle("Scan product first");
            alert.setAnimation(R.raw.warning);
            alert.disableCancel(true);
            alert.showAlert();
            return;
        }
/*
        if (!cashless.IsReady()) {
            alert.setTitle("Payments not available");
            alert.setAnimation(R.raw.warning_red);
            alert.disableCancel(true);
            alert.showAlert();
            return;
        }*/

        cashless.reset();
        Intent intent = new Intent(this, PayActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("cardShop", cardShopArrayList);
        intent.putExtra("Cashless", cashless);
        startActivity(intent);
    }

    private void clearAll() {
        if (cardShopArrayList.size() < 1)
            return;


        String l = String.format("%s [%s]: %s", TAG, "clearAll", "Delete all products in car?");
        Timber.i(l.toString());
        delete = true;
        alert.setAnimation(R.raw.warning);
        alert.setTitle("Delate all products?");
        alert.setAceptText("Delete all");
        alert.setCancelText("Cancel");
        alert.disableCancel(false);
        alert.showAlert();
    }

    private void startProductDeletionAnimation() {
        int firstElement = 0;
        if (firstElement < cardShopArrayList.size()) {
            cardShopArrayList.remove(firstElement);
            adapter.notifyItemRemoved(firstElement);

            // Si aún quedan productos, continúa con la animación de eliminación
            if (firstElement < cardShopArrayList.size()) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startProductDeletionAnimation();
                    }
                }, 300);
            }
        }
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

    public static void updateTotal(ArrayList<Concept> cardShopArrayList) {
        double total = 0.00;
        for (Concept cardShop1 : cardShopArrayList) {
            total += cardShop1.getPrice() * cardShop1.getInCart();
        }
        tvTotal.setText(String.format("$%.2f", total));
    }

    private void checkAvailable() {
        alert.setTitle("Check product availability");
        alert.setLoop(false);
        alert.setAnimation(R.raw.warning);
        alert.disableCancel(true);
        alert.showAlert();
    }

    @Override
    public void onSuccessConcept(ConceptResponse succesResponse) {
        stopLoading();

        if (succesResponse.getConcept().getQuantity() < 1) {
            alert.setAnimation(R.raw.warning);
            alert.setTitle("Contact the administrator to check availability");
            alert.setLoop(true);
            alert.disableCancel(true);
            alert.showAlert();
            return;
        }

        boolean producInCart = false;
        for (Concept item : cardShopArrayList) {
            if (succesResponse.getConcept().getId().equals(item.getId())) {
                producInCart = true;
                int max = item.getInCart() + 1;
                if (item.getQuantity() >= max) {
                    item.setInCart(max);
                    updateTotal(cardShopArrayList);
                    String internalStoragePath = getFilesDir().getPath();
                    adapter = new CardShopAdapter(cardShopArrayList, internalStoragePath, false);
                    cardShop.setAdapter(adapter);
                    String l = String.format("%s [%s]: %s", TAG, "OnSuccessConcept", "Product in cart:" + item.getName());
                    Timber.i(l.toString());
                } else {
                    checkAvailable();
                }
                break;
            }
        }
        if (!producInCart) {
            succesResponse.getConcept().setInCart(1);
            String l = String.format("%s [%s]: %s", TAG, "OnSuccessConcept", succesResponse.getConcept());
            Timber.i(l.toString());

            cardShopArrayList.add(succesResponse.getConcept());

            String internalStoragePath = getFilesDir().getPath();
            adapter = new CardShopAdapter(cardShopArrayList, internalStoragePath, false);
            cardShop.setAdapter(adapter);
            cardShop.setItemAnimator(new DeleteAnimation());
            updateTotal(cardShopArrayList);
        }
        
        adapter.getIsMax().observe(this, isMax -> {
            if (isMax)
                checkAvailable();
        });
    }

    @Override
    public void onErrorConcept(ConceptResponse errorResponse) {
        stopLoading();
        Toast.makeText(this, errorResponse.getMessage(), Toast.LENGTH_LONG).show();
        switch (errorResponse.getType()) {
            case NOTFOUND:
                String l = String.format("%s [%s]: %s", TAG, "OnErrorConcept", ("not found: " + errorResponse.getConcept().getId()));
                Timber.i(l.toString());
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
        alert.disableCancel(true);
        alert.showAlert();
    }

    private class ConectMarshall implements Runnable {

        @Override
        public void run() {
            cashless.disconect();
            cashless.VPOSConfig(getApplicationContext());
        }
    }

    private class Reconnection implements Runnable {
        @Override
        public void run() {
            int conDisconect = 0;
            while (true) {
                if (cashless.IsReady()) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    conDisconect++;
                    if (conDisconect == 1) {
                        ConectMarshall hilo = new ConectMarshall();
                        threadReconnection = new Thread(hilo);
                        threadReconnection.start();
                    }
                    if (conDisconect > 25) {
                        conDisconect = 0;
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

        }
    }
}