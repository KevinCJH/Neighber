package orbital.raspberry.neighber;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class RecordsOfferList extends ArrayAdapter<OfferToLendPost> {

    private Activity context;
    List<OfferToLendPost> offers;

    public RecordsOfferList(Activity context, List<OfferToLendPost> offers) {
        super(context, R.layout.layout_records_list, offers);
        this.context = context;
        this.offers = offers;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_records_list, null, true);

        TextView itemname = (TextView) listViewItem.findViewById(R.id.itemnameTxt);
        TextView offernum = (TextView) listViewItem.findViewById(R.id.offernumTxt);
        TextView status = (TextView) listViewItem.findViewById(R.id.statusTxt);

        OfferToLendPost offer = offers.get(position);
        itemname.setText(offer.getItemname());

        switch(offer.getStatus()){
            case 1:
                status.setText("Status: Pending Reply");
                offernum.setText("From: " + offer.getTargetname());
                break;
            case 2:
                status.setText("Status: Collection of Item");
                offernum.setText("Collect From: " + offer.getTargetname());
                break;
            case 3:
                status.setText("Status: Borrowing in Progress");
                offernum.setText("Borrowing From: " + offer.getTargetname());
                break;
            case 4:
                status.setText("Status: Returning Item");
                offernum.setText("Return To: " + offer.getTargetname());
                break;

        }


        return listViewItem;
    }

}
