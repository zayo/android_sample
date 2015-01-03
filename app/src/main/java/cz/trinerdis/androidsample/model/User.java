package cz.trinerdis.androidsample.model;

/**
 * Created by stanislav on 3. 1. 2015.
 * User model
 */
public class User {

  protected String login;
  protected String avatar_url;
  protected String url;

  public User(String login, String avatar_url, String url) {
    this.login = login;
    this.avatar_url = avatar_url;
    this.url = url;
  }

  public String getUrl() {
    return url;
  }

  public String getAvatar_url() {
    return avatar_url;
  }

  public String getLogin() {
    return login;
  }
}
