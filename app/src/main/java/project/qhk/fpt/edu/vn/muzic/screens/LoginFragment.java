package project.qhk.fpt.edu.vn.muzic.screens;


import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import project.qhk.fpt.edu.vn.muzic.Logistic;
import project.qhk.fpt.edu.vn.muzic.MainActivity;
import project.qhk.fpt.edu.vn.muzic.R;
import project.qhk.fpt.edu.vn.muzic.managers.MusicPlayer;
import project.qhk.fpt.edu.vn.muzic.managers.NetworkManager;
import project.qhk.fpt.edu.vn.muzic.managers.PreferenceManager;
import project.qhk.fpt.edu.vn.muzic.managers.RealmManager;
import project.qhk.fpt.edu.vn.muzic.models.Playlist;
import project.qhk.fpt.edu.vn.muzic.models.Song;
import project.qhk.fpt.edu.vn.muzic.models.api_models.LocalSyncJSON;
import project.qhk.fpt.edu.vn.muzic.models.api_models.LoginResult;
import project.qhk.fpt.edu.vn.muzic.models.api_models.PlaylistResult;
import project.qhk.fpt.edu.vn.muzic.models.api_models.Result;
import project.qhk.fpt.edu.vn.muzic.notifiers.FragmentChanger;
import project.qhk.fpt.edu.vn.muzic.services.MusicService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    @BindView(R.id.login_username)
    EditText editTextUsername;

    @BindView(R.id.login_password)
    EditText editTextPassword;

    @BindView(R.id.register_username)
    EditText register_username;

    @BindView(R.id.register_password)
    EditText register_password;

    @BindView(R.id.register_fullname)
    EditText register_fullname;

    @BindView(R.id.register_email)
    EditText register_email;

    @BindView(R.id.register_retype_password)
    EditText register_retype_password;

    @BindView(R.id.login_bar_waiting)
    ProgressBar waitingBar;

    @BindView(R.id.register_layout)
    LinearLayout register_layout;

    @BindView(R.id.signin_layout)
    LinearLayout signin_layout;

    @BindView(R.id.logout_layout)
    LinearLayout logout_layout;

    @BindView(R.id.logout_fullname)
    TextView logout_fullname;

    @BindView(R.id.logout_email)
    TextView logout_email;


    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        settingThingsUp(view);

        return view;
    }

    private void settingThingsUp(View view) {
        ButterKnife.bind(this, view);
        if (PreferenceManager.getInstance().getToken().isEmpty()){
            signin_layout.setVisibility(View.VISIBLE);
            logout_layout.setVisibility(View.INVISIBLE);
        } else {
            signin_layout.setVisibility(View.INVISIBLE);
            logout_layout.setVisibility(View.VISIBLE);
            logout_fullname.setText(PreferenceManager.getInstance().getFullname());
            logout_email.setText(PreferenceManager.getInstance().getEmail());
        }
        getContent();
        new CountDownTimer(500, 100) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                ((MainActivity) getActivity()).setLayoutDaddy(View.INVISIBLE);
            }
        }.start();
    }

    private void getContent() {
        waitingBar.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.login_button_back)
    public void onBackPressed() {
        ((MainActivity) getActivity()).setLayoutDaddy(View.VISIBLE);
        getActivity().onBackPressed();
    }

    @OnClick(R.id.login_button)
    public void onLoginButton() {
        System.out.println("goLogin");
        waitingBar.setVisibility(View.VISIBLE);

        JsonObject object = new JsonObject();
        object.addProperty("username", editTextUsername.getText().toString());
        object.addProperty("password", editTextPassword.getText().toString());
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());

        Retrofit mediaRetrofit = new Retrofit.Builder()
                .baseUrl(Logistic.SERVER_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MusicService musicService = mediaRetrofit.create(MusicService.class);
        musicService.getLoginResult(body).enqueue(new Callback<LoginResult>() {
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                LoginResult result = response.body();
                Toast.makeText(getContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
                if (result.isSuccess()) {
                    PreferenceManager.getInstance().login(result.getName(), result.getToken(), result.getUser().getName(), result.getUser().getEmail());

                    if (NetworkManager.getInstance().isConnectedToInternet()  && !PreferenceManager.getInstance().getToken().isEmpty()){
                        waitingBar.setVisibility(View.VISIBLE);
                        if (RealmManager.getInstance().getAllPlaylist().size() == 0) {
                            JsonObject object = new JsonObject();
                            object.addProperty("token", PreferenceManager.getInstance().getToken());
                            RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());

                            Retrofit mediaRetrofit = new Retrofit.Builder()
                                    .baseUrl(Logistic.SERVER_API)
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build();
                            MusicService musicService = mediaRetrofit.create(MusicService.class);
                            musicService.getPlaylistByUser(body).enqueue(new Callback<PlaylistResult>() {
                                @Override
                                public void onResponse(Call<PlaylistResult> call, Response<PlaylistResult> response) {
                                    PlaylistResult result = response.body();
                                    if (result.isSuccess()) {
//                                    Toast.makeText(getContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
                                        System.out.println(result.getMessage());
                                        RealmManager.getInstance().clearSongOfPlaylist();
                                        RealmManager.getInstance().clearAllPlaylist();
                                        for (PlaylistResult.Playlist returnPlaylist : result.getPlaylists()) {
                                            Playlist playlist = Playlist.createPlaylist(returnPlaylist.getName());
                                            playlist.set_id(returnPlaylist.getId());
                                            RealmManager.getInstance().addNewPlaylist(playlist);
                                            for (PlaylistResult.Playlist.Song returnSong: returnPlaylist.getSongList()){
                                                Song song = Song.createForPlaylist(playlist.getPlaylistID(),
                                                        new Song(returnSong.getId(),
                                                                returnSong.getName(),
                                                                returnSong.getArtist(),
                                                                returnSong.getThumbnail(),
                                                                returnSong.getStream()));
                                                RealmManager.getInstance().addSong(song);
                                            }
                                        }
                                    }
                                    waitingBar.setVisibility(View.INVISIBLE);
                                }

                                @Override
                                public void onFailure(Call<PlaylistResult> call, Throwable t) {
                                    System.out.println("failure");
                                    waitingBar.setVisibility(View.INVISIBLE);
                                }
                            });
                        } else {
                            LocalSyncJSON localSyncJSON = new LocalSyncJSON();
                            Gson gson= new GsonBuilder().setPrettyPrinting().create();
                            String requestJSON = gson.toJson(localSyncJSON, localSyncJSON.getClass());

                            RequestBody body = RequestBody.create(MediaType.parse("application/json"), requestJSON);

                            Retrofit mediaRetrofit = new Retrofit.Builder()
                                    .baseUrl(Logistic.SERVER_API)
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build();
                            MusicService musicService = mediaRetrofit.create(MusicService.class);
                            musicService.syncPlaylist(body).enqueue(new Callback<PlaylistResult>() {
                                @Override
                                public void onResponse(Call<PlaylistResult> call, Response<PlaylistResult> response) {
                                    PlaylistResult result = response.body();
                                    if (result.isSuccess()) {
//                                    Toast.makeText(getContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
                                        System.out.println(result.getMessage());
                                        RealmManager.getInstance().clearSongOfPlaylist();
                                        RealmManager.getInstance().clearAllPlaylist();
                                        for (PlaylistResult.Playlist returnPlaylist : result.getPlaylists()) {
                                            Playlist playlist = Playlist.createPlaylist(returnPlaylist.getName());
                                            playlist.set_id(returnPlaylist.getId());
                                            RealmManager.getInstance().addNewPlaylist(playlist);
                                            for (PlaylistResult.Playlist.Song returnSong: returnPlaylist.getSongList()){
                                                Song song = Song.createForPlaylist(playlist.getPlaylistID(),
                                                        new Song(returnSong.getId(),
                                                                returnSong.getName(),
                                                                returnSong.getArtist(),
                                                                returnSong.getThumbnail(),
                                                                returnSong.getStream()));
                                                RealmManager.getInstance().addSong(song);
                                            }
                                        }
                                    }
                                    waitingBar.setVisibility(View.INVISIBLE);
                                }

                                @Override
                                public void onFailure(Call<PlaylistResult> call, Throwable t) {
//                                Toast.makeText(getContext(), "SYNC FAILURE", Toast.LENGTH_SHORT).show();
                                    System.out.println("failure");
                                    waitingBar.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                    } else {
                        waitingBar.setVisibility(View.INVISIBLE);
                    }

                    onBackPressed();
                } else {
                    waitingBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(Call<LoginResult> call, Throwable throwable) {
                Toast.makeText(getContext(), "LOGIN FAILURE", Toast.LENGTH_SHORT).show();
                waitingBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    @OnClick(R.id.register_button)
    public void onRegisterButton(){
        System.out.println("goRegister");
        register_layout.setVisibility(View.VISIBLE);
        signin_layout.setVisibility(View.INVISIBLE);
        editTextUsername.setText("");
        editTextPassword.setText("");
    }

    @OnClick(R.id.register_button_back)
    public void onRegisterButtonBack(){
        System.out.println("goRegisterBack");
        register_layout.setVisibility(View.INVISIBLE);
        signin_layout.setVisibility(View.VISIBLE);
        register_email.setText("");
        register_password.setText("");
        register_fullname.setText("");
        register_username.setText("");
        register_retype_password.setText("");
    }

    @OnClick(R.id.create_account_button)
    public void onCreateAccountButton(){
        System.out.println("goCreateAccount");
        if (!register_password.getText().toString().equals(register_retype_password.getText().toString())){
            Toast.makeText(getContext(), "Please input correct Retype Password", Toast.LENGTH_SHORT).show();
            return;
        }
        waitingBar.setVisibility(View.VISIBLE);
        JsonObject object = new JsonObject();
        object.addProperty("username", register_username.getText().toString());
        object.addProperty("password", register_password.getText().toString());
        object.addProperty("name", register_fullname.getText().toString());
        object.addProperty("email", register_email.getText().toString());
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());

        Retrofit mediaRetrofit = new Retrofit.Builder()
                .baseUrl(Logistic.SERVER_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MusicService musicService = mediaRetrofit.create(MusicService.class);
        musicService.getRegisterResult(body).enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Result result = response.body();
                System.out.println(result.toString());
                Toast.makeText(getContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
                if (result.isSuccess()) {
                    onRegisterButtonBack();
                }
                waitingBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getContext(), "FAILURE", Toast.LENGTH_SHORT).show();
                waitingBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    @OnClick(R.id.logout_button_back)
    public void onLogoutBackPressed() {
        ((MainActivity) getActivity()).setLayoutDaddy(View.VISIBLE);
        getActivity().onBackPressed();
    }

    @OnClick(R.id.logout_button)
    public void onLogoutButton(){
        PreferenceManager.getInstance().logout();
        RealmManager.getInstance().clearSongOfPlaylist();
        RealmManager.getInstance().clearAllPlaylist();
        onBackPressed();
    }
}
