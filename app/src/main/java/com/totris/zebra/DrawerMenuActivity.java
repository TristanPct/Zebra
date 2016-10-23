package com.totris.zebra;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.totris.zebra.conversations.ConversationsListActivity;
import com.totris.zebra.users.User;
import com.totris.zebra.users.contacts.ContactsListActivity;
import com.totris.zebra.users.profile.UserProfileActivity;

public class DrawerMenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected DrawerLayout drawer;
    protected RelativeLayout contentLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        if(User.getCurrent() != null) {
            navigationView = (NavigationView) findViewById(R.id.nav_view);
            View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_drawer_menu);

            TextView usernameTextView = (TextView) headerLayout.findViewById(R.id.menuUserUsername);
            TextView emailTextView = (TextView) headerLayout.findViewById(R.id.menuUserEmail);

            usernameTextView.setText(User.getCurrent().getUsername());
            emailTextView.setText(User.getCurrent().getMail());
        }

        contentLayout = (RelativeLayout) findViewById(R.id.content_drawer_menu);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer_menu, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_conversations_list) {
            Intent intent = new Intent(this, ConversationsListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        } else if (id == R.id.nav_contacts_list) {
            Intent intent = new Intent(this, ContactsListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        } else if (id == R.id.nav_user_profile) {
            Intent intent = new Intent(this, UserProfileActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
