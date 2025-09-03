package com.example.prompt;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private OnProductStatusChangeListener listener;

    public interface OnProductStatusChangeListener {
        void onStatusChange(int productId, String newStatus);
    }

    public ProductAdapter(List<Product> productList, OnProductStatusChangeListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product currentProduct = productList.get(position);
        holder.textViewName.setText(currentProduct.getName());
        holder.textViewStatus.setText(currentProduct.getStatus());
        holder.textViewDate.setText(currentProduct.getDate());

        if (currentProduct.getStatus().equals(DatabaseHelper.STATUS_PENDING)) {
            holder.buttonToggleStatus.setText("Marcar como Comprado");
        } else {
            holder.buttonToggleStatus.setText("Marcar como Pendiente");
        }

        holder.buttonToggleStatus.setOnClickListener(v -> {
            if (listener != null) {
                String newStatus = currentProduct.getStatus().equals(DatabaseHelper.STATUS_PENDING)
                        ? DatabaseHelper.STATUS_BOUGHT
                        : DatabaseHelper.STATUS_PENDING;
                listener.onStatusChange(currentProduct.getId(), newStatus);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void updateProducts(List<Product> newProductList) {
        this.productList = newProductList;
        notifyDataSetChanged();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewStatus;
        TextView textViewDate;
        Button buttonToggleStatus;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewProductName);
            textViewStatus = itemView.findViewById(R.id.textViewProductStatus);
            textViewDate = itemView.findViewById(R.id.textViewProductDate);
            buttonToggleStatus = itemView.findViewById(R.id.buttonToggleStatus);
        }
    }
}

