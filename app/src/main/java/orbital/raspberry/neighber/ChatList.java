package orbital.raspberry.neighber;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatList extends ArrayAdapter<ChatItem> {

    private Activity context;
    List<ChatItem> chats;

    public ChatList(Activity context, List<ChatItem> chats) {
        super(context, R.layout.layout_chat_list, chats);
        this.context = context;
        this.chats = chats;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_chat_list, null, true);

        TextView itemname = (TextView) listViewItem.findViewById(R.id.itemnameTxt);
        TextView username = (TextView) listViewItem.findViewById(R.id.usernameTxt);
        CircleImageView imgview = (CircleImageView) listViewItem.findViewById(R.id.imgView);

        ChatItem chat = chats.get(position);
        itemname.setText("Item: " + chat.getItemname());
        username.setText("User: " + chat.getOthername());
        Picasso.with(context).load(chat.getOtherimguri()).placeholder(R.mipmap.defaultprofile).into(imgview);

        return listViewItem;
    }

}
