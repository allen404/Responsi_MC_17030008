package id.mobilecomputing.mc_responsi.activity.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.mobilecomputing.mc_responsi.R;

public class ResetActivity extends AppCompatActivity {
    @BindView(R.id.edt_reset_email) EditText edt_reset_email;
    @BindView(R.id.btn_reset_password) Button btn_reset_password;
    @BindView(R.id.progbar_reset) ProgressBar progbar_reset;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);
        ButterKnife.bind(this);

        firebaseAuth =  FirebaseAuth.getInstance();

        btn_reset_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = edt_reset_email.getText().toString().trim();
                if (TextUtils.isEmpty(userEmail)) {
                    Toast.makeText(ResetActivity.this, "Enter your register email id", Toast.LENGTH_SHORT).show();
                    return;
                }

                progbar_reset.setVisibility(View.VISIBLE);

                firebaseAuth.sendPasswordResetEmail(userEmail)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ResetActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ResetActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                }

                                progbar_reset.setVisibility(View.GONE);
                            }
                        });

            }
        });
    }
}