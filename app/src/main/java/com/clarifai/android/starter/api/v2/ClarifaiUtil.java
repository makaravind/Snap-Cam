package com.clarifai.android.starter.api.v2;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import clarifai2.dto.prediction.Concept;

import com.clarifai.android.starter.api.v2.Util;

public final class ClarifaiUtil {
  private ClarifaiUtil() {
    throw new UnsupportedOperationException("No instances");
  }

  /**
   * @param context
   * @param data
   * @return
   */
  @Nullable
  public static byte[] retrieveSelectedImage(@NonNull Context context, @NonNull Intent data) {
    InputStream inStream = null;
    Bitmap bitmap = null;
    try {
      inStream = context.getContentResolver().openInputStream(data.getData());
      bitmap = BitmapFactory.decodeStream(inStream);
      final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
      return outStream.toByteArray();
    } catch (FileNotFoundException e) {
      return null;
    } finally {
      if (inStream != null) {
        try {
          inStream.close();
        } catch (IOException ignored) {
        }
      }
      if (bitmap != null) {
        bitmap.recycle();
      }
    }
  }


  @Nullable
  public static byte[] retrieveSnappedImage(@NonNull Context context, @NonNull Intent data) {

      Bundle extras = data.getExtras();
      Bitmap imageBitmap = (Bitmap) extras.get("data");
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      byte[] byteArray = null;
      try {
          imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
          byteArray = stream.toByteArray();
      }catch (NullPointerException e) {
          Toast.makeText(context, "converting to byte arr exception", Toast.LENGTH_SHORT).show();
      }
      return byteArray;
  }

  @NonNull
  public static List<Concept> filterPredictions(List<Concept> predictions){

      final int size = 3;
      int count = 0;
      List<Concept> filteredPredictions = new ArrayList<>(size);

      // shuffling the predictions before taking top n elements
      Collections.shuffle(predictions);

      // taking only valid top n predicitons
      for(Concept item: predictions){

          if(count >= size)
              break;
          if(Util.isValid(item.name())){
              filteredPredictions.add(item);
              count++;
          }
      }
      return filteredPredictions;
  }



    @NonNull
  public static Activity unwrapActivity(@NonNull Context startFrom) {
    while (startFrom instanceof ContextWrapper) {
      if (startFrom instanceof Activity) {
        return ((Activity) startFrom);
      }
      startFrom = ((ContextWrapper) startFrom).getBaseContext();
    }
    throw new IllegalStateException("This Context can't be unwrapped to an Activity!");
  }

  @Nullable
  public static <T> T firstChildOfType(@NonNull View root, @NonNull Class<T> type) {
    if (type.isInstance(root)) {
      return type.cast(root);
    }
    if (root instanceof ViewGroup) {
      final ViewGroup rootGroup = (ViewGroup) root;
      for (int i = 0; i < rootGroup.getChildCount(); i++) {
        final View child = rootGroup.getChildAt(i);
        final T childResult = firstChildOfType(child, type);
        if (childResult != null) {
          return childResult;
        }
      }
    }
    return null;
  }

  @NonNull
  public static <T> List<T> childrenOfType(@NonNull View root, @NonNull Class<T> type) {
    final List<T> children = new ArrayList<>();
    if (type.isInstance(root)) {
      children.add(type.cast(root));
    }
    if (root instanceof ViewGroup) {
      final ViewGroup rootGroup = (ViewGroup) root;
      for (int i = 0; i < rootGroup.getChildCount(); i++) {
        final View child = rootGroup.getChildAt(i);
        children.addAll(childrenOfType(child, type));
      }
    }
    return children;
  }
}
