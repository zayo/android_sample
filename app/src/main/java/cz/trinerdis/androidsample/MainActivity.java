package cz.trinerdis.androidsample;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.trinerdis.androidsample.adapter.UserAdapter;
import cz.trinerdis.androidsample.model.User;

public class MainActivity extends Activity {

  private static final String TAG = MainActivity.class.getName();

  // Object holding UI elements
  private UISet ui = new UISet();

  // Holding users loaded from network
  private ArrayList<User> users;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    ui.loadUI();
    ui.setLoading(true);
    loadData();
    setUpCallback();
  }

  /**
   * Loads data from network, converts them to ArrayList
   * of User objects, then calls method setData()
   */
  private void loadData() {
    // Instantiate the RequestQueue.
    RequestQueue queue = Volley.newRequestQueue(this);
    String url = "https://api.github.com/repos/torvalds/linux/contributors";

    if (users == null) {
      users = new ArrayList<>();
    } else {
      users.clear();
    }

    JsonArrayRequest request = new JsonArrayRequest(url,
      new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response) {
          Log.d(TAG, "onResponse(): Catch response" + response.toString());
          try {
            //convert json obj to users
            for (int i = 0; i < response.length(); i++) {
              JSONObject json_user = (JSONObject) response.get(i);
              users.add(
                new User(
                  json_user.getString("login"),
                  json_user.getString("avatar_url"),
                  json_user.getString("url"))
              );
            }
          } catch (JSONException e) {
            Log.d(TAG, "onResponse(): parse error", e);
            if (Config.DEBUG) {
              Toast.makeText(MainActivity.this, "Failed to parse contributors", Toast.LENGTH_LONG)
                .show();
            }
          } finally {
            setData();
            ui.setLoading(false);
          }
        }
      }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        Log.e(TAG, "Network problem ", error);
        Toast.makeText(MainActivity.this, "Failed to load from network!!! Have you enabled data connection?",
          Toast.LENGTH_LONG)
          .show();
      }
    });

    //Override default retry policy
    request.setRetryPolicy(new DefaultRetryPolicy(
      DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
      DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
      DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    // Add the request to the RequestQueue.
    queue.add(request);
  }

  /**
   * Sets adapter, empty view for list view
   */
  public void setData() {
    ui.list.setAdapter(new UserAdapter(this, R.layout.view_user_list_item, users));
    ui.list.setEmptyView(ui.emptyView);
  }

  /**
   * Creates callback for opening Intent with user Url attribute
   */
  public void setUpCallback() {
    ui.list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(users.get(position).getUrl())));
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      Toast.makeText(MainActivity.this, "© stanislav.nedbalek@gmail.com", Toast.LENGTH_LONG).show();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  /**
   * UISet corresponding to @layout/activity_main.xml
   */
  private class UISet {

    public FrameLayout contentView;
    public ProgressBar loadingView;

    public ListView list;
    public View emptyView;

    /**
     * Fill ui set with corresponding views from layout
     */
    public void loadUI() {
      contentView = (FrameLayout) findViewById(R.id.content_view);
      loadingView = (ProgressBar) findViewById(R.id.loading_view);

      list = (ListView) findViewById(R.id.list_view);
      emptyView = findViewById(android.R.id.empty);

      if (!checkViews()) {
        Log.d(TAG, "onCreate(): Failed to load ui.");
        if (Config.DEBUG) {
          Toast.makeText(MainActivity.this, "Failed to load ui", Toast.LENGTH_LONG).show();
        }
      }
    }

    /**
     * Test null values
     * @return true if all values are non null
     */
    private boolean checkViews() {
      return contentView != null && loadingView != null && list != null && emptyView != null;
    }

    /**
     * Swap content or loading view
     * @param loading When param is true, loading view is shown
     */
    public void setLoading(boolean loading) {
      contentView.setVisibility((loading) ? View.INVISIBLE : View.VISIBLE);
      loadingView.setVisibility((loading) ? View.VISIBLE : View.INVISIBLE);
    }
  }
}
