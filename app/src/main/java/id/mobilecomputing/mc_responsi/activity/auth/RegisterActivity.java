package id.mobilecomputing.mc_responsi.activity.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.mobilecomputing.mc_responsi.R;
import id.mobilecomputing.mc_responsi.activity.weather.MainActivity;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.edt_register_email) EditText edt_register_email;
    @BindView(R.id.edt_register_password) EditText edt_register_password;
    @BindView(R.id.btn_register_sign_in) Button btn_register_signIn;
    @BindView(R.id.btn_register) Button btn_register;
    @BindView(R.id.btn_register_reset) Button btn_registerReset;
    @BindView(R.id.progbar_register) ProgressBar progbar_register;

    private static final String TAG = "SignupActivity";
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();

        btn_registerReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterActivity.this.startActivity(new Intent(RegisterActivity.this, ResetActivity.class));
            }
        });

        btn_register_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterActivity.this.finish();
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterActivity.this.registerUser();
            }
        });
    }

    private void registerUser() {
        String userEmail = edt_register_email.getText().toString().trim();
        String userPassword = edt_register_password.getText().toString().trim();

        if (TextUtils.isEmpty(userEmail)) {
            showToast("Enter email address!");
            return;
        }

        if(TextUtils.isEmpty(userPassword)){
            showToast("Enter Password!");
            return;
        }

        if(userPassword.length() < 6){
            showToast("Password too short, enter minimum 6 characters");
            return;
        }

        progbar_register.setVisibility(View.VISIBLE);

        //register user
        firebaseAuth.createUserWithEmailAndPassword(userEmail,userPassword)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "New user registration: " + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            RegisterActivity.this.showToast("Authentication failed. " + task.getException());
                        } else {
                            RegisterActivity.this.startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            RegisterActivity.this.finish();
                        }
                    }
                });
    }

    private void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        progbar_register.setVisibility(View.GONE);
    }
}