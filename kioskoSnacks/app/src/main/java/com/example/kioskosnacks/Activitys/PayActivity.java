package com.example.kioskosnacks.Activitys;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bitmick.marshall.vmc.vmc_vend_t;
import com.example.kioskosnacks.Alertas.AlertMessage;
import com.example.kioskosnacks.Marshall.CashlessResponse;
import com.example.kioskosnacks.Marshall.Marshall2;
import com.example.kioskosnacks.RecyclerViewUtils.Models.ProducsCardShop;
import com.example.kioskosnacks.R;
import com.example.kioskosnacks.WebService.Models.Concept;
import com.example.kioskosnacks.WebService.Observers.PayObserver;
import com.example.kioskosnacks.WebService.Observers.UpdateConceptObserver;
import com.example.kioskosnacks.WebService.Responses.PayConceptResponse;
import com.example.kioskosnacks.WebService.Responses.UpdateConceptResponse;
import com.example.kioskosnacks.WebService.ViewModels.ConceptViewModel;
import com.example.kioskosnacks.WebService.ViewModels.PayViewModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import timber.log.Timber;

public class PayActivity extends BaseActivity implements View.OnClickListener, PayObserver, UpdateConceptObserver {

    private String TAG = this.getClass().getName();
    private ArrayList<Concept> conceptArrayList;
    private Marshall2 Cashless;
    private AlertMessage alerta;
    private PayViewModel payViewModel;
    private double total;
    private boolean isCancel = false;
    private ConceptViewModel conceptViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        try {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            conceptArrayList = (ArrayList<Concept>) bundle.get("cardShop");
            Cashless = (Marshall2) bundle.get("Cashless");

            FrameLayout back = findViewById(R.id.backActivity);
            back.setOnClickListener(this);


            TextView titulo = findViewById(R.id.titulo);
            titulo.setText("Pay");

            loadCarshop();
            alerta = new AlertMessage(this);
            alerta.setListener(this);
            alerta.disableCancel(true);

            payViewModel = new PayViewModel(this);
            payViewModel.addObserver(this);

            conceptViewModel = new ConceptViewModel(this);
            conceptViewModel.addObserverUpdate(this);

            Cashless.getObserver().observe(this, response -> {
                if (response == null)
                    return;
                switch (response) {
                    case VendApproved:
                        VendApproved();
                        break;
                    case VendDenied:
                        vendDenied();
                        break;
                }
            });

        } catch (Exception e) {
            String log = String.format("%s [%s]: %s", TAG, "onCreate", e.getMessage());
            Timber.i(log.toString());
        }
        Button pay = findViewById(R.id.btnPayFull);
        Button cancel = findViewById(R.id.btnPayCancel);
        pay.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    private void update() {
        for (Concept item : conceptArrayList) {
            item.setQuantity(item.getQuantity() - item.getInCart());
            conceptViewModel.updateConcept(item);
        }
    }

    private void vendDenied() {
        if (isCancel)
            return;

        alerta.setAnimation(R.raw.warning_red);
        alerta.setTitle("Payment Declined");
        show();
    }

    private void VendApproved() {
        payViewModel.Pay(conceptArrayList, total);
        update();
        alerta.setAnimation(R.raw.save);
        alerta.setTitle("Payment finished");
        show();
    }

    private void show() {
        alerta.showAlert();
    }

    private void loadCarshop() {
        TableLayout listProduct = findViewById(R.id.listProduct);
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());
        double subTotal = 0;
        double tax = 0;
        total = 0;
        for (Concept product : conceptArrayList) {
            // Agrega el borde blanco al TableLayout
            View borderView = new View(this);
            borderView.setBackgroundColor(Color.WHITE);
            TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    3); // Altura de 1 píxel
            borderView.setLayoutParams(layoutParams);
            listProduct.addView(borderView);

            View rowProduct = LayoutInflater.from(this).inflate(R.layout.product_row, null, false);
            TextView qtyRow = rowProduct.findViewById(R.id.qtyRow);
            TextView nameRow = rowProduct.findViewById(R.id.nameRow);
            TextView priceRow = rowProduct.findViewById(R.id.priceRow);
            TextView amountRow = rowProduct.findViewById(R.id.amountRow);

            double amount = product.getPrice() * product.getInCart();

            qtyRow.setText("" + product.getInCart());
            nameRow.setText("" + product.getName());
            priceRow.setText(numberFormat.format(product.getPrice()));
            amountRow.setText(numberFormat.format(amount));

            listProduct.addView(rowProduct);

            subTotal += amount;
        }
        tax = subTotal * 0.0825;
        total = subTotal + tax;
        TextView tvSubTotal = findViewById(R.id.subTotalPay);
        TextView tvTax = findViewById(R.id.taxPay);
        TextView tvTotal = findViewById(R.id.totalPay);

        tvSubTotal.setText(numberFormat.format(subTotal));
        tvTax.setText(numberFormat.format(tax));
        tvTotal.setText(numberFormat.format(total));

        Pay(total * 100);
    }


    private void Pay(double monto) {
        // Verificar si Nayax esta disponible
        boolean nayaxDisponible = Cashless.IsReady();

        if (nayaxDisponible) {
            // Empezar proceso de compra creo no es necesario pero igual a ver que pedo
            Cashless.StartSession();

            // Lista de articulos pa cobrar en el Nayax
            ArrayList<vmc_vend_t.vend_item_t> list = new ArrayList<vmc_vend_t.vend_item_t>();
            list.add(new vmc_vend_t.vend_item_t((short) 1, (short) monto, (short) 1, (byte) 1));

            // Iniciar proceso de venta este sí es necesario para iniciar la venta con los articulos enviados
            Cashless.Vend_Start(list);

        } else {
            Toast.makeText(this, "Nayax no conectado", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backActivity:
                isCancel = true;
                Cashless.cancelarVenta();
                this.finish();
                break;
            case R.id.alertConfirm:
                closeActivity();
                break;
            case R.id.btnPayFull:
                Cashless.getObserver().postValue(CashlessResponse.MessageType.VendApproved);
                break;
            case R.id.btnPayCancel:
                Cashless.getObserver().postValue(CashlessResponse.MessageType.VendDenied);
                break;
        }
    }

    private void closeActivity() {
        alerta.alertDismiss();
        this.finish();
    }

    @Override
    public void onSuccessPayConcept(PayConceptResponse succesResponse) {

        Toast.makeText(this, "Pay saved", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onErrorPayConcept(PayConceptResponse errorResponse) {
        Toast.makeText(this, "Failded to save pay", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSuccessUpdate(UpdateConceptResponse succesResponse) {

    }

    @Override
    public void onErrorUpdate(UpdateConceptResponse errorResponse) {

    }
}