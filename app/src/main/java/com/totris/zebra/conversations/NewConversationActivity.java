package com.totris.zebra.conversations;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.totris.zebra.groups.Group;
import com.totris.zebra.groups.GroupUser;
import com.totris.zebra.users.contacts.ContactsAdapter;
import com.totris.zebra.users.contacts.ContactsListFragment;
import com.totris.zebra.users.contacts.ContactsListMode;
import com.totris.zebra.users.User;
import com.totris.zebra.R;

import java.util.ArrayList;

public class NewConversationActivity extends AppCompatActivity implements ContactsAdapter.ContactItemListener {
    private static final String TAG = "NewConversationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_conversation);

        if (savedInstanceState == null) {

            ContactsListFragment fragment = new ContactsListFragment();
            fragment.setMode(ContactsListMode.SELECTABLE);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.activity_new_conversation, fragment)
                    .commit();
        } else {
            ((ContactsListFragment)getSupportFragmentManager().getFragments().get(0)).setMode(ContactsListMode.SELECTABLE);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.title_activity_new_conversation));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_confirmation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_confirm:
                ContactsListFragment fragment = (ContactsListFragment) getSupportFragmentManager().getFragments().get(0);
                ArrayList<User> users = fragment.getSelectedUsers();

                if (users.size() == 0) return true;

                Group group = Group.getCommonGroup(fragment.getSelectedUsers());

                users.add(User.getCurrent());
                GroupUser conversation = new GroupUser(users, group);

                Intent intent = new Intent(this, ConversationActivity.class);
                intent.putExtra("group", group);
                intent.putExtra("title", conversation.getTitle());
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onContactItemClick(User user, boolean selected) {

    }
}
