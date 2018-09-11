package com.hotelnow.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.hotelnow.R;
import java.util.ArrayList;

public class ActLoading extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        //권한 예제
//   권한 후 동작 진행
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(ActLoading.this, "권한 허가", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(ActLoading.this, MainActivity.class);
                startActivity(intent);


            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(ActLoading.this, "권한 거부\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(ActLoading.this, MainActivity.class);
                startActivity(intent);
            }

        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("구글 로그인을 하기 위해서는 주소록 접근 권한이 필요해요")
                .setDeniedMessage("왜 거부하셨어요...\n하지만 [설정] > [권한] 에서 권한을 허용할 수 있어요.")
                .setPermissions(Manifest.permission.READ_CONTACTS)
                .check();
    }



}
