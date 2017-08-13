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

public class HistoryList extends ArrayAdapter<Post> {

    private Activity context;
    List<Post> posts;

    public HistoryList(Activity context, List<Post> posts) {
        super(context, R.layout.layout_offerrecords_list, posts);
        this.context = context;
        this.posts = posts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_offerrecords_list, null, true);

        TextView itemname = (TextView) listViewItem.findViewById(R.id.itemnameTxt);
        TextView offernum = (TextView) listViewItem.findViewById(R.id.offernumTxt);
        TextView status = (TextView) listViewItem.findViewById(R.id.statusTxt);
        ImageView cat = (ImageView) listViewItem.findViewById(R.id.imgView);
        ImageView type = (ImageView) listViewItem.findViewById(R.id.imgView2);
        ImageView photo = (ImageView) listViewItem.findViewById(R.id.imgViewPhoto);

        type.setVisibility(View.INVISIBLE);

        Post post = posts.get(position);

        itemname.setText(post.getItemname());

        if(!post.getImgUri().toString().trim().isEmpty()){
            Picasso.with(context).load(post.getImgUri()).placeholder(R.mipmap.neighberlogo).into(photo);
        }

        if(post.getPosttype() == 1){
            status.setText("Post Type: Borrow");
            offernum.setText("Borrowed From: " + post.getOthername());
        }else{
            status.setText("Post Type: Lend");
            offernum.setText("Lend To: " + post.getOthername());
        }


        switch(post.getCategory()){
            case 0:
                cat.setImageResource(R.mipmap.others);
                break;
            case 1:
                cat.setImageResource(R.mipmap.worktools);
                break;
            case 2:
                cat.setImageResource(R.mipmap.kitchen);
                break;
            case 3:
                cat.setImageResource(R.mipmap.cleaning);
                break;
            case 4:
                cat.setImageResource(R.mipmap.office);
                break;
            case 5:
                cat.setImageResource(R.mipmap.party);
                break;
            case 6:
                cat.setImageResource(R.mipmap.furniture);
                break;
            case 7:
                cat.setImageResource(R.mipmap.shirtf);
                break;
            case 8:
                cat.setImageResource(R.mipmap.shirtm);
                break;
            case 9:
                cat.setImageResource(R.mipmap.sports);
                break;
            case 10:
                cat.setImageResource(R.mipmap.electrical);
                break;
            case 11:
                cat.setImageResource(R.mipmap.food);
                break;
        }

        return listViewItem;
    }

}
