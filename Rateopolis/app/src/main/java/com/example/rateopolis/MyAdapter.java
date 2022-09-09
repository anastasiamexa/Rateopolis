package com.example.rateopolis;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> implements Filterable {

    Context context;
    ArrayList<Object> list;
    ArrayList<Object> listFull; // List copy used for search
    String c = ActivitiesList.category;

    // Constructor
    public MyAdapter(Context context, ArrayList<Object> list) {
        this.context = context;
        this.list = list;
        // New list containing the same items, for independent usage
        listFull = new ArrayList<>(list);
    }

    // THIS IS NOT A CONSTRUCTOR!!!
    // listFull does not fill up in the constructor method, because the list is still empty...
    // So fill up the list here
    public void MyAdapter1(){listFull = new ArrayList<>(list);}

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new MyViewHolder(v);
    }

    // Method to bind the view in Card view with data in Event or Store class
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Here the list is full, so go to MyAdapter1 method to fill up the listFull
        if (listFull.size() == 0){
            MyAdapter1();
        }
        if (c.equals("Events")) {
            // Add attribute values from model class to appropriate view in Card view
            Event event = (Event) list.get(position);
            holder.title.setText(event.getTitle());
            holder.hour.setText(event.getHour());
            holder.stage.setText(event.getArea());
            holder.address.setText(event.getAddress());
            // Get event cover images from Firebase Storage
            StorageReference imageRef2 = holder.storageReference.child(event.getCover());
            imageRef2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                // Use Glide to get the image through url
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(context).load(uri).into(holder.cover);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.getLocalizedMessage();
                }
            });

            holder.mainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // When user clicks on a card view, go to the next activity and pass the event object
                    Intent intent = new Intent(context, More.class);
                    intent.putExtra("object", event);
                    context.startActivity(intent);
                }
            });
        } else {
            // Add attribute values from model class to appropriate view in Card view
            Store store = (Store) list.get(position);
            holder.title.setText(store.getTitle());
            holder.hour.setText(store.getHour());
            holder.stage.setText(store.getArea());
            holder.address.setText(store.getAddress());
            // Get store cover images from Firebase Storage
            StorageReference imageRef2 = holder.storageReference.child(store.getCover());
            imageRef2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                // Use Glide to get the image through url
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(context).load(uri).into(holder.cover);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.getLocalizedMessage();
                }
            });

            holder.mainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // When user clicks on a card view, go to the next activity and pass the store object
                    Intent intent = new Intent(context, More.class);
                    intent.putExtra("object", store);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    // Class to create references of the views in Card view
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView stage, address, hour, title;
        ImageView cover;
        FirebaseStorage firebaseStorage;
        StorageReference storageReference;
        ConstraintLayout mainLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            firebaseStorage = FirebaseStorage.getInstance();
            storageReference = firebaseStorage.getReference();
            cover = itemView.findViewById(R.id.cover);
            stage = itemView.findViewById(R.id.stage);
            address = itemView.findViewById(R.id.nAddress);
            hour = itemView.findViewById(R.id.nHour);
            title = itemView.findViewById(R.id.title);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }

    @Override
    public Filter getFilter() {
        return listFilter;
    }

    // Return the filter results
    private Filter listFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Object> filteredList = new ArrayList<>();
            // constraint variable is the input of the search bar
            // If constraint is null, return all the results
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(listFull);
            } else { // Create a string with the filter pattern (case insensitive)
                String filterPattern = constraint.toString().toLowerCase().trim();
                // Iterate all events or stores from list and find those that match the filter pattern
                for (Object item : listFull) {
                    if (c.equals("Events")) { // Filter for event titles
                        if (((Event) item).getTitle().toLowerCase().startsWith(filterPattern)){
                            filteredList.add(item); // add it to the filtered list
                        }
                    } else { // Filter for store titles
                        if (((Store) item).getTitle().toLowerCase().startsWith(filterPattern)){
                            filteredList.add(item); // add it to the filtered list
                        }
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            // Returns the filtered list to the publishResults method
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // Delete all items from list and add only those that match the filter pattern
            list.clear();
            list.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };
}
