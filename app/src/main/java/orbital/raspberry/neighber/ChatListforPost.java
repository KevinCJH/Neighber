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

public class ChatListforPost extends ArrayAdapter<ChatItem> {

    private Activity context;
    List<ChatItem> chats;

    public ChatListforPost(Activity context, List<ChatItem> chats) {
        super(context, R.layout.layout_chat_list_forpost, chats);
        this.context = context;
        this.chats = chats;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_chat_list_forpost, null, true);

        TextView itemname = (TextView) listViewItem.findViewById(R.id.itemnameTxt);
        TextView username = (TextView) listViewItem.findViewById(R.id.usernameTxt);
        TextView posttype = (TextView) listViewItem.findViewById(R.id.posttype);
        CircleImageView imgview = (CircleImageView) listViewItem.findViewById(R.id.imgView);

        ChatItem chat = chats.get(position);
        itemname.setText("Item: " + chat.getItemname());
        username.setText("User: " + chat.getOthername());
        if(chat.getPosttype() == 1) {
            posttype.setText("You are Borrowing");
        }else{
            posttype.setText("You are Lending");
        }
        Picasso.with(context).load(chat.getOtherimguri()).placeholder(R.mipmap.defaultprofile).into(imgview);

        return listViewItem;
    }

}
