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
        TextView status = (TextView) listViewItem.findViewById(R.id.statusTxt);

        Post post = posts.get(position);
        itemname.setText(post.getItemname());

        switch(post.getStatus()){
            case 1:
                status.setText("Status: Pending Offers");
                offernum.setText("Number of Offers: " + post.getRecordcount());
                break;
            case 2:
                status.setText("Status: Waiting for Agreement");
                offernum.setText("From: " + post.getOthername());
                break;
            case 3:
                status.setText("Status: Collection of Item");
                offernum.setText("Collect From: " + post.getOthername());
                break;
            case 4:
                status.setText("Status: Borrowing in Progress");
                offernum.setText("Borrowing From: " + post.getOthername());
                break;
            case 5:
                status.setText("Status: Returning Item");
                offernum.setText("Return To: " + post.getOthername());
                break;

        }


        return listViewItem;
    }

}
