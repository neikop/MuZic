package project.qhk.fpt.edu.vn.muzic.managers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import project.qhk.fpt.edu.vn.muzic.R;
import project.qhk.fpt.edu.vn.muzic.models.Playlist;
import project.qhk.fpt.edu.vn.muzic.models.Song;

/**
 * Created by WindzLord on 11/1/2017.
 */

public class PopupManager {

    private static PopupManager instance;
    private PopupMenu popup;

    public PopupManager(Context context, View view) {
        popup = new PopupMenu(context, view);
        popup.getMenuInflater().inflate(R.menu.menu_like, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == -1)
                    createPlaylist(context);
                else addToPlaylist(context, item.getItemId());

                for (Playlist playlist : RealmManager.getInstance().getAllPlaylistAlive()) {
                    System.out.println("=== " + playlist.getName());
                    for (Song song : playlist.getListSong()) {
                        System.out.println("+ " + song.getName() + " - " + song.getArtist());
                    }
                }
                return true;
            }
        });
    }

    public static PopupManager getInstance() {
        return instance;
    }

    public static void init(Context context, View view) {
        instance = new PopupManager(context, view);
    }

    private void createPlaylist(Context context) {
        EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Name");
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title = input.getText().toString().trim();
                if (title.isEmpty()) title = "Empty name";

                RealmManager.getInstance().addPlaylist(Playlist.create(title), MusicPlayer.getInstance().getSong());

                Toast.makeText(context, "Add to " + title, Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void addToPlaylist(Context context, int indexList) {
        Playlist playlist = RealmManager.getInstance().getAllPlaylistAlive().get(indexList);
        RealmManager.getInstance().addSongToList(playlist, MusicPlayer.getInstance().getSong());

        Toast.makeText(context, "Add to " + playlist.getName(), Toast.LENGTH_SHORT).show();
    }

    public void show() {
        popup.getMenu().clear();
        int index = -1;
        addItem(index++);
        for (Playlist playlist : RealmManager.getInstance().getAllPlaylistAlive()) {
            addItem(index++, playlist.getName());
        }
        popup.show();
    }

    public void addItem(int index) {
        popup.getMenu().add(0, index, 0, "New playlist");
    }

    public void addItem(int index, String title) {
        popup.getMenu().add(0, index, 0, title);
    }

}
