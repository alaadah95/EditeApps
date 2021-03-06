package domain.com.recipes;

/*-----------------------------------

    - Recipes -

    created by cubycode ©2017
    All Rights reserved

-----------------------------------*/

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;

public class RecipeDetails extends AppCompatActivity {

    int Width , Hight ;
    /* Views */
    ImageView coverImage, avatarImg;
    RelativeLayout recipeLayout;

    TextView titleTxt, recipeTitleTxt, categoryTxt, likesTxt, userFullNameTxt, aboutReceipeTxt,
            difficultyTxt, cookingTxt, bakingTxt, restingTxt, ingredientsTxt,
    preparationTxt, videoTitleTxt, commentsTxt,Price,priceNumber;

    WebView videoWebView;

    Button addToShoppingButt, likeButt, commentButt;



    /* Variables */
    ParseObject recipeObj;
    List<ParseObject>likesArray;
    MarshMallowPermission mmp = new MarshMallowPermission(this);





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_details);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Hide ActionBar
        getSupportActionBar().hide();


        // Init views
        avatarImg =  findViewById(R.id.crAvatarImg);
        coverImage =  findViewById(R.id.rdCoverImg);

        likeButt = findViewById(R.id.rdLikeButt);
        commentButt = findViewById(R.id.rdCommentButt);
        addToShoppingButt =  findViewById(R.id.rdAddToShoppingButt);

        titleTxt = findViewById(R.id.rdTitleTxt);
        titleTxt.setTypeface(Configs.typeWriter);
        recipeTitleTxt = findViewById(R.id.rdRecipeTitleTxt);
        categoryTxt = findViewById(R.id.rdCategoryTxt);
        difficultyTxt = findViewById(R.id.rdDifficultyTxt);
        likesTxt = findViewById(R.id.rdLikesTxt);
        commentsTxt = findViewById(R.id.rdCommentsTxt);
        userFullNameTxt = findViewById(R.id.rdUserFullNameTxt);
        aboutReceipeTxt = findViewById(R.id.rdAboutRecipeTxt);
        cookingTxt = findViewById(R.id.rdCookingTxt);
        bakingTxt = findViewById(R.id.rdBakingTxt);
        restingTxt = findViewById(R.id.rdRestingTxt);
        videoTitleTxt = findViewById(R.id.rdVideoTitleTxt);
        preparationTxt = findViewById(R.id.rdPreparationTxt);
        ingredientsTxt = findViewById(R.id.rdIngredientsTxt);
        Price = findViewById(R.id.priceNumber);
        priceNumber = findViewById(R.id.priceNumber);

        videoWebView = (WebView) findViewById(R.id.rdVideoWebView);
        videoWebView.getSettings().setJavaScriptEnabled(true);
        videoWebView.setWebViewClient(new WebViewClient());




        // Get objectID from previous .java
        Bundle extras = getIntent().getExtras();
        String objectID = extras.getString("objectID");
        recipeObj = ParseObject.createWithoutData(Configs.RECIPES_CLASS_NAME, objectID);
        try { recipeObj.fetchIfNeeded().getParseObject(Configs.RECIPES_CLASS_NAME);


            // Show recipe details
            showRecipeDetails();


            // MARK: - SHARE RECIPE BUTTON ------------------------------------
            Button shareButt = findViewById(R.id.rdShareButt);
            shareButt.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  if (!mmp.checkPermissionForWriteExternalStorage()) {
                      mmp.requestPermissionForWriteExternalStorage();
                  } else {
                      Bitmap bitmap = ((BitmapDrawable) coverImage.getDrawable()).getBitmap();
                      Uri uri = getImageUri(RecipeDetails.this, bitmap);
                      Intent intent = new Intent(Intent.ACTION_SEND);
                      intent.setType("image/jpeg");
                      intent.putExtra(Intent.EXTRA_STREAM, uri);
                      intent.putExtra(Intent.EXTRA_TEXT, "I love this Recipe: " + recipeObj.getString(Configs.RECIPES_TITLE) + ", found on #" + getString(R.string.app_name));
                      startActivity(Intent.createChooser(intent, "Share on..."));
                  }
             }});


        } catch (ParseException e) { e.printStackTrace(); }




        // MARK: - BACK BUTTON ------------------------------------
        Button backButt = findViewById(R.id.rdBackButt);
        backButt.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) { finish(); }});




        // Init AdMob banner
        /*
        AdView mAdView =  findViewById(R.id.admobBanner);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
*/

    }// end onCreate()






    // MARK: - SHOW RECIPE DETAILS ---------------------------------------------------------------
    void showRecipeDetails() {

        // Get userPointer
        recipeObj.getParseObject(Configs.RECIPES_USER_POINTER).fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            public void done(final ParseObject userPointer, ParseException e) {
                if (e == null) {

                    // Get User's details
                    if (userPointer.getString(Configs.USER_JOB) != null) { userFullNameTxt.setText("Made by: " + userPointer.getString(Configs.USER_FULLNAME) + ", " + userPointer.getString(Configs.USER_JOB));
                    } else { userFullNameTxt.setText("Made by: " + userPointer.getString(Configs.USER_FULLNAME)); }

                    // Get avatar image
                    ParseFile fileObject2 = userPointer.getParseFile(Configs.USER_AVATAR);
                    if (fileObject2 != null ) {
                        fileObject2.getDataInBackground(new GetDataCallback() {
                            public void done(byte[] data, ParseException error) {
                                if (error == null) {
                                    Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    if (bmp != null) {
                                        avatarImg.setImageBitmap(bmp);
                    }}}});}


                    // MARK: - GO TO USER'S PROFILE ------------------------------------------------
                    avatarImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(RecipeDetails.this, OtherUserProfile.class);
                            Bundle extras = new Bundle();
                            extras.putString("userID", userPointer.getObjectId());
                            i.putExtras(extras);
                            startActivity(i);
                    }});



                    // Get Recipe cover image
                    ParseFile fileObject = recipeObj.getParseFile(Configs.RECIPES_COVER);
                    if (fileObject != null ) {
                        fileObject.getDataInBackground(new GetDataCallback() {
                            public void done(byte[] data, ParseException error) {
                                if (error == null) {
                                    Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    if (bmp != null) {
                                        coverImage.setImageBitmap(bmp);
                    }}}});}


                    // Get Recipe details
                    recipeTitleTxt.setText(recipeObj.getString(Configs.RECIPES_TITLE));
                    categoryTxt.setText(recipeObj.getString(Configs.RECIPES_CATEGORY));
                    Price.setText(recipeObj.getString(Configs.RECIPES_price));
                    // Get Likes
                    if (recipeObj.getNumber(Configs.RECIPES_LIKES) != null) {
                        int likes = recipeObj.getInt(Configs.RECIPES_LIKES);
                        likesTxt.setText(Configs.roundThousandsIntoK(likes));
                    } else { likesTxt.setText("0"); }


                    // Get Comments
                    if (recipeObj.getNumber(Configs.RECIPES_COMMENTS) != null) {
                        int comments = recipeObj.getInt(Configs.RECIPES_COMMENTS);
                        commentsTxt.setText(Configs.roundThousandsIntoK(comments));
                    } else { commentsTxt.setText("0"); }


                    aboutReceipeTxt.setText(recipeObj.getString(Configs.RECIPES_ABOUT));
                    difficultyTxt.setText("Difficulty: " + recipeObj.getString(Configs.RECIPES_DIFFICULTY));

                    cookingTxt.setText("Cooking:\n" + recipeObj.getString(Configs.RECIPES_COOKING));
                    bakingTxt.setText("Baking:\n" + recipeObj.getString(Configs.RECIPES_BAKING));
                    restingTxt.setText("Serving:\n" + recipeObj.getString(Configs.RECIPES_RESTING));


                    // Get Recipe video
                    if (recipeObj.getString(Configs.RECIPES_YOUTUBE) != null) {
                        if (!recipeObj.getString(Configs.RECIPES_YOUTUBE).matches("") ) {
                            String youtubeLink = recipeObj.getString(Configs.RECIPES_YOUTUBE);
                            final String videoId = youtubeLink.replace("https://youtu.be/", "");

                            String embedHTML = "<iframe width='42-0'+ Width  height='200' src='https://www.youtube.com/embed/" + videoId + "?rel=0&amp;controls=0&amp;showinfo=0' frameborder='0' allowfullscreen></iframe>";
                            videoWebView.loadData(embedHTML, "text/html", null);

                        // Hide videoWebView in case of no video
                        } else { videoWebView.setVisibility(View.INVISIBLE); }
                    } else { videoWebView.setVisibility(View.INVISIBLE); }


                    // Get video title
                    if (recipeObj.getString(Configs.RECIPES_VIDEO_TITLE) != null ) {
                        if (!recipeObj.getString(Configs.RECIPES_VIDEO_TITLE).matches("")) {
                            videoTitleTxt.setText(recipeObj.getString(Configs.RECIPES_VIDEO_TITLE));
                        } else { videoTitleTxt.setText("No video Available"); }
                    } else { videoTitleTxt.setText("No video Available"); }


                    // Get Ingredients and make an array (for your Shopping List)
                    ingredientsTxt.setText(recipeObj.getString(Configs.RECIPES_INGREDIENTS));

                    // Get Preparation Steps
                    preparationTxt.setText(recipeObj.getString(Configs.RECIPES_PREPARATION));





                    // MARK: - ADD INGREDIENTS TO SHOPPING LIST BUTTON ------------------------------------
                    addToShoppingButt.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View view) {


                          SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(RecipeDetails.this);
                          Configs.shoppingString = prefs.getString("shoppingString", "");

                          Configs.shoppingString = Configs.shoppingString + "\n" + recipeTitleTxt.getText().toString();

                          prefs.edit().putString("shoppingString", Configs.shoppingString).apply();
                          Log.i("log-", "SHOPPING STRING: \n" + Configs.shoppingString);


                          Configs.simpleAlert("These ingredients have been saved into your Shopping List!", RecipeDetails.this);
                    }});




                    // MARK: - COMMENTS BUTTON ------------------------------------
                    Button commButt = findViewById(R.id.rdCommentButt);
                    commButt.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View view) {
                          if (ParseUser.getCurrentUser().getUsername() != null) {
                              Intent i = new Intent(RecipeDetails.this, Comments.class);
                              Bundle extras = new Bundle();
                              extras.putString("objectID", recipeObj.getObjectId());
                              i.putExtras(extras);
                              startActivity(i);
                          } else {
                              AlertDialog.Builder alert = new AlertDialog.Builder(RecipeDetails.this);
                              alert.setMessage("You must login/sign up to comment a recipe!")
                                      .setTitle(R.string.app_name)
                                      .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                          @Override
                                          public void onClick(DialogInterface dialog, int which) {
                                              startActivity(new Intent(RecipeDetails.this, Login.class));
                                          }})
                                      .setNegativeButton("Cancel", null)
                                      .setIcon(R.drawable.logo);
                              alert.create().show();
                          }
                     }});




                    // MARK: - LIKE RECIPE BUTTON ------------------------------------
                    Button likeButt = findViewById(R.id.rdLikeButt);
                    likeButt.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View view) {

                          final ParseUser currUser = ParseUser.getCurrentUser();

                          // USER IS LOGGED IN
                          if (currUser.getUsername() != null) {

                              // Query Likes
                              ParseQuery<ParseObject> query = ParseQuery.getQuery(Configs.LIKES_CLASS_NAME);
                              query.whereEqualTo(Configs.LIKES_LIKED_BY,currUser);
                              query.whereEqualTo(Configs.LIKES_RECIPE_LIKED, recipeObj);
                              query.findInBackground(new FindCallback<ParseObject>() {
                                  public void done(List<ParseObject> objects, ParseException error) {
                                      if (error == null) {
                                          likesArray = objects;

                                          ParseObject likesObj = new ParseObject(Configs.LIKES_CLASS_NAME);

                                          if (likesArray.size() == 0) {

                                              // LIKE RECIPE ------------------------------
                                              recipeObj.increment(Configs.RECIPES_LIKES, 1);
                                              recipeObj.saveInBackground();
                                              int likeInt = recipeObj.getInt(Configs.RECIPES_LIKES);
                                              likesTxt.setText(Configs.roundThousandsIntoK(likeInt));

                                              likesObj.put(Configs.LIKES_LIKED_BY, currUser);
                                              likesObj.put(Configs.LIKES_RECIPE_LIKED, recipeObj);
                                              likesObj.saveInBackground(new SaveCallback() {
                                                  @Override
                                                  public void done(ParseException e) {
                                                      if (e == null) {
                                                          Configs.simpleAlert("You've liked this recipe and saved into your Account!", RecipeDetails.this);

                                                          // Send push notification
                                                          final String pushMessage = currUser.getString(Configs.USER_FULLNAME) + " liked your recipe: " + recipeObj.getString(Configs.RECIPES_TITLE);

                                                          HashMap<String, Object> params = new HashMap<String, Object>();
                                                          params.put("someKey", userPointer.getObjectId());
                                                          params.put("data", pushMessage);

                                                          ParseCloud.callFunctionInBackground("pushAndroid", params, new FunctionCallback<String>() {
                                                              @Override
                                                              public void done(String object, ParseException e) {
                                                                  if (e == null) {

                                                                      Log.i("log-", "PUSH SENT TO: " + userPointer.getString(Configs.USER_USERNAME)
                                                                              + "\nMESSAGE: "
                                                                              + pushMessage);

                                                                      // Error in Cloud Code
                                                                  } else {
                                                                      Configs.simpleAlert(e.getMessage(), RecipeDetails.this);
                                                          }}});

                                                          // Save activity
                                                          ParseObject actObj = new ParseObject(Configs.ACTIVITY_CLASS_NAME);
                                                          actObj.put(Configs.ACTIVITY_CURRENT_USER, userPointer);
                                                          actObj.put(Configs.ACTIVITY_OTHER_USER, currUser);
                                                          actObj.put(Configs.ACTIVITY_TEXT, pushMessage);
                                                          actObj.saveInBackground();
                                                      }}});



                                              // UNLIKE RECIPE --------------------------
                                          } else if (likesArray.size() > 0) {
                                              recipeObj.increment(Configs.RECIPES_LIKES, -1);
                                              recipeObj.saveInBackground();
                                              int likeInt = recipeObj.getInt(Configs.RECIPES_LIKES);
                                              likesTxt.setText(Configs.roundThousandsIntoK(likeInt));

                                              likesObj = likesArray.get(0);
                                              likesObj.deleteInBackground(new DeleteCallback() {
                                                  @Override
                                                  public void done(ParseException e) {
                                                      if (e == null) {
                                                          Configs.simpleAlert("You've unliked this recipe", RecipeDetails.this);
                                              }}});
                                          }


                                          // Error in query Likes
                                      } else {
                                          Configs.simpleAlert(error.getMessage(), RecipeDetails.this);
                                      }}});




                          // USER IS NOT LOGGED IN/REGISTERED
                          } else {
                              AlertDialog.Builder alert = new AlertDialog.Builder(RecipeDetails.this);
                              alert.setMessage("You must login/sign up to like a recipe!")
                                      .setTitle(R.string.app_name)
                                      .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                          @Override
                                          public void onClick(DialogInterface dialog, int which) {
                                              startActivity(new Intent(RecipeDetails.this, Login.class));
                                      }})
                                      .setNegativeButton("Cancel", null)
                                      .setIcon(R.drawable.logo);
                              alert.create().show();
                          }

                     }});






                    // MARK: - REPORT RECIPE BUTTON ------------------------------------
                    Button repButt = findViewById(R.id.rdReportButt);
                    repButt.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View view) {

                          AlertDialog.Builder alert = new AlertDialog.Builder(RecipeDetails.this);
                          final EditText editTxt = new EditText(RecipeDetails.this);
                          editTxt.setHint(" type here...");
                          alert.setMessage("Tell us briefly why you're reporting this Recipe")
                                  .setView(editTxt)
                                  .setTitle(R.string.app_name)
                                  .setIcon(R.drawable.logo)
                                  .setPositiveButton("Report", new DialogInterface.OnClickListener() {
                                      @Override
                                      public void onClick(DialogInterface dialog, int which) {
                                          String textStr = editTxt.getText().toString();

                                          recipeObj.put(Configs.RECIPES_IS_REPORTED, true);
                                          recipeObj.put(Configs.RECIPES_REPORT_MESSAGE, textStr);
                                          recipeObj.saveInBackground(new SaveCallback() {
                                              @Override
                                              public void done(ParseException e) {

                                                  AlertDialog.Builder alert = new AlertDialog.Builder(RecipeDetails.this);
                                                  alert.setMessage("Thanks for reporting this recipe!.\nWe'll check it out within 24h. Go back and hit the Refresh button")
                                                      .setTitle(R.string.app_name)
                                                      .setPositiveButton("Go back", new DialogInterface.OnClickListener() {
                                                          @Override
                                                          public void onClick(DialogInterface dialog, int which) {
                                                              finish();
                                                      }})
                                                      .setIcon(R.drawable.logo);
                                                  alert.create().show();
                                          }});
                                  }})
                                  .setNegativeButton("Cancel", null);
                          alert.create().show();


                      }});


                // error
                } else { Configs.simpleAlert(e.getMessage(), RecipeDetails.this);

        }}});// end userPointer

    }





    // Method to get URI of a stored image
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "image", null);
        return Uri.parse(path);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final WebView layout = (WebView) findViewById(R.id.rdVideoWebView);

        ViewTreeObserver viewTreeObserver = layout.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                    Width = layout.getWidth();

                    Hight = layout.getHeight();
                }
            });
        }
    }

}// @end
