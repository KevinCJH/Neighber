package orbital.raspberry.neighber;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class RequestList extends ArrayAdapter<Post> {

    private Activity context;
    List<Post> requests;

    public RequestList(Activity context, List<Post> requests) {
        super(context, R.layout.layout_request_list, requests);
        this.context = context;
        this.requests = requests;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_request_list, null, true);

        TextView textViewName = (TextView) listViewItem.findViewById(R.id.itemnameTxt);
     //   TextView textViewDate = (TextView) listViewItem.findViewById(R.id.postdateTxt);
        TextView textViewPostby = (TextView) listViewItem.findViewById(R.id.postbyTxt);

        Post request = requests.get(position);
        textViewName.setText(request.getItemname());
        textViewPostby.setText(request.getDatetime());

        return listViewItem;
    }

}
