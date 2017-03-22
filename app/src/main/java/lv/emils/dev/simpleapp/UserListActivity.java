package lv.emils.dev.simpleapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import lv.emils.dev.simpleapp.util.JsonReaderUtil;
import lv.emils.dev.simpleapp.content.UserContent;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * An activity representing a list of Users. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link UserDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class UserListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Placeholder for something in the future", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if (UserContent.USERS.isEmpty()) {
            try {
                new AllUsersJsonReaderTask().execute().get(10000, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        setupRecyclerView((RecyclerView) findViewById(R.id.user_list));

        if (findViewById(R.id.user_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.user_list_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }

            private void refreshContent() {
                mSwipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(true);
                        UserContent.USERS.clear();
                        UserContent.USER_MAP.clear();
                        try {
                            new AllUsersJsonReaderTask().execute().get(10000, TimeUnit.MILLISECONDS);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        setupRecyclerView((RecyclerView) findViewById(R.id.user_list));
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new UsersRecyclerViewAdapter(UserContent.USERS));
    }

    public class UsersRecyclerViewAdapter
            extends RecyclerView.Adapter<UsersRecyclerViewAdapter.ViewHolder> {

        private final List<UserContent.UserListUser> mValues;

        public UsersRecyclerViewAdapter(List<UserContent.UserListUser> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.user_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int id) {
            holder.mItem = mValues.get(id);
            holder.mContentView.setText(mValues.get(id).toString());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(UserDetailFragment.ARG_ITEM_ID, holder.mItem.getId());
                        UserDetailFragment fragment = new UserDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.user_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, UserDetailActivity.class);
                        intent.putExtra(UserDetailFragment.ARG_ITEM_ID, holder.mItem.getId());

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mContentView;
            public UserContent.UserListUser mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }

    private class AllUsersJsonReaderTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog progressDialog = new ProgressDialog(UserListActivity.this);
        JSONArray jsonUsers;

        protected void onPreExecute() {
            progressDialog.setMessage("Getting user list");
            progressDialog.show();
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    AllUsersJsonReaderTask.this.cancel(true);
                }
            });
        }

        protected Void doInBackground(Void... v) {
            try {
                jsonUsers = JsonReaderUtil.readJsonArrayFromUrl(UserContent.USERS_LINK);
                for (int i = 0; i < jsonUsers.length(); i++) {
                    JSONObject jsonObject = jsonUsers.getJSONObject(i);
                    UserContent.UserListUser user = JsonReaderUtil.getUserListUser(jsonObject);
                    UserContent.addUser(user);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException je) {
                je.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void jsonUsers) {
            progressDialog.setMessage("User list received");
            this.progressDialog.dismiss();
        }
    }
}
