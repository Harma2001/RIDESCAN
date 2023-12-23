package com.example.tvsridescan;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.example.tvsridescan.utility.NewProductModel;
import com.example.tvsridescan.utility.NewProductRecyclerAdapter;

import java.util.ArrayList;

public class Samp extends AppCompatActivity {

    private NewProductModel newProductModel;
    private ArrayList<NewProductModel> newProductModelArrayList = new ArrayList<>();
    private NewProductRecyclerAdapter newProductRecyclerAdapter;
    RecyclerView recyclerView_newProduct;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_samp);

        context =  Samp.this;


        // for new product
        recyclerView_newProduct = findViewById(R.id.recycler_newProd);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
        recyclerView_newProduct.setLayoutManager(linearLayoutManager);
        recyclerView_newProduct.setItemAnimator(new DefaultItemAnimator());

        newProductRecyclerAdapter = new NewProductRecyclerAdapter(this,newProductModelArrayList,getScreenWidth());
        recyclerView_newProduct.setAdapter(newProductRecyclerAdapter );

        newProductModel = new NewProductModel("1", getString(R.string.apacherr310),R.drawable.rr310bike );
        newProductModelArrayList.add(newProductModel);

        newProductModel = new NewProductModel("2", getString(R.string.rtr2004v2chcarb),R.drawable.rtr2004vbike1 );
        newProductModelArrayList.add(newProductModel);

        newProductModel = new NewProductModel("3", getString(R.string.rtr2004v2chefi),R.drawable.rtr2004vbike1 );
        newProductModelArrayList.add(newProductModel);

        newProductModel = new NewProductModel("4", getString(R.string.rtr1604v1chcarb),R.drawable.rtr1604vbike1 );
        newProductModelArrayList.add(newProductModel);

        newProductModel = new NewProductModel("5", getString(R.string.rtr1604v1chefi),R.drawable.rtr1604vbike1 );
        newProductModelArrayList.add(newProductModel);

        newProductModel = new NewProductModel("6", getString(R.string.rtr2004v1chcarb),R.drawable.rtr2004vbike2 );
        newProductModelArrayList.add(newProductModel);

        newProductModel = new NewProductModel("7", getString(R.string.rtr2004v1chefi),R.drawable.rtr2004vbike2 );
        newProductModelArrayList.add(newProductModel);
        newProductRecyclerAdapter.notifyDataSetChanged();

    }

    public int getScreenWidth()
    {

        int width = 100;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager)getApplicationContext().getSystemService(WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;

        return width;
    }

}
