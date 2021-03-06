package domain.com.recipes;

/*-----------------------------------

    - Recipes -

    created by cubycode ©2017
    All Rights reserved

-----------------------------------*/


import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Categories extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categories);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Hide ActionBar
        getSupportActionBar().hide();


        setupCategoriesListView();


        // MARK: - SELECT BUTTON ------------------------------------
        Button selButt = findViewById(R.id.catSelectButt);
        selButt.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {  finish();
              Intent intent =new Intent(Categories.this ,Home.class);
              startActivity(intent);
          }});
        //OFFLINE ---------- mode
        Button offline = findViewById(R.id.offButton);
        offline.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent b =new Intent(Categories.this,Offline.class);
                startActivity(b);
            }
        });


    }// end onCreate()





    // MARK: - SETUP CATEGORIES LISTVIEW ---------------------------------------------------------------
    void setupCategoriesListView() {
        final List<String>catArray = new ArrayList<String>(Arrays.asList(Configs.categoriesArray));

        class ListAdapter extends BaseAdapter {
            private Context context;

            public ListAdapter(Context context, List<String> categories) {
                super();
                this.context = context;
            }

            // CONFIGURE CELL
            @Override
            public View getView(int position, View cell, ViewGroup parent) {
                if (cell == null) {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    assert inflater != null;
                    cell = inflater.inflate(R.layout.cell_category, null);
                }

                // Get name
                TextView nametxt = cell.findViewById(R.id.cCatName);
                nametxt.setText(catArray.get(position));


                // Get image
                ImageView catImg = cell.findViewById(R.id.cCatImage);
                String imageName = catArray.get(position).toLowerCase();

                // IMPORTANT: Set the correct png name for those Categories that have multiple words in their name

                if (imageName.matches("main dish")) {
                    imageName = "main_dish";
                }
                if (imageName.matches("burger jumbo")){
                    imageName = "burger_jumbo";
                }
                if (imageName.matches("milk shake")){
                    imageName = "milk_shake";
                }
                if (imageName.matches("mix shake")){
                    imageName = "mix_shake";
                }  if (imageName.matches("ice cream")){
                    imageName = "ice_cream";
                }if (imageName.matches("hot drinks")){
                    imageName = "hot_drinks";
                }

                int resID = getResources().getIdentifier(imageName , "drawable", getPackageName());
                catImg.setImageResource(resID);


                return cell;
            }

            @Override public int getCount() { return catArray.size(); }
            @Override public Object getItem(int position) { return catArray.get(position); }
            @Override public long getItemId(int position) { return position; }
        }


        // Init ListView and set its adapter
        ListView catListView =  findViewById(R.id.catCategoriesListView);
        catListView.setAdapter(new ListAdapter(Categories.this, catArray));
        catListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Configs.categoryStr = catArray.get(position);
                Log.i("log-", "SELECTED CATEGORY: " + Configs.categoryStr);
        }});

    }




}// @end
