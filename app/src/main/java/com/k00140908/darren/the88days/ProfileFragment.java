package com.k00140908.darren.the88days;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.k00140908.darren.the88days.Model.User;
import com.k00140908.darren.the88days.RetrofitInterfaces.BackpackerAPI;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private static final int REQUEST_CODE = 9;
    private Button mTakePhoto;
    Button mGaleryPhoto;
    private ImageView mImageView;
    private static final String TAG = "upload";

    UserLocalStore userLocalStore;
    TextView etName, etAge, etUsername;


    private View mProgressView;
    private View mProfileView;

    final static String ENDPOINT ="https://the88days.azurewebsites.net/";
    User user = null;

    String mCurrentPhotoPath;

    static final int REQUEST_TAKE_PHOTO = 1;
    File photoFile = null;

    boolean backgroundTaskInProgress =false;

    View v;
    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_profile, container, false);

        mListener.SetupToolbar(0,"My Profile");
        
        mImageView = (ImageView) v.findViewById(R.id.profilephoto);

        mImageView.setOnClickListener(this);

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.profile_view_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.loadSettings();
                //Toast.makeText(getActivity(), "Leave feedback", Toast.LENGTH_SHORT).show();
            }
        });

        etUsername = (TextView) v.findViewById(R.id.textViewUserName);
        etName = (TextView) v.findViewById(R.id.textViewNationality);
        etAge = (TextView) v.findViewById(R.id.textViewWorkStatus);

        userLocalStore = new UserLocalStore(getContext());

        User user = userLocalStore.getLoggedInUser();
        etUsername.setText(user.getUserName());
        etName.setText(user.getNationality());
        etAge.setText(user.getWorkStatus());


        mProfileView = v.findViewById(R.id.profile);
        mProgressView = v.findViewById(R.id.profile_photo_upload_progress);
        String urlThumnail = null;
        if (user.getProfilePhoto() != null) {
            urlThumnail = user.getProfilePhoto();
        }
        if (urlThumnail != "") {
            // Trigger the download of the URL asynchronously into the image view.
            Picasso.with(getContext())
                    .load(urlThumnail).error(R.drawable.ic_contact_picture)
                    .into(mImageView);
        }
        if (backgroundTaskInProgress==true)
        {
            showProgress(true);
        }
        return v;
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
                case R.id.profilephoto:
                selectImage();
                break;
        }
    }
    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Edit Profile Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    takePhoto();
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select Photo"),
                            REQUEST_CODE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    private void takePhoto() {
        dispatchTakePictureIntent();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (((requestCode == REQUEST_TAKE_PHOTO) || (requestCode == 9) ) && resultCode == Activity.RESULT_OK) {
            if(requestCode==REQUEST_TAKE_PHOTO) {
                setPic();
            }
            else {
                Uri selectedImageUri = data.getData();
                String[] projection = { MediaStore.MediaColumns.DATA };
                CursorLoader cursorLoader = new CursorLoader(getActivity(),selectedImageUri, projection, null, null,null);
                Cursor cursor =cursorLoader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                String selectedImagePath = cursor.getString(column_index);

                Bitmap bm;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(String.valueOf(selectedImagePath), options);
                final int REQUIRED_SIZE = 400;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                        && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeFile(selectedImagePath, options);
                mImageView.setImageBitmap(bm);


                try {
                    UploadPhoto(bm);
                    //sendPhoto(bm);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
    }
public void UploadPhoto(Bitmap bitmap) {
    backgroundTaskInProgress=true;
    showProgress(true);

    Bitmap photo = bitmap;//(Bitmap) data.getExtras().get("data");

    File imageFileFolder = new File(getActivity().getCacheDir(), "Avatar");
    if (!imageFileFolder.exists()) {
        imageFileFolder.mkdir();
    }

    FileOutputStream out = null;

    File imageFileName = new File(imageFileFolder, "avatar-" + System.currentTimeMillis() + ".jpg");
    try {
        out = new FileOutputStream(imageFileName);
        photo.compress(Bitmap.CompressFormat.JPEG, 100, out);
        out.flush();
    } catch (IOException e) {
        Log.e(TAG, "Failed to convert image to JPEG", e);
    } finally {
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to close output stream", e);
        }
    }

    TypedFile image = new TypedFile("image/jpeg", imageFileName);

    user = userLocalStore.getLoggedInUser();

    RestAdapter.Builder adapter = new RestAdapter.Builder()
            .setEndpoint(ENDPOINT)
            .setRequestInterceptor(new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {
                    request.addHeader("Authorization", "Bearer " + user.getAccess_token());
                }
            });

    BackpackerAPI api = adapter.build().create(BackpackerAPI.class);

    api.upload(image, new Callback<Object>() {


        @Override
        public void success(Object o, Response response) {
            showProgress(false);
            Picasso.with(getContext()).invalidate(user.getProfilePhoto());
            Picasso.with(getContext())
                    .load(user.getProfilePhoto()).error(R.drawable.ic_contact_picture)//.networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(mImageView);
            backgroundTaskInProgress=false;
            Toast.makeText(getActivity(), "Successfully Upload", Toast.LENGTH_LONG).show();
            //Log.e("Upload", "success");
        }

        @Override
        public void failure(RetrofitError error) {
            showProgress(false);
            Toast.makeText(getActivity(), "Sorry an error has occurred please check your internet connection and try again.", Toast.LENGTH_LONG).show();
            //Log.e("Upload", "error");
        }
    });
}

        private void dispatchTakePictureIntent() {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File

                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile));
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }
        }

        /**
         * http://developer.android.com/training/camera/photobasics.html
         */
        private File createImageFile() throws IOException {
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            String storageDir = Environment.getExternalStorageDirectory() + "/the88days";
            File dir = new File(storageDir);
            if (!dir.exists())
                dir.mkdir();

            File image = new File(storageDir + "/" + imageFileName + ".jpg");

            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPhotoPath = image.getAbsolutePath();
            Log.i(TAG, "photo path = " + mCurrentPhotoPath);
            return image;
        }

        private void setPic() {
            // Get the dimensions of the View
            int targetW = mImageView.getWidth();
            int targetH = mImageView.getHeight();

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor << 1;
            bmOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

            Matrix mtx = new Matrix();
            mtx.postRotate(90);
            // Rotating Bitmap
//            Bitmap rotatedBMP = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mtx, true);
//
//            if (rotatedBMP != bitmap)
//                bitmap.recycle();

            mImageView.setImageBitmap(bitmap);

            try {
                UploadPhoto(bitmap);
                //sendPhoto(rotatedBMP);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

        void SetupToolbar(int i, String s);

        void loadSettings();
    }
    /**
     * Shows the progress UI and hides the backpackeradapter form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mProfileView.setVisibility(show ? View.GONE : View.VISIBLE);
            mProfileView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProfileView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProfileView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
