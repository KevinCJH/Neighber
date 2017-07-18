package orbital.raspberry.neighber;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class OfferList2 extends ArrayAdapter<OfferToLendPost> {

    private Activity context;
    List<OfferToLendPost> offers;

    public OfferList2(Activity context, List<OfferToLendPost> offers) {
        super(context, R.layout.layout_offer_list, offers);
        this.context = context;
        this.offers = offers;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_offer_list, null, true);

        TextView username = (TextView) listViewItem.findViewById(R.id.usernameTxt);
        TextView datetime = (TextView) listViewItem.findViewById(R.id.datetimeTxt);

        OfferToLendPost offer = offers.get(position);
        username.setText(offer.getOwnername());
        datetime.setText(offer.getDatetime());

        return listViewItem;
    }

}
