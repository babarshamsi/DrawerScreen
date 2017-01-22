package invision.com.vetsplusmore.BaseActivities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;
import invision.com.vetsplusmore.Activities.AboutAppActivity;
import invision.com.vetsplusmore.Activities.CallVetActivity;
import invision.com.vetsplusmore.Activities.HomeActivity2;
import invision.com.vetsplusmore.Activities.LostAndFoundPetsActivity;
import invision.com.vetsplusmore.Activities.MyProfileActivity;
import invision.com.vetsplusmore.Activities.NewLoginActivity;
import invision.com.vetsplusmore.Activities.NewPetLicensingActivity;
import invision.com.vetsplusmore.Activities.NewPetServicesActivity;
import invision.com.vetsplusmore.Activities.NewRegistrationActivity;
import invision.com.vetsplusmore.Activities.PackagesActivity;
import invision.com.vetsplusmore.Activities.PetAdoptionActivity;
import invision.com.vetsplusmore.Activities.PetLicensingActivity;
import invision.com.vetsplusmore.Activities.RewardsActivity;
import invision.com.vetsplusmore.Common.CommonMethod;
import invision.com.vetsplusmore.Common.Constants;
import invision.com.vetsplusmore.CrashReport.CustomUncaughtExceptionHandler;
import invision.com.vetsplusmore.Fragments.CallVetFragment;
import invision.com.vetsplusmore.Fragments.PetLicensingFragment;
import invision.com.vetsplusmore.Fragments.PetPackagesFragment;
import invision.com.vetsplusmore.Fragments.ViewPetFragment;

import invision.com.vetsplusmore.Model.Pet;
import invision.com.vetsplusmore.Model.User;
import invision.com.vetsplusmore.R;
import invision.com.vetsplusmore.util.Permissions;
import invision.com.vetsplusmore.util.PictureUpload;

/**
 * Created by Lenovo on 11/17/2016.
 */
//
public class DrawerBaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    Context context;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
    FrameLayout frameLayout;
    LinearLayout content;
    private NavigationView navigationView;
    CircleImageView nav_user_image;

    private static AsyncHttpClient client = new AsyncHttpClient();

    int All_PERMISSIONS = 1;
    DrawerBaseActivity drawerBaseActivity;

    private static Bitmap storeBitmap;
    private static final int SELECT_PHOTO = 1, TAKE_PICTURE = 2;
    public static Uri selectedUri, imageUri;
    private String picturePath = "";
    private boolean fromCamera = true;
    private static String imagePathStore;

    PictureUpload pictureUpload;

    //public static final String VIEW_USER_URL = Constants.BASE_URL + "get_user_profile.php?userId=";

    public  String VIEW_USER_URL = Constants.MAIN_BASE_URL+"api/Users/"+Constants.userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base_drawer);

        Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler(this));

        context = DrawerBaseActivity.this;

        InitViews();



        String PERMISSIONS[] = {Manifest.permission.INTERNET};
