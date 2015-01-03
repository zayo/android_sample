package cz.trinerdis.androidsample.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wilson.android.library.DrawableManager;

import java.util.List;

import cz.trinerdis.androidsample.R;
import cz.trinerdis.androidsample.model.User;

/**
 * Created by stanislav on 3. 1. 2015.
 * Users adapter, loads user, fetch avatar.
 */
public class UserAdapter extends ArrayAdapter<User> {

  //manager for downloading avatars
  private DrawableManager drawableManager;

  private LayoutInflater layoutInflater;

  public UserAdapter(Context context, int resource, List<User> objects) {
    super(context, resource, objects);
    drawableManager = new DrawableManager();
    layoutInflater = LayoutInflater.from(context);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolderUser viewHolder;

    if (convertView == null) {
      convertView = layoutInflater.inflate(R.layout.view_user_list_item, parent, false);

      viewHolder = new ViewHolderUser();
      viewHolder.avatar = (ImageView) convertView.findViewById(R.id.user_avatar_image_view);
      viewHolder.name = (TextView) convertView.findViewById(R.id.user_name_text_view);
      convertView.setTag(viewHolder);
    } else {
      viewHolder = (ViewHolderUser) convertView.getTag();
    }

    User user = getItem(position);

    if (user != null) {
      viewHolder.name.setText(user.getLogin());
      drawableManager.fetchDrawableOnThread(user.getAvatar_url(), viewHolder.avatar);
    }
    return convertView;
  }

  static class ViewHolderUser {
    ImageView avatar;
    TextView name;
  }
}
