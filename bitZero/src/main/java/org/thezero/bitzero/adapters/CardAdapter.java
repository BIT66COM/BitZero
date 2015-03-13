package org.thezero.bitzero.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.thezero.bitzero.EditActivity;
import org.thezero.bitzero.MainActivity;
import org.thezero.bitzero.address.Address;
import org.thezero.bitzero.R;

import java.util.List;

/**
 * Created by thezero on 11/03/15.
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder>
{

    private List<Address> address;

    private Context mContext;

    public CardAdapter( Context context , List<Address> a)
    {
        this.mContext = context;
        this.address = a;
    }

    @Override
    public ViewHolder onCreateViewHolder( ViewGroup viewGroup, int i )
    {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_ex, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder( ViewHolder viewHolder, int i )
    {
        final Address p = address.get(i);

        if(p.getValuta()==Address.Val[1][0]) {
            viewHolder.mValuta.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_bitcoin));
        }else if(p.getValuta()==Address.Val[1][1]) {
            viewHolder.mValuta.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_litecoin));
        }else if(p.getValuta()==Address.Val[1][2]) {
            viewHolder.mValuta.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_dogecoin));
        }else if(p.getValuta()==Address.Val[1][3]) {
            viewHolder.mValuta.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_zetacoin));
        }

        viewHolder.mLabel.setText(p.getName());
        viewHolder.mAddr.setText(mContext.getString(R.string.addr)+" " + p.getAddress());

        if(p.getBalance()>-1){
            if(p.getValuta()==Address.Val[1][0]) {
                viewHolder.mBitcoin.setText(mContext.getString(R.string.balance)+" " + Address.toBTC(p.getBalance()) + " " + p.getValuta());
            }else if(p.getValuta()==Address.Val[1][1]) {
                viewHolder.mBitcoin.setText(mContext.getString(R.string.balance)+" " + p.getBalance() + " " + p.getValuta());
            }else if(p.getValuta()==Address.Val[1][2]) {
                viewHolder.mBitcoin.setText(mContext.getString(R.string.balance)+" " + p.getBalance() + " " + p.getValuta());
            }else if(p.getValuta()==Address.Val[1][3]) {
                viewHolder.mBitcoin.setText(mContext.getString(R.string.balance)+" " + p.getBalance() + " " + p.getValuta());
            }

            viewHolder.mTx.setText(mContext.getString(R.string.transaction)+" " + p.getTx().toString());
        }else{
            viewHolder.mBitcoin.setText(mContext.getString(R.string.balance)+" "+mContext.getString(R.string.no_connection));
            viewHolder.mTx.setText(mContext.getString(R.string.transaction)+" "+mContext.getString(R.string.no_connection));
        }

        viewHolder.mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action1:
                        Intent it = new Intent(mContext, EditActivity.class);
                        it.putExtra("label", p.getName());
                        it.putExtra("addr", p.getAddress());
                        mContext.startActivity(it);
                        break;
                    case R.id.action3:
                        MainActivity.encodeBarcode("TEXT_TYPE", p.getValuta(true) + ":" + p.getAddress() + "?label=" + p.getName());
                        break;
                    case R.id.action2:
                        AlertDialog.Builder altBx = new AlertDialog.Builder(mContext);
                        altBx.setMessage(mContext.getString(R.string.on_delete))
                                .setIcon(R.drawable.ic_launcher)
                                .setPositiveButton(mContext.getString(R.string.ok), new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                MainActivity.addr.remove(p.getAddress());
                                MainActivity.Refresh();
                            }
                        })
                                .setNegativeButton(mContext.getString(R.string.cancel), new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                            }

                        }).show();
                        break;
                }
                return true;
            }
        });
    }

    public void add(Address item, int position) {
        address.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(Address item) {
        int position = address.indexOf(item);
        address.remove(position);
        notifyItemRemoved(position);
    }

    public void removeAll() {
        address.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount()
    {
        return address == null ? 0 : address.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder
    {
        public TextView mLabel;
        public ImageView mValuta;
        public TextView mAddr;
        public TextView mBitcoin;
        public TextView mTx;
        public Toolbar mToolbar;

        public ViewHolder( View v )
        {
            super(v);
            mLabel = (TextView) v.findViewById(R.id.label);
            mValuta = (ImageView) v.findViewById(R.id.valuta);
            mAddr = (TextView) v.findViewById(R.id.address);
            mBitcoin = (TextView) v.findViewById(R.id.bitcoin);
            mTx = (TextView) v.findViewById(R.id.tx);
            mToolbar = (Toolbar) v.findViewById(R.id.toolbar2);

            mToolbar.inflateMenu(R.menu.popup);

        }
    }
}
