package com.tmall;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tmall.pojo.User;
import com.tmall.util.TmallUtil;
import com.tmall.util.VolleyUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {
    @Bind(R.id.txt_username)
    EditText txtUsername;
    @Bind(R.id.txt_password)
    EditText txtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_login)
    public void btnLoginClick() {
        final String username = txtUsername.getText().toString();
        final String password = txtPassword.getText().toString();

        StringRequest request = new StringRequest(
                Request.Method.POST,
                VolleyUtil.getAbsoluteUrl("LoginServletJson"),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String json) {
                        Log.v(VolleyUtil.TAG, json);
                        try {
                            JSONObject jo = new JSONObject(json);
                            if (jo.getInt("id") == 0) {
                                TmallUtil.showToast(LoginActivity.this, "错误的用户名或密码!");
                            } else {
                                // 登录成功
                                Integer id = jo.getInt("id");
                                String username = jo.getString("username");
                                // 若user表有image_url字段则可以
                                // String imageUrl = VolleyUtil.BASE_URL + jo.getString("imageUrl");
                                String imageUrl = "";

                                User user = new User();
                                user.setId(id);
                                user.setUsername(username);
                                user.setImageUrl(imageUrl);
                                TmallUtil.saveUser(LoginActivity.this, user);
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                LoginActivity.this.finish();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v(VolleyUtil.TAG, error.getMessage());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };
        VolleyUtil.getInstance(this).addToRequestQueue(request, "login");
    }

    @OnClick(R.id.btn_register)
    public void btnRegisterClick() {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VolleyUtil.getInstance(this).cancelRequests("login");
    }
}
