package laodresource.demo.com.loadresourcedemo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;

public class MainActivity extends Activity {
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ImageView imageView = (ImageView) findViewById(R.id.icon);
        final TextView textView = (TextView) findViewById(R.id.text);
        final ViewGroup contentWrapper = (ViewGroup) findViewById(R.id.plugin_content);

        findViewById(R.id.id_load_from_plugin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 加载后的图片，文字，和布局，分别赋值给imageView，textView,contentWrapper
                loadResFromPlugin(imageView, textView, contentWrapper);
            }
        });


    }

    private void loadResFromPlugin(ImageView imageView, TextView textView, ViewGroup contentWrapper) {
        String apkPath = Environment.getExternalStorageDirectory() + "/DCIM/apkbeloaded-debug.apk";
        Resources resources = getBundleResource(this, apkPath);

         // 1、获取图片资源
        Drawable drawable = null;
        try{
        drawable = resources.getDrawable(resources.getIdentifier("icon_be_load", "drawable",
                "laodresource.demo.com.apkbeloaded"));
        }catch (Exception e){
            Toast.makeText(this,"1、确保插件apk已经存到根目录下面的DCIM目录下\n" +
                    "2、要从存储中读取插件，请先开通存储权限\n",Toast.LENGTH_LONG);
            e.printStackTrace();
            return;
        }

        // 1.2、获取不存在的图片资源
        try {
            Drawable drawableNotExist = resources.getDrawable(resources.getIdentifier("icon_be_load_null", "drawable",
                    "laodresource.demo.com.apkbeloaded"));
        }catch (Exception e){
            e.printStackTrace();
        }


        // 2、获取文本资源
        String text = resources.getString(resources.getIdentifier("text_beload", "string",
                "laodresource.demo.com.apkbeloaded"));

        imageView.setImageDrawable(drawable);
        textView.setText(text);

        // 3、获取布局资源
        XmlPullParser xmlResourceParser = resources.getLayout(resources.getIdentifier("layout_be_load", "layout",
                "laodresource.demo.com.apkbeloaded"));
        View viewFromPlugin = LayoutInflater.from(this).inflate(xmlResourceParser, null);
        contentWrapper.addView(viewFromPlugin);
    }



    private Resources getBundleResource(Context context, String apkPath) {
        AssetManager assetManager = createAssetManager(apkPath);
        return new Resources(assetManager, context.getResources().getDisplayMetrics(), context.getResources().getConfiguration());
    }

    private AssetManager createAssetManager(String apkPath) {
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            AssetManager.class.getDeclaredMethod("addAssetPath", String.class).invoke(
                    assetManager, apkPath);
            return assetManager;
        } catch (Throwable th) {
            th.printStackTrace();
        }
        return null;
    }

    // 跳转到当前应用的设置界面
    private void goToAppSetting() {
        Intent intent = new Intent();

        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);

        startActivityForResult(intent, 123);
    }
}
