package domain.com.recipes;

/*-----------------------------------

    - Recipes -

    created by cubycode ©2017
    All Rights reserved

-----------------------------------*/

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.SharedElementCallback;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;

import android.widget.EditText;
import android.widget.ListView;

import android.widget.TextView;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;



public class Shopping extends AppCompatActivity {

    /* Views */
    ListView shoppingListView;
  Button Order ;
    /* Variables */
    List<String> ingredientsArray;
    SharedPreferences prefs;
    ListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Hide ActionBar
        getSupportActionBar().hide();
        Order = findViewById(R.id.Order);
        Order.setOnClickListener(new View.OnClickListener() {


                                     @Override
                                     public void onClick(View view) {

                                         final Dialog dialog = new Dialog(Shopping.this);
                                         dialog.setContentView(R.layout.order_dialog);

                                         final EditText Tablet = dialog.findViewById(R.id.Order_name);
                                         Button Btn_send = dialog.findViewById(R.id.Btn_send);

                                         Btn_send.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View view) {
                                                 Configs.showPD("Please Wait",Shopping.this);
                                                 if (Tablet.getText().toString().isEmpty()){

                                                     dialog.dismiss();
                                                     Configs.simpleAlert("Please Enter Order Name ",Shopping.this);
                                                     Configs.hidePD();

                                                 }
                                                 else {


                                                     DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                     String date = df.format(Calendar.getInstance().getTime());
                                                     // Write a message to the database
                                                     FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                     DatabaseReference myRef = database.getReference("Kitchen").child(

                                                             Tablet.getText().toString()+ "  " + date

                                                     );
                                                     for (int i = 0 ; i <ingredientsArray.size(); i++  ) {
                                                         final int finalI = i;
                                                         myRef.push().setValue(ingredientsArray.get(i)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                             @Override
                                                             public void onSuccess(Void aVoid) {

                                                                 if ( finalI == (ingredientsArray.size()-1)) {
                                                                     Configs.simpleAlert(" Sent Successfully  ", Shopping.this);
                                                                     dialog.dismiss();

                                                                     Configs.hidePD();
                                                                    //clear And save
                                                                     ingredientsArray.clear();
                                                                     SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Shopping.this);
                                                                     prefs.edit().putString("shoppingString"," ").apply();

                                                                     //refresh
                                                                     adapter.notifyDataSetChanged();
                                                                     shoppingListView.deferNotifyDataSetChanged();


                                                                 }
                                                             }
                                                         });


                                                     }

                                                 }
                                             }
                                         });



                                         dialog.show();

 }
        });


                                         // Get shopping List
                                         prefs = PreferenceManager.getDefaultSharedPreferences(Shopping.this);
                                         Configs.shoppingString = prefs.getString("shoppingString", "");


                                         // Make an array of ingredients (to be shown in the shoppingListView)
                                         String[] one = Configs.shoppingString.split("\\r?\\n");
                                         ingredientsArray = new ArrayList<String>();
                                         ingredientsArray.addAll(Arrays.asList(one));
                                         ingredientsArray.remove(0);

                                         // Log.i("log-", "INGREDIENTS ARRAY: " + ingredientsArray);
                                         setupShoppingListView();


                                         // Init views
                                         TextView titleTxt = findViewById(R.id.shopTitleTxt);
                                         titleTxt.setTypeface(Configs.typeWriter);


                                         // Init TabBar buttons
                                         Button tab_one = findViewById(R.id.tab_one);
                                         Button tab_two = findViewById(R.id.tab_three);

                                         tab_one.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                 startActivity(new Intent(Shopping.this, Categories.class));
                                             }
                                         });

                                         tab_two.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                 startActivity(new Intent(Shopping.this, Account.class));
                                             }
                                         });


                                         // MARK: - CLEAR LIST BUTTON ------------------------------------
                                         Button clButt = findViewById(R.id.shopClearListButt);
                                         clButt.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View view) {
                                                 AlertDialog.Builder alert = new AlertDialog.Builder(Shopping.this);
                                                 alert.setMessage("Are you sure you want to clear this List?")
                                                         .setTitle(R.string.app_name)
                                                         .setPositiveButton("Clear", new DialogInterface.OnClickListener() {
                                                             @Override
                                                             public void onClick(DialogInterface dialog, int which) {
                                                                 if (ingredientsArray != null) {
                                                                     ingredientsArray.clear();
                                                                     shoppingListView.invalidateViews();
                                                                     shoppingListView.refreshDrawableState();

                                                                     // Save the shopping string
                                                                     prefs.edit().putString("shoppingString", "").apply();
                                                                 }
                                                             }
                                                         })
                                                         .setNegativeButton("Cancel", null)
                                                         .setIcon(R.drawable.logo);
                                                 alert.create().show();

                                             }
                                         });


                                         // MARK: - SHARE SHOPPING LIST BUTTON ------------------------------------
                                         Button shareButt = findViewById(R.id.shopShare);
                                         shareButt.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View view) {
                                                 Intent intent = new Intent(Intent.ACTION_SEND);
                                                 intent.setType("image/jpeg");
                                                 intent.putExtra(Intent.EXTRA_TEXT, "List of ingredients I need: \n\n" + Configs.shoppingString);
                                                 startActivity(Intent.createChooser(intent, "Share on..."));
                                             }
                                         });


                                         // Init AdMob banner
        /*
        AdView mAdView =  findViewById(R.id.admobBanner);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
*/
                                     }// end onCreate()





    // MARK: - SETUP SHOPPING LISTVIEW -------------------------------------------------------
    void setupShoppingListView() {



        // Init ListView and set its adapter
        shoppingListView =  findViewById(R.id.shopShoppingListView);
         adapter = new ListAdapter(Shopping.this, ingredientsArray);
        shoppingListView.setAdapter(adapter);


        // LONG PRESS -> DELETE AN ITEM -----------------------------------------
        shoppingListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {

                ingredientsArray.remove(position);
                adapter.notifyDataSetChanged();
                shoppingListView.deferNotifyDataSetChanged();
                shoppingListView.invalidateViews();
                shoppingListView.refreshDrawableState();


                prefs.edit().putString("shoppingString", "").apply();
                for (int i = 0 ; i < ingredientsArray.size(); i++) {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Shopping.this);
                    Configs.shoppingString = prefs.getString("shoppingString", "");
                    Configs.shoppingString = Configs.shoppingString + "\n" + ingredientsArray.get(i);
                    prefs.edit().putString("shoppingString", Configs.shoppingString).apply();

                }
           return true;
        }});
    }



    class ListAdapter extends BaseAdapter {
        private Context context;
        private ListAdapter(Context context, List<String> objects) {
            super();
            this.context = context;
        }

        // CONFIGURE CELL
        @Override
        public View getView(final int position, View cell, ViewGroup parent) {
            if (cell == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                assert inflater != null;
                cell = inflater.inflate(R.layout.cell_shopping_list, null);
            }

            // Get ingredient
            TextView ingrTxt = cell.findViewById(R.id.cslIngredientTxt);
            ingrTxt.setText(ingredientsArray.get(position));

            return cell;
        }
        @Override public int getCount() { return ingredientsArray.size(); }
        @Override public Object getItem(int position) { return ingredientsArray.get(position); }
        @Override public long getItemId(int position) { return position; }
    }

}// @end
