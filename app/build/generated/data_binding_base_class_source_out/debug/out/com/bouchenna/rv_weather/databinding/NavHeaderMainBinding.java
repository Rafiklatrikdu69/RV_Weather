// Generated by view binder compiler. Do not edit!
package com.bouchenna.rv_weather.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.bouchenna.rv_weather.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class NavHeaderMainBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final Button buttonDeconnexion;

  @NonNull
  public final TextView textView;

  @NonNull
  public final TextView textViewRafik;

  @NonNull
  public final TextView textViewValentin;

  private NavHeaderMainBinding(@NonNull LinearLayout rootView, @NonNull Button buttonDeconnexion,
      @NonNull TextView textView, @NonNull TextView textViewRafik,
      @NonNull TextView textViewValentin) {
    this.rootView = rootView;
    this.buttonDeconnexion = buttonDeconnexion;
    this.textView = textView;
    this.textViewRafik = textViewRafik;
    this.textViewValentin = textViewValentin;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static NavHeaderMainBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static NavHeaderMainBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.nav_header_main, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static NavHeaderMainBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.buttonDeconnexion;
      Button buttonDeconnexion = ViewBindings.findChildViewById(rootView, id);
      if (buttonDeconnexion == null) {
        break missingId;
      }

      id = R.id.textView;
      TextView textView = ViewBindings.findChildViewById(rootView, id);
      if (textView == null) {
        break missingId;
      }

      id = R.id.textViewRafik;
      TextView textViewRafik = ViewBindings.findChildViewById(rootView, id);
      if (textViewRafik == null) {
        break missingId;
      }

      id = R.id.textViewValentin;
      TextView textViewValentin = ViewBindings.findChildViewById(rootView, id);
      if (textViewValentin == null) {
        break missingId;
      }

      return new NavHeaderMainBinding((LinearLayout) rootView, buttonDeconnexion, textView,
          textViewRafik, textViewValentin);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