//,Manifest.permission.WRITE_EXTERNAL_STORAGE
        Permissions permissions = new Permissions(context);

        if (!permissions.haspermissions(context,PERMISSIONS))
            ActivityCompat.requestPermissions(this,PERMISSIONS,All_PERMISSIONS);



         toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        if (CommonMethod.isOnline(this)) {
            //   Log.e("User ID","User ID :" + Constants.userID);
            client.addHeader("Content-Type","application/x-www-form-urlencoded");
            client.addHeader("Accept","application/json");

            client.get(VIEW_USER_URL, mViewUserHandler);
        } else {
            CommonMethod.showDialog(this, getResources().getString(R.string.internet_not_available));
        }

    }

    public void InitViews(){

        content = (LinearLayout) findViewById(R.id.activity_blank);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        frameLayout = (FrameLayout) findViewById(R.id.fl_content_frame);

        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(DrawerBaseActivity.this);
        navigationView.setItemIconTintList(null);

        final View hView =  navigationView.getHeaderView(0);
          nav_user_image = (CircleImageView) hView.findViewById(R.id.iv_sideMenu_Image);

     //  drawerBaseActivity = new DrawerBaseActivity();

//        nav_user_image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                 pictureUpload = new PictureUpload(context,DrawerBaseActivity.this,hView,nav_user_image);
//                pictureUpload.showImageDialog();
//
//              //  pictureUpload.savePhotoIntoSdcard();
//
//            }
//        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (resultCode == Activity.RESULT_OK) {
//            storeBitmap = null;
//            if (requestCode == TAKE_PICTURE) {
//                System.out.println("Take Pic camera");
//               imageUri = pictureUpload.getImageUri();
//
//                selectedUri = imageUri;
//
//                Log.i("ProfileActivity", "notifyChange");
//                getContentResolver().notifyChange(selectedUri, null);
//
//                try {
//                    Bitmap bt = pictureUpload.decodeSampledBitmapFromUri(selectedUri, 150, 150);
//                    Matrix matrix = new Matrix();
//                    Bitmap resizedBitmap = Bitmap.createBitmap(bt, 0, 0, bt.getWidth(), bt.getHeight(), matrix, true);
//                    pictureUpload.savePhotoIntoSdcard(resizedBitmap);
//
//                    if (resizedBitmap != null) {
//                        Log.i("ProfileActivity", "resized bitmap set");
//                        nav_user_image.setImageBitmap(resizedBitmap);
//                    } else {
//                        nav_user_image.setImageBitmap(bt);
//                    }
//
////                    mAvatarView.setVisibility(View.VISIBLE);
////                    btnChange.setVisibility(View.VISIBLE);
////                    btnRemove.setVisibility(View.VISIBLE);
////                    btnUpload.setVisibility(View.GONE);
//
//                    //picChanged = true;
//                    //fromCamera = true;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            } else if (requestCode == SELECT_PHOTO) {
//                // Code to show image in image view
//                if (data != null) {
//                    selectedUri = data.getData();
//                    String[] filePathColumn2 = {MediaStore.Images.Media.DATA};
//
//                    Cursor cursor1 = context.getContentResolver().query(selectedUri,
//                            filePathColumn2, null, null, null);
//                    cursor1.moveToFirst();
//
//                    int columnIndex1 = cursor1.getColumnIndex(filePathColumn2[0]);
//                    picturePath = cursor1.getString(columnIndex1);
//                    cursor1.close();
//
//                    fromCamera = false;
//
//                    float density = getResources().getDisplayMetrics().density;
//                    int width = (int) (100 * density);
//                    int height = (int) (100 * density);
//
//                    try {
//                        Bitmap bt = pictureUpload.decodeSampledBitmapFromUri(selectedUri, width, height);
//                        Matrix matrix = new Matrix();
//                        Bitmap resizedBitmap = Bitmap.createBitmap(bt, 0, 0, bt.getWidth(), bt.getHeight(), matrix, true);
//
//                        //						mAvatarView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
//                        nav_user_image.setBackgroundResource(android.R.color.transparent);
//                        //  ivProfilePic.setImageBitmap(null);
//                        // ivProfilePic.setImageResource(R.drawable.avatar);
//                        nav_user_image.setImageBitmap(resizedBitmap);
////                        mAvatarView.setVisibility(View.VISIBLE);
////                        btnChange.setVisibility(View.VISIBLE);
////                        btnRemove.setVisibility(View.VISIBLE);
////                        btnUpload.setVisibility(View.GONE);
//                        storeBitmap = resizedBitmap;
//                      //  picChanged = true;
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            } /*else if (requestCode == REMOVE_PHOTO){
//                ivProfilePic.setImageResource(R.drawable.avatar);
//            }*/
//
//            if (storeBitmap != null) {
//                final Bitmap finalBitmap = storeBitmap;
//                new AsyncTask<Void, Void, Boolean>() {
//
//                    @Override
//                    protected void onPreExecute() {
//
//                    }
//
//                    @Override
//                    protected Boolean doInBackground(Void... voids) {
//
//                        Boolean res = true;
//
//                        FileOutputStream out;
//                        try {
//
//                            String path = imagePathStore + "/userImage";
//                            final File direct = new File(path);
//                            if (!direct.exists()) {
//                                if (!direct.mkdirs()) {
//                                    return false;
//                                }
//                            }
//                            File file = new File(imagePathStore + "/userImage/" + Constants.userID + ".png");
//                            out = new FileOutputStream(file);
//                        } catch (FileNotFoundException e) {
//                            return false;
//                        }
//                        try {
//                            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
//                            out.flush();
//                        } catch (Exception e) {
//                            System.gc();
//                            res = false;
//                        } finally {
//                            try {
//                                out.close();
//                            } catch (Throwable ignore) {
//
//                            }
//                        }
//
//                        return res;
//                    }
//
//                    @Override
//                    protected void onPostExecute(Boolean aBoolean) {
//
//                        if (aBoolean) {
//                            CommonMethod.setPreferncesBoolean(context, Constants.PREF_KEY_PROFILE_PIC_EXIST + Constants.userID, true);
//                        }
//
//                    }
//                }.execute();
//            }
//
//        }


    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (All_PERMISSIONS) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                       {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    if ( !(grantResults[0] == PackageManager.PERMISSION_GRANTED))
                    {
  //                        finish();
//                        Intent intent = new Intent(DrawerBaseActivity.this,NewLoginActivity.class);
//                        startActivity(intent);
                        Toast.makeText(DrawerBaseActivity.this, "Permission denied to access your Internet", Toast.LENGTH_SHORT).show();
                    }
//                    else if (!(grantResults[1] == PackageManager.PERMISSION_GRANTED)){
//
//                        finish();
//                        Toast.makeText(context,"Permission denied to access Camera",Toast.LENGTH_SHORT).show();
//
//                    }

                }
                return;
            }

        }
    }

    private AsyncHttpResponseHandler mViewUserHandler = new AsyncHttpResponseHandler() {

        ProgressDialog progressDialog = null;

        @Override
        public void onStart() {
            super.onStart();
          //  progressDialog = new ProgressDialog(context);
           // progressDialog.setMessage("Loading user info from server..");
            //progressDialog.show();
        }

        @Override
        public void onFinish() {
            super.onFinish();
        }

        @Override
        public void onSuccess(int StatusCode,String content) {
            //progressDialog.dismiss();
            Log.e("onSuccess","UserID"+ Constants.userID);
            Log.e("onSuccess","Content : " + content);
            if (StatusCode == 200) {
                try {
                   final String  userName ;
                    final String imagepath;
                    JSONObject jsonobject = new JSONObject(content);
                    userName = jsonobject.getString("firstname");
                    imagepath = jsonobject.getString("imagePath");



                    try {
                        View hView =  navigationView.getHeaderView(0);
                        final CircleImageView nav_user_image = (CircleImageView) hView.findViewById(R.id.iv_sideMenu_Image);
                        final TextView nav_user = (TextView)hView.findViewById(R.id.tv_sideMenu_Name);
//                        nav_user.setText(name);
                     //   final  Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    //    nav_user_image.setImageBitmap(bmp);

                        new Thread(new Runnable(){
                            @Override
                            public void run() {
                                try {
                                    Log.e("onSuccess","berfore try : " + userName);
                                    nav_user.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.e("onSuccess","Name : " + userName);
                                            nav_user.setText(userName);
                                        }
                                    });
                                }catch (Exception e){}

                                try{
                                    final URL url = new URL(imagepath);
                                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                    final Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                    conn.disconnect();
                                    nav_user_image.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            nav_user_image.setImageBitmap(bmp);
                                        }
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                        navigationView.removeView(hView);
                    }

                    catch (Exception e){
                        e.printStackTrace();
                        Log.e("onSuccess","Catch : " + e.toString());

                    }finally{
                    }
                } catch (Exception e1) {
                    Log.e("onSuccess","Catch e1 : " + e1.toString());
                    e1.printStackTrace();

                    try{
                        final URL url = new URL(Constants.ImagePath);


                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        try {
                            View hView =  navigationView.getHeaderView(0);
                            final CircleImageView nav_user_image = (CircleImageView) hView.findViewById(R.id.iv_sideMenu_Image);
                            final TextView nav_user = (TextView)hView.findViewById(R.id.tv_sideMenu_Name);

                            new Thread(new Runnable(){
                                @Override
                                public void run() {
                                    try {
                                        final  Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        nav_user.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                nav_user.setText("User");
                                            }
                                        });
                                        nav_user_image.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                nav_user_image.setImageBitmap(bmp);
                                            }
                                        });
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }
                        catch (Exception e){
                            e.printStackTrace();

                        }finally{
                            conn.disconnect();
                        }
                    }catch (Exception d){

                    }
                }


//                Log.e("onSuccess","Nameeeee : " + name);
//                Log.e("Array","Name:" +name);
//                String[] arr;
//                View hView =  navigationView.getHeaderView(0);
//                TextView nav_user = (TextView)hView.findViewById(R.id.tv_sideMenu_Name);
//                CircleImageView nav_user_image = (CircleImageView) hView.findViewById(R.id.iv_sideMenu_Image);
//                nav_user_image.setImageResource(R.drawable.menu_avatar);
//                nav_user.setText(name);

             //   int lenght = mainJSONArray.length();
              //  Log.e("onSuccess","Lenght : " + lenght);
//                    JSONObject jsonobject = mainJSONArray.getJSONObject(0);
//
//                    String name =   jsonobject.getString("username");
//                    String myname = name;
                //   int len = mainJSONArray.length();
               // for (int i = 0; i < mainJSONArray.length(); i++) {
                  //  mPetsList.add(new Pet(mainJSONArray.getJSONObject(i)));
                   // JSONObject jsonobject = mainJSONArray.getJSONObject(i);

                  //  JSONObject jsonobject = mainJSONArray.getJSONObject(i);
                 //   String name = jsonobject.getString("username");

                    //String url = jsonobject.getString("url");

                //     new User(mainJSONArray.getJSONObject(i));

                 //   String name = jsonobject.getString("username");
                 //   String myname = name;
                  //  String url = jsonobject.getString("url");





            } else {

                Log.e("Error","Content : " + content);

            }


