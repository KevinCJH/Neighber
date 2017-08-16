package orbital.raspberry.neighber;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////NOT IN USE///////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////
public class RecordsOfferList2 extends ArrayAdapter<Send> {

    private Activity context;
    List<Send> offers;

    public RecordsOfferList2(Activity context, List<Send> offers) {
        super(context, R.layout.layout_offerrecords_list, offers);
        this.context = context;
        this.offers = offers;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_offerrecords_list, null, true);

        TextView itemname = (TextView) listViewItem.findViewById(R.id.itemnameTxt);
        TextView offernum = (TextView) listViewItem.findViewById(R.id.offernumTxt);
        TextView status = (TextView) listViewItem.findViewById(R.id.statusTxt);

        Send offer = offers.get(position);
        itemname.setText(offer.getItemname());

        switch(offer.getStatus()){
            case 1:
                status.setText("Status: Pending Reply");
                offernum.setText("From: " + offer.getTargetname());
                break;
            case 2:
                status.setText("Status: Write Agreement");
                offernum.setText("Offer accepted by: " + offer.getTargetname());
                break;
            case 3:
                status.setText("Status: Collection of Item");
                offernum.setText("Deliver To: " + offer.getTargetname());
                break;
            case 4:
                status.setText("Status: Lending in Progress");
                offernum.setText("Lending To: " + offer.getTargetname());
                break;
            case 5:
                status.setText("Status: Returning Item");
                offernum.setText("Collect From: " + offer.getTargetname());
                break;

        }


        return listViewItem;
    }

}
