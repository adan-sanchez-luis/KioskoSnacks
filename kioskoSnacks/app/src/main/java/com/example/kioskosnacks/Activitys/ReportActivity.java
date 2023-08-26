package com.example.kioskosnacks.Activitys;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.kioskosnacks.Alertas.AlertMessage;
import com.example.kioskosnacks.R;
import com.example.kioskosnacks.WebService.Models.ConceptReport;
import com.example.kioskosnacks.WebService.Observers.ReportObserver;
import com.example.kioskosnacks.WebService.Responses.ReportResponse;
import com.example.kioskosnacks.WebService.ViewModels.ReportViewModel;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ReportActivity extends BaseActivity implements View.OnClickListener, ReportObserver {

    private String TAG = this.getClass().getSimpleName();
    private LottieAnimationView loading;
    private View layoutLoading;
    private ReportViewModel reportViewModel;
    private AlertMessage alert;
    private Button btnStartDate;
    private Button btnEndDate;
    private Calendar selectedStartDate = Calendar.getInstance();
    private Calendar selectedEndDate = Calendar.getInstance();
    private Spinner spinnerOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        FrameLayout back = findViewById(R.id.backActivity);
        back.setOnClickListener(this);

        TextView titulo = findViewById(R.id.titulo);
        titulo.setText("Report");


        layoutLoading = findViewById(R.id.layout_loading);
        loading = findViewById(R.id.loading);
        startLoading();

        reportViewModel = new ReportViewModel(this);
        reportViewModel.setReportObserver(this);

        alert = new AlertMessage(this);
        alert.setListener(this);

        btnStartDate = findViewById(R.id.startDate);
        btnEndDate = findViewById(R.id.endDate);

        btnStartDate.setOnClickListener(this);
        btnEndDate.setOnClickListener(this);

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        selectedStartDate.add(Calendar.DAY_OF_MONTH, -7);
        btnStartDate.setText(sdf.format(selectedStartDate.getTime()));

        btnEndDate.setText(sdf.format(selectedEndDate.getTime()));

        spinnerOrder = findViewById(R.id.order);
        configSpinner();

        Button btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(this);

        SimpleDateFormat formatService = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String date1 = formatService.format(selectedStartDate.getTime()) + "T00:00:00";
        String date2 = formatService.format(selectedEndDate.getTime()) + "T00:00:00";
        int order = 1;
        reportViewModel.getTopSales(date1, date2, order);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backActivity:
                this.finish();
                break;
            case R.id.startDate:
                showDatePickerDialog(selectedStartDate, btnStartDate);
                break;
            case R.id.endDate:
                showDatePickerDialog(selectedEndDate, btnEndDate);
                break;
            case R.id.btnSearch:
                searchReport();
                break;
            case R.id.alertConfirm:
                alert.alertDismiss();
                break;
        }
    }

    private void searchReport() {
        startLoading();
        String s = btnStartDate.getText().toString();
        String e = btnEndDate.getText().toString();
        int ord = spinnerOrder.getSelectedItemPosition() == 0 ? 1 : 0;

        String[] split = s.split("/");
        //2023-07-08T23:06:23
        String start = split[2] + "-" + split[0] + "-" + split[1] + "T00:00:00";
        split = e.split("/");
        String end = split[2] + "-" + split[0] + "-" + split[1] + "T00:00:00";

        reportViewModel.getTopSales(start, end, ord);
    }

    // Método para mostrar el diálogo de selección de fecha
    private void showDatePickerDialog(final Calendar selectedDate, final Button btn) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Actualiza la fecha seleccionada
                        selectedDate.set(Calendar.YEAR, year);
                        selectedDate.set(Calendar.MONTH, monthOfYear);
                        selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        // Formatea la fecha seleccionada y actualiza el TextView
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                        String formattedDate = sdf.format(selectedDate.getTime());
                        btn.setText(formattedDate);
                    }
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }


    private void configSpinner() {
        String[] salesOptions = {"Top Sale", "Lower Sales"};
        // Crea un ArrayAdapter personalizado
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                R.layout.spinner_sales, // Utiliza el diseño personalizado
                salesOptions
        ) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextSize(40); // Tamaño de letra en 50sp
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

        spinnerOrder.setAdapter(adapter);
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
    public void onSuccessReport(ReportResponse succesResponse) {
        stopLoading();
        TableLayout listProduct = findViewById(R.id.listProduct);
        listProduct.removeAllViews();
        double total = 0;
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());
        for (ConceptReport item : succesResponse.getTopSales()) {
            View borderView = new View(this);
            borderView.setBackgroundColor(Color.WHITE);
            TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    3); // Altura de 1 píxel
            borderView.setLayoutParams(layoutParams);
            listProduct.addView(borderView);

            View rowProduct = LayoutInflater.from(this).inflate(R.layout.product_row_report, null, false);
            TextView nameRow = rowProduct.findViewById(R.id.nameRow);
            TextView unitPriceRow = rowProduct.findViewById(R.id.unitPriceRow);
            TextView unitsSalesRow = rowProduct.findViewById(R.id.unitsSalesRow);
            TextView totalConceptRow = rowProduct.findViewById(R.id.totalConceptRow);

            double price = Double.parseDouble(item.getPrice());
            double salesConcept = item.getTotalSales() * price;
            total += salesConcept;

            nameRow.setText(item.getName());
            unitsSalesRow.setText(item.getTotalSales() + "");
            unitPriceRow.setText(numberFormat.format(price));
            totalConceptRow.setText(numberFormat.format(salesConcept));

            listProduct.addView(rowProduct);
        }

        TextView tvTotal = findViewById(R.id.totalSales);
        tvTotal.setText(numberFormat.format(total));
    }

    @Override
    public void onErrorReport(ReportResponse errorResponse) {
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