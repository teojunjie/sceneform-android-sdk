package com.google.ar.sceneform.samples.hellosceneform;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;

import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Texture;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.concurrent.CompletableFuture;


public class HelloSceneformActivity extends AppCompatActivity {
  static final String GLB_ASSET =  "https://models-furnitures.s3-ap-southeast-1.amazonaws.com/just_work_i_command_you.glb";
  static final String GLB_TEXTURE1 = "https://models-furnitures.s3-ap-southeast-1.amazonaws.com/texture1.jpeg";
  static final String GLB_TEXTURE2 = "https://models-furnitures.s3-ap-southeast-1.amazonaws.com/texture2.jpeg";
  static final String GLB_TEXTURE3 = "https://models-furnitures.s3-ap-southeast-1.amazonaws.com/texture3.jpeg";
  private ArFragment arFragment;



  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    setContentView(R.layout.activity_ux);
    arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

    arFragment.setOnTapArPlaneListener((HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
            placeModel(hitResult.createAnchor());
        });


  }

    private void placeModel(Anchor anchor) {

        CompletableFuture<Texture> modelTexture = Texture.builder().setSource(this, Uri.parse(GLB_TEXTURE3)).setUsage(Texture.Usage.COLOR).build();

        ModelRenderable
            .builder()
            .setSource(this,
                    RenderableSource
                            .builder()
                            .setSource(this, Uri.parse(GLB_ASSET), RenderableSource.SourceType.GLB)
                    .setScale(0.01f)  // Scale the original model to 50%.
                    .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                    .build())
            .setRegistryId(GLB_ASSET)
            .build()
            .thenAcceptBoth(modelTexture, (renderable, texture) -> addNodeToScene(renderable, anchor, texture))
            .exceptionally(throwable -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(throwable.getMessage()).show();
                return null;
                    });
    }

    private void addNodeToScene(ModelRenderable renderable, Anchor anchor, Texture texture){
//          renderable.getMaterial().setTexture("baseColor" , texture);

          AnchorNode anchorNode = new AnchorNode(anchor);
          anchorNode.setParent(arFragment.getArSceneView().getScene());

          TransformableNode model = new TransformableNode(arFragment.getTransformationSystem());
          model.setParent(anchorNode);
          model.setRenderable(renderable);
          model.select();
      }


}
