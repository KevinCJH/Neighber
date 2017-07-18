package orbital.raspberry.neighber;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class HistoryList extends ArrayAdapter<Post> {

    private Activity context;
    List<Post> posts;

    public HistoryList(Activity context, List<Post> posts) {
        super(context, R.layout.layout_records_list, posts);
        this.context = context;
        this.posts = posts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_records_list, null, true);

        TextView itemname = (TextView) listViewItem.findViewById(R.id.itemnameTxt);
        TextView offernum = (TextView) listViewItem.findViewById(R.id.offernumTxt);
        TextView status = (TextView) listViewItem.findViewById(R.id.statusTxt);

        Post post = posts.get(position);
        itemname.setText(post.getItemname());

        if(post.getPosttype() == 1){
            status.setText("Post Type: Borrow");
            offernum.setText("Borrowed From: " + post.getOthername());
        }else{
            status.setText("Post Type: Lend");
            offernum.setText("Lend To: " + post.getOthername());
        }


        return listViewItem;
    }

}
