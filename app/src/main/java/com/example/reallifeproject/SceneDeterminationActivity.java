package com.example.reallifeproject;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.Manifest;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.reallifeproject.ml.Modellandscape;
import com.example.reallifeproject.model.DayModel;
import com.example.reallifeproject.model.ItemModel;
import com.example.reallifeproject.model.PlayerModel;
import com.example.reallifeproject.utils.AndroidUtil;
import com.example.reallifeproject.utils.FirebaseUtil;
import com.example.reallifeproject.utils.ItemUtil;
import com.google.firebase.firestore.DocumentSnapshot;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class SceneDeterminationActivity extends AppCompatActivity {
    private ImageView imagePic;
    private TextView resultTxt;
    private Button takePicBtn, gallaryBtn, nextBtn;
    private ProgressBar loadProgress;
    private String gender, scene;
    private int money, strength, smart, attack, magic, defense, resistance, agility;
    private String event;
    int imageSize = 64;

    private static final String TAG = "SceneDeterminationActiv";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_determination);

        initView();

        gender = getIntent().getStringExtra("gender").toString();

        setInProgress(false);

        takePicBtn.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 3);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });
        gallaryBtn.setOnClickListener(v -> {
            Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(cameraIntent, 1);
        });

        nextBtn.setOnClickListener(v -> {
            setInProgress(true);
            if (imagePic.getDrawable() == null) {
                AndroidUtil.showToast(this, "Hãy chụp một bức ảnh");
            } else {
                String[] events = getApplicationContext().getResources().getStringArray(R.array.event_age0);
                if (gender.equals("Nam")) {
                    if (scene.equals("Quý tộc")) {
                        event = events[0];
                        strength = 0;
                        smart = 0;
                        Random random = new Random();
                        int randomAttack = random.nextInt(3) + 2;
                        int randomMagic = random.nextInt(3) + 2;
                        int randomDefense = random.nextInt(3) + 2;
                        int randomResistance = random.nextInt(3) + 2;
                        int randomAgility = random.nextInt(3) + 2;
                        attack = randomAttack;
                        magic = randomMagic;
                        defense = randomDefense;
                        resistance = randomResistance;
                        agility = randomAgility;
                        money = 500;
                    } else {
                        event = events[1];
                        strength = 0;
                        smart = 0;
                        Random random = new Random();
                        int randomAttack = random.nextInt(4) + 3;
                        int randomMagic = random.nextInt(4) + 3;
                        int randomDefense = random.nextInt(4) + 3;
                        int randomResistance = random.nextInt(4) + 3;
                        int randomAgility = random.nextInt(4) + 3;
                        attack = randomAttack;
                        magic = randomMagic;
                        defense = randomDefense;
                        resistance = randomResistance;
                        agility = randomAgility;
                        money = 200;
                    }
                } else {
                    if (scene.equals("Quý tộc")) {
                        event = events[2];
                        strength = 0;
                        smart = 0;
                        Random random = new Random();
                        int randomAttack = random.nextInt(4) + 1;
                        int randomMagic = random.nextInt(4) + 1;
                        int randomDefense = random.nextInt(2) + 1;
                        int randomResistance = random.nextInt(2) + 1;
                        int randomAgility = random.nextInt(4) + 1;
                        attack = randomAttack;
                        magic = randomMagic;
                        defense = randomDefense;
                        resistance = randomResistance;
                        agility = randomAgility;
                        money = 500;
                    } else {
                        event = events[3];
                        strength = 0;
                        smart = 0;
                        Random random = new Random();
                        int randomAttack = random.nextInt(5) + 2;
                        int randomMagic = random.nextInt(5) + 2;
                        int randomDefense = random.nextInt(3) + 2;
                        int randomResistance = random.nextInt(3) + 2;
                        int randomAgility = random.nextInt(5) + 2;
                        attack = randomAttack;
                        magic = randomMagic;
                        defense = randomDefense;
                        resistance = randomResistance;
                        agility = randomAgility;
                        money = 200;
                    }
                }

                PlayerModel playerModel = new PlayerModel(gender, scene, FirebaseUtil.currentUserId(), money, 0, event, 100, 0, strength, smart, attack, magic, defense, resistance,agility, false);

                DayModel dayModel = new DayModel(money, 0, event, 100, 0, strength, smart, attack, magic, defense, resistance,agility);

                ArrayList<ItemModel> itemModelArrayList = ItemUtil.getInstance().getItemModels();
                List<Map<String, Object>> arrayListAsMap = new ArrayList<>();
                for (ItemModel model : itemModelArrayList) {
                    Map<String, Object> modelMap = new HashMap<>();
                    modelMap.put("id", model.getId());
                    modelMap.put("money", model.getMoney());
                    modelMap.put("name", model.getName());
                    modelMap.put("attack", model.getAttack());
                    modelMap.put("defense", model.getDefense());
                    modelMap.put("magic", model.getMagic());
                    modelMap.put("resistance", model.getResistance());
                    modelMap.put("agility", model.getAgility());
                    modelMap.put("asked", model.getAsked());
                    modelMap.put("describe", model.getDescribe());
                    modelMap.put("gender", model.getGender());
                    modelMap.put("type", model.getType());
                    modelMap.put("kind", model.getKind());
                    modelMap.put("common", model.getCommon());
                    modelMap.put("isBuy", model.isBuy());
                    modelMap.put("imageId", model.getImageId());
                    arrayListAsMap.add(modelMap);
                }

                FirebaseUtil.getPlayerModelReference().get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                if (document.getId().equals(playerModel.getPlayerId())) {
                                    document.getReference().delete();
                                }
                            }
                            FirebaseUtil.getPlayerModelReferenceWithId().set(playerModel).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    FirebaseUtil.getDayModelReference().get().addOnCompleteListener(task2 -> {
                                        if(task2.isSuccessful()){
                                            if (!task2.getResult().isEmpty()){

                                                for (DocumentSnapshot document : task2.getResult()) {
                                                    document.getReference().delete();
                                                }

                                                setInProgress(false);

                                                FirebaseUtil.getDayModelReference().add(dayModel).addOnCompleteListener(task3 -> {
                                                    if (task3.isSuccessful()){
                                                        FirebaseUtil.getItemModelReference().get().addOnCompleteListener(task4 -> {
                                                            if (task4.isSuccessful()){
                                                                if (task4.getResult().isEmpty()){
                                                                    for (Map<String, Object> map : arrayListAsMap){
                                                                        FirebaseUtil.getItemModelReference().add(map);
                                                                    }
                                                                    navigateToWaiting();
                                                                }else {
                                                                    for (DocumentSnapshot document : task4.getResult()) {
                                                                        document.getReference().delete();
                                                                    }
                                                                    for (Map<String, Object> map : arrayListAsMap){
                                                                        FirebaseUtil.getItemModelReference().add(map);
                                                                    }
                                                                    navigateToWaiting();
                                                                }
                                                            }
                                                        });

                                                    }
                                                });
                                            }else {
                                                setInProgress(false);
                                                FirebaseUtil.getDayModelReference().add(dayModel).addOnCompleteListener(task3 -> {
                                                    if (task3.isSuccessful()){
                                                        FirebaseUtil.getItemModelReference().get().addOnCompleteListener(task4 -> {
                                                            if (task4.isSuccessful()){
                                                                if (task4.getResult().isEmpty()){
                                                                    for (Map<String, Object> map : arrayListAsMap){
                                                                        FirebaseUtil.getItemModelReference().add(map);
                                                                    }
                                                                    navigateToWaiting();
                                                                }else {
                                                                    navigateToWaiting();
                                                                }
                                                            }
                                                        });

                                                    }
                                                });

                                                Log.d(TAG, "Collection does not exist or is empty");
                                            }
                                        }else {
                                            Log.d(TAG, "Error getting collection: ", task.getException());
                                        }
                                    });
                                }
                            });
                        } else {
                            setInProgress(false);
                            FirebaseUtil.getPlayerModelReferenceWithId().set(playerModel).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    FirebaseUtil.getDayModelReference().get().addOnCompleteListener(task2 -> {
                                        if(task2.isSuccessful()){
                                            if (!task2.getResult().isEmpty()){

                                                for (DocumentSnapshot document : task2.getResult()) {
                                                    document.getReference().delete();
                                                }

                                                setInProgress(false);

                                                FirebaseUtil.getDayModelReference().add(dayModel).addOnCompleteListener(task3 -> {
                                                    if (task3.isSuccessful()){
                                                        FirebaseUtil.getItemModelReference().get().addOnCompleteListener(task4 -> {
                                                            if (task4.isSuccessful()){
                                                                if (task4.getResult().isEmpty()){
                                                                    for (Map<String, Object> map : arrayListAsMap){
                                                                        FirebaseUtil.getItemModelReference().add(map);
                                                                    }
                                                                    navigateToWaiting();
                                                                }else {
                                                                    navigateToWaiting();
                                                                }
                                                            }
                                                        });

                                                    }
                                                });
                                            }else {
                                                setInProgress(false);
                                                FirebaseUtil.getDayModelReference().add(dayModel).addOnCompleteListener(task3 -> {
                                                    if (task3.isSuccessful()){
                                                        FirebaseUtil.getItemModelReference().get().addOnCompleteListener(task4 -> {
                                                            if (task4.isSuccessful()){
                                                                if (task4.getResult().isEmpty()){
                                                                    for (Map<String, Object> map : arrayListAsMap){
                                                                        FirebaseUtil.getItemModelReference().add(map);
                                                                    }
                                                                    navigateToWaiting();
                                                                }else {
                                                                    navigateToWaiting();
                                                                }
                                                            }
                                                        });

                                                    }
                                                });

                                                Log.d(TAG, "Collection does not exist or is empty");
                                            }
                                        }else {
                                            Log.d(TAG, "Error getting collection: ", task.getException());
                                        }
                                    });
                                }
                            });
                            Log.d(TAG, "Collection does not exist or is empty");
                        }
                    } else {
                        Log.d(TAG, "Error getting collection: ", task.getException());
                    }
                });
            }
        });
    }

    private void navigateToWaiting() {
        Intent intent = new Intent(this, WaitingActivity.class);
        intent.putExtra("to_activity", "InGameActivity");
        startActivity(intent);
    }

    private void setInProgress(boolean isLoad){
        if (isLoad) {
            loadProgress.setVisibility(View.VISIBLE);
            nextBtn.setVisibility(View.GONE);
        } else {
            loadProgress.setVisibility(View.GONE);
            nextBtn.setVisibility(View.VISIBLE);
        }
    }

    private void classifyImage(Bitmap image) {
        try {
            Modellandscape model = Modellandscape.newInstance(getApplicationContext());
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 64, 64, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
            int pixel = 0;
            for (int i = 0; i < imageSize; i++) {
                for (int j = 0; j < imageSize; j++) {
                    int val = intValues[pixel++];
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 1));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 1));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 1));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            Modellandscape.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidence = outputFeature0.getFloatArray();

            int maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0; i < confidence.length; i++) {
                if (confidence[i] > maxConfidence) {
                    maxConfidence = confidence[i];
                    maxPos = i;
                }
            }
            String[] classes = {"Quý tộc", "Nông dân"};
            resultTxt.setText(classes[maxPos]);
            scene = classes[maxPos];
            model.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 3) {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                int dimension = Math.min(image.getWidth(), image.getHeight());
                image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
                imagePic.setImageBitmap(image);

                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                classifyImage(image);
            } else {
                Uri dat = data.getData();
                Bitmap image = null;
                try {
                    image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), dat);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imagePic.setImageBitmap(image);

                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                classifyImage(image);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initView() {
        imagePic = findViewById(R.id.imagePic);
        takePicBtn = findViewById(R.id.takePicBtn);
        gallaryBtn = findViewById(R.id.gallaryBtn);
        nextBtn = findViewById(R.id.nextBtn);
        resultTxt = findViewById(R.id.resultTxt);
        loadProgress = findViewById(R.id.loadProgress);
    }

}