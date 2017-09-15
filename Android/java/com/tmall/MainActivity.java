package com.tmall;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.tmall.adapter.ProductViewPagerAdapter;
import com.tmall.fragment.ProductListFragment;
import com.tmall.pojo.User;
import com.tmall.util.ImageTools;
import com.tmall.util.ImageUri;
import com.tmall.util.LoadDialog;
import com.tmall.util.TmallUtil;
import com.tmall.util.UploadService;
import com.tmall.util.VolleyUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;

    private static final int TAKE_PHOTO = 0;
    private static final int CHOOSE_PHONE = 1;
    private static final int SCALE = 5; // 照片缩小比例
    private String uploadFile; // 上传文件
    private ImageView imgUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 抽屉布局
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        // 工具栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 应用栏
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        // 导航视图
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        if (navView != null) {
            setDrawerContent(navView);
        }

        // ViewPager
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setViewPager(viewPager);
        }

        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        // fab 按钮
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Snackbar 类似于 Toast
                Snackbar.make(view, "Snackbar for 分页", Snackbar.LENGTH_SHORT).
                        setAction(R.string.app_name, null).show();
            }
        });
    }

    // 分页视图
    private void setViewPager(ViewPager viewPager) {
        ProductViewPagerAdapter adapter =
                new ProductViewPagerAdapter(this.getSupportFragmentManager());
        adapter.add(new ProductListFragment(), "产品列表");
        //adapter.add(new ProductListFragment(), "推荐商品");
        //adapter.add(new ProductListFragment(), "折扣商品");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return true;
    }

    // 导航视图
    private void setDrawerContent(NavigationView navView) {
        // 获得首选项存储中的用户信息
        final User user = TmallUtil.getUser(this);

        View view = navView.getHeaderView(0);
        imgUsername = (ImageView) view.findViewById(R.id.imgUsername);
        final TextView tvUsername = (TextView) view.findViewById(R.id.tvUsername);
        final Button btnLogin = (Button) view.findViewById(R.id.btn_login);

        imgUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUserImage();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.getId() == 0 && user.getUsername().length() == 0) {
                    // 登录
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    MainActivity.this.finish();
                } else {
                    // 注销
                    user.setId(0);
                    user.setUsername("");
                    user.setImageUrl("");
                    TmallUtil.saveUser(MainActivity.this, user);

                    imgUsername.setImageResource(R.drawable.me);
                    tvUsername.setText("请登录");
                    btnLogin.setText("登录");
                }
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        if (user.getId() == 0 && user.getUsername().length() == 0) {
            // 未登录,显示默认信息
            imgUsername.setImageResource(R.drawable.me);
            tvUsername.setText("请登录");
            btnLogin.setText("登录");
        } else {
            // 已登录

            // 从云端加载图片
            ImageLoader imageLoader = VolleyUtil.getInstance(this).getImageLoader();
            ImageLoader.ImageListener listener =
                    ImageLoader.getImageListener(imgUsername,
                            R.drawable.me2, R.drawable.me2);
            imageLoader.get(user.getImageUrl(), listener);

            tvUsername.setText(user.getUsername());
            btnLogin.setText("注销");
        }

        // -- 左侧抽屉导航视图 菜单 ------------------------------
        // 导航视图中的菜单选中事件
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_about:
                        AlertDialog.Builder builder =
                                new AlertDialog.Builder(MainActivity.this);
                        builder.setIcon(R.drawable.ic_launcher);
                        builder.setTitle(R.string.app_name);
                        builder.setMessage(R.string.about);
                        builder.create().show();
                        break;
                    // 其它的菜单选项事件
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    // 更改用户图像
    private void changeUserImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("图片来源:");
        builder.setNegativeButton("取消", null);
        builder.setItems(
                new CharSequence[]{"相机", "相册"},
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case TAKE_PHOTO:
                                // 打开 相机 App
                                Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                Uri imageUri = Uri.fromFile(
                                        new File(Environment.getExternalStorageDirectory(), "image.jpg"));
                                // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                startActivityForResult(openCameraIntent, TAKE_PHOTO);
                                break;
                            case CHOOSE_PHONE:
                                // 从 相册 中选择图片
                                Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
                                openAlbumIntent.setType("image/*");
                                startActivityForResult(openAlbumIntent, CHOOSE_PHONE);
                                break;
                        }
                    }
                });

        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_PHOTO:
                    // 将保存在本地的图片取出并缩小后显示在界面上
                    Bitmap bitmap = BitmapFactory.decodeFile(
                            Environment.getExternalStorageDirectory() + "/image.jpg");
                    // 缩小相机的图片
                    Bitmap newBitmap = ImageTools.zoomBitmap(
                            bitmap, bitmap.getWidth() / SCALE, bitmap.getHeight() / SCALE);
                    // 由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
                    bitmap.recycle();

                    // 同时复制至存储相片的目录
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                    String filename = sdf.format(new Date());
                    // 文件名
                    filename = "IMG_" + filename;
                    // 图片目录
                    String dir = Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                            .getAbsolutePath()
                            + "/Camera";
                    ImageTools.savePhotoToSDCard(newBitmap, dir, filename);
                    // 上传文件名
                    uploadFile = dir + "/" + filename + ".png";
                    break;
                case CHOOSE_PHONE:
                    ContentResolver resolver = this.getContentResolver();
                    // 照片的原始资源地址
                    Uri originalUri = data.getData();
                    try {
                        // 通过内容访问者和图片url获得bitmap的图片(相册中的原图)
                        Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                        if (photo != null) {
                            // 为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
                            Bitmap smallBitmap = ImageTools.zoomBitmap(
                                    photo, photo.getWidth() / SCALE, photo.getHeight() / SCALE);
                            // 释放原始图片占用的内存，防止out of memory异常发生
                            photo.recycle();
                            // 上传文件名
                            uploadFile = ImageUri.getImageAbsolutePath(this, originalUri);
                            Log.v("MainActivity", originalUri.toString());
                        }
                    } catch (FileNotFoundException e) {
                        TmallUtil.showToast(this, "文件不存在");
                    } catch (IOException e) {
                        TmallUtil.showToast(this, "读取文件,出错啦");
                    }
                    break;
            }
            // 获得选中图像的路径
            Log.v(VolleyUtil.TAG, uploadFile);

            // 保存至首选项存储
            final User user = TmallUtil.getUser(this);
            String imageUrl = "images" + uploadFile.substring(uploadFile.lastIndexOf("/"), uploadFile.length());
            user.setImageUrl(VolleyUtil.BASE_URL + imageUrl);
            TmallUtil.saveUser(this, user);

            // 上传图像
            String requestUrl = VolleyUtil.BASE_URL + "UploadServlet";
            File file = new File(uploadFile);
            LoadDialog dialog = new LoadDialog(this);
            dialog.execute(new LoadDialog.Callback() {
                @Override
                public void getResult(Object obj) {
                    // 更新图像
                    ImageLoader imageLoader = VolleyUtil.getInstance(MainActivity.this).getImageLoader();
                    ImageLoader.ImageListener listener =
                            ImageLoader.getImageListener(imgUsername,
                                    R.drawable.me2, R.drawable.me2);
                    imageLoader.get(user.getImageUrl(), listener);
                }
            }, UploadService.class, "postUseUrlConnection", requestUrl, file);
        }
    }
}
