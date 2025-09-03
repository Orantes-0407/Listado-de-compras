package com.example.prompt;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements ProductAdapter.OnProductStatusChangeListener {

    private DatabaseHelper db;
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList;
    private EditText editTextProductName;
    private Spinner spinnerStatusFilter;
    private String selectedDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);
        editTextProductName = findViewById(R.id.editTextProductName);
        Button buttonAddProduct = findViewById(R.id.buttonAddProduct);
        recyclerView = findViewById(R.id.recyclerViewProducts);
        spinnerStatusFilter = findViewById(R.id.spinnerStatusFilter);
        Button buttonDateFilter = findViewById(R.id.buttonDateFilter);
        Button buttonGenerateReport = findViewById(R.id.buttonGenerateReport);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadProducts();

        buttonAddProduct.setOnClickListener(v -> {
            String productName = editTextProductName.getText().toString();
            if (!productName.isEmpty()) {
                db.addProduct(productName);
                editTextProductName.setText("");
                loadProducts();
            } else {
                Toast.makeText(MainActivity.this, "Por favor, ingrese un nombre de producto", Toast.LENGTH_SHORT).show();
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.status_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatusFilter.setAdapter(adapter);

        spinnerStatusFilter.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                filterProducts();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        buttonDateFilter.setOnClickListener(v -> showDatePickerDialog());

        buttonGenerateReport.setOnClickListener(v -> generateReport());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadProducts() {
        productList = db.getAllProducts();
        if (adapter == null) {
            adapter = new ProductAdapter(productList, this);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateProducts(productList);
        }
    }

    private void filterProducts() {
        String status = spinnerStatusFilter.getSelectedItem().toString();
        if (status.equals("Todos")) {
            status = null;
        }
        productList = db.getProductsByFilter(status, selectedDate);
        adapter.updateProducts(productList);
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar newDate = Calendar.getInstance();
            newDate.set(year, month, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            selectedDate = sdf.format(newDate.getTime());
            filterProducts();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void generateReport() {
        String status = spinnerStatusFilter.getSelectedItem().toString();
        if (status.equals("Todos")) {
            status = null;
        }

        List<Product> filteredList = db.getProductsByFilter(status, selectedDate);
        int total = filteredList.size();
        long bought = filteredList.stream().filter(p -> p.getStatus().equals(DatabaseHelper.STATUS_BOUGHT)).count();
        long pending = total - bought;

        String report = "Reporte de Compras:\n" +
                "Total de productos: " + total + "\n" +
                "Comprados: " + bought + "\n" +
                "Pendientes: " + pending;

        Toast.makeText(this, report, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStatusChange(int productId, String newStatus) {
        db.updateProductStatus(productId, newStatus);
        filterProducts();
    }
}