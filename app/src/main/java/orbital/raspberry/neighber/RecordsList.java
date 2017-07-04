package orbital.raspberry.neighber;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class RecordsList extends ArrayAdapter<Post> {

    private Activity context;
    List<Post> posts;

    public RecordsList(Activity context, List<Post> posts) {
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

        Post post = posts.get(position);
        itemname.setText(post.getItemname());
        offernum.setText("Number of Offers: " + post.getRecordcount());

        return listViewItem;
    }

}
