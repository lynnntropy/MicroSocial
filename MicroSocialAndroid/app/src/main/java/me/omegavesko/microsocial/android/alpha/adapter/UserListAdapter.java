package me.omegavesko.microsocial.android.alpha.adapter;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.omegavesko.microsocial.android.alpha.R;
import me.omegavesko.microsocial.android.alpha.RegisteredUser;
import me.omegavesko.microsocial.android.alpha.activity.UserActivity;

public class UserListAdapter extends ArrayAdapter<RegisteredUser>
{
    private boolean dummyData;
    private Context context;

    private List<RegisteredUser> data;

    public UserListAdapter(Context context, List<RegisteredUser> objects, boolean dummyData)
    {
        super(context, R.layout.list_item_user, objects);
        this.dummyData = dummyData;
        this.context = context;

        if (!dummyData)
        {
            this.data = objects;
        }
        else
        {
            // full a list with dummy users

            List<RegisteredUser> dummyUsers = new ArrayList<RegisteredUser>();

            for (int i = 1; i <= 5; i++)
                dummyUsers.add(new RegisteredUser("none", "username", "John Doe " + i, null));

            this.data = dummyUsers;
        }
    }

    @Override
    public int getCount()
    {
        if (dummyData)
        {
            return 5;
        }
        else
        {
            return super.getCount();
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
//        return super.getView(position, convertView, parent);

        View view = convertView;

        if (view == null)
        {
            // inflate a new view from a layout

            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.list_item_user, parent, false);
        }

        // populate the elements of the view normally
        // ...
        TextView userNameView = (TextView) view.findViewById(R.id.userName);
        userNameView.setText(data.get(position).userRealName);

        view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openContactPage(data.get(position));
            }
        });

        return view;
    }

    private String getContactIdByNumber(String number)
    {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String id = "?";

        ContentResolver contentResolver = context.getContentResolver();
        Cursor contactLookup = contentResolver.query(uri, new String[] {BaseColumns._ID,
                ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);

        try {
            if (contactLookup != null && contactLookup.getCount() > 0) {
                contactLookup.moveToNext();
//                name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                id = contactLookup.getString(contactLookup.getColumnIndex(BaseColumns._ID));
            }
        } finally {
            if (contactLookup != null) {
                contactLookup.close();
            }
        }

        return id;
    }

    private void openContactPage(RegisteredUser user)
    {
        if (user.userPhoneNumber != null && !user.userPhoneNumber.trim().equals("none") && !user.userPhoneNumber.trim().equals(""))
        {
            // attempt to open the native contact for this phone number

//            Intent openContactIntent = new Intent();
//            openContactIntent.setAction(Intent.ACTION_VIEW);
//            openContactIntent.setData(Uri.fromParts("tel", user.userPhoneNumber, null));
//            openContactIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(openContactIntent);

            String id = getContactIdByNumber(user.userPhoneNumber);
            Intent contactIntent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(id));
            contactIntent.setData(uri);
            contactIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            try
            {
                context.startActivity(contactIntent);
            }
            catch (ActivityNotFoundException e)
            {
                // Android is telling us the contact doesn't exist.
                // Open our custom user activity.

                openCustomUserActivity(user);
            }
        }
        else
        {
            openCustomUserActivity(user);
        }
    }

    private void openCustomUserActivity(RegisteredUser user)
    {
        Intent intent = new Intent(context, UserActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Pack the user's information into the intent so we have something to show
        intent.putExtra("USERNAME", user.userName);
        intent.putExtra("FULLNAME", user.userRealName);
        intent.putExtra("PHONE", user.userPhoneNumber);

        context.startActivity(intent);
    }
}
