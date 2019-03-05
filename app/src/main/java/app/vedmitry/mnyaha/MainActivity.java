package app.vedmitry.mnyaha;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "tag21221";
    private static final int REQUEST_CODE_SIGN_IN = 77;


    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
              //  .requestServerAuthCode()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            Log.e(TAG, "signInWithCredential: Success!");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user!=null){
                                Log.w(TAG, "user.getDisplayName()" + user.getDisplayName());
                                Log.w(TAG, "signInWithCredential: Success" + user.getEmail());
                                Log.w(TAG, "signInWithCredential: Success" + user.getPhoneNumber());
                                Log.w(TAG, "signInWithCredential: Success" + user.getPhotoUrl());
                                Log.w(TAG, "signInWithCredential: Success" + user.getProviderId());
                                Log.w(TAG, "signInWithCredential: Success" + user.getUid());

                            }

                            //     updateUI(user);
                        } else {
                            // Sign in fails
                            Log.w(TAG, "signInWithCredential: Failed!", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed!",
                                    Toast.LENGTH_SHORT).show();
                            //    updateUI(null);
                        }

                    }
                });
    }
    //eyJhbGciOiJSUzI1NiIsImtpZCI6ImNmMDIyYTQ5ZTk3ODYxNDhhZDBlMzc5Y2M4NTQ4NDRlMzZjM2VkYzEiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiIzNzU2ODI0NDUyMTItbzB2OGtnMHJ2ajU5NmVqbHR2dDB1aDlqYzAxMXVmbWQuYXBwcy5nb29


    //eyJhbGciOiJSUzI1NiIsImtpZCI6ImNmMDIyYTQ5ZTk3ODYxNDhhZDBlMzc5Y2M4NTQ4NDRlMzZjM2VkYzEiLCJ0eXAiOiJKV1QifQ.e



    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //  updateUI(currentUser);
    }


    @OnClick(R.id.button_out)
    public void signOut(View v) {
        // sign out Firebase
        mAuth.signOut();

        // sign out Google
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        Log.w(TAG, "Out! " + status);
                        //    updateUI(null);
                    }
                });
    }


    @OnClick(R.id.btn_sign_in)
    public void signIn() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(intent, REQUEST_CODE_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent();
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // successful -> authenticate with Firebase

                // tester - 4_rKPs_iSGg9E26rva68lDoErb5z8XG2veo0lyf-5JF5yXQ
                // dmitry sgCkuZhI4H5SE8pxtSKEEBwsunWc6ad5StQWQoofhQ3X_i0LRwTDMy_g2HAeMc9kwDLbw
                GoogleSignInAccount account = result.getSignInAccount();
                Log.w(TAG, "account.getIdToken()" + account.getIdToken());
                Log.w(TAG, "account.getId()" + account.getId());
                Log.w(TAG, "account.getServerAuthCode()" + account.getServerAuthCode());
                Log.w(TAG, "account.getPhotoUrl()" + account.getPhotoUrl());
                Log.w(TAG, "account.getDisplayName()" + account.getDisplayName());
                firebaseAuthWithGoogle(account);
            } else {
                // failed -> update UI
                //    updateUI(null);
                Toast.makeText(getApplicationContext(), "SignIn: failed! " + result.getStatus(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
