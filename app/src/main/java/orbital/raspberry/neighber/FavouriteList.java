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

public class FavouriteList extends ArrayAdapter<Post> {

    private Activity context;
    List<Post> posts;

    public FavouriteList(Activity context, List<Post> posts) {
        super(context, R.layout.layout_favourite_list, posts);
        this.context = context;
        this.posts = posts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_favourite_list, null, true);

        TextView itemname = (TextView) listViewItem.findViewById(R.id.itemnameTxt);
        TextView datetime = (TextView) listViewItem.findViewById(R.id.datetimeTxt);
        TextView dname = (TextView) listViewItem.findViewById(R.id.desc1);
        ImageView imgview = (ImageView) listViewItem.findViewById(R.id.imgView);
        ImageView imgview2 = (ImageView) listViewItem.findViewById(R.id.imgView2);
        ImageView photo = (ImageView) listViewItem.findViewById(R.id.imgViewPhoto);
        TextView location = (TextView) listViewItem.findViewById(R.id.locationTxt);

        Post post = posts.get(position);
        itemname.setText(post.getItemname());
       // datetime.setText(" " + post.getDatetime());
        dname.setText(" " + post.getDisplayname());

        if(post.getLocation().trim().length() > 30){
            location.setText(post.getLocation().substring(0, 29) + "...");
        }else {
            location.setText(post.getLocation());
        }
        if(post.getImgUri() != null) {
            if (!post.getImgUri().toString().trim().isEmpty()) {
                Picasso.with(context).load(post.getImgUri()).placeholder(R.mipmap.neighberlogo).into(photo);
            }
        }

        if(post.getStatus() == 1) {
            imgview2.setImageResource(R.mipmap.tick);
        }else{
            imgview2.setImageResource(R.mipmap.cross);
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
