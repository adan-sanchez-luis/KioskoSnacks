package com.example.kioskosnacks.RecyclerViewUtils.Adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kioskosnacks.R;
import com.example.kioskosnacks.WebService.Models.Concept;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolderInventory> {
    private ArrayList<Concept> inventoryArrayList;
    private ArrayList<Concept> filteredList;
    private OnItemClickListener onItemClickListener;
    private String path;

    public InventoryAdapter(ArrayList<Concept> inventoryArrayList, String path) {
        this.inventoryArrayList = inventoryArrayList;
        this.filteredList = new ArrayList<>(inventoryArrayList);
        this.path = path;
    }

    @NonNull
    @Override
    public InventoryAdapter.ViewHolderInventory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.products_inventory, null, false);
        return new ViewHolderInventory(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryAdapter.ViewHolderInventory holder, int position) {
        holder.setData(filteredList.get(position));
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public class ViewHolderInventory extends RecyclerView.ViewHolder {
        private TextView nameProduct;
        private TextView stock;
        private TextView descriptionProduct;
        private ImageView imageProduct;
        private CardView cardView;

        public ViewHolderInventory(@NonNull View itemView) {
            super(itemView);
            nameProduct = itemView.findViewById(R.id.productNameInventory);
            stock = itemView.findViewById(R.id.productStock);
            descriptionProduct = itemView.findViewById(R.id.descriptionProductInventory);
            imageProduct = itemView.findViewById(R.id.imageProductInventory);
            cardView = itemView.findViewById(R.id.producInventory);
        }

        public void setData(Concept producsInventory) {
            nameProduct.setText(producsInventory.getName());
            stock.setText("Stock: " + producsInventory.getQuantity());
            descriptionProduct.setText(producsInventory.getDescription());

            File imageFile = new File(path + File.separator + producsInventory.getId() + ".jpg");

            if (imageFile.exists()) {
                // Mostrar la imagen en el ImageView
                imageProduct.setImageURI(Uri.fromFile(imageFile));
            } else {
                imageProduct.setImageResource(R.drawable.not_found);
            }

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null)
                        onItemClickListener.onClickListener(producsInventory);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onClickListener(Concept producsInventory);
    }

    // Método para establecer el listener del clic
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void filter(String query) {
        filteredList.clear();
        System.out.println("busqueda: " + query);
        if (query.isEmpty()) {
            filteredList.addAll(inventoryArrayList);
        } else {
            query = query.toLowerCase(Locale.getDefault());
            for (Concept item : inventoryArrayList) {
                if (item.getName().toLowerCase(Locale.getDefault()).contains(query)) {
                    filteredList.add(item);
                    System.out.println("relacionado");
                    System.out.println(item);
                }
            }
        }
        notifyDataSetChanged();
    }
}
