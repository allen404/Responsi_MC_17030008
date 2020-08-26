package id.mobilecomputing.mc_responsi.activity.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.edt_login_email) EditText edt_email;
    @BindView(R.id.edt_login_password) EditText edt_password;
    @BindView(R.id.btn_login_button) Button btn_login;
    @BindView(R.id.btn_login_reset) Button btn_loginResetAcc;
    @BindView(R.id.btn_login_signup) Button btn_loginSignUp;
    @BindView(R.id.progbar_login) ProgressBar progbar_loginProg;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()!=null){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();

        btn_loginSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.this.startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        btn_loginResetAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.this.startActivity(new Intent(LoginActivity.this, ResetActivity.class));
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = edt_email.getText().toString();
                String userPassword = edt_password.getText().toString();

                if (TextUtils.isEmpty(userEmail)) {
                    Toast.makeText(LoginActivity.this.getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(userPassword)) {
                    Toast.makeText(LoginActivity.this.getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progbar_loginProg.setVisibility(View.VISIBLE);

                firebaseAuth.signInWithEmailAndPassword(userEmail,userPassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progbar_loginProg.setVisibility(View.GONE);

                                if (!task.isSuccessful()) {

                                    if (userPassword.length() < 6) {
                                        edt_password.setError(LoginActivity.this.getString(R.string.minimum_password));
                                    } else {
                                        Toast.makeText(LoginActivity.this, LoginActivity.this.getString(R.string.auth_failed), Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    LoginActivity.this.startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    LoginActivity.this.finish();
                                }
                            }
                        });

            }
        });


    }
}