//            progressDialog.dismiss();
        }

        @Override
        public void onFailure(Throwable error, String content) {
            super.onFailure(error, content);
        //    progressDialog.dismiss();
            CommonMethod.showDialog(context, "ERROR", "Unable to get response from server");
        }
    };


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            finish();
        } else {
            finish();
            super.onBackPressed();
        }
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.home_activity2, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = null;

        if (id == R.id.nav_callVet) {


         //   content.setVisibility(View.VISIBLE);
//            fragment = new CallVetFragment();
            startActivity(new Intent(DrawerBaseActivity.this, AboutAppActivity.class));
            finish();


            toolbar.setTitle("Call Vet");

        } else if (id == R.id.nav_petLicensing) {

           // view_pet_licensing = (RelativeLayout) findViewById(R.id.activity_new_petlicensing);

//            fragment = new PetPackagesFragment();
            //startActivity(new Intent(DrawerBaseActivity.this, PackagesActivity.class));
            Intent intent = new Intent(DrawerBaseActivity.this,NewPetLicensingActivity.class);
            intent.putExtra("PetLicensing","HomeLicence");
            startActivity(intent);
            finish();
            Prefs.putString("name","HomeActivity2");    // is not done by me..
            toolbar.setTitle("Pet Licensing");

        } else if (id == R.id.nav_myPet) {

            Intent intent = new Intent(DrawerBaseActivity.this,NewPetLicensingActivity.class);
            intent.putExtra("PetLicensing","HomeAddPet");
            startActivity(intent);
            finish();

        } else if (id == R.id.nav_petServices) {

            startActivity(new Intent(DrawerBaseActivity.this, NewPetServicesActivity.class));
            finish();

        } else if (id == R.id.nav_lostOrFoundPet) {

            startActivity(new Intent(DrawerBaseActivity.this, LostAndFoundPetsActivity.class));
            finish();

        } else if (id == R.id.nav_reward) {

            startActivity(new Intent(DrawerBaseActivity.this, RewardsActivity.class));
            finish();

        }
        else if (id == R.id.nav_profile){

            Intent intent = new Intent(DrawerBaseActivity.this,MyProfileActivity.class);
            startActivity(intent);
            finish();


        }


        if (fragment != null){

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fl_content_frame,fragment).commit();
//
//
//
//
//
       }

        else {

            Log.e("MainActivity", "Error in creating fragment");

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
