package com.example.kioskosnacks.RecyclerViewUtils.Adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kioskosnacks.MainActivity;
import com.example.kioskosnacks.RecyclerViewUtils.Models.ProducsCardShop;
import com.example.kioskosnacks.R;
import com.example.kioskosnacks.WebService.Models.Concept;

import java.io.File;
import java.util.ArrayList;

public class CardShopAdapter extends RecyclerView.Adapter<CardShopAdapter.ViewHolderCardShop> {

    private ArrayList<Concept> cardShops;
    private String path;
    private boolean supply = false;
    private MutableLiveData<Boolean> isMax;

    public CardShopAdapter(ArrayList<Concept> cardShops, String path, boolean supply) {
        this.cardShops = cardShops;
        this.path = path;
        this.supply = supply;
        isMax=new MutableLiveData<>();
    }

    public MutableLiveData<Boolean> getIsMax() {
        if (isMax ==null)
            return new MutableLiveData<>(false);
        return isMax;
    }

    @NonNull
    @Override
    public CardShopAdapter.ViewHolderCardShop onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.products_card, null, false);
        return new ViewHolderCardShop(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardShopAdapter.ViewHolderCardShop holder, int position) {
        Concept producsCardShop = cardShops.get(position);
        holder.setData(producsCardShop, position);
    }

    @Override
    public int getItemCount() {
        return cardShops.size();
    }

    public void deleteProduct(int pos) {
        cardShops.get(pos).setDelete(true);
        eliminarProductosMarcados(pos);
        //cardShops.remove(pos);
    }

    // Método para realizar la eliminación definitiva de los productos marcados
    public void eliminarProductosMarcados(int posicionEliminada) {
        for (int i = cardShops.size() - 1; i >= 0; i--) {
            Concept producto = cardShops.get(i);
            if (producto.isDelete()) {
                cardShops.remove(i);
                notifyItemRemoved(i);

                // Verificar si la posición del producto eliminado es anterior a la posición actual
                // decrementar la posición actual para que se mantenga correcta.
                if (i < posicionEliminada) {
                    posicionEliminada--;
                }
            }
        }
    }

    public ArrayList<Concept> getCardShops() {
        return cardShops;
    }

    public class ViewHolderCardShop extends RecyclerView.ViewHolder {
        private TextView nameProduct;
        private TextView descriptionProduct;
        private TextView amountProduct;
        private TextView priceProduct;
        private ImageView imageProduct;
        private ImageView minus;
        private ImageView add;

        public ViewHolderCardShop(@NonNull View itemView) {
            super(itemView);
            nameProduct = itemView.findViewById(R.id.nameProductCardShop);
            descriptionProduct = itemView.findViewById(R.id.descriptionProductCardShop);
            amountProduct = itemView.findViewById(R.id.amountProductCardShop);
            priceProduct = itemView.findViewById(R.id.priceProductCardShop);
            imageProduct = itemView.findViewById(R.id.imageProductCardShop);
            minus = itemView.findViewById(R.id.minus);
            add = itemView.findViewById(R.id.add);
        }

        public void setData(Concept producsCardShop, int position) {
            nameProduct.setText(producsCardShop.getName());
            descriptionProduct.setText(producsCardShop.getDescription());
            amountProduct.setText(producsCardShop.getInCart() + "");
            priceProduct.setText(String.format("$%.2f", producsCardShop.getPrice()));


            File imageFile = new File(path + File.separator + producsCardShop.getId() + ".jpg");

            if (imageFile.exists()) {
                // Mostrar la imagen en el ImageView
                imageProduct.setImageURI(Uri.fromFile(imageFile));
            } else {
                imageProduct.setImageResource(R.drawable.not_found);
            }
            minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    producsCardShop.setInCart(producsCardShop.getInCart() - 1);
                    amountProduct.setText(producsCardShop.getInCart() + "");
                    if (producsCardShop.getInCart() == 0) {
                        producsCardShop.setDelete(true);
                        int currentPosition = getAdapterPosition(); // Obtener la posición actual del producto
                        deleteProduct(currentPosition); // Eliminar el producto actualmente seleccionado
                    } else {
                        producsCardShop.setDelete(false);
                    }
                    MainActivity.updateTotal(cardShops);
                }
            });
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int max = producsCardShop.getInCart() + 1;
                    if (supply) {
                        producsCardShop.setInCart(max);
                        amountProduct.setText(producsCardShop.getInCart() + "");
                        MainActivity.updateTotal(cardShops);
                    } else {
                        if (producsCardShop.getQuantity() >= max) {
                            producsCardShop.setInCart(max);
                            amountProduct.setText(producsCardShop.getInCart() + "");
                            MainActivity.updateTotal(cardShops);
                        }
                        else {
                            isMax.postValue(true);
                        }
                    }
                }
            });
        }
    }
}
