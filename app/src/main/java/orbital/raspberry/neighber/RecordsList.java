package orbital.raspberry.neighber;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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
        ImageView imgview = (ImageView) listViewItem.findViewById(R.id.imgView);
        ImageView photo = (ImageView) listViewItem.findViewById(R.id.imgViewPhoto);


        Post post = posts.get(position);
        itemname.setText(post.getItemname());

        if(!post.getImgUri().toString().trim().isEmpty()){
            Picasso.with(context).load(post.getImgUri()).placeholder(R.mipmap.neighberlogo).into(photo);
        }

        switch(post.getStatus()){
            case 1:
                status.setText("Status: Pending Offers");
                offernum.setText("Number of Offers: " + post.getRecordcount());
                break;
            case 2:
                status.setText("Status: Awaiting Response");
                offernum.setText("From: " + post.getOthername());
                break;
            case 3:
                status.setText("Status: Collect Item");
                offernum.setText("Collect From: " + post.getOthername());
                break;
            case 4:
                status.setText("Status: Borrowing Item");
                offernum.setText("Borrowing From: " + post.getOthername());
                break;
            case 5:
                status.setText("Status: Returning Item");
                offernum.setText("Return To: " + post.getOthername());
                break;

        }

        switch(post.getCategory()){
            case 0:
                imgview.setImageResource(R.mipmap.others);
                break;
            case 1:
                imgview.setImageResource(R.mipmap.worktools);
                break;
            case 2:
                imgview.setImageResource(R.mipmap.kitchen);
                break;
            case 3:
                imgview.setImageResource(R.mipmap.cleaning);
                break;
            case 4:
                imgview.setImageResource(R.mipmap.office);
                break;
            case 5:
                imgview.setImageResource(R.mipmap.party);
                break;
            case 6:
                imgview.setImageResource(R.mipmap.furniture);
                break;
            case 7:
                imgview.setImageResource(R.mipmap.shirtf);
                break;
            case 8:
                imgview.setImageResource(R.mipmap.shirtm);
                break;
            case 9:
                imgview.setImageResource(R.mipmap.sports);
                break;
            case 10:
                imgview.setImageResource(R.mipmap.electrical);
                break;
            case 11:
                imgview.setImageResource(R.mipmap.food);
                break;
        }


        return listViewItem;
    }

}
