package com.example.recycleviewexample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ExampleAdapter extends RecyclerView.Adapter<ExampleAdapter.MyViewHolder> {

    private int count = 0;
    private ArrayList<String> list = new ArrayList<>();
    Context context;
    private Map<String, Bitmap> map = new HashMap<>();

    public ExampleAdapter(Context context) {
        this.context = context;
        File directory = new File(context.getFilesDir().getPath());
        File[] files = directory.listFiles();
        count = files.length;
        for (int i = 0;i<count;i++) {
            list.add(files[i].getName());
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        int positionInList = position % count;
        holder.textView.setText(list.get(positionInList));
        setImage(list.get(positionInList), holder.imageView);
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.list_item_image);
            textView = itemView.findViewById(R.id.list_item_text);
        }
    }

    private void setImage(String name, ImageView view) {
        Bitmap bitmap;
        if (map.containsKey(name)) {
            bitmap = map.get(name);
        } else {
            File file = new File(context.getFilesDir().getPath(), name);
            try (InputStream is = new FileInputStream(file)) {
                bitmap = BitmapFactory.decodeStream(is);
                map.put(name, bitmap);
            } catch (Exception e) {
                bitmap = null;
                e.printStackTrace();
            }
        }
        view.setImageBitmap(bitmap);
    }
}
