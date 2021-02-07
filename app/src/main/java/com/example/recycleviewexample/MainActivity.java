package com.example.recycleviewexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try (InputStream io = getAssets().open("webp_store.zip")){
            unpackFileIO(io, this.getFilesDir().getPath());
        } catch (IOException e) {
            Log.e("FileHelper", "unpackFileIO exception " + e.getMessage());
        }

        recyclerView = findViewById(R.id.my_recycler_view);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new ExampleAdapter(this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.getLayoutManager().scrollToPosition(Integer.MAX_VALUE / 2);
        LinearSnapHelper snapHelper = new LinearSnapHelper() {
            @Override
            public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
                View centerView = findSnapView(layoutManager);
                if (centerView == null) {
                    return RecyclerView.NO_POSITION;
                }

                int position =  ((LinearLayoutManager)layoutManager).findFirstVisibleItemPosition();
                int lastPosition = ((LinearLayoutManager)layoutManager).findLastVisibleItemPosition();
                int targetPosition = -1;

                if (velocityX < 0) {
                    targetPosition = lastPosition - 1;
                } else {
                    targetPosition = position + 1;
                }


                if (targetPosition < 0) {
                    targetPosition = 0;
                }
                return targetPosition;
            }
        };
        snapHelper.attachToRecyclerView(recyclerView);
    }


    private void unpackFileIO(InputStream inputStream, String destination) {
        byte[] buffer = new byte[1024];
        ZipEntry ze;

        try ( ZipInputStream zis = new ZipInputStream(inputStream)) {
            while ((ze = zis.getNextEntry()) != null) {
                String name = ze.getName();
                File newFile = new File(destination + "/" + name);
                try ( FileOutputStream fos = new FileOutputStream(newFile)) {
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                } catch (Exception e) {
                    Log.e("FileHelper", "unpackFileIO exception " + e.getMessage());
                }
            }

        } catch (Exception e) {
            Log.e("FileHelper", "unpackFileIO exception " + e.getMessage());
        }
    }
}